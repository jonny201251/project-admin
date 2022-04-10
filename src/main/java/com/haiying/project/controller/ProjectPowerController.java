package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.ProjectPower;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.ProjectPowerAfter;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProjectPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般和重大项目立项时，授权信息 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-24
 */
@RestController
@RequestMapping("/projectPower")
@Wrapper
public class ProjectPowerController {
    @Autowired
    ProjectPowerService projectPowerService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<ProjectPower> list(@RequestBody Map<String, Object> paramMap) {
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProjectPower> page;
        LambdaQueryWrapper<ProjectPower> wrapper = new LambdaQueryWrapper<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
//        wrapper.like(ProjectPower::getLoginName, user.getLoginName()).orderByDesc(ProjectPower::getId);
        page = projectPowerService.page(new Page<>(current, pageSize), wrapper);
        List<ProjectPower> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProjectPower::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }


    @GetMapping("get")
    public ProjectPower get(Integer id) {
        ProjectPower projectPower = projectPowerService.getById(id);
        projectPower.setTimeLimitTmp(Arrays.asList(projectPower.getTimeLimit().split("至")));
        return projectPower;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody ProjectPowerAfter after) {
        return projectPowerService.btnHandle(after);
    }
}
