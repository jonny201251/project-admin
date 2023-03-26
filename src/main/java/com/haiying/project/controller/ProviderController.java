package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class ProviderController {
    @Autowired
    ProviderService providerService;
    @Autowired
    ProviderSimpleService providerSimpleService;
    @Autowired
    ProviderQueryService providerQueryService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    FormFileService formFileService;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<Provider> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object usee = paramMap.get("usee");
        Object name = paramMap.get("name");
        Object code = paramMap.get("code");
        Object result = paramMap.get("result");
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper.like(Provider::getUsee, usee);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Provider::getName, name);
        }
        if (ObjectUtil.isNotEmpty(code)) {
            wrapper.like(Provider::getCode, code);
        }
        if (ObjectUtil.isNotEmpty(result)) {
            wrapper.like(Provider::getResult, result);
        }

        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getDisplayName().equals("孙欢")) {
            wrapper.eq(Provider::getDisplayName, user.getDisplayName());
        }
        return providerService.page(new Page<>(current, pageSize), wrapper);
    }

    //项目备案
    @PostMapping("listCode")
    public IPage<Provider> listCode(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<Provider>().eq(Provider::getResult, "合格");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Provider::getName, name);
        }

        return providerService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("listSmallProject")
    public IPage<Provider> listSmallProject(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<Provider>().eq(Provider::getUsee, "一般项目立项时(三类)").eq(Provider::getResult, "合格");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Provider::getName, name);
        }

        return providerService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("listBigProject")
    public IPage<Provider> listBigProject(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<Provider>().eq(Provider::getUsee, "重大项目立项时(三类)").eq(Provider::getResult, "合格");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Provider::getName, name);
        }

        return providerService.page(new Page<>(current, pageSize), wrapper);
    }

    //供方情况简表 弹窗
    @PostMapping("list2")
    public IPage<Provider> list2(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<Provider>().eq(Provider::getDeptId, user.getDeptId()).in(Provider::getUsee, Arrays.asList("一般项目立项时(三类)", "重大项目立项时(三类)")).in(Provider::getResult, Arrays.asList("", "不合格"));
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

    //供方评分 弹窗
    @PostMapping("list3")
    public List<Provider> list3(@RequestBody Map<String, Object> paramMap) {
        /*
        一般项目-三类：供方简表+供方评分
        一般项目-其他方：供方评分
        重大项目-三类：供方简表+尽职调查+供方评分
        重大项目-其他方：供方评分
         */
        List<Provider> list = new ArrayList<>();
        LambdaQueryWrapper<Provider> wrapper1 = new LambdaQueryWrapper<Provider>().in(Provider::getUsee, Arrays.asList("一般项目立项后(其他方)", "重大项目立项后(其他方)")).in(Provider::getResult, Arrays.asList("", "不合格"));
        LambdaQueryWrapper<ProviderSimple> wrapper2 = new LambdaQueryWrapper<ProviderSimple>().eq(ProviderSimple::getUsee, "一般项目立项时(三类)").in(ProviderSimple::getResult, Arrays.asList("", "不合格"));
        LambdaQueryWrapper<ProviderQuery> wrapper3 = new LambdaQueryWrapper<ProviderQuery>().in(ProviderQuery::getResult, Arrays.asList("", "不合格"));
        //
        List<ProcessInst> ll = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, "providerQueryPath").eq(ProcessInst::getProcessStatus, "完成").eq(ProcessInst::getBusinessHaveDisplay, "是"));
        if (ObjectUtil.isNotEmpty(ll)) {
            List<Integer> lll = ll.stream().map(ProcessInst::getBusinessId).collect(Collectors.toList());
            wrapper3.in(ProviderQuery::getId, lll);
        } else {
            wrapper3.eq(ProviderQuery::getId, 0);
        }

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

    //供方动态监控
    @PostMapping("list4")
    public List<Provider> list4(@RequestBody Map<String, Object> paramMap) {
        List<Provider> list = new ArrayList<>();
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<Provider>().in(Provider::getHaveDisplay, "", "是").eq(Provider::getResult, "合格");

        Object usee = paramMap.get("usee");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Provider::getName, name);
        }
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper.like(Provider::getUsee, usee);
        }

        list = providerService.list(wrapper);
        return list;
    }

    @PostMapping("add")
    public boolean add(@RequestBody Provider provider) {
        //判断是否重复添加
        List<Provider> list = providerService.list(new LambdaQueryWrapper<Provider>().eq(Provider::getUsee, provider.getUsee().trim()).eq(Provider::getName, provider.getName().trim()));
        if (ObjectUtil.isNotEmpty(list)) {
            throw new PageTipException("供方用途和供方名称   已存在");
        }

        SysUser user = (SysUser) httpSession.getAttribute("user");
        provider.setDisplayName(user.getDisplayName());
        provider.setLoginName(user.getLoginName());
        provider.setDeptId(user.getDeptId());
        provider.setDeptName(user.getDeptName());
        provider.setCreateDatetime(LocalDateTime.now());

        return providerService.add(provider);
    }

    @GetMapping("get")
    public Provider get(Integer id) {
        Provider provider = providerService.getById(id);
        //
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Provider").eq(FormFile::getBusinessId, id));
        if (ObjectUtil.isNotEmpty(formFileList)) {
            List<FileVO> fileList = new ArrayList<>();
            for (FormFile formFile : formFileList) {
                FileVO fileVO = new FileVO();
                fileVO.setName(formFile.getName());
                fileVO.setUrl(formFile.getUrl());
                fileVO.setStatus("done");
                fileList.add(fileVO);
            }
            provider.setFileList(fileList);
        }
        return provider;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody Provider provider) {
        return providerService.edit(provider);
    }
}
