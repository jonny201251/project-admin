package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.CustomerCheck12Mapper;
import com.haiying.project.model.entity.CustomerCheck12;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.vo.CustomerCheck12After;
import com.haiying.project.service.CustomerCheck12Service;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户信用审批 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@Service
public class CustomerCheck12ServiceImpl extends ServiceImpl<CustomerCheck12Mapper, CustomerCheck12> implements CustomerCheck12Service {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;

    private void add(CustomerCheck12 formValue) {
        formValue.setHaveDisplay("是");
        formValue.setVersion(1);
        formValue.setDesc2(String.join(",", formValue.getDesc2Tmp()));
        this.save(formValue);
    }

    private void edit(CustomerCheck12 formValue) {
        formValue.setDesc2(String.join(",", formValue.getDesc2Tmp()));
        this.updateById(formValue);
    }

    private void change(CustomerCheck12 formValue) {
        CustomerCheck12 customerCheck12 = this.getById(formValue.getId());
        customerCheck12.setHaveDisplay("否");
        formValue.setDesc2(String.join(",", formValue.getDesc2Tmp()));
        this.updateById(customerCheck12);
        //
        formValue.setId(null);
        formValue.setProcessInstId(null);
        formValue.setBeforeId(customerCheck12.getId());
        formValue.setHaveDisplay("是");
        formValue.setVersion(formValue.getVersion() + 1);
        if (formValue.getBaseId() == null) {
            //第一次修改
            formValue.setBaseId(customerCheck12.getId());
        } else {
            //第二、三、N次修改
            formValue.setBaseId(customerCheck12.getBaseId());
        }
        formValue.setDesc2(String.join(",", formValue.getDesc2Tmp()));
        this.save(formValue);
    }

    private void delete(CustomerCheck12 formValue) {
        this.removeById(formValue.getId());
        Integer version = formValue.getVersion();
        if (ObjectUtil.isNotEmpty(version) && version > 1) {
            //回退到上一个版本
            Integer beforeId = formValue.getBeforeId();
            CustomerCheck12 before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    @Override
    public boolean btnHandle(CustomerCheck12After after) {
        CustomerCheck12 formValue = after.getFormValue();
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
