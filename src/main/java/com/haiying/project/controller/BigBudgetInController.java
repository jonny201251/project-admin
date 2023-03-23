package com.haiying.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.service.BudgetInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 一般和重大项目预算-预计收入 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/bigBudgetIn")
@Wrapper
public class BigBudgetInController {
    @Autowired
    BudgetInService budgetInService;

    @PostMapping("list")
    public IPage<BudgetIn> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        QueryWrapper<BudgetIn> wrapper = new QueryWrapper<BudgetIn>().eq("type", "重大项目").select("distinct budget_id,project_id,name,task_code,type,in_type").orderByDesc("name,in_type");
        return budgetInService.page(new Page<>(current, pageSize), wrapper);
    }


}
