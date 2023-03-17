package com.haiying.project.common.activiti;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.bean.UserTaskBean;
import com.haiying.project.bean.WorkFlowBean;
import com.haiying.project.common.utils.SpringUtil;
import com.haiying.project.model.entity.ProcessDesignTask;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.ProcessInstNode;
import com.haiying.project.service.ProcessDesignTaskService;
import com.haiying.project.service.ProcessInstNodeService;
import com.haiying.project.service.ProcessInstService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

//设置任务的处理人
@Component
@Slf4j
public class ActEventListener implements ActivitiEventListener {
    @Autowired
    HttpSession httpSession;
    //这里无法注入自己定义的service,所以使用了SpringUtil
    //@Autowired
    //ProcessInstanceDataService processInstanceDataService

    @Override
    public void onEvent(ActivitiEvent activitiEvent) {
        if (activitiEvent.getType().equals(ActivitiEventType.TASK_CREATED)) {
            ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) activitiEvent;
            TaskEntity taskEntity = (TaskEntity) entityEvent.getEntity();
            //id13:1:5003
            String actProcessDefinitionId = taskEntity.getProcessDefinitionId();
            //13
            Integer processDesignId = Integer.parseInt(actProcessDefinitionId.split(":")[0].replaceAll("id", ""));
            //
            String actProcessInstanceId = taskEntity.getProcessInstanceId();
            String taskKey = taskEntity.getTaskDefinitionKey();

            //businessId
            WorkFlowBean workFlowBean = SpringUtil.getBean(WorkFlowBean.class);
            Integer businessId = workFlowBean.getBusinessIdByProcessInstanceId(actProcessInstanceId);

            ProcessInstService processInstService = SpringUtil.getBean(ProcessInstService.class);
            ProcessInstNodeService processInstNodeService = SpringUtil.getBean(ProcessInstNodeService.class);
            ProcessDesignTaskService processDesignTaskService = SpringUtil.getBean(ProcessDesignTaskService.class);
            UserTaskBean userTaskBean = SpringUtil.getBean(UserTaskBean.class);
            //历史节点
            ProcessInstNode processInstNode = null;
            ProcessInst processInst = processInstService.getOne(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessDesignId, processDesignId).eq(ProcessInst::getActProcessInstanceId, actProcessInstanceId));
            if (processInst != null) {
                List<ProcessInstNode> nodeList = processInstNodeService.list(new LambdaQueryWrapper<ProcessInstNode>().eq(ProcessInstNode::getProcessInstId, processInst.getId()).eq(ProcessInstNode::getTaskKey, taskKey));
                if (ObjectUtil.isNotEmpty(nodeList)) {
                    processInstNode = nodeList.get(0);
                }
            }
            //
            if (processInstNode != null) {
                taskEntity.addCandidateUser(processInstNode.getLoginName());
            } else {
                ProcessDesignTask processDesignTask = processDesignTaskService.getOne(new LambdaQueryWrapper<ProcessDesignTask>().eq(ProcessDesignTask::getProcessDesignId, processDesignId).eq(ProcessDesignTask::getTaskKey, taskKey));
                Set<String> loginNameSet = userTaskBean.getLoginNameList(processDesignTask, businessId, actProcessInstanceId);
                if (ObjectUtil.isNotEmpty(loginNameSet)) {
                    taskEntity.addCandidateUsers(loginNameSet);
                } else {
                    log.error(processDesignTask.getTaskName() + "-没有处理人");
                }
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        log.error("GlobalEventListener-isFailOnException处理人发生错误");
        return false;
    }
}
