package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物信息
 */
@Data
@TableName("pet")
public class Pet implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;           // 宠物名字
    private Long categoryId;        // 品种ID
    private Integer age;            // 年龄（月）
    private String gender;          // 性别：公/母
    private String color;           // 毛色
    private Double weight;          // 体重(kg)
    private String healthStatus;   // 健康状况
    private String description;     // 描述
    private String images;           // 图片（逗号分隔）
    private Integer status;          // 状态：0可领养，1已领养，2寄养中，3待处理
    private Long roomId;             // 所在房间ID
    private Long adoptUserId;         // 领养人ID
    private LocalDateTime adoptTime;  // 领养完成时间（用于本月回家统计）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String categoryName;    // 品种名称（联表查询）
    @TableField(exist = false)
    private String roomNumber;       // 房间号（联表查询）
}
