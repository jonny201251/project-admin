package com.haiying.project.model.vo;

import lombok.Data;

import java.util.List;

//用于流程表单渲染之前
@Data
public class ProcessFormBefore {
    private List<String> buttonList;
    private String haveEditForm;
}
