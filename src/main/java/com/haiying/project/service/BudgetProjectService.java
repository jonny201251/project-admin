package com.haiying.project.service;

import com.haiying.project.model.entity.BudgetProject;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 一般和重大项目预算-项目 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
public interface BudgetProjectService extends IService<BudgetProject> {

    boolean add(BudgetProject budgetProject);

    boolean edit(BudgetProject budgetProject);

    boolean modify(Integer id);
}
