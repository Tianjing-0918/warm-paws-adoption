package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领养申请
 */
@Data
@TableName("adopt_apply")
public class AdoptApply implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;           // 申请人
    private Long petId;             // 申请领养的宠物
    private String reason;          // 领养理由
    private String address;         // 居住地址
    private String experience;      // 养宠经验
    private Integer status;         // 状态：0待审核，1已通过，2已拒绝
    private String reply;           // 审核回复

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String petName;
    @TableField(exist = false)
    private String petImage;
}
