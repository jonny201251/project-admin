package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.Price3Mapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.Price3After;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.Price33Service;
import com.haiying.project.service.Price3Service;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 采购方式-比价单 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
@Service
public class Price3ServiceImpl extends ServiceImpl<Price3Mapper, Price3> implements Price3Service {
    @Autowired
    HttpSession httpSession;
    @Autowired
    FormFileService formFileService;
    @Autowired
    Price33Service price33Service;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    ButtonHandleBean buttonHandleBean;


    private void add(Price3 formValue) {
        this.save(formValue);
        List<Price33> list = formValue.getList();
        list.forEach(item -> {
            item.setPrice3Id(formValue.getId());
        });
        price33Service.saveBatch(list);
        //文件
        List<FormFile> listt = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Price3");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }

    private void edit(Price3 formValue) {
        this.updateById(formValue);
        price33Service.remove(new LambdaQueryWrapper<Price33>().eq(Price33::getPrice3Id, formValue.getId()));
        List<Price33> list = formValue.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setPrice3Id(formValue.getId());
        });
        price33Service.saveBatch(list);

        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price3").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> listt = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Price3");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }

    private void delete(Price3 formValue) {
        this.removeById(formValue.getId());
        price33Service.remove(new LambdaQueryWrapper<Price33>().eq(Price33::getPrice3Id, formValue.getId()));
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price3").eq(FormFile::getBusinessId, formValue.getId()));
    }

    @Override
    public boolean btnHandle(Price3After after) {
        Price3 formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        String comment = after.getComment();
        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getProjectName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getProjectName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("check") || type.equals("reject")) {
            //
            SysUser user = (SysUser) httpSession.getAttribute("user");
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            //
            ProcessInst processInst = processInstService.getById(formValue.getProcessInstId());
            String[] tmp = processInst.getLoginProcessStep().split(",");
            if (tmp.length > 1 && buttonName.contains("同意")) {
                buttonHandleBean.checkUpOne(formValue.getProcessInstId(), formValue, buttonName, comment);
            } else {
                buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
            }
        } else if (type.equals("recall")) {
            buttonHandleBean.recall(formValue.getProcessInstId(), buttonName);
        } else if (type.equals("delete")) {
            delete(formValue);
            buttonHandleBean.delete(formValue.getProcessInstId());
        }
        return true;
    }
}
