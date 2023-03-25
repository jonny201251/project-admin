package com.haiying.project.bean;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.ProcessDesign;
import com.haiying.project.model.entity.ProcessDesignEdge;
import com.haiying.project.service.ProcessDesignEdgeService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BpmnToActivitiBean {
    @Autowired
    ProcessDesignEdgeService processDesignEdgeService;

    private Map<String, String> getEgeMap(ProcessDesign processDesign) {
        Map<String, String> map = new HashMap<>();
        List<ProcessDesignEdge> edgeList = processDesignEdgeService.list(new LambdaQueryWrapper<ProcessDesignEdge>().eq(ProcessDesignEdge::getProcessDesignId, processDesign.getId()));
        if (ObjectUtil.isNotEmpty(edgeList)) {
            for (ProcessDesignEdge edge : edgeList) {
                List<String> tmp = new ArrayList<>();
                tmp.add("<sequenceFlow id=\"" + edge.getEdgeId() + "\" name=\"" + edge.getEdgeName() + "\" sourceRef=\"" + edge.getSourceTaskKey() + "\" targetRef=\"" + edge.getTargetTaskKey() + "\">");
                if (ObjectUtil.isNotEmpty(edge.getButtonName())) {
                    tmp.add("<conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[#{" + edge.getSourceTaskKey() + "==\"" + edge.getButtonName() + "\"}]]></conditionExpression>");
                } else if (ObjectUtil.isNotEmpty(edge.getConditionExpression())) {
                    tmp.add("<conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[#{" + edge.getConditionExpression() + "}]]></conditionExpression>");
                }
                tmp.add("</sequenceFlow>");
                map.put(edge.getEdgeId(), tmp.stream().collect(Collectors.joining(System.getProperty("line.separator"))));
            }
        }
        return map;
    }

    public String convert(ProcessDesign processDesign) {
        Map<String, String> edgeMap = getEgeMap(processDesign);
        List<String> list = Lists.newArrayList();
        String bpmnXml = processDesign.getBpmnXml();
        //如果流程名称包含(、)等字符，无法启动流程
        String actProcessName = "id" + processDesign.getId();
        list.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        list.add("<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:activiti=\"http://activiti.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://www.activiti.org/test\">");
        String[] arr = bpmnXml.replaceAll("bpmn:", "").split("\\r\\n|\\r|\\n");
        for (String str : arr) {
            if (ObjectUtil.isNotEmpty(str)) {
                if (str.contains("<process")) {
                    list.add("<process id=\"" + actProcessName + "\"    isExecutable=\"true\">");
                } else if (str.contains("<startEvent")) {
                    list.add(str + "</startEvent>");
                } else if (str.contains("<endEvent")) {
                    list.add(str + "</endEvent>");
                } else if (str.contains("<exclusiveGateway")) {
                    list.add(str + "</exclusiveGateway>");
                } else if (str.contains("<parallelGateway")) {
                    list.add(str + "</parallelGateway>");
                } else if (str.contains("<userTask") || str.contains("<serviceTask")) {
                    list.add(str.replaceAll("<(\\w+)Task", "<userTask") + "</userTask>");
                } else if (str.contains("<sequenceFlow")) {
                    String edgeId = ReUtil.getGroup0("id=\"[\\w|\\W]+?\"", str).replaceAll("id=", "").replaceAll("\"", "");
                    if (ObjectUtil.isNotEmpty(edgeMap.get(edgeId))) {
                        list.add(edgeMap.get(edgeId));
                    } else {
                        list.add(str);
                    }
                } else if (str.contains("</process>")) {
                    list.add("</process>");
                    list.add("</definitions>");
                    break;
                }
            }
        }
        return list.stream().collect(Collectors.joining(System.getProperty("line.separator")));
    }
}
