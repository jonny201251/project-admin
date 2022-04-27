package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectIo;
import com.haiying.project.service.ProjectIoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 项目收支-往来款inout 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@RestController
@RequestMapping("/projectIo")
@Wrapper
public class ProjectIoController {
    @Autowired
    ProjectIoService projectIoService;

    @PostMapping("list")
    public IPage<ProjectIo> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectIo> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectIo::getId, type);
        }
        return projectIoService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProjectIo projectIo){
        return projectIoService.add(projectIo);
    }

    @GetMapping("get")
    public ProjectIo get(String id) {
        return projectIoService.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectIo projectIo) {
        return projectIoService.updateById(projectIo);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return projectIoService.removeByIds(idList);
    }
}
