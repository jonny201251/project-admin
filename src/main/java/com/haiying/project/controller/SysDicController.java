package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.SysDic;
import com.haiying.project.service.SysDicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-12-27
 */
@RestController
@RequestMapping("/sysDic")
@Wrapper
public class SysDicController {
    @Autowired
    SysDicService sysDicService;

    @PostMapping("list")
    public IPage<SysDic> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<SysDic> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(SysDic::getType, type);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(SysDic::getName, name);
        }
        return sysDicService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody SysDic sysDic) {
        return sysDicService.save(sysDic);
    }

    @GetMapping("get")
    public SysDic get(String id) {
        return sysDicService.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody SysDic sysDic) {
        return sysDicService.updateById(sysDic);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return sysDicService.removeByIds(idList);
    }
}
