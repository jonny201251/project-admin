package com.haiying.project.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.bean.WorkFlowBean;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.common.utils.ExcelListener;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.excel.*;
import com.haiying.project.service.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/initData")
@Wrapper
public class InitDataController {
    @Autowired
    SysDeptService deptService;
    @Autowired
    ProviderService providerService;
    @Autowired
    CustomerService customerService;
    @Autowired
    ProjectCodeService projectCodeService;
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    SmallProjectNoService smallProjectNoService;

    @Autowired
    BudgetProjecttService budgetProjecttService;
    @Autowired
    BudgetInnService budgetInnService;
    @Autowired
    BudgetOutService budgetOutService;
    @Autowired
    ProcessInstService processInstService;

    @Autowired
    ProjectProtectService projectProtectService;

    @Autowired
    WorkFlowBean workFlowBean;

    //60276
    @GetMapping("d")
    public boolean d(String id) {
        workFlowBean.deleteProcessInstance(id);
        return true;
    }

    //60276
    @GetMapping("d2")
    public boolean d2(String id) {
        workFlowBean.deleteDeploy(id);
        return true;
    }


    @GetMapping("a1")
    public boolean a1(String ids) {
        //保证金
        List<Integer> idList = new ArrayList<>();

        String[] arr = ids.split(",");
        for (String id : arr) {
            idList.add(Integer.parseInt(id));
        }
        List<ProjectProtect> list = projectProtectService.list(new LambdaQueryWrapper<ProjectProtect>().in(ProjectProtect::getId, idList));

        //
        List<ProcessInst> list2 = new ArrayList<>();
        for (ProjectProtect tmp : list) {
            ProcessInst p = new ProcessInst();
            p.setProcessDesignId(58);
            p.setProcessName("投标保证金(函)登记");
            p.setBusinessName(tmp.getName());
            p.setBusinessId(tmp.getId());
            p.setBusinessHaveDisplay("是");
            p.setBusinessVersion(0);
            p.setProcessStatus("完成");
            p.setDeptId(tmp.getDeptId());
            p.setDeptName(tmp.getDeptName());
            p.setPath("projectProtectPath");

            list2.add(p);

        }
        processInstService.saveBatch(list2);
        //
        Map<Integer, Integer> map = new HashMap<>();
        for (ProcessInst tmp : list2) {
            map.put(tmp.getBusinessId(), tmp.getId());
        }
        //
        for (ProjectProtect tmp : list) {
            tmp.setProcessInstId(map.get(tmp.getId()));
        }

        projectProtectService.updateBatchById(list);
        return true;
    }

    @GetMapping("a2")
    public boolean a2(String ids) {
        //保证金
        List<Integer> idList = new ArrayList<>();

        String[] arr = ids.split(",");
        for (String id : arr) {
            idList.add(Integer.parseInt(id));
        }
        List<BudgetProjectt> list = budgetProjecttService.list(new LambdaQueryWrapper<BudgetProjectt>().in(BudgetProjectt::getId, idList));

        //
        List<ProcessInst> list2 = new ArrayList<>();
        for (BudgetProjectt tmp : list) {
            ProcessInst p = new ProcessInst();
            p.setProcessDesignId(47);
            p.setProcessName("一般项目预算");
            p.setBusinessName(tmp.getName());
            p.setBusinessId(tmp.getId());
            p.setBusinessHaveDisplay("是");
            p.setBusinessVersion(0);
            p.setProcessStatus("完成");
            p.setDeptId(tmp.getDeptId());
            p.setDeptName(tmp.getDeptName());
            p.setPath("budgetProjecttPath");

            list2.add(p);

        }
        processInstService.saveBatch(list2);
        //
        Map<Integer, Integer> map = new HashMap<>();
        for (ProcessInst tmp : list2) {
            map.put(tmp.getBusinessId(), tmp.getId());
        }
        //
        for (BudgetProjectt tmp : list) {
            tmp.setProcessInstId(map.get(tmp.getId()));
        }

        budgetProjecttService.updateBatchById(list);
        return true;
    }


