package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.SmallBudgetOutVO;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般项目预算-预计支出 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/smallBudgetOut")
@Wrapper
public class SmallBudgetOutController {
    @Autowired
    SmallBudgetOutService smallBudgetOutService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<SmallBudgetOut> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        QueryWrapper<SmallBudgetOut> wrapper = new QueryWrapper<SmallBudgetOut>().eq("have_display", "是");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object costType = paramMap.get("costType");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like("task_code", taskCode);
        }
        if (ObjectUtil.isNotEmpty(costType)) {
            wrapper.eq("cost_type", costType);
        }

        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq("display_name", user.getDisplayName());
        }
        wrapper.select("distinct budget_id,project_id,name,task_code,wbs,cost_type,cost_rate,version,display_name,dept_name,create_datetime").orderByAsc("budget_id,sort");
        IPage<SmallBudgetOut> page = smallBudgetOutService.page(new Page<>(current, pageSize), wrapper);
        List<SmallBudgetOut> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<Integer> idList = recordList.stream().map(SmallBudgetOut::getBudgetId).collect(Collectors.toList());
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, idList));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getBudgetId())));
        }
        return page;
    }


    @PostMapping("add")
    public boolean add(@RequestBody SmallBudgetOutVO vo) {
        return smallBudgetOutService.add(vo);
    }

    @GetMapping("get")
    public SmallBudgetOutVO get(Integer budgetId, String costType, String costRate) {
        SmallBudgetOutVO vo = new SmallBudgetOutVO();
        LambdaQueryWrapper<SmallBudgetOut> wrapper = new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, budgetId).eq(SmallBudgetOut::getCostType, costType);
        if (ObjectUtil.isNotEmpty(costRate)) {
            wrapper.eq(SmallBudgetOut::getCostRate, costRate);
        }
        List<SmallBudgetOut> list = smallBudgetOutService.list(wrapper);
        BeanUtils.copyProperties(list.get(0), vo);
        vo.setList(list);
        return vo;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody SmallBudgetOutVO vo) {
        return smallBudgetOutService.edit(vo);
    }
}
