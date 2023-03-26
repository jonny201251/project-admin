package com.haiying.project.service;

import com.haiying.project.model.entity.InContract;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 收款合同 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
public interface InContractService extends IService<InContract> {

    boolean upload(MultipartFile multipartFile);

    boolean edit(InContract inContract);

    boolean add(InContract inContract);
}
