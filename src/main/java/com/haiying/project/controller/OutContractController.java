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
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");

        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(OutContract::getName, name);
        }
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(OutContract::getDisplayName, user.getDisplayName());
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
