package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.utils.ExcelListener;
import com.haiying.project.mapper.InContractMapper;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.InContract;
import com.haiying.project.model.excel.InContractExcel;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.InContractService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 收款合同 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
@Service
public class InContractServiceImpl extends ServiceImpl<InContractMapper, InContract> implements InContractService {

    @Autowired
    FormFileService formFileService;

    @Override
    @SneakyThrows
    public boolean upload(MultipartFile file) {
        InputStream inputStream = file.getInputStream();
        //
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        //
        ExcelListener<InContractExcel> listener = new ExcelListener<>();
        //获取sheet对象
        ReadSheet sheet = EasyExcel.readSheet(0).head(InContractExcel.class).registerReadListener(listener).build();
        //读取数据
        excelReader.read(sheet);
        //获取数据
        List<InContractExcel> list = listener.getData();

        return true;
    }

    @Override
    public boolean edit(InContract inContract) {
        this.updateById(inContract);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "InContract").eq(FormFile::getBusinessId, inContract.getId()));
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = inContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("InContract");
                formFile.setBusinessId(inContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean add(InContract inContract) {
        //判断是否重复添加
        List<InContract> ll = this.list(new LambdaQueryWrapper<InContract>().eq(InContract::getTaskCode, inContract.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("任务号   已存在");
        }
        this.save(inContract);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = inContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("InContract");
                formFile.setBusinessId(inContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }
}
