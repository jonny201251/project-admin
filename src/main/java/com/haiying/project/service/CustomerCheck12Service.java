package com.haiying.project.service;

import com.haiying.project.model.entity.CustomerCheck12;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.CustomerCheck12After;

/**
 * <p>
 * 客户信用审批 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
public interface CustomerCheck12Service extends IService<CustomerCheck12> {

    boolean btnHandle(CustomerCheck12After after);
}
