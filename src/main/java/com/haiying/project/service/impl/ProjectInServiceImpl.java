package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectInMapper;
import com.haiying.project.model.entity.ProjectIn;
import com.haiying.project.model.entity.ProjectInOutCount;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProjectInOutCountService;
import com.haiying.project.service.ProjectInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目收支-收入明细 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@Service
public class ProjectInServiceImpl extends ServiceImpl<ProjectInMapper, ProjectIn> implements ProjectInService {
    @Autowired
    ProjectInOutCountService projectInOutCountService;
    @Autowired
    HttpSession httpSession;

    @Override
    public boolean add(ProjectIn projectIn) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        projectIn.setDisplayName(user.getDisplayName());
        projectIn.setLoginName(user.getLoginName());
        projectIn.setDeptId(user.getDeptId());
        projectIn.setDeptName(user.getDeptName());
        projectIn.setCreateDatetime(LocalDateTime.now());

        ProjectInOutCount count = projectInOutCountService.getById(1);
        projectIn.setSort(Double.valueOf(count.getCount()));
        count.setCount(count.getCount() + 1);

        this.save(projectIn);
        projectInOutCountService.updateById(count);
        return true;
    }
}
