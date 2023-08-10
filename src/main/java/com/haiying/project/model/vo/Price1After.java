package com.haiying.project.model.vo;

import com.haiying.project.model.entity.Price1;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Price1After extends ProcessFormAfter {
    private Price1 formValue;
}
