package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectOutMapper;
import com.haiying.project.model.entity.ProjectInOutCount;
import com.haiying.project.model.entity.ProjectOut;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProjectInOutCountService;
import com.haiying.project.service.ProjectOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目收支-支出明细 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@Service
public class ProjectOutServiceImpl extends ServiceImpl<ProjectOutMapper, ProjectOut> implements ProjectOutService {
    @Autowired
    ProjectInOutCountService projectInOutCountService;
    @Autowired
    HttpSession httpSession;

    @Override
    public boolean add(ProjectOut projectOut) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        projectOut.setDisplayName(user.getDisplayName());
        projectOut.setLoginName(user.getLoginName());
        projectOut.setDeptId(user.getDeptId());
        projectOut.setDeptName(user.getDeptName());
        projectOut.setCreateDatetime(LocalDateTime.now());

        ProjectInOutCount count = projectInOutCountService.getById(1);
        projectOut.setSort(count.getCount());
        count.setCount(count.getCount() + 1);

        this.save(projectOut);
        projectInOutCountService.updateById(count);
        return true;
    }
}