    @GetMapping("provider")
    public boolean provider() throws Exception {
        List<SysDept> deptList = deptService.list();
        Map<String, Integer> deptMap = new HashMap<>();
        for (SysDept tmp : deptList) {
            deptMap.put(tmp.getName(), tmp.getId());
        }


        InputStream inputStream = new FileInputStream("d:/a/供方2.xls");
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<ProviderExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet0 = EasyExcel.readSheet(0).head(ProviderExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet0);
        //获取数据
        List<ProviderExcel> list = listener.getData();


        for (ProviderExcel tmp : list) {
            if (deptMap.get(tmp.getDeptName()) == null) {
                if (tmp.getDeptName().equals("第三事业部")) {
                    tmp.setDeptName("天津第三事业部");
                }
                if (tmp.getDeptName().equals("第五事业部")) {
                    tmp.setDeptName("天津第五事业部");
                }
                if (tmp.getDeptName().equals("风机研发中心")) {
                    tmp.setDeptName("节能环保事业部");
                }
                if (tmp.getDeptName().equals("纪检法审")) {
                    tmp.setDeptName("纪监法审部");
                }
                if (tmp.getDeptName().equals("海南分公司")) {
                    tmp.setDeptName("海南事业部");
                }
                if (tmp.getDeptName().equals("天津（第五）") || tmp.getDeptName().equals("天津（5）")) {
                    tmp.setDeptName("天津第五事业部");
                }
                if (tmp.getDeptName().equals("资产与信息化")) {
                    tmp.setDeptName("资产与信息化部");
                }
                if (tmp.getDeptName().equals("机电系统集成") || tmp.getDeptName().equals("第八事业部")) {
                    tmp.setDeptName("机电系统集成事业部");
                }
                if (tmp.getDeptName().equals("天津（第三）")) {
                    tmp.setDeptName("天津第三事业部");
                }
                if (tmp.getDeptName().equals("国际工程")) {
                    tmp.setDeptName("国际工程事业部");
                }
                if (tmp.getDeptName().equals("军民融合") || tmp.getDeptName().equals("JM融合")) {
                    tmp.setDeptName("军民融合部");
                }
                if (tmp.getDeptName().equals("动力运营事业部") || tmp.getDeptName().equals("动力运营（经营调度中心）")) {
                    tmp.setDeptName("经营调度中心");
                }
                if (tmp.getDeptName().equals("动力运营事业部（供水）")) {
                    tmp.setDeptName("供水中心");
                }
            }
        }

        for (ProviderExcel tmp : list) {
            if (deptMap.get(tmp.getDeptName()) == null) {
                System.out.println(tmp.getDeptName());
            } else {
                Integer deptId = deptMap.get(tmp.getDeptName());
                tmp.setDeptId(deptId);
            }
        }

        List<Provider> list1 = new ArrayList<>();
        List<Provider> list2 = new ArrayList<>();

        List<Provider> ll = providerService.list();
        Map<String, Provider> mm = new HashMap<>();
        for (Provider provider : ll) {
            Provider provider1 = mm.get(provider.getName());
            if (provider1 == null) {
                mm.put(provider.getName(), provider);
            } else {
                System.out.println(provider.getName());
            }
        }

        for (ProviderExcel tmp : list) {
            Provider p1 = new Provider();
            Provider p2 = mm.get(tmp.getName());
            if (p2 != null) {
                if (ObjectUtil.isNotEmpty(tmp.getScore())) {
                    int score = Integer.parseInt(tmp.getScore());
                    p2.setScore(score);
                    if (score >= 40) {
                        p2.setResult("优良");
                    } else if (score >= 30) {
                        p2.setResult("合格");
                    } else {
                        p2.setResult("不合格");
                    }
                }
                list2.add(p2);
            } else {
                if (ObjectUtil.isNotEmpty(tmp.getScore())) {
                    int score = Integer.parseInt(tmp.getScore());
                    p1.setScore(score);
                    if (score >= 40) {
                        p1.setResult("优良");
                    } else if (score >= 30) {
                        p1.setResult("合格");
                    } else {
                        p1.setResult("不合格");
                    }
                } else {
                    p1.setResult("合格");
                }
                p1.setName(tmp.getName());
                p1.setDeptName(tmp.getDeptName());
                p1.setDeptId(tmp.getDeptId());
                if (ObjectUtil.isEmpty(tmp.getRemark())) {
                    p1.setRemark(tmp.getPs() + "," + tmp.getRemark());
                }

                list1.add(p1);
            }


        }
        System.out.println(list.size());
        System.out.println(list1.size());
        System.out.println(list2.size());

        providerService.saveBatch(list1);
        providerService.updateBatchById(list2);

        return true;
    }

