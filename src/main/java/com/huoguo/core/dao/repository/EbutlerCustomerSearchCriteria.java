package com.huoguo.core.dao.repository;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EbutlerCustomerSearchCriteria {
    private Integer id;
    private Integer siteId;
    private Integer lineId;
    private String siteName;
    private String lineName;
    private String accountName;
    private String accountNumber;
    private String customerName;
    private String divisionPoint;
    private String capacity;
    private String electricalCapacity;
    private String selfPower;
    private String telephone;
    private String photovoltaicNumber;
    private String photovoltaicCapacity;
    private String transformerModel;
    private String transformerImpedanceVoltage;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String creator;
    private String modifier;
}
