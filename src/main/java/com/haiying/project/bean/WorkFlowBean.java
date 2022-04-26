package com.haiying.project.bean;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.bean.command.DeleteTaskCmd;
import com.haiying.project.bean.command.Jump2TargetFlowNodeCommand;
import com.haiying.project.bean.command.SetFLowNodeAndGoCmd;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.utils.SpringUtil;
import com.haiying.project.model.entity.ProcessDesign;
import com.haiying.project.model.entity.ProcessDesignEdge;
import com.haiying.project.model.entity.ProcessInstNode;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProcessDesignEdgeService;
import com.haiying.project.service.ProcessDesignService;
import com.haiying.project.service.ProcessInstNodeService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WorkFlowBean {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    TaskService taskService;
    @Autowired
    HistoryService historyService;
    @Autowired
    IdentityService identityService;
    @Autowired
    ManagementService managementService;
    @Autowired
    HttpSession httpSession;

    //启动流程
    public String startPrcoess(String path, Integer businessId, String processType) {
        ProcessDesignService processDesignService = SpringUtil.getBean(ProcessDesignService.class);
        ProcessDesign processdesign;
        processdesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, processType));
        if (processdesign == null) {
            processdesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, "新增流程"));
        }
        String actProcessName = "id" + processdesign.getId();
        if (ObjectUtil.isEmpty(processdesign.getDeployId())) {
            //先部署
            BpmnToActivitiBean bpmnToActivitiBean = SpringUtil.getBean(BpmnToActivitiBean.class);
            String activitiXml = bpmnToActivitiBean.convert(processdesign);
            Deployment deployment = this.deploy(actProcessName, activitiXml);
            //
            processdesign.setDeployId(deployment.getId());
            processDesignService.updateById(processdesign);
        }
        ProcessInstance processInstance = this.startProcessInstance(actProcessName, businessId);
        return processInstance.getId();
    }

    public Deployment deploy(String actProcessName, String activitiXml) {
        return repositoryService.createDeployment().name(actProcessName).addString(actProcessName + ".bpmn", activitiXml).deploy();
    }

    //级联删除流程部署
    public void deleteDeploy(String deployId) {
        repositoryService.deleteDeployment(deployId, true);
    }

    //删除流程实例
    public void deleteProcessInstance(String actProcessInstanceId) {
        //删除顺序不能换
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(actProcessInstanceId).singleResult();
        if (pi != null) {
            runtimeService.deleteProcessInstance(actProcessInstanceId, "删除原因");
        }
        historyService.deleteHistoricProcessInstance(actProcessInstanceId);
    }

    public ProcessInstance startProcessInstance(String actProcessName, Integer businessId) {
        //必须是activitiXml中的process标签的id
        return runtimeService.startProcessInstanceByKey(actProcessName, businessId + "");
    }

    public List<Task> getRunTaskList(String actProcessInstanceId) {
        return taskService.createTaskQuery().processInstanceId(actProcessInstanceId).active().list();
    }

    public Task getMyRunTask(String actProcessInstanceId) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        List<Task> list = taskService.createTaskQuery().processInstanceId(actProcessInstanceId).taskCandidateOrAssigned(user.getLoginName()).active().list();
        if (ObjectUtil.isEmpty(list)) {
            throw new PageTipException("用户没有任务");
        } else {
            if (list.size() > 1) {
                throw new PageTipException("用户任务有" + list.size() + "个");
            }
        }
        return list.get(0);
    }

    //获取节点的下一个节点(排他网关)的连线条件
    public Set<String> getExclusiveGatewayJavaVarName(Integer processDesignId, String taskKey) {
        ProcessDesignEdgeService processDesignEdgeService = SpringUtil.getBean(ProcessDesignEdgeService.class);
        Set<String> set = new HashSet<>();
        System.out.println();
        //排他网关的连线的id
        List<ProcessDesignEdge> edgeList = processDesignEdgeService.list(new LambdaQueryWrapper<ProcessDesignEdge>().eq(ProcessDesignEdge::getProcessDesignId, processDesignId).eq(ProcessDesignEdge::getSourceTaskKey, taskKey).likeRight(ProcessDesignEdge::getTargetTaskKey, "ExclusiveGateway"));
        if (ObjectUtil.isNotEmpty(edgeList)) {
            if (edgeList.size() == 1) {
                //排他网关的连线的edge
                List<ProcessDesignEdge> edgeList2 = processDesignEdgeService.list(new LambdaQueryWrapper<ProcessDesignEdge>().eq(ProcessDesignEdge::getProcessDesignId, processDesignId).eq(ProcessDesignEdge::getSourceTaskKey, edgeList.get(0).getTargetTaskKey()));
                for (ProcessDesignEdge processDesignEdge : edgeList2) {
                    set.addAll(Arrays.stream(processDesignEdge.getJavaVarName().split(",")).collect(Collectors.toSet()));
                }
            } else {
                throw new PageTipException("getExclusiveGatewayJavaVarName-发生错误错误");
            }
        }
        return set;
    }

    /*
      按钮类型：
        一条连线：oneEdge_提交/oneEdge_按钮名称
        多条连线：moreEdge_按钮名称1，moreEdge_按钮名称2

        连线：edge_按钮名称
        自由跳转：jump_targetId_按钮名称
     功能说明：新增、编辑、审批、变更流程的表单的按钮，完成任务，最后判断任务的下一个节点有没有排他网关
     map:初始化网关条件
     */
    public void completeTask(Task actTask, String buttonName, Map<String, Object> map) {
        SysUser currentUser = (SysUser) httpSession.getAttribute("user");
        String taskId = actTask.getId();
        String taskKey = actTask.getTaskDefinitionKey();
        //拾取任务
        taskService.claim(taskId, currentUser.getLoginName());
        //
        if (buttonName.contains("edge")) {
            //连线条件
            map.put(taskKey, buttonName.replaceAll("edge_", ""));
            //完成任务，设置 网关条件和连线条件
            taskService.complete(taskId, map);
        } else {
            //跳转
            String[] arr = buttonName.split("_");
            this.jump1(actTask, arr[1] + "_" + arr[2]);
        }
    }

    //获取流程步骤
    public Map<String, String> getPrcocessStep(Integer processDesignId, Integer processInstId, String actProcessInstanceId) {
        ProcessInstNodeService processInstNodeService = SpringUtil.getBean(ProcessInstNodeService.class);
        UserTaskBean userTaskBean = SpringUtil.getBean(UserTaskBean.class);
        Map<String, String> resultMap = new HashMap<>();
        if (!finish(actProcessInstanceId)) {
            List<String> displayList = new ArrayList<>();
            List<String> loginList = new ArrayList<>();
            //历史处理节点
            List<ProcessInstNode> nodeList = processInstNodeService.list(new LambdaQueryWrapper<ProcessInstNode>().eq(ProcessInstNode::getProcessInstId, processInstId));
            Map<String, ProcessInstNode> nodeMap = nodeList.stream().collect(Collectors.toMap(ProcessInstNode::getTaskKey, v -> v, (key1, key2) -> key2));
            //获取当前活动任务
            List<Task> runTaskList = getRunTaskList(actProcessInstanceId);
            for (Task task : runTaskList) {
                ProcessInstNode processInstNode = nodeMap.get(task.getTaskDefinitionKey());
                if (processInstNode != null) {
                    //存在历史节点，使用历史处理人
                    displayList.add(processInstNode.getTaskName() + "[" + processInstNode.getDisplayName() + "]");
                    loginList.add(processInstNode.getLoginName());
                } else {
                    //获取处理人
                    Set<String> loginNameSet = userTaskBean.getLoginNameList(processDesignId, task.getTaskDefinitionKey(), actProcessInstanceId);
                    displayList.add(task.getName() + "[" + String.join(",", loginNameSet) + "]");
                    loginList.add(String.join(",", loginNameSet));
                }
            }
            resultMap.put("displayProcessStep", String.join(",", displayList));
            resultMap.put("loginProcessStep", String.join(",", loginList));
        }
        return resultMap;
    }

    public boolean finish(String actProcessInstanceId) {
        List<Task> runTaskList = this.getRunTaskList(actProcessInstanceId);
        return ObjectUtil.isEmpty(runTaskList);
    }

    public Integer getBusinessIdByProcessInstanceId(String actProcessInstanceId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(actProcessInstanceId)
                .singleResult();
        return pi == null ? null : Integer.parseInt(pi.getBusinessKey());
    }


    public HistoricTaskInstance getHistoricTaskInstance(String actProcessInstanceId, String taskKey) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(actProcessInstanceId)
                .taskDefinitionKey(taskKey)
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();
        return list.get(0);
    }

    //自由跳转1
    @Transactional(noRollbackFor = Exception.class)
    public void jump1(Task startTask, String targetTaskKey) {
        // 获取流程定义
        Process process = repositoryService.getBpmnModel(startTask.getProcessDefinitionId()).getMainProcess();
        // 获取目标节点定义
        FlowNode targetNode = (FlowNode) process.getFlowElement(targetTaskKey);
        // 删除当前运行任务
        String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(startTask.getId()));
        //删除其他运行任务
        List<Task> runTaskList = this.getRunTaskList(startTask.getProcessInstanceId());
        for (Task task : runTaskList) {
            if (!task.getId().equals(startTask.getId())) {
                managementService.executeCommand(new DeleteTaskCmd(task.getId()));
            }
        }
        // 流程执行到来源节点
        managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
    }

    //自由跳转11
    @Transactional(noRollbackFor = Exception.class)
    public void jump11(String curTaskId, String targetTaskKey) {
        // 当前任务
        Task currentTask = taskService.createTaskQuery().taskId(curTaskId).singleResult();
        // 获取流程定义
        Process process = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        // 获取目标节点定义
        FlowNode targetNode = (FlowNode) process.getFlowElement(targetTaskKey);
        // 删除当前运行任务
        String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(currentTask.getId()));
        // 流程执行到来源节点
        managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
    }

    //自由跳转22
    @Transactional(noRollbackFor = Exception.class)
    public void jump22(String curTaskId, String targetKey) {
        managementService.executeCommand(new Jump2TargetFlowNodeCommand(curTaskId, targetKey));
    }
}
