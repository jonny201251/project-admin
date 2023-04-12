package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.SmallProjectMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.SmallProjectAfter;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 一般项目立项 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
@Service
public class SmallProjectServiceImpl extends ServiceImpl<SmallProjectMapper, SmallProject> implements SmallProjectService {

    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    SmallProtectService smallProtectService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    ProjectCodeService projectCodeService;
    @Autowired
    CustomerService customerService;
    @Autowired
    ProviderService providerService;
    @Autowired
    HttpSession httpSession;


    private void add(SmallProject formValue) {
        //判断是否重复添加
        List<SmallProject> ll = this.list(new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getTaskCode, formValue.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号   已存在");
        }

        formValue.setHaveDisplay("是");
        formValue.setVersion(0);
        //
        formValue.setIdType(String.join(",", formValue.getIdTypeListTmp()));
        formValue.setWorkDate(String.join("至", formValue.getWorkDateTmp()));
        this.save(formValue);
        List<SmallProtect> list = formValue.getList();
        list.forEach(item -> {
            item.setProjectId(formValue.getId());
            item.setProjectType("一般项目");
        });
        smallProtectService.saveBatch(list);
        //文件
        List<FormFile> listt = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("SmallProject");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }

    private void edit(SmallProject formValue) {
        formValue.setIdType(String.join(",", formValue.getIdTypeListTmp()));
        formValue.setWorkDate(String.join("至", formValue.getWorkDateTmp()));
        this.updateById(formValue);
        smallProtectService.remove(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getProjectId, formValue.getId()));
        List<SmallProtect> list = formValue.getList();
        list.forEach(item -> {
            item.setId(null);
            item.setProjectId(formValue.getId());
            item.setProjectType("一般项目");
        });
        smallProtectService.saveBatch(list);

        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "SmallProject").eq(FormFile::getBusinessId, formValue.getId()));
        //文件
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> listt = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("SmallProject");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }

    private void delete(SmallProject formValue) {
        this.removeById(formValue.getId());
        smallProtectService.remove(new LambdaQueryWrapper<SmallProtect>().eq(SmallProtect::getProjectId, formValue.getId()));
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "SmallProject").eq(FormFile::getBusinessId, formValue.getId()));

        Integer beforeId = formValue.getBeforeId();
        if (beforeId != null) {
            SmallProject before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    private void change(SmallProject current) {
        SmallProject before = this.getById(current.getId());
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
            item.setProjectId(current.getId());
            item.setProjectType("一般项目");
        });
        smallProtectService.saveBatch(list);

        //文件
        List<FileVO> fileList = current.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> listt = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("SmallProject");
                formFile.setBusinessId(current.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                listt.add(formFile);
            }
            formFileService.saveBatch(listt);
        }
    }


    @Override
    public boolean btnHandle(SmallProjectAfter after) {
        SmallProject formValue = after.getFormValue();
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
            //
            SysUser user = (SysUser) httpSession.getAttribute("user");
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            if (user.getDisplayName().equals("祁瑛")) {
                if (ObjectUtil.isNotEmpty(formValue.getPowerCode())) {
                    SmallProject db = this.getById(formValue.getId());
                    db.setPowerCode(formValue.getPowerCode());
                    this.updateById(db);
                }
            }
            //
            ProcessInst processInst = processInstService.getById(formValue.getProcessInstId());
            //业务主管领导
            if (processInst.getDisplayProcessStep().contains("业务主管领导")) {
                List<Customer> list = customerService.list(new LambdaQueryWrapper<Customer>().eq(Customer::getName, after.getFormValue().getCustomerName()).in(Customer::getResult, Arrays.asList("优秀", "良好", "一般")));
                if (ObjectUtil.isEmpty(list)) {
                    throw new PageTipException("先审批 客户信用评级评分,客户名称=" + after.getFormValue().getCustomerName());
                }
                if (formValue.getProperty().equals("三类")) {
                    List<Provider> listt = providerService.list(new LambdaQueryWrapper<Provider>().eq(Provider::getName, after.getFormValue().getProviderName()).eq(Provider::getResult, "合格"));
                    if (ObjectUtil.isEmpty(listt)) {
                        throw new PageTipException("先审批 供方信息,供方名称=" + after.getFormValue().getProviderName());
                    }
                }
            }

            boolean flag = buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
            if (user.getDisplayName().equals("郭琳")) {
                ProcessInst processInstt = processInstService.getById(formValue.getProcessInstId());
                if (processInstt.getDisplayProcessStep().contains("郭琳")) {
                    flag = buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
                }
            }
            if (flag) {
                ProjectCode code = projectCodeService.getOne(new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getTaskCode, formValue.getTaskCode()));
                code.setStatus("已使用");
                projectCodeService.updateById(code);
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
