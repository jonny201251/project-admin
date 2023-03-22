package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProjectProtect;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectProtectAfter extends ProcessFormAfter {
    private ProjectProtect formValue;
}
