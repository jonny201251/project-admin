package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectIoMapper;
import com.haiying.project.model.entity.ProjectInOutCount;
import com.haiying.project.model.entity.ProjectIo;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.service.ProjectInOutCountService;
import com.haiying.project.service.ProjectIoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目收支-往来款inout 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@Service
public class ProjectIoServiceImpl extends ServiceImpl<ProjectIoMapper, ProjectIo> implements ProjectIoService {
    @Autowired
    ProjectInOutCountService projectInOutCountService;
    @Autowired
    HttpSession httpSession;

    @Override
    public boolean add(ProjectIo projectIo) {
        SysUser user = (SysUser) httpSession.getAttribute("user");
        projectIo.setDisplayName(user.getDisplayName());
        projectIo.setLoginName(user.getLoginName());
        projectIo.setDeptId(user.getDeptId());
        projectIo.setDeptName(user.getDeptName());
        projectIo.setCreateDatetime(LocalDateTime.now());

        ProjectInOutCount count = projectInOutCountService.getById(1);
        projectIo.setSort(count.getCount());
        count.setCount(count.getCount() + 1);

        this.save(projectIo);
        projectInOutCountService.updateById(count);
        return true;
    }
}
