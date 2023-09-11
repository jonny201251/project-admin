package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ContractMoney;
import com.haiying.project.service.ContractMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 收付款合同的合同金额的修改历史 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-09-04
 */
@Wrapper
@RestController
@RequestMapping("/contractMoney")
public class ContractMoneyController {
    @Autowired
    ContractMoneyService contractMoneyService;

    @PostMapping("list")
    public IPage<ContractMoney> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<ContractMoney> wrapper = new LambdaQueryWrapper<ContractMoney>().eq(ContractMoney::getType, "收款合同");

        Object contractCode = paramMap.get("contractCode");
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper.eq(ContractMoney::getContractCode, contractCode);
        } else {
            wrapper.eq(ContractMoney::getContractCode, "无");
        }

        return contractMoneyService.page(new Page<>(1, 100), wrapper);
    }
}
