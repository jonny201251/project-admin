package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.CustomerScore1Mapper;
import com.haiying.project.model.entity.CustomerScore1;
import com.haiying.project.model.entity.CustomerScore2;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.vo.CustomerScore1After;
import com.haiying.project.service.CustomerScore1Service;
import com.haiying.project.service.CustomerScore2Service;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 客户信用评分1 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@Service
public class CustomerScore1ServiceImpl extends ServiceImpl<CustomerScore1Mapper, CustomerScore1> implements CustomerScore1Service {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    CustomerScore2Service customerScore2Service;


    private void add(CustomerScore1 formValue) {
        formValue.setHaveDisplay("是");
        formValue.setVersion(1);
        this.save(formValue);
        List<CustomerScore2> list = formValue.getList();
        list.forEach(item -> item.setCustomerScore1Id(formValue.getId()));
        customerScore2Service.saveBatch(list);
    }

    private void edit(CustomerScore1 formValue) {
        this.updateById(formValue);
        customerScore2Service.remove(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, formValue.getId()));
        List<CustomerScore2> list = formValue.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setCustomerScore1Id(formValue.getId());
        });
        customerScore2Service.saveBatch(list);
    }

    private void change(CustomerScore1 formValue) {
        CustomerScore1 customerScore1 = this.getById(formValue.getId());
        customerScore1.setHaveDisplay("否");
        this.updateById(customerScore1);
        //
        formValue.setId(null);
        formValue.setProcessInstId(null);
        formValue.setBeforeId(customerScore1.getId());
        formValue.setHaveDisplay("是");
        formValue.setVersion(formValue.getVersion() + 1);
        if (formValue.getBaseId() == null) {
            //第一次修改
            formValue.setBaseId(customerScore1.getId());
        } else {
            //第二、三、N次修改
            formValue.setBaseId(customerScore1.getBaseId());
        }
        this.save(formValue);

        List<CustomerScore2> list = formValue.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setCustomerScore1Id(formValue.getId());
        });
        customerScore2Service.saveBatch(list);
    }

    private void delete(CustomerScore1 formValue) {
        this.removeById(formValue.getId());
        customerScore2Service.remove(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, formValue.getId()));
        Integer version = formValue.getVersion();
        if (ObjectUtil.isNotEmpty(version) && version > 1) {
            //回退到上一个版本
            Integer beforeId = formValue.getBeforeId();
            CustomerScore1 before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    @Override
    public boolean btnHandle(CustomerScore1After after) {
        CustomerScore1 formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getCustomerName());
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
