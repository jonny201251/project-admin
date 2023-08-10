package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProviderCancel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProviderCancelAfter extends ProcessFormAfter {
    private ProviderCancel formValue;
}
