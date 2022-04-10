package com.haiying.project.service;

import com.haiying.project.model.entity.BigBudgetOut;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.BigBudgetOutVO;

/**
 * <p>
 * 一般项目预算-预计支出 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
public interface BigBudgetOutService extends IService<BigBudgetOut> {

    boolean edit(BigBudgetOutVO bigBudgetOutVO);
}
