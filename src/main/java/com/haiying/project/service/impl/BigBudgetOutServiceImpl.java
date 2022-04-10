package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.BigBudgetOutMapper;
import com.haiying.project.model.entity.BigBudgetOut;
import com.haiying.project.model.vo.BigBudgetOutVO;
import com.haiying.project.service.BigBudgetOutService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一般项目预算-预计支出 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Service
public class BigBudgetOutServiceImpl extends ServiceImpl<BigBudgetOutMapper, BigBudgetOut> implements BigBudgetOutService {
    @Override
    public boolean edit(BigBudgetOutVO bigBudgetOutVO) {
        this.remove(new LambdaQueryWrapper<BigBudgetOut>().eq(BigBudgetOut::getBudgetId, bigBudgetOutVO.getBudgetId()));
        double count = 1;
        List<BigBudgetOut> list = bigBudgetOutVO.getList();
        bigBudgetOutVO.setId(null);
        for (BigBudgetOut bigBudgetOut : list) {
            bigBudgetOut.setId(null);
            bigBudgetOut.setSort(count++);
            bigBudgetOut.setBudgetId(bigBudgetOutVO.getBudgetId());
            bigBudgetOut.setProjectId(bigBudgetOutVO.getProjectId());
            bigBudgetOut.setProjectName(bigBudgetOutVO.getProjectName());
            bigBudgetOut.setProjectTaskCode(bigBudgetOutVO.getProjectTaskCode());
            bigBudgetOut.setCostType(bigBudgetOutVO.getCostType());
            bigBudgetOut.setCostRate(bigBudgetOutVO.getCostRate());
            bigBudgetOut.setSort(bigBudgetOutVO.getSort());
            bigBudgetOut.setCompanyId(bigBudgetOutVO.getCompanyId());
            bigBudgetOut.setCompanyName(bigBudgetOutVO.getCompanyName());
            bigBudgetOut.setRemark(bigBudgetOutVO.getRemark());
        }
        return this.saveBatch(list);
    }
}
