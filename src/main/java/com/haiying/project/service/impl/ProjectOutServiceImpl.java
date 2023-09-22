package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.ProjectOutMapper;
import com.haiying.project.model.entity.BudgetOut;
import com.haiying.project.model.entity.OutContract;
import com.haiying.project.model.entity.ProjectInOutCount;
import com.haiying.project.model.entity.ProjectOut;
import com.haiying.project.model.vo.ProjectOutAfter;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 项目收支-支出明细 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@Service
public class ProjectOutServiceImpl extends ServiceImpl<ProjectOutMapper, ProjectOut> implements ProjectOutService {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    ProjectInOutCountService projectInOutCountService;
    @Autowired
    BudgetOutService budgetOutService;
    @Autowired
    OutContractService outContractService;


    private void validate(ProjectOut projectOut) {
        //有合同的，跟 合同签署情况->付款合同
        //无合同的，跟 预算中的费用比较
        if (projectOut.getHaveContract().equals("无")) {
            List<BudgetOut> ll = budgetOutService.list(new LambdaQueryWrapper<BudgetOut>().eq(BudgetOut::getBudgetId, projectOut.getBudgetId()).eq(BudgetOut::getOutType, projectOut.getCostType()));
            double totalCost = 0.0;
            for (BudgetOut budgetOut : ll) {
                totalCost += ofNullable(budgetOut.getMoney()).orElse(0.0);
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney1())) {
                if (projectOut.getMoney1() > totalCost) {
                    throw new PageTipException("开票金额:" + projectOut.getMoney1() + " ,超出预算额:" + totalCost);
                }
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney2())) {
                if (projectOut.getMoney2() > totalCost) {
                    throw new PageTipException("付款金额:" + projectOut.getMoney2() + " ,超出预算额:" + totalCost);
                }
            }
        } else {
            List<OutContract> ll = outContractService.list(new LambdaQueryWrapper<OutContract>().eq(OutContract::getTaskCode, projectOut.getTaskCode()).eq(OutContract::getCostType, projectOut.getCostType()).eq(OutContract::getContractCode, projectOut.getContractCode()));
            double totalCost = 0.0;
            for (OutContract outContract : ll) {
                totalCost += ofNullable(outContract.getContractMoney()).orElse(0.0);
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney1())) {
                if (projectOut.getMoney1() > totalCost) {
                    throw new PageTipException("开票金额:" + projectOut.getMoney1() + " ,超出预算额:" + totalCost);
                }
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney2())) {
                if (projectOut.getMoney2() > totalCost) {
                    throw new PageTipException("付款金额:" + projectOut.getMoney2() + " ,超出预算额:" + totalCost);
                }
            }
        }
    }

    public boolean add(ProjectOut formValue) {
        validate(formValue);

        ProjectInOutCount count = projectInOutCountService.getById(1);
        formValue.setSort(Double.valueOf(count.getCount()));
        count.setCount(count.getCount() + 1);

        formValue.setUserNamee(String.join(",", formValue.getUserNameeList()));
        this.save(formValue);
        projectInOutCountService.updateById(count);
        return true;
    }

    public void edit(ProjectOut formValue) {
        validate(formValue);

        formValue.setUserNamee(String.join(",", formValue.getUserNameeList()));
        if (formValue.getHaveContract().equals("无")) {
            formValue.setProviderId(null);
            formValue.setProviderName(null);
            formValue.setContractCode(null);
            formValue.setContractMoney(null);
            formValue.setContractName(null);
            formValue.setEndMoney(null);
            formValue.setCostRate(null);
            formValue.setOutStyle(null);
            formValue.setArriveDate(null);
        }
        this.updateById(formValue);
    }

    private void delete(ProjectOut formValue) {
        this.removeById(formValue.getId());
    }


    @Override
    public boolean btnHandle(ProjectOutAfter after) {
        ProjectOut formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        String comment = after.getComment();
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
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
        } else if (type.equals("recall")) {
            buttonHandleBean.recall(formValue.getProcessInstId(), buttonName);
        } else if (type.equals("delete")) {
            delete(formValue);
            buttonHandleBean.delete(formValue.getProcessInstId());
        }
        return true;
    }
}
