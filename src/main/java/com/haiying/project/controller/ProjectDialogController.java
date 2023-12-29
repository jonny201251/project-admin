package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.ProjectVO;
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

    private List<SmallProject> get1(Object name, Object taskCode) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
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
                return listt;
            }
        }
        return null;
    }

    private List<SmallProjectNo> get2(Object name, Object taskCode) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<SmallProjectNo> wrapper = new LambdaQueryWrapper<SmallProjectNo>().like(SmallProjectNo::getName, name);
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(SmallProjectNo::getDeptId, user.getDeptId());
        }
        return smallProjectNoService.list(wrapper);
    }

    private List<BigProject> get3(Object name, Object taskCode) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
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
                return listt;
            }
        }
        return null;
    }

    @PostMapping("list")
    public ResponseResult list(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        List<ProjectVO> dataList = new ArrayList<>();
        int count = 1;

        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        //一般项目
        List<SmallProject> list1 = get1(name, taskCode);
        if (ObjectUtil.isNotEmpty(list1)) {
            for (SmallProject tmp : list1) {
                ProjectVO p = new ProjectVO();
                p.setIdd(count++);
                p.setId(tmp.getId());
                p.setProjectType("一般项目");
                p.setName(tmp.getName());
                p.setTaskCode(tmp.getTaskCode());
                p.setProperty(tmp.getProperty());
                p.setCustomerId(tmp.getCustomerId());
                p.setCustomerName(tmp.getCustomerName());
                p.setProjectRate(tmp.getProjectRate());
                p.setVersion(tmp.getVersion());

                p.setExpectMoney(tmp.getExpectMoney());
                p.setLoginName(tmp.getLoginName());
                p.setDeptName(tmp.getDeptName());
                p.setCreateDatetime(tmp.getCreateDatetime());
                dataList.add(p);
            }
        }
        //一般项目非
        List<SmallProjectNo> list2 = get2(name, taskCode);
        if (ObjectUtil.isNotEmpty(list2)) {
            for (SmallProjectNo tmp : list2) {
                ProjectVO p = new ProjectVO();
                p.setIdd(count++);
                p.setId(tmp.getId());
                p.setProjectType("一般项目非");
                p.setName(tmp.getName());
                p.setTaskCode(tmp.getTaskCode());
                p.setProperty(tmp.getProperty());
                p.setProjectRate(tmp.getProjectRate());

                p.setLoginName(tmp.getLoginName());
                p.setDeptName(tmp.getDeptName());
                p.setCreateDatetime(tmp.getCreateDatetime());
                dataList.add(p);
            }
        }
        //重大项目
        List<BigProject> list3 = get3(name, taskCode);
        if (ObjectUtil.isNotEmpty(list3)) {
            for (BigProject tmp : list3) {
                ProjectVO p = new ProjectVO();
                p.setIdd(count++);
                p.setId(tmp.getId());
                p.setProjectType("重大项目");
                p.setName(tmp.getName());
                p.setTaskCode(tmp.getTaskCode());
                p.setProperty(tmp.getProperty());
                p.setCustomerId(tmp.getCustomerId());
                p.setCustomerName(tmp.getCustomerName());
                p.setProjectRate(tmp.getProjectRate());
                p.setVersion(tmp.getVersion());

                p.setExpectMoney(tmp.getExpectMoney());
                p.setLoginName(tmp.getLoginName());
                p.setDeptName(tmp.getDeptName());
                p.setCreateDatetime(tmp.getCreateDatetime());
                dataList.add(p);
            }
        }

        if (ObjectUtil.isNotEmpty(dataList)) {
            responseResult = pageBean.get(1, 100, dataList.size(), dataList);
        }

        return responseResult;
    }
}
