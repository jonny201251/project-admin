package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.BudgetInMapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.model.vo.BudgetInVO;
import com.haiying.project.service.BudgetInService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一般和重大项目预算-预计收入 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Service
public class BudgetInServiceImpl extends ServiceImpl<BudgetInMapper, BudgetIn> implements BudgetInService {

    @Override
    public boolean edit(BudgetInVO budgetInVO) {
        this.remove(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, budgetInVO.getBudgetId()));
        double count = 1;
        List<BudgetIn> list = budgetInVO.getList();
        budgetInVO.setId(null);
        for (BudgetIn budgetIn : list) {
            budgetIn.setId(null);
            budgetIn.setSort(count++);
            budgetIn.setBudgetId(budgetInVO.getBudgetId());
            budgetIn.setProjectId(budgetInVO.getProjectId());
            budgetIn.setName(budgetInVO.getName());
            budgetIn.setTaskCode(budgetInVO.getTaskCode());
            budgetIn.setInType(budgetInVO.getInType());
            budgetIn.setSort(budgetInVO.getSort());
            budgetIn.setRemark(budgetInVO.getRemark());
        }
        return this.saveBatch(list);
    }
}
