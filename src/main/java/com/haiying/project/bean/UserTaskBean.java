package com.haiying.project.bean;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserTaskBean {
    @Autowired
    HttpSession httpSession;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysRoleUserService sysRoleUserService;
    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    ChargeDeptLeaderService chargeDeptLeaderService;
    @Autowired
    ProcessDesignTaskService processDesignTaskService;

    public Set<String> getLoginNameList(ProcessDesignTask processDesignTask) {
        Set<String> loginNameSet = new HashSet<>();

        String type = processDesignTask.getType();
        String[] idArr = processDesignTask.getTypeIds().split(",");
        List<Integer> idList = new ArrayList<>();
        for (String str : idArr) {
            idList.add(Integer.parseInt(str));
        }

        if (type.equals("角色")) {
            //第一种情况
            List<SysRoleUser> roleUserList = sysRoleUserService.list(new LambdaQueryWrapper<SysRoleUser>().in(SysRoleUser::getRoleId, idList));
            if (ObjectUtil.isNotEmpty(roleUserList)) {
                List<Integer> userIdList = roleUserList.stream().map(SysRoleUser::getUserId).collect(Collectors.toList());
                List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIdList));
                userList.forEach(user -> loginNameSet.add(user.getLoginName()));
            }
            //第二种情况
            SysUser currentUser = (SysUser) httpSession.getAttribute("user");
            List<SysRole> roleList = sysRoleService.list(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, idList));
            for (SysRole sysRole : roleList) {
                if (sysRole.getName().equals("当前用户")) {
                    loginNameSet.add(currentUser.getLoginName());
                } else if (sysRole.getName().equals("部门领导")) {
                    List<SysUser> leaderList = sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getDeptId, currentUser.getDeptId()).eq(SysUser::getPosition, "部门正职领导"));
                    if (ObjectUtil.isNotEmpty(leaderList)) {
                        leaderList.forEach(user -> loginNameSet.add(user.getLoginName()));
                    }
                } else if (sysRole.getName().equals("公司主管领导")) {
                    List<String> leader2List = chargeDeptLeaderService
                            .list(new LambdaQueryWrapper<ChargeDeptLeader>().in(ChargeDeptLeader::getDeptId, currentUser.getDeptId()))
                            .stream().map(ChargeDeptLeader::getLoginName).collect(Collectors.toList());
                    if (ObjectUtil.isNotEmpty(leader2List)) {
                        loginNameSet.addAll(leader2List);
                    }
                }
            }
        } else {
            List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, idList));
            userList.forEach(user -> loginNameSet.add(user.getLoginName()));
        }
        return loginNameSet;
    }

    public Set<String> getLoginNameList(Integer processDesignId, String taskKey) {
        ProcessDesignTask processDesignTask = processDesignTaskService.getOne(new LambdaQueryWrapper<ProcessDesignTask>().eq(ProcessDesignTask::getProcessDesignId, processDesignId).eq(ProcessDesignTask::getTaskKey, taskKey));
        return getLoginNameList(processDesignTask);
    }
}

