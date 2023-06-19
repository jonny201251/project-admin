package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.OtherPower;
import com.haiying.project.service.OtherPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/otherPowerReport")
public class OtherPowerReport {
    @Autowired
    OtherPowerService otherPowerService;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<OtherPower>> get(Integer id) {
        Map<String, List<OtherPower>> map = new HashMap<>();
        List<OtherPower> list = otherPowerService.list(new LambdaQueryWrapper<OtherPower>().eq(OtherPower::getId, id));

        map.put("data", list);
        return map;
    }
}
