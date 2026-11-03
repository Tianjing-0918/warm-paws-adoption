package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.*;
import com.pet.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单管理（含支付宝沙箱支付）
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private UserMapper userMapper;

    @Value("${alipay.app-id}")
    private String alipayAppId;
    @Value("${alipay.private-key}")
    private String alipayPrivateKey;
    @Value("${alipay.public-key}")
    private String alipayPublicKey;
    @Value("${alipay.notify-url}")
    private String notifyUrl;
    @Value("${alipay.return-url}")
    private String returnUrl;

    /**
     * 用户下单
     */
    @PostMapping
    public Result<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body,
                                                   HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        String receiverName = (String) body.get("receiverName");
        String receiverPhone = (String) body.get("receiverPhone");
        String receiverAddress = (String) body.get("receiverAddress");

        // 生成订单号
        String orderNo = "PET" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            Product product = productMapper.selectById(productId);
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(quantity)));
        }

        // 创建订单
        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        ordersMapper.insert(order);

        // 创建订单明细
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            Product product = productMapper.selectById(productId);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(productId);
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItemMapper.insert(orderItem);

            // 扣减库存
            product.setStock(product.getStock() - quantity);
            productMapper.updateById(product);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getId());
        data.put("orderNo", orderNo);
        data.put("totalAmount", totalAmount);
        return Result.success("下单成功，请前往支付", data);
    }

    /**
     * 我的订单
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> myOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Orders> page = new Page<>(pageNum, pageSize);
        Page<Orders> result = ordersMapper.selectPage(page,
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId)
                        .orderByDesc(Orders::getCreateTime));
        fillItems(result.getRecords());
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 管理员查看所有订单
     */
    @GetMapping("/adminPage")
    public Result<Map<String, Object>> adminPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Orders::getStatus, status);
        wrapper.orderByDesc(Orders::getCreateTime);
        Page<Orders> result = ordersMapper.selectPage(page, wrapper);
        fillItems(result.getRecords());
        for (Orders o : result.getRecords()) {
            User user = userMapper.selectById(o.getUserId());
            if (user != null) o.setUsername(user.getNickname());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 模拟支付（毕业设计演示用，实际项目接支付宝SDK）
     */
    @PostMapping("/pay/{orderId}")
    public Result<String> pay(@PathVariable Long orderId, HttpServletRequest request) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) return Result.error("订单不存在");
        if (order.getStatus() != 0) return Result.error("订单状态异常");

        // 模拟支付成功（实际项目中这里调用支付宝SDK生成支付页面）
        order.setStatus(1);  // 已付款待发货
        order.setPayTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        // 毕业设计中可替换为真实支付宝沙箱调用：
        // AlipayClient alipayClient = new DefaultAlipayClient(...);
        // AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // alipayRequest.setReturnUrl(returnUrl);
        // alipayRequest.setNotifyUrl(notifyUrl);
        // ...设置订单参数后调用alipayClient.pageExecute(alipayRequest).getBody()

        return Result.success("支付成功", null);
    }

    /**
     * 支付宝异步通知回调（实际项目中使用）
     */
    @PostMapping("/alipay/notify")
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 实际项目中验签后更新订单状态
        // String tradeStatus = request.getParameter("trade_status");
        // if ("TRADE_SUCCESS".equals(tradeStatus)) { ... }
        response.getWriter().write("success");
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    public Result<String> cancel(@PathVariable Long orderId, HttpServletRequest request) {
        Orders order = ordersMapper.selectById(orderId);
        if (!order.getUserId().equals(request.getAttribute("userId"))) {
            return Result.error("无权操作");
        }
        order.setStatus(4);
        ordersMapper.updateById(order);
        return Result.success("订单已取消", null);
    }

    private void fillItems(List<Orders> list) {
        for (Orders o : list) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId()));
            o.setItems(items);
            // 查物流
            Logistics logistics = logisticsMapper.selectOne(
                    new LambdaQueryWrapper<Logistics>().eq(Logistics::getOrderId, o.getId()));
            o.setLogistics(logistics);
        }
    }
}
