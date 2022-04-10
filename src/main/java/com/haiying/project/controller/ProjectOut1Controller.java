package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectOut1;
import com.haiying.project.model.entity.ProjectOut2;
import com.haiying.project.service.ProjectOut1Service;
import com.haiying.project.service.ProjectOut2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目收支-支出明细 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/projectOut1")
@Wrapper
public class ProjectOut1Controller {
    @Autowired
    ProjectOut1Service projectOut1Service;
    @Autowired
    ProjectOut2Service projectOut2Service;

    @PostMapping("list")
    public IPage<ProjectOut1> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectOut1> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectOut1::getId, type);
        }
        return projectOut1Service.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProjectOut1 projectOut1) {
        return projectOut1Service.add(projectOut1);
    }

    @GetMapping("get")
    public ProjectOut1 get(String id) {
        ProjectOut1 projectOut1 = projectOut1Service.getById(id);
        List<ProjectOut2> list = projectOut2Service.list(new LambdaQueryWrapper<ProjectOut2>().eq(ProjectOut2::getProjectOut1Id, id));
        projectOut1.setList(list);
        return projectOut1;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectOut1 projectOut1) {
        return projectOut1Service.edit(projectOut1);
    }
}
