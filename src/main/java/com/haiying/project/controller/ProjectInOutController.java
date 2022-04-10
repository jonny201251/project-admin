package com.haiying.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectIn1;
import com.haiying.project.model.entity.ProjectIn2;
import com.haiying.project.model.entity.ProjectOut1;
import com.haiying.project.model.entity.ProjectOut2;
import com.haiying.project.model.vo.ProjectInOutVO;
import com.haiying.project.service.ProjectIn1Service;
import com.haiying.project.service.ProjectIn2Service;
import com.haiying.project.service.ProjectOut1Service;
import com.haiying.project.service.ProjectOut2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目收支-收入明细 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/projectInOut")
@Wrapper
public class ProjectInOutController {
    @Autowired
    ProjectIn1Service projectIn1Service;
    @Autowired
    ProjectIn2Service projectIn2Service;
    @Autowired
    ProjectOut1Service projectOut1Service;
    @Autowired
    ProjectOut2Service projectOut2Service;

    @GetMapping("get")
    public ProjectInOutVO get(Integer projectId) {
        ProjectInOutVO projectInOutVO = new ProjectInOutVO();
        List<ProjectIn1> in1List = projectIn1Service.list(new LambdaQueryWrapper<ProjectIn1>().eq(ProjectIn1::getProjectId, projectId));
        ProjectIn1 projectIn1 = in1List.get(0);
        //
        projectInOutVO.setName(projectIn1.getName());
        projectInOutVO.setTaskCode(projectIn1.getTaskCode());
        projectInOutVO.setProperty(projectIn1.getProperty());
        projectInOutVO.setWbs(projectIn1.getWbs());
        projectInOutVO.setRemark(projectIn1.getRemark());
        //
        List<Integer> in1IdList = in1List.stream().map(ProjectIn1::getId).collect(Collectors.toList());
        List<ProjectIn2> in2List = projectIn2Service.list(new LambdaQueryWrapper<ProjectIn2>().in(ProjectIn2::getProjectIn1Id, in1IdList).orderByAsc(ProjectIn2::getInDate));
        projectInOutVO.setIn2List(in2List);
        Double inTotal = 0.0;
        for (ProjectIn2 projectIn2 : in2List) {
            inTotal += projectIn2.getMoney2();
        }
        projectInOutVO.setInTotal(inTotal);
        //
        List<ProjectOut1> out1List = projectOut1Service.list(new LambdaQueryWrapper<ProjectOut1>().eq(ProjectOut1::getProjectId, projectId));
        List<Integer> out1IdList = out1List.stream().map(ProjectOut1::getId).collect(Collectors.toList());
        List<ProjectOut2> out2List = projectOut2Service.list(new LambdaQueryWrapper<ProjectOut2>().in(ProjectOut2::getProjectOut1Id, out1IdList).orderByAsc(ProjectOut2::getOutDate));
        projectInOutVO.setOut2List(out2List);
        Double outTotal = 0.0;
        for (ProjectOut2 projectOut2 : out2List) {
            outTotal += projectOut2.getMoney2();
        }
        projectInOutVO.setOutTotal(outTotal);
        //
        projectInOutVO.setMoreTotal(inTotal - outTotal);
        //
        Double deviceTotal = 0.0;//材料及设备费
        Double labourTotal = 0.0;//劳务费
        Double techTotal = 0.0;//技术服务费
        Double engTotal = 0.0;//工程款
        Double taxTotal = 0.0;//税费
        Double otherTotal = 0.0;//其他费用
        for (ProjectOut2 projectOut2 : out2List) {
            String costType = projectOut2.getCostType();
            if (costType.equals("材料及设备费")) {
                deviceTotal += projectOut2.getMoney2();
            } else if (costType.equals("劳务费")) {
                labourTotal += projectOut2.getMoney2();
            } else if (costType.equals("技术服务费")) {
                techTotal += projectOut2.getMoney2();
            } else if (costType.equals("工程款")) {
                engTotal += projectOut2.getMoney2();
            } else if (costType.equals("税费")) {
                taxTotal += projectOut2.getMoney2();
            } else {
                otherTotal += projectOut2.getMoney2();
            }
        }
        projectInOutVO.setDeviceTotal(deviceTotal);
        projectInOutVO.setLabourTotal(labourTotal);
        projectInOutVO.setTechTotal(techTotal);
        projectInOutVO.setEngTotal(engTotal);
        projectInOutVO.setTaxTotal(taxTotal);
        projectInOutVO.setOtherTotal(otherTotal);
        return projectInOutVO;
    }
}
