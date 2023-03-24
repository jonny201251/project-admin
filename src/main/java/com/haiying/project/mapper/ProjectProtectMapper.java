package com.haiying.project.mapper;

import com.haiying.project.model.entity.ProjectProtect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 一般和重大项目的保证金登记表 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2023-03-22
 */
@Mapper
public interface ProjectProtectMapper extends BaseMapper<ProjectProtect> {

}
