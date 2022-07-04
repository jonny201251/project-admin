package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.bean.WorkFlowBean;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProcessInstNodeService;
import com.haiying.project.service.ProcessInstService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程实例 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@RestController
@RequestMapping("/processInst")
@Wrapper
public class ProcessInstController {
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    ProcessInstNodeService processInstNodeService;
    @Autowired
    WorkFlowBean workFlowBean;
    @Autowired
    HttpSession httpSession;

    //待办任务
    @PostMapping("myList")
    public IPage<ProcessInst> list() {
        LambdaQueryWrapper<ProcessInst> wrapper = new LambdaQueryWrapper<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
        wrapper.ne(ProcessInst::getProcessStatus, "完成")
                .eq(ProcessInst::getLoginProcessStep, user.getLoginName()).or()
                .likeLeft(ProcessInst::getLoginProcessStep, "," + user.getLoginName()).or()
                .likeRight(ProcessInst::getLoginProcessStep, user.getLoginName() + ",").or()
                .like(ProcessInst::getLoginProcessStep, "," + user.getLoginName() + ",");
        wrapper.orderByAsc(ProcessInst::getProcessName);
        return processInstService.page(new Page<>(1, 100), wrapper);
    }

    @GetMapping("getRunTaskKeyList")
    public List<String> getRunTaskList(String processInstId) {
        List<String> list = new ArrayList<>();
        ProcessInst processInst = processInstService.getById(processInstId);
        if (processInst != null) {
            List<Task> runTaskList = workFlowBean.getRunTaskList(processInst.getActProcessInstanceId());
            if (ObjectUtil.isNotEmpty(runTaskList)) {
                list = runTaskList.stream().map(Task::getTaskDefinitionKey).collect(Collectors.toList());
            }
        }
        return list;
    }
}
