package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.BudgetProjectMapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.service.*;
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
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    SmallProjectNoService smallProjectNoService;
    @Autowired
    BigProjectService bigProjectService;

    @Override
    public boolean add(BudgetProject obj) {
        //判断是否重复
        List<BudgetProject> ll = this.list(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getTaskCode, obj.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号   已存在");
        }
        //页面的毛利率>立项时的毛利率
        int page = Integer.parseInt(obj.getProjectRate().replaceAll("%", ""));
        int build;
        String tmp = "";
        if (obj.getProjectType().equals("一般项目")) {
            tmp = smallProjectService.getById(obj.getProjectId()).getProjectRate();
        } else if (obj.getProjectType().equals("重大项目")) {
            tmp = bigProjectService.getById(obj.getProjectId()).getProjectRate();
        } else if (obj.getProjectType().equals("一般项目非")) {
            tmp = smallProjectNoService.getById(obj.getProjectId()).getProjectRate();
        }
        build = Integer.parseInt(tmp.replaceAll("%", ""));
        if (page < build) {
            throw new PageTipException("预计毛利率低于立项时的毛利率");
        }

        obj.setHaveDisplay("是");
        obj.setVersion(0);
        obj.setProjectDisplayName(obj.getProjectLoginName());
        this.save(obj);
        List<BudgetProtect> list = obj.getList();
        list.forEach(item -> {
            item.setBudgetId(obj.getId());
            item.setProjectId(obj.getProjectId());
            item.setProjectType(obj.getProjectType());
        });
        budgetProtectService.saveBatch(list);
        return true;
    }

    @Override
    public boolean edit(BudgetProject obj) {
        //页面的毛利率>立项时的毛利率
        int page = Integer.parseInt(obj.getProjectRate().replaceAll("%", ""));
        int build;
        String tmp = "";
        if (obj.getProjectType().equals("一般项目")) {
            tmp = smallProjectService.getById(obj.getProjectId()).getProjectRate();
        } else if (obj.getProjectType().equals("重大项目")) {
            tmp = bigProjectService.getById(obj.getProjectId()).getProjectRate();
        } else if (obj.getProjectType().equals("一般项目非")) {
            tmp = smallProjectNoService.getById(obj.getProjectId()).getProjectRate();
        }
        build = Integer.parseInt(tmp.replaceAll("%", ""));
        if (page < build) {
            throw new PageTipException("预计毛利率低于立项时的毛利率");
        }

        this.updateById(obj);
        //
        budgetProtectService.remove(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, obj.getId()));
        List<BudgetProtect> list = obj.getList();
        list.forEach(item -> {
            item.setBudgetId(obj.getId());
            item.setProjectId(obj.getProjectId());
            item.setProjectType(obj.getProjectType());
        });
        budgetProtectService.saveBatch(list);
        return true;
    }

    @Override
    public boolean modify(Integer id) {
        Integer oldId, newId;
        BudgetProject project = this.getById(id);
        oldId = project.getId();
        List<BudgetProtect> protectList = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, id));
        List<BudgetIn> inList = inService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, id));
        List<SmallBudgetOut> outList = outService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, id));

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

        project.setBeforeId(oldId);
        if (project.getBaseId() == null) {
            //第一次修改
            project.setBaseId(oldId);
        }

        this.save(project);
        newId = project.getId();
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
                tmp.setVersion(project.getVersion());
            }
            inService.saveBatch(inList);
        }
        //
        if (ObjectUtil.isNotEmpty(outList)) {
            for (SmallBudgetOut tmp : outList) {
                tmp.setId(null);
                tmp.setBudgetId(newId);
                tmp.setHaveDisplay("是");
                tmp.setVersion(project.getVersion());
            }
            outService.saveBatch(outList);
        }
        return true;
    }
}
