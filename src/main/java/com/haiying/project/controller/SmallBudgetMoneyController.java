package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.*;
import com.haiying.project.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

//一般项目预算表
@RestController
@RequestMapping("/smallBudgetMoney")
@Slf4j
public class SmallBudgetMoneyController {
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    BudgetProtectService budgetProtectService;
    @Autowired
    BudgetInService budgetInService;
    @Autowired
    SmallBudgetOutService budgetOutService;
    @Autowired
    ProjectInService projectInService;
    @Autowired
    ProjectOutService projectOutService;
    @Autowired
    OutContractService outContractService;

    Map<Integer, SmallBudgetMoney2VO> inEndMap = new ConcurrentHashMap<>();
    Map<Integer, Map<String, Integer>> inDateMapp = new ConcurrentHashMap<>();
    //key=budgetId+预计收入下的类别
    Map<String, SmallBudgetMoney4VO> a245Map = new ConcurrentHashMap<>();

    //基本信息
    @GetMapping("get")
    public synchronized Map<String, List<SmallBudgetMoney1VO>> get(Integer budgetId) {
        System.out.println(budgetId);
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
            if (project.getBaseId() == null) {
                vo.setType(project.getProjectType() + "预算表");
            } else {
                vo.setType(project.getProjectType() + "预算表(第" + project.getVersion() + "次调整)");
            }
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
    收入明细-第一次
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
        inEndMap.put(budgetId, end);

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

    //支出明细-第一次
    @GetMapping("out")
    public synchronized Map<String, List<SmallBudgetMoney2VO>> out(Integer budgetId) throws Exception {
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
                throw new Exception("收入明细和支出明细的日期不一致");
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
            SmallBudgetMoney2VO inEnd = inEndMap.get(budgetId);
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

    /*
    预算调整
        原预算额---a2
        已签合同额-a4
        截止2022年11月已收入额-a5
     */
    private void a245(BudgetProject project) {
        //
        a245Map.clear();

        Integer beforeBudgetId = project.getBeforeId();
        Integer projectId = project.getProjectId();
        String fisrtInDate = null;
        //收入
        List<BudgetIn> inList = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, beforeBudgetId));
        if (ObjectUtil.isNotEmpty(inList)) {
            //a2
            Map<String, List<BudgetIn>> inMap = inList.stream().collect(Collectors.groupingBy(BudgetIn::getInType, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<BudgetIn>> entry : inMap.entrySet()) {
                String key = entry.getKey();
                if (key.equals("其他")) {
                    key = "收入的其他";
                }
                SmallBudgetMoney4VO vo = new SmallBudgetMoney4VO();
                double a2 = 0.0;
                List<BudgetIn> budgetInList = entry.getValue();
                for (BudgetIn in : budgetInList) {
                    a2 += ofNullable(in.getMoney()).orElse(0.0);
                }
                vo.setA2(a2);
                a245Map.put(beforeBudgetId + "-" + key, vo);
            }
            //a5--项目收支.收入信息
            SmallBudgetMoney4VO vo = a245Map.get(beforeBudgetId + "-项目收入");
            //取出budgetId中的第一个日期
            for (BudgetIn budgetIn : inList) {
                budgetIn.setInDateInt(Integer.parseInt(budgetIn.getInDate().replaceAll("[年月]", "")));
            }
            inList = inList.stream().sorted(Comparator.comparing(BudgetIn::getSort).thenComparing(BudgetIn::getInDateInt)).collect(Collectors.toList());
            fisrtInDate = inList.get(0).getInDate().replaceAll("[年月]", "-") + "-31";
            List<ProjectIn> list = projectInService.list(new LambdaQueryWrapper<ProjectIn>().eq(ProjectIn::getProjectId, projectId).isNotNull(ProjectIn::getMoney2).lt(ProjectIn::getInDate, fisrtInDate));
            if (ObjectUtil.isNotEmpty(list)) {
                double a5 = 0.0;
                for (ProjectIn projectIn : list) {
                    a5 += ofNullable(projectIn.getMoney2()).orElse(0.0);
                }
                vo.setA5(a5);
            }
        }
        //支出
        List<SmallBudgetOut> outList = budgetOutService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getBudgetId, beforeBudgetId).orderByAsc(SmallBudgetOut::getSort));
        if (ObjectUtil.isNotEmpty(outList)) {
            //a4,已签合同额--没有判断 审批流程是否结束
            List<OutContract> outContractList = outContractService.list(new LambdaQueryWrapper<OutContract>().eq(OutContract::getProjectId, projectId));
            //a5,截止2022年11月已支出额
            List<ProjectOut> projectOutList = projectOutService.list(new LambdaQueryWrapper<ProjectOut>().eq(ProjectOut::getProjectId, projectId).isNotNull(ProjectOut::getMoney2).lt(ProjectOut::getOutDate, fisrtInDate));
            //分组
            Map<String, List<SmallBudgetOut>> outMap = outList.stream().collect(Collectors.groupingBy(o -> o.getCostType() + (o.getCostRate() == null ? "" : "(" + o.getCostRate() + ")"), LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<SmallBudgetOut>> entry : outMap.entrySet()) {
                String key = entry.getKey();
                SmallBudgetMoney4VO vo = new SmallBudgetMoney4VO();
                //a2
                double a2 = 0.0;
                List<SmallBudgetOut> budgetOutList = entry.getValue();
                for (SmallBudgetOut out : budgetOutList) {
                    a2 += ofNullable(out.getMoney()).orElse(0.0);
                }
                vo.setA2(a2);
                //a4--合同签署情况.付款合同
                double a4 = 0.0;
                for (OutContract tmp : outContractList) {
                    String keyy = tmp.getCostType() + (tmp.getCostRate() == null ? "" : "(" + tmp.getCostRate() + ")");
                    if (key.equals(keyy)) {
                        a4 += ofNullable(tmp.getEndMoney()).orElse(0.0);
                    }
                }
                vo.setA4(a4);
                //a5--项目收支.支出信息
                double a5 = 0.0;
                for (ProjectOut tmp : projectOutList) {
                    String keyy = tmp.getCostType() + (tmp.getCostRate() == null ? "" : "(" + tmp.getCostRate() + ")");
                    if (key.equals(keyy)) {
                        a5 += ofNullable(tmp.getMoney2()).orElse(0.0);
                    }
                }
                vo.setA5(a5);
                //
                a245Map.put(beforeBudgetId + "-" + key, vo);
            }
        }
        System.out.println();
    }

