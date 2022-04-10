package com.haiying.project.service;

import com.haiying.project.model.entity.ProviderQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.ProviderQueryAfter;

/**
 * <p>
 * 供方尽职调查 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
public interface ProviderQueryService extends IService<ProviderQuery> {

    boolean btnHandle(ProviderQueryAfter after);
}
