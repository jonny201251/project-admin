package com.haiying.project.mapper;

import com.haiying.project.model.entity.BudgetProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 一般和重大项目预算-项目 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Mapper
public interface BudgetProjectMapper extends BaseMapper<BudgetProject> {

}
