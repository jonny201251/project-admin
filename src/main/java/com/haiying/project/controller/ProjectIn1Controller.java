package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.model.entity.ProjectIn1;
import com.haiying.project.model.entity.ProjectIn2;
import com.haiying.project.service.ProjectIn1Service;
import com.haiying.project.service.ProjectIn2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目收支-收入明细 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/projectIn1")
public class ProjectIn1Controller {
    @Autowired
    ProjectIn1Service projectIn1Service;
    @Autowired
    ProjectIn2Service projectIn2Service;

    @PostMapping("list")
    public IPage<ProjectIn1> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectIn1> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectIn1::getId, type);
        }
        return projectIn1Service.page(new Page<>(current, pageSize), wrapper);
    }

    @GetMapping("list2")
    public Map<String, List<ProjectIn1>> list2() {
        Map<String, List<ProjectIn1>> map = new HashMap<>();
        ProjectIn1 projectIn1 = projectIn1Service.getById(4);
        List<ProjectIn1> list=new ArrayList<>();
        list.add(projectIn1);
        map.put("data", list);
        return map;
    }


    @PostMapping("add")
    public boolean add(@RequestBody ProjectIn1 projectIn1) {
        return projectIn1Service.add(projectIn1);
    }

    @GetMapping("get")
    public ProjectIn1 get(String id) {
        ProjectIn1 projectIn1 = projectIn1Service.getById(id);
        List<ProjectIn2> list = projectIn2Service.list(new LambdaQueryWrapper<ProjectIn2>().eq(ProjectIn2::getProjectIn1Id, id));
        projectIn1.setList(list);
        return projectIn1;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectIn1 projectIn1) {
        return projectIn1Service.edit(projectIn1);
    }
}
