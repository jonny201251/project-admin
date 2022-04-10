package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProviderScore1;
import lombok.Data;

//用于流程表单提交之后
@Data
public class ProviderScore1VO {
    private ProviderScore1 formValue;
    private String buttonName;
    private String type;
    private String path;
    private String haveEditForm;
    private String comment;
}
