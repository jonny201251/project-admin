package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.CustomerScore1;
import com.haiying.project.model.entity.CustomerScore2;
import com.haiying.project.service.CustomerScore1Service;
import com.haiying.project.service.CustomerScore2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customerScore1Report")
public class CustomerScore1Report {
    @Autowired
    CustomerScore1Service customerScore1Service;
    @Autowired
    CustomerScore2Service customerScore2Service;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<CustomerScore1>> get(Integer id) {
        Map<String, List<CustomerScore1>> map = new HashMap<>();
        List<CustomerScore1> list = customerScore1Service.list(new LambdaQueryWrapper<CustomerScore1>().eq(CustomerScore1::getHaveDisplay, "是").eq(CustomerScore1::getId, id));
        map.put("data", list);
        return map;
    }

    @GetMapping("get2")
    public synchronized Map<String, List<CustomerScore2>> get2(Integer customerScore1Id) {
        Map<String, List<CustomerScore2>> map = new HashMap<>();
        List<CustomerScore2> list = customerScore2Service.list(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, customerScore1Id));
        map.put("data", list);
        return map;
    }
}
