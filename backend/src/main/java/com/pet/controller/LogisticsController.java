package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.common.Result;
import com.pet.entity.Logistics;
import com.pet.entity.LogisticsCompany;
import com.pet.entity.Orders;
import com.pet.mapper.LogisticsCompanyMapper;
import com.pet.mapper.LogisticsMapper;
import com.pet.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 物流管理
 */
@RestController
@RequestMapping("/logistics")
public class LogisticsController {

    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private LogisticsCompanyMapper companyMapper;
    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 管理员发货（填写物流信息）
     */
    @PostMapping
    public Result<String> ship(@RequestBody Logistics logistics, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        logistics.setStatus("已发货");
        logisticsMapper.insert(logistics);

        // 更新订单状态为已发货
        Orders order = ordersMapper.selectById(logistics.getOrderId());
        order.setStatus(2);
        ordersMapper.updateById(order);

        return Result.success("发货成功", null);
    }

    /**
     * 查看某订单物流
     */
    @GetMapping("/{orderId}")
    public Result<Logistics> getByOrderId(@PathVariable Long orderId) {
        Logistics logistics = logisticsMapper.selectOne(
                new LambdaQueryWrapper<Logistics>().eq(Logistics::getOrderId, orderId));
        if (logistics != null) {
            LogisticsCompany company = companyMapper.selectById(logistics.getCompanyId());
            if (company != null) logistics.setCompanyName(company.getName());
        }
        return Result.success(logistics);
    }

    /**
     * 物流列表（管理员）
     */
    @GetMapping("/list")
    public Result<List<Logistics>> list(HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        List<Logistics> list = logisticsMapper.selectList(
                new LambdaQueryWrapper<Logistics>().orderByDesc(Logistics::getCreateTime));
        for (Logistics l : list) {
            LogisticsCompany c = companyMapper.selectById(l.getCompanyId());
            if (c != null) l.setCompanyName(c.getName());
        }
        return Result.success(list);
    }

    @PutMapping
    public Result<String> update(@RequestBody Logistics logistics, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        logisticsMapper.updateById(logistics);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        logisticsMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
