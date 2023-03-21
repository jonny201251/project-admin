package com.haiying.project.controller;


import com.haiying.project.common.result.Wrapper;
import com.haiying.project.controller.base.BaseTreeController;
import com.haiying.project.model.entity.SysPermission;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-01-12
 */
@RestController
@RequestMapping("/sysPermission")
@Wrapper
public class SysPermissionController extends BaseTreeController<SysPermission>{

}
