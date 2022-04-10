package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.entity.SmallProtect;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.SmallProjectAfter;
import com.haiying.project.service.SmallProjectService;
import com.haiying.project.service.SmallProtectService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般项目立项 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
@RestController
@RequestMapping("/smallProject")
@Wrapper
public class SmallProjectController {
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<SmallProject> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<SmallProject> page;
        LambdaQueryWrapper<SmallProject> wrapper = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHaveDisplay, "是");
        SysUser user = (SysUser) httpSession.getAttribute("user");
//        wrapper.like(SmallProject::getLoginName, user.getLoginName()).orderByDesc(SmallProject::getId);
        page = smallProjectService.page(new Page<>(current, pageSize), wrapper);
        List<SmallProject> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(SmallProject::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<SmallProject> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<SmallProject> page;
        LambdaQueryWrapper<SmallProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SmallProject::getId, beforeIdList).orderByDesc(SmallProject::getId);
        page = smallProjectService.page(new Page<>(1, 100), wrapper);
        List<SmallProject> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public SmallProject get(Integer id) {
        SmallProject smallProject = smallProjectService.getById(id);
        List<SmallProtect> list = smallProtectService.list(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getSmallProjectId, id));
        smallProject.setList(list);
        return smallProject;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody SmallProjectAfter after) {
        return smallProjectService.btnHandle(after);
    }
}
