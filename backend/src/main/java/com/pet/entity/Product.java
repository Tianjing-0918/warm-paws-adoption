package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物用品
 */
@Data
@TableName("product")
public class Product implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;            // 用品名称
    private String category;        // 分类：食品/玩具/用品/医疗
    private BigDecimal price;       // 价格
    private Integer stock;          // 库存
    private String image;           // 主图
    private String description;      // 详情描述
    private Integer status;          // 状态：0上架，1下架

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
