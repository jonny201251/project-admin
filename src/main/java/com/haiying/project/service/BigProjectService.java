package com.haiying.project.service;

import com.haiying.project.model.entity.BigProject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.BigProjectAfter;

/**
 * <p>
 * 重大项目立项 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-01-11
 */
public interface BigProjectService extends IService<BigProject> {

    boolean btnHandle(BigProjectAfter after);
}
