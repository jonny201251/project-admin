package com.haiying.project.service;

import com.haiying.project.model.entity.CustomerScore1;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.CustomerScore1After;

/**
 * <p>
 * 客户信用评分1 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
public interface CustomerScore1Service extends IService<CustomerScore1> {

    boolean btnHandle(CustomerScore1After after);
}
