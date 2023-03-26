package com.haiying.project.service;

import com.haiying.project.model.entity.OutContract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.InOutVO;

/**
 * <p>
 * 付款合同 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-03-26
 */
public interface OutContractService extends IService<OutContract> {

    boolean add(OutContract outContract);

    boolean edit(OutContract outContract);

    void updateCode(InOutVO inOutVO);
}
