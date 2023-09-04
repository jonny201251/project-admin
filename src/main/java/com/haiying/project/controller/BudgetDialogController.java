package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.model.entity.BudgetProjectt;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.BudgetOut;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.BudgetProjecttService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.BudgetOutService;
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
    BudgetProjecttService budgetProjecttService;
    @Autowired
    PageBean pageBean;
    @Autowired
    BudgetOutService budgetOutService;

    //项目预算
    @PostMapping("list")
    public ResponseResult list(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");

        LambdaQueryWrapper<BudgetProjectt> wrapper = new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是");
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(BudgetProjectt::getDeptId, user.getDeptId());
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(BudgetProjectt::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(BudgetProjectt::getTaskCode, taskCode);
        }
        List<BudgetProjectt> list = budgetProjecttService.list(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getBusinessHaveDisplay, "是").eq(ProcessInst::getProcessStatus, "完成").like(ProcessInst::getPath, "budgetProjecttPath").in(ProcessInst::getBusinessId, list.stream().map(BudgetProjectt::getId).collect(Collectors.toList())));
            if (ObjectUtil.isNotEmpty(processInstList)) {
                Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
                List<BudgetProjectt> listt = new ArrayList<>();
                for (BudgetProjectt tmp : list) {
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

        QueryWrapper<BudgetOut> wrapper = new QueryWrapper<BudgetOut>().eq("have_display", "是");
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
        wrapper.select("distinct budget_id,project_id,name,task_code,wbs,cost_type,cost_rate,display_name,dept_name");
        List<BudgetOut> list = budgetOutService.list(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getBusinessHaveDisplay, "是").eq(ProcessInst::getProcessStatus, "完成").like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, list.stream().map(BudgetOut::getBudgetId).collect(Collectors.toList())));
            if (ObjectUtil.isNotEmpty(processInstList)) {
                Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
                List<BudgetOut> listt = new ArrayList<>();
                for (BudgetOut tmp : list) {
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
