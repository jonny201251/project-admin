package com.haiying.project.service.impl;

import com.haiying.project.model.entity.Customer;
import com.haiying.project.mapper.CustomerMapper;
import com.haiying.project.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户信息 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

}
