package com.haiying.project.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.TextSimilarity;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.result.ResponseResult;
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
 * 一般和重大项目立项备案号 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
@RestController
@RequestMapping("/projectCode")
public class ProjectCodeController {
    @Autowired
    ProjectCodeService projectCodeService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    PageBean pageBean;

    @Wrapper
    @PostMapping("list")
    public IPage<ProjectCode> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectCode> wrapper = new LambdaQueryWrapper<ProjectCode>().orderByDesc(ProjectCode::getId);
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectCode::getBusinessType, type);
        }

        Object projectName = paramMap.get("projectName");
        Object taskCode = paramMap.get("taskCode");
        Object status = paramMap.get("status");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(projectName)) {
            wrapper.like(ProjectCode::getProjectName, projectName);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(ProjectCode::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(status)) {
            wrapper.like(ProjectCode::getStatus, status);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(ProjectCode::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(ProjectCode::getDeptName, deptName);
        }

        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(ProjectCode::getDeptId, user.getDeptId());
        }
        return projectCodeService.page(new Page<>(current, pageSize), wrapper);
    }


    @Wrapper
    @PostMapping("list2")
    public IPage<ProjectCode> list2(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<ProjectCode> wrapper = new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getStatus, "未使用").eq(ProjectCode::getDeptId, user.getDeptId());
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object projectName = paramMap.get("projectName");
        if (ObjectUtil.isNotEmpty(projectName)) {
            wrapper.like(ProjectCode::getProjectName, projectName);
        }

        return projectCodeService.page(new Page<>(current, pageSize), wrapper);
    }


    @PostMapping("add")
    public ResponseResult add(@RequestBody ProjectCode page) {
        ResponseResult responseResult = ResponseResult.success(true);

        List<ProjectCode> resultList = new ArrayList<>();
        if (ObjectUtil.isEmpty(page.getLikeValue())) {
            //判断是否有相似度高的项目名称
            Integer year = Integer.parseInt(DateUtil.format(DateUtil.date(), "yyyy"));
            List<ProjectCode> list = projectCodeService.list(new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getYear, year));
            if (ObjectUtil.isNotEmpty(list)) {
                for (ProjectCode db : list) {
                    double d = TextSimilarity.similar(db.getProjectName(), page.getProjectName().trim());
                    if (d >= 0.8) {
                        db.setLikeValue((d * 100) + "%");
                        resultList.add(db);
                    }
                }
            }
        }
        if (ObjectUtil.isNotEmpty(resultList)) {
            responseResult.setData(pageBean.get(1, 100, resultList.size(), resultList));
        } else {
            page.setProjectName(page.getProjectName().replaceAll("\\s+", ""));
            projectCodeService.add(page);
        }

        return responseResult;
    }

    @Wrapper
    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectCode projectCode) {
        projectCode.setProjectName(projectCode.getProjectName().replaceAll("\\s+", ""));
        return projectCodeService.edit(projectCode);
    }

    @Wrapper
    @GetMapping("get")
    public ProjectCode get(String id) {
        ProjectCode projectCode = projectCodeService.getById(id);
        if (ObjectUtil.isNotEmpty(projectCode.getBusinessType())) {
            projectCode.setBusinessTypeTmp(Arrays.asList(projectCode.getBusinessType().split(",")));
        }
        return projectCode;
    }

    @Wrapper
    @GetMapping("getLabelValue")
    public List<LabelValue> getLabelValue() {
        List<LabelValue> list = new ArrayList<>();
        LambdaQueryWrapper<ProjectCode> wrapper = new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getStatus, "未使用");

        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(ProjectCode::getDeptId, user.getDeptId());
        }
        List<ProjectCode> codeList = projectCodeService.list(wrapper);
        if (ObjectUtil.isNotEmpty(codeList)) {
            list = codeList.stream().map(item -> new LabelValue(item.getTaskCode(), item.getTaskCode())).collect(Collectors.toList());
        }
        return list;
    }
}
