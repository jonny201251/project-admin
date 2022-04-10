package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.BigBudgetCompanyMapper;
import com.haiying.project.model.entity.BigBudgetCompany;
import com.haiying.project.model.vo.BigBudgetCompanyVO;
import com.haiying.project.service.BigBudgetCompanyService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 重大项目预算-费用类型下的公司 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Service
public class BigBudgetCompanyServiceImpl extends ServiceImpl<BigBudgetCompanyMapper, BigBudgetCompany> implements BigBudgetCompanyService {

    @Override
    public boolean edit(BigBudgetCompanyVO bigBudgetCompanyVO) {
        this.remove(new LambdaQueryWrapper<BigBudgetCompany>().eq(BigBudgetCompany::getBudgetId, bigBudgetCompanyVO.getBudgetId()));
        double count = 1;
        List<BigBudgetCompany> list = bigBudgetCompanyVO.getList();
        bigBudgetCompanyVO.setId(null);
        for (BigBudgetCompany bigBudgetCompany : list) {
            bigBudgetCompany.setId(null);
            bigBudgetCompany.setSort(count++);
            bigBudgetCompany.setBudgetId(bigBudgetCompanyVO.getBudgetId());
            bigBudgetCompany.setProjectId(bigBudgetCompanyVO.getProjectId());
            bigBudgetCompany.setProjectName(bigBudgetCompanyVO.getProjectName());
            bigBudgetCompany.setProjectTaskCode(bigBudgetCompanyVO.getProjectTaskCode());
            bigBudgetCompany.setCostType(bigBudgetCompanyVO.getCostType());
            bigBudgetCompany.setCostRate(bigBudgetCompanyVO.getCostRate());
            bigBudgetCompany.setSort(bigBudgetCompanyVO.getSort());
            bigBudgetCompany.setRemark(bigBudgetCompanyVO.getRemark());
        }
        return this.saveBatch(list);
    }
}
