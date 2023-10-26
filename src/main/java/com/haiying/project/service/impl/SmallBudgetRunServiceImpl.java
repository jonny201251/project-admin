package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.SmallBudgetRunMapper;
import com.haiying.project.model.entity.SmallBudgetRun;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.SmallBudgetRunAfter;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.SmallBudgetRunService;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProcessInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 项目预算的流程 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
@Service
public class SmallBudgetRunServiceImpl extends ServiceImpl<SmallBudgetRunMapper, SmallBudgetRun> implements SmallBudgetRunService {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    HttpSession httpSession;

    private void add(SmallBudgetRun formValue) {
        //判断是否重复添加
        List<SmallBudgetRun> ll = this.list(new LambdaQueryWrapper<SmallBudgetRun>().eq(SmallBudgetRun::getHaveDisplay, "是").eq(SmallBudgetRun::getTaskCode, formValue.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("备案号   已存在");
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
                formFile.setType("SmallBudgetRun");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void edit(SmallBudgetRun formValue) {
        this.updateById(formValue);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "SmallBudgetRun").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("SmallBudgetRun");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void delete(SmallBudgetRun formValue) {
        this.removeById(formValue.getId());
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "SmallBudgetRun").eq(FormFile::getBusinessId, formValue.getId()));
        Integer beforeId = formValue.getBeforeId();
        if (beforeId != null) {
            SmallBudgetRun before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    public boolean change(SmallBudgetRun current) {
        SmallBudgetRun before = this.getById(current.getBeforeId());
        before.setHaveDisplay("否");
        this.updateById(before);
        //
        current.setProcessInstId(null);
        current.setHaveDisplay("是");
        current.setVersion(current.getVersion() + 1);
        this.save(current);
        //文件
        List<FileVO> fileList = current.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("SmallBudgetRun");
                formFile.setBusinessId(current.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean change(SmallBudgetRun current, Integer newId) {
        SmallBudgetRun before = this.getById(current.getId());
        before.setHaveDisplay("否");
        this.updateById(before);
        //
        current.setProcessInstId(null);
        current.setBeforeId(before.getId());
        current.setHaveDisplay("是");
        current.setVersion(current.getVersion() + 1);
        if (current.getBaseId() == null) {
            //第一次修改
            current.setBaseId(before.getId());
        }
        current.setId(newId);
        this.save(current);

        //文件
        List<FileVO> fileList = current.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("SmallBudgetRun");
                formFile.setBusinessId(current.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean btnHandle(SmallBudgetRunAfter after) {
        SmallBudgetRun formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        String comment = after.getComment();

        if (formValue.getProjectType().equals("重大项目")) {
            path = "bigBudgetRunPath";
        }

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
            SysUser user = (SysUser) httpSession.getAttribute("user");
            if (user.getDisplayName().equals("于欣坤") && formValue.getHaveThree() != null) {
                SmallBudgetRun tmp = this.getById(formValue.getId());
                tmp.setHaveThree(formValue.getHaveThree());
                this.updateById(tmp);
            }
            //
            buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
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
