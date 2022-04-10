package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProviderQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProviderQueryAfter extends ProcessFormAfter {
    private ProviderQuery formValue;
}
