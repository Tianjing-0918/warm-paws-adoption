package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 爱心捐赠记录
 */
@Data
@TableName("donation")
public class Donation implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String donorName;
    private BigDecimal amount;
    private String message;
    private Integer anonymous;
    private Integer monthly;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
