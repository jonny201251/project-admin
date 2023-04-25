package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.common.utils.TreeUtil;
import com.haiying.project.model.entity.LoginLog;
import com.haiying.project.model.entity.SysPermission;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.UserVO;
import com.haiying.project.service.LoginLogService;
import com.haiying.project.service.SysPermissionService;
import com.haiying.project.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-15
 */
@RestController
@RequestMapping("/sysUser")
@Wrapper
public class SysUserController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    SysPermissionService sysPermissionService;
    @Autowired
    LoginLogService loginLogService;
    @Autowired
    HttpServletRequest request;


    @PostMapping("list")
    public IPage<SysUser> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        Object deptName = paramMap.get("deptName");
        Object displayName = paramMap.get("displayName");
        Object loginName = paramMap.get("loginName");
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(SysUser::getDeptName, deptName);
        }
        if (ObjectUtil.isNotEmpty(loginName)) {
            wrapper.like(SysUser::getLoginName, loginName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(SysUser::getDisplayName, displayName);
        }
        return sysUserService.page(new Page<>(current, pageSize), wrapper);
    }


    @PostMapping("add")
    public boolean add(@RequestBody SysUser sysUser) {
        SysUser user = sysUserService.getById(sysUser.getLoginName());
        if (user != null) {
            throw new PageTipException(user.getLoginName() + "--已经存在");
        }
        sysUser.setPassword(SecureUtil.md5("1"));
        return sysUserService.save(sysUser);
    }

    @GetMapping("get")
    public SysUser getById(String id) {
        return sysUserService.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody SysUser sysUser) {
        return sysUserService.updateById(sysUser);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return sysUserService.removeByIds(idList);
    }

    private String getClientIp() {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    @PostMapping("login")
    public UserVO login(@RequestBody SysUser pageUser) {
        LoginLog log = new LoginLog();
        log.setLoginName(pageUser.getLoginName());
        log.setIp(getClientIp());
        log.setCreateDatetime(LocalDateTime.now());
        loginLogService.save(log);

        SysUser dbUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getLoginName, pageUser.getLoginName()));
        if (dbUser == null) {
            throw new PageTipException("用户名不存在");
        }
        //校验 登录密码
        String dbPassword = dbUser.getPassword();
        String pagePassword = SecureUtil.md5(pageUser.getPassword());
        if (!dbPassword.equals(pagePassword)) {
            throw new PageTipException("密码错误");
        }
        //用户放入session
        httpSession.removeAttribute("user");
        httpSession.setAttribute("user", dbUser);

        UserVO userVO = new UserVO();
        userVO.setUser(dbUser);

        List<SysPermission> menuList;
        if (dbUser.getDisplayName().equals("张强")) {
            menuList = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>().ne(SysPermission::getId, 45).orderByAsc(SysPermission::getSort));
        } else {
            menuList = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>().notIn(SysPermission::getId, 45).gt(SysPermission::getId, 34).orderByAsc(SysPermission::getSort));
        }
        userVO.setMenuList(TreeUtil.getTree(menuList));

        return userVO;
    }

    @GetMapping("all")
    public List<SysUser> getAll() {
        return sysUserService.list();
    }

    //用户自己，修改密码
    @GetMapping("changePassword")
    public boolean changePassword(String password1) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (user == null) {
            throw new PageTipException("用户未登录");
        }
        user.setPassword(SecureUtil.md5(password1));
        return sysUserService.updateById(user);
    }

    //管理人员初始化密码
    @GetMapping("initPassword")
    public boolean adminChangePassword(Integer id) {
        SysUser user = sysUserService.getById(id);
        user.setPassword(SecureUtil.md5("1"));
        return sysUserService.updateById(user);
    }

    @GetMapping("logout")
    public boolean logout() {
        httpSession.removeAttribute("user");
        return true;
    }
}
