package com.haiying.project.service;

import com.haiying.project.model.entity.BudgetIn;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.BudgetInVO;

/**
 * <p>
 * 一般和重大项目预算-预计收入 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
public interface BudgetInService extends IService<BudgetIn> {

    boolean edit(BudgetInVO budgetInVO);
}
