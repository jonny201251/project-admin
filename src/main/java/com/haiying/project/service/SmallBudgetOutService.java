package com.haiying.project.service;

import com.haiying.project.model.entity.SmallBudgetOut;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.SmallBudgetOutVO;

/**
 * <p>
 * 一般项目预算-预计支出 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-02
 */
public interface SmallBudgetOutService extends IService<SmallBudgetOut> {

    boolean edit(SmallBudgetOutVO smallBudgetOutVO);
}
