package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.Price4Mapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.Price4;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.Price4After;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.Price4Service;
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
public class Price4ServiceImpl extends ServiceImpl<Price4Mapper, Price4> implements Price4Service {
    @Autowired
    HttpSession httpSession;
    @Autowired
    FormFileService formFileService;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    ButtonHandleBean buttonHandleBean;


    private void add(Price4 formValue) {
        formValue.setRequest(String.join(",", formValue.getRequestList()));
        this.save(formValue);
        //文件
        List<FormFile> listt = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Price4");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }

    private void edit(Price4 formValue) {
        formValue.setRequest(String.join(",", formValue.getRequestList()));
        this.updateById(formValue);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price4").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> listt = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Price4");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }

    private void delete(Price4 formValue) {
        this.removeById(formValue.getId());
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Price4").eq(FormFile::getBusinessId, formValue.getId()));
    }

    @Override
    public boolean btnHandle(Price4After after) {
        Price4 formValue = after.getFormValue();
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
