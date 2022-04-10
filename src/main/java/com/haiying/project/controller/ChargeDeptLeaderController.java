package com.haiying.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ChargeDeptLeader;
import com.haiying.project.model.entity.SysDept;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.ChargeDeptLeaderVO;
import com.haiying.project.model.vo.LabelValue;
import com.haiying.project.service.ChargeDeptLeaderService;
import com.haiying.project.service.SysDeptService;
import com.haiying.project.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 主管部门领导 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-28
 */
@RestController
@RequestMapping("/chargeDeptLeader")
@Wrapper
public class ChargeDeptLeaderController {
    @Autowired
    ChargeDeptLeaderService chargeDeptLeaderService;
    @Autowired
    SysDeptService sysDeptService;
    @Autowired
    SysUserService sysUserService;

    @PostMapping("list")
    public IPage<ChargeDeptLeader> list(@RequestBody Map<String,Object> paramMap) {
        Integer current= (Integer) paramMap.get("current");
        Integer pageSize= (Integer) paramMap.get("pageSize");
        QueryWrapper<ChargeDeptLeader> wrapper = new QueryWrapper<ChargeDeptLeader>().select("distinct login_name");
        return chargeDeptLeaderService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody ChargeDeptLeaderVO chargeDeptLeaderVO) {
        List<ChargeDeptLeader> list = new ArrayList<>();
        for (Integer deptId : chargeDeptLeaderVO.getDeptIdList()) {
            ChargeDeptLeader chargeDeptLeader = new ChargeDeptLeader();
            chargeDeptLeader.setLoginName(chargeDeptLeaderVO.getLoginName());
            chargeDeptLeader.setDeptId(deptId);
            list.add(chargeDeptLeader);
        }
        return chargeDeptLeaderService.saveBatch(list);
    }

    @GetMapping("get")
    public ChargeDeptLeaderVO get(String loginName) {
        List<ChargeDeptLeader> list = chargeDeptLeaderService.list(new LambdaQueryWrapper<ChargeDeptLeader>().eq(ChargeDeptLeader::getLoginName, loginName));
        ChargeDeptLeaderVO chargeDeptLeaderVO = new ChargeDeptLeaderVO();
        chargeDeptLeaderVO.setLoginName(loginName);
        chargeDeptLeaderVO.setDeptIdList(list.stream().map(ChargeDeptLeader::getDeptId).collect(Collectors.toList()));
        return chargeDeptLeaderVO;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody ChargeDeptLeaderVO chargeDeptLeaderVO) {
        //先删除
        chargeDeptLeaderService.remove(new LambdaQueryWrapper<ChargeDeptLeader>().eq(ChargeDeptLeader::getLoginName, chargeDeptLeaderVO.getLoginName()));
        //后插入
        add(chargeDeptLeaderVO);
        return true;
    }

    @GetMapping("delete")
    public boolean delete(String[] arr) {
        List<String> list = Stream.of(arr).collect(Collectors.toList());
        return chargeDeptLeaderService.remove(new LambdaQueryWrapper<ChargeDeptLeader>().in(ChargeDeptLeader::getLoginName, list));
    }

    @GetMapping("getChargeDeptLeader")
    public List<LabelValue> getLeadName() {
        List<Integer> deptIdList = sysDeptService.list(new LambdaQueryWrapper<SysDept>().in(SysDept::getName, Arrays.asList("安全生产总监","副总师级", "财务副总监"))).stream().map(SysDept::getId).collect(Collectors.toList());
        List<SysUser> list = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPosition, "公司领导").or().in(SysUser::getDeptId, deptIdList));
        return list.stream().map(item -> new LabelValue(item.getLoginName(), item.getLoginName())).collect(Collectors.toList());
    }


    @GetMapping("getDeptVL")
    public List<LabelValue> getTreeSelect() {
        List<SysDept> list = sysDeptService.list(new LambdaQueryWrapper<SysDept>().notIn(SysDept::getName, Arrays.asList("公司领导", "安全生产总监", "副总师级", "财务副总监","离退休办公室")).orderByAsc(SysDept::getSort));
        return list.stream().map(item -> new LabelValue(item.getName(), item.getId())).collect(Collectors.toList());
    }
}
