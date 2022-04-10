package com.haiying.project.service;

import com.haiying.project.model.entity.ProviderSimple;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 供方情况简表 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
public interface ProviderSimpleService extends IService<ProviderSimple> {

    boolean add(ProviderSimple providerSimple);

    boolean edit(ProviderSimple providerSimple);
}
