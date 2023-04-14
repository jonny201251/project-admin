package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.BudgetProjecttAfter;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般和重大项目预算-项目 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-04-13
 */
@RestController
@RequestMapping("/budgetProjectt")
@Wrapper
public class BudgetProjecttController {
    @Autowired
    BudgetProjecttService budgetProjecttService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    BudgetProtectService budgetProtectService;
    @Autowired
    BudgetInnService budgetInnService;
    @Autowired
    BudgetOutService budgetOutService;

    @PostMapping("list")
    public IPage<BudgetProjectt> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        LambdaQueryWrapper<BudgetProjectt> wrapper = new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object projectType = paramMap.get("projectType");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object property = paramMap.get("property");
        Object contractCode = paramMap.get("contractCode");
        Object contractName = paramMap.get("contractName");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(projectType)) {
            wrapper.like(BudgetProjectt::getProjectType, projectType);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(BudgetProjectt::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(BudgetProjectt::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(property)) {
            wrapper.like(BudgetProjectt::getProperty, property);
        }
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper.like(BudgetProjectt::getContractCode, contractCode);
        }
        if (ObjectUtil.isNotEmpty(contractName)) {
            wrapper.like(BudgetProjectt::getContractName, contractName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(BudgetProjectt::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(BudgetProjectt::getDeptName, deptName);
        }
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(BudgetProjectt::getDisplayName, user.getDisplayName());
        }

        IPage<BudgetProjectt> page = budgetProjecttService.page(new Page<>(current, pageSize), wrapper);
        List<BudgetProjectt> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<Integer> idList = recordList.stream().map(BudgetProjectt::getId).collect(Collectors.toList());
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetProjecttPath").in(ProcessInst::getBusinessId, idList));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getId())));
        }

        return page;
    }

    @PostMapping("viewHistory")
    public IPage<BudgetProjectt> historyList(@RequestBody Map<String, Object> paramMap) {
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetRunPath").eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<BudgetProjectt> page;
        LambdaQueryWrapper<BudgetProjectt> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BudgetProjectt::getId, beforeIdList).orderByDesc(BudgetProjectt::getId);
        page = budgetProjecttService.page(new Page<>(1, 100), wrapper);
        List<BudgetProjectt> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }

        return page;
    }

    @GetMapping("get")
    public BudgetProjectt get(Integer id, String type) {
        BudgetProjectt budgetProjectt = budgetProjecttService.getById(id);

        List<BudgetProtect> protectList = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, id));
        budgetProjectt.setProtectList(protectList);

        List<BudgetInn> innList = budgetInnService.list(new LambdaQueryWrapper<BudgetInn>().eq(BudgetInn::getBudgetId, id));
        budgetProjectt.setInnList(innList);

        List<BudgetOut> outList = budgetOutService.list(new LambdaQueryWrapper<BudgetOut>().eq(BudgetOut::getBudgetId, id));
        budgetProjectt.setOutList(outList);

        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "BudgetProjectt").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        budgetProjectt.setFileList(fileList);
        return budgetProjectt;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody BudgetProjecttAfter after) {
        return budgetProjecttService.btnHandle(after);
    }

}