    @GetMapping("customer2")
    public boolean customer2() throws Exception {
        List<SysDept> deptList = deptService.list();
        Map<String, Integer> deptMap = new HashMap<>();
        for (SysDept tmp : deptList) {
            deptMap.put(tmp.getName(), tmp.getId());
        }


        InputStream inputStream = new FileInputStream("d:/a/客户2.xls");
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<CustomerExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet0 = EasyExcel.readSheet(1).head(CustomerExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet0);
        //获取数据
        List<CustomerExcel> list = listener.getData();
        System.out.println();


        List<Customer> ll = new ArrayList<>();
        for (CustomerExcel tmp : list) {
            if (ObjectUtil.isNotEmpty(tmp.getName())) {
                Customer p = new Customer();
                p.setName(tmp.getName());
                p.setHaveDisplay("是");
                p.setVersion(0);
                p.setResult("优秀");
                ll.add(p);
            }

        }
        System.out.println();
        customerService.saveBatch(ll);

        return true;
    }

    @GetMapping("customer")
    public boolean customer() throws Exception {
        List<SysDept> deptList = deptService.list();
        Map<String, Integer> deptMap = new HashMap<>();
        for (SysDept tmp : deptList) {
            deptMap.put(tmp.getName(), tmp.getId());
        }


        InputStream inputStream = new FileInputStream("d:/a/客户2.xls");
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<CustomerExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet0 = EasyExcel.readSheet(0).head(CustomerExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet0);
        //获取数据
        List<CustomerExcel> list = listener.getData();
        System.out.println();

        for (CustomerExcel tmp : list) {
            if (deptMap.get(tmp.getDeptName()) == null) {
                if (tmp.getDeptName().equals("动力运营")) {
                    tmp.setDeptName("经营调度中心");
                }
                if (tmp.getDeptName().equals("风机研发中心")) {
                    tmp.setDeptName("节能环保事业部");
                }
                if (tmp.getDeptName().equals("互联网运营中心") || tmp.getDeptName().equals("资产与信息化")) {
                    tmp.setDeptName("资产与信息化部");
                }
                if (tmp.getDeptName().equals("天津事业部")) {
                    tmp.setDeptName("天津第三事业部");
                    tmp.setRemark("天津事业部");
                }
            }
        }

        for (CustomerExcel tmp : list) {
            if (deptMap.get(tmp.getDeptName()) == null) {
                System.out.println(tmp.getDeptName());
            } else {
                Integer deptId = deptMap.get(tmp.getDeptName());
                tmp.setDeptId(deptId);
            }
        }

        List<Customer> ll = new ArrayList<>();
        for (CustomerExcel tmp : list) {
            Customer p = new Customer();
            p.setName(tmp.getName());
            p.setDeptName(tmp.getDeptName());
            p.setDeptId(tmp.getDeptId());
            p.setRemark(tmp.getRemark());
            p.setHaveDisplay("是");
            p.setVersion(0);
            p.setResult(tmp.getResult().replaceAll("级", ""));
            ll.add(p);
        }

        customerService.saveBatch(ll);

        return true;
    }

