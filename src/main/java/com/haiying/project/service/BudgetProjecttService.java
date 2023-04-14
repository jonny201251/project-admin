package com.haiying.project.service;

import com.haiying.project.model.entity.BudgetProjectt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.BudgetProjecttAfter;

/**
 * <p>
 * 一般和重大项目预算-项目 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-04-13
 */
public interface BudgetProjecttService extends IService<BudgetProjectt> {

    boolean btnHandle(BudgetProjecttAfter after);
}
