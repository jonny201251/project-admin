package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.CustomerCheck12;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.CustomerCheck12After;
import com.haiying.project.service.CustomerCheck12Service;
import com.haiying.project.service.CustomerScore2Service;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 客户信用审批 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@RestController
@RequestMapping("/customerCheck12")
@Wrapper
public class CustomerCheck12Controller {
    @Autowired
    CustomerCheck12Service customerCheck12Service;
    @Autowired
    CustomerScore2Service customerScore2Service;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<CustomerCheck12> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<CustomerCheck12> page;
        LambdaQueryWrapper<CustomerCheck12> wrapper = new LambdaQueryWrapper<CustomerCheck12>().eq(CustomerCheck12::getHaveDisplay, "是");
        SysUser user = (SysUser) httpSession.getAttribute("user");
//        wrapper.like(CustomerCheck12::getLoginName, user.getLoginName()).orderByDesc(CustomerCheck12::getId);
        page = customerCheck12Service.page(new Page<>(current, pageSize), wrapper);
        List<CustomerCheck12> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(CustomerCheck12::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<CustomerCheck12> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<CustomerCheck12> page;
        LambdaQueryWrapper<CustomerCheck12> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CustomerCheck12::getId, beforeIdList).orderByDesc(CustomerCheck12::getId);
        page = customerCheck12Service.page(new Page<>(1, 100), wrapper);
        List<CustomerCheck12> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public CustomerCheck12 get(Integer id) {
        CustomerCheck12 customerCheck12 = customerCheck12Service.getById(id);
        customerCheck12.setDesc2Tmp(Arrays.asList(customerCheck12.getDesc2().split(",")));
        return customerCheck12;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody CustomerCheck12After after) {
        return customerCheck12Service.btnHandle(after);
    }
}
