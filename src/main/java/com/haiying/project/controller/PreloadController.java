package com.haiying.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.SysDept;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.LabelValue;
import com.haiying.project.service.SysDeptService;
import com.haiying.project.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/preload")
@Wrapper
public class PreloadController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysDeptService sysDeptService;

    @GetMapping("get")
    public Map<String, Object> get() {
        Map<String, Object> map = new HashMap<>();
        //部门编码
        List<SysDept> deptList = sysDeptService.list(new LambdaQueryWrapper<SysDept>().isNotNull(SysDept::getType).orderByAsc(SysDept::getType));
        List<LabelValue> deptLabelValue = deptList.stream().map(item -> new LabelValue(item.getName(), item.getType())).collect(Collectors.toList());
        map.put("deptLabelValue", deptLabelValue);
        //用户
        List<SysUser> sysUserList = sysUserService.list();
        List<LabelValue> userList = sysUserList.stream().map(item -> new LabelValue(item.getLoginName(), item.getLoginName())).collect(Collectors.toList());
        map.put("userList", userList);
        //用户
        Map<String, SysUser> userMap = sysUserList.stream().collect(Collectors.toMap(SysUser::getLoginName, v -> v));
        map.put("userMap", userMap);
        return map;
    }
}

