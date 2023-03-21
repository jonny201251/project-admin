package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectProtect1;
import com.haiying.project.model.entity.ProjectProtect2;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProjectProtect1Service;
import com.haiying.project.service.ProjectProtect2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一般和重大项目的保证金登记表1 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
@RestController
@RequestMapping("/projectProtect1")
@Wrapper
public class ProjectProtect1Controller {
    @Autowired
    ProjectProtect1Service projectProtect1Service;
    @Autowired
    ProjectProtect2Service projectProtect2Service;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<ProjectProtect1> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProjectProtect1> page;
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<ProjectProtect1> wrapper = new LambdaQueryWrapper<ProjectProtect1>().eq(ProjectProtect1::getDeptId, user.getDeptId());
        page = projectProtect1Service.page(new Page<>(current, pageSize), wrapper);
        return page;
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProjectProtect1 projectProtect1) {
        return projectProtect1Service.add(projectProtect1);
    }

    @GetMapping("get")
    public ProjectProtect1 get(String id) {
        ProjectProtect1 projectProtect1 = projectProtect1Service.getById(id);
        List<ProjectProtect2> list = projectProtect2Service.list(new LambdaQueryWrapper<ProjectProtect2>().eq(ProjectProtect2::getProtect1Id, id));
        if (ObjectUtil.isNotEmpty(list)) {
            projectProtect1.setList(list);
        }
        return projectProtect1;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectProtect1 projectProtect1) {
        return projectProtect1Service.edit(projectProtect1);
    }
}
