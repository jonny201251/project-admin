package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.WorkFlowBean;
import com.haiying.project.common.utils.SpringUtil;
import com.haiying.project.mapper.OutContractMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.InOutVO;
import com.haiying.project.model.vo.OutContractVO;
import com.haiying.project.service.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * <p>
 * 付款合同 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-21
 */
@Service
public class OutContractServiceImpl extends ServiceImpl<OutContractMapper, OutContract> implements OutContractService {
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
    @Autowired
    FormFileService formFileService;
    @Autowired
    BudgetProjectService budgetProjectService;


    private void add(OutContract formValue) {
        formValue.setHaveDisplay("是");
        formValue.setVersion(1);
        formValue.setEndMoney(formValue.getContractMoney());
        this.save(formValue);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("OutContract");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void edit(OutContract formValue) {
        this.updateById(formValue);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "OutContract").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("OutContract");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void change(OutContract formValue) {
        OutContract outContract = this.getById(formValue.getId());
        outContract.setHaveDisplay("否");
        this.updateById(outContract);
        //
        formValue.setId(null);
        formValue.setProcessInstId(null);
        formValue.setBeforeId(outContract.getId());
        formValue.setHaveDisplay("是");
        formValue.setVersion(formValue.getVersion() + 1);
        if (formValue.getBaseId() == null) {
            //第一次修改
            formValue.setBaseId(outContract.getId());
        } else {
            //第二、三、N次修改
            formValue.setBaseId(outContract.getBaseId());
        }
        this.save(formValue);

        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "OutContract").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("OutContract");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    @Override
    public synchronized boolean btnHandle(OutContractVO outContractVO) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        OutContract formValue = outContractVO.getFormValue();
        String type = outContractVO.getType();
        String buttonName = outContractVO.getButtonName();
        String path = outContractVO.getPath();
        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                //启动流程
                String actProcessInstanceId = workFlowBean.startPrcoess(path, formValue.getId(), "新增流程");
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
                ProcessInst processInst = new ProcessInst();
                processInst.setProcessDesignId(processDesign.getId());
                processInst.setProcessName(processDesign.getName());
                processInst.setBusinessName(formValue.getContractName());
                processInst.setBusinessId(formValue.getId());
                processInst.setBusinessHaveDisplay("是");
                processInst.setBusinessVersion(1);
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
                //
                HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(actProcessInstanceId, task.getTaskDefinitionKey());
                Date startDateTime = historicTaskInstance.getStartTime();
                Date endDateTime = historicTaskInstance.getEndTime();
                processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
                processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
                processInstNodeService.save(processInstNode);

                formValue.setProcessInstId(processInst.getId());
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                //启动流程
                String actProcessInstanceId = workFlowBean.startPrcoess(path, formValue.getId(), "新增流程");
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
                ProcessInst processInst = new ProcessInst();
                processInst.setProcessDesignId(processDesign.getId());
                processInst.setProcessName(processDesign.getName());
                processInst.setBusinessName(formValue.getContractName());
                processInst.setBusinessId(formValue.getId());
                processInst.setBusinessHaveDisplay("是");
                processInst.setBusinessVersion(1);
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
                //
                HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(actProcessInstanceId, task.getTaskDefinitionKey());
                Date startDateTime = historicTaskInstance.getStartTime();
                Date endDateTime = historicTaskInstance.getEndTime();
                processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
                processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
                processInstNodeService.save(processInstNode);

                formValue.setProcessInstId(processInst.getId());
                this.updateById(formValue);
            }
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = outContractVO.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            Integer processInstId = formValue.getProcessInstId();
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
                Map<String, String> stepMap = workFlowBean.getPrcocessStep(processInst.getProcessDesignId(), processInst.getId(), actProcessInstanceId);
                processInst.setDisplayProcessStep(stepMap.get("displayProcessStep"));
                processInst.setLoginProcessStep(stepMap.get("loginProcessStep"));
                String btnName = buttonName.substring(buttonName.lastIndexOf("_") + 1);
                if (btnName.equals("退回") || btnName.equals("退回申请人")) {
                    processInst.setProcessStatus(btnName);
                } else {
                    processInst.setProcessStatus("审批中");
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
            if (ObjectUtil.isNotEmpty(outContractVO.getComment())) {
                processInstNode.setComment(outContractVO.getComment());
            }
            //
            HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(actProcessInstanceId, task.getTaskDefinitionKey());
            Date startDateTime = historicTaskInstance.getStartTime();
            Date endDateTime = historicTaskInstance.getEndTime();
            processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
            processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
            processInstNodeService.save(processInstNode);
        } else if (type.equals("change")) {
            //旧processInst
            Integer processInstId = formValue.getProcessInstId();
            ProcessInst tmp = processInstService.getById(processInstId);
            tmp.setBusinessHaveDisplay("否");
            processInstService.updateById(tmp);
            //更新旧formVale,生成新formValue
            change(formValue);
            //启动流程
            String actProcessInstanceId = workFlowBean.startPrcoess(path, formValue.getId(), "变更流程");
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
            processInst.setProcessName(processDesign.getName());
            processInst.setBusinessName(formValue.getContractName());
            processInst.setBusinessId(formValue.getId());
            //
            processInst.setBusinessBeforeId(tmp.getBusinessId());
            if (tmp.getBusinessBaseId() == null) {
                //第一次修改
                processInst.setBusinessBaseId(tmp.getBusinessId());
            } else {
                //第二、三、N次修改
                processInst.setBusinessBaseId(tmp.getBusinessBaseId());
            }
            processInst.setBusinessHaveDisplay("是");
            processInst.setBusinessVersion(tmp.getBusinessVersion() + 1);
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
            //
            HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(actProcessInstanceId, task.getTaskDefinitionKey());
            Date startDateTime = historicTaskInstance.getStartTime();
            Date endDateTime = historicTaskInstance.getEndTime();
            processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
            processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
            processInstNodeService.save(processInstNode);

            formValue.setProcessInstId(processInst.getId());
            this.updateById(formValue);
        } else if (type.equals("recall")) {
            Integer processInstId = formValue.getProcessInstId();
            ProcessInst processInst = processInstService.getById(processInstId);
            //跳转任务
            Task task = workFlowBean.getRunTaskList(processInst.getActProcessInstanceId()).get(0);
            ProcessDesignJump jumpTask = processDesignJumpService.list(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, processInst.getProcessDesignId()).eq(ProcessDesignJump::getDirection, "退回").eq(ProcessDesignJump::getButtonName, "退回申请人")).get(0);
            String firstTaskKey = jumpTask.getTargetTaskKey();
            workFlowBean.jump1(task, firstTaskKey);
            //
            Map<String, String> stepMap = workFlowBean.getPrcocessStep(processInst.getProcessDesignId(), processInst.getId(), processInst.getActProcessInstanceId());
            processInst.setDisplayProcessStep(stepMap.get("displayProcessStep"));
            processInst.setLoginProcessStep(stepMap.get("loginProcessStep"));
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
            HistoricTaskInstance historicTaskInstance = workFlowBean.getHistoricTaskInstance(processInst.getActProcessInstanceId(), task.getTaskDefinitionKey());
            Date startDateTime = historicTaskInstance.getStartTime();
            Date endDateTime = historicTaskInstance.getEndTime();
            processInstNode.setStartDatetime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
            processInstNode.setEndDatetime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));
            processInstNodeService.save(processInstNode);
        } else if (type.equals("delete")) {
            Integer processInstId = formValue.getProcessInstId();
            ProcessInst processInst = processInstService.getById(processInstId);
            if (processInst == null) {
                //草稿
                this.removeById(formValue.getId());
                this.remove(new LambdaQueryWrapper<OutContract>().eq(OutContract::getId, formValue.getId()));
            } else {
                //退回、退回申请人、申请人撤回
                Integer version = processInst.getBusinessVersion();
                if (version > 1) {
                    //回退到上一个版本
                    ProcessInst before = processInstService.getOne(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, processInst.getPath()).eq(ProcessInst::getBusinessBaseId, processInst.getBusinessBaseId()).eq(ProcessInst::getBusinessId, processInst.getBusinessBeforeId()));
                    before.setBusinessHaveDisplay("是");
                    processInstService.updateById(before);
                }
                this.removeById(formValue.getId());
                this.remove(new LambdaQueryWrapper<OutContract>().eq(OutContract::getId, formValue.getId()));
                processInstService.removeById(processInst.getId());
                processInstNodeService.remove(new LambdaQueryWrapper<ProcessInstNode>().eq(ProcessInstNode::getProcessInstId, processInst.getId()));
                workFlowBean.deleteProcessInstance(processInst.getActProcessInstanceId());
            }
        }
        return true;
    }

    @Override
    public boolean updateCode(InOutVO inOutVO) {
        OutContract outcontract = this.getById(inOutVO.getId());
        outcontract.setContractCode(inOutVO.getContractCode());
        if (ObjectUtil.isEmpty(outcontract.getWbs())) {
            outcontract.setWbs(inOutVO.getWbs());

            List<BudgetProject> list = budgetProjectService.list(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getProjectId, inOutVO.getProjectId()));
            if (ObjectUtil.isNotEmpty(list)) {
                list.forEach(item -> item.setWbs(inOutVO.getWbs()));
                budgetProjectService.updateBatchById(list);
            }

            InContractService inContractService = SpringUtil.getBean(InContractService.class);
            List<InContract> list2 = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getProjectId, inOutVO.getProjectId()));
            if (ObjectUtil.isNotEmpty(list2)) {
                list2.forEach(item -> item.setWbs(inOutVO.getWbs()));
                inContractService.updateBatchById(list2);
            }
        }
        this.updateById(outcontract);
        return true;
    }
}
