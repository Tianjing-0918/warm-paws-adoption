package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.common.Result;
import com.pet.entity.TrialCheckin;
import com.pet.entity.User;
import com.pet.mapper.TrialCheckinMapper;
import com.pet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

/**
 * 试养打卡
 */
@RestController
@RequestMapping("/checkin")
public class TrialCheckinController {

    @Autowired
    private TrialCheckinMapper checkinMapper;
    @Autowired
    private UserMapper userMapper;

    @PostMapping
    public Result<String> add(@RequestBody TrialCheckin checkin, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        checkin.setUserId(userId);
        if (checkin.getCheckinDate() == null) {
            checkin.setCheckinDate(LocalDate.now());
        }
        checkinMapper.insert(checkin);
        return Result.success("打卡成功", null);
    }

    @GetMapping("/my")
    public Result<List<TrialCheckin>> my(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(checkinMapper.selectList(
                new LambdaQueryWrapper<TrialCheckin>()
                        .eq(TrialCheckin::getUserId, userId)
                        .orderByDesc(TrialCheckin::getCheckinDate)
                        .orderByDesc(TrialCheckin::getCreateTime)));
    }

    @GetMapping("/adminPage")
    public Result<List<TrialCheckin>> adminPage(HttpServletRequest request) {
        if ((Integer) request.getAttribute("role") < 1) {
            return Result.error(403, "无权限");
        }
        List<TrialCheckin> list = checkinMapper.selectList(
                new LambdaQueryWrapper<TrialCheckin>()
                        .orderByDesc(TrialCheckin::getCheckinDate)
                        .last("LIMIT 100"));
        for (TrialCheckin c : list) {
            User user = userMapper.selectById(c.getUserId());
            if (user != null) c.setUsername(user.getNickname());
        }
        return Result.success(list);
    }
}
