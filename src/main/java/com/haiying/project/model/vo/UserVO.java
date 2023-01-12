package com.haiying.project.model.vo;

import com.haiying.project.model.entity.SysPermission;
import com.haiying.project.model.entity.SysUser;
import lombok.Data;

import java.util.List;

@Data
public class UserVO {
    //用户
    private SysUser user;
    //导航菜单
    private List<SysPermission> menuList;
}
