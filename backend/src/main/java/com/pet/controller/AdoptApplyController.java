package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.AdoptApply;
import com.pet.entity.Pet;
import com.pet.entity.User;
import com.pet.mapper.AdoptApplyMapper;
import com.pet.mapper.PetMapper;
import com.pet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 领养申请
 */
@RestController
@RequestMapping("/adopt")
public class AdoptApplyController {

    @Autowired
    private AdoptApplyMapper adoptMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户提交领养申请
     */
    @PostMapping
    public Result<String> apply(@RequestBody AdoptApply apply, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        apply.setUserId(userId);
        apply.setStatus(0);
        adoptMapper.insert(apply);
        return Result.success("领养申请已提交，请等待审核", null);
    }

    /**
     * 我的领养申请
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> myList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<AdoptApply> page = new Page<>(pageNum, pageSize);
        Page<AdoptApply> result = adoptMapper.selectPage(page,
                new LambdaQueryWrapper<AdoptApply>()
                        .eq(AdoptApply::getUserId, userId)
                        .orderByDesc(AdoptApply::getCreateTime));
        fillInfo(result.getRecords());
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 管理员查看所有领养申请
     */
    @GetMapping("/adminPage")
    public Result<Map<String, Object>> adminPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Page<AdoptApply> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AdoptApply> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(AdoptApply::getStatus, status);
        wrapper.orderByDesc(AdoptApply::getCreateTime);
        Page<AdoptApply> result = adoptMapper.selectPage(page, wrapper);
        fillInfo(result.getRecords());
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 审核领养申请
     */
    @PutMapping("/audit/{id}")
    public Result<String> audit(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        AdoptApply apply = adoptMapper.selectById(id);
        apply.setStatus((Integer) body.get("status"));
        apply.setReply((String) body.get("reply"));
        adoptMapper.updateById(apply);

        // 如果通过，更新宠物状态为已领养
        if ((Integer) body.get("status") == 1) {
            Pet pet = petMapper.selectById(apply.getPetId());
            pet.setStatus(1);
            pet.setAdoptUserId(apply.getUserId());
            pet.setAdoptTime(LocalDateTime.now());
            petMapper.updateById(pet);
        }
        return Result.success("审核完成", null);
    }

    private void fillInfo(java.util.List<AdoptApply> list) {
        for (AdoptApply a : list) {
            User user = userMapper.selectById(a.getUserId());
            if (user != null) a.setUsername(user.getNickname());
            Pet pet = petMapper.selectById(a.getPetId());
            if (pet != null) {
                a.setPetName(pet.getName());
                a.setPetImage(pet.getImages() != null && pet.getImages().contains(",")
                        ? pet.getImages().split(",")[0] : pet.getImages());
            }
        }
    }
}
