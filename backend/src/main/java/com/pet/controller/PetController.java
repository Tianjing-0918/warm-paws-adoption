package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.Pet;
import com.pet.entity.PetCategory;
import com.pet.mapper.PetCategoryMapper;
import com.pet.mapper.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宠物管理
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private PetCategoryMapper categoryMapper;

    /**
     * 分页查询宠物列表（前台用户可看可领养的）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "9") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {

        Page<Pet> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Pet::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Pet::getCategoryId, categoryId);
        }
        if (status != null && status >= 0) {
            wrapper.eq(Pet::getStatus, status);
        } else if (status == null) {
            wrapper.eq(Pet::getStatus, 0);  // 默认只看可领养的
        }
        // status=-1 时不过滤，显示全部
        wrapper.orderByDesc(Pet::getCreateTime);
        Page<Pet> result = petMapper.selectPage(page, wrapper);

        // 填充品种名称
        List<PetCategory> categories = categoryMapper.selectList(null);
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(PetCategory::getId, PetCategory::getName));
        result.getRecords().forEach(p -> p.setCategoryName(categoryMap.get(p.getCategoryId())));

        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 宠物详情
     */
    @GetMapping("/detail/{id}")
    public Result<Pet> detail(@PathVariable Long id) {
        Pet pet = petMapper.selectById(id);
        if (pet != null) {
            PetCategory category = categoryMapper.selectById(pet.getCategoryId());
            if (category != null) {
                pet.setCategoryName(category.getName());
            }
        }
        return Result.success(pet);
    }

    /**
     * 管理员新增宠物
     */
    @PostMapping
    public Result<String> add(@RequestBody Pet pet, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 1) return Result.error(403, "无权限");
        pet.setStatus(0);
        petMapper.insert(pet);
        return Result.success("添加成功", null);
    }

    /**
     * 管理员修改宠物
     */
    @PutMapping
    public Result<String> update(@RequestBody Pet pet, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 1) return Result.error(403, "无权限");
        if (pet.getStatus() != null && pet.getStatus() == 1) {
            Pet existing = petMapper.selectById(pet.getId());
            if (existing == null || existing.getAdoptTime() == null) {
                pet.setAdoptTime(LocalDateTime.now());
            }
        } else if (pet.getStatus() != null) {
            petMapper.update(null, new LambdaUpdateWrapper<Pet>()
                    .eq(Pet::getId, pet.getId())
                    .set(Pet::getAdoptTime, null));
        }
        petMapper.updateById(pet);
        return Result.success("修改成功", null);
    }

    /**
     * 管理员删除宠物
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 1) return Result.error(403, "无权限");
        petMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 管理员查看所有宠物（含各种状态）
     */
    @GetMapping("/adminPage")
    public Result<Map<String, Object>> adminPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 1) return Result.error(403, "无权限");

        Page<Pet> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) wrapper.like(Pet::getName, name);
        if (status != null) wrapper.eq(Pet::getStatus, status);
        wrapper.orderByDesc(Pet::getCreateTime);
        Page<Pet> result = petMapper.selectPage(page, wrapper);

        List<PetCategory> categories = categoryMapper.selectList(null);
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(PetCategory::getId, PetCategory::getName));
        result.getRecords().forEach(p -> p.setCategoryName(categoryMap.get(p.getCategoryId())));

        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }
}
