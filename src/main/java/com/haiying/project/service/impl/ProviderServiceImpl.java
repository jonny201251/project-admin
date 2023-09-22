package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.ProviderMapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.Provider;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 供方信息 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-03-17
 */
@Service
public class ProviderServiceImpl extends ServiceImpl<ProviderMapper, Provider> implements ProviderService {
    @Autowired
    FormFileService formFileService;

    @Override
    public boolean add(Provider provider) {
        if (!provider.getUsee().equals("民用产业项目")) {
            provider.setType(null);
        }
        this.save(provider);
        //文件
        List<FileVO> fileList = provider.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Provider");
                formFile.setBusinessId(provider.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }

        return true;
    }

    @Override
    public boolean edit(Provider provider) {
        if (!provider.getUsee().equals("民用产业项目")) {
            provider.setType(null);
        }
        if (ObjectUtil.isNotEmpty(provider.getResult())) {
            throw new PageTipException("供方不能编辑");
        }
        this.updateById(provider);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "Provider").eq(FormFile::getBusinessId, provider.getId()));
        //文件
        List<FileVO> fileList = provider.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("Provider");
                formFile.setBusinessId(provider.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }
}
