package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.Price1After;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.Price11Service;
import com.haiying.project.service.Price1Service;
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
@RequestMapping("/price1")
@Wrapper
public class Price1Controller {
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    Price1Service price1Service;
    @Autowired
    Price11Service price11Service;

    @PostMapping("list")
    public IPage<Price1> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<Price1> page;
        LambdaQueryWrapper<Price1> wrapper = new LambdaQueryWrapper<Price1>().orderByDesc(Price1::getId);

        Object projectName = paramMap.get("projectName");
        Object taskCode = paramMap.get("taskCode");
        Object inContractName = paramMap.get("inContractName");
        Object inContractCode = paramMap.get("inContractCode");
        if (ObjectUtil.isNotEmpty(projectName)) {
            wrapper.like(Price1::getProjectName, projectName);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(Price1::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(inContractName)) {
            wrapper.like(Price1::getInContractName, inContractName);
        }
        if (ObjectUtil.isNotEmpty(inContractCode)) {
            wrapper.like(Price1::getInContractCode, inContractCode);
        }


        if (!(user.getDeptName().equals("综合计划部") || user.getDeptName().equals("财务部"))) {
            wrapper.eq(Price1::getDeptId, user.getDeptId());
        }


        page = price1Service.page(new Page<>(current, pageSize), wrapper);
        List<Price1> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(Price1::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public Price1 get(Integer id) {
        Price1 price1 = price1Service.getById(id);
        List<Price11> list = price11Service.list(new LambdaQueryWrapper<Price11>().eq(Price11::getPrice1Id, id));
        price1.setList(list);

        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price1").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        price1.setFileList(fileList);
        return price1;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody Price1After after) {
        return price1Service.btnHandle(after);
    }
}
