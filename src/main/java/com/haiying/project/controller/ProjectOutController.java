package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectOut;
import com.haiying.project.service.ProjectOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @PostMapping("list")
    public IPage<ProjectOut> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ProjectOut> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(ProjectOut::getId, type);
        }
        return projectOutService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody ProjectOut projectOut) {
        if (ObjectUtil.isAllEmpty(projectOut.getMoney1(), projectOut.getMoney2())) {
            throw new PageTipException("必须有一个开票金额或者付款金额");
        }
        if (ObjectUtil.isAllNotEmpty(projectOut.getMoney1(), projectOut.getMoney2())) {
            throw new PageTipException("只能有一个开票金额或者付款金额");
        }
        if (projectOut.getHaveContract().equals("无")) {
            projectOut.setProviderId(null);
            projectOut.setProviderName(null);
            projectOut.setContractCode(null);
            projectOut.setContractMoney(null);
            projectOut.setContractName(null);
            projectOut.setEndMoney(null);
            projectOut.setCostRate(null);
            projectOut.setOutStyle(null);
            projectOut.setArriveDate(null);
        }
        return projectOutService.add(projectOut);
    }

    @GetMapping("get")
    public ProjectOut get(String id) {
        return projectOutService.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ProjectOut projectOut) {
        if (ObjectUtil.isAllEmpty(projectOut.getMoney1(), projectOut.getMoney2())) {
            throw new PageTipException("必须有一个开票金额或者付款金额");
        }
        if (ObjectUtil.isAllNotEmpty(projectOut.getMoney1(), projectOut.getMoney2())) {
            throw new PageTipException("只能有一个开票金额或者付款金额");
        }
        if (projectOut.getHaveContract().equals("无")) {
            projectOut.setProviderId(null);
            projectOut.setProviderName(null);
            projectOut.setContractCode(null);
            projectOut.setContractMoney(null);
            projectOut.setContractName(null);
            projectOut.setEndMoney(null);
            projectOut.setCostRate(null);
            projectOut.setOutStyle(null);
            projectOut.setArriveDate(null);
        }
        return projectOutService.updateById(projectOut);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return projectOutService.removeByIds(idList);
    }
}
