package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知公告
 */
@Data
@TableName("notice")
public class Notice implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;          // 公告标题
    private String content;         // 公告内容
    private Integer status;         // 状态：0发布，1草稿

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
