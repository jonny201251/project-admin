package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.ProviderQueryMapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.ProviderQuery;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.ProviderQueryAfter;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProviderQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 供方尽职调查 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@Service
public class ProviderQueryServiceImpl extends ServiceImpl<ProviderQueryMapper, ProviderQuery> implements ProviderQueryService {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    private void add(ProviderQuery formValue) {
        this.save(formValue);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("ProviderQuery");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
        }
        if (ObjectUtil.isNotEmpty(list)) {
            formFileService.saveBatch(list);
        }
    }

    private void edit(ProviderQuery formValue) {
        this.updateById(formValue);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderQuery").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("ProviderQuery");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void delete(ProviderQuery formValue) {
        this.removeById(formValue.getId());
    }

    @Override
    public boolean btnHandle(ProviderQueryAfter after) {
        ProviderQuery formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), "供方尽职调查");
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), "供方尽职调查");
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, after.getComment());
        } else if (type.equals("recall")) {
            buttonHandleBean.recall(formValue.getProcessInstId(), buttonName);
        } else if (type.equals("delete")) {
            delete(formValue);
            buttonHandleBean.delete(formValue.getProcessInstId());
        }
        return true;
    }
}
