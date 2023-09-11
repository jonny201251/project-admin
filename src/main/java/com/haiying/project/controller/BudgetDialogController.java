package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.bean.PageBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.BudgetProjecttOutVO;
import com.haiying.project.service.BudgetOutService;
import com.haiying.project.service.BudgetProjecttService;
import com.haiying.project.service.InContractService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
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
    @Autowired
    InContractService inContractService;

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


    //付款合同-弹窗
    //项目名称，任务号，WBS编号，成本类型
    @PostMapping("listCost")
    public ResponseResult listCost(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        SysUser user = (SysUser) httpSession.getAttribute("user");

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");

        LambdaQueryWrapper<InContract> wrapper = new LambdaQueryWrapper<InContract>();
        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq(InContract::getDeptId, user.getDeptId());
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(InContract::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like(InContract::getTaskCode, taskCode);
        }
        List<InContract> list = inContractService.list(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            List<String> taskCodeList = new ArrayList<>();
            list.forEach(item -> taskCodeList.add(item.getTaskCode()));
            //
            LambdaQueryWrapper<BudgetProjectt> wrapper1 = new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是").in(BudgetProjectt::getTaskCode, taskCodeList);
            List<BudgetProjectt> list1 = budgetProjecttService.list(wrapper1);
            if (ObjectUtil.isNotEmpty(list1)) {
                List<Integer> idList = new ArrayList<>();
                Map<Integer, BudgetProjectt> map1 = new HashMap<>();
                for (BudgetProjectt tmp : list1) {
                    idList.add(tmp.getId());
                    map1.put(tmp.getId(), tmp);
                }
                //
                List<BudgetProjecttOutVO> dataList = new ArrayList<>();
                LambdaQueryWrapper<BudgetOut> wrapper2 = new LambdaQueryWrapper<BudgetOut>().in(BudgetOut::getBudgetId, idList);
                wrapper2.in(BudgetOut::getOutType, "材料及设备费", "劳务费", "技术服务费", "工程款");
                List<BudgetOut> list2 = budgetOutService.list(wrapper2);
                for (BudgetOut out : list2) {
                    BudgetProjectt b = map1.get(out.getBudgetId());
                    BudgetProjecttOutVO vo = new BudgetProjecttOutVO();
                    vo.setBudgetId(b.getId());
                    vo.setProjectType(b.getProjectType());
                    vo.setName(b.getName());
                    vo.setWbs(b.getWbs());
                    vo.setTaskCode(b.getTaskCode());
                    vo.setOutType(out.getOutType());
                    vo.setRate(out.getRate());
                    vo.setMoney(out.getMoney());
                    vo.setOutId(out.getId());
                    //
                    vo.setLoginName(b.getDisplayName());
                    vo.setDisplayName(b.getDisplayName());
                    vo.setDeptId(b.getDeptId());
                    vo.setDeptName(b.getDeptName());
                    vo.setCreateDatetime(b.getCreateDatetime());

                    dataList.add(vo);
                }
                if (ObjectUtil.isNotEmpty(dataList)) {
                    responseResult = pageBean.get(current, pageSize, dataList.size(), dataList);
                }
            }
        }
        return responseResult;
    }

}
