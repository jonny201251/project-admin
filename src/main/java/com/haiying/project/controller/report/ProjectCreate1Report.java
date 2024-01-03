package com.haiying.project.controller.report;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.model.entity.BigProject;
import com.haiying.project.model.entity.BudgetProjectt;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.vo.ProjectCreate1VO;
import com.haiying.project.model.vo.ProjectCreate2VO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;

////项目立项-汇总1
@RestController
@RequestMapping("/projectCreate1Report")
public class ProjectCreate1Report {
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    BudgetProjecttService budgetProjecttService;

    @GetMapping("get")
    public synchronized Map<String, List<ProjectCreate1VO>> get(Integer year3) {
        Integer year1 = year3 - 2;
        Integer year2 = year3 - 1;

        Map<String, List<ProjectCreate1VO>> map = new HashMap<>();
        List<ProjectCreate1VO> list = new ArrayList<>();

        List<ProjectCreate2VO> listt = new ArrayList<>();
        //
        //.apply("substring(task_code,8,2)={0}", shortYear))
        //
        List<ProcessInst> list1 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessStatus, "完成").in(ProcessInst::getPath, Arrays.asList("smallProjectPath", "bigProjectPath")).likeLeft(ProcessInst::getEndDatetime, year1));
        List<ProcessInst> list2 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessStatus, "完成").in(ProcessInst::getPath, Arrays.asList("smallProjectPath", "bigProjectPath")).likeLeft(ProcessInst::getEndDatetime, year2));
        List<String> taskCodeList1 = new ArrayList<>();
        List<Integer> idList11 = new ArrayList<>();
        List<Integer> idList12 = new ArrayList<>();
        List<String> taskCodeList2 = new ArrayList<>();
        List<Integer> idList21 = new ArrayList<>();
        List<Integer> idList22 = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list1)) {
            for (ProcessInst item : list1) {
                if ("smallProjectPath".equals(item.getPath())) {
                    idList11.add(item.getBusinessId());
                } else {
                    idList12.add(item.getBusinessId());
                }
            }
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            for (ProcessInst item : list2) {
                if ("smallProjectPath".equals(item.getPath())) {
                    idList21.add(item.getBusinessId());
                } else {
                    idList22.add(item.getBusinessId());
                }
            }
        }
        List<SmallProject> list11 = null;
        LambdaQueryWrapper<SmallProject> wrapper11 = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHaveDisplay, "是");
        if (idList11.size() > 0) {
            wrapper11.in(SmallProject::getId, idList11);
            list11 = smallProjectService.list(wrapper11);
        }
        List<BigProject> list12 = null;
        LambdaQueryWrapper<BigProject> wrapper12 = new LambdaQueryWrapper<BigProject>().eq(BigProject::getHaveDisplay, "是");
        if (idList12.size() > 0) {
            wrapper12.in(BigProject::getId, idList12);
            list12 = bigProjectService.list(wrapper12);
        }
        List<SmallProject> list21 = null;
        LambdaQueryWrapper<SmallProject> wrapper21 = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHaveDisplay, "是");
        if (idList21.size() > 0) {
            wrapper21.in(SmallProject::getId, idList21);
            list21 = smallProjectService.list(wrapper21);
        }
        List<BigProject> list22 = null;
        LambdaQueryWrapper<BigProject> wrapper22 = new LambdaQueryWrapper<BigProject>().eq(BigProject::getHaveDisplay, "是");
        if (idList22.size() > 0) {
            wrapper22.in(BigProject::getId, idList22);
            list22 = bigProjectService.list(wrapper22);
        }


        if (ObjectUtil.isNotEmpty(list11)) {
            list11.forEach(item -> taskCodeList1.add(item.getTaskCode()));
        }
        if (ObjectUtil.isNotEmpty(list12)) {
            list12.forEach(item -> taskCodeList1.add(item.getTaskCode()));
        }
        if (ObjectUtil.isNotEmpty(list21)) {
            list21.forEach(item -> taskCodeList2.add(item.getTaskCode()));
        }
        if (ObjectUtil.isNotEmpty(list22)) {
            list22.forEach(item -> taskCodeList2.add(item.getTaskCode()));
        }

        List<BudgetProjectt> budgetList1 = null;
        LambdaQueryWrapper<BudgetProjectt> wrapper1 = new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是");
        if (taskCodeList1.size() > 0) {
            wrapper1.in(BudgetProjectt::getTaskCode, taskCodeList1);
            budgetList1 = budgetProjecttService.list(wrapper1);
        }
        List<BudgetProjectt> budgetList2 = null;
        LambdaQueryWrapper<BudgetProjectt> wrapper2 = new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是");
        if (taskCodeList2.size() > 0) {
            wrapper2.in(BudgetProjectt::getTaskCode, taskCodeList2);
            budgetList2 = budgetProjecttService.list(wrapper2);
        }


        Map<String, Integer> budgetMap1 = new HashMap<>();
        Map<String, Integer> budgetMap2 = new HashMap<>();

        if (ObjectUtil.isNotEmpty(budgetList1)) {
            for (BudgetProjectt item : budgetList1) {
                budgetMap1.merge(item.getDeptName(), 1, Integer::sum);
            }
        }
        if (ObjectUtil.isNotEmpty(budgetList2)) {
            for (BudgetProjectt item : budgetList2) {
                budgetMap2.merge(item.getDeptName(), 1, Integer::sum);
            }
        }
        //
        List<ProcessInst> list3 = processInstService.list(new LambdaQueryWrapper<ProcessInst>().eq(ProcessInst::getProcessStatus, "完成").in(ProcessInst::getPath, Arrays.asList("smallProjectPath", "bigProjectPath")).likeRight(ProcessInst::getEndDatetime, year3));
        Map<String, LocalDateTime> map3 = new HashMap<>();
        List<String> taskCodeList3 = new ArrayList<>();
        List<Integer> idList3 = new ArrayList<>();
        List<Integer> idList31 = new ArrayList<>();
        List<Integer> idList32 = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list3)) {
            for (ProcessInst item : list3) {
                map3.put(item.getPath() + "," + item.getBusinessId(), item.getEndDatetime());
                idList3.add(item.getBusinessId());
                if ("smallProjectPath".equals(item.getPath())) {
                    idList31.add(item.getBusinessId());
                } else {
                    idList32.add(item.getBusinessId());
                }
            }
        }

        List<SmallProject> list31 = null;
        LambdaQueryWrapper<SmallProject> wrapper31 = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHaveDisplay, "是");
        if (idList31.size() > 0) {
            wrapper31.in(SmallProject::getId, idList31);
            list31 = smallProjectService.list(wrapper31);
        }
        List<BigProject> list32 = null;
        LambdaQueryWrapper<BigProject> wrapper32 = new LambdaQueryWrapper<BigProject>().eq(BigProject::getHaveDisplay, "是");
        if (idList32.size() > 0) {
            wrapper32.in(BigProject::getId, idList32);
            list32 = bigProjectService.list(wrapper32);
        }

        //
        if (ObjectUtil.isNotEmpty(list31)) {
            for (SmallProject item : list31) {
                taskCodeList3.add(item.getTaskCode());

                ProjectCreate2VO vo = new ProjectCreate2VO();

                vo.setDeptId(item.getDeptId());
                vo.setDeptName(item.getDeptName());
                vo.setName(vo.getName());
                vo.setTaskCode(vo.getTaskCode());
                vo.setProperty(vo.getProperty());
                String str2 = item.getHaveBid();
                vo.setBidStatus("是".equals(str2) ? "投标" : "直签");
                vo.setEndDatetime(map3.get("smallProjectPath," + item.getId()));

                listt.add(vo);

            }
        }
        if (ObjectUtil.isNotEmpty(list32)) {
            for (BigProject item : list32) {
                taskCodeList3.add(item.getTaskCode());

                ProjectCreate2VO vo = new ProjectCreate2VO();

                vo.setDeptId(item.getDeptId());
                vo.setDeptName(item.getDeptName());
                vo.setName(vo.getName());
                vo.setProperty(vo.getProperty());
                vo.setBidStatus("投标");
                vo.setEndDatetime(map3.get("bigProjectPath," + item.getId()));

                listt.add(vo);
            }
        }
        //
        if (ObjectUtil.isNotEmpty(listt)) {
            Collections.sort(listt, Comparator.comparing(ProjectCreate2VO::getDeptId));
            //a1-12,b1-b3,c1-c2,d1-d3,f1-f2
            List<BudgetProjectt> budgetList3 = budgetProjecttService.list(new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是").in(BudgetProjectt::getTaskCode, taskCodeList3));
            Map<String, String> budgetMap3 = new HashMap<>();
            if (ObjectUtil.isNotEmpty(budgetList3)) {
                budgetList3.forEach(item -> budgetMap3.put(item.getTaskCode(), "是"));
            }
            Map<String, ProjectCreate1VO> vo1Map = new TreeMap<>();
            for (ProjectCreate2VO vo2 : listt) {
                ProjectCreate1VO vo1 = vo1Map.get(vo2.getDeptName());
                if (vo1 == null) {
                    vo1 = new ProjectCreate1VO();
                    vo1.setDeptName(vo2.getDeptName());

                    vo1Map.put(vo2.getDeptName(), vo1);
                }
                Integer month = vo2.getEndDatetime().getMonthValue();
                Integer value = (Integer) ReflectUtil.getFieldValue(vo1, "a" + month);
                ReflectUtil.setFieldValue(vo1, "a" + month, ofNullable(value).orElse(0) + 1);

                if ("三类".equals(vo2.getProperty())) {
                    Integer b3 = vo1.getB3();
                    vo1.setB3(ofNullable(b3).orElse(0) + 1);

                    String str = budgetMap3.get(vo2.getTaskCode());
                    if (str != null) {
                        Integer d3 = vo1.getD3();
                        vo1.setD3(ofNullable(d3).orElse(0) + 1);
                    }
                } else if ("二类".equals(vo2.getProperty())) {
                    Integer b2 = vo1.getB2();
                    vo1.setB2(ofNullable(b2).orElse(0) + 1);

                    String str = budgetMap3.get(vo2.getTaskCode());
                    if (str != null) {
                        Integer d2 = vo1.getD2();
                        vo1.setD2(ofNullable(d2).orElse(0) + 1);
                    }
                } else {
                    Integer b1 = vo1.getB1();
                    vo1.setB1(ofNullable(b1).orElse(0) + 1);

                    String str = budgetMap3.get(vo2.getTaskCode());
                    if (str != null) {
                        Integer d1 = vo1.getD1();
                        vo1.setD1(ofNullable(d1).orElse(0) + 1);
                    }
                }

                if ("投标".equals(vo2.getBidStatus())) {
                    Integer c1 = vo1.getC1();
                    vo1.setC1(ofNullable(c1).orElse(0) + 1);

                    String str = budgetMap3.get(vo2.getTaskCode());
                    if (str != null) {
                        Integer f1 = vo1.getF1();
                        vo1.setF1(ofNullable(f1).orElse(0) + 1);
                    }
                } else {
                    Integer c2 = vo1.getC2();
                    vo1.setC2(ofNullable(c2).orElse(0) + 1);

                    String str = budgetMap3.get(vo2.getTaskCode());
                    if (str != null) {
                        Integer f2 = vo1.getF2();
                        vo1.setF2(ofNullable(f2).orElse(0) + 1);
                    }
                }
            }
            //a,d,e1-e3,e,g1,f3-f4,f,g2
            Collection<ProjectCreate1VO> values = vo1Map.values();
            int i = 1;
            if (values.size() > 0) {
                for (ProjectCreate1VO v : values) {
                    v.setNum(i++);
                    int aSum = 0, dSum = 0;
                    for (int j = 1; j <= 12; j++) {
                        Integer value = (Integer) ReflectUtil.getFieldValue(v, "a" + j);
                        aSum += ofNullable(value).orElse(0);
                    }
                    v.setA(aSum);

                    for (int j = 1; j <= 3; j++) {
                        Integer value = (Integer) ReflectUtil.getFieldValue(v, "d" + j);
                        dSum += ofNullable(value).orElse(0);
                    }
                    v.setD(dSum);

                    if (ObjectUtil.isAllNotEmpty(v.getD1(), v.getB1())) {
                        double result = (double) v.getD1() / v.getB1();
                        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                        String formattedResult = decimalFormat.format(result);
                        v.setE1(formattedResult);
                    }
                    if (ObjectUtil.isAllNotEmpty(v.getD2(), v.getB2())) {
                        double result = (double) v.getD2() / v.getB2();
                        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                        String formattedResult = decimalFormat.format(result);
                        v.setE2(formattedResult);
                    }
                    if (ObjectUtil.isAllNotEmpty(v.getD3(), v.getB3())) {
                        double result = (double) v.getD3() / v.getB3();
                        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                        String formattedResult = decimalFormat.format(result);
                        v.setE3(formattedResult);
                    }

                    if (ObjectUtil.isAllNotEmpty(v.getD(), v.getC1())) {
                        double result = (double) v.getD() / v.getC1();
                        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                        String formattedResult = decimalFormat.format(result);
                        v.setE(formattedResult);
                        v.setG1(formattedResult);
                    }

                    int f = 0;
                    Integer f3 = budgetMap1.get(v.getDeptName());
                    Integer f4 = budgetMap2.get(v.getDeptName());
                    if (f3 != null) {
                        v.setF3(f3);
                        f += f3;
                    }
                    if (f4 != null) {
                        v.setF4(f4);
                        f += f4;
                    }
                    if (f > 0) {
                        v.setF(f);

                        double result = (double) f / v.getA();
                        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                        String formattedResult = decimalFormat.format(result);
                        v.setG2(formattedResult);
                    }

                    list.add(v);
                }
            }

            //合计
            if (list.size() > 0) {
                ProjectCreate1VO end = new ProjectCreate1VO();
                end.setDeptName("合计");
                //a1-a12,a,b1-b3,c1-c2,d1-d3,d,f1-f4,f
                List<String> l = Arrays.asList("a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "a10", "a11", "a12", "a", "b1", "b2", "b3", "c1", "c2", "d1", "d2", "d3", "d", "f1", "f2", "f3", "f4", "f");
                for (ProjectCreate1VO v : list) {
                    for (String name : l) {
                        Object aa = ReflectUtil.getFieldValue(end, name);
                        Object bb = ReflectUtil.getFieldValue(v, name);
                        int sum = 0;
                        if (aa != null) {
                            sum += ofNullable((Integer) aa).orElse(0);
                        }
                        if (bb != null) {
                            sum += ofNullable((Integer) bb).orElse(0);
                        }

                        if (sum > 0) {
                            ReflectUtil.setFieldValue(end, name, sum);
                        }
                    }
                }
                //e1-e3,e,g1,g2
                if (ObjectUtil.isAllNotEmpty(end.getD1(), end.getB1())) {
                    double result = (double) end.getD1() / end.getB1();
                    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                    String formattedResult = decimalFormat.format(result);
                    end.setE1(formattedResult);
                }
                if (ObjectUtil.isAllNotEmpty(end.getD2(), end.getB2())) {
                    double result = (double) end.getD2() / end.getB2();
                    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                    String formattedResult = decimalFormat.format(result);
                    end.setE2(formattedResult);
                }
                if (ObjectUtil.isAllNotEmpty(end.getD3(), end.getB3())) {
                    double result = (double) end.getD3() / end.getB3();
                    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                    String formattedResult = decimalFormat.format(result);
                    end.setE3(formattedResult);
                }

                if (ObjectUtil.isAllNotEmpty(end.getD(), end.getC1())) {
                    double result = (double) end.getD() / end.getC1();
                    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                    String formattedResult = decimalFormat.format(result);
                    end.setE(formattedResult);
                    end.setG1(formattedResult);
                }

                if (ObjectUtil.isNotEmpty(end.getF())) {
                    double result = (double) end.getF() / end.getA();
                    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                    String formattedResult = decimalFormat.format(result);
                    end.setG2(formattedResult);
                }

                list.add(end);
            }

        }

        map.put("data", list);
        return map;
    }
}
