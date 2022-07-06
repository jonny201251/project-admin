package com.haiying.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.vo.SmallBudgetMoney1VO;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.BudgetProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//一般项目预算表
@RestController
@RequestMapping("/smallBudgetMoney")
public class SmallBudgetMoneyController {
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    BudgetProtectService budgetProtectService;

    @PostMapping("list")
    @Wrapper
    public IPage<SmallProject> list(@RequestBody Map<String, Object> paramMap) {
        return null;
    }

    //项目基本信息
    public synchronized Map<String, List<SmallBudgetMoney1VO>> get1(Integer projectId) {
        Map<String, List<SmallBudgetMoney1VO>> map = new HashMap<>();
        List<SmallBudgetMoney1VO> list = new ArrayList<>();
        SmallBudgetMoney1VO vo = new SmallBudgetMoney1VO();
        BudgetProject project = budgetProjectService.getOne(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getHaveDisplay, "是").eq(BudgetProject::getProjectId, projectId));
        if (project != null) {
            vo.setDeptName(project.getDeptName());
            vo.setName(project.getName());
            vo.setProjectDisplayName(project);

            List<BudgetProtect> protectList = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, project.getId()));
        }



        list.add(vo);
        map.put("data", list);
        return map;
    }
    //收入信息
    //支出信息
}
