package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.BudgetProjectMapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.service.BudgetInService;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.BudgetProtectService;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一般和重大项目预算-项目 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Service
public class BudgetProjectServiceImpl extends ServiceImpl<BudgetProjectMapper, BudgetProject> implements BudgetProjectService {
    @Autowired
    BudgetProtectService budgetProtectService;
    @Autowired
    BudgetInService inService;
    @Autowired
    SmallBudgetOutService outService;

    @Override
    public boolean add(BudgetProject project, String type) {
        project.setHaveDisplay("是");
        project.setVersion(1);
        project.setType(type);
        this.save(project);
        List<BudgetProtect> list = project.getList();
        if (ObjectUtil.isNotEmpty(list)) {
            for (BudgetProtect protect : list) {
                protect.setBudgetId(project.getId());
                protect.setProjectId(project.getProjectId());
            }
            budgetProtectService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean edit(BudgetProject project) {
        this.updateById(project);
        //
        budgetProtectService.remove(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, project.getId()));
        List<BudgetProtect> list = project.getList();
        if (ObjectUtil.isNotEmpty(list)) {
            for (BudgetProtect protect : list) {
                protect.setBudgetId(project.getId());
                protect.setProjectId(project.getProjectId());
            }
            budgetProtectService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean modify(Integer id) {
        BudgetProject project = this.getById(id);
        List<BudgetProtect> protectList = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, id));
        List<BudgetIn> inList = inService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, id));
        List<SmallBudgetOut> outList = outService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, id));
        if (project.getBaseId() == null) {
            //第一次修改
            project.setHaveDisplay("否");
            this.updateById(project);
            if (ObjectUtil.isNotEmpty(inList)) {
                inList.forEach(item -> item.setHaveDisplay("否"));
                inService.updateBatchById(inList);
            }
            if (ObjectUtil.isNotEmpty(outList)) {
                outList.forEach(item -> item.setHaveDisplay("否"));
                outService.updateBatchById(outList);
            }
            //复制一份新的
            project.setId(null);
            project.setHaveDisplay("是");
            project.setVersion(project.getVersion() + 1);
            this.save(project);
            Integer newId = project.getId();
            //
            if (ObjectUtil.isNotEmpty(protectList)) {
                for (BudgetProtect tmp : protectList) {
                    tmp.setId(null);
                    tmp.setBudgetId(newId);
                }
                budgetProtectService.saveBatch(protectList);
            }
            //
            if (ObjectUtil.isNotEmpty(inList)) {
                for (BudgetIn tmp : inList) {
                    tmp.setId(null);
                    tmp.setBudgetId(newId);
                    tmp.setHaveDisplay("是");
                }
                inService.saveBatch(inList);
            }
            //
            if (ObjectUtil.isNotEmpty(outList)) {
                for (SmallBudgetOut tmp : outList) {
                    tmp.setId(null);
                    tmp.setBudgetId(newId);
                    tmp.setHaveDisplay("是");
                }
                outService.saveBatch(outList);
            }
        } else {
            //第二、三、N次修改
        }
        return true;
    }
}
