package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//收款合同，付款合同，项目收支 弹窗
@RestController
@RequestMapping("/budgetDialog")
public class BudgetDialogController {
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    PageBean pageBean;
    @Autowired
    SmallBudgetOutService smallBudgetOutService;

    //项目预算
    @PostMapping("list")
    public ResponseResult list(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");

        LambdaQueryWrapper<BudgetProject> wrapper = new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getHaveDisplay, "是");
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(BudgetProject::getDeptId, user.getDeptId());
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(BudgetProject::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(BudgetProject::getTaskCode, taskCode);
        }
        List<BudgetProject> list = budgetProjectService.list(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getBusinessHaveDisplay, "是").eq(ProcessInst::getProcessStatus, "完成").like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, list.stream().map(BudgetProject::getId).collect(Collectors.toList())));
            if (ObjectUtil.isNotEmpty(processInstList)) {
                Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
                List<BudgetProject> listt = new ArrayList<>();
                for (BudgetProject tmp : list) {
                    if (processInstMap.get(tmp.getId()) != null) {
                        listt.add(tmp);
                    }
                }
                if (ObjectUtil.isNotEmpty(listt)) {
                    responseResult = pageBean.get(current, pageSize, listt.size(), listt);
                }
            }
        }
        return responseResult;
    }

    //成本类型
    @PostMapping("listCost")
    public ResponseResult listCost(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object costType = paramMap.get("costType");

        QueryWrapper<SmallBudgetOut> wrapper = new QueryWrapper<SmallBudgetOut>().eq("have_display", "是");
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq("dept_id", user.getDeptId());
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like("task_code", taskCode);
        }
        if (ObjectUtil.isNotEmpty(costType)) {
            wrapper.like("cost_type", costType);
        }
        wrapper.select("distinct budget_id,project_id,name,task_code,cost_type,cost_rate,money,display_name,dept_name");
        List<SmallBudgetOut> list = smallBudgetOutService.list(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getBusinessHaveDisplay, "是").eq(ProcessInst::getProcessStatus, "完成").like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, list.stream().map(SmallBudgetOut::getBudgetId).collect(Collectors.toList())));
            if (ObjectUtil.isNotEmpty(processInstList)) {
                Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
                List<SmallBudgetOut> listt = new ArrayList<>();
                for (SmallBudgetOut tmp : list) {
                    if (processInstMap.get(tmp.getBudgetId()) != null) {
                        listt.add(tmp);
                    }
                }
                if (ObjectUtil.isNotEmpty(listt)) {
                    responseResult = pageBean.get(current, pageSize, listt.size(), listt);
                }
            }
        }
        return responseResult;
    }

}