    @GetMapping("code")
    public boolean code() throws Exception {
        List<SysDept> deptList = deptService.list();
        Map<String, Integer> deptMap = new HashMap<>();
        for (SysDept tmp : deptList) {
            deptMap.put(tmp.getName(), tmp.getId());
        }


        InputStream inputStream = new FileInputStream("d:/a/任务号.xls");
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<CodeExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet0 = EasyExcel.readSheet(0).head(CodeExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet0);
        //获取数据
        List<CodeExcel> list = listener.getData();


        System.out.println();
        for (CodeExcel tmp : list) {
            if (deptMap.get(tmp.getDeptName()) == null) {
                if (tmp.getDeptName().equals("动力运营事业部")) {
                    tmp.setDeptName("经营调度中心");
                }
            }
        }

        for (CodeExcel tmp : list) {
            if (deptMap.get(tmp.getDeptName()) == null) {
                System.out.println(tmp.getDeptName());
            } else {
                Integer deptId = deptMap.get(tmp.getDeptName());
                tmp.setDeptId(deptId);
            }
        }

        List<ProjectCode> ll = new ArrayList<>();
        for (CodeExcel tmp : list) {
            ProjectCode p = new ProjectCode();
            p.setProjectName(tmp.getName());
            p.setDeptName(tmp.getDeptName());
            p.setDeptId(tmp.getDeptId());
            p.setRemark(tmp.getAa());
            p.setTaskCode(tmp.getTaskCode());
            p.setProjectMoney(tmp.getMoney());
            p.setCustomerName(tmp.getC());
            p.setProviderName(tmp.getP());
            p.setYear(2023);
            p.setStatus("已使用");
            ll.add(p);
        }

        projectCodeService.saveBatch(ll);

        return true;
    }

