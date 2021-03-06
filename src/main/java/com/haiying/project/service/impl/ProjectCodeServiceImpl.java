package com.haiying.project.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectCodeMapper;
import com.haiying.project.model.entity.ProjectCode;
import com.haiying.project.model.entity.ProjectCodeCount;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProjectCodeCountService;
import com.haiying.project.service.ProjectCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * <p>
 * 一般和重大项目立项任务号 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
@Service
public class ProjectCodeServiceImpl extends ServiceImpl<ProjectCodeMapper, ProjectCode> implements ProjectCodeService {
    @Autowired
    ProjectCodeCountService projectCodeCountService;
    @Autowired
    HttpSession httpSession;

    @Override
    public boolean add(ProjectCode projectCode) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        String year = DateUtil.format(DateUtil.date(), "yyyy");
        String simpleYear = DateUtil.format(DateUtil.date(), "yy");
        ProjectCodeCount projectCodeCount = projectCodeCountService.getOne(new LambdaQueryWrapper<ProjectCodeCount>().eq(ProjectCodeCount::getYear, year).eq(ProjectCodeCount::getDeptId, user.getDeptId()));
        projectCode.setDisplayName(user.getDisplayName());
        projectCode.setLoginName(user.getLoginName());
        projectCode.setDeptId(user.getDeptId());
        projectCode.setDeptName(user.getDeptName());
        projectCode.setDeptType(projectCodeCount.getDeptType());
        projectCode.setCreateDatetime(LocalDateTime.now());
        projectCode.setBusinessType(String.join(",", projectCode.getBusinessTypeList()));
        //任务号
        Integer count = projectCodeCount.getCount() + 1;
        projectCodeCount.setCount(count);
        String strCount = count + "";
        if (strCount.length() == 1) {
            strCount = "00" + strCount;
        } else if (strCount.length() == 2) {
            strCount = "0" + strCount;
        }
        String taskCode = projectCodeCount.getDeptType()
                + projectCode.getProjectProperty()
                + projectCode.getCustomerProperty()
                + projectCode.getProviderProperty() + String.join("", projectCode.getBusinessTypeList())
                + simpleYear + strCount;
        projectCode.setTaskCode(taskCode);
        this.save(projectCode);
        projectCodeCountService.updateById(projectCodeCount);
        return true;
    }
}
