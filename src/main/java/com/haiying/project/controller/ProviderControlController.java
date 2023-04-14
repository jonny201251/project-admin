package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.ProviderControlAfter;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProviderControlService;
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
@RequestMapping("/providerControl")
@Wrapper
public class ProviderControlController {
    @Autowired
    ProviderControlService providerControlService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<ProviderControl> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProviderControl> page;
        LambdaQueryWrapper<ProviderControl> wrapper = new LambdaQueryWrapper<ProviderControl>().eq(ProviderControl::getHaveDisplay, "是").orderByDesc(ProviderControl::getId);
        //数据权限
        if (!user.getDisplayName().equals("孙欢")) {
            wrapper.eq(ProviderControl::getDeptId, user.getDeptId());
        }
        page = providerControlService.page(new Page<>(current, pageSize), wrapper);
        List<ProviderControl> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProviderControl::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("list2")
    public IPage<ProviderControl> list2(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProviderControl> page;
        LambdaQueryWrapper<ProviderControl> wrapper = new LambdaQueryWrapper<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
        page = providerControlService.page(new Page<>(current, pageSize), wrapper);
        List<ProviderControl> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProviderControl::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<ProviderControl> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<ProviderControl> page;
        LambdaQueryWrapper<ProviderControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProviderControl::getId, beforeIdList).orderByDesc(ProviderControl::getId);
        page = providerControlService.page(new Page<>(1, 100), wrapper);
        List<ProviderControl> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public ProviderControl get(Integer id) {
        ProviderControl providerControl = providerControlService.getById(id);
        providerControl.setUserNameeList(Arrays.asList(providerControl.getUserNamee().split(",")));
        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderControl").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        providerControl.setFileList(fileList);
        return providerControl;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody ProviderControlAfter after) {
        return providerControlService.btnHandle(after);
    }
}
