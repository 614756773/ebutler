package com.huoguo.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ebutler_customer")
public class EbutlerCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelIgnore
    private Integer id;
    @ExcelIgnore
    private Integer siteId;
    @ExcelIgnore
    private Integer lineId;
    @ExcelIgnore
    private String siteName;
    @ExcelIgnore
    private String lineName;
    @ExcelProperty("户号")
    private String accountNumber;
    @ExcelProperty("户名")
    private String accountName;
    @ExcelProperty("客户号")
    private String customerName;
    @ExcelProperty("产权分界点")
    private String divisionPoint;
    @ExcelProperty("容量")
    private String capacity;
    @ExcelProperty("电器容量")
    private String electricalCapacity;
    @ExcelProperty("自备电源")
    private String selfPower;
    @ExcelProperty("通讯号码")
    private String telephone;
    @ExcelProperty("光伏户号")
    private String photovoltaicNumber;
    @ExcelProperty("光伏容量")
    private String photovoltaicCapacity;
    @ExcelProperty("变压器型号")
    private String transformerModel;
    @ExcelProperty("变压器阻抗电压")
    private String transformerImpedanceVoltage;
    @ExcelIgnore
    private LocalDateTime createTime;
    @ExcelIgnore
    private LocalDateTime modifyTime;
    @ExcelIgnore
    private String creator;
    @ExcelIgnore
    private String modifier;
}
