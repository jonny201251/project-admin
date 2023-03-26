package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.CustomerScore1Mapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.CustomerScore1After;
import com.haiying.project.service.CustomerScore1Service;
import com.haiying.project.service.CustomerScore2Service;
import com.haiying.project.service.CustomerService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
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
    @Autowired
    HttpSession httpSession;
    @Autowired
    CustomerService customerService;


    private void add(CustomerScore1 formValue) {
        formValue.setHaveDisplay("是");
        formValue.setVersion(0);
        formValue.setDesc2(String.join(",", formValue.getDesc2Tmp()));
        this.save(formValue);
        List<CustomerScore2> list = formValue.getList();
        list.forEach(item -> item.setCustomerScore1Id(formValue.getId()));
        customerScore2Service.saveBatch(list);
    }

    private void edit(CustomerScore1 formValue) {
        formValue.setDesc2(String.join(",", formValue.getDesc2Tmp()));
        this.updateById(formValue);
        customerScore2Service.remove(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, formValue.getId()));
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
        if (ObjectUtil.isNotEmpty(version) && version > 0) {
            //回退到上一个版本
            Integer beforeId = formValue.getBeforeId();
            CustomerScore1 before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    private void change(CustomerScore1 current) {
        CustomerScore1 before = this.getById(current.getId());
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
        if (!user.getLoginName().equals("宋思奇")) {
            current.setEndResult("");
            current.setEndScore(0);
        }

        if (current.getBaseId() == null) {
            //第一次修改
            current.setBaseId(before.getId());
        } else {
            //第二、三、N次修改
            current.setBaseId(before.getBaseId());
        }
        current.setDesc2(String.join(",", current.getDesc2Tmp()));
        this.save(current);

        customerScore2Service.remove(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, current.getId()));
        List<CustomerScore2> list = current.getList();
        for (CustomerScore2 item : list) {
            item.setId(null);
            item.setCustomerScore1Id(current.getId());
            if (!user.getLoginName().equals("宋思奇")) {
                item.setEndScore(null);
            }
        }
        customerScore2Service.saveBatch(list);

    }


    @Override
    public boolean btnHandle(CustomerScore1After after) {
        CustomerScore1 formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        String comment = after.getComment();

        //
        if (buttonName.contains("退回申请人")) {
            formValue.setEndResult("");
            formValue.setEndScore(0);
            formValue.getList().forEach(item -> item.setEndScore(null));
        }
        if (buttonName.equals("申请人撤回")) {
            formValue.setEndResult("");
            formValue.setEndScore(0);
            this.updateById(formValue);
            List<CustomerScore2> list2 = customerScore2Service.list(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, formValue.getId()));
            list2.forEach(item -> item.setEndScore(null));
            customerScore2Service.remove(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, formValue.getId()));
            customerScore2Service.saveBatch(list2);
        }


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
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            boolean flag=buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
            if (flag) {
                Customer customer = customerService.getById(formValue.getCustomerId());
                customer.setResult(formValue.getResult());
                customerService.updateById(customer);
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
            Integer newProcessInstId = buttonHandleBean.change(before, path, formValue, buttonName, formValue.getId(), formValue.getCustomerName(), comment);
            formValue.setProcessInstId(newProcessInstId);
            this.updateById(formValue);
        }
        return true;
    }

}
