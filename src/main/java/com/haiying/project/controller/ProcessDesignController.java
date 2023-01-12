package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.bean.WorkFlowBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.ProcessDesignVO;
import com.haiying.project.model.vo.ProcessFormBefore;
import com.haiying.project.service.*;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 流程设计 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@RestController
@RequestMapping("/processDesign")
@Wrapper
public class ProcessDesignController {
    @Autowired
    ProcessDesignService processDesignService;
    @Autowired
    ProcessDesignTaskService processDesignTaskService;
    @Autowired
    ProcessDesignJumpService processDesignJumpService;
    @Autowired
    ProcessDesignEdgeService processDesignEdgeService;
    @Autowired
    WorkFlowBean workFlowBean;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<ProcessDesign> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProcessDesign> queryWrapper = new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object path = paramMap.get("path");
        Object processType = paramMap.get("processType");
        if (ObjectUtil.isNotEmpty(name)) {
            queryWrapper.like(ProcessDesign::getName, name);
        }
        if (ObjectUtil.isNotEmpty(path)) {
            queryWrapper.like(ProcessDesign::getPath, path);
        }
        if (ObjectUtil.isNotEmpty(processType)) {
            queryWrapper.eq(ProcessDesign::getProcessType, processType);
        }
        IPage<ProcessDesign> page = processDesignService.page(new Page<>(current, pageSize), queryWrapper);
        page.getRecords().forEach(item -> item.setBpmnXml(null));
        return page;
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProcessDesignVO processDesignVO) {
        return processDesignService.add(processDesignVO);
    }

    @GetMapping("get")
    public ProcessDesignVO get(Integer id) {
        ProcessDesignVO processDesignVO = new ProcessDesignVO();
        ProcessDesign processDesign = processDesignService.getById(id);
        List<ProcessDesignTask> taskList = processDesignTaskService.list(new LambdaQueryWrapper<ProcessDesignTask>().eq(ProcessDesignTask::getProcessDesignId, id));
        taskList.forEach(item -> {
            if (ObjectUtil.isNotEmpty(item.getTypeIds())) {
                List<String> list = Arrays.asList(item.getTypeIds().split(","));
                item.setTypeIdList(list.stream().map(Integer::parseInt).collect(Collectors.toList()));
            }
            //
            List<ProcessDesignJump> jumpList = processDesignJumpService.list(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, id).eq(ProcessDesignJump::getSourceTaskKey, item.getTaskKey()));
            if (ObjectUtil.isNotEmpty(jumpList)) {
                item.setJumpList(jumpList);
            }
        });
        List<ProcessDesignEdge> edgeList = processDesignEdgeService.list(new LambdaQueryWrapper<ProcessDesignEdge>().eq(ProcessDesignEdge::getProcessDesignId, id));

        processDesignVO.setProcessDesign(processDesign);
        processDesignVO.setTaskList(taskList);
        processDesignVO.setEdgeList(edgeList);
        return processDesignVO;
    }

    @GetMapping("getBpmnXml")
    public ResponseResult getBpmnXml(Integer processInstId) {
        ProcessInst processInst = processInstService.getById(processInstId);
        return ResponseResult.success(processDesignService.getById(processInst.getProcessDesignId()).getBpmnXml());
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProcessDesignVO processDesignVO) {
        return processDesignService.edit(processDesignVO);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return processDesignService.delete(idList);
    }

    private List<String> getTaskButton(Integer processDesignId, String taskKey) {
        List<String> btnList = new ArrayList<>();
        String[] btnArr = new String[10];
        //edge
        List<ProcessDesignEdge> edgeList = processDesignEdgeService.list(new LambdaQueryWrapper<ProcessDesignEdge>().eq(ProcessDesignEdge::getProcessDesignId, processDesignId).eq(ProcessDesignEdge::getSourceTaskKey, taskKey).isNotNull(ProcessDesignEdge::getDirection).isNotNull(ProcessDesignEdge::getButtonName));
        if (ObjectUtil.isNotEmpty(edgeList)) {
            edgeList.forEach(edge -> btnArr[edge.getButtonSort()] = "edge_" + edge.getButtonName());
        }
        //jump
        List<ProcessDesignJump> jumpList = processDesignJumpService.list(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, processDesignId).eq(ProcessDesignJump::getSourceTaskKey, taskKey));
        if (ObjectUtil.isNotEmpty(jumpList)) {
            jumpList.forEach(item -> btnArr[item.getButtonSort()] = "jump_" + item.getTargetTaskKey() + "_" + item.getButtonName());
        }
        //
        for (String str : btnArr) {
            if (ObjectUtil.isNotEmpty(str)) {
                btnList.add(str);
            }
        }
        return btnList;
    }

    /*
      按钮类型：
        一条连线：oneEdge_提交/oneEdge_按钮名称
        多条连线：moreEdge_按钮名称1，moreEdge_按钮名称2

        连线：edge_按钮名称
        自由跳转：jump_targetId_按钮名称
      功能说明：获取 新增、编辑、审批、变更流程的表单的按钮
     */
    @PostMapping("getProcessFormBefore")
    public ProcessFormBefore getProcessFormBefore(@RequestBody Map<String, Object> map) {
        ProcessFormBefore before = new ProcessFormBefore();
        List<String> buttonList = new ArrayList<>();
        //
        String path = (String) map.get("path");
        String type = (String) map.get("type");
        Integer processInstId = (Integer) map.get("processInstId");

        if (type.equals("add") || type.equals("edit")) {
            //取出流程图中的第一个节点
            ProcessDesign processDesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, "新增流程"));
            ProcessDesignJump jumpTask = processDesignJumpService.list(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, processDesign.getId()).eq(ProcessDesignJump::getDirection, "退回").eq(ProcessDesignJump::getButtonName, "退回申请人")).get(0);
            String firstTaskKey = jumpTask.getTargetTaskKey();
            buttonList = getTaskButton(processDesign.getId(), firstTaskKey);
            if (ObjectUtil.isEmpty(buttonList)) {
                buttonList.add("草稿");
                buttonList.add("edge_提交");
            }  
        } else if (type.equals("change")) {
            ProcessDesign processDesign;
            processDesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, "变更流程"));
            if (processDesign == null) {
                processDesign = processDesignService.getOne(new LambdaQueryWrapper<ProcessDesign>().eq(ProcessDesign::getHaveDisplay, "是").eq(ProcessDesign::getPath, path).eq(ProcessDesign::getProcessType, "新增流程"));
            }
            ProcessDesignJump jumpTask = processDesignJumpService.list(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, processDesign.getId()).eq(ProcessDesignJump::getDirection, "退回").eq(ProcessDesignJump::getButtonName, "退回申请人")).get(0);
            String firstTaskKey = jumpTask.getTargetTaskKey();
            buttonList = getTaskButton(processDesign.getId(), firstTaskKey);
            if (ObjectUtil.isEmpty(buttonList)) {
                buttonList.add("edge_提交");
            }
        } else if (type.equals("check")) {
            ProcessInst processInst = processInstService.getById(processInstId);
            Task myTask = workFlowBean.getMyRunTask(processInst.getActProcessInstanceId());
            buttonList = getTaskButton(processInst.getProcessDesignId(), myTask.getTaskDefinitionKey());
            if (ObjectUtil.isEmpty(buttonList)) {
                buttonList.add("edge_同意");
            }
            ProcessDesignTask processDesignTask = processDesignTaskService.getOne(new LambdaQueryWrapper<ProcessDesignTask>().eq(ProcessDesignTask::getProcessDesignId, processInst.getProcessDesignId()).eq(ProcessDesignTask::getTaskKey, myTask.getTaskDefinitionKey()));
            before.setHaveEditForm(processDesignTask.getHaveEditForm());
        } else if (type.equals("reject") || type.equals("recall")) {
            ProcessInst processInst = processInstService.getById(processInstId);
            Task myTask = workFlowBean.getMyRunTask(processInst.getActProcessInstanceId());
            buttonList = getTaskButton(processInst.getProcessDesignId(), myTask.getTaskDefinitionKey());
            if (ObjectUtil.isEmpty(buttonList)) {
                buttonList.add("edge_提交");
            }
            before.setHaveEditForm("是");
        }
        before.setButtonList(buttonList);
        return before;
    }
}
