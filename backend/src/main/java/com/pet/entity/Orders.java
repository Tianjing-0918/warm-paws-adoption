package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单
 */
@Data
@TableName("orders")
public class Orders implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;          // 订单编号
    private Long userId;             // 用户ID
    private BigDecimal totalAmount;  // 订单总金额
    private Integer status;           // 状态：0待付款，1已付款待发货，2已发货，3已完成，4已取消
    private String receiverName;     // 收货人姓名
    private String receiverPhone;     // 收货人电话
    private String receiverAddress;   // 收货地址
    private LocalDateTime payTime;    // 支付时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private List<OrderItem> items;   // 订单明细
    @TableField(exist = false)
    private Logistics logistics;     // 物流信息
}
