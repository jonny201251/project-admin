package com.haiying.project.controller.report;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.ProjectCreate2VO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

//项目立项-汇总2 列表
@RestController
@RequestMapping("/projectCreate2Report")
public class ProjectCreate2Report {
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    BigProjectTestService bigProjectTestService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    ProcessInstService processInstService;


    @GetMapping("get")
    public synchronized Map<String, List<ProjectCreate2VO>> get(String year) {
        Map<String, List<ProjectCreate2VO>> map = new HashMap<>();
        List<ProjectCreate2VO> list = new ArrayList<>();
        //
        List<ProcessInst> list0 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessStatus, "完成").in(ProcessInst::getPath, Arrays.asList("smallProjectPath", "bigProjectPath")).likeRight(ProcessInst::getEndDatetime, year));
        Map<String, LocalDateTime> map0 = new HashMap<>();
        List<Integer> idList = new ArrayList<>();
        List<Integer> idList1 = new ArrayList<>();
        List<Integer> idList2 = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list0)) {
            for (ProcessInst item : list0) {
                map0.put(item.getPath() + "," + item.getBusinessId(), item.getEndDatetime());
                idList.add(item.getBusinessId());
                if ("smallProjectPath".equals(item.getPath())) {
                    idList1.add(item.getBusinessId());
                } else {
                    idList2.add(item.getBusinessId());
                }
            }
        }
        //
        List<SmallProject> list1 = null;
        LambdaQueryWrapper<SmallProject> wrapper1 = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHaveDisplay, "是");
        if (ObjectUtil.isNotEmpty(idList1)) {
            wrapper1.in(SmallProject::getId, idList1);
            list1 = smallProjectService.list(wrapper1);
        }
        List<BigProject> list2 = null;
        LambdaQueryWrapper<BigProject> wrapper2 = new LambdaQueryWrapper<BigProject>().eq(BigProject::getHaveDisplay, "是");
        if (ObjectUtil.isNotEmpty(idList2)) {
            wrapper2.in(BigProject::getId, idList2);
            list2 = bigProjectService.list(wrapper2);
        }

        //
        List<SmallProtect> list4 = null;
        LambdaQueryWrapper<SmallProtect> wrapper4 = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(idList)) {
            wrapper4.in(SmallProtect::getProjectId, idList);
            list4 = smallProtectService.list(wrapper4);
        }

        List<BigProjectTest> list5 = null;
        LambdaQueryWrapper<BigProjectTest> wrapper5=new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getDesc1, "垫资额度(万元)").eq(BigProjectTest::getType, "project");
        if(ObjectUtil.isNotEmpty(idList2)){
            wrapper5.in(BigProjectTest::getProjectId, idList2);
            list5=bigProjectTestService.list(wrapper5);
        }


        Map<String, String> map4 = new HashMap<>();
        Map<Integer, String> map5 = new HashMap<>();

        if (ObjectUtil.isNotEmpty(list4)) {
            list4.forEach(item -> map4.put(item.getProjectType() + "," + item.getProjectId() + "," + item.getType(), item.getMoney()));
        }
        if (ObjectUtil.isNotEmpty(list5)) {
            list5.forEach(item -> map5.put(item.getProjectId(), item.getDesc2()));
        }
        //
        if (ObjectUtil.isNotEmpty(list1)) {
            for (SmallProject item : list1) {
                ProjectCreate2VO vo = new ProjectCreate2VO();

                vo.setDeptName(item.getDeptName());
                vo.setCreateDatetime(item.getCreateDatetime());
                LocalDateTime endDatetime = map0.get("smallProjectPath," + item.getId());
                vo.setEndDatetime(endDatetime);
                vo.setProcessStatus(endDatetime == null ? "审批中" : "完成");
                vo.setProjectType(vo.getProjectType());
                vo.setProjectTypee(vo.getProjectTypee());
                vo.setName(vo.getName());
                vo.setTaskCode(vo.getTaskCode());
                vo.setProperty(vo.getProperty());
                vo.setProjectRate(vo.getProjectRate());
                vo.setExpectMoney(vo.getExpectMoney());
                vo.setExpectDate(vo.getExpectDate());
                vo.setA1(map4.get("一般项目," + item.getId() + ",投标保证金/函"));
                vo.setA2(map4.get("一般项目," + item.getId() + ",质量保证金/函"));
                vo.setA3(map4.get("一般项目," + item.getId() + ",工资保证金/函"));
                vo.setA4(map4.get("一般项目," + item.getId() + ",履约保证金/函"));
                if (ObjectUtil.isNotEmpty(item.getGiveMoney())) {
                    vo.setGiveMoney(item.getGiveMoney() + "元");
                }
                vo.setLocation(item.getLocation());
                vo.setProviderName(item.getProviderName());
                String str2 = item.getHaveBid();
                vo.setBidStatus("是".equals(str2) ? "投标" : "直签");
                vo.setBidDate(item.getBidDate());
                vo.setRemark(item.getRemark());

                list.add(vo);
            }
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            for (BigProject item : list2) {
                ProjectCreate2VO vo = new ProjectCreate2VO();

                vo.setDeptName(item.getDeptName());
                vo.setCreateDatetime(item.getCreateDatetime());
                LocalDateTime endDatetime = map0.get("bigProjectPath," + item.getId());
                vo.setEndDatetime(endDatetime);
                vo.setProcessStatus(endDatetime == null ? "审批中" : "完成");
                vo.setProjectType(vo.getProjectType());
                vo.setProjectTypee(vo.getProjectTypee());
                vo.setName(vo.getName());
                vo.setProperty(vo.getProperty());
                vo.setProjectRate(vo.getProjectRate());
                vo.setExpectMoney(vo.getExpectMoney());
//                vo.setExpectDate(vo.getExpectDate());
                vo.setA1(map4.get("重大项目," + item.getId() + ",投标保证金/函"));
                vo.setA2(map4.get("重大项目," + item.getId() + ",质量保证金/函"));
                vo.setA3(map4.get("重大项目," + item.getId() + ",工资保证金/函"));
                vo.setA4(map4.get("重大项目," + item.getId() + ",履约保证金/函"));
                if (ObjectUtil.isNotEmpty(map5.get(item.getId()))) {
                    vo.setGiveMoney(map5.get(item.getId()) + "万元");
                }
                vo.setLocation(item.getLocation());
                vo.setProviderName(item.getProviderName());
                vo.setBidStatus("投标");
                vo.setBidDate(item.getExpectDate());
                vo.setRemark(item.getRemark());

                list.add(vo);
            }
        }
        if (ObjectUtil.isNotEmpty(list)) {
            Collections.sort(list, Comparator.comparing(ProjectCreate2VO::getEndDatetime));
        }
        map.put("data", list);
        return map;
    }
}
