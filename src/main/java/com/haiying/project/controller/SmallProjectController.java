package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.SmallProjectAfter;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallProjectService;
import com.haiying.project.service.SmallProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般项目立项 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
@RestController
@RequestMapping("/smallProject")
@Wrapper
public class SmallProjectController {
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<SmallProject> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<SmallProject> page;
        LambdaQueryWrapper<SmallProject> wrapper = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHaveDisplay, "是").orderByDesc(SmallProject::getId);

        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object projectStatus = paramMap.get("projectStatus");
        Object customerName = paramMap.get("customerName");
        Object providerName = paramMap.get("providerName");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(SmallProject::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(SmallProject::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(projectStatus)) {
            wrapper.like(SmallProject::getProjectStatus, projectStatus);
        }
        if (ObjectUtil.isNotEmpty(customerName)) {
            wrapper.like(SmallProject::getCustomerName, customerName);
        }
        if (ObjectUtil.isNotEmpty(providerName)) {
            wrapper.like(SmallProject::getProviderName, providerName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(SmallProject::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(SmallProject::getDeptName, deptName);
        }


        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(SmallProject::getDisplayName, user.getDisplayName());
        }
        page = smallProjectService.page(new Page<>(current, pageSize), wrapper);
        List<SmallProject> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(SmallProject::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<SmallProject> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<SmallProject> page;
        LambdaQueryWrapper<SmallProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SmallProject::getId, beforeIdList).orderByDesc(SmallProject::getId);
        page = smallProjectService.page(new Page<>(1, 100), wrapper);
        List<SmallProject> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public SmallProject get(Integer id) {
        SmallProject smallProject = smallProjectService.getById(id);
        List<SmallProtect> list = smallProtectService.list(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getProjectId, id));
        smallProject.setList(list);
        smallProject.setIdTypeListTmp(Arrays.asList(smallProject.getIdType().split(",")));

        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "SmallProject").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        smallProject.setFileList(fileList);
        return smallProject;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody SmallProjectAfter after) {
        return smallProjectService.btnHandle(after);
    }
}
