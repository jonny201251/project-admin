package com.haiying.project.model.vo;

import lombok.Data;

import java.util.List;
@Data
public class TestVO {
    private String type;
    private String a;
    private String b;
    private List<FileVO> uploadList;
}
