package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.BigProjectMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.BigProjectAfter;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 重大项目立项 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-01-11
 */
@Service
public class BigProjectServiceImpl extends ServiceImpl<BigProjectMapper, BigProject> implements BigProjectService {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    BigProjectTestService bigProjectTestService;
    @Autowired
    FormFileService formFileService;


    private void add(BigProject formValue) {
        //判断是否重复添加
        List<BigProject> ll = this.list(new LambdaQueryWrapper<BigProject>().eq(BigProject::getTaskCode, formValue.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号   已存在");
        }

        formValue.setHaveDisplay("是");
        formValue.setVersion(0);
        //
        formValue.setIdType(String.join(",", formValue.getIdTypeListTmp()));
        this.save(formValue);
        List<SmallProtect> list = formValue.getList();
        list.forEach(item -> item.setSmallProjectId(formValue.getId()));
        smallProtectService.saveBatch(list);

        List<BigProjectTest> list234 = new ArrayList<>();
        List<BigProjectTest> list2 = formValue.getList2();
        List<BigProjectTest> list3 = formValue.getList3();
        List<BigProjectTest> list4 = formValue.getList4();
        if (ObjectUtil.isNotEmpty(list2)) {
            list2.forEach(item -> {
                item.setProjectId(formValue.getId());
                item.setType("project");
            });
            list234.addAll(list2);
        }
        if (ObjectUtil.isNotEmpty(list3)) {
            list3.forEach(item -> {
                item.setProjectId(formValue.getId());
                item.setType("customer");
            });
            list234.addAll(list3);
        }
        if (ObjectUtil.isNotEmpty(list4)) {
            list4.forEach(item -> {
                item.setProjectId(formValue.getId());
                item.setType("provider");
            });
            list234.addAll(list4);
        }
        bigProjectTestService.saveBatch(list234);
        //文件
        List<FormFile> listt = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("BigProject");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }

    private void edit(BigProject formValue) {
        formValue.setIdType(String.join(",", formValue.getIdTypeListTmp()));
        this.updateById(formValue);

        smallProtectService.remove(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getSmallProjectId, formValue.getId()));
        List<SmallProtect> list = formValue.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setSmallProjectId(formValue.getId());
        });
        smallProtectService.saveBatch(list);

        bigProjectTestService.remove(new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getProjectId, formValue.getId()));
        List<BigProjectTest> list234 = new ArrayList<>();
        List<BigProjectTest> list2 = formValue.getList2();
        List<BigProjectTest> list3 = formValue.getList3();
        List<BigProjectTest> list4 = formValue.getList4();
        if (ObjectUtil.isNotEmpty(list2)) {
            list2.forEach(item -> {
                item.setProjectId(formValue.getId());
                item.setType("project");
            });
            list234.addAll(list2);
        }
        if (ObjectUtil.isNotEmpty(list3)) {
            list3.forEach(item -> {
                item.setProjectId(formValue.getId());
                item.setType("customer");
            });
            list234.addAll(list3);
        }
        if (ObjectUtil.isNotEmpty(list4)) {
            list4.forEach(item -> {
                item.setProjectId(formValue.getId());
                item.setType("provider");
            });
            list234.addAll(list4);
        }
        bigProjectTestService.saveBatch(list234);

        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "BigProject").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> listt = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("BigProject");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }


    private void delete(BigProject formValue) {
        this.removeById(formValue.getId());
        smallProtectService.remove(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getSmallProjectId, formValue.getId()));
        bigProjectTestService.remove(new LambdaQueryWrapper<BigProjectTest>().eq(BigProjectTest::getProjectId, formValue.getId()));
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "BigProject").eq(FormFile::getBusinessId, formValue.getId()));

        Integer beforeId = formValue.getBeforeId();
        if (beforeId != null) {
            BigProject before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    private void change(BigProject current) {
        BigProject before = this.getById(current.getId());
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
        current.setIdType(String.join(",", current.getIdTypeListTmp()));
        this.save(current);
        List<SmallProtect> list = current.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setSmallProjectId(current.getId());
        });
        smallProtectService.saveBatch(list);

        List<BigProjectTest> list234 = new ArrayList<>();
        List<BigProjectTest> list2 = current.getList2();
        List<BigProjectTest> list3 = current.getList3();
        List<BigProjectTest> list4 = current.getList4();
        if (ObjectUtil.isNotEmpty(list2)) {
            list2.forEach(item -> {
                item.setId(null);
                item.setProjectId(current.getId());
                item.setType("project");
            });
            list234.addAll(list2);
        }
        if (ObjectUtil.isNotEmpty(list3)) {
            list3.forEach(item -> {
                item.setId(null);
                item.setProjectId(current.getId());
                item.setType("customer");
            });
            list234.addAll(list3);
        }
        if (ObjectUtil.isNotEmpty(list4)) {
            list4.forEach(item -> {
                item.setId(null);
                item.setProjectId(current.getId());
                item.setType("provider");
            });
            list234.addAll(list4);
        }
        bigProjectTestService.saveBatch(list234);
        //文件
        List<FileVO> fileList = current.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> listt = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("BigProject");
                formFile.setBusinessId(current.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }


    @Override
    public boolean btnHandle(BigProjectAfter after) {
        BigProject formValue = after.getFormValue();
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
