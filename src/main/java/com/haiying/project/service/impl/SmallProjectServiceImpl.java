package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.SmallProjectMapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.entity.SmallProtect;
import com.haiying.project.model.vo.SmallProjectAfter;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallProjectService;
import com.haiying.project.service.SmallProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一般项目立项 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
@Service
public class SmallProjectServiceImpl extends ServiceImpl<SmallProjectMapper, SmallProject> implements SmallProjectService {

    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    SmallProtectService smallProtectService;


    private void add(SmallProject formValue) {
        formValue.setHaveDisplay("是");
        formValue.setVersion(1);
        //
        formValue.setIdType(String.join(",", formValue.getIdTypeList()));
        this.save(formValue);
        List<SmallProtect> list = formValue.getList();
        list.forEach(item -> item.setSmallProjectId(formValue.getId()));
        smallProtectService.saveBatch(list);
    }

    private void edit(SmallProject formValue) {
        this.updateById(formValue);
        smallProtectService.remove(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getSmallProjectId, formValue.getId()));
        List<SmallProtect> list = formValue.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setSmallProjectId(formValue.getId());
        });
        smallProtectService.saveBatch(list);
    }

    private void change(SmallProject formValue) {
        SmallProject smallProject = this.getById(formValue.getId());
        smallProject.setHaveDisplay("否");
        this.updateById(smallProject);
        //
        formValue.setId(null);
        formValue.setProcessInstId(null);
        formValue.setBeforeId(smallProject.getId());
        formValue.setHaveDisplay("是");
        formValue.setVersion(formValue.getVersion() + 1);
        if (formValue.getBaseId() == null) {
            //第一次修改
            formValue.setBaseId(smallProject.getId());
        } else {
            //第二、三、N次修改
            formValue.setBaseId(smallProject.getBaseId());
        }
        this.save(formValue);

        List<SmallProtect> list = formValue.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setSmallProjectId(formValue.getId());
        });
        smallProtectService.saveBatch(list);
    }

    private void delete(SmallProject formValue) {
        this.removeById(formValue.getId());
        smallProtectService.remove(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getSmallProjectId, formValue.getId()));
        Integer version = formValue.getVersion();
        if (ObjectUtil.isNotEmpty(version) && version > 1) {
            //回退到上一个版本
            Integer beforeId = formValue.getBeforeId();
            SmallProject before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    @Override
    public boolean btnHandle(SmallProjectAfter after) {
        SmallProject formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getCustomerName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("change")) {
            //旧processInst
            ProcessInst old = processInstService.getById(formValue.getProcessInstId());
            old.setBusinessHaveDisplay("否");
            processInstService.updateById(old);
            change(formValue);
            Integer newProcessInstId = buttonHandleBean.change(old, path, formValue, buttonName, formValue.getId(), formValue.getCustomerName());
            formValue.setProcessInstId(newProcessInstId);
            this.updateById(formValue);
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, after.getComment());
        } else if (type.equals("recall")) {
            buttonHandleBean.recall(formValue.getProcessInstId(), buttonName);
        } else if (type.equals("delete")) {
            delete(formValue);
            buttonHandleBean.delete(formValue.getProcessInstId());
        }
        return true;
    }
}
