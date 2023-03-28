package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.SmallBudgetRunAfter;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallBudgetRunService;
import org.jeecgframework.minidao.pagehelper.PageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目预算的流程 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
@RestController
@RequestMapping("/smallBudgetRun")
@Wrapper
public class SmallBudgetRunController {
    @Autowired
    SmallBudgetRunService smallBudgetRunService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    BudgetProjectService budgetProjectService;

    @PostMapping("list")
    public IPage<SmallBudgetRun> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<SmallBudgetRun> page;
        LambdaQueryWrapper<SmallBudgetRun> wrapper = new LambdaQueryWrapper<SmallBudgetRun>().eq(SmallBudgetRun::getHaveDisplay, "是").orderByDesc(SmallBudgetRun::getId);

        Object projectType = paramMap.get("projectType");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");

        if (ObjectUtil.isNotEmpty(projectType)) {
            wrapper.like(SmallBudgetRun::getProjectType, projectType);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(SmallBudgetRun::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(SmallBudgetRun::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(SmallBudgetRun::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(SmallBudgetRun::getDeptName, deptName);
        }


        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(SmallBudgetRun::getDisplayName, user.getDisplayName());
        }

        page = smallBudgetRunService.page(new Page<>(current, pageSize), wrapper);
        List<SmallBudgetRun> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(SmallBudgetRun::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
            //
            List<Integer> idList = recordList.stream().map(SmallBudgetRun::getId).collect(Collectors.toList());
            List<BudgetProject> list = budgetProjectService.list(new LambdaQueryWrapper<BudgetProject>().in(BudgetProject::getBeforeId, idList));
            Map<Integer, BudgetProject> map = list.stream().collect(Collectors.toMap(BudgetProject::getBeforeId, v -> v));
            recordList.forEach(record -> record.setNewBudgetProject(map.get(record.getId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<SmallBudgetRun> historyList(@RequestBody Map<String, Object> paramMap) {
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetRunPath").eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<SmallBudgetRun> page;
        LambdaQueryWrapper<SmallBudgetRun> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SmallBudgetRun::getId, beforeIdList).orderByDesc(SmallBudgetRun::getId);
        page = smallBudgetRunService.page(new Page<>(1, 100), wrapper);
        List<SmallBudgetRun> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public SmallBudgetRun get(Integer id, String type) {
        SmallBudgetRun smallBudgetRun = smallBudgetRunService.getById(id);
        //
        if ("change".equals(type)) {
            BudgetProject tmp = budgetProjectService.getOne(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getHaveDisplay, "是").eq(BudgetProject::getBeforeId, id));
            if (tmp == null) {
                throw new PageException("先从 项目信息 中调整预算");
            } else {
                smallBudgetRun.setId(tmp.getId());
                smallBudgetRun.setBaseId(tmp.getBaseId());
                smallBudgetRun.setBeforeId(tmp.getBeforeId());
            }
        }

        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "SmallBudgetRun").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        smallBudgetRun.setFileList(fileList);
        return smallBudgetRun;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody SmallBudgetRunAfter after) {
        return smallBudgetRunService.btnHandle(after);
    }
}
