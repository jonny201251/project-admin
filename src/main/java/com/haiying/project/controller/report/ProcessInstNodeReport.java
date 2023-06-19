package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.ProcessInstNode;
import com.haiying.project.service.ProcessInstNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/processInstNodeReport")
public class ProcessInstNodeReport {
    @Autowired
    ProcessInstNodeService processInstNodeService;

    @GetMapping("get")
    public synchronized Map<String, List<ProcessInstNode>> get(Integer processInstId) {
        Map<String, List<ProcessInstNode>> map = new HashMap<>();
        List<ProcessInstNode> list = processInstNodeService.list(new LambdaQueryWrapper<ProcessInstNode>().eq(ProcessInstNode::getProcessInstId, processInstId));

        map.put("data", list);
        return map;
    }
}
