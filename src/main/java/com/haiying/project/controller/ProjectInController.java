package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectIn;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProjectInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 项目收支-收入明细 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@RestController
@RequestMapping("/projectIn")
@Wrapper
public class ProjectInController {
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProjectInService projectInService;
    @Autowired
    PageBean pageBean;

    @PostMapping("list")
    public IPage<ProjectIn> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<ProjectIn> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");

        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object wbs = paramMap.get("wbs");
        Object contractCode = paramMap.get("contractCode");
        Object contractName = paramMap.get("contractName");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(ProjectIn::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(ProjectIn::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(wbs)) {
            wrapper.like(ProjectIn::getWbs, wbs);
        }
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper.like(ProjectIn::getContractCode, contractCode);
        }
        if (ObjectUtil.isNotEmpty(contractName)) {
            wrapper.like(ProjectIn::getContractName, contractName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper.like(ProjectIn::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(ProjectIn::getDeptName, deptName);
        }

        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(ProjectIn::getDisplayName, user.getDisplayName());
        }
        return projectInService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("list2")
    public ResponseResult list2(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        Object taskCode = paramMap.get("taskCode");
        Object contractCode = paramMap.get("contractCode");
        LambdaQueryWrapper<ProjectIn> wrapper = new LambdaQueryWrapper<ProjectIn>().eq(ProjectIn::getTaskCode, taskCode).eq(ProjectIn::getContractCode,contractCode);
        List<ProjectIn> list = projectInService.list(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            ProjectIn tmp = new ProjectIn();
            Double money1 = 0.0, money2 = 0.0;
            int count = 1;
            for (ProjectIn in : list) {
                in.setId(count++);
                money1 += ofNullable(in.getMoney1()).orElse(0.0);
                money2 += ofNullable(in.getMoney2()).orElse(0.0);
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

    @PostMapping("add")
    public boolean add(@RequestBody ProjectIn projectIn) {
        if (ObjectUtil.isAllEmpty(projectIn.getMoney1(), projectIn.getMoney2())) {
            throw new PageTipException("必须有一个开票金额或者收款金额");
        }
        if (ObjectUtil.isAllNotEmpty(projectIn.getMoney1(), projectIn.getMoney2())) {
            throw new PageTipException("只能有一个开票金额或者收款金额");
        }
        return projectInService.add(projectIn);
    }

    @GetMapping("get")
    public ProjectIn get(String id) {
        return projectInService.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectIn projectIn) {
        if (ObjectUtil.isAllEmpty(projectIn.getMoney1(), projectIn.getMoney2())) {
            throw new PageTipException("必须有一个开票金额或者收款金额");
        }
        if (ObjectUtil.isAllNotEmpty(projectIn.getMoney1(), projectIn.getMoney2())) {
            throw new PageTipException("只能有一个开票金额或者收款金额");
        }
        return projectInService.updateById(projectIn);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return projectInService.removeByIds(idList);
    }
}
