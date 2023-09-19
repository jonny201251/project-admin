package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.Price2;
import com.haiying.project.service.Price2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/price2Report")
public class Price2Report {
    @Autowired
    Price2Service price2Service;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<Price2>> get(Integer id) {
        Map<String, List<Price2>> map = new HashMap<>();
        List<Price2> list = price2Service.list(new LambdaQueryWrapper<Price2>().eq(Price2::getId, id));
        map.put("data", list);
        return map;
    }
}
