package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BigBudgetOut;
import com.haiying.project.model.vo.BigBudgetOutVO;
import com.haiying.project.service.BigBudgetOutService;
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
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/bigBudgetOut")
@Wrapper
public class BigBudgetOutController {
    @Autowired
    BigBudgetOutService bigBudgetOutService;

    @PostMapping("list")
    public IPage<BigBudgetOut> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        QueryWrapper<BigBudgetOut> wrapper = new QueryWrapper<BigBudgetOut>().select("distinct budget_id,project_id,project_name,project_task_code,cost_type,cost_rate,company_name");
        return bigBudgetOutService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody BigBudgetOutVO bigBudgetOutVO) {
        double count = 1;
        List<BigBudgetOut> list = bigBudgetOutVO.getList();
        for (BigBudgetOut bigBudgetOut : list) {
            if (ObjectUtil.isEmpty(bigBudgetOutVO.getSort())) {
                bigBudgetOut.setSort(count++);
            }else{
                bigBudgetOut.setSort(bigBudgetOutVO.getSort());
            }
            bigBudgetOut.setBudgetId(bigBudgetOutVO.getBudgetId());
            bigBudgetOut.setProjectId(bigBudgetOutVO.getProjectId());
            bigBudgetOut.setName(bigBudgetOutVO.getName());
            bigBudgetOut.setTaskCode(bigBudgetOutVO.getTaskCode());
            bigBudgetOut.setCostType(bigBudgetOutVO.getCostType());
            bigBudgetOut.setCostRate(bigBudgetOutVO.getCostRate());
            bigBudgetOut.setCompanyId(bigBudgetOutVO.getCompanyId());
            bigBudgetOut.setCompanyName(bigBudgetOutVO.getCompanyName());
            bigBudgetOut.setRemark(bigBudgetOutVO.getRemark());
        }
        return bigBudgetOutService.saveBatch(list);
    }

    @GetMapping("get")
    public BigBudgetOutVO get(Integer budgetId) {
        BigBudgetOutVO bigBudgetOutVO = new BigBudgetOutVO();
        List<BigBudgetOut> list = bigBudgetOutService.list(new LambdaQueryWrapper<BigBudgetOut>().eq(BigBudgetOut::getBudgetId, budgetId));
        BeanUtils.copyProperties(list.get(0), bigBudgetOutVO);
        bigBudgetOutVO.setList(list);
        return bigBudgetOutVO;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody BigBudgetOutVO bigBudgetOutVO) {
        return bigBudgetOutService.edit(bigBudgetOutVO);
    }
}
