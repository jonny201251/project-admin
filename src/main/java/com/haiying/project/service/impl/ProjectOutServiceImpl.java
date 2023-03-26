package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.ProjectOutMapper;
import com.haiying.project.model.entity.ProjectInOutCount;
import com.haiying.project.model.entity.ProjectOut;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.service.ProjectInOutCountService;
import com.haiying.project.service.ProjectOutService;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 项目收支-支出明细 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@Service
public class ProjectOutServiceImpl extends ServiceImpl<ProjectOutMapper, ProjectOut> implements ProjectOutService {
    @Autowired
    ProjectInOutCountService projectInOutCountService;
    @Autowired
    SmallBudgetOutService smallBudgetOutService;

    @Override
    public boolean add(ProjectOut projectOut) {
        //支出不能超出预算
        if (projectOut.getHaveContract().equals("有")) {
            List<SmallBudgetOut> ll = smallBudgetOutService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getHaveDisplay, "是").eq(SmallBudgetOut::getTaskCode, projectOut.getTaskCode()).eq(SmallBudgetOut::getCostType, projectOut.getCostType()));
            double totalCost = 0.0;
            for (SmallBudgetOut smallBudgetOut : ll) {
                totalCost += ofNullable(smallBudgetOut.getMoney()).orElse(0.0);
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney1())) {
                if (projectOut.getMoney1() > totalCost) {
                    throw new PageTipException("开票金额:" + projectOut.getMoney1() + " ,超出预算额:" + totalCost);
                }
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney2())) {
                if (projectOut.getMoney2() > totalCost) {
                    throw new PageTipException("付款金额:" + projectOut.getMoney2() + " ,超出预算额:" + totalCost);
                }
            }
        }
        ProjectInOutCount count = projectInOutCountService.getById(1);
        projectOut.setSort(Double.valueOf(count.getCount()));
        count.setCount(count.getCount() + 1);

        this.save(projectOut);
        projectInOutCountService.updateById(count);
        return true;
    }
}
