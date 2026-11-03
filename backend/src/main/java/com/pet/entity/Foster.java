package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 寄养申请
 */
@Data
@TableName("foster")
public class Foster implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;            // 申请人ID
    private String petName;         // 宠物名字
    private Long categoryId;        // 品种
    private String petGender;       // 宠物性别
    private Integer petAge;         // 宠物年龄
    private LocalDate startDate;    // 开始日期
    private LocalDate endDate;      // 结束日期
    private String description;     // 备注
    private Integer status;         // 状态：0待处理，1已分配，2已完成，3已拒绝
    private Long roomId;             // 分配房间ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String roomNumber;
}
