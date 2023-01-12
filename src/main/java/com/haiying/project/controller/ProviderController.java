package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.controller.base.BaseController;
import com.haiying.project.model.entity.Provider;
import com.haiying.project.model.entity.ProviderQuery;
import com.haiying.project.model.entity.ProviderSimple;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProviderQueryService;
import com.haiying.project.service.ProviderService;
import com.haiying.project.service.ProviderSimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 供方信息 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-27
 */
@RestController
@RequestMapping("/provider")
@Wrapper
public class ProviderController extends BaseController<Provider> {
    @Autowired
    ProviderService providerService;
    @Autowired
    ProviderSimpleService providerSimpleService;
    @Autowired
    ProviderQueryService providerQueryService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<Provider> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object num = paramMap.get("num");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Provider::getName, name);
        }
        return providerService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("list2")
    public IPage<Provider> list2(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<Provider>().in(Provider::getUsee, Arrays.asList("一般项目立项时(三类)", "重大项目立项时(三类)"));
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object usee = paramMap.get("usee");
        Object code = paramMap.get("code");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Provider::getName, name);
        }
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper.like(Provider::getUsee, usee);
        }
        if (ObjectUtil.isNotEmpty(code)) {
            wrapper.like(Provider::getName, code);
        }
        return providerService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("list3")
    public List<Provider> list3(@RequestBody Map<String, Object> paramMap) {
        /*
        一般项目-三类-渠道方：供方简表+供方评分
        一般项目-三类-其他方：供方评分
        重大项目-三类-渠道方：供方简表+尽职调查+供方评分
        重大项目-三类-其他方：供方评分
         */
        List<Provider> list = new ArrayList<>();
        LambdaQueryWrapper<Provider> wrapper1 = new LambdaQueryWrapper<Provider>().in(Provider::getUsee, Arrays.asList("一般项目立项后(其他方)", "重大项目立项后(其他方)"));
        LambdaQueryWrapper<ProviderSimple> wrapper2 = new LambdaQueryWrapper<ProviderSimple>().eq(ProviderSimple::getUsee, "一般项目立项时(三类)");
        LambdaQueryWrapper<ProviderQuery> wrapper3 = new LambdaQueryWrapper<ProviderQuery>();

        Object usee = paramMap.get("usee");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper1.like(Provider::getName, name);
            wrapper2.like(ProviderSimple::getName, name);
            wrapper3.like(ProviderQuery::getName, name);
        }
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper1.like(Provider::getUsee, usee);
        }

        List<Provider> list1 = providerService.list(wrapper1);
        List<ProviderSimple> list2 = providerSimpleService.list(wrapper2);
        List<ProviderQuery> list3 = providerQueryService.list(wrapper3);


        List<Integer> idList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list1)) {
            list1.forEach(item -> idList.add(item.getId()));
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            list2.forEach(item -> idList.add(item.getProviderId()));
        }
        if (ObjectUtil.isNotEmpty(list3)) {
            list3.forEach(item -> idList.add(item.getProviderId()));
        }
        if (ObjectUtil.isNotEmpty(idList)) {
            LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<Provider>().in(Provider::getId, idList).orderByAsc(Provider::getId);
            if (ObjectUtil.isNotEmpty(name)) {
                wrapper.like(Provider::getName, name);
            }
            if (ObjectUtil.isNotEmpty(usee)) {
                wrapper.like(Provider::getUsee, usee);
            }
            List<Provider> list123 = providerService.list(wrapper);
            if (ObjectUtil.isNotEmpty(list123)) {
                list.addAll(list123);
            }
        }

        return list;
    }

    @PostMapping("add")
    public boolean add(@RequestBody Provider provider) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        provider.setDisplayName(user.getDisplayName());
        provider.setLoginName(user.getLoginName());
        provider.setDeptId(user.getDeptId());
        provider.setDeptName(user.getDeptName());
        return providerService.save(provider);
    }
}
