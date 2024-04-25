package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.InNoContract;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.InNoContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 无合同收款 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2024-04-24
 */
@RestController
@RequestMapping("/inNoContract")
@Wrapper
public class InNoContractController {
    @Autowired
    InNoContractService inNoContractService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public IPage<InNoContract> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<InNoContract> wrapper = new LambdaQueryWrapper<InNoContract>().orderByDesc(InNoContract::getId);
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");

        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object deptName = paramMap.get("deptName");
        Object customerName = paramMap.get("customerName");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(InNoContract::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(InNoContract::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper.like(InNoContract::getDeptName, deptName);
        }
        if (ObjectUtil.isNotEmpty(customerName)) {
            wrapper.like(InNoContract::getCustomerName, customerName);
        }
        if (!(user.getDeptName().equals("综合计划部") || user.getDeptName().equals("财务部"))) {
            wrapper.eq(InNoContract::getDeptId, user.getDeptId());
        }

        return inNoContractService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody InNoContract inNoContract) {
        //判断是否重复添加
        List<InNoContract> ll = inNoContractService.list(new LambdaQueryWrapper<InNoContract>().eq(InNoContract::getTaskCode, inNoContract.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("备案号   已存在");
        }
        return inNoContractService.save(inNoContract);
    }

    @GetMapping("get")
    public InNoContract get(String id) {
        return inNoContractService.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody InNoContract inNoContract) {
        return inNoContractService.updateById(inNoContract);
    }

}
