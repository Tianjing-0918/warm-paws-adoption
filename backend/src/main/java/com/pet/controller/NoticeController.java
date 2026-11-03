package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.Notice;
import com.pet.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知公告
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 前台获取最新公告（前10条）
     */
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        return Result.success(noticeMapper.selectPage(
                new Page<Notice>(1, 10),
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 0)
                        .orderByDesc(Notice::getCreateTime)).getRecords());
    }

    /**
     * 管理员分页
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Page<Notice> page = new Page<>(pageNum, pageSize);
        Page<Notice> result = noticeMapper.selectPage(page,
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime));
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    @PostMapping
    public Result<String> add(@RequestBody Notice notice, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        noticeMapper.insert(notice);
        return Result.success("发布成功", null);
    }

    @PutMapping
    public Result<String> update(@RequestBody Notice notice, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        noticeMapper.updateById(notice);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        noticeMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
