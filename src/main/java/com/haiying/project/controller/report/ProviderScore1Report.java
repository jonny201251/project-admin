package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.ProviderScore1;
import com.haiying.project.model.entity.ProviderScore2;
import com.haiying.project.service.ProviderScore1Service;
import com.haiying.project.service.ProviderScore2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/providerScore1Report")
public class ProviderScore1Report {
    @Autowired
    ProviderScore1Service providerScore1Service;
    @Autowired
    ProviderScore2Service providerScore2Service;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<ProviderScore1>> get(Integer id) {
        Map<String, List<ProviderScore1>> map = new HashMap<>();
        List<ProviderScore1> list = providerScore1Service.list(new LambdaQueryWrapper<ProviderScore1>().eq(ProviderScore1::getId, id));
        map.put("data", list);
        return map;
    }
    @GetMapping("get2")
    public synchronized Map<String, List<ProviderScore2>> get2(Integer providerScore1Id) {
        Map<String, List<ProviderScore2>> map = new HashMap<>();
        List<ProviderScore2> list = providerScore2Service.list(new LambdaQueryWrapper<ProviderScore2>().eq(ProviderScore2::getProviderScore1Id,providerScore1Id));
        map.put("data", list);
        return map;
    }
}
