package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProviderSimple2Service;
import com.haiying.project.service.ProviderSimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 供方情况简表 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@RestController
@RequestMapping("/providerSimple")
@Wrapper
public class ProviderSimpleController {
    @Autowired
    ProviderSimpleService providerSimpleService;
    @Autowired
    ProviderSimple2Service providerSimple2Service;
    @Autowired
    HttpSession httpSession;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<ProviderSimple> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProviderSimple> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object usee = paramMap.get("usee");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper.like(ProviderSimple::getUsee, usee);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(ProviderSimple::getName, name);
        }
        //数据权限
        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getDisplayName().equals("孙欢")) {
            wrapper.eq(ProviderSimple::getDisplayName, user.getDisplayName());
        }
        return providerSimpleService.page(new Page<>(current, pageSize), wrapper);
    }

    //供方尽职调查 弹窗
    @PostMapping("list2")
    public IPage<ProviderSimple> list2(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        LambdaQueryWrapper<ProviderSimple> wrapper = new LambdaQueryWrapper<ProviderSimple>().eq(ProviderSimple::getDeptId, user.getDeptId()).eq(ProviderSimple::getUsee, "重大项目立项时(三类)");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object usee = paramMap.get("usee");
        Object name = paramMap.get("name");
        Object code = paramMap.get("code");
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper.like(ProviderSimple::getUsee, usee);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(ProviderSimple::getName, name);
        }
        if (ObjectUtil.isNotEmpty(code)) {
            wrapper.like(ProviderSimple::getCode, code);
        }
        return providerSimpleService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProviderSimple providerSimple) {
        //判断是否重复添加
        List<ProviderSimple> list = providerSimpleService.list(new LambdaQueryWrapper<ProviderSimple>().eq(ProviderSimple::getProviderId, providerSimple.getProviderId()));
        if (ObjectUtil.isNotEmpty(list)) {
            throw new PageTipException("供方用途和供方名称   已存在");
        }

        return providerSimpleService.add(providerSimple);
    }

    @GetMapping("get")
    public ProviderSimple get(Integer id) {
        ProviderSimple providerSimple = providerSimpleService.getById(id);
        List<ProviderSimple2> list = providerSimple2Service.list(new LambdaQueryWrapper<ProviderSimple2>().eq(ProviderSimple2::getProviderSimpleId, id));
        providerSimple.setList(list);
        //
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderSimple").eq(FormFile::getBusinessId, id));
        if (ObjectUtil.isNotEmpty(formFileList)) {
            List<FileVO> fileList = new ArrayList<>();
            for (FormFile formFile : formFileList) {
                FileVO fileVO = new FileVO();
                fileVO.setName(formFile.getName());
                fileVO.setUrl(formFile.getUrl());
                fileVO.setStatus("done");
                fileList.add(fileVO);
            }
            providerSimple.setFileList(fileList);
        }
        return providerSimple;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProviderSimple providerSimple) {
        return providerSimpleService.edit(providerSimple);
    }
}