    @GetMapping("project")
    public boolean project() throws Exception {
        List<SysDept> deptList = deptService.list();
        Map<String, Integer> deptMap = new HashMap<>();
        for (SysDept tmp : deptList) {
            deptMap.put(tmp.getName(), tmp.getId());
        }

        List<Provider> providerList = providerService.list(new LambdaQueryWrapper<Provider>().eq(Provider::getResult, "合格"));
        Map<String, Integer> providerMap = new HashMap<>();
        for (Provider tmp : providerList) {
            providerMap.put(tmp.getName(), tmp.getId());
        }

        List<Customer> customerList = customerService.list(new LambdaQueryWrapper<Customer>().in(Customer::getResult, Arrays.asList("优秀", "良好", "一般")));
        Map<String, Integer> customerMap = new HashMap<>();
        for (Customer tmp : customerList) {
            customerMap.put(tmp.getName(), tmp.getId());
        }


        InputStream inputStream = new FileInputStream("d:/a/项目信息和预算4.xls");
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<ProjectExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet0 = EasyExcel.readSheet(0).head(ProjectExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet0);
        //获取数据
        List<ProjectExcel> list = listener.getData();

        //
        for (ProjectExcel tmp : list) {
            if (tmp.getDeptName().equals("第三")) {
                tmp.setDeptName("天津第三事业部");
            } else if (tmp.getDeptName().equals("第五")) {
                tmp.setDeptName("天津第五事业部");
            } else if (tmp.getDeptName().equals("动力工程")) {
                tmp.setDeptName("动力工程事业部");
            } else if (tmp.getDeptName().equals("国际工程")) {
                tmp.setDeptName("国际工程事业部");
            } else if (tmp.getDeptName().equals("海南")) {
                tmp.setDeptName("海南事业部");
            } else if (tmp.getDeptName().equals("机电")) {
                tmp.setDeptName("机电系统集成事业部");
            } else if (tmp.getDeptName().equals("节能环保")) {
                tmp.setDeptName("节能环保事业部");
            } else if (tmp.getDeptName().equals("系统运维")) {
                tmp.setDeptName("系统运维事业部");
            } else if (tmp.getDeptName().equals("智慧产业")) {
                tmp.setDeptName("智慧产业事业部");
            } else if (tmp.getDeptName().equals("市场")) {
                tmp.setDeptName("市场部");
            }
        }
        //设置部门、客户、供方
        for (ProjectExcel tmp : list) {
            if (deptMap.get(tmp.getDeptName()) == null) {
                System.out.println(tmp.getDeptName());
            } else {
                Integer deptId = deptMap.get(tmp.getDeptName());
                tmp.setDeptId(deptId);
            }

            Integer customerId = customerMap.get(tmp.getCustomerName());
            tmp.setCustomerId(customerId);

            Integer providerId = providerMap.get(tmp.getProviderName());
            tmp.setProviderId(providerId);
        }
        //插入项目中
        Set<String> set = new HashSet<>();
        List<SmallProject> list1 = new ArrayList<>();
        List<BigProject> list2 = new ArrayList<>();
        List<SmallProjectNo> list3 = new ArrayList<>();

        Map<String, SmallProject> map1 = new HashMap<>();
        Map<String, BigProject> map2 = new HashMap<>();
        Map<String, SmallProjectNo> map3 = new HashMap<>();

        for (ProjectExcel tmp : list) {
            set.add(tmp.getTaskCode());
            if (tmp.getProjectType().equals("一般")) {
                SmallProject p = new SmallProject();
                p.setDeptId(tmp.getDeptId());
                p.setDeptName(tmp.getDeptName());
                p.setProjectType("一般项目");
                p.setTaskCode(tmp.getTaskCode());
                p.setName(tmp.getName());
                if ("自营".equals(tmp.getProperty())) {
                    p.setProperty("一类");
                } else if ("半自营".equals(tmp.getProperty())) {
                    p.setProperty("二类");
                } else if ("合作".equals(tmp.getProperty())) {
                    p.setProperty("三类");
                }
                p.setCustomerName(tmp.getCustomerName());
                p.setCustomerId(tmp.getCustomerId());
                p.setProviderName(tmp.getProviderName());
                p.setProviderId(tmp.getProviderId());

                p.setHaveDisplay("是");
                p.setVersion(0);
                p.setProcessInstId(0);
                list1.add(p);
            } else if (tmp.getProjectType().equals("重大")) {
                BigProject p = new BigProject();
                p.setDeptId(tmp.getDeptId());
                p.setDeptName(tmp.getDeptName());
                p.setProjectType("重大项目");
                p.setTaskCode(tmp.getTaskCode());
                p.setName(tmp.getName());
                if ("自营".equals(tmp.getProperty())) {
                    p.setProperty("一类");
                } else if ("半自营".equals(tmp.getProperty())) {
                    p.setProperty("二类");
                } else if ("合作".equals(tmp.getProperty())) {
                    p.setProperty("三类");
                }
                p.setCustomerName(tmp.getCustomerName());
                p.setCustomerId(tmp.getCustomerId());
                p.setProviderName(tmp.getProviderName());
                p.setProviderId(tmp.getProviderId());

                p.setHaveDisplay("是");
                p.setVersion(0);
                p.setProcessInstId(0);
                list2.add(p);
            } else if (tmp.getProjectType().equals("非")) {
                SmallProjectNo p = new SmallProjectNo();
                p.setDeptId(tmp.getDeptId());
                p.setDeptName(tmp.getDeptName());
                p.setProjectType("一般项目非");
                p.setTaskCode(tmp.getTaskCode());
                p.setName(tmp.getName());
                if ("自营".equals(tmp.getProperty())) {
                    p.setProperty("一类");
                } else if ("半自营".equals(tmp.getProperty())) {
                    p.setProperty("二类");
                } else if ("合作".equals(tmp.getProperty())) {
                    p.setProperty("三类");
                }

                list3.add(p);
            }
        }

        System.out.println(set.size());
        System.out.println(list1.size() + list2.size() + list3.size());
        //
        smallProjectService.saveBatch(list1);
        bigProjectService.saveBatch(list2);
        smallProjectNoService.saveBatch(list3);

        for (SmallProject tmp : list1) {
            map1.put(tmp.getTaskCode(), tmp);
        }
        for (BigProject tmp : list2) {
            map2.put(tmp.getTaskCode(), tmp);
        }
        for (SmallProjectNo tmp : list3) {
            map3.put(tmp.getTaskCode(), tmp);
        }
        //
        List<BudgetProjectt> l = new ArrayList<>();
        Map<String, BudgetProjectt> m = new HashMap<>();
        for (ProjectExcel tmp : list) {
            SmallProject smallProject = null;
            BigProject bigProject = null;
            SmallProjectNo smallProjectNo = null;
            if (tmp.getProjectType().equals("一般")) {
                smallProject = map1.get(tmp.getTaskCode());
            } else if (tmp.getProjectType().equals("重大")) {
                bigProject = map2.get(tmp.getTaskCode());
            } else if (tmp.getProjectType().equals("非")) {
                smallProjectNo = map3.get(tmp.getTaskCode());
            }
            BudgetProjectt b = new BudgetProjectt();
            b.setHaveDisplay("是");
            b.setVersion(0);
            b.setProcessInstId(0);
            b.setDeptId(tmp.getDeptId());
            b.setDeptName(tmp.getDeptName());
            if (tmp.getProjectType().equals("一般")) {
                b.setProjectId(smallProject.getId());
                b.setProjectType(smallProject.getProjectType());
                b.setName(smallProject.getName());
                b.setTaskCode(smallProject.getTaskCode());
                b.setProperty(smallProject.getProperty());
                b.setCustomerId(smallProject.getCustomerId());
                b.setCustomerName(smallProject.getCustomerName());
            } else if (tmp.getProjectType().equals("重大")) {
                b.setProjectId(bigProject.getId());
                b.setProjectType(bigProject.getProjectType());
                b.setName(bigProject.getName());
                b.setTaskCode(bigProject.getTaskCode());
                b.setProperty(bigProject.getProperty());
                b.setCustomerId(bigProject.getCustomerId());
                b.setCustomerName(bigProject.getCustomerName());
            } else if (tmp.getProjectType().equals("非")) {
                b.setProjectId(smallProjectNo.getId());
                b.setProjectType(smallProjectNo.getProjectType());
                b.setName(smallProjectNo.getName());
                b.setTaskCode(smallProjectNo.getTaskCode());
                b.setProperty(smallProjectNo.getProperty());
            }

            b.setContractCode(tmp.getContractCode());
            b.setContractMoney(tmp.getContractMoney());
            b.setProjectRate(tmp.getProjectRate());
            if (ObjectUtil.isNotEmpty(tmp.getTotalCost()) && tmp.getTotalCost() > 0) {
                b.setTotalCost(tmp.getTotalCost());
            }

            m.put(tmp.getTaskCode(), b);
            l.add(b);
        }
        budgetProjecttService.saveBatch(l);
        System.out.println(l.size());

        List<BudgetOut> ll = new ArrayList<>();
        for (ProjectExcel tmp : list) {
            BudgetProjectt b = m.get(tmp.getTaskCode());
            //a
            if (ObjectUtil.isNotEmpty(tmp.getA1())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("材料及设备费");
                out.setRate("13%");
                out.setMoney(tmp.getA1());
                ll.add(out);
            }
            if (ObjectUtil.isNotEmpty(tmp.getA2())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("材料及设备费");
                out.setRate("3%");
                out.setMoney(tmp.getA2());
                ll.add(out);
            }
            if (ObjectUtil.isNotEmpty(tmp.getA3())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("材料及设备费");
                out.setRate("1%");
                out.setMoney(tmp.getA3());
                ll.add(out);
            }
            if (ObjectUtil.isNotEmpty(tmp.getA4())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("材料及设备费");
                out.setRate("0%");
                out.setMoney(tmp.getA4());
                ll.add(out);
            }
            //b
            if (ObjectUtil.isNotEmpty(tmp.getB1())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("技术服务费");
                out.setRate("6%");
                out.setMoney(tmp.getB1());
                ll.add(out);
            }
            if (ObjectUtil.isNotEmpty(tmp.getB2())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("技术服务费");
                out.setRate("3%");
                out.setMoney(tmp.getB1());
                ll.add(out);
            }
            //c
            if (ObjectUtil.isNotEmpty(tmp.getC1())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("劳务费");
                out.setRate("3%");
                out.setMoney(tmp.getC1());
                ll.add(out);
            }
            if (ObjectUtil.isNotEmpty(tmp.getC2())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("劳务费");
                out.setRate("1%");
                out.setMoney(tmp.getC2());
                ll.add(out);
            }
            if (ObjectUtil.isNotEmpty(tmp.getC3())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("劳务费");
                out.setRate("0%");
                out.setMoney(tmp.getC3());
                ll.add(out);
            }
            //d
            if (ObjectUtil.isNotEmpty(tmp.getD1())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("工程款");
                out.setRate("9%");
                out.setMoney(tmp.getD1());
                ll.add(out);
            }
            if (ObjectUtil.isNotEmpty(tmp.getD2())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("工程款");
                out.setRate("3%");
                out.setMoney(tmp.getD2());
                ll.add(out);
            }
            //e
            if (ObjectUtil.isNotEmpty(tmp.getE())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("税费");
                out.setMoney(tmp.getE());
                ll.add(out);
            }
            //f
            if (ObjectUtil.isNotEmpty(tmp.getF())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("交易服务费");
                out.setMoney(tmp.getF());
                ll.add(out);
            }
            //g
            if (ObjectUtil.isNotEmpty(tmp.getG())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("差旅费");
                out.setMoney(tmp.getG());
                ll.add(out);
            }
            //h
            if (ObjectUtil.isNotEmpty(tmp.getH())) {
                BudgetOut out = new BudgetOut();
                out.setBudgetId(b.getId());
                out.setProjectId(b.getProjectId());
                out.setProjectType(b.getProjectType());
                out.setOutType("交通费");
                out.setMoney(tmp.getH());
                ll.add(out);
            }
            //j1-j9
            Double sum = 0.0;
            sum = ofNullable(tmp.getJ1()).orElse(0.0) +
                    ofNullable(tmp.getJ2()).orElse(0.0) +
                    ofNullable(tmp.getJ3()).orElse(0.0) +
                    ofNullable(tmp.getJ4()).orElse(0.0) +
                    ofNullable(tmp.getJ5()).orElse(0.0) +
                    ofNullable(tmp.getJ6()).orElse(0.0) +
                    ofNullable(tmp.getJ7()).orElse(0.0) +
                    ofNullable(tmp.getJ8()).orElse(0.0) +
                    ofNullable(tmp.getJ9()).orElse(0.0);
            BudgetOut out = new BudgetOut();
            out.setBudgetId(b.getId());
            out.setProjectId(b.getProjectId());
            out.setProjectType(b.getProjectType());
            out.setOutType("其他");
            out.setMoney(sum);
            if (sum > 0) {
                ll.add(out);
                List<String> sb = new ArrayList<>();
                if (ObjectUtil.isNotEmpty(tmp.getJ1())) {
                    sb.add("其他-招待费：" + tmp.getJ1());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ2())) {
                    sb.add("其他-专家费用：" + tmp.getJ2());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ3())) {
                    sb.add("其他-锁证费：" + tmp.getJ3());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ4())) {
                    sb.add("其他-资金成本：" + tmp.getJ4());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ5())) {
                    sb.add("其他-其他1：" + tmp.getJ5());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ6())) {
                    sb.add("其他-其他2：" + tmp.getJ6());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ7())) {
                    sb.add("其他-其他3：" + tmp.getJ7());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ8())) {
                    sb.add("其他-其他4：" + tmp.getJ8());
                }
                if (ObjectUtil.isNotEmpty(tmp.getJ9())) {
                    sb.add("其他-其他5：" + tmp.getJ9());
                }

                b.setRemark(Strings.join(sb, '，'));
            }
        }

        budgetOutService.saveBatch(ll);

        budgetProjecttService.updateBatchById(l);

        List<BudgetInn> inList = new ArrayList<>();

        for (BudgetProjectt tmp : l) {
            BudgetInn in = new BudgetInn();
            in.setBudgetId(tmp.getId());
            in.setProjectId(tmp.getProjectId());
            in.setProjectType(tmp.getProjectType());
            in.setInType("项目收入");
            in.setMoney(tmp.getContractMoney());

            inList.add(in);
        }


        budgetInnService.saveBatch(inList);

        return true;
    }


    @GetMapping("project2")
    public boolean project2() {
        List<BudgetInn> inList = new ArrayList<>();
        List<BudgetProjectt> list = budgetProjecttService.list(new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getProcessInstId, 0).ne(BudgetProjectt::getDeptName, "节能环保事业部"));
        for (BudgetProjectt tmp : list) {
            if (ObjectUtil.isEmpty(tmp.getContractMoney())) {
                System.out.println(tmp.getTaskCode());
            } else {
                BudgetInn in = new BudgetInn();
                in.setBudgetId(tmp.getId());
                in.setProjectId(tmp.getProjectId());
                in.setProjectType(tmp.getProjectType());
                in.setInType("项目收入");
                in.setMoney(tmp.getContractMoney());

                inList.add(in);
            }
        }
        budgetInnService.saveBatch(inList);
        return true;
    }

    @GetMapping("project3")
    public boolean project3() {
        List<BudgetProjectt> list = budgetProjecttService.list(new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getProcessInstId, 0));
        for (BudgetProjectt tmp : list) {
            List<BudgetOut> list1 = budgetOutService.list(new LambdaQueryWrapper<BudgetOut>().eq(BudgetOut::getBudgetId, tmp.getId()));
            if (ObjectUtil.isNotEmpty(list1)) {
                Double sum = 0.0d;
                for (BudgetOut out : list1) {
                    sum += ofNullable(out.getMoney()).orElse(0.0);
                }
//                System.out.println(tmp.getTaskCode() + "," + sum + "," + tmp.getTotalCost());
                if (!sum.equals(tmp.getTotalCost())) {
                    System.out.println(tmp.getId() + "," + tmp.getTaskCode() + "," + sum + "," + tmp.getTotalCost());
                }
            }
        }

        return true;
    }

    @GetMapping("process")
    public boolean process() {
        List<ProcessInst> instList1 = new ArrayList<>();
        List<ProcessInst> instList2 = new ArrayList<>();
        List<ProcessInst> instList3 = new ArrayList<>();

        List<SmallProject> list1 = smallProjectService.list(new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getProcessInstId, 0));
        List<BigProject> list2 = bigProjectService.list(new LambdaQueryWrapper<BigProject>().eq(BigProject::getProcessInstId, 0));
        List<BudgetProjectt> list3 = budgetProjecttService.list(new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getProcessInstId, 0));

        for (SmallProject tmp : list1) {
            ProcessInst p = new ProcessInst();
            p.setProcessDesignId(70);
            p.setProcessName("一般项目立项");
            p.setBusinessName(tmp.getName());
            p.setBusinessId(tmp.getId());
            p.setBusinessHaveDisplay("是");
            p.setBusinessVersion(0);
            p.setProcessStatus("完成");
            p.setDeptId(tmp.getDeptId());
            p.setDeptName(tmp.getDeptName());
            p.setPath("smallProjectPath");

            processInstService.save(p);
            tmp.setProcessInstId(p.getId());
        }
        smallProjectService.updateBatchById(list1);


        for (BigProject tmp : list2) {
            ProcessInst p = new ProcessInst();
            p.setProcessDesignId(69);
            p.setProcessName("重大项目评估");
            p.setBusinessName(tmp.getName());
            p.setBusinessId(tmp.getId());
            p.setBusinessHaveDisplay("是");
            p.setBusinessVersion(0);
            p.setProcessStatus("完成");
            p.setDeptId(tmp.getDeptId());
            p.setDeptName(tmp.getDeptName());
            p.setPath("bigProjectPath");

            processInstService.save(p);
            tmp.setProcessInstId(p.getId());
        }
        bigProjectService.updateBatchById(list2);

        for (BudgetProjectt tmp : list3) {
            ProcessInst p = new ProcessInst();
            p.setBusinessName(tmp.getName());
            p.setBusinessId(tmp.getId());
            p.setBusinessHaveDisplay("是");
            p.setBusinessVersion(0);
            p.setProcessStatus("完成");
            p.setDeptId(tmp.getDeptId());
            p.setDeptName(tmp.getDeptName());
            if (tmp.getProjectType().equals("重大项目")) {
                p.setProcessDesignId(48);
                p.setProcessName("重大项目预算");
            } else {
                p.setProcessDesignId(47);
                p.setProcessName("一般项目预算");
            }
            p.setPath("budgetProjecttPath");

            processInstService.save(p);
            tmp.setProcessInstId(p.getId());
        }
        budgetProjecttService.updateBatchById(list3);


        return true;
    }


    @GetMapping("user")
    public boolean user() throws Exception {
        InputStream inputStream = new FileInputStream("g:/人员名单.xls");
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<UserExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet0 = EasyExcel.readSheet(0).head(UserExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet0);
        //获取数据
        List<UserExcel> list = listener.getData();

        List<String> ll = new ArrayList<>();
        Set<String> ss = new LinkedHashSet<>();
        for (UserExcel tmp : list) {
//            System.out.println(PinyinUtil.getPinyin(tmp.getName()).replaceAll("\\s+", ""));
            ss.add(tmp.getDeptName().trim());
        }

        for (String s : ss) {
            System.out.println(s);
        }
        return true;
    }

}
