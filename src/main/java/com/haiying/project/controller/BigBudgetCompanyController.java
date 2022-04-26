package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BigBudgetCompany;
import com.haiying.project.model.vo.BigBudgetCompanyVO;
import com.haiying.project.service.BigBudgetCompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 重大项目预算-费用类型下的公司 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/bigBudgetCompany")
@Wrapper
public class BigBudgetCompanyController {
    @Autowired
    BigBudgetCompanyService bigBudgetCompanyService;

    @PostMapping("list")
    public IPage<BigBudgetCompany> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        QueryWrapper<BigBudgetCompany> wrapper = new QueryWrapper<BigBudgetCompany>().select("distinct budget_id,project_id,project_name,project_task_code,cost_type,cost_rate");
        return bigBudgetCompanyService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody BigBudgetCompanyVO bigBudgetCompanyVO) {
        double count = 1;
        List<BigBudgetCompany> list = bigBudgetCompanyVO.getList();
        for (BigBudgetCompany bigBudgetCompany : list) {
            if (ObjectUtil.isEmpty(bigBudgetCompanyVO.getSort())) {
                bigBudgetCompany.setSort(count++);
            }else{
                bigBudgetCompany.setSort(bigBudgetCompanyVO.getSort());
            }
            bigBudgetCompany.setBudgetId(bigBudgetCompanyVO.getBudgetId());
            bigBudgetCompany.setProjectId(bigBudgetCompanyVO.getProjectId());
            bigBudgetCompany.setName(bigBudgetCompanyVO.getName());
            bigBudgetCompany.setTaskCode(bigBudgetCompanyVO.getTaskCode());
            bigBudgetCompany.setCostType(bigBudgetCompanyVO.getCostType());
            bigBudgetCompany.setCostRate(bigBudgetCompanyVO.getCostRate());
            bigBudgetCompany.setRemark(bigBudgetCompanyVO.getRemark());
        }
        return bigBudgetCompanyService.saveBatch(list);
    }

    @GetMapping("get")
    public BigBudgetCompanyVO get(Integer budgetId) {
        BigBudgetCompanyVO bigBudgetCompanyVO = new BigBudgetCompanyVO();
        List<BigBudgetCompany> list = bigBudgetCompanyService.list(new LambdaQueryWrapper<BigBudgetCompany>().eq(BigBudgetCompany::getBudgetId, budgetId));
        BeanUtils.copyProperties(list.get(0), bigBudgetCompanyVO);
        bigBudgetCompanyVO.setList(list);
        return bigBudgetCompanyVO;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody BigBudgetCompanyVO bigBudgetCompanyVO) {
        return bigBudgetCompanyService.edit(bigBudgetCompanyVO);
    }

    @PostMapping("dialogList")
    public IPage<BigBudgetCompany> dialogList(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        QueryWrapper<BigBudgetCompany> wrapper = new QueryWrapper<BigBudgetCompany>().select("distinct id,budget_id,project_id,project_name,project_task_code,cost_type,cost_rate,company_name");
        return bigBudgetCompanyService.page(new Page<>(current, pageSize), wrapper);
    }

}
