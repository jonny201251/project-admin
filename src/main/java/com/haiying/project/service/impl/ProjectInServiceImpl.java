package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectInMapper;
import com.haiying.project.model.entity.ProjectIn;
import com.haiying.project.model.entity.ProjectInOutCount;
import com.haiying.project.service.ProjectInOutCountService;
import com.haiying.project.service.ProjectInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public boolean add(ProjectIn projectIn) {
        ProjectInOutCount count = projectInOutCountService.getById(1);
        projectIn.setSort(Double.valueOf(count.getCount()));
        count.setCount(count.getCount() + 1);

        this.save(projectIn);
        projectInOutCountService.updateById(count);
        return true;
    }
}
