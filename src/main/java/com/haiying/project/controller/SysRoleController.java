package com.haiying.project.controller;


import com.haiying.project.common.result.Wrapper;
import com.haiying.project.controller.base.BaseController;
import com.haiying.project.model.entity.SysRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-15
 */
@RestController
@RequestMapping("/sysRole")
@Wrapper
public class SysRoleController extends BaseController<SysRole> {
    @GetMapping("all")
    public List<SysRole> getAll() {
        return service.list();
    }
}
