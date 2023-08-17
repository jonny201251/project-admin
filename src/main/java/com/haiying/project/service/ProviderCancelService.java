package com.haiying.project.service;

import com.haiying.project.model.entity.ProviderCancel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.ProviderCancelAfter;

/**
 * <p>
 * 供方资格取消 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-08-10
 */
public interface ProviderCancelService extends IService<ProviderCancel> {

    boolean btnHandle(ProviderCancelAfter after);
}
