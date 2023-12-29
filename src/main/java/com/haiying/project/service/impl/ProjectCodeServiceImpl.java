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

        LambdaQueryWrapper<ProjectCodeCount> l = new LambdaQueryWrapper<ProjectCodeCount>().eq(ProjectCodeCount::getYear, year);
        if (user.getDeptName().equals("动力运营事业部")) {
            l.eq(ProjectCodeCount::getDeptId, user.getDeptId2());
        } else {
            l.eq(ProjectCodeCount::getDeptId, user.getDeptId());
        }
        ProjectCodeCount projectCodeCount = projectCodeCountService.getOne(l);
        projectCode.setDeptType(projectCodeCount.getDeptType());
        projectCode.setBusinessType(String.join(",", projectCode.getBusinessTypeTmp()));
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
                + "1"
                + projectCode.getCustomerProperty()
                + projectCode.getProviderProperty() + String.join("", projectCode.getBusinessTypeTmp())
                + simpleYear + strCount;
        projectCode.setTaskCode(taskCode);
        projectCode.setStatus("未使用");
        projectCode.setYear(Integer.parseInt(year));
        projectCode.setProjectName(projectCode.getProjectName().trim());
        this.save(projectCode);
        projectCodeCountService.updateById(projectCodeCount);
        return true;
    }

    @Override
    public boolean edit(ProjectCode projectCode) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        String year = DateUtil.format(DateUtil.date(), "yyyy");
        String simpleYear = DateUtil.format(DateUtil.date(), "yy");

        LambdaQueryWrapper<ProjectCodeCount> l = new LambdaQueryWrapper<ProjectCodeCount>().eq(ProjectCodeCount::getYear, year);
        if (user.getDeptName().equals("动力运营事业部")) {
            l.eq(ProjectCodeCount::getDeptId, user.getDeptId2());
        } else {
            l.eq(ProjectCodeCount::getDeptId, user.getDeptId());
        }
        ProjectCodeCount projectCodeCount = projectCodeCountService.getOne(l);

        projectCode.setDeptType(projectCodeCount.getDeptType());
        projectCode.setBusinessType(String.join(",", projectCode.getBusinessTypeTmp()));
        //任务号
        String strCount = projectCode.getTaskCode().substring(9);

        String taskCode = projectCodeCount.getDeptType()
                + "1"
                + projectCode.getCustomerProperty()
                + projectCode.getProviderProperty() + String.join("", projectCode.getBusinessTypeTmp())
                + simpleYear + strCount;
        projectCode.setTaskCode(taskCode);

        this.updateById(projectCode);
        return true;
    }
}
