package com.haiying.project.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

//点击流程表单上的按钮之后
@Data
@EqualsAndHashCode(callSuper = false)
public class ProcessFormAfter {
    private String buttonName;
    private String type;
    private String path;
    private String haveEditForm;
    private String comment;
}