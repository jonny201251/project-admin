package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.ProviderScore1Mapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.ProviderScore1After;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProviderScore1Service;
import com.haiying.project.service.ProviderScore2Service;
import com.haiying.project.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 供方评分1 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-02-27
 */
@Service
public class ProviderScore1ServiceImpl extends ServiceImpl<ProviderScore1Mapper, ProviderScore1> implements ProviderScore1Service {
    @Autowired
    ProviderScore2Service providerScore2Service;
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProviderService providerService;

    private void add(ProviderScore1 formValue) {
        //判断是否重复添加
        List<ProviderScore1> ll = this.list(new LambdaQueryWrapper<ProviderScore1>().eq(ProviderScore1::getProviderName, formValue.getProviderName()).eq(ProviderScore1::getUsee, formValue.getUsee()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("供方用途和供方名称   已存在");
        }

        formValue.setHaveDisplay("是");
        formValue.setVersion(0);
        this.save(formValue);

        List<ProviderScore2> list = formValue.getProviderScore2List();
        list.forEach(item -> item.setProviderScore1Id(formValue.getId()));
        providerScore2Service.saveBatch(list);
    }

    private void edit(ProviderScore1 formValue) {
        this.updateById(formValue);
        providerScore2Service.remove(new LambdaQueryWrapper<ProviderScore2>().eq(ProviderScore2::getProviderScore1Id, formValue.getId()));
        List<ProviderScore2> list = formValue.getProviderScore2List();
        list.forEach(item -> {
            item.setId(null);
            item.setProviderScore1Id(formValue.getId());
        });
        providerScore2Service.saveBatch(list);
    }

    private void delete(ProviderScore1 formValue) {
        this.removeById(formValue.getId());
        providerScore2Service.remove(new LambdaQueryWrapper<ProviderScore2>().eq(ProviderScore2::getProviderScore1Id, formValue.getId()));
        Integer beforeId = formValue.getBeforeId();
        if (beforeId != null) {
            ProviderScore1 before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    private void change(ProviderScore1 current) {
        ProviderScore1 before = this.getById(current.getId());
        before.setHaveDisplay("否");
        this.updateById(before);
        //
        current.setId(null);
        current.setProcessInstId(null);
        current.setBeforeId(before.getId());
        current.setHaveDisplay("是");
        current.setVersion(current.getVersion() + 1);

        //
        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getLoginName().equals("孙欢")) {
            current.setResult("");
            current.setEndScore(0);
        }

        if (current.getBaseId() == null) {
            //第一次修改
            current.setBaseId(before.getId());
        } else {
            //第二、三、N次修改
            current.setBaseId(before.getBaseId());
        }
        this.save(current);

        providerScore2Service.remove(new LambdaQueryWrapper<ProviderScore2>().eq(ProviderScore2::getProviderScore1Id, current.getId()));
        List<ProviderScore2> list = current.getProviderScore2List();
        for (ProviderScore2 item : list) {
            item.setId(null);
            item.setProviderScore1Id(current.getId());
            if (!user.getLoginName().equals("孙欢")) {
                item.setEndScore(null);
            }
        }
        providerScore2Service.saveBatch(list);

    }

    @Override
    public boolean btnHandle(ProviderScore1After after) {
        ProviderScore1 formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        String comment = after.getComment();

        //
        if (buttonName.contains("退回申请人")) {
            formValue.setResult("");
            formValue.setEndScore(0);
            formValue.getProviderScore2List().forEach(item -> item.setEndScore(null));
        }
        if (buttonName.equals("申请人撤回")) {
            formValue.setResult("");
            formValue.setEndScore(0);
            this.updateById(formValue);
            List<ProviderScore2> list2 = providerScore2Service.list(new LambdaQueryWrapper<ProviderScore2>().eq(ProviderScore2::getProviderScore1Id, formValue.getId()));
            list2.forEach(item -> item.setEndScore(null));
            providerScore2Service.remove(new LambdaQueryWrapper<ProviderScore2>().eq(ProviderScore2::getProviderScore1Id, formValue.getId()));
            providerScore2Service.saveBatch(list2);
        }

        //
        if (!formValue.getType().equals("民用产业项目")) {
            path = path.replaceAll("Path1", "Path2");
        }

        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getProviderName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getProviderName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            boolean flag = buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
            if (flag) {
                Provider provider = providerService.getById(formValue.getProviderId());
                provider.setResult(formValue.getResult());
                providerService.updateById(provider);
            }
        } else if (type.equals("recall")) {
            buttonHandleBean.recall(formValue.getProcessInstId(), buttonName);
        } else if (type.equals("delete")) {
            delete(formValue);
            buttonHandleBean.delete(formValue.getProcessInstId());
        } else if (type.equals("change")) {
            ProcessInst before = processInstService.getById(formValue.getProcessInstId());
            before.setBusinessHaveDisplay("否");
            processInstService.updateById(before);
            change(formValue);
            Integer newProcessInstId = buttonHandleBean.change(before, path, formValue, buttonName, formValue.getId(), formValue.getProviderName(), comment);
            formValue.setProcessInstId(newProcessInstId);
            this.updateById(formValue);
        }
        return true;
    }


}
