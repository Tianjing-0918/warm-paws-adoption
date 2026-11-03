package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物房间
 */
@Data
@TableName("pet_room")
public class PetRoom implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roomNumber;      // 房间号
    private String type;            // 房间类型：猫舍/狗舍/隔离区
    private Integer capacity;       // 容量
    private Integer status;         // 状态：0空闲，1已满，2维修中
    private String description;     // 描述

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
