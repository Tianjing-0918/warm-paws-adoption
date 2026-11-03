package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.common.Result;
import com.pet.entity.Donation;
import com.pet.entity.User;
import com.pet.mapper.DonationMapper;
import com.pet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 爱心捐赠
 */
@RestController
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    private DonationMapper donationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 提交捐赠（演示环境模拟支付成功）
     */
    @PostMapping
    public Result<Map<String, Object>> donate(@RequestBody Map<String, Object> body,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Object amountValue = body.get("amount");
        if (amountValue == null) {
            return Result.error("请填写捐赠金额");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountValue.toString());
        } catch (Exception e) {
            return Result.error("捐赠金额格式不正确");
        }
        if (amount.compareTo(BigDecimal.ONE) < 0) {
            return Result.error("捐赠金额不能少于 1 元");
        }

        User user = userMapper.selectById(userId);
        Donation donation = new Donation();
        donation.setUserId(userId);
        donation.setDonorName(user == null ? "爱心伙伴" : user.getNickname());
        donation.setAmount(amount);
        donation.setMessage(body.get("message") == null ? "" : body.get("message").toString());
        donation.setAnonymous(isTrue(body.get("anonymous")) ? 1 : 0);
        donation.setMonthly(isTrue(body.get("monthly")) ? 1 : 0);
        donation.setStatus(1);
        donationMapper.insert(donation);

        Map<String, Object> data = new HashMap<>();
        data.put("id", donation.getId());
        data.put("amount", amount);
        return Result.success("捐赠成功，感谢你的善意", data);
    }

    /**
     * 爱心墙与累计金额
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list() {
        List<Donation> list = donationMapper.selectList(
                new LambdaQueryWrapper<Donation>()
                        .eq(Donation::getStatus, 1)
                        .orderByDesc(Donation::getCreateTime)
                        .last("LIMIT 30"));

        BigDecimal total = BigDecimal.ZERO;
        for (Donation d : list) {
            if (d.getAmount() != null) {
                total = total.add(d.getAmount());
            }
            if (d.getAnonymous() != null && d.getAnonymous() == 1) {
                d.setDonorName("爱心人士");
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.success(data);
    }

    /**
     * 管理员查看捐赠记录与汇总
     */
    @GetMapping("/adminPage")
    public Result<Map<String, Object>> adminPage(HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) {
            return Result.error(403, "无权限");
        }
        List<Donation> list = donationMapper.selectList(
                new LambdaQueryWrapper<Donation>()
                        .eq(Donation::getStatus, 1)
                        .orderByDesc(Donation::getCreateTime));

        BigDecimal total = BigDecimal.ZERO;
        int monthly = 0;
        int anonymous = 0;
        for (Donation d : list) {
            if (d.getAmount() != null) total = total.add(d.getAmount());
            if (d.getMonthly() != null && d.getMonthly() == 1) monthly++;
            if (d.getAnonymous() != null && d.getAnonymous() == 1) anonymous++;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("count", list.size());
        data.put("monthly", monthly);
        data.put("anonymous", anonymous);
        return Result.success(data);
    }

    private boolean isTrue(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() == 1;
        return value != null && "true".equalsIgnoreCase(value.toString());
    }
}
