package com.haiying.project.common.config;


import com.haiying.project.common.result.ResponseResultInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //配置ResponseResultInterceptor
        registry.addInterceptor(new ResponseResultInterceptor()).addPathPatterns("/**");
        //配置登录拦截器
        List<String> excludeList = new ArrayList<>();
        excludeList.add("/sysUser/login");
        excludeList.add("/login");
        excludeList.add("/back");
        excludeList.add("/*.js");
        excludeList.add("/*.css");
        excludeList.add("/static/*.svg");
        excludeList.add("/favicon.ico");
        //项目收支表
        excludeList.add("/projectInOut/getInOut1");
        excludeList.add("/projectInOut/getInOut2");
        //收支明细表
        excludeList.add("/projectInOut/getProjectDetail");
        excludeList.add("/projectInOut/getInDetail");
        excludeList.add("/projectInOut/getOutDetail");
        excludeList.add("/projectInOut/getIoDetail");
        //一般项目预算
        excludeList.add("/smallBudgetMoney/get");
        excludeList.add("/smallBudgetMoney/in");
        excludeList.add("/smallBudgetMoney/out");
        excludeList.add("/smallBudgetMoney/inModify");
        excludeList.add("/smallBudgetMoney/outModify");
        //
        excludeList.add("/*Report/*");
        excludeList.add("/jmreport/*");
//        excludeList.add("/otherPowerReport/get");
//        excludeList.add("/processInstNodeReport/get");


        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns(excludeList);
        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //
        registry.addResourceHandler("/upload/**").addResourceLocations("file:D:/appFile/projectFile/upload/");
        //
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}
