package com.haiying.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.WorkFlowBean;
import com.haiying.project.mapper.ProcessDesignMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.ProcessDesignVO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 流程设计 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Service
public class ProcessDesignServiceImpl extends ServiceImpl<ProcessDesignMapper, ProcessDesign> implements ProcessDesignService {
    @Autowired
    ProcessDesignTaskService processDesignTaskService;
    @Autowired
    ProcessDesignJumpService processDesignJumpService;
    @Autowired
    ProcessDesignEdgeService processDesignEdgeService;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    WorkFlowBean workFlowBean;

    @Override
    public boolean add(ProcessDesignVO processDesignVO) {
        ProcessDesign processDesign = processDesignVO.getProcessDesign();
        processDesign.setHaveDisplay("是");
        processDesign.setCreateDatetime(LocalDateTime.now());
        this.save(processDesign);

        List<ProcessDesignTask> taskList = processDesignVO.getTaskList();
        List<ProcessDesignEdge> edgeList = processDesignVO.getEdgeList();

        taskList.forEach(item -> {
            item.setId(null);
            item.setProcessDesignId(processDesign.getId());
            item.setTypeIds(CollUtil.join(item.getTypeIdList(), ","));
            //
            List<ProcessDesignJump> jumpList = item.getJumpList();
            if (ObjectUtil.isNotEmpty(jumpList)) {
                jumpList.forEach(itemm -> {
                    itemm.setId(null);
                    itemm.setProcessDesignId(processDesign.getId());
                });
                processDesignJumpService.saveBatch(jumpList);
            }
        });

        edgeList.forEach(item -> {
            item.setId(null);
            item.setProcessDesignId(processDesign.getId());
        });

        processDesignTaskService.saveBatch(taskList);
        processDesignEdgeService.saveBatch(edgeList);

        return true;
    }

    @Override
    public boolean edit(ProcessDesignVO processDesignVO) {
        Integer processDesignId = processDesignVO.getProcessDesign().getId();
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessDesignId, processDesignId));
        if (ObjectUtil.isEmpty(processInstList)) {
            //先删除
            this.removeById(processDesignId);
            processDesignTaskService.remove(new LambdaQueryWrapper<ProcessDesignTask>().eq(ProcessDesignTask::getProcessDesignId, processDesignId));
            processDesignJumpService.remove(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, processDesignId));
            processDesignEdgeService.remove(new LambdaQueryWrapper<ProcessDesignEdge>().eq(ProcessDesignEdge::getProcessDesignId, processDesignId));
            //后插入
            processDesignVO.getProcessDesign().setId(null);
            this.add(processDesignVO);
        } else {
            //流程设计的版本控制
            //先更新
            ProcessDesign processDesign = this.getById(processDesignId);
            processDesign.setHaveDisplay("否");
            this.updateById(processDesign);
            //
            processDesignVO.getProcessDesign().setId(null);
            processDesignVO.getProcessDesign().setDeployId(null);
            processDesignVO.getProcessDesign().setBeforeId(processDesignId);
            if (processDesign.getBaseId() == null) {
                //第一次修改
                processDesignVO.getProcessDesign().setBaseId(processDesignId);
            }
            this.add(processDesignVO);
        }
        return true;
    }

    private void delete(Integer id) {
        ProcessDesign processDesign = this.getById(id);
        String deployId = processDesign.getDeployId();
        if (ObjectUtil.isNotEmpty(deployId)) {
            //已经发起了流程实例
            List<ProcessInst> instList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().ne(ProcessInst::getProcessStatus, "完成").eq(ProcessInst::getProcessDesignId, id));
            if (ObjectUtil.isNotEmpty(instList)) {
                for (ProcessInst processInst : instList) {
                    processInstService.delete(processInst);
                }
            }
            workFlowBean.deleteDeploy(deployId);
        }
        this.removeById(id);
        processDesignTaskService.remove(new LambdaQueryWrapper<ProcessDesignTask>().eq(ProcessDesignTask::getProcessDesignId, id));
        processDesignJumpService.remove(new LambdaQueryWrapper<ProcessDesignJump>().eq(ProcessDesignJump::getProcessDesignId, id));
        processDesignEdgeService.remove(new LambdaQueryWrapper<ProcessDesignEdge>().eq(ProcessDesignEdge::getProcessDesignId, id));
        //流程版本
        if (ObjectUtil.isNotEmpty(processDesign.getBaseId())) {
            //回退到上一个版本
            Integer beforeId = processDesign.getBeforeId();
            ProcessDesign before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    @Override
    public boolean delete(List<Integer> idList) {
        for (Integer id : idList) {
            delete(id);
        }
        return true;
    }
}
