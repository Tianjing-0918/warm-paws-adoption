package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.common.Result;
import com.pet.entity.Banner;
import com.pet.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 首页轮播图
 */
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerMapper bannerMapper;

    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.success(bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, 0)
                        .orderByAsc(Banner::getSortOrder)
                        .orderByDesc(Banner::getId)));
    }

    @GetMapping("/adminPage")
    public Result<List<Banner>> adminPage(HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) {
            return Result.error(403, "无权限");
        }
        return Result.success(bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .orderByAsc(Banner::getSortOrder)
                        .orderByDesc(Banner::getId)));
    }

    @PostMapping
    public Result<String> add(@RequestBody Banner banner, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        if (banner.getStatus() == null) banner.setStatus(0);
        if (banner.getSortOrder() == null) banner.setSortOrder(0);
        bannerMapper.insert(banner);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<String> update(@RequestBody Banner banner, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        bannerMapper.updateById(banner);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        bannerMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
