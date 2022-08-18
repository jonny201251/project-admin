package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BudgetIn;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.BudgetProtect;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.vo.SmallBudgetMoney1VO;
import com.haiying.project.model.vo.SmallBudgetMoney2VO;
import com.haiying.project.service.BudgetInService;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.BudgetProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

//一般项目预算表
@RestController
@RequestMapping("/smallBudgetMoney")
public class SmallBudgetMoneyController {
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    BudgetProtectService budgetProtectService;
    @Autowired
    BudgetInService budgetInService;

    @PostMapping("list")
    @Wrapper
    public IPage<SmallProject> list(@RequestBody Map<String, Object> paramMap) {
        return null;
    }

    //项目基本信息
    @GetMapping("get")
    public synchronized Map<String, List<SmallBudgetMoney1VO>> get(Integer budgetId) {
        Map<String, List<SmallBudgetMoney1VO>> map = new HashMap<>();
        List<SmallBudgetMoney1VO> list = new ArrayList<>();
        SmallBudgetMoney1VO vo = new SmallBudgetMoney1VO();
        BudgetProject project = budgetProjectService.getOne(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getHaveDisplay, "是").eq(BudgetProject::getId, budgetId));
        if (project != null) {
            vo.setDeptName(project.getDeptName());
            vo.setName(project.getName());
            vo.setProjectDisplayName(project.getProjectDisplayName());
            vo.setProperty(project.getProperty());
            vo.setTaskCode(project.getTaskCode());
            vo.setContractMoney(project.getContractMoney());
            vo.setTotalCost(project.getTotalCost());
            vo.setStartDate(project.getStartDate().toString());
            vo.setEndDate(project.getEndDate().toString());
            vo.setProtectRate(project.getProtectRate());
            vo.setProjectRate(project.getProjectRate());
            vo.setEndMoney(project.getEndMoney());
            vo.setInChangeMoney(project.getInChangeMoney());
            vo.setOutChangeMoney(project.getOutChangeMoney());
            vo.setInvoiceRate(project.getInvoiceRate());
            vo.setRemark(project.getRemark());

            List<BudgetProtect> protectList = budgetProtectService.list(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, project.getId()));
            if (ObjectUtil.isNotEmpty(protectList)) {
                for (BudgetProtect protect : protectList) {
                    if (protect.getName().equals("投标保证金")) {
                        vo.setStyle1(protect.getStyle());
                        vo.setMoney1(protect.getMoney());
                        vo.setOutDate1(protect.getOutDate());
                        vo.setInDate1(protect.getInDate());
                    } else if (protect.getName().equals("履约保证金")) {
                        vo.setStyle2(protect.getStyle());
                        vo.setMoney2(protect.getMoney());
                        vo.setOutDate2(protect.getOutDate());
                        vo.setInDate2(protect.getInDate());
                    } else if (protect.getName().equals("预付款担保")) {
                        vo.setStyle3(protect.getStyle());
                        vo.setMoney3(protect.getMoney());
                        vo.setOutDate3(protect.getOutDate());
                        vo.setInDate3(protect.getInDate());
                    } else if (protect.getName().equals("其他担保")) {
                        vo.setStyle4(protect.getStyle());
                        vo.setMoney4(protect.getMoney());
                        vo.setOutDate4(protect.getOutDate());
                        vo.setInDate4(protect.getInDate());
                    }
                }
            }
        }

        list.add(vo);
        map.put("data", list);
        return map;
    }

    //收入信息
    /*
        1.生成 第一个,中间几个,最后一个 对象
        2.分别对 第一个,中间几个,最后一个
     */
    @GetMapping("in")
    public synchronized Map<String, List<SmallBudgetMoney2VO>> in(Integer budgetId) {
        Map<String, List<SmallBudgetMoney2VO>> map = new HashMap<>();
        List<SmallBudgetMoney2VO> list = new ArrayList<>();
        //第一个
        SmallBudgetMoney2VO first = new SmallBudgetMoney2VO();
        first.setA0("预计收入");
        first.setA1("合计");
        //中间几个
        List<SmallBudgetMoney2VO> middleList = new ArrayList<>();
        //最后一个
        SmallBudgetMoney2VO end = new SmallBudgetMoney2VO();
        end.setA0("预计收入累计");

        List<BudgetIn> inList = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, budgetId).orderByAsc(BudgetIn::getSort));
        if (ObjectUtil.isNotEmpty(inList)) {
            //分组
            Map<String, List<BudgetIn>> inMap = inList.stream().collect(Collectors.groupingBy(BudgetIn::getInType, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<BudgetIn>> entry : inMap.entrySet()) {
                SmallBudgetMoney2VO middle = new SmallBudgetMoney2VO();
                middle.setA0(entry.getKey());
                double a1 = 0.0;
                //
                List<BudgetIn> tmpList = entry.getValue();
                for (int i = 0; i < tmpList.size(); i++) {
                    int index = i + 2;
                    BudgetIn tmp = tmpList.get(i);
                    //first
                    ReflectUtil.setFieldValue(first, "a" + index, tmp.getInDate());
                    //a1
                    a1 += ofNullable(tmp.getMoney()).orElse(0.0);
                    //middle
                    ReflectUtil.setFieldValue(middle, "a" + index, a1);
                }
                middle.setA1(a1);
                middleList.add(middle);
            }
            //取出middleList,计算累计，放入end
            for (SmallBudgetMoney2VO vo : middleList) {
                for (int i = 1; i <= 11; i++) {
                    Object d1 = ReflectUtil.getFieldValue(vo, "a" + i);
                    if (ObjectUtil.isNotEmpty(d1)) {
                        double d12 = (double) d1;
                        Object d2 = ReflectUtil.getFieldValue(end, "a" + i);
                        if (ObjectUtil.isNotEmpty(d2)) {
                            double d2_ = (double) d2;
                            d12 += d2_;
                        }
                        ReflectUtil.setFieldValue(end, "a" + i, d12);
                    }
                }
            }
            //
            list.add(first);
            list.addAll(middleList);
            list.add(end);
        }
        map.put("data", list);
        return map;
    }

    //支出信息
    @GetMapping("out")
    public synchronized Map<String, List<SmallBudgetMoney2VO>> out(Integer budgetId) {
        Map<String, List<SmallBudgetMoney2VO>> map = new HashMap<>();
        List<SmallBudgetMoney2VO> list = new ArrayList<>();

        return map;
    }
}
