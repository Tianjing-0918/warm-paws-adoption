package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.PetCategory;
import com.pet.mapper.PetCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 宠物品种管理
 */
@RestController
@RequestMapping("/category")
public class PetCategoryController {

    @Autowired
    private PetCategoryMapper categoryMapper;

    /**
     * 品种列表（所有用户可查）
     */
    @GetMapping("/list")
    public Result<List<PetCategory>> list() {
        return Result.success(categoryMapper.selectList(
                new LambdaQueryWrapper<PetCategory>().orderByAsc(PetCategory::getId)));
    }

    /**
     * 分页查询（管理员）
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        Page<PetCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PetCategory> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) wrapper.like(PetCategory::getName, name);
        Page<PetCategory> result = categoryMapper.selectPage(page, wrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    @PostMapping
    public Result<String> add(@RequestBody PetCategory category, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        categoryMapper.insert(category);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<String> update(@RequestBody PetCategory category, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        categoryMapper.updateById(category);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        categoryMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
