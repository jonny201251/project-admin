package com.haiying.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.model.vo.SmallBudgetOutVO;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一般项目预算-预计支出 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/smallBudgetOut")
@Wrapper
public class SmallBudgetOutController {
    @Autowired
    SmallBudgetOutService smallBudgetOutService;

    @PostMapping("list")
    public IPage<SmallBudgetOut> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        QueryWrapper<SmallBudgetOut> wrapper = new QueryWrapper<SmallBudgetOut>().eq("have_display", "是").select("distinct budget_id,project_id,name,task_code,cost_type,cost_rate,have_display,version").orderByAsc("budget_id,sort");
        return smallBudgetOutService.page(new Page<>(current, pageSize), wrapper);
    }


    @PostMapping("add")
    public boolean add(@RequestBody SmallBudgetOutVO smallBudgetOutVO) {
        double count = 1;
        List<SmallBudgetOut> list = smallBudgetOutVO.getList();
        for (SmallBudgetOut smallBudgetOut : list) {
            smallBudgetOut.setSort(smallBudgetOutVO.getSort());
            smallBudgetOut.setBudgetId(smallBudgetOutVO.getBudgetId());
            smallBudgetOut.setProjectId(smallBudgetOutVO.getProjectId());
            smallBudgetOut.setName(smallBudgetOutVO.getName());
            smallBudgetOut.setTaskCode(smallBudgetOutVO.getTaskCode());
            smallBudgetOut.setRemark(smallBudgetOutVO.getRemark());
            smallBudgetOut.setCostType(smallBudgetOutVO.getCostType());
            smallBudgetOut.setCostRate(smallBudgetOutVO.getCostRate());
            smallBudgetOut.setHaveDisplay(smallBudgetOutVO.getHaveDisplay());
            smallBudgetOut.setVersion(smallBudgetOutVO.getVersion());
        }
        return smallBudgetOutService.saveBatch(list);
    }

    @GetMapping("get")
    public SmallBudgetOutVO get(Integer budgetId, String costType) {
        SmallBudgetOutVO smallBudgetOutVO = new SmallBudgetOutVO();
        List<SmallBudgetOut> list = smallBudgetOutService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, budgetId).eq(SmallBudgetOut::getCostType, costType));
        BeanUtils.copyProperties(list.get(0), smallBudgetOutVO);
        smallBudgetOutVO.setList(list);
        return smallBudgetOutVO;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody SmallBudgetOutVO smallBudgetOutVO) {
        return smallBudgetOutService.edit(smallBudgetOutVO);
    }
}
