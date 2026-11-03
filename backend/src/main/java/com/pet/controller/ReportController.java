package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.Report;
import com.pet.entity.User;
import com.pet.mapper.ReportMapper;
import com.pet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 流浪宠物上报
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户上报流浪宠物
     */
    @PostMapping
    public Result<String> add(@RequestBody Report report, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        report.setUserId(userId);
        report.setStatus(0);
        reportMapper.insert(report);
        return Result.success("上报成功，我们会尽快处理", null);
    }

    /**
     * 我的上报记录
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> myList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Report> page = new Page<>(pageNum, pageSize);
        Page<Report> result = reportMapper.selectPage(page,
                new LambdaQueryWrapper<Report>()
                        .eq(Report::getUserId, userId)
                        .orderByDesc(Report::getCreateTime));
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 管理员查看所有上报
     */
    @GetMapping("/adminPage")
    public Result<Map<String, Object>> adminPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Page<Report> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Report::getStatus, status);
        wrapper.orderByDesc(Report::getCreateTime);
        Page<Report> result = reportMapper.selectPage(page, wrapper);
        for (Report r : result.getRecords()) {
            User user = userMapper.selectById(r.getUserId());
            if (user != null) r.setUsername(user.getNickname());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 管理员处理上报
     */
    @PutMapping("/handle/{id}")
    public Result<String> handle(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Report report = reportMapper.selectById(id);
        report.setStatus((Integer) body.get("status"));
        report.setReply((String) body.get("reply"));
        reportMapper.updateById(report);
        return Result.success("处理完成", null);
    }
}
