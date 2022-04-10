package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.BudgetProjectMapper;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.BudgetProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一般和重大项目预算-项目 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Service
public class BudgetProjectServiceImpl extends ServiceImpl<BudgetProjectMapper, BudgetProject> implements BudgetProjectService {
    @Autowired
    BudgetProtectService budgetProtectService;

    @Override
    public boolean add(BudgetProject budgetProject,String type) {
        budgetProject.setHaveDisplay("是");
        budgetProject.setVersion(1);
        budgetProject.setType(type);
        this.save(budgetProject);
        List<BudgetProtect> list = budgetProject.getList();
        if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(item -> item.setBudgetId(budgetProject.getId()));
            budgetProtectService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean edit(BudgetProject budgetProject) {
        this.updateById(budgetProject);
        //
        budgetProtectService.remove(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, budgetProject.getId()));
        List<BudgetProtect> list = budgetProject.getList();
        if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(item -> item.setBudgetId(budgetProject.getId()));
            budgetProtectService.saveBatch(list);
        }
        return true;
    }
}
