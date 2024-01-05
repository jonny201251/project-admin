package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.CustomerMapper;
import com.haiying.project.model.entity.Customer;
import com.haiying.project.model.entity.CustomerScore1;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.CustomerScore1Service;
import com.haiying.project.service.CustomerService;
import com.haiying.project.service.FormFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
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
    @Autowired
    HttpSession httpSession;
    @Autowired
    CustomerScore1Service score1Service;

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

    @Override
    public boolean modify(Integer id) {
        SysUser loginUser = (SysUser) httpSession.getAttribute("user");

        Customer old = this.getById(id);
        Customer neww = new Customer();
        BeanUtils.copyProperties(old, neww);
        old.setHaveDisplay("否");
        this.updateById(old);

        neww.setId(null);
        neww.setVersion(old.getVersion() + 1);
        neww.setHaveDisplay("是");
        neww.setResult(null);
        neww.setLoginName(loginUser.getLoginName());
        neww.setDisplayName(loginUser.getDisplayName());
        neww.setCreateDatetime(LocalDateTime.now());
        neww.setDeptId(loginUser.getDeptId());
        neww.setDeptName(loginUser.getDeptName());
        if (neww.getBaseId() == null) {
            //第一次修改
            neww.setBaseId(old.getId());
        }
        neww.setBeforeId(old.getId());
        this.save(neww);
        //客户评分
        CustomerScore1 score1 = score1Service.getOne(new LambdaQueryWrapper<CustomerScore1>().eq(CustomerScore1::getHaveDisplay, "是").eq(CustomerScore1::getCustomerId, old.getId()));
        if (score1 != null) {
            score1.setHaveDisplay("否");
            score1Service.updateById(score1);
        }
        return true;
    }
}
