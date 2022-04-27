package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectIn;
import com.haiying.project.service.ProjectInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 项目收支-收入明细 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@RestController
@RequestMapping("/projectIn")
@Wrapper
public class ProjectInController {
    @Autowired
    ProjectInService projectInService;

    @PostMapping("list")
    public IPage<ProjectIn> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectIn> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectIn::getId, type);
        }
        return projectInService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProjectIn projectIn){
        return projectInService.add(projectIn);
    }

    @GetMapping("get")
    public ProjectIn get(String id) {
        return projectInService.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectIn projectIn) {
        return projectInService.updateById(projectIn);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return projectInService.removeByIds(idList);
    }
}
