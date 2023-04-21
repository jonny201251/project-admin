package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.model.entity.*;
import com.haiying.project.service.BigProjectService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallProjectNoService;
import com.haiying.project.service.SmallProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//项目立项弹窗
@RestController
@RequestMapping("/projectDialog")
public class ProjectDialogController {
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    SmallProjectNoService smallProjectNoService;
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    PageBean pageBean;

    @PostMapping("list")
    public ResponseResult list(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        String projectType = (String) paramMap.get("projectType");
        if (ObjectUtil.isNotEmpty(projectType)) {
            if (projectType.equals("一般项目")) {
                LambdaQueryWrapper<SmallProject> wrapper = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHaveDisplay, "是").like(SmallProject::getName, name);
                if (!user.getDeptName().equals("综合计划部")) {
                    wrapper.eq(SmallProject::getDeptId, user.getDeptId());
                }
                if (ObjectUtil.isNotEmpty(taskCode)) {
                    wrapper.like(SmallProject::getTaskCode, taskCode);
                }
                List<SmallProject> list = smallProjectService.list(wrapper);
                if (ObjectUtil.isNotEmpty(list)) {
                    List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getBusinessHaveDisplay, "是").eq(ProcessInst::getProcessStatus, "完成").in(ProcessInst::getId, list.stream().map(SmallProject::getProcessInstId).collect(Collectors.toList())));
                    if (ObjectUtil.isNotEmpty(processInstList)) {
                        Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
                        List<SmallProject> listt = new ArrayList<>();
                        for (SmallProject tmp : list) {
                            if (processInstMap.get(tmp.getProcessInstId()) != null) {
                                listt.add(tmp);
                            }
                        }
                        if (ObjectUtil.isNotEmpty(listt)) {
                            responseResult = pageBean.get(current, pageSize, listt.size(), listt);
                        }
                    }
                }
            } else if (projectType.equals("一般项目非")) {
                LambdaQueryWrapper<SmallProjectNo> wrapper = new LambdaQueryWrapper<SmallProjectNo>().like(SmallProjectNo::getName, name);
                if (!user.getDeptName().equals("综合计划部")) {
                    wrapper.eq(SmallProjectNo::getDeptId, user.getDeptId());
                }
                IPage<SmallProjectNo> page = smallProjectNoService.page(new Page<>(current, pageSize), wrapper);
                if (ObjectUtil.isNotEmpty(page.getRecords())) {
                    responseResult = ResponseResult.success(page);
                }
            } else if (projectType.equals("重大项目")) {
                LambdaQueryWrapper<BigProject> wrapper = new LambdaQueryWrapper<BigProject>().eq(BigProject::getHaveDisplay, "是").like(BigProject::getName, name);
                if (!user.getDeptName().equals("综合计划部")) {
                    wrapper.eq(BigProject::getDeptId, user.getDeptId());
                }
                if (ObjectUtil.isNotEmpty(taskCode)) {
                    wrapper.like(BigProject::getTaskCode, taskCode);
                }
                List<BigProject> list = bigProjectService.list(wrapper);
                if (ObjectUtil.isNotEmpty(list)) {
                    List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getBusinessHaveDisplay, "是").eq(ProcessInst::getProcessStatus, "完成").in(ProcessInst::getId, list.stream().map(BigProject::getProcessInstId).collect(Collectors.toList())));
                    if (ObjectUtil.isNotEmpty(processInstList)) {
                        Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
                        List<BigProject> listt = new ArrayList<>();
                        for (BigProject tmp : list) {
                            if (processInstMap.get(tmp.getProcessInstId()) != null) {
                                listt.add(tmp);
                            }
                        }
                        if (ObjectUtil.isNotEmpty(listt)) {
                            responseResult = pageBean.get(current, pageSize, listt.size(), listt);
                        }
                    }
                }
            }
        }
        return responseResult;
    }


}
