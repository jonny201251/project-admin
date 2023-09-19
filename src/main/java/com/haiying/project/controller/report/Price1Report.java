package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.Price1;
import com.haiying.project.model.entity.Price11;
import com.haiying.project.service.Price11Service;
import com.haiying.project.service.Price1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/price1Report")
public class Price1Report {
    @Autowired
    Price1Service price1Service;
    @Autowired
    Price11Service price11Service;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<Price1>> get(Integer id) {
        Map<String, List<Price1>> map = new HashMap<>();
        List<Price1> list = price1Service.list(new LambdaQueryWrapper<Price1>().eq(Price1::getId, id));
        map.put("data", list);
        return map;
    }
    @GetMapping("get2")
    public synchronized Map<String, List<Price11>> get2(Integer price1Id) {
        Map<String, List<Price11>> map = new HashMap<>();
        List<Price11> list = price11Service.list(new LambdaQueryWrapper<Price11>().eq(Price11::getPrice1Id,price1Id));
        map.put("data", list);
        return map;
    }
}
