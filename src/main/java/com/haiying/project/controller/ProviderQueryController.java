package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.ProviderQueryAfter;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProviderQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 供方尽职调查 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@RestController
@RequestMapping("/providerQuery")
@Wrapper
public class ProviderQueryController {
    @Autowired
    ProviderQueryService providerQueryService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<ProviderQuery> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProviderQuery> page;
        LambdaQueryWrapper<ProviderQuery> wrapper = new LambdaQueryWrapper<ProviderQuery>().eq(ProviderQuery::getHaveDisplay, "是").orderByDesc(ProviderQuery::getId);

        Object usee = paramMap.get("usee");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper.like(ProviderQuery::getUsee, usee);
        }
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProviderQuery::getType, type);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(ProviderQuery::getName, name);
        }

        if (!user.getDisplayName().equals("孙欢")) {
            wrapper.eq(ProviderQuery::getDeptId, user.getDeptId());
        }

        page = providerQueryService.page(new Page<>(current, pageSize), wrapper);
        List<ProviderQuery> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProviderQuery::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("list2")
    public IPage<ProviderQuery> list2(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProviderQuery> page;
        LambdaQueryWrapper<ProviderQuery> wrapper = new LambdaQueryWrapper<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
        page = providerQueryService.page(new Page<>(current, pageSize), wrapper);
        List<ProviderQuery> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProviderQuery::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<ProviderQuery> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<ProviderQuery> page;
        LambdaQueryWrapper<ProviderQuery> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProviderQuery::getId, beforeIdList).orderByDesc(ProviderQuery::getId);
        page = providerQueryService.page(new Page<>(1, 100), wrapper);
        List<ProviderQuery> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public ProviderQuery get(Integer id) {
        ProviderQuery providerQuery = providerQueryService.getById(id);
        providerQuery.setUserNameeList(Arrays.asList(providerQuery.getUserNamee().split(",")));
        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderQuery").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        providerQuery.setFileList(fileList);
        return providerQuery;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody ProviderQueryAfter after) {
        return providerQueryService.btnHandle(after);
    }
}
