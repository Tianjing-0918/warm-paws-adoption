package com.pet.controller;

import com.pet.common.Result;
import com.pet.entity.LogisticsCompany;
import com.pet.mapper.LogisticsCompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 物流公司管理
 */
@RestController
@RequestMapping("/company")
public class LogisticsCompanyController {

    @Autowired
    private LogisticsCompanyMapper companyMapper;

    @GetMapping("/list")
    public Result<List<LogisticsCompany>> list() {
        return Result.success(companyMapper.selectList(null));
    }

    @PostMapping
    public Result<String> add(@RequestBody LogisticsCompany company, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        companyMapper.insert(company);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<String> update(@RequestBody LogisticsCompany company, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        companyMapper.updateById(company);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) return Result.error(403, "无权限");
        companyMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
