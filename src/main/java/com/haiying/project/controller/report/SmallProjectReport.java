package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.entity.SmallProtect;
import com.haiying.project.service.SmallProjectService;
import com.haiying.project.service.SmallProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/smallProjectReport")
public class SmallProjectReport {
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<SmallProject>> get(Integer id) {
        Map<String, List<SmallProject>> map = new HashMap<>();
        List<SmallProject> list = smallProjectService.list(new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getId, id));
        map.put("data", list);
        return map;
    }
    @GetMapping("get2")
    public synchronized Map<String, List<SmallProtect>> get2(Integer projectId) {
        Map<String, List<SmallProtect>> map = new HashMap<>();
        List<SmallProtect> list = smallProtectService.list(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getProjectType,"一般项目").eq(SmallProtect::getProjectId, projectId));
        map.put("data", list);
        return map;
    }
}
