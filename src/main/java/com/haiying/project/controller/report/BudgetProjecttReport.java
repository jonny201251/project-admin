package com.haiying.project.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.BudgetInn;
import com.haiying.project.model.entity.BudgetOut;
import com.haiying.project.model.entity.BudgetProjectt;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.service.BudgetInnService;
import com.haiying.project.service.BudgetOutService;
import com.haiying.project.service.BudgetProjecttService;
import com.haiying.project.service.BudgetProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bugetProjecttReport")
public class BudgetProjecttReport {
    @Autowired
    BudgetProjecttService budgetProjecttService;
    @Autowired
    BudgetProtectService budgetProtectService;
    @Autowired
    BudgetInnService budgetInnService;
    @Autowired
    BudgetOutService budgetOutService;
    @Autowired
    HttpSession httpSession;

    @GetMapping("get")
    public synchronized Map<String, List<BudgetProjectt>> get(Integer id) {
        Map<String, List<BudgetProjectt>> map = new HashMap<>();
        List<BudgetProjectt> list = budgetProjecttService.list(new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getId, id));

        map.put("data", list);
        return map;
    }

    @GetMapping("get5")
    public synchronized Map<String, List<BudgetProtect>> get5(Integer budgetId) {
        Map<String, List<BudgetProtect>> map = new HashMap<>();
        List<BudgetProtect> list = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, budgetId));

        map.put("data", list);
        return map;
    }

    @GetMapping("get6")
    public synchronized Map<String, List<BudgetInn>> get6(Integer budgetId) {
        Map<String, List<BudgetInn>> map = new HashMap<>();
        List<BudgetInn> list = budgetInnService.list(new LambdaQueryWrapper<BudgetInn>().eq(BudgetInn::getBudgetId, budgetId));

        map.put("data", list);
        return map;
    }

    @GetMapping("get7")
    public synchronized Map<String, List<BudgetOut>> get7(Integer budgetId) {
        Map<String, List<BudgetOut>> map = new HashMap<>();
        List<BudgetOut> list = budgetOutService.list(new LambdaQueryWrapper<BudgetOut>().eq(BudgetOut::getBudgetId, budgetId));

        map.put("data", list);
        return map;
    }
}