    //收入明细-调整
    @GetMapping("inModify")
    public synchronized Map<String, List<SmallBudgetMoney2VO>> inModify(Integer budgetId) {
        //
        BudgetProject project = budgetProjectService.getById(budgetId);
        a245(project);
        Integer beforeId = project.getBeforeId();

        Map<String, List<SmallBudgetMoney2VO>> map = new HashMap<>();
        List<SmallBudgetMoney2VO> list = new ArrayList<>();
        //第一个
        SmallBudgetMoney2VO first = new SmallBudgetMoney2VO();
        first.setA0("预计收入");
        first.setA1("合计");
        first.setA2("原预算额");
        first.setA3("申请调整额");
        int firstIndexEnd = 5;
        //中间两个
        List<SmallBudgetMoney2VO> middleList = new ArrayList<>();
        //最后一个
        SmallBudgetMoney2VO end = new SmallBudgetMoney2VO();
        end.setA0("预计收入累计");
        //存储
        inEndMap.put(budgetId, end);

        List<BudgetIn> inList = budgetInService.list(new LambdaQueryWrapper<BudgetIn>().eq(BudgetIn::getBudgetId, budgetId));
        if (ObjectUtil.isNotEmpty(inList)) {
            //日期：先设置，后排序
            for (BudgetIn budgetIn : inList) {
                budgetIn.setInDateInt(Integer.parseInt(budgetIn.getInDate().replaceAll("[年月]", "")));
            }
            inList = inList.stream().sorted(Comparator.comparing(BudgetIn::getSort).thenComparing(BudgetIn::getInDateInt)).collect(Collectors.toList());
            //设置first.A5
            first.setA5("截止" + inList.get(0).getInDate() + "已收入额");
            //日期设定index
            Integer index = 6;
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
                //a2,a5
                String key = entry.getKey();
                if (key.equals("其他")) {
                    key = "收入的其他";
                }
                SmallBudgetMoney4VO vo = a245Map.get(beforeId + "-" + key);
                if (vo != null) {
                    if (vo.getA2() != null && vo.getA2() > 0) {
                        middle.setA2(vo.getA2());
                    }
                    if (vo.getA5() != null && vo.getA5() > 0) {
                        middle.setA5(vo.getA5());
                    }
                    //a1需要在此处加上a5
                    middle.setA1(ofNullable((Double) middle.getA1()).orElse(0.0) + ofNullable(vo.getA5()).orElse(0.0));
                }
                //a3
                double a3 = ofNullable((Double) middle.getA1()).orElse(0.0) - ofNullable((Double) middle.getA2()).orElse(0.0);
                if (a3 > 0) {
                    middle.setA3(a3);
                }

                middleList.add(middle);
            }
            //取出middleList,计算累计，放入end
            SmallBudgetMoney2VOTmp tmpp = new SmallBudgetMoney2VOTmp();
            for (int i = 0; i < middleList.size(); i++) {
                ReflectUtil.setFieldValue(tmpp, "obj" + i, middleList.get(i));
            }
            for (int i = 1; i <= firstIndexEnd; i++) {
                double sum = 0;
                //i == 1 || i == 2 || i=3 || i=5
                if (i == 4) continue;

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

                if (i > 5) {
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

    //支出明细-调整
    @GetMapping("outModify")
    public synchronized Map<String, List<SmallBudgetMoney2VO>> outModify(Integer budgetId) {
        //
        BudgetProject project = budgetProjectService.getById(budgetId);
        Integer beforeId = project.getBeforeId();

        Map<String, List<SmallBudgetMoney2VO>> map = new HashMap<>();
        List<SmallBudgetMoney2VO> list = new ArrayList<>();
        //第一个
        SmallBudgetMoney2VO first = new SmallBudgetMoney2VO();
        first.setA0("预计支出");
        first.setA1("合计");
        first.setA2("原预算额");
        first.setA3("申请调整额");
        first.setA4("已签合同额");
        int firstIndexEnd = 5;
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
            //设置first.A5
            first.setA5("截止" + outList.get(0).getOutDate() + "已支出额");
            //日期设定index
            Integer index = 6;
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
                log.error("收入明细和支出明细的日期不一致");
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
                //a2,a4,a5
                SmallBudgetMoney4VO vo = a245Map.get(beforeId + "-" + entry.getKey());
                if (vo != null) {
                    if (vo.getA2() != null && vo.getA2() > 0) {
                        middle.setA2(vo.getA2());
                    }
                    if (vo.getA4() != null && vo.getA4() > 0) {
                        middle.setA4(vo.getA4());
                    }
                    if (vo.getA5() != null && vo.getA5() > 0) {
                        middle.setA5(vo.getA5());
                    }
                    //a1需要在此处加上a5
                    middle.setA1(ofNullable((Double) middle.getA1()).orElse(0.0) + ofNullable(vo.getA5()).orElse(0.0));
                }
                //a3
                double a3 = ofNullable((Double) middle.getA1()).orElse(0.0) - ofNullable((Double) middle.getA2()).orElse(0.0);
                if (a3 > 0) {
                    middle.setA3(a3);
                }

                middleList.add(middle);
            }
            //取出middleList,计算累计，放入end1
            SmallBudgetMoney2VOTmp tmpp = new SmallBudgetMoney2VOTmp();
            for (int i = 0; i < middleList.size(); i++) {
                ReflectUtil.setFieldValue(tmpp, "obj" + i, middleList.get(i));
            }
            for (int i = 1; i <= firstIndexEnd; i++) {
                double sum = 0;
                //i == 1 || i == 2 || i=3 || i=5
                if (i == 4) continue;

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

                if (i > 5) {
                    Object d = ReflectUtil.getFieldValue(end1, "a" + (i - 1));
                    double ddd = (double) d;
                    sum += ddd;
                }
                ReflectUtil.setFieldValue(end1, "a" + i, sum);
            }
            //end2
            SmallBudgetMoney2VO inEnd = inEndMap.get(budgetId);
            for (int i = 1; i <= 15; i++) {
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
