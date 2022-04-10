package com.haiying.project.controller;


import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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

    @PostMapping("list")
    public IPage<SysUser> list(@RequestBody Map<String,Object> paramMap) {
        Integer current= (Integer) paramMap.get("current");
        Integer pageSize= (Integer) paramMap.get("pageSize");
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        return sysUserService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody SysUser sysUser) {
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

    @PostMapping("login")
    public SysUser login(@RequestBody SysUser pageUser) {
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

        return dbUser;
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
