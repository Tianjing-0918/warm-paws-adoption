package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话记录
 */
@Data
@TableName("chat_message")
public class ChatMessage implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;           // 用户ID
    private String content;        // 用户问题
    private String reply;           // AI回复

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
