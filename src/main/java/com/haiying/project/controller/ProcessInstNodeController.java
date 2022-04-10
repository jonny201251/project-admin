package com.haiying.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInstNode;
import com.haiying.project.service.ProcessInstNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 流程实例节点 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@RestController
@RequestMapping("/processInstNode")
@Wrapper
public class ProcessInstNodeController {
    @Autowired
    ProcessInstNodeService processInstNodeService;

    @PostMapping("list")
    public IPage<ProcessInstNode> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Integer processInstId = (Integer) paramMap.get("processInstId");
        return processInstNodeService.page(new Page<>(1, 100), new LambdaQueryWrapper<ProcessInstNode>().eq(ProcessInstNode::getProcessInstId, processInstId));
    }
}
