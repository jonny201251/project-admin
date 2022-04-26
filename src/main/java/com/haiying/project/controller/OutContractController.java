package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.OutContract;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.OutContractVO;
import com.haiying.project.service.OutContractService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 付款合同 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-21
 */
@RestController
@RequestMapping("/outContract")
@Wrapper
public class OutContractController {
    @Autowired
    OutContractService outContractService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<OutContract> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<OutContract> page;
        LambdaQueryWrapper<OutContract> wrapper = new LambdaQueryWrapper<OutContract>().eq(OutContract::getHaveDisplay, "是").orderByDesc(OutContract::getId);
        SysUser user = (SysUser) httpSession.getAttribute("user");
//        wrapper.like(OutContract::getLoginName, user.getLoginName()).orderByDesc(OutContract::getId);
        page = outContractService.page(new Page<>(current, pageSize), wrapper);
        List<OutContract> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(OutContract::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<OutContract> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<OutContract> page;
        LambdaQueryWrapper<OutContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OutContract::getId, beforeIdList).orderByDesc(OutContract::getId);
        page = outContractService.page(new Page<>(1, 100), wrapper);
        List<OutContract> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public OutContract get(Integer id) {
        return outContractService.getById(id);
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody OutContractVO outContractVO) {
        return outContractService.btnHandle(outContractVO);
    }
}
