package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.SmallBudgetMoney1VO;
import com.haiying.project.model.vo.SmallBudgetMoney2VO;
import com.haiying.project.model.vo.SmallBudgetMoney2VOTmp;
import com.haiying.project.model.vo.SmallBudgetMoney3VO;
import com.haiying.project.service.BudgetInService;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.BudgetProtectService;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    @Autowired
    SmallBudgetOutService budgetOutService;

    Map<Integer, SmallBudgetMoney2VO> endMap = new ConcurrentHashMap<>();
    Map<Integer, Map<String, Integer>> inDateMapp = new ConcurrentHashMap<>();

    @PostMapping("list")
    @Wrapper
    public IPage<SmallProject> list(@RequestBody Map<String, Object> paramMap) {
        return null;
    }

    //基本信息
    @GetMapping("get")
    public synchronized Map<String, List<SmallBudgetMoney1VO>> get(Integer budgetId) {
        Map<String, List<SmallBudgetMoney1VO>> map = new HashMap<>();
        List<SmallBudgetMoney1VO> list = new ArrayList<>();
        SmallBudgetMoney1VO vo = new SmallBudgetMoney1VO();
        BudgetProject project = budgetProjectService.getOne(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getId, budgetId));
        if (project != null) {
            vo.setDeptName(project.getDeptName());
            vo.setName(project.getName());
            vo.setProjectDisplayName(project.getProjectDisplayName());
            vo.setProperty(project.getProperty());
            vo.setTaskCode(project.getTaskCode());
            vo.setType(project.getType()+"预算表");
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

    /*
        预算表-收入信息
        第一行
        中间两行
        最后一行
     */
    @GetMapping("in")
    public synchronized Map<String, List<SmallBudgetMoney2VO>> in(Integer budgetId) {
        Map<String, List<SmallBudgetMoney2VO>> map = new HashMap<>();
        List<SmallBudgetMoney2VO> list = new ArrayList<>();
        //第一个
        SmallBudgetMoney2VO first = new SmallBudgetMoney2VO();
        first.setA0("预计收入");
        first.setA1("合计");
        int firstIndexEnd = 1;
        //中间两个
        List<SmallBudgetMoney2VO> middleList = new ArrayList<>();
        //最后一个
        SmallBudgetMoney2VO end = new SmallBudgetMoney2VO();
        end.setA0("预计收入累计");
        //存储
        endMap.put(budgetId, end);

        List<BudgetIn> inList = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, budgetId));
        if (ObjectUtil.isNotEmpty(inList)) {
            //日期：先设置，后排序
            for (BudgetIn budgetIn : inList) {
                budgetIn.setInDateInt(Integer.parseInt(budgetIn.getInDate().replaceAll("[年月]", "")));
            }
            inList = inList.stream().sorted(Comparator.comparing(BudgetIn::getSort).thenComparing(BudgetIn::getInDateInt)).collect(Collectors.toList());
            //日期设定index
            Integer index = 2;
            HashMap<String, Integer> dateMap = new LinkedHashMap<>();
            for (BudgetIn budgetIn : inList) {
                Integer i = dateMap.get(budgetIn.getInDate());
                if (i == null) {
                    dateMap.put(budgetIn.getInDate(), index);
                    index++;
                }
            }
            firstIndexEnd += dateMap.size();
            //
            inDateMapp.put(budgetId, dateMap);
            //first
            for (Map.Entry<String, Integer> entry : dateMap.entrySet()) {
                ReflectUtil.setFieldValue(first, "a" + entry.getValue(), entry.getKey());
            }
            //组装List<SmallBudgetMoney3VO>
            List<SmallBudgetMoney3VO> list3 = new ArrayList<>();
            for (int i = 0; i < inList.size(); i++) {
                BudgetIn tmp = inList.get(i);
                SmallBudgetMoney3VO vo3 = new SmallBudgetMoney3VO();
                vo3.setIoMonth(tmp.getInDate());
                vo3.setType(tmp.getInType());
                vo3.setMoney(tmp.getMoney());
                vo3.setIndex(dateMap.get(tmp.getInDate()));
                list3.add(vo3);
            }
            //分组
            Map<String, List<SmallBudgetMoney3VO>> inMap = list3.stream().collect(Collectors.groupingBy(SmallBudgetMoney3VO::getType, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<SmallBudgetMoney3VO>> entry : inMap.entrySet()) {
                SmallBudgetMoney2VO middle = new SmallBudgetMoney2VO();
                middle.setA0(entry.getKey());
                double a1 = 0.0;
                //
                List<SmallBudgetMoney3VO> tmpList = entry.getValue();
                for (int i = 0; i < tmpList.size(); i++) {
                    SmallBudgetMoney3VO tmp = tmpList.get(i);
                    //a1
                    a1 += ofNullable(tmp.getMoney()).orElse(0.0);
                    //middle
                    ReflectUtil.setFieldValue(middle, "a" + tmp.getIndex(), tmp.getMoney());
                }
                middle.setA1(a1);
                middleList.add(middle);
            }
            //取出middleList,计算累计，放入end
            SmallBudgetMoney2VOTmp tmpp = new SmallBudgetMoney2VOTmp();
            for (int i = 0; i < middleList.size(); i++) {
                ReflectUtil.setFieldValue(tmpp, "obj" + i, middleList.get(i));
            }
            for (int i = 1; i <= firstIndexEnd; i++) {
                double sum = 0;
                //i == 1 || i == 2
                for (int j = 0; j <= 19; j++) {
                    Object obj = ReflectUtil.getFieldValue(tmpp, "obj" + j);
                    if (ObjectUtil.isNotEmpty(obj)) {
                        Object d = ReflectUtil.getFieldValue(obj, "a" + i);
                        if (ObjectUtil.isNotEmpty(d)) {
                            double dd = (double) d;
                            sum += dd;
                        }
                    }
                }

                if (i > 2) {
                    Object d = ReflectUtil.getFieldValue(end, "a" + (i - 1));
                    double ddd = (double) d;
                    sum += ddd;
                }
                ReflectUtil.setFieldValue(end, "a" + i, sum);
            }
        }
        //
        list.add(first);
        list.addAll(middleList);
        list.add(end);
        map.put("data", list);
        return map;
    }

    //支出信息
    @GetMapping("out")
    public synchronized Map<String, List<SmallBudgetMoney2VO>> out(Integer budgetId) {
        Map<String, List<SmallBudgetMoney2VO>> map = new HashMap<>();
        List<SmallBudgetMoney2VO> list = new ArrayList<>();
        //第一个
        SmallBudgetMoney2VO first = new SmallBudgetMoney2VO();
        first.setA0("预计支出");
        first.setA1("合计");
        int firstIndexEnd = 1;
        //中间多个
        List<SmallBudgetMoney2VO> middleList = new ArrayList<>();
        //最后两个
        SmallBudgetMoney2VO end1 = new SmallBudgetMoney2VO();
        end1.setA0("预计累计支出");
        SmallBudgetMoney2VO end2 = new SmallBudgetMoney2VO();
        end2.setA0("预计收支累计结余");

        List<SmallBudgetOut> outList = budgetOutService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, budgetId));
        if (ObjectUtil.isNotEmpty(outList)) {
            //日期:先设置，后排序
            for (SmallBudgetOut budgetOut : outList) {
                budgetOut.setOutDateInt(Integer.parseInt(budgetOut.getOutDate().replaceAll("[年月]", "")));
            }
            outList = outList.stream().sorted(Comparator.comparing(SmallBudgetOut::getOutDateInt).thenComparing(SmallBudgetOut::getSort)).collect(Collectors.toList());
            //日期设定index
            Integer index = 2;
            HashMap<String, Integer> dateMap = new LinkedHashMap<>();
            for (SmallBudgetOut budgetOut : outList) {
                Integer i = dateMap.get(budgetOut.getOutDate());
                if (i == null) {
                    dateMap.put(budgetOut.getOutDate(), index);
                    index++;
                }
            }
            firstIndexEnd += dateMap.size();
            //收入和支出的日期 校验
            String outDateString = String.join(",", dateMap.keySet());
            Map<String, Integer> inDateMap = inDateMapp.get(budgetId);
            String inDateString = String.join(",", inDateMap.keySet());
            if (!outDateString.equals(inDateString)) {
                throw new PageTipException("收入明细和支出明细的日期不一致");
            }
            //first
            for (Map.Entry<String, Integer> entry : dateMap.entrySet()) {
                ReflectUtil.setFieldValue(first, "a" + entry.getValue(), entry.getKey());
            }
            //组装List<SmallBudgetMoney3VO>
            List<SmallBudgetMoney3VO> list3 = new ArrayList<>();
            for (int i = 0; i < outList.size(); i++) {
                SmallBudgetOut tmp = outList.get(i);
                SmallBudgetMoney3VO vo3 = new SmallBudgetMoney3VO();
                vo3.setIoMonth(tmp.getOutDate());
                vo3.setType(tmp.getCostType());
                vo3.setRate(tmp.getCostRate());
                vo3.setMoney(tmp.getMoney());
                vo3.setIndex(dateMap.get(tmp.getOutDate()));
                list3.add(vo3);
            }
            //分组
            Map<String, List<SmallBudgetMoney3VO>> outMap = list3.stream().collect(Collectors.groupingBy(o -> o.getType() + (o.getRate() == null ? "" : "(" + o.getRate() + ")"), LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<SmallBudgetMoney3VO>> entry : outMap.entrySet()) {
                SmallBudgetMoney2VO middle = new SmallBudgetMoney2VO();
                middle.setA0(entry.getKey());
                double a1 = 0.0;
                //
                List<SmallBudgetMoney3VO> tmpList = entry.getValue();
                for (int i = 0; i < tmpList.size(); i++) {
                    SmallBudgetMoney3VO tmp = tmpList.get(i);
                    //a1
                    a1 += ofNullable(tmp.getMoney()).orElse(0.0);
                    //middle
                    ReflectUtil.setFieldValue(middle, "a" + tmp.getIndex(), tmp.getMoney());
                }
                middle.setA1(a1);
                middleList.add(middle);
            }
            //取出middleList,计算累计，放入end1
            SmallBudgetMoney2VOTmp tmpp = new SmallBudgetMoney2VOTmp();
            for (int i = 0; i < middleList.size(); i++) {
                ReflectUtil.setFieldValue(tmpp, "obj" + i, middleList.get(i));
            }
            for (int i = 1; i <= firstIndexEnd; i++) {
                double sum = 0;
                //i == 1 || i == 2
                for (int j = 0; j <= 19; j++) {
                    Object obj = ReflectUtil.getFieldValue(tmpp, "obj" + j);
                    if (ObjectUtil.isNotEmpty(obj)) {
                        Object d = ReflectUtil.getFieldValue(obj, "a" + i);
                        if (ObjectUtil.isNotEmpty(d)) {
                            double dd = (double) d;
                            sum += dd;
                        }
                    }
                }

                if (i > 2) {
                    Object d = ReflectUtil.getFieldValue(end1, "a" + (i - 1));
                    double ddd = (double) d;
                    sum += ddd;
                }
                ReflectUtil.setFieldValue(end1, "a" + i, sum);
            }
            //end2
            SmallBudgetMoney2VO inEnd = endMap.get(budgetId);
            for (int i = 1; i <= 11; i++) {
                Object inValue = ReflectUtil.getFieldValue(inEnd, "a" + i);
                Object outValue = ReflectUtil.getFieldValue(end1, "a" + i);
                if (ObjectUtil.isNotEmpty(inValue) && ObjectUtil.isNotEmpty(outValue)) {
                    double inValuee = (double) inValue;
                    double outValuee = (double) outValue;
                    ReflectUtil.setFieldValue(end2, "a" + i, inValuee - outValuee);
                }
            }
        }
        //
        list.add(first);
        list.addAll(middleList);
        list.add(end1);
        list.add(end2);
        map.put("data", list);
        return map;
    }
}
