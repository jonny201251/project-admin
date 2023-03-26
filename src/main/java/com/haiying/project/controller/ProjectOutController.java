package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectOut;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProjectOutService;
import com.haiying.project.service.SmallBudgetOutService;
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
    HttpSession httpSession;
    @Autowired
    ProjectOutService projectOutService;
    @Autowired
    SmallBudgetOutService smallBudgetOutService;

    @PostMapping("list")
    public IPage<ProjectOut> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        LambdaQueryWrapper<ProjectOut> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");

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

        //支出不能超出预算
        if (projectOut.getHaveContract().equals("有")) {
            List<SmallBudgetOut> ll = smallBudgetOutService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getHaveDisplay, "是").eq(SmallBudgetOut::getTaskCode, projectOut.getTaskCode()).eq(SmallBudgetOut::getCostType, projectOut.getCostType()));
            double totalCost = 0.0;
            for (SmallBudgetOut smallBudgetOut : ll) {
                totalCost += ofNullable(smallBudgetOut.getMoney()).orElse(0.0);
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney1())) {
                if (projectOut.getMoney1() > totalCost) {
                    throw new PageTipException("开票金额:" + projectOut.getMoney1() + " ,超出预算额:" + totalCost);
                }
            }
            if (ObjectUtil.isNotEmpty(projectOut.getMoney2())) {
                if (projectOut.getMoney2() > totalCost) {
                    throw new PageTipException("付款金额:" + projectOut.getMoney2() + " ,超出预算额:" + totalCost);
                }
            }
        }
        return projectOutService.updateById(projectOut);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        return projectOutService.removeByIds(idList);
    }
}
