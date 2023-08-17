package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.ProviderCancel;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.ProviderCancelAfter;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProviderCancelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
@RequestMapping("/providerCancel")
@Wrapper
public class ProviderCancelController {
    @Autowired
    ProviderCancelService providerCancelService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<ProviderCancel> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProviderCancel> page;
        LambdaQueryWrapper<ProviderCancel> wrapper = new LambdaQueryWrapper<ProviderCancel>().eq(ProviderCancel::getHaveDisplay, "是").orderByDesc(ProviderCancel::getId);

        Object usee = paramMap.get("usee");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(usee)) {
            wrapper.like(ProviderCancel::getUsee, usee);
        }
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProviderCancel::getType, type);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(ProviderCancel::getName, name);
        }

        if (!user.getDisplayName().equals("孙欢")) {
            wrapper.eq(ProviderCancel::getDeptId, user.getDeptId());
        }

        page = providerCancelService.page(new Page<>(current, pageSize), wrapper);
        List<ProviderCancel> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProviderCancel::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }


    @GetMapping("get")
    public ProviderCancel get(Integer id) {
        ProviderCancel providerCancel = providerCancelService.getById(id);
        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderCancel").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        providerCancel.setFileList(fileList);
        return providerCancel;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody ProviderCancelAfter after) {
        return providerCancelService.btnHandle(after);
    }
}
