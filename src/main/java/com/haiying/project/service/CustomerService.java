package com.haiying.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.entity.Customer;

/**
 * <p>
 * 客户信息 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
public interface CustomerService extends IService<Customer> {
    boolean add(Customer customer);

    boolean edit(Customer customer);
}
