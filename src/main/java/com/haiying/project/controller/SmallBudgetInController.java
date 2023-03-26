package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.BudgetInVO;
import com.haiying.project.service.BudgetInService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一般和重大项目预算-预计收入 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/smallBudgetIn")
@Wrapper
public class SmallBudgetInController {
    @Autowired
    BudgetInService budgetInService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<BudgetIn> list(@RequestBody Map<String, Object> paramMap) {
        SysUser user = (SysUser) httpSession.getAttribute("user");

        QueryWrapper<BudgetIn> wrapper = new QueryWrapper<BudgetIn>().eq("have_display", "是");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object inType = paramMap.get("inType");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper.like("task_code", taskCode);
        }
        if (ObjectUtil.isNotEmpty(inType)) {
            wrapper.eq("in_type", inType);
        }

        if (!user.getDeptName().equals("综合计划部")) {
            wrapper.eq("display_name", user.getDisplayName());
        }
        wrapper.select("distinct budget_id,name,task_code,in_type,version,display_name,dept_name").orderByAsc("budget_id,sort");
        IPage<BudgetIn> page = budgetInService.page(new Page<>(current, pageSize), wrapper);
        List<BudgetIn> recordList = page.getRecords();
        if (ObjectUtil.isNotEmpty(recordList)) {
            List<Integer> idList = recordList.stream().map(BudgetIn::getBudgetId).collect(Collectors.toList());
            List<ProcessInst> processInstList = processInstService.list(new LambdaQueryWrapper<ProcessInst>().like(ProcessInst::getPath, "BudgetRunPath").in(ProcessInst::getBusinessId, idList));
            Map<Integer, ProcessInst> processInstMap = processInstList.stream().collect(Collectors.toMap(ProcessInst::getBusinessId, v -> v));
            recordList.forEach(record -> record.setProcessInst(processInstMap.get(record.getBudgetId())));
        }
        return page;
    }


    @PostMapping("add")
    public boolean add(@RequestBody BudgetInVO vo) {
        //判断是否重复添加
        List<BudgetIn> ll = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, vo.getBudgetId()).eq(BudgetIn::getInType, vo.getInType()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号和收入类型   已存在");
        }

        double count = 1;
        List<BudgetIn> list = vo.getList();
        for (BudgetIn budgetIn : list) {
            if (ObjectUtil.isEmpty(vo.getSort())) {
                budgetIn.setSort(count++);
            } else {
                budgetIn.setSort(vo.getSort());
            }
            budgetIn.setHaveDisplay(vo.getHaveDisplay());
            budgetIn.setVersion(vo.getVersion());
            budgetIn.setBudgetId(vo.getBudgetId());
            budgetIn.setProjectId(vo.getProjectId());
            budgetIn.setProjectType(vo.getProjectType());
            budgetIn.setName(vo.getName());
            budgetIn.setTaskCode(vo.getTaskCode());
            budgetIn.setInType(vo.getInType());
            budgetIn.setRemark(vo.getRemark());
            budgetIn.setLoginName(vo.getLoginName());
            budgetIn.setDisplayName(vo.getDisplayName());
            budgetIn.setDeptId(vo.getDeptId());
            budgetIn.setDeptName(vo.getDeptName());
            budgetIn.setCreateDatetime(vo.getCreateDatetime());
        }
        return budgetInService.saveBatch(list);
    }

    @GetMapping("get")
    public BudgetInVO get(Integer budgetId, String inType) {
        BudgetInVO vo = new BudgetInVO();
        List<BudgetIn> list = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, budgetId).eq(BudgetIn::getInType, inType));
        BeanUtils.copyProperties(list.get(0), vo);
        vo.setList(list);
        return vo;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody BudgetInVO vo) {
        return budgetInService.edit(vo);
    }
}
