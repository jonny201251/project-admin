package com.haiying.project.model.vo;

import com.haiying.project.model.entity.Price3;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Price3After extends ProcessFormAfter {
    private Price3 formValue;
}
