package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectIo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目收支-往来款inout 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
public interface ProjectIoService extends IService<ProjectIo> {

    boolean add(ProjectIo projectIo);
}
