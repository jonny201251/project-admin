package com.haiying.project.mapper;

import com.haiying.project.model.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 客户信息 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}
