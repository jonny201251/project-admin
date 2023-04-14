package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.OtherPowerAfter;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.OtherPowerService;
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
 * 其他授权 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-08
 */
@RestController
@RequestMapping("/otherPower")
@Wrapper
public class OtherPowerController {
    @Autowired
    OtherPowerService otherPowerService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @PostMapping("list")
    public IPage<OtherPower> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<OtherPower> page;
        LambdaQueryWrapper<OtherPower> wrapper = new LambdaQueryWrapper<OtherPower>().eq(OtherPower::getHaveDisplay, "是").orderByDesc(OtherPower::getId);

        Object displayNamee = paramMap.get("displayNamee");
        Object descc = paramMap.get("descc");
        Object code = paramMap.get("code");
        Object status = paramMap.get("status");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(displayNamee)) {
            wrapper.like(OtherPower::getDisplayNamee, displayNamee);
        }
        if (ObjectUtil.isNotEmpty(descc)) {
            wrapper.like(OtherPower::getDescc, descc);
        }
        if (ObjectUtil.isNotEmpty(code)) {
            wrapper.like(OtherPower::getCode, code);
        }
        if (ObjectUtil.isNotEmpty(status)) {
            wrapper.like(OtherPower::getStatus, status);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(OtherPower::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(OtherPower::getDeptName, deptName);
        }

        if (!user.getDeptName().equals("纪监法审部")) {
            wrapper.eq(OtherPower::getDeptId, user.getDeptId());
        }
        page = otherPowerService.page(new Page<>(current, pageSize), wrapper);
        List<OtherPower> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(OtherPower::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }


    @GetMapping("get")
    public OtherPower get(Integer id) {
        OtherPower otherPower = otherPowerService.getById(id);
        otherPower.setDisplayNameeTmp(Arrays.asList(otherPower.getDisplayNamee().split(",")));
        otherPower.setTimeLimitTmp(Arrays.asList(otherPower.getTimeLimit().split("至")));
        List<FileVO> fileList = new ArrayList<>();
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "OtherPower").eq(FormFile::getBusinessId, id));
        for (FormFile formFile : formFileList) {
            FileVO fileVO = new FileVO();
            fileVO.setName(formFile.getName());
            fileVO.setUrl(formFile.getUrl());
            fileVO.setStatus("done");
            fileList.add(fileVO);
        }
        otherPower.setFileList(fileList);
        //
        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (user.getDisplayName().equals("祁瑛") && otherPower.getCode() == null) {
            otherPower.setStatus("未使用");
/*            String code = otherPowerService.getCode(otherPower);
            otherPower.setCode(code);*/
        }
        return otherPower;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody OtherPowerAfter after) {
        return otherPowerService.btnHandle(after);
    }
}
