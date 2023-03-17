package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.ProviderScore1;
import com.haiying.project.model.entity.ProviderScore2;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.ProviderScore1After;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProviderScore1Service;
import com.haiying.project.service.ProviderScore2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 供方评分1 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-27
 */
@RestController
@RequestMapping("/providerScore1")
@Wrapper
public class ProviderScore1Controller {
    @Autowired
    ProviderScore1Service providerScore1Service;
    @Autowired
    ProviderScore2Service providerScore2Service;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<ProviderScore1> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProviderScore1> page;
        LambdaQueryWrapper<ProviderScore1> wrapper = new LambdaQueryWrapper<ProviderScore1>().eq(ProviderScore1::getDisplayName, user.getDisplayName()).eq(ProviderScore1::getHaveDisplay, "是").orderByDesc(ProviderScore1::getId);
        page = providerScore1Service.page(new Page<>(current, pageSize), wrapper);
        List<ProviderScore1> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProviderScore1::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<ProviderScore1> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().likeLeft(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().likeLeft(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<ProviderScore1> page;
        LambdaQueryWrapper<ProviderScore1> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProviderScore1::getId, beforeIdList).orderByDesc(ProviderScore1::getId);
        page = providerScore1Service.page(new Page<>(1, 100), wrapper);
        List<ProviderScore1> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public ProviderScore1 get(Integer id) {
        ProviderScore1 providerScore1 = providerScore1Service.getById(id);
        List<ProviderScore2> list = providerScore2Service.list(new LambdaQueryWrapper<ProviderScore2>().eq(ProviderScore2::getProviderScore1Id, id));
        providerScore1.setProviderScore2List(list);
        return providerScore1;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody ProviderScore1After providerScore1After) {
        return providerScore1Service.btnHandle(providerScore1After);
    }
}
