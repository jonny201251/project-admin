package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.Price2After;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.Price2Service;
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
@RequestMapping("/price2")
@Wrapper
public class Price2Controller {
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    Price2Service price2Service;

    @PostMapping("list")
    public IPage<Price2> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<Price2> page;
        LambdaQueryWrapper<Price2> wrapper = new LambdaQueryWrapper<Price2>().orderByDesc(Price2::getId);

        Object projectName = paramMap.get("projectName");
        Object projectLevel = paramMap.get("projectLevel");
        Object taskCode = paramMap.get("taskCode");
        Object inContractName = paramMap.get("inContractName");
        Object inContractCode = paramMap.get("inContractCode");
        if (ObjectUtil.isNotEmpty(projectName)) {
            wrapper.like(Price2::getProjectName, projectName);
        }
        if (ObjectUtil.isNotEmpty(projectLevel)) {
            wrapper.like(Price2::getProjectLevel, projectLevel);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(Price2::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(inContractName)) {
            wrapper.like(Price2::getInContractName, inContractName);
        }
        if (ObjectUtil.isNotEmpty(inContractCode)) {
            wrapper.like(Price2::getInContractCode, inContractCode);
        }

        if (!(user.getDeptName().equals("综合计划部") || user.getDeptName().equals("财务部"))) {
            wrapper.eq(Price2::getDeptId, user.getDeptId());
        }


        page = price2Service.page(new Page<>(current, pageSize), wrapper);
        List<Price2> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(Price2::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public Price2 get(Integer id) {
        Price2 price2 = price2Service.getById(id);

        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price2").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        price2.setFileList(fileList);
        return price2;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody Price2After after) {
        return price2Service.btnHandle(after);
    }
}
