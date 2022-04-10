package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.service.BudgetProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/budgetProject")
@Wrapper
public class BudgetProjectController {
    @Autowired
    BudgetProjectService budgetProjectService;

    @PostMapping("list")
    public IPage<BudgetProject> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<BudgetProject> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(BudgetProject::getId, type);
        }
        return budgetProjectService.page(new Page<>(current, pageSize), wrapper);
    }
}
