package com.haiying.project.service;

import com.haiying.project.model.entity.SmallProject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.SmallProjectAfter;

/**
 * <p>
 * 一般项目立项 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
public interface SmallProjectService extends IService<SmallProject> {

    boolean btnHandle(SmallProjectAfter after);
}
