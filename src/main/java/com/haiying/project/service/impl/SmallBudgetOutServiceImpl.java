package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.SmallBudgetOutMapper;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.model.vo.SmallBudgetOutVO;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一般项目预算-预计支出 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-02
 */
@Service
public class SmallBudgetOutServiceImpl extends ServiceImpl<SmallBudgetOutMapper, SmallBudgetOut> implements SmallBudgetOutService {

    @Override
    public boolean edit(SmallBudgetOutVO smallBudgetOutVO) {
        this.remove(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, smallBudgetOutVO.getBudgetId()).eq(SmallBudgetOut::getCostType, smallBudgetOutVO.getCostType()));
        List<SmallBudgetOut> list = smallBudgetOutVO.getList();
        smallBudgetOutVO.setId(null);
        for (SmallBudgetOut smallBudgetOut : list) {
            smallBudgetOut.setId(null);
            smallBudgetOut.setSort(smallBudgetOutVO.getSort());
            smallBudgetOut.setBudgetId(smallBudgetOutVO.getBudgetId());
            smallBudgetOut.setProjectId(smallBudgetOutVO.getProjectId());
            smallBudgetOut.setName(smallBudgetOutVO.getName());
            smallBudgetOut.setTaskCode(smallBudgetOutVO.getTaskCode());
            smallBudgetOut.setSort(smallBudgetOutVO.getSort());
            smallBudgetOut.setRemark(smallBudgetOutVO.getRemark());
            smallBudgetOut.setCostType(smallBudgetOutVO.getCostType());
            smallBudgetOut.setCostRate(smallBudgetOutVO.getCostRate());
            smallBudgetOut.setHaveDisplay(smallBudgetOutVO.getHaveDisplay());
            smallBudgetOut.setVersion(smallBudgetOutVO.getVersion());
        }
        return this.saveBatch(list);
    }
}
