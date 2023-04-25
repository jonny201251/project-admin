package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.ProjectProtectMapper;
import com.haiying.project.model.entity.BigProject;
import com.haiying.project.model.entity.ProjectProtect;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.vo.ProjectProtectAfter;
import com.haiying.project.service.BigProjectService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProjectProtectService;
import com.haiying.project.service.SmallProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 一般和重大项目的保证金登记表 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-03-22
 */
@Service
public class ProjectProtectServiceImpl extends ServiceImpl<ProjectProtectMapper, ProjectProtect> implements ProjectProtectService {

    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    BigProjectService bigProjectService;

    private void add(ProjectProtect formValue) {
        formValue.setUserNamee(String.join(",", formValue.getUserNameeList()));
        this.save(formValue);
    }

    private void edit(ProjectProtect formValue) {
        formValue.setUserNamee(String.join(",", formValue.getUserNameeList()));
        this.updateById(formValue);
    }

    private void delete(ProjectProtect formValue) {
        this.removeById(formValue.getId());
    }


    @Override
    public boolean btnHandle(ProjectProtectAfter after) {
        ProjectProtect formValue = after.getFormValue();
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
            boolean flag = buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
            if (flag) {
                if (formValue.getType().equals("一般项目")) {
                    SmallProject tmp = smallProjectService.getById(formValue.getProjectId());
                    tmp.setProjectStatus(formValue.getStatus());
                    smallProjectService.updateById(tmp);
                } else if (formValue.getType().equals("重大项目")) {
                    BigProject tmp = bigProjectService.getById(formValue.getProjectId());
                    tmp.setProjectStatus(formValue.getStatus());
                    bigProjectService.updateById(tmp);
                }
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
