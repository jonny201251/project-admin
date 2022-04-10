package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 一般和重大项目立项任务号 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
public interface ProjectCodeService extends IService<ProjectCode> {

    boolean add(ProjectCode projectCode);
}
