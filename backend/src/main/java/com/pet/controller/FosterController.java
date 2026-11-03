package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.*;
import com.pet.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 宠物寄养
 */
@RestController
@RequestMapping("/foster")
public class FosterController {

    @Autowired
    private FosterMapper fosterMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PetCategoryMapper categoryMapper;
    @Autowired
    private PetRoomMapper roomMapper;

    /**
     * 用户申请寄养
     */
    @PostMapping
    public Result<String> apply(@RequestBody Foster foster, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        foster.setUserId(userId);
        foster.setStatus(0);
        fosterMapper.insert(foster);
        return Result.success("寄养申请已提交，请等待分配", null);
    }

    /**
     * 我的寄养记录
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> myList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Foster> page = new Page<>(pageNum, pageSize);
        Page<Foster> result = fosterMapper.selectPage(page,
                new LambdaQueryWrapper<Foster>()
                        .eq(Foster::getUserId, userId)
                        .orderByDesc(Foster::getCreateTime));
        fillInfo(result.getRecords());
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 管理员查看所有寄养申请
     */
    @GetMapping("/adminPage")
    public Result<Map<String, Object>> adminPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Page<Foster> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Foster> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Foster::getStatus, status);
        wrapper.orderByDesc(Foster::getCreateTime);
        Page<Foster> result = fosterMapper.selectPage(page, wrapper);
        fillInfo(result.getRecords());
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 管理员分配房间
     */
    @PutMapping("/assign/{id}")
    public Result<String> assign(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                 HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Foster foster = fosterMapper.selectById(id);
        foster.setRoomId(Long.valueOf(body.get("roomId").toString()));
        foster.setStatus(1);
        fosterMapper.updateById(foster);
        return Result.success("房间分配成功", null);
    }

    private void fillInfo(java.util.List<Foster> list) {
        for (Foster f : list) {
            User user = userMapper.selectById(f.getUserId());
            if (user != null) f.setUsername(user.getNickname());
            if (f.getCategoryId() != null) {
                PetCategory c = categoryMapper.selectById(f.getCategoryId());
                if (c != null) f.setCategoryName(c.getName());
            }
            if (f.getRoomId() != null) {
                PetRoom r = roomMapper.selectById(f.getRoomId());
                if (r != null) f.setRoomNumber(r.getRoomNumber());
            }
        }
    }
}
