package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.SmallProjectNo;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.SmallProjectNoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 一般项目非立项 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
@RestController
@RequestMapping("/smallProjectNo")
@Wrapper
public class SmallProjectNoController {
    @Autowired
    HttpSession httpSession;
    @Autowired
    SmallProjectNoService smallProjectNoService;

    @PostMapping("list")
    public IPage<SmallProjectNo> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<SmallProjectNo> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");

        Object name = paramMap.get("name");
        Object projectLevel = paramMap.get("projectLevel");
        Object taskCode = paramMap.get("taskCode");
        Object projectStatus = paramMap.get("projectStatus");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(SmallProjectNo::getName, name);
        }
        if (ObjectUtil.isNotEmpty(projectLevel)) {
            wrapper.like(SmallProjectNo::getProjectLevel, projectLevel);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(SmallProjectNo::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(projectStatus)) {
            wrapper.like(SmallProjectNo::getProjectStatus, projectStatus);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(SmallProjectNo::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(SmallProjectNo::getDeptName, deptName);
        }

        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!(user.getDeptName().equals("综合计划部") || user.getDeptName().equals("财务部") || user.getPosition().equals("公司领导")  || user.getPosition().equals("安全生产总监") || user.getPosition().equals("副总师级"))) {
            wrapper.eq(SmallProjectNo::getDeptId, user.getDeptId());
        }

        return smallProjectNoService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody SmallProjectNo smallProjectNo) {
        smallProjectNo.setName(smallProjectNo.getName().trim().replaceAll("\\s+", ""));
        return smallProjectNoService.add(smallProjectNo);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody SmallProjectNo smallProjectNo) {
        smallProjectNo.setName(smallProjectNo.getName().trim().replaceAll("\\s+", ""));
        return smallProjectNoService.updateById(smallProjectNo);
    }

    @GetMapping("get")
    public SmallProjectNo get(String id) {
        return smallProjectNoService.getById(id);
    }
}
