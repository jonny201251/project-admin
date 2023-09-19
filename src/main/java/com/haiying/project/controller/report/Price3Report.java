package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.Price3;
import com.haiying.project.model.entity.Price33;
import com.haiying.project.service.Price33Service;
import com.haiying.project.service.Price3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/price3Report")
public class Price3Report {
    @Autowired
    Price3Service price3Service;
    @Autowired
    Price33Service price33Service;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<Price3>> get(Integer id) {
        Map<String, List<Price3>> map = new HashMap<>();
        List<Price3> list = price3Service.list(new LambdaQueryWrapper<Price3>().eq(Price3::getId, id));
        map.put("data", list);
        return map;
    }
    @GetMapping("get2")
    public synchronized Map<String, List<Price33>> get2(Integer price3Id) {
        Map<String, List<Price33>> map = new HashMap<>();
        List<Price33> list = price33Service.list(new LambdaQueryWrapper<Price33>().eq(Price33::getPrice3Id,price3Id));
        map.put("data", list);
        return map;
    }
}
