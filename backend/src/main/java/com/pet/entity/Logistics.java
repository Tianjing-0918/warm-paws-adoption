package com.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流信息
 */
@Data
@TableName("logistics")
public class Logistics implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;              // 订单ID
    private Long companyId;            // 物流公司ID
    private String trackingNo;         // 运单号
    private String senderAddress;      // 发货地址
    private String receiverAddress;    // 收货地址
    private String status;             // 物流状态：已发货/运输中/已签收
    private String traceInfo;           // 物流轨迹

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String companyName;
}
