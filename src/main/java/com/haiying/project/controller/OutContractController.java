package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.OutContract;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.OutContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 付款合同 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-03-26
 */
@RestController
@RequestMapping("/outContract")
@Wrapper
public class OutContractController {
    @Autowired
    OutContractService outContractService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<OutContract> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<OutContract> wrapper = new LambdaQueryWrapper<OutContract>().orderByDesc(OutContract::getId);
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");

        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object wbs = paramMap.get("wbs");
        Object contractCode = paramMap.get("contractCode");
        Object contractName = paramMap.get("contractName");
        Object providerName = paramMap.get("providerName");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(OutContract::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(OutContract::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(wbs)) {
            wrapper.like(OutContract::getWbs, wbs);
        }
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper.like(OutContract::getContractCode, contractCode);
        }
        if (ObjectUtil.isNotEmpty(contractName)) {
            wrapper.like(OutContract::getContractName, contractName);
        }
        if (ObjectUtil.isNotEmpty(providerName)) {
            wrapper.like(OutContract::getProviderName, providerName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(OutContract::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(OutContract::getDeptName, deptName);
        }

        if (!(user.getDeptName().equals("综合计划部") || user.getDeptName().equals("财务部"))) {
            wrapper.eq(OutContract::getDeptId, user.getDeptId());
        }

        return outContractService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody OutContract outContract) {
        return outContractService.add(outContract);
    }

    @GetMapping("get")
    public OutContract get(String id) {
        OutContract outContract = outContractService.getById(id);
        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "OutContract").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        outContract.setFileList(fileList);
        return outContract;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody OutContract outContract) {
        return outContractService.edit(outContract);
    }
}
