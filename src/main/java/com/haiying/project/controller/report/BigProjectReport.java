package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.BigProject;
import com.haiying.project.model.entity.BigProjectTest;
import com.haiying.project.model.entity.BigProjectTest2;
import com.haiying.project.model.entity.SmallProtect;
import com.haiying.project.service.BigProjectService;
import com.haiying.project.service.BigProjectTest2Service;
import com.haiying.project.service.BigProjectTestService;
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
@RequestMapping("/bigProjectReport")
public class BigProjectReport {
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    BigProjectTestService bigProjectTestService;
    @Autowired
    BigProjectTest2Service bigProjectTest2Service;


    @GetMapping("get")
    public synchronized Map<String, List<BigProject>> get(Integer id) {
        Map<String, List<BigProject>> map = new HashMap<>();
        List<BigProject> list = bigProjectService.list(new LambdaQueryWrapper<BigProject>().eq(BigProject::getId, id));
        map.put("data", list);
        return map;
    }

    @GetMapping("get2")
    public synchronized Map<String, List<SmallProtect>> get2(Integer projectId) {
        Map<String, List<SmallProtect>> map = new HashMap<>();
        List<SmallProtect> list = smallProtectService.list(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getProjectType, "重大项目").eq(SmallProtect::getProjectId, projectId));
        map.put("data", list);
        return map;
    }

    @GetMapping("get5")
    public synchronized Map<String, List<BigProjectTest>> get5(Integer projectId) {
        Map<String, List<BigProjectTest>> map = new HashMap<>();

        //
        List<BigProjectTest2> test2List = bigProjectTest2Service.list();
        Map<String, String> test2Map = new HashMap<>();
        for (BigProjectTest2 tmp : test2List) {
            test2Map.put(tmp.getType() + tmp.getDesc1(), tmp.getScoreDesc());
        }

        List<BigProjectTest> list = bigProjectTestService.list(new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getType, "project").eq(BigProjectTest::getProjectId, projectId));
        for (BigProjectTest test : list) {
            test.setScoreDesc(test2Map.get("project" + test.getDesc1()));
        }

        map.put("data", list);
        return map;
    }

    @GetMapping("get6")
    public synchronized Map<String, List<BigProjectTest>> get6(Integer projectId) {
        Map<String, List<BigProjectTest>> map = new HashMap<>();

        //
        List<BigProjectTest2> test2List = bigProjectTest2Service.list();
        Map<String, String> test2Map = new HashMap<>();
        for (BigProjectTest2 tmp : test2List) {
            test2Map.put(tmp.getType() + tmp.getDesc1(), tmp.getScoreDesc());
        }

        List<BigProjectTest> list = bigProjectTestService.list(new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getType, "customer").eq(BigProjectTest::getProjectId, projectId));
        for (BigProjectTest test : list) {
            test.setScoreDesc(test2Map.get("customer" + test.getDesc1()));
        }

        map.put("data", list);
        return map;
    }

    @GetMapping("get7")
    public synchronized Map<String, List<BigProjectTest>> get7(Integer projectId) {
        Map<String, List<BigProjectTest>> map = new HashMap<>();

        //
        List<BigProjectTest2> test2List = bigProjectTest2Service.list();
        Map<String, String> test2Map = new HashMap<>();
        for (BigProjectTest2 tmp : test2List) {
            test2Map.put(tmp.getType() + tmp.getDesc1(), tmp.getScoreDesc());
        }

        List<BigProjectTest> list = bigProjectTestService.list(new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getType, "provider").eq(BigProjectTest::getProjectId, projectId));
        for (BigProjectTest test : list) {
            test.setScoreDesc(test2Map.get("provider" + test.getDesc1()));
        }

        map.put("data", list);
        return map;
    }
}
