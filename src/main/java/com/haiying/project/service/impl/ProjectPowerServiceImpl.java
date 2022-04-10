package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.mapper.ProjectPowerMapper;
import com.haiying.project.model.entity.ProjectPower;
import com.haiying.project.model.vo.ProjectPowerAfter;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.ProjectPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 一般和重大项目立项时，授权信息 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-24
 */
@Service
public class ProjectPowerServiceImpl extends ServiceImpl<ProjectPowerMapper, ProjectPower> implements ProjectPowerService {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;

    private void add(ProjectPower formValue) {
        formValue.setTimeLimit(String.join("至", formValue.getTimeLimitTmp()));
        this.save(formValue);
    }

    private void edit(ProjectPower formValue) {
        formValue.setTimeLimit(String.join("至", formValue.getTimeLimitTmp()));
        this.updateById(formValue);
    }

    private void delete(ProjectPower formValue) {
        this.removeById(formValue.getId());
    }

    @Override
    public boolean btnHandle(ProjectPowerAfter after) {
        ProjectPower formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), "授权");
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), "授权");
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
