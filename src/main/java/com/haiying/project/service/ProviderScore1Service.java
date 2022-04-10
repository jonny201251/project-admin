package com.haiying.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.entity.ProviderScore1;
import com.haiying.project.model.vo.ProviderScore1VO;

/**
 * <p>
 * 供方评分1 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-17
 */
public interface ProviderScore1Service extends IService<ProviderScore1> {
    boolean btnHandle(ProviderScore1VO providerScore1VO);
}
