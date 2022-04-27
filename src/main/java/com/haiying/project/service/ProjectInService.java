package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectIn;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目收支-收入明细 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
public interface ProjectInService extends IService<ProjectIn> {

    boolean add(ProjectIn projectIn);
}
