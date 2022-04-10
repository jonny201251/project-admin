package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProviderSimpleMapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.ProviderSimple;
import com.haiying.project.model.entity.ProviderSimple2;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.ProviderSimple2Service;
import com.haiying.project.service.ProviderSimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 供方情况简表 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@Service
public class ProviderSimpleServiceImpl extends ServiceImpl<ProviderSimpleMapper, ProviderSimple> implements ProviderSimpleService {
    @Autowired
    ProviderSimple2Service providerSimple2Service;
    @Autowired
    FormFileService formFileService;

    @Override
    public boolean add(ProviderSimple providerSimple) {
        this.save(providerSimple);
        List<ProviderSimple2> simple2List = providerSimple.getList();
        simple2List.forEach(item -> item.setProviderSimpleId(providerSimple.getId()));
        providerSimple2Service.saveBatch(simple2List);
        //文件
        List<FileVO> fileList = providerSimple.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("ProviderSimple");
                formFile.setBusinessId(providerSimple.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean edit(ProviderSimple providerSimple) {
        this.updateById(providerSimple);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "ProviderSimple").eq(FormFile::getBusinessId, providerSimple.getId()));
        providerSimple2Service.remove(new LambdaQueryWrapper<ProviderSimple2>().eq(ProviderSimple2::getProviderSimpleId, providerSimple.getId()));
        List<ProviderSimple2> simple2List = providerSimple.getList();
        for (ProviderSimple2 providerSimple2 : simple2List) {
            providerSimple2.setId(null);
            providerSimple2.setProviderSimpleId(providerSimple.getId());
        }
        providerSimple2Service.saveBatch(simple2List);
        return true;
    }
}
