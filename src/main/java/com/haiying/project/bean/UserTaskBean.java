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
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    ProjectProtectService projectProtectService;
    @Autowired
    BudgetProjecttService budgetProjecttService;
    @Autowired
    ProjectOutService projectOutService;
    @Autowired
    Price1Service price1Service;
    @Autowired
    Price2Service price2Service;
    @Autowired
    Price3Service price3Service;

    public Set<String> getLoginNameList(ProcessDesignTask processDesignTask, Integer businessId, String actProcessInstanceId) {
        Set<String> loginNameSet = new TreeSet<>();

        Integer processDesignId = processDesignTask.getProcessDesignId();
        Set<Integer> processDesignIdSet = new HashSet<>();
        processDesignIdSet.add(69);
        processDesignIdSet.add(70);
        processDesignIdSet.add(47);
        processDesignIdSet.add(48);

        String type = processDesignTask.getType();
        if (type.equals("角色") || type.equals("用户")) {
            String[] idArr = processDesignTask.getTypeIds().split(",");
            List<Integer> idList = new ArrayList<>();
            for (String str : idArr) {
                idList.add(Integer.parseInt(str));
            }
            if (type.equals("角色")) {
                SysUser loginUser = (SysUser) httpSession.getAttribute("user");
                ProcessInst processInst = processInstService.getOne(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getActProcessInstanceId, actProcessInstanceId));
                if (processInst != null) {
                    loginUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getLoginName, processInst.getLoginName()));
                }
                List<SysRole> roleList = sysRoleService.list(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, idList));
                for (SysRole sysRole : roleList) {
                    if (sysRole.getName().equals("当前用户")) {
                        loginNameSet.add(loginUser.getLoginName());
                    } else if (sysRole.getName().equals("部门领导")) {
                        List<SysUser> leaderList = sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getDeptId, loginUser.getDeptId()).eq(SysUser::getPosition, "部门正职领导"));
                        if (ObjectUtil.isNotEmpty(leaderList)) {
                            leaderList.forEach(user -> loginNameSet.add(user.getLoginName()));
                        }
                        //供电中心
                        if (loginUser.getDeptName().equals("供电中心") && processDesignIdSet.contains(processDesignId)) {
                            loginNameSet.add("黄少芳");
                        }
                    } else if (sysRole.getName().equals("公司主管领导")) {
                        List<String> leader2List = chargeDeptLeaderService
                                .list(new LambdaQueryWrapper<ChargeDeptLeader>().in(ChargeDeptLeader::getDeptId, processInst.getDeptId()))
                                .stream().map(ChargeDeptLeader::getLoginName).collect(Collectors.toList());
                        if (ObjectUtil.isNotEmpty(leader2List)) {
                            loginNameSet.addAll(leader2List);
                        }
                    }
                }
            } else {
                List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, idList).orderByAsc(SysUser::getId));
                userList.forEach(user -> loginNameSet.add(user.getLoginName()));
            }
        } else {
            //表单---删除
            ProcessDesign processDesign = processDesignService.getById(processDesignTask.getProcessDesignId());
            String path = processDesign.getPath();
            if (path.equals("providerQueryPath")) {
                //供方尽职调查
                ProviderQuery providerQuery = providerQueryService.getById(businessId);
                String str = providerQuery.getUserNamee();
                String[] tmp = str.split(",");
                loginNameSet.addAll(Arrays.asList(tmp));
            } else if (path.equals("providerScorePath")) {
                //供方评分
                ProviderScore1 score1 = providerScore1Service.getById(businessId);
                String str = score1.getUserNamee();
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
                    loginNameSet.add("李海燕");
                } else {
                    loginNameSet.add("乔丹月");
                }
            } else if (path.equals("otherPowerPath")) {
                //一事一授权
                OtherPower otherPower = otherPowerService.getById(businessId);
                if (processDesignTask.getTaskName().equals("授权人意见")) {
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
                } else {
                    String userNamee = otherPower.getUserNamee();
                    String[] tmp = userNamee.split(",");
                    loginNameSet.addAll(Arrays.asList(tmp));
                }
            } else if (path.equals("smallProjectPath")) {
                loginNameSet.add("于欣坤");
                loginNameSet.add("祁瑛");
                //一般项目立项
                SmallProject smallProject = smallProjectService.getById(businessId);
                //财务
                String userNamee = smallProject.getUserNamee();
                loginNameSet.add(userNamee);
                //是否垫资
                String haveGiveMoney = smallProject.getHaveGiveMoney();
                if ("是".equals(haveGiveMoney)) {
                    loginNameSet.add("马聪聪");
                }
                //项目密级
                String projectLevel = smallProject.getProjectLevel();
                if (!"非密".equals(projectLevel)) {
                    loginNameSet.add("王媛媛");
                }
            } else if (path.equals("bigProjectPath")) {
                loginNameSet.add("于欣坤");
                loginNameSet.add("祁瑛");
                loginNameSet.add("马聪聪");
                //项目密级
                BigProject bigProject = bigProjectService.getById(businessId);
                String projectLevel = bigProject.getProjectLevel();
                if (!"非密".equals(projectLevel)) {
                    loginNameSet.add("王媛媛");
                }
            } else if (path.equals("projectProtectPath")) {
                //投标保证金(函)登记
                ProjectProtect projectProtect = projectProtectService.getById(businessId);
                String str = projectProtect.getUserNamee();
                String[] tmp = str.split(",");
                loginNameSet.addAll(Arrays.asList(tmp));
            } else if (path.equals("budgetProjecttPath") || path.equals("bigBudgetProjecttPath")) {
                //项目预算
                BudgetProjectt budgetProjectt = budgetProjecttService.getById(businessId);
                String taskName = processDesignTask.getTaskName();
                if ("财务部".equals(taskName)) {
                    String userNamee = budgetProjectt.getUserNamee();
                    loginNameSet.add(userNamee);
                } else {
                    String deptName = budgetProjectt.getDeptName();
                    if (deptName.equals("机电系统集成事业部") || deptName.equals("市场部") || deptName.equals("海南事业部")) {
                        loginNameSet.add("李海燕");
                    } else {
                        loginNameSet.add("乔丹月");
                    }
                    loginNameSet.add("于欣坤");
                }
            } else if (path.equals("price1Path")) {
                Price1 price1 = price1Service.getById(businessId);
                String userNamee = price1.getUserNamee();
                loginNameSet.add(userNamee);
                //
                loginNameSet.add("孙欢");
                //项目密级
                String projectLevel = price1.getProjectLevel();
                if (!"非密".equals(projectLevel)) {
                    loginNameSet.add("王媛媛");
                }
            } else if (path.equals("price2Path")) {
                Price2 price2 = price2Service.getById(businessId);
                String userNamee = price2.getUserNamee();
                loginNameSet.add(userNamee);
                //
                loginNameSet.add("孙欢");
                //项目密级
                String projectLevel = price2.getProjectLevel();
                if (!"非密".equals(projectLevel)) {
                    loginNameSet.add("王媛媛");
                }
            } else if (path.equals("price3Path")) {
                Price3 price3 = price3Service.getById(businessId);
                String userNamee = price3.getUserNamee();
                loginNameSet.add(userNamee);
                //
                loginNameSet.add("孙欢");
                //项目密级
                String projectLevel = price3.getProjectLevel();
                if (!"非密".equals(projectLevel)) {
                    loginNameSet.add("王媛媛");
                }
            } else if (path.equals("projectOutPath")) {
                //项目收支的支出信息
                ProjectOut projectOut = projectOutService.getById(businessId);
                String str = projectOut.getUserNamee();
                String[] tmp = str.split(",");
                loginNameSet.addAll(Arrays.asList(tmp));
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

