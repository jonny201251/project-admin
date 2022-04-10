package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.controller.base.BaseController;
import com.haiying.project.model.entity.Customer;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 客户信息 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@RestController
@RequestMapping("/customer")
@Wrapper
public class CustomerController extends BaseController<Customer> {
    @Autowired
    CustomerService customerService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<Customer> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object num = paramMap.get("num");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Customer::getName, name);
        }
        return customerService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody Customer customer) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        customer.setDisplayName(user.getDisplayName());
        customer.setLoginName(user.getLoginName());
        return customerService.save(customer);
    }

}
