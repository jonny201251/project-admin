package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.SmallProjectNoMapper;
import com.haiying.project.model.entity.ProjectCode;
import com.haiying.project.model.entity.SmallProjectNo;
import com.haiying.project.service.ProjectCodeService;
import com.haiying.project.service.SmallProjectNoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 一般项目非立项 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
@Service
public class SmallProjectNoServiceImpl extends ServiceImpl<SmallProjectNoMapper, SmallProjectNo> implements SmallProjectNoService {
    @Autowired
    ProjectCodeService projectCodeService;

    @Override
    public boolean add(SmallProjectNo smallProjectNo) {
        this.save(smallProjectNo);

        ProjectCode code = projectCodeService.getOne(new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getTaskCode, smallProjectNo.getTaskCode()));
        code.setStatus("已使用");
        projectCodeService.updateById(code);

        return true;
    }
}
