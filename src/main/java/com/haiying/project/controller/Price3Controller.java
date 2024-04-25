package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.Price3After;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.Price33Service;
import com.haiying.project.service.Price3Service;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购方式-比价单 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
@RestController
@RequestMapping("/price3")
@Wrapper
public class Price3Controller {
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    Price3Service price3Service;
    @Autowired
    Price33Service price33Service;

    @PostMapping("list")
    public IPage<Price3> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<Price3> page;
        LambdaQueryWrapper<Price3> wrapper = new LambdaQueryWrapper<Price3>().orderByDesc(Price3::getId);

        Object projectName = paramMap.get("projectName");
        Object projectLevel = paramMap.get("projectLevel");
        Object taskCode = paramMap.get("taskCode");
        Object inContractName = paramMap.get("inContractName");
        Object inContractCode = paramMap.get("inContractCode");
        if (ObjectUtil.isNotEmpty(projectName)) {
            wrapper.like(Price3::getProjectName, projectName);
        }
        if (ObjectUtil.isNotEmpty(projectLevel)) {
            wrapper.like(Price3::getProjectLevel, projectLevel);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(Price3::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(inContractName)) {
            wrapper.like(Price3::getInContractName, inContractName);
        }
        if (ObjectUtil.isNotEmpty(inContractCode)) {
            wrapper.like(Price3::getInContractCode, inContractCode);
        }

        if (!(user.getDeptName().equals("综合计划部") || user.getDeptName().equals("财务部"))) {
            wrapper.eq(Price3::getDeptId, user.getDeptId());
        }

        page = price3Service.page(new Page<>(current, pageSize), wrapper);
        List<Price3> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(Price3::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public Price3 get(Integer id) {
        Price3 price3 = price3Service.getById(id);
        List<Price33> list = price33Service.list(new LambdaQueryWrapper<Price33>().eq(Price33::getPrice3Id, id));
        price3.setList(list);

        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price3").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        price3.setFileList(fileList);
        return price3;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody Price3After after) {
        return price3Service.btnHandle(after);
    }
}
