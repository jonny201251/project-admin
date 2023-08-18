package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.utils.ExcelListener;
import com.haiying.project.mapper.InContractMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.excel.InContractExcel;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.InOutVO;
import com.haiying.project.service.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 收款合同 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
@Service
public class InContractServiceImpl extends ServiceImpl<InContractMapper, InContract> implements InContractService {
    @Autowired
    FormFileService formFileService;
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    SmallBudgetOutService smallBudgetOutService;
    @Autowired
    SysDeptService sysDeptService;


    private LocalDate getDate(String str) {
        LocalDate parse = null;
        if (ObjectUtil.isNotEmpty(str)) {
            String[] arr = str.split("[-|/]");
            if (arr.length == 3) {
                String year = arr[0];
                String month = arr[1].length() == 1 ? "0" + arr[1] : arr[1];
                String day = arr[2].length() == 1 ? "0" + arr[2] : arr[2];
                parse = LocalDate.parse(year + "-" + month + "-" + day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        }
        return parse;
    }

    @Override
    @SneakyThrows
    public boolean upload(InputStream inputStream) {
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<InContractExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet = EasyExcel.readSheet(0).head(InContractExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet);
        //获取数据
        List<InContractExcel> list = listener.getData();

        //
        List<SysDept> deptList = sysDeptService.list();
        Map<String, SysDept> deptMap = new HashMap<>();
        for (SysDept dept : deptList) {
            deptMap.put(dept.getName(), dept);
        }

        if (ObjectUtil.isNotEmpty(list)) {
            List<InContract> resultList = new ArrayList<>();
            for (InContractExcel tmp : list) {
                InContract obj = new InContract();
                obj.setName(tmp.getContractName());
                obj.setLoginName(tmp.getDisplayName());

                obj.setContractCode(tmp.getContractCode());
                obj.setContractName(tmp.getContractName());
                obj.setCustomerName(tmp.getCustomerName());
                obj.setContractMoney(tmp.getContractMoney());
                obj.setTaskCode(tmp.getTaskCode());
                obj.setProperty(tmp.getProperty());
                obj.setContractType(tmp.getContractType());
                obj.setContractLevel(tmp.getContractLevel());
                obj.setPrintType(tmp.getPrintType());
                obj.setPrintDate(getDate(tmp.getPrintDate()));
                obj.setDisplayName(tmp.getDisplayName());
                obj.setLocation(tmp.getLocation());
                obj.setStartDate(getDate(tmp.getStartDate()));
                obj.setEndDate(getDate(tmp.getEndDate()));
                obj.setExpectDate(getDate(tmp.getExpectDate()));
                obj.setDocumentDate(getDate(tmp.getDocumentDate()));
                obj.setRemark(tmp.getRemark());
                //设置部门
                String deptName = tmp.getDeptName();
                if ("第五事业部".equals(deptName)) {
                    SysDept sysDept = deptMap.get("天津第五事业部");
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                } else if ("系统运维".equals(deptName)) {
                    SysDept sysDept = deptMap.get("系统运维事业部");
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                } else if ("动力工程".equals(deptName)) {
                    SysDept sysDept = deptMap.get("动力工程事业部");
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                } else if ("动力运营事业部".equals(deptName)) {
                    SysDept sysDept = deptMap.get("经营调度中心");
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                } else if ("资产与信息化".equals(deptName)) {
                    SysDept sysDept = deptMap.get("资产与信息化部");
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                } else if ("节能环保".equals(deptName)) {
                    SysDept sysDept = deptMap.get("节能环保事业部");
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                } else if ("国际工程".equals(deptName)) {
                    SysDept sysDept = deptMap.get("国际工程事业部");
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                } else {
                    SysDept sysDept = deptMap.get(deptName);
                    if (sysDept != null) {
                        obj.setDeptId(sysDept.getId());
                        obj.setDeptName(sysDept.getName());
                    }
                }
                resultList.add(obj);
            }
            //先删除，后插入
            this.remove(new LambdaQueryWrapper<InContract>().in(InContract::getTaskCode, resultList.stream().map(InContract::getTaskCode).collect(Collectors.toList())));
            this.saveBatch(resultList);
        }
        return true;
    }

    @Override
    public boolean edit(InContract inContract) {
        this.updateById(inContract);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "InContract").eq(FormFile::getBusinessId, inContract.getId()));
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = inContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("InContract");
                formFile.setBusinessId(inContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean add(InContract inContract) {
        //判断是否重复添加
        List<InContract> ll = this.list(new LambdaQueryWrapper<InContract>().eq(InContract::getTaskCode, inContract.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号   已存在");
        }
        this.save(inContract);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = inContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("InContract");
                formFile.setBusinessId(inContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean updateCode(InOutVO inOutVO) {
        InContract inContract = this.getById(inOutVO.getId());
        inContract.setContractCode(inOutVO.getContractCode());
        inContract.setWbs(inOutVO.getWbs());
        this.updateById(inContract);

        List<BudgetProject> list1 = budgetProjectService.list(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getTaskCode, inOutVO.getTaskCode()));
        if (ObjectUtil.isNotEmpty(list1)) {
            list1.forEach(item -> {
                item.setWbs(inOutVO.getWbs());
                item.setContractCode(inOutVO.getContractCode());
            });
            budgetProjectService.updateBatchById(list1);
        }
        List<SmallBudgetOut> list2 = smallBudgetOutService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getTaskCode, inOutVO.getTaskCode()));
        if (ObjectUtil.isNotEmpty(list2)) {
            list2.forEach(item -> {
                item.setWbs(inOutVO.getWbs());
            });
            smallBudgetOutService.updateBatchById(list2);
        }

        return true;
    }
}
