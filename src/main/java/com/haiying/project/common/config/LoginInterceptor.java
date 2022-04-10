package com.haiying.project.common.config;
//https://blog.csdn.net/leeta521/article/details/119532691

import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.model.entity.SysUser;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        SysUser user = (SysUser) request.getSession().getAttribute("user");
        if (user == null) {
            throw new PageTipException("用户未登录");
        }
        return true;
    }
}
