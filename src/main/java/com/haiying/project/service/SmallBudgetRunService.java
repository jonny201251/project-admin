package com.haiying.project.service;

import com.haiying.project.model.entity.SmallBudgetRun;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.SmallBudgetRunAfter;

/**
 * <p>
 * 项目预算的流程 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
public interface SmallBudgetRunService extends IService<SmallBudgetRun> {

    boolean btnHandle(SmallBudgetRunAfter after);
    boolean change(SmallBudgetRun current,Integer newId);
}
