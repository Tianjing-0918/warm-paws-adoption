package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流公司
 */
@Data
@TableName("logistics_company")
public class LogisticsCompany implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;           // 公司名称：顺丰/中通/圆通等
    private String phone;          // 客服电话
    private String description;    // 描述

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
