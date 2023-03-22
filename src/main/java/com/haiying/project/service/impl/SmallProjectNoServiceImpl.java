package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.SmallProjectNoMapper;
import com.haiying.project.model.entity.ProjectProtect1;
import com.haiying.project.model.entity.SmallProjectNo;
import com.haiying.project.service.ProjectProtect1Service;
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
    ProjectProtect1Service projectProtect1Service;

    @Override
    public boolean add(SmallProjectNo smallProjectNo) {
        this.save(smallProjectNo);
        ProjectProtect1 protect1 = projectProtect1Service.getOne(new LambdaQueryWrapper<ProjectProtect1>().eq(ProjectProtect1::getTaskCode, smallProjectNo.getTaskCode()));
        protect1.setStatus("已使用");
        projectProtect1Service.updateById(protect1);
        return true;
    }
}
