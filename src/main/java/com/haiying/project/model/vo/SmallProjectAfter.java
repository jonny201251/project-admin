package com.haiying.project.model.vo;

import com.haiying.project.model.entity.SmallProject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SmallProjectAfter extends ProcessFormAfter {
    private SmallProject formValue;
}
