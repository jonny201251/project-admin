package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.model.vo.BudgetInVO;
import com.haiying.project.service.BudgetInService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("list")
    public IPage<BudgetIn> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        QueryWrapper<BudgetIn> wrapper = new QueryWrapper<BudgetIn>().eq("have_display", "是").select("distinct budget_id,project_id,name,task_code,in_type,version").orderByDesc("name,in_type");
        return budgetInService.page(new Page<>(current, pageSize), wrapper);
    }


    @PostMapping("add")
    public boolean add(@RequestBody BudgetInVO budgetInVO) {
        double count = 1;
        List<BudgetIn> list = budgetInVO.getList();
        for (BudgetIn budgetIn : list) {
            if (ObjectUtil.isEmpty(budgetInVO.getSort())) {
                budgetIn.setSort(count++);
            } else {
                budgetIn.setSort(budgetInVO.getSort());
            }
            budgetIn.setBudgetId(budgetInVO.getBudgetId());
            budgetIn.setProjectId(budgetInVO.getProjectId());
            budgetIn.setName(budgetInVO.getName());
            budgetIn.setTaskCode(budgetInVO.getTaskCode());
            budgetIn.setType("一般项目");
            budgetIn.setInType(budgetInVO.getInType());
            budgetIn.setRemark(budgetInVO.getRemark());
            budgetIn.setHaveDisplay(budgetInVO.getHaveDisplay());
            budgetIn.setVersion(budgetInVO.getVersion());
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
