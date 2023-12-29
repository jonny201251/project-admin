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
        String shortYear = year.substring(2);

        Map<String, List<ProjectCreate2VO>> map = new HashMap<>();
        List<ProjectCreate2VO> list = new ArrayList<>();
        //
        List<SmallProject> list1 = smallProjectService.list(new LambdaQueryWrapper<SmallProject>().apply("substring(task_code,8,2)={0}", shortYear));
        List<BigProject> list2 = bigProjectService.list(new LambdaQueryWrapper<BigProject>().apply("substring(task_code,8,2)={0}", shortYear));

        List<Integer> idList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list1)) {
            list1.forEach(item -> idList.add(item.getId()));
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            list2.forEach(item -> idList.add(item.getId()));
        }
        //
        List<ProcessInst> list3 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessStatus, "完成").in(ProcessInst::getPath, Arrays.asList("smallProjectPath", "bigProjectPath")).in(ProcessInst::getBusinessId, idList));
        List<SmallProtect> list4 = smallProtectService.list(new LambdaQueryWrapper<SmallProtect>().in(SmallProtect::getProjectId, idList));
        List<BigProjectTest> list5 = bigProjectTestService.list(new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getDesc1, "垫资额度(万元)").eq(BigProjectTest::getType, "project").in(BigProjectTest::getProjectId, idList));

        Map<String, String> map3 = new HashMap<>();
        Map<String, String> map4 = new HashMap<>();
        Map<Integer, String> map5 = new HashMap<>();

        if (ObjectUtil.isNotEmpty(list3)) {
            list3.forEach(item -> map3.put(item.getPath() + "," + item.getBusinessId(), "完成"));
        }
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
                String str1 = map3.get("smallProjectPath," + item.getId());
                vo.setProcessStatus(str1 == null ? "审批中" : "完成");
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
                String str1 = map3.get("bigProjectPath," + item.getId());
                vo.setProcessStatus(str1 == null ? "审批中" : "完成");
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

        Collections.sort(list, Comparator.comparing(ProjectCreate2VO::getCreateDatetime));
        map.put("data", list);
        return map;
    }
}
