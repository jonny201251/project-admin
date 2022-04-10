package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.BudgetProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一般和重大项目预算-项目 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/bigBudgetProject")
@Wrapper
public class BigBudgetProjectController {
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    BudgetProtectService budgetProtectService;

    @PostMapping("list")
    public IPage<BudgetProject> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<BudgetProject> wrapper = new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getType, "重大项目");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(BudgetProject::getId, type);
        }
        return budgetProjectService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody BudgetProject budgetProject) {
        return budgetProjectService.add(budgetProject, "重大项目");
    }

    @GetMapping("get")
    public BudgetProject get(String id) {
        BudgetProject budgetProject = budgetProjectService.getById(id);
        List<BudgetProtect> list = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, id));
        budgetProject.setList(list);
        return budgetProject;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody BudgetProject budgetProject) {
        return budgetProjectService.edit(budgetProject);
    }
}
