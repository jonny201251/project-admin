package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
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
    public boolean edit(BudgetInVO vo) {
        this.remove(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, vo.getBudgetId()).eq(BudgetIn::getInType, vo.getInType()));
        double count = 1;
        List<BudgetIn> list = vo.getList();
        for (BudgetIn budgetIn : list) {
            budgetIn.setId(null);
            if (ObjectUtil.isEmpty(vo.getSort())) {
                budgetIn.setSort(count++);
            } else {
                budgetIn.setSort(vo.getSort());
            }
            budgetIn.setHaveDisplay(vo.getHaveDisplay());
            budgetIn.setVersion(vo.getVersion());
            budgetIn.setBudgetId(vo.getBudgetId());
            budgetIn.setProjectId(vo.getProjectId());
            budgetIn.setProjectType(vo.getProjectType());
            budgetIn.setName(vo.getName());
            budgetIn.setTaskCode(vo.getTaskCode());
            budgetIn.setInType(vo.getInType());
            budgetIn.setRemark(vo.getRemark());
            budgetIn.setLoginName(vo.getLoginName());
            budgetIn.setDisplayName(vo.getDisplayName());
            budgetIn.setDeptId(vo.getDeptId());
            budgetIn.setDeptName(vo.getDeptName());
            budgetIn.setCreateDatetime(vo.getCreateDatetime());
        }
        return this.saveBatch(list);
    }
}
