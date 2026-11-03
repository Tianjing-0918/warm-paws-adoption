package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.*;
import com.pet.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页统计数据
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired private PetMapper petMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private NoticeMapper noticeMapper;
    @Autowired private AdoptApplyMapper adoptApplyMapper;
    @Autowired private FosterMapper fosterMapper;
    @Autowired private ReportMapper reportMapper;
    @Autowired private PetCategoryMapper categoryMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        Long petTotal = petMapper.selectCount(null);
        Long petAvailable = petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 0));
        Long petAdopted = petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 1));
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        Long petAdoptedThisMonth = petMapper.selectCount(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getStatus, 1)
                .ge(Pet::getAdoptTime, monthStart)
                .lt(Pet::getAdoptTime, monthEnd));
        Long petFoster = petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 2));
        Long petPending = petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 3));
        Long petAtStation = petAvailable + petPending;
        Long userTotal = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, 0));
        Long adoptPending = adoptApplyMapper.selectCount(new LambdaQueryWrapper<AdoptApply>().eq(AdoptApply::getStatus, 0));
        Long fosterPending = fosterMapper.selectCount(new LambdaQueryWrapper<Foster>().eq(Foster::getStatus, 0));
        Long reportPending = reportMapper.selectCount(new LambdaQueryWrapper<Report>().eq(Report::getStatus, 0));

        data.put("petTotal", petTotal);
        data.put("petAvailable", petAvailable);
        data.put("petAdopted", petAdopted);
        data.put("petAdoptedThisMonth", petAdoptedThisMonth);
        data.put("petFoster", petFoster);
        data.put("petPending", petPending);
        data.put("petAtStation", petAtStation);
        data.put("userTotal", userTotal);
        data.put("adoptPending", adoptPending);
        data.put("fosterPending", fosterPending);
        data.put("reportPending", reportPending);

        // 最新公告
        List<Notice> notices = noticeMapper.selectPage(
                new Page<Notice>(1, 10),
                new LambdaQueryWrapper<Notice>().eq(Notice::getStatus, 0).orderByDesc(Notice::getCreateTime)
        ).getRecords();
        data.put("notices", notices);

        // 最新可领养宠物
        List<Pet> latestPets = petMapper.selectPage(
                new Page<Pet>(1, 8),
                new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 0).orderByDesc(Pet::getCreateTime)
        ).getRecords();
        List<PetCategory> cats = categoryMapper.selectList(null);
        Map<Long, String> catMap = cats.stream().collect(Collectors.toMap(PetCategory::getId, PetCategory::getName));
        latestPets.forEach(p -> p.setCategoryName(catMap.get(p.getCategoryId())));
        data.put("latestPets", latestPets);

        // 品种分类统计
        List<PetCategory> allCats = categoryMapper.selectList(null);
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        for (PetCategory c : allCats) {
            Long count = petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getCategoryId, c.getId()));
            Map<String, Object> item = new HashMap<>();
            item.put("name", c.getName());
            item.put("count", count);
            categoryStats.add(item);
        }
        data.put("categoryStats", categoryStats);

        return Result.success(data);
    }
}
