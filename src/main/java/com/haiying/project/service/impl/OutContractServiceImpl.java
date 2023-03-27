package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.OutContractMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.InOutVO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 付款合同 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-03-26
 */
@Service
public class OutContractServiceImpl extends ServiceImpl<OutContractMapper, OutContract> implements OutContractService {
    @Autowired
    FormFileService formFileService;
    @Autowired
    InContractService inContractService;
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    SmallBudgetOutService smallBudgetOutService;

    @Override
    public boolean edit(OutContract outContract) {
        //先有收款合同，才能进行付款合同
        List<InContract> ll = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getTaskCode, outContract.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("必须先有收款合同，才能进行付款合同");
        }
        this.updateById(outContract);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "OutContract").eq(FormFile::getBusinessId, outContract.getId()));
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = outContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("OutContract");
                formFile.setBusinessId(outContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public void updateCode(InOutVO inOutVO) {
        OutContract outcontract = this.getById(inOutVO.getId());
        outcontract.setContractCode(inOutVO.getContractCode());
        outcontract.setWbs(inOutVO.getWbs());
        this.updateById(outcontract);

        List<BudgetProject> list1 = budgetProjectService.list(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getTaskCode, inOutVO.getTaskCode()));
        if (ObjectUtil.isNotEmpty(list1)) {
            list1.forEach(item -> {
                item.setWbs(inOutVO.getWbs());
            });
            budgetProjectService.updateBatchById(list1);
        }
        List<SmallBudgetOut> list2 = smallBudgetOutService.list(new LambdaQueryWrapper<SmallBudgetOut>().eq(SmallBudgetOut::getTaskCode, inOutVO.getTaskCode()));
        if (ObjectUtil.isNotEmpty(list2)) {
            list2.forEach(item -> {
                item.setWbs(inOutVO.getWbs());
            });
            smallBudgetOutService.updateBatchById(list2);
        }
    }

    @Override
    public boolean add(OutContract outContract) {
        if (ObjectUtil.isEmpty(outContract.getWbs())) {
            throw new PageTipException("必须有WBS编号，如果没有，合同签署情况->合同号和WBS号,进行补全");
        }
        //先有收款合同，才能进行付款合同
        List<InContract> ll = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getTaskCode, outContract.getTaskCode()));
        if (ObjectUtil.isEmpty(ll)) {
            throw new PageTipException("必须先有收款合同，才能进行付款合同");
        }
        this.save(outContract);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = outContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("OutContract");
                formFile.setBusinessId(outContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }
}
