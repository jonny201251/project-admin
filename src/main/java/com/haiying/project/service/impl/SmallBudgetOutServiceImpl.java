package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.utils.SpringUtil;
import com.haiying.project.mapper.SmallBudgetOutMapper;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.model.vo.SmallBudgetOutVO;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 一般项目预算-预计支出 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-02
 */
@Service
public class SmallBudgetOutServiceImpl extends ServiceImpl<SmallBudgetOutMapper, SmallBudgetOut> implements SmallBudgetOutService {
    @Override
    public boolean add(SmallBudgetOutVO vo) {
        //判断是否重复添加
        LambdaQueryWrapper<SmallBudgetOut> wrapper = new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, vo.getBudgetId()).eq(SmallBudgetOut::getCostType, vo.getCostType());
        if (ObjectUtil.isNotEmpty(vo.getCostRate())) {
            wrapper.eq(SmallBudgetOut::getCostRate, vo.getCostRate());
        }
        List<SmallBudgetOut> ll = this.list(wrapper);
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号、成本类型和税率   已存在");
        }

        double count = 1, totalCost = 0.0;
        List<SmallBudgetOut> list = vo.getList();

        for (SmallBudgetOut budgetOut : list) {
            totalCost += ofNullable(budgetOut.getMoney()).orElse(0.0);
            if (ObjectUtil.isEmpty(vo.getSort())) {
                budgetOut.setSort(count++);
            } else {
                budgetOut.setSort(vo.getSort());
            }
            budgetOut.setHaveDisplay(vo.getHaveDisplay());
            budgetOut.setVersion(vo.getVersion());
            budgetOut.setBudgetId(vo.getBudgetId());
            budgetOut.setProjectId(vo.getProjectId());
            budgetOut.setProjectType(vo.getProjectType());
            budgetOut.setName(vo.getName());
            budgetOut.setTaskCode(vo.getTaskCode());
            budgetOut.setCostType(vo.getCostType());
            budgetOut.setCostRate(vo.getCostRate());
            budgetOut.setRemark(vo.getRemark());
            budgetOut.setLoginName(vo.getLoginName());
            budgetOut.setDisplayName(vo.getDisplayName());
            budgetOut.setDeptId(vo.getDeptId());
            budgetOut.setDeptName(vo.getDeptName());
            budgetOut.setCreateDatetime(vo.getCreateDatetime());
        }
        this.saveBatch(list);
        //成本总预算
        BudgetProjectService budgetProjectService= SpringUtil.getBean(BudgetProjectService.class);
        BudgetProject budgetProject = budgetProjectService.getById(vo.getBudgetId());
        budgetProject.setTotalCost(totalCost);
        budgetProjectService.updateById(budgetProject);
        return true;
    }

    @Override
    public boolean edit(SmallBudgetOutVO vo) {
        LambdaQueryWrapper<SmallBudgetOut> wrapper = new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, vo.getBudgetId()).eq(SmallBudgetOut::getCostType, vo.getCostType());
        if (ObjectUtil.isNotEmpty(vo.getCostRate())) {
            wrapper.eq(SmallBudgetOut::getCostRate, vo.getCostRate());
        }
        this.remove(wrapper);

        double count = 1, totalCost = 0.0;
        List<SmallBudgetOut> list = vo.getList();

        for (SmallBudgetOut budgetOut : list) {
            totalCost += ofNullable(budgetOut.getMoney()).orElse(0.0);
            budgetOut.setId(null);
            if (ObjectUtil.isEmpty(vo.getSort())) {
                budgetOut.setSort(count++);
            } else {
                budgetOut.setSort(vo.getSort());
            }
            budgetOut.setHaveDisplay(vo.getHaveDisplay());
            budgetOut.setVersion(vo.getVersion());
            budgetOut.setBudgetId(vo.getBudgetId());
            budgetOut.setProjectId(vo.getProjectId());
            budgetOut.setProjectType(vo.getProjectType());
            budgetOut.setName(vo.getName());
            budgetOut.setTaskCode(vo.getTaskCode());
            budgetOut.setCostType(vo.getCostType());
            budgetOut.setCostRate(vo.getCostRate());
            budgetOut.setRemark(vo.getRemark());
            budgetOut.setLoginName(vo.getLoginName());
            budgetOut.setDisplayName(vo.getDisplayName());
            budgetOut.setDeptId(vo.getDeptId());
            budgetOut.setDeptName(vo.getDeptName());
            budgetOut.setCreateDatetime(vo.getCreateDatetime());
        }
        this.saveBatch(list);
        //成本总预算
        BudgetProjectService budgetProjectService= SpringUtil.getBean(BudgetProjectService.class);
        BudgetProject budgetProject = budgetProjectService.getById(vo.getBudgetId());
        budgetProject.setTotalCost(totalCost);
        budgetProjectService.updateById(budgetProject);
        return true;
    }

}
