package com.haiying.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.entity.ProviderControl;
import com.haiying.project.model.vo.ProviderControlAfter;

/**
 * <p>
 * 供方动态监控 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-03-17
 */
public interface ProviderControlService extends IService<ProviderControl> {
    boolean btnHandle(ProviderControlAfter after);
}
