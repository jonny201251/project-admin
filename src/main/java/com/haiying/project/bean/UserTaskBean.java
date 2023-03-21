package com.haiying.project.bean;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.utils.SpringUtil;
import com.haiying.project.model.entity.*;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.*;
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
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    ProviderScore1Service providerScore1Service;
    @Autowired
    ProviderQueryService providerQueryService;
    @Autowired
    ProviderControlService providerControlService;
    @Autowired
    OutContractService outContractService;
    @Autowired
    ProcessDesignService processDesignService;
    @Autowired
    OtherPowerService otherPowerService;
    @Autowired
    SmallProjectService smallProjectService;

    public Set<String> getLoginNameList(ProcessDesignTask processDesignTask, Integer businessId, String actProcessInstanceId) {
        Set<String> loginNameSet = new TreeSet<>();

        String type = processDesignTask.getType();
        if (type.equals("角色") || type.equals("用户")) {
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
                        //申请部门的deptId
                        ProcessInst processInst = processInstService.getOne(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessDesignId, processDesignTask.getProcessDesignId()).eq(ProcessInst::getActProcessInstanceId, actProcessInstanceId));
                        if (processInst == null) {
                            throw new PageTipException("需要处理人");
                        }
                        List<String> leader2List = chargeDeptLeaderService
                                .list(new LambdaQueryWrapper<ChargeDeptLeader>().in(ChargeDeptLeader::getDeptId, processInst.getDeptId()))
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
        } else {
            //表单---删除
            ProcessDesign processDesign = processDesignService.getById(processDesignTask.getProcessDesignId());
            String path = processDesign.getPath();
            if (path.equals("providerScore1Path")) {
                //供方评分
                ProviderScore1 providerScore1 = providerScore1Service.getById(businessId);
                String typee = providerScore1.getType();
                if (typee.equals("民用产业项目")) {
                    loginNameSet.add("张强");
                } else {
                    //技改或自筹项目
                    loginNameSet.add("张强");
                }
            } else if (path.equals("providerQueryPath")) {
                //供方尽职调查
                ProviderQuery providerQuery = providerQueryService.getById(businessId);
                String str = providerQuery.getUserNamee();
                String[] tmp = str.split(",");
                loginNameSet.addAll(Arrays.asList(tmp));
            } else if (path.equals("providerControlPath")) {
                //供方动态监控
                ProviderControl providerControl = providerControlService.getById(businessId);
                String str = providerControl.getUserNamee();
                String[] tmp = str.split(",");
                loginNameSet.addAll(Arrays.asList(tmp));
            } else if (path.equals("outContractPath")) {
                //付款合同
                OutContract outContract = outContractService.getById(businessId);
                Object fieldValue = ReflectUtil.getFieldValue(outContract, processDesignTask.getJavaVarName());
                String deptName = (String) fieldValue;
                if (deptName.equals("机电系统集成事业部") || deptName.equals("市场部") || deptName.equals("海南事业部")) {
                    loginNameSet.add("王灿");
                } else {
                    loginNameSet.add("乔丹月");
                }
            } else if (path.equals("otherPowerPath")) {
                //一事一授权
                OtherPower otherPower = otherPowerService.getById(businessId);
                String endType = otherPower.getEndType();
                if (endType.equals("董事长")) {
                    loginNameSet.add("高志国");
                } else {
                    //公司主管领导
                    List<String> leader2List = chargeDeptLeaderService
                            .list(new LambdaQueryWrapper<ChargeDeptLeader>().in(ChargeDeptLeader::getDeptId, otherPower.getDeptId()))
                            .stream().map(ChargeDeptLeader::getLoginName).collect(Collectors.toList());
                    if (ObjectUtil.isNotEmpty(leader2List)) {
                        loginNameSet.addAll(leader2List);
                    }
                }
            } else if (path.equals("smallProjectPath")) {
                //一般项目立项
                SmallProject smallProject = smallProjectService.getById(businessId);
                String userNamee = smallProject.getUserNamee();
                loginNameSet.add(userNamee);
            } else {
                throw new PageTipException("需要处理人");
            }
        }
        return loginNameSet;
    }

    public Set<String> getLoginNameList(Integer processDesignId, String taskKey, String actProcessInstanceId) {
        ProcessDesignTask processDesignTask = processDesignTaskService.getOne(new LambdaQueryWrapper<ProcessDesignTask>().eq(ProcessDesignTask::getProcessDesignId, processDesignId).eq(ProcessDesignTask::getTaskKey, taskKey));
        //businessId
        WorkFlowBean workFlowBean = SpringUtil.getBean(WorkFlowBean.class);
        Integer businessId = workFlowBean.getBusinessIdByProcessInstanceId(actProcessInstanceId);

        return getLoginNameList(processDesignTask, businessId, actProcessInstanceId);
    }
}

