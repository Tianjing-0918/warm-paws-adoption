package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.LostPet;
import com.pet.mapper.LostPetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/lost")
public class LostPetController {

    @Autowired
    private LostPetMapper lostPetMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "9") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String petType) {
        Page<LostPet> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LostPet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostPet::getStatus, 0);
        if (type != null && !type.isEmpty()) wrapper.eq(LostPet::getType, type);
        if (petType != null && !petType.isEmpty()) wrapper.eq(LostPet::getPetType, petType);
        wrapper.orderByDesc(LostPet::getCreateTime);
        Page<LostPet> result = lostPetMapper.selectPage(page, wrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<LostPet> detail(@PathVariable Long id) {
        return Result.success(lostPetMapper.selectById(id));
    }

    @PostMapping
    public Result<String> add(@RequestBody LostPet lostPet, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        lostPet.setUserId(userId);
        lostPet.setStatus(0);
        lostPetMapper.insert(lostPet);
        return Result.success("发布成功", null);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody LostPet lostPet) {
        lostPet.setId(id);
        lostPetMapper.updateById(lostPet);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        lostPetMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/my")
    public Result<?> myList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(lostPetMapper.selectList(
                new LambdaQueryWrapper<LostPet>().eq(LostPet::getUserId, userId).orderByDesc(LostPet::getCreateTime)));
    }
}
