package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.BudgetInVO;
import com.haiying.project.service.BudgetInService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一般和重大项目预算-预计收入 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/smallBudgetIn")
@Wrapper
public class SmallBudgetInController {
    @Autowired
    BudgetInService budgetInService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<BudgetIn> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        QueryWrapper<BudgetIn> wrapper = new QueryWrapper<BudgetIn>().eq("have_display", "是");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object inType = paramMap.get("inType");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like("task_code", taskCode);
        }
        if (ObjectUtil.isNotEmpty(inType)) {
            wrapper.eq("in_type", inType);
        }

        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq("display_name", user.getDisplayName());
        }
        wrapper.select("distinct budget_id,name,task_code,in_type,version,display_name,dept_name,create_datetime").orderByAsc("budget_id,sort");
        return budgetInService.page(new Page<>(current, pageSize), wrapper);
    }


    @PostMapping("add")
    public boolean add(@RequestBody BudgetInVO budgetInVO) {
        //判断是否重复添加
        List<BudgetIn> ll = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getTaskCode, budgetInVO.getTaskCode()).eq(BudgetIn::getInType, budgetInVO.getInType()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号和收入类型   已存在");
        }

        double count = 1;
        List<BudgetIn> list = budgetInVO.getList();
        for (BudgetIn budgetIn : list) {
            if (ObjectUtil.isEmpty(budgetInVO.getSort())) {
                budgetIn.setSort(count++);
            } else {
                budgetIn.setSort(budgetInVO.getSort());
            }
            budgetIn.setHaveDisplay(budgetInVO.getHaveDisplay());
            budgetIn.setVersion(budgetInVO.getVersion());
            budgetIn.setBudgetId(budgetInVO.getBudgetId());
            budgetIn.setProjectId(budgetInVO.getProjectId());
            budgetIn.setProjectType(budgetInVO.getProjectType());
            budgetIn.setName(budgetInVO.getName());
            budgetIn.setTaskCode(budgetInVO.getTaskCode());
            budgetIn.setInType(budgetInVO.getInType());
            budgetIn.setRemark(budgetInVO.getRemark());
            budgetIn.setLoginName(budgetInVO.getLoginName());
            budgetIn.setDisplayName(budgetInVO.getDisplayName());
            budgetIn.setDeptId(budgetInVO.getDeptId());
            budgetIn.setDeptName(budgetInVO.getDeptName());
            budgetIn.setCreateDatetime(budgetInVO.getCreateDatetime());
        }
        return budgetInService.saveBatch(list);
    }

    @GetMapping("get")
    public BudgetInVO get(Integer budgetId, String inType) {
        BudgetInVO budgetInVO = new BudgetInVO();
        List<BudgetIn> list = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, budgetId).eq(BudgetIn::getInType, inType));
        BeanUtils.copyProperties(list.get(0), budgetInVO);
        budgetInVO.setList(list);
        return budgetInVO;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody BudgetInVO budgetInVO) {
        return budgetInService.edit(budgetInVO);
    }
}
