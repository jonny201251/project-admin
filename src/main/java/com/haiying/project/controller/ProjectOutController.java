package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.ProjectOut;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.ProjectOutAfter;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProjectOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 项目收支-支出明细 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@RestController
@RequestMapping("/projectOut")
@Wrapper
public class ProjectOutController {
    @Autowired
    ProjectOutService projectOutService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    PageBean pageBean;

    @PostMapping("list")
    public IPage<ProjectOut> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        IPage<ProjectOut> page;

        LambdaQueryWrapper<ProjectOut> wrapper = new LambdaQueryWrapper<ProjectOut>().orderByDesc(ProjectOut::getId);

        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object wbs = paramMap.get("wbs");
        Object costType = paramMap.get("costType");
        Object contractCode = paramMap.get("contractCode");
        Object contractName = paramMap.get("contractName");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(ProjectOut::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(ProjectOut::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(wbs)) {
            wrapper.like(ProjectOut::getWbs, wbs);
        }
        if (ObjectUtil.isNotEmpty(costType)) {
            wrapper.like(ProjectOut::getCostType, costType);
        }
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper.like(ProjectOut::getContractCode, contractCode);
        }
        if (ObjectUtil.isNotEmpty(contractName)) {
            wrapper.like(ProjectOut::getContractName, contractName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(ProjectOut::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(ProjectOut::getDeptName, deptName);
        }

        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(ProjectOut::getDisplayName, user.getDisplayName());
        }

        page = projectOutService.page(new Page<>(current, pageSize), wrapper);
        List<ProjectOut> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, recordList.stream().map(ProjectOut::getProcessInstId).collect(Collectors.toList())));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getProcessInstId())));
        }
        return page;
    }


    @PostMapping("list2")
    public ResponseResult list2(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        Object haveContract = paramMap.get("haveContract");
        Object taskCode = paramMap.get("taskCode");
        Object contractCode = paramMap.get("contractCode");
        Object costType = paramMap.get("costType");
        Object costRate = paramMap.get("costRate");
        LambdaQueryWrapper<ProjectOut> wrapper = new LambdaQueryWrapper<ProjectOut>().eq(ProjectOut::getHaveContract, haveContract).eq(ProjectOut::getTaskCode, taskCode);
        if ("有".equals(haveContract)) {
            if (ObjectUtil.isNotEmpty(contractCode)) {
                wrapper.eq(ProjectOut::getContractCode, contractCode);
            }
            if (ObjectUtil.isNotEmpty(costType)) {
                wrapper.eq(ProjectOut::getCostType, costType);
            }
            if (ObjectUtil.isNotEmpty(costRate)) {
                wrapper.eq(ProjectOut::getCostRate, costRate);
            }
        } else {
            if (ObjectUtil.isNotEmpty(costType)) {
                wrapper.eq(ProjectOut::getCostType, costType);
            }
        }
        List<ProjectOut> list = projectOutService.list(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            ProjectOut tmp = new ProjectOut();
            Double money1 = 0.0, money2 = 0.0;
            int count = 1;
            for (ProjectOut out : list) {
                out.setId(count++);
                money1 += ofNullable(out.getMoney1()).orElse(0.0);
                money2 += ofNullable(out.getMoney2()).orElse(0.0);
            }
            if (money1 > 0) {
                tmp.setMoney1(money1);
            }
            if (money2 > 0) {
                tmp.setMoney2(money2);
            }
            list.add(tmp);
            responseResult = pageBean.get(1, 100, list.size(), list);
        }

        return responseResult;
    }

    @GetMapping("get")
    public ProjectOut get(Integer id) {
        ProjectOut projectOut = projectOutService.getById(id);
        projectOut.setUserNameeList(Arrays.asList(projectOut.getUserNamee().split(",")));
        return projectOut;
    }

    @PostMapping("btnHandle")
    public boolean btnHandle(@RequestBody ProjectOutAfter after) {
        return projectOutService.btnHandle(after);
    }
}
