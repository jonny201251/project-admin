package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.ProviderCancelMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.ProviderCancelAfter;
import com.haiying.project.service.*;
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
public class ProviderCancelServiceImpl extends ServiceImpl<ProviderCancelMapper, ProviderCancel> implements ProviderCancelService {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;

    @Autowired
    ProviderService providerService;
    @Autowired
    ProviderQueryService providerQueryService;
    @Autowired
    ProviderScore1Service providerScore1Service;

    private void add(ProviderCancel formValue) {
        //判断是否重复添加
        List<ProviderCancel> ll = this.list(new LambdaQueryWrapper<ProviderCancel>().eq(ProviderCancel::getName, formValue.getName()).eq(ProviderCancel::getUsee, formValue.getUsee()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("项目类别和供方名称   已存在");
        }

        formValue.setHaveDisplay("是");
        formValue.setVersion(0);
        this.save(formValue);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("ProviderCancel");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void edit(ProviderCancel formValue) {
        this.updateById(formValue);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderCancel").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("ProviderCancel");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void delete(ProviderCancel formValue) {
        this.removeById(formValue.getId());
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderCancel").eq(FormFile::getBusinessId, formValue.getId()));
        Integer beforeId = formValue.getBeforeId();
        if (beforeId != null) {
            ProviderCancel before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }


    private void change(ProviderCancel current) {
        ProviderCancel before = this.getById(current.getId());
        before.setHaveDisplay("否");
        this.updateById(before);
        //
        current.setId(null);
        current.setProcessInstId(null);
        current.setBeforeId(before.getId());
        current.setHaveDisplay("是");
        current.setVersion(current.getVersion() + 1);
        if (current.getBaseId() == null) {
            //第一次修改
            current.setBaseId(before.getId());
        } else {
            //第二、三、N次修改
            current.setBaseId(before.getBaseId());
        }
        this.save(current);

        //文件
        List<FileVO> fileList = current.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("ProviderCancel");
                formFile.setBusinessId(current.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    @Override
    public boolean btnHandle(ProviderCancelAfter after) {
        ProviderCancel formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        String comment = after.getComment();
        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            //
            boolean flag = false;
            //
            ProcessInst processInst = processInstService.getById(formValue.getProcessInstId());
            String[] tmp = processInst.getLoginProcessStep().split(",");
            if (tmp.length > 1 && buttonName.contains("同意")) {
                buttonHandleBean.checkUpOne(formValue.getProcessInstId(), formValue, buttonName, comment);
            } else {
                flag = buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
            }
            if (flag) {
                Provider provider = providerService.getById(formValue.getProviderId());
                provider.setResult(provider.getResult() + "(资格取消)");
                providerService.updateById(provider);

                ProviderQuery providerQuery = providerQueryService.getById(formValue.getProviderId());
                if (providerQuery != null) {
                    providerQuery.setResult(provider.getResult() + "(资格取消)");
                    providerQueryService.updateById(providerQuery);
                }

                ProviderScore1 providerScore1 = providerScore1Service.getById(formValue.getProviderId());
                if (providerScore1 != null) {
                    providerScore1.setResult(provider.getResult() + "(资格取消)");
                    providerScore1Service.updateById(providerScore1);
                }

            }
        } else if (type.equals("recall")) {
            buttonHandleBean.recall(formValue.getProcessInstId(), buttonName);
        } else if (type.equals("delete")) {
            delete(formValue);
            buttonHandleBean.delete(formValue.getProcessInstId());
        } else if (type.equals("change")) {
            ProcessInst before = processInstService.getById(formValue.getProcessInstId());
            before.setBusinessHaveDisplay("否");
            processInstService.updateById(before);
            change(formValue);
            Integer newProcessInstId = buttonHandleBean.change(before, path, formValue, buttonName, formValue.getId(), formValue.getName(), comment);
            formValue.setProcessInstId(newProcessInstId);
            this.updateById(formValue);
        }
        return true;
    }

}
