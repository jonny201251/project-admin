package com.haiying.project.mapper;

import com.haiying.project.model.entity.ProjectPower;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 一般和重大项目立项时，授权信息 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2022-03-24
 */
@Mapper
public interface ProjectPowerMapper extends BaseMapper<ProjectPower> {

}
