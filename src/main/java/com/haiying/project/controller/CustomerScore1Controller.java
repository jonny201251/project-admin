package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.CustomerScore1After;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.CustomerScore1Service;
import com.haiying.project.service.CustomerScore2Service;
import com.haiying.project.service.FormFileService;
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
 * 客户信用评分1 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@RestController
@RequestMapping("/customerScore1")
@Wrapper
public class CustomerScore1Controller {
    @Autowired
    CustomerScore1Service customerScore1Service;
    @Autowired
    CustomerScore2Service customerScore2Service;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<CustomerScore1> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<CustomerScore1> page;
        LambdaQueryWrapper<CustomerScore1> wrapper = new LambdaQueryWrapper<CustomerScore1>().eq(CustomerScore1::getHaveDisplay, "是").orderByDesc(CustomerScore1::getId);

        Object customerName = paramMap.get("customerName");
        Object result = paramMap.get("result");
        if (ObjectUtil.isNotEmpty(customerName)) {
            wrapper.like(CustomerScore1::getCustomerName, customerName);
        }
        if (ObjectUtil.isNotEmpty(result)) {
            wrapper.like(CustomerScore1::getResult, result);
        }


        if (!user.getDisplayName().equals("宋思奇")) {
            wrapper.eq(CustomerScore1::getDeptId, user.getDeptId());
        }
        page = customerScore1Service.page(new Page<>(current, pageSize), wrapper);
        List<CustomerScore1> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(CustomerScore1::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<CustomerScore1> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<CustomerScore1> page;
        LambdaQueryWrapper<CustomerScore1> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CustomerScore1::getId, beforeIdList).orderByDesc(CustomerScore1::getId);
        page = customerScore1Service.page(new Page<>(1, 100), wrapper);
        List<CustomerScore1> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public CustomerScore1 get(Integer id) {
        CustomerScore1 customerScore1 = customerScore1Service.getById(id);
        //
        customerScore1.setDesc2Tmp(Arrays.asList(customerScore1.getDesc2().split(",")));
        List<CustomerScore2> list = customerScore2Service.list(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, id));
        customerScore1.setList(list);
        //
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "CustomerScore1").eq(FormFile::getBusinessId, id));
        if (ObjectUtil.isNotEmpty(formFileList)) {
            List<FileVO> fileList = new ArrayList<>();
            for (FormFile formFile : formFileList) {
                FileVO fileVO = new FileVO();
                fileVO.setName(formFile.getName());
                fileVO.setUrl(formFile.getUrl());
                fileVO.setStatus("done");
                fileList.add(fileVO);
            }
            customerScore1.setFileList(fileList);
        }
        return customerScore1;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody CustomerScore1After after) {
        return customerScore1Service.btnHandle(after);
    }
}
