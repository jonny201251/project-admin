package com.haiying.project.model.vo;

import com.haiying.project.model.entity.BigProject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BigProjectAfter extends ProcessFormAfter {
    private BigProject formValue;
}
