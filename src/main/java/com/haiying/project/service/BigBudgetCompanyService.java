package com.haiying.project.service;

import com.haiying.project.model.entity.BigBudgetCompany;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.BigBudgetCompanyVO;

/**
 * <p>
 * 重大项目预算-费用类型下的公司 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
public interface BigBudgetCompanyService extends IService<BigBudgetCompany> {

    boolean edit(BigBudgetCompanyVO bigBudgetCompanyVO);
}
