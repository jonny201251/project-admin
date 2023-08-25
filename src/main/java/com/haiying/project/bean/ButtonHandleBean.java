package com.haiying.project.bean;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.service.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class ButtonHandleBean {
    @Autowired
    ProcessDesignService processDesignService;
    @Autowired
    WorkFlowBean workFlowBean;
    @Autowired
    ProcessDesignJumpService processDesignJumpService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    ProcessInstNodeService processInstNodeService;
    @Autowired
    ProcessDesignTaskService processDesignTaskService;

    public Integer addEdit(String path, Object formValue, String buttonName, Integer businessId, String businessName) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        ProcessInst processInst = processInstService.getOne(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessId, businessId));
        String actProcessInstanceId;
        if (processInst == null) {
            //启动流程
            actProcessInstanceId = workFlowBean.startPrcoess(path, businessId, "新增流程");
        } else {
            actProcessInstanceId = processInst.getActProcessInstanceId();
        }
        //完成任务,判断排他网关条件
        Task task = workFlowBean.getMyRunTask(actProcessInstanceId);
        ProcessDesign processDesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, "新增流程"));
        Map<String, Object> map = new HashMap<>();
        Set<String> javaVarName = workFlowBean.getExclusiveGatewayJavaVarName(processDesign.getId(), task.getTaskDefinitionKey());
        if (ObjectUtil.isNotEmpty(javaVarName)) {
            for (String name : javaVarName) {
                Object value = ReflectUtil.getFieldValue(formValue, name);
                if (value != null) {
                    map.put(name, value);
                }
            }
            //map.put("day", 100);
        }
        workFlowBean.completeTask(task, buttonName, map);
        //
        if (processInst == null) {
            processInst = new ProcessInst();
            processInst.setProcessDesignId(processDesign.getId());
            processInst.setProcessName(processDesign.getName());
            processInst.setBusinessName(businessName);
            processInst.setBusinessId(businessId);
            processInst.setBusinessHaveDisplay("是");
            processInst.setBusinessVersion(0);
            processInst.setActProcessInstanceId(actProcessInstanceId);
            processInst.setProcessStatus("审批中");
            Map<String, String> stepMap = workFlowBean.getPrcocessStep(processDesign.getId(), null, actProcessInstanceId);
            processInst.setDisplayProcessStep(stepMap.get("displayProcessStep"));
            processInst.setLoginProcessStep(stepMap.get("loginProcessStep"));
            processInst.setLoginName(user.getLoginName());
            processInst.setDisplayName(user.getDisplayName());
            processInst.setDeptId(user.getDeptId());
            processInst.setDeptName(user.getDeptName());
            processInst.setStartDatetime(LocalDateTime.now());
            processInst.setPath(processDesign.getPath());
            processInstService.save(processInst);
        } else {
            processInst.setProcessStatus("审批中");
            Map<String, String> stepMap = workFlowBean.getPrcocessStep(processDesign.getId(), null, actProcessInstanceId);
            processInst.setDisplayProcessStep(stepMap.get("displayProcessStep"));
            processInst.setLoginProcessStep(stepMap.get("loginProcessStep"));
            processInstService.updateById(processInst);
        }
        ProcessInstNode processInstNode = new ProcessInstNode();
        processInstNode.setProcessInstId(processInst.getId());
        processInstNode.setTaskKey(task.getTaskDefinitionKey());
        processInstNode.setTaskName(task.getName());
        processInstNode.setLoginName(user.getLoginName());
        processInstNode.setDisplayName(user.getDisplayName());
        processInstNode.setDeptId(user.getDeptId());
        processInstNode.setDeptName(user.getDeptName());
        processInstNode.setButtonName(buttonName.substring(buttonName.lastIndexOf("_") + 1));
        //
        HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(actProcessInstanceId, task.getTaskDefinitionKey());
        Date startDateTime = historicTaskInstance.getStartTime();
        Date endDateTime = historicTaskInstance.getEndTime();
        processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
        processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
        processInstNodeService.save(processInstNode);
        return processInst.getId();
    }

    public Integer change(ProcessInst old, String path, Object formValue, String buttonName, Integer businessId, String businessName, String comment) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        //启动流程
        String actProcessInstanceId = workFlowBean.startPrcoess(path, businessId, "变更流程");
        //完成任务,判断排他网关条件
        Task task = workFlowBean.getMyRunTask(actProcessInstanceId);
        ProcessDesign processDesign;
        processDesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, "变更流程"));
        if (processDesign == null) {
            processDesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, "新增流程"));
        }
        Map<String, Object> map = new HashMap<>();
        Set<String> javaVarName = workFlowBean.getExclusiveGatewayJavaVarName(processDesign.getId(), task.getTaskDefinitionKey());
        if (ObjectUtil.isNotEmpty(javaVarName)) {
            for (String name : javaVarName) {
                Object value = ReflectUtil.getFieldValue(formValue, name);
                if (value != null) {
                    map.put(name, value);
                }
            }
            //map.put("day", 100);
        }
        workFlowBean.completeTask(task, buttonName, map);
        //
        ProcessInst processInst = new ProcessInst();
        processInst.setProcessDesignId(processDesign.getId());
        if (path.equals("budgetProjecttPath")) {
            processInst.setProcessName(processDesign.getName());
        } else {
            processInst.setProcessName(processDesign.getName() + "-变更");
        }
        processInst.setBusinessName(businessName);
        processInst.setBusinessId(businessId);
        //
        processInst.setBusinessBeforeId(old.getBusinessId());
        if (old.getBusinessBaseId() == null) {
            //第一次修改
            processInst.setBusinessBaseId(old.getBusinessId());
        } else {
            //第二、三、N次修改
            processInst.setBusinessBaseId(old.getBusinessBaseId());
        }
        processInst.setBusinessHaveDisplay("是");
        processInst.setBusinessVersion(old.getBusinessVersion() + 1);
        processInst.setActProcessInstanceId(actProcessInstanceId);
        processInst.setProcessStatus("审批中");
        Map<String, String> stepMap = workFlowBean.getPrcocessStep(processDesign.getId(), null, actProcessInstanceId);
        processInst.setDisplayProcessStep(stepMap.get("displayProcessStep"));
        processInst.setLoginProcessStep(stepMap.get("loginProcessStep"));
        processInst.setLoginName(user.getLoginName());
        processInst.setDisplayName(user.getDisplayName());
        processInst.setDeptId(user.getDeptId());
        processInst.setDeptName(user.getDeptName());
        processInst.setStartDatetime(LocalDateTime.now());
        processInst.setPath(processDesign.getPath());
        processInstService.save(processInst);
        ProcessInstNode processInstNode = new ProcessInstNode();
        processInstNode.setProcessInstId(processInst.getId());
        processInstNode.setTaskKey(task.getTaskDefinitionKey());
        processInstNode.setTaskName(task.getName());
        processInstNode.setLoginName(user.getLoginName());
        processInstNode.setDisplayName(user.getDisplayName());
        processInstNode.setDeptId(user.getDeptId());
        processInstNode.setDeptName(user.getDeptName());
        processInstNode.setButtonName(buttonName.substring(buttonName.lastIndexOf("_") + 1));
        if (ObjectUtil.isNotEmpty(comment)) {
            processInstNode.setComment(comment);
        }

        //
        HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(actProcessInstanceId, task.getTaskDefinitionKey());
        Date startDateTime = historicTaskInstance.getStartTime();
        Date endDateTime = historicTaskInstance.getEndTime();
        processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
        processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
        processInstNodeService.save(processInstNode);
        return processInst.getId();
    }

    //一个节点多人并发处理,loginProcessStep人数大于1
    public void checkUpOne(Integer processInstId, Object formValue, String buttonName, String comment) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        String loginName = user.getLoginName();

        ProcessInst processInst = processInstService.getById(processInstId);
        String actProcessInstanceId = processInst.getActProcessInstanceId();
        Task task = workFlowBean.getMyRunTask(actProcessInstanceId);

        String loginProcessStep = processInst.getLoginProcessStep();

        List<String> nameList = new ArrayList<>();
        String[] tmp = loginProcessStep.split(",");
        for (String name : tmp) {
            if (!loginName.equals(name)) {
                nameList.add(name);
            }
        }
        processInst.setLoginProcessStep(String.join(",", nameList));
        processInst.setDisplayProcessStep(task.getName() + "[" + String.join(",", nameList) + "]");
        processInstService.updateById(processInst);

        ProcessInstNode processInstNode = new ProcessInstNode();
        processInstNode.setProcessInstId(processInst.getId());
        processInstNode.setTaskKey(task.getTaskDefinitionKey());
        processInstNode.setTaskName(task.getName());
        processInstNode.setLoginName(user.getLoginName());
        processInstNode.setDisplayName(user.getDisplayName());
        processInstNode.setDeptId(user.getDeptId());
        processInstNode.setDeptName(user.getDeptName());
        processInstNode.setButtonName(buttonName.substring(buttonName.lastIndexOf("_") + 1));
        if (ObjectUtil.isNotEmpty(comment)) {
            processInstNode.setComment(comment);
        }
        //
        HistoricTaskInstance historicTaskInstance = workFlowBean.getBeforeTaskInstance(actProcessInstanceId);
        Date startDateTime = historicTaskInstance.getEndTime();
        processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
        processInstNode.setEndDatetime(LocalDateTime.now());
        processInstNodeService.save(processInstNode);
    }

    //获取申请人
    public String getStartUserName(Integer processInstId) {
        List<ProcessInstNode> list = processInstNodeService.list(new LambdaQueryWrapper<ProcessInstNode>().eq(ProcessInstNode::getProcessInstId, processInstId));
        return list.get(0).getLoginName();
    }

    //一个节点一个人处理
    public boolean checkReject(Integer processInstId, Object formValue, String buttonName, String comment) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        ProcessInst processInst = processInstService.getById(processInstId);
        String actProcessInstanceId = processInst.getActProcessInstanceId();
        //完成任务,判断排他网关条件
        Task task = workFlowBean.getMyRunTask(actProcessInstanceId);
        Map<String, Object> map = new HashMap<>();
        Set<String> javaVarName = workFlowBean.getExclusiveGatewayJavaVarName(processInst.getProcessDesignId(), task.getTaskDefinitionKey());
        if (ObjectUtil.isNotEmpty(javaVarName)) {
            for (String name : javaVarName) {
                Object value = ReflectUtil.getFieldValue(formValue, name);
                if (value != null) {
                    map.put(name, value);
                }
            }
            //map.put("day", 100);
        }
        workFlowBean.completeTask(task, buttonName, map);
        //
        boolean flag = workFlowBean.finish(actProcessInstanceId);
        if (flag) {
            processInst.setEndDatetime(LocalDateTime.now());
            processInst.setProcessStatus("完成");
            processInst.setDisplayProcessStep("");
            processInst.setLoginProcessStep("");
        } else {
            String btnName = buttonName.substring(buttonName.lastIndexOf("_") + 1);
            if (btnName.equals("退回") || btnName.equals("退回申请人")) {
                processInst.setProcessStatus(btnName);
                String loginName = getStartUserName(processInst.getId());
                processInst.setDisplayProcessStep("申请部门[" + loginName + "]");
                processInst.setLoginProcessStep(loginName);
            } else {
                processInst.setProcessStatus("审批中");
                Map<String, String> stepMap = workFlowBean.getPrcocessStep(processInst.getProcessDesignId(), processInst.getId(), actProcessInstanceId);
                processInst.setDisplayProcessStep(stepMap.get("displayProcessStep"));
                processInst.setLoginProcessStep(stepMap.get("loginProcessStep"));
            }
        }
        processInstService.updateById(processInst);
        ProcessInstNode processInstNode = new ProcessInstNode();
        processInstNode.setProcessInstId(processInst.getId());
        processInstNode.setTaskKey(task.getTaskDefinitionKey());
        processInstNode.setTaskName(task.getName());
        processInstNode.setLoginName(user.getLoginName());
        processInstNode.setDisplayName(user.getDisplayName());
        processInstNode.setDeptId(user.getDeptId());
        processInstNode.setDeptName(user.getDeptName());
        processInstNode.setButtonName(buttonName.substring(buttonName.lastIndexOf("_") + 1));
        if (ObjectUtil.isNotEmpty(comment)) {
            processInstNode.setComment(comment);
        }
        //
        HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(actProcessInstanceId, task.getTaskDefinitionKey());
        Date startDateTime = historicTaskInstance.getStartTime();
        Date endDateTime = historicTaskInstance.getEndTime();
        processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
        processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
        processInstNodeService.save(processInstNode);
        if (flag) {
            workFlowBean.deleteProcessInstance(processInst.getActProcessInstanceId());
        }
        return flag;
    }

    //申请人撤回
    public void recall(Integer processInstId, String buttonName) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        ProcessInst processInst = processInstService.getById(processInstId);
        //跳转任务
        Task task = workFlowBean.getRunTaskList(processInst.getActProcessInstanceId()).get(0);
        ProcessDesignJump jumpTask = processDesignJumpService.list(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, processInst.getProcessDesignId()).eq(ProcessDesignJump::getDirection, "退回").eq(ProcessDesignJump::getButtonName, "退回申请人")).get(0);
        String firstTaskKey = jumpTask.getTargetTaskKey();
        workFlowBean.jump1(task, firstTaskKey);
        //
        String loginName = getStartUserName(processInst.getId());
        processInst.setDisplayProcessStep("申请部门[" + loginName + "]");
        processInst.setLoginProcessStep(loginName);
        processInst.setProcessStatus(buttonName);
        processInstService.updateById(processInst);
        ProcessInstNode processInstNode = new ProcessInstNode();
        processInstNode.setProcessInstId(processInst.getId());
        processInstNode.setLoginName(user.getLoginName());
        processInstNode.setDisplayName(user.getDisplayName());
        processInstNode.setDeptId(user.getDeptId());
        processInstNode.setDeptName(user.getDeptName());
        processInstNode.setButtonName(buttonName);
        //
        LocalDateTime localDateTime = LocalDateTime.now();
        processInstNode.setStartDatetime(localDateTime);
        processInstNode.setEndDatetime(localDateTime);
        processInstNodeService.save(processInstNode);
    }

    public void delete(Integer processInstId) {
        ProcessInst processInst = processInstService.getById(processInstId);
        if (processInst == null) {
            //草稿
        } else {
            //退回、退回申请人、申请人撤回
            Integer version = processInst.getBusinessVersion();
            if (ObjectUtil.isNotEmpty(version) && version > 0) {
                //回退到上一个版本
                ProcessInst before = processInstService.getOne(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, processInst.getPath()).eq(ProcessInst::getProcessDesignId, processInst.getProcessDesignId()).eq(ProcessInst::getBusinessId, processInst.getBusinessBeforeId()));
                before.setBusinessHaveDisplay("是");
                processInstService.updateById(before);
            }
            processInstService.removeById(processInst.getId());
            processInstNodeService.remove(new LambdaQueryWrapper<ProcessInstNode>().eq(ProcessInstNode::getProcessInstId, processInst.getId()));
            workFlowBean.deleteProcessInstance(processInst.getActProcessInstanceId());
        }
    }
}
