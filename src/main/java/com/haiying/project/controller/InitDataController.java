package com.haiying.project.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.common.utils.ExcelListener;
import com.haiying.project.model.entity.Customer;
import com.haiying.project.model.entity.ProjectCode;
import com.haiying.project.model.entity.Provider;
import com.haiying.project.model.entity.SysDept;
import com.haiying.project.model.excel.CodeExcel;
import com.haiying.project.model.excel.CustomerExcel;
import com.haiying.project.model.excel.ProviderExcel;
import com.haiying.project.service.CustomerService;
import com.haiying.project.service.ProjectCodeService;
import com.haiying.project.service.ProviderService;
import com.haiying.project.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("provider")
    public boolean provider() throws Exception {
        List<SysDept> deptList = deptService.list();
        Map<String, Integer> deptMap = new HashMap<>();
        for (SysDept tmp : deptList) {
            deptMap.put(tmp.getName(), tmp.getId());
        }


        InputStream inputStream = new FileInputStream("d:/a/供方.xlsx");
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

        List<Provider> ll = new ArrayList<>();
        for (ProviderExcel tmp : list) {
            Provider p = new Provider();
            p.setName(tmp.getName());
            p.setDeptName(tmp.getDeptName());
            p.setDeptId(tmp.getDeptId());
            if (ObjectUtil.isEmpty(tmp.getRemark())) {
                p.setRemark(tmp.getPs() + "," + tmp.getScore() + "分");
            }

            ll.add(p);
        }

        providerService.saveBatch(ll);

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
            if(ObjectUtil.isNotEmpty(tmp.getName())){
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
            p.setResult(tmp.getResult().replaceAll("级",""));
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
}
