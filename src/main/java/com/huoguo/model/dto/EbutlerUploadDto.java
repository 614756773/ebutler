package com.huoguo.model.dto;

import com.huoguo.model.entity.EbutlerCustomer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Getter
@Setter
public class EbutlerUploadDto {
    @Delegate
    private EbutlerCustomer ebutlerCustomer;
}
