package com.haiying.project.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.TextSimilarity;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.PageData;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.common.utils.MyPageUtil;
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
public class ProjectCodeController {
    @Autowired
    ProjectCodeService projectCodeService;
    @Autowired
    HttpSession httpSession;

    @Wrapper
    @PostMapping("list")
    public IPage<ProjectCode> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectCode> wrapper = new LambdaQueryWrapper<ProjectCode>().orderByDesc(ProjectCode::getId);
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectCode::getBusinessType, type);
        }


/*        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getDisplayName().equals("代佳宝")) {
            wrapper.eq(ProjectCode::getLoginName, user.getLoginName());
        }*/

        return projectCodeService.page(new Page<>(current, pageSize), wrapper);
    }

    //是否有重复的项目名称
    @PostMapping("have")
    public PageData have(@RequestBody ProjectCode projectCode) {
        List<ProjectCode> resultList = new ArrayList<>();
        //判断是否有相似度高的项目名称
        Integer year = Integer.parseInt(DateUtil.format(DateUtil.date(), "yyyy"));
        List<ProjectCode> list = projectCodeService.list(new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getYear, year));

        return new PageData(1, 100, resultList.size(), 1, resultList);
    }

    @Wrapper
    @PostMapping("add")
    public ResponseResult add(@RequestBody ProjectCode page) {
        ResponseResult responseResult = ResponseResult.success();

        List<ProjectCode> resultList = new ArrayList<>();
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

        if (ObjectUtil.isNotEmpty(resultList)) {
            responseResult.setData(MyPageUtil.get(1, 100, resultList.size(), resultList));
        } else {
            projectCodeService.add(page);
        }

        return responseResult;
    }

    @Wrapper
    @GetMapping("get")
    public ProjectCode get(String id) {
        ProjectCode projectCode = projectCodeService.getById(id);
        projectCode.setBusinessTypeTmp(Arrays.asList(projectCode.getBusinessType().split(",")));
        return projectCode;
    }

    @Wrapper
    @GetMapping("getLabelValue")
    public List<LabelValue> getLabelValue() {
        List<LabelValue> list = new ArrayList<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
        List<ProjectCode> codeList = projectCodeService.list(new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getDeptId, user.getDeptId()).eq(ProjectCode::getStatus, "未使用"));
        if (ObjectUtil.isNotEmpty(codeList)) {
            list = codeList.stream().map(item -> new LabelValue(item.getTaskCode(), item.getTaskCode())).collect(Collectors.toList());
        }
        return list;
    }
}
