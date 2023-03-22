package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.BigProjectAfter;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 重大项目立项 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-01-11
 */
@RestController
@RequestMapping("/bigProject")
@Wrapper
public class BigProjectController {
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    BigProjectTestService bigProjectTestService;
    @Autowired
    BigProjectTest2Service bigProjectTest2Service;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<BigProject> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<BigProject> page;
        LambdaQueryWrapper<BigProject> wrapper = new LambdaQueryWrapper<BigProject>().eq(BigProject::getHaveDisplay, "是").orderByDesc(BigProject::getId);
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(BigProject::getDisplayName, user.getDisplayName());
        }
        page = bigProjectService.page(new Page<>(current, pageSize), wrapper);
        List<BigProject> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(BigProject::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @PostMapping("viewHistory")
    public IPage<BigProject> historyList(@RequestBody Map<String, Object> paramMap) {
        String path = (String) paramMap.get("path");
        Integer businessBaseId = (Integer) paramMap.get("businessBaseId");
        List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).eq(ProcessInst::getBusinessBaseId, businessBaseId));
        List<Integer> beforeIdList = processInstList.stream().map(ProcessInst::getBusinessBeforeId).collect(Collectors.toList());

        List<ProcessInst> processInstList2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getPath, path).in(ProcessInst::getBusinessId, beforeIdList));
        Map<Integer, ProcessInst> processInst2Map = processInstList2.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));

        IPage<BigProject> page;
        LambdaQueryWrapper<BigProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BigProject::getId, beforeIdList).orderByDesc(BigProject::getId);
        page = bigProjectService.page(new Page<>(1, 100), wrapper);
        List<BigProject> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setProcessInst(processInst2Map.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public BigProject get(Integer id) {
        BigProject bigProject = bigProjectService.getById(id);
        List<SmallProtect> list = smallProtectService.list(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getProjectId, id));
        bigProject.setList(list);
        bigProject.setIdTypeListTmp(Arrays.asList(bigProject.getIdType().split(",")));

        //
        List<BigProjectTest2> test2List = bigProjectTest2Service.list();
        Map<String, String> test2Map = new HashMap<>();
        for (BigProjectTest2 tmp : test2List) {
            test2Map.put(tmp.getType() + tmp.getDesc1(), tmp.getScoreDesc());
        }

        List<BigProjectTest> list2 = new ArrayList<>();
        List<BigProjectTest> list3 = new ArrayList<>();
        List<BigProjectTest> list4 = new ArrayList<>();

        List<BigProjectTest> list234 = bigProjectTestService.list(new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getProjectId, id));
        for (BigProjectTest test : list234) {
            if (test.getType().equals("project")) {
                test.setScoreDesc(test2Map.get("project" + test.getDesc1()));
                list2.add(test);
            } else if (test.getType().equals("customer")) {
                test.setScoreDesc(test2Map.get("customer" + test.getDesc1()));
                list3.add(test);
            } else {
                test.setScoreDesc(test2Map.get("provider" + test.getDesc1()));
                list4.add(test);
            }
        }

        bigProject.setList2(list2);
        bigProject.setList3(list3);
        bigProject.setList4(list4);
        //
        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "BigProject").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        bigProject.setFileList(fileList);
        return bigProject;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody BigProjectAfter after) {
        return bigProjectService.btnHandle(after);
    }

}
