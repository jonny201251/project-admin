package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.Price4;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.Price4After;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.Price4Service;
import com.haiying.project.service.ProcessInstService;
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
 * 采购方式-比价单 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
@RestController
@RequestMapping("/price4")
@Wrapper
public class Price4Controller {
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    Price4Service price4Service;

    @PostMapping("list")
    public IPage<Price4> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<Price4> page;
        LambdaQueryWrapper<Price4> wrapper = new LambdaQueryWrapper<Price4>().orderByDesc(Price4::getId);

        Object projectName = paramMap.get("projectName");
        Object projectLevel = paramMap.get("projectLevel");
        Object taskCode = paramMap.get("taskCode");
        Object inContractName = paramMap.get("inContractName");
        Object inContractCode = paramMap.get("inContractCode");
        if (ObjectUtil.isNotEmpty(projectName)) {
            wrapper.like(Price4::getProjectName, projectName);
        }
        if (ObjectUtil.isNotEmpty(projectLevel)) {
            wrapper.like(Price4::getProjectLevel, projectLevel);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(Price4::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(inContractName)) {
            wrapper.like(Price4::getInContractName, inContractName);
        }
        if (ObjectUtil.isNotEmpty(inContractCode)) {
            wrapper.like(Price4::getInContractCode, inContractCode);
        }

        if (!(user.getDeptName().equals("综合计划部") || user.getDeptName().equals("财务部"))) {
            wrapper.eq(Price4::getDeptId, user.getDeptId());
        }


        page = price4Service.page(new Page<>(current, pageSize), wrapper);
        List<Price4> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(Price4::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public Price4 get(Integer id) {
        Price4 price4 = price4Service.getById(id);
        price4.setRequestList(Arrays.asList(price4.getRequest().split(",")));

        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price4").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        price4.setFileList(fileList);
        return price4;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody Price4After after) {
        return price4Service.btnHandle(after);
    }
}
