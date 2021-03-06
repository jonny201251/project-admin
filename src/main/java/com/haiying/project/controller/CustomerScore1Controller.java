package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.CustomerScore1;
import com.haiying.project.model.entity.CustomerScore2;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.CustomerScore1After;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.CustomerScore1Service;
import com.haiying.project.service.CustomerScore2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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

    @PostMapping("list")
    public IPage<CustomerScore1> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<CustomerScore1> page;
        LambdaQueryWrapper<CustomerScore1> wrapper = new LambdaQueryWrapper<CustomerScore1>().eq(CustomerScore1::getHaveDisplay, "是");
        SysUser user = (SysUser) httpSession.getAttribute("user");
//        wrapper.like(CustomerScore1::getLoginName, user.getLoginName()).orderByDesc(CustomerScore1::getId);
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
        List<CustomerScore2> list = customerScore2Service.list(new LambdaQueryWrapper<CustomerScore2>().eq(CustomerScore2::getCustomerScore1Id, id));
        customerScore1.setList(list);
        return customerScore1;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody CustomerScore1After after) {
        return customerScore1Service.btnHandle(after);
    }
}
