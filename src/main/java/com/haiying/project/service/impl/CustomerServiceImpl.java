package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.CustomerMapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.Customer;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 供方信息 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-17
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Autowired
    FormFileService formFileService;

    @Override
    public boolean add(Customer customer) {
        this.save(customer);
        //文件
        List<FileVO> fileList = customer.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Customer");
                formFile.setBusinessId(customer.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }

        return true;
    }

    @Override
    public boolean edit(Customer customer) {
        this.updateById(customer);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Customer").eq(FormFile::getBusinessId, customer.getId()));
        //文件
        List<FileVO> fileList = customer.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Customer");
                formFile.setBusinessId(customer.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }
}
