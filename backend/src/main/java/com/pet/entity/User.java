package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;        // 用户名/账号
    private String password;        // 密码（加密存储）
    private String email;           // 邮箱
    private String nickname;        // 昵称
    private String avatar;           // 头像URL
    private String phone;            // 手机号
    private Integer role;           // 角色：0普通用户，1管理员，2超级管理员
    private Integer status;          // 状态：0正常，1禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
