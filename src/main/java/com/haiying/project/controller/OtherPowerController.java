package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.OtherPower;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
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
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<OtherPower> page;
        LambdaQueryWrapper<OtherPower> wrapper = new LambdaQueryWrapper<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
//        wrapper.like(OtherPower::getLoginName, user.getLoginName()).orderByDesc(OtherPower::getId);
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
        return otherPower;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody OtherPowerAfter after) {
        return otherPowerService.btnHandle(after);
    }
}
