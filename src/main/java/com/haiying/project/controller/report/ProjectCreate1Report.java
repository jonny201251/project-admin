package com.haiying.project.controller.report;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.ProjectCreate1VO;
import com.haiying.project.model.vo.ProjectCreate2VO;
import com.haiying.project.service.BigProjectService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallProjectService;
import com.haiying.project.service.SmallProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

////项目立项-汇总1
@RestController
@RequestMapping("/projectCreate1Report")
public class ProjectCreate1Report {
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    ProcessInstService processInstService;

    @GetMapping("get")
    public synchronized Map<String, List<ProjectCreate1VO>> get(String year) {
        String shortYear = year.substring(2);

        Map<String, List<ProjectCreate1VO>> map = new HashMap<>();
        List<ProjectCreate1VO> list = new ArrayList<>();

        List<ProjectCreate2VO> listt = new ArrayList<>();
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

        Map<String, String> map3 = new HashMap<>();

        if (ObjectUtil.isNotEmpty(list3)) {
            list3.forEach(item -> map3.put(item.getPath() + "," + item.getBusinessId(), "完成"));
        }

        //
        if (ObjectUtil.isNotEmpty(list1)) {
            for (SmallProject item : list1) {
                ProjectCreate2VO vo = new ProjectCreate2VO();

                vo.setDeptName(item.getDeptName());
                vo.setName(vo.getName());
                vo.setTaskCode(vo.getTaskCode());
                vo.setProperty(vo.getProperty());
                String str2 = item.getHaveBid();
                vo.setBidStatus("是".equals(str2) ? "投标" : "直签");
                String str1 = map3.get("smallProjectPath," + item.getId());
                if(str1!=null){
                    listt.add(vo);
                }
            }
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            for (BigProject item : list2) {
                ProjectCreate2VO vo = new ProjectCreate2VO();

                vo.setDeptName(item.getDeptName());
                vo.setName(vo.getName());
                vo.setProperty(vo.getProperty());
                vo.setBidStatus("投标");
                String str1 = map3.get("bigProjectPath," + item.getId());
                if(str1!=null){
                    listt.add(vo);
                }
            }
        }

        //


        map.put("data", list);
        return map;
    }
}
