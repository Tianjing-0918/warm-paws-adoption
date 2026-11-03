package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.PetRoom;
import com.pet.mapper.PetRoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 宠物房间管理
 */
@RestController
@RequestMapping("/room")
public class PetRoomController {

    @Autowired
    private PetRoomMapper roomMapper;

    /**
     * 所有房间列表
     */
    @GetMapping("/list")
    public Result<List<PetRoom>> list() {
        return Result.success(roomMapper.selectList(
                new LambdaQueryWrapper<PetRoom>().orderByAsc(PetRoom::getRoomNumber)));
    }

    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        Page<PetRoom> page = new Page<>(pageNum, pageSize);
        Page<PetRoom> result = roomMapper.selectPage(page, new LambdaQueryWrapper<PetRoom>()
                .orderByAsc(PetRoom::getRoomNumber));
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    @PostMapping
    public Result<String> add(@RequestBody PetRoom room, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        roomMapper.insert(room);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<String> update(@RequestBody PetRoom room, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        roomMapper.updateById(room);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        roomMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
