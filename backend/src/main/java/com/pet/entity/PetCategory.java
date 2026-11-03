package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物品种
 */
@Data
@TableName("pet_category")
public class PetCategory implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;         // 品种名称
    private String type;         // 类别：猫/狗/其他
    private String description;  // 描述

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
