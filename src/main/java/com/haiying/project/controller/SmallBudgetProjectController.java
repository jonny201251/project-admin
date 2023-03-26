package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.BudgetProtectService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般和重大项目预算-项目 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/smallBudgetProject")
@Wrapper
public class SmallBudgetProjectController {
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    BudgetProtectService budgetProtectService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;


    @PostMapping("list")
    public IPage<BudgetProject> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        LambdaQueryWrapper<BudgetProject> wrapper = new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getHaveDisplay, "是");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object projectType = paramMap.get("projectType");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object property = paramMap.get("property");
        Object contractCode = paramMap.get("contractCode");
        Object contractName = paramMap.get("contractName");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(projectType)) {
            wrapper.like(BudgetProject::getProjectType, projectType);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(BudgetProject::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(BudgetProject::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(property)) {
            wrapper.like(BudgetProject::getProperty, property);
        }
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper.like(BudgetProject::getContractCode, contractCode);
        }
        if (ObjectUtil.isNotEmpty(contractName)) {
            wrapper.like(BudgetProject::getContractName, contractName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(BudgetProject::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(BudgetProject::getDeptName, deptName);
        }
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(BudgetProject::getDisplayName, user.getDisplayName());
        }

        IPage<BudgetProject> page = budgetProjectService.page(new Page<>(current, pageSize), wrapper);
        List<BudgetProject> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<Integer> idList = recordList.stream().map(BudgetProject::getId).collect(Collectors.toList());
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, idList));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getId())));
        }

        return page;
    }

    //用于 预计收入、支出、预算
    @PostMapping("list2")
    public IPage<BudgetProject> list2(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        LambdaQueryWrapper<BudgetProject> wrapper = new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getHaveDisplay, "是");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object contractCode = paramMap.get("contractCode");
        Object contractName = paramMap.get("contractName");

        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(BudgetProject::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(BudgetProject::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper.like(BudgetProject::getContractCode, contractCode);
        }
        if (ObjectUtil.isNotEmpty(contractName)) {
            wrapper.like(BudgetProject::getContractName, contractName);
        }
        wrapper.eq(BudgetProject::getDisplayName, user.getDisplayName());


        return budgetProjectService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody BudgetProject budgetProject) {
        return budgetProjectService.add(budgetProject);
    }

    @GetMapping("get")
    public BudgetProject get(String id) {
        BudgetProject budgetProject = budgetProjectService.getById(id);
        List<BudgetProtect> list = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, id));
        budgetProject.setList(list);
        return budgetProject;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody BudgetProject budgetProject) {
        return budgetProjectService.edit(budgetProject);
    }

    @GetMapping("modify")
    public boolean modify(Integer id) {
        return budgetProjectService.modify(id);
    }
}
