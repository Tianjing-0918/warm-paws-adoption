package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细
 */
@Data
@TableName("order_item")
public class OrderItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;            // 订单ID
    private Long productId;           // 商品ID
    private String productName;       // 商品名称（快照）
    private String productImage;     // 商品图片（快照）
    private BigDecimal price;          // 单价（快照）
    private Integer quantity;          // 数量

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
