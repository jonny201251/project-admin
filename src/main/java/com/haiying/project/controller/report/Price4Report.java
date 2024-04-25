package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.Price4;
import com.haiying.project.service.Price4Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/price4Report")
public class Price4Report {
    @Autowired
    Price4Service price4Service;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<Price4>> get(Integer id) {
        Map<String, List<Price4>> map = new HashMap<>();
        List<Price4> list = price4Service.list(new LambdaQueryWrapper<Price4>().eq(Price4::getId, id));
        map.put("data", list);
        return map;
    }
}
