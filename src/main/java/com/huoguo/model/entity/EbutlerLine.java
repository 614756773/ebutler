package com.huoguo.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ebutler_line")
public class EbutlerLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String  name;
    private Integer siteId;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String creator;
    private String modifier;
}
