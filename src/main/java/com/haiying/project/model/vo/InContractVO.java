package com.haiying.project.model.vo;

import com.haiying.project.model.entity.InContract;
import lombok.Data;

//用于流程表单提交之后
@Data
public class InContractVO {
    private InContract formValue;
    private String buttonName;
    private String type;
    private String path;
    private String haveEditForm;
    private String comment;
}
