package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.ProjectProtect;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.ProjectProtectAfter;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProjectProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般和重大项目的保证金登记表 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-03-22
 */
@RestController
@RequestMapping("/projectProtect")
@Wrapper
public class ProjectProtectController {
    @Autowired
    ProjectProtectService projectProtectService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<ProjectProtect> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProjectProtect> page;
        LambdaQueryWrapper<ProjectProtect> wrapper = new LambdaQueryWrapper<ProjectProtect>().orderByDesc(ProjectProtect::getId);
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(ProjectProtect::getDisplayName, user.getDisplayName());
        }

        page = projectProtectService.page(new Page<>(current, pageSize), wrapper);
        List<ProjectProtect> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProjectProtect::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }

    @GetMapping("get")
    public ProjectProtect get(Integer id) {
        return projectProtectService.getById(id);
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody ProjectProtectAfter after) {
        return projectProtectService.btnHandle(after);
    }
}
