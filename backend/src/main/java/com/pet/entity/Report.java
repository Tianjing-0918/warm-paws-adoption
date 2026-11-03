package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流浪宠物上报
 */
@Data
@TableName("report")
public class Report implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;           // 上报人ID
    private String location;       // 发现地点
    private String description;     // 描述信息
    private String images;          // 图片（逗号分隔，多张）
    private String contact;         // 联系方式
    private Integer status;         // 状态：0待处理，1已处理，2已拒绝
    private String reply;           // 管理员处理回复

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String username;
}
