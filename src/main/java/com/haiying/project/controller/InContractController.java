package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.InContract;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.excel.InContractExcel;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.InContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 收款合同 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
@RestController
@RequestMapping("/inContract")
@Wrapper
public class InContractController {
    @Autowired
    InContractService inContractService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<InContract> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<InContract> wrapper = new LambdaQueryWrapper<InContract>().orderByDesc(InContract::getId);
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");

        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(InContract::getName, name);
        }
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(InContract::getDisplayName, user.getDisplayName());
        }

        return inContractService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody InContract inContract) {
        return inContractService.add(inContract);
    }

    @GetMapping("get")
    public InContract get(String id) {
        InContract inContract = inContractService.getById(id);
        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "InContract").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        inContract.setFileList(fileList);
        return inContract;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody InContract inContract) {
        return inContractService.edit(inContract);
    }

    @PostMapping("upload")
    public boolean upload(@RequestBody MultipartFile multipartFile) {
        return inContractService.upload(multipartFile);
    }

    @GetMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("收款合同模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");
        //
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).useDefaultStyle(false).excelType(ExcelTypeEnum.XLS).build();
        List<InContract> dataList = new ArrayList<>();
        WriteSheet sheet = EasyExcel.writerSheet(0, "收款合同模板").head(InContractExcel.class).build();
        //
        excelWriter.write(dataList, sheet);
        //
        excelWriter.finish();
    }
}
