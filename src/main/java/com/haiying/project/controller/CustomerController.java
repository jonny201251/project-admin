package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.Customer;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.excel.CustomerrExcel;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.CustomerService;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户信息 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@RestController
@RequestMapping("/customer")
@Wrapper
public class CustomerController {
    @Autowired
    CustomerService customerService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    FormFileService formFileService;
    @Autowired
    ProcessInstService processInstService;

    @PostMapping("list")
    public IPage<Customer> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().orderByDesc(Customer::getId).in(Customer::getResult, Arrays.asList("", "优秀", "良好", "一般"));
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        Object property = paramMap.get("property");
        Object code = paramMap.get("code");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Customer::getName, name);
        }
        if (ObjectUtil.isNotEmpty(property)) {
            wrapper.like(Customer::getProperty, property);
        }
        if (ObjectUtil.isNotEmpty(code)) {
            wrapper.like(Customer::getCode, code);
        }

/*        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getDisplayName().equals("宋思奇")) {
            wrapper.eq(Customer::getDeptId, user.getDeptId());
        }*/
        return customerService.page(new Page<>(current, pageSize), wrapper);
    }


    //用于 客户评分
    @PostMapping("list2")
    public IPage<Customer> list2(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().eq(Customer::getResult,"");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Customer::getName, name);
        }
        SysUser user = (SysUser) httpSession.getAttribute("user");
        if (!user.getDisplayName().equals("宋思奇")) {
            wrapper.eq(Customer::getDeptId, user.getDeptId());
        }
        return customerService.page(new Page<>(current, pageSize), wrapper);
    }

    //用于项目立项
    @PostMapping("list3")
    public IPage<Customer> list3(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().in(Customer::getResult, Arrays.asList("优秀", "良好", "一般"));
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(Customer::getName, name);
        }

        SysUser user = (SysUser) httpSession.getAttribute("user");

        return customerService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("add")
    public boolean add(@RequestBody Customer customer) {
        //判断是否重复添加
        List<Customer> list = customerService.list(new LambdaQueryWrapper<Customer>().eq(Customer::getName, customer.getName().trim()));
        if (ObjectUtil.isNotEmpty(list)) {
            throw new PageTipException("客户名称   已存在");
        }

        SysUser user = (SysUser) httpSession.getAttribute("user");
        customer.setDisplayName(user.getDisplayName());
        customer.setLoginName(user.getLoginName());
        customer.setDisplayName(user.getDisplayName());
        customer.setLoginName(user.getLoginName());
        customer.setDeptId(user.getDeptId());
        customer.setDeptName(user.getDeptName());
        customer.setCreateDatetime(LocalDateTime.now());
        customer.setHaveDisplay("是");
        customer.setVersion(0);

        return customerService.add(customer);
    }


    @GetMapping("get")
    public Customer get(Integer id) {
        Customer customer = customerService.getById(id);
        //
        List<FormFile> formFileList = formFileService.list(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Customer").eq(FormFile::getBusinessId, id));
        if (ObjectUtil.isNotEmpty(formFileList)) {
            List<FileVO> fileList = new ArrayList<>();
            for (FormFile formFile : formFileList) {
                FileVO fileVO = new FileVO();
                fileVO.setName(formFile.getName());
                fileVO.setUrl(formFile.getUrl());
                fileVO.setStatus("done");
                fileList.add(fileVO);
            }
            customer.setFileList(fileList);
        }
        return customer;
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody Customer customer) {
//        if(ObjectUtil.isNotEmpty(customer.getResult())){
//            throw new PageTipException("暂时不让编辑");
//        }
        return customerService.edit(customer);
    }

    @GetMapping("export")
    public void download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("客户名录", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");
        //
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).useDefaultStyle(false).excelType(ExcelTypeEnum.XLS).build();
        List<CustomerrExcel> dataList = new ArrayList<>();
        WriteSheet sheet = EasyExcel.writerSheet(0, "客户").head(CustomerrExcel.class).build();
        //
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().in(Customer::getResult, Arrays.asList("优秀", "良好", "一般")).orderByDesc(Customer::getId);
        List<Customer> list = customerService.list(wrapper);
        for (Customer customer : list) {
            CustomerrExcel customerrExcel=new CustomerrExcel();
            BeanUtils.copyProperties(customer,customerrExcel);
            dataList.add(customerrExcel);
        }

        excelWriter.write(dataList, sheet);
        //
        excelWriter.finish();
    }
}
