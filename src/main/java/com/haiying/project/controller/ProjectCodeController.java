package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectCode;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.LabelValue;
import com.haiying.project.service.ProjectCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般和重大项目立项任务号 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
@RestController
@RequestMapping("/projectCode")
@Wrapper
public class ProjectCodeController {
    @Autowired
    ProjectCodeService projectCodeService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<ProjectCode> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectCode> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectCode::getBusinessType, type);
        }
        return projectCodeService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProjectCode projectCode) {
        return projectCodeService.add(projectCode);
    }

    @GetMapping("get")
    public ProjectCode get(String id) {
        ProjectCode projectCode = projectCodeService.getById(id);
        projectCode.setBusinessTypeList(Arrays.asList(projectCode.getBusinessType().split(",")));
        return projectCode;
    }

    @GetMapping("getLabelValue")
    public List<LabelValue> getLabelValue() {
        List<LabelValue> list = new ArrayList<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
        List<ProjectCode> codeList = projectCodeService.list();
        if (ObjectUtil.isNotEmpty(codeList)) {
            list = codeList.stream().map(item -> new LabelValue(item.getTaskCode(), item.getTaskCode())).collect(Collectors.toList());
        }
        return list;
    }
}
