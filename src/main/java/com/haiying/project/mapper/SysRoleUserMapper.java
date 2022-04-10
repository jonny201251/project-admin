package com.haiying.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haiying.project.model.entity.SysRoleUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色-用户 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2022-02-24
 */
@Mapper
public interface SysRoleUserMapper extends BaseMapper<SysRoleUser> {

}
