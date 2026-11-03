package com.pet.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.JwtUtil;
import com.pet.common.Result;
import com.pet.entity.User;
import com.pet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制器：登录/注册/邮箱验证/找回密码/个人信息
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 临时存储邮箱验证码（实际项目可存Redis）
    private static final Map<String, String> CODE_CACHE = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        String code = body.get("code");
        String nickname = body.getOrDefault("nickname", username);

        // 校验验证码
        String cachedCode = CODE_CACHE.get(email);
        if (cachedCode == null || !cachedCode.equals(code)) {
            return Result.error("验证码错误或已过期");
        }

        // 检查用户名是否已存在
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (count > 0) {
            return Result.error("用户名已存在");
        }

        // 检查邮箱是否已注册
        count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (count > 0) {
            return Result.error("该邮箱已注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(SecureUtil.md5(password));
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole(0);  // 默认普通用户
        user.setStatus(0);
        userMapper.insert(user);

        CODE_CACHE.remove(email);
        return Result.success("注册成功", null);
    }

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/sendEmailCode")
    public Result<String> sendEmailCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return Result.error("邮箱格式不正确");
        }

        // 生成6位随机验证码
        String code = String.format("%06d", (int) (Math.random() * 1000000));
        CODE_CACHE.put(email, code);

        // 5分钟后自动清除验证码
        EXECUTOR.schedule(() -> CODE_CACHE.remove(email), 5, TimeUnit.MINUTES);

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("流浪动物救助领养系统 - 邮箱验证码");
        message.setText("您的验证码是：" + code + "，5分钟内有效，请勿泄露给他人。");
        mailSender.send(message);

        return Result.success("验证码已发送，请查收邮件", null);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(SecureUtil.md5(password))) {
            return Result.error("密码错误");
        }
        if (user.getStatus() == 1) {
            return Result.error("账号已被禁用，请联系管理员");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        user.setPassword(null);  // 不向前端返回密码哈希
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user);
        return Result.success("登录成功", data);
    }

    /**
     * 找回密码（通过邮箱）
     */
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");

        String cachedCode = CODE_CACHE.get(email);
        if (cachedCode == null || !cachedCode.equals(code)) {
            return Result.error("验证码错误或已过期");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (user == null) {
            return Result.error("该邮箱未注册");
        }

        user.setPassword(SecureUtil.md5(newPassword));
        userMapper.updateById(user);
        CODE_CACHE.remove(email);
        return Result.success("密码重置成功，请重新登录", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        user.setPassword(null);  // 不返回密码
        return Result.success(user);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/updateInfo")
    public Result<String> updateInfo(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        user.setPassword(null);  // 不允许通过此接口改密码
        user.setRole(null);
        user.setStatus(null);
        userMapper.updateById(user);
        return Result.success("修改成功", null);
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        User user = userMapper.selectById(userId);
        if (!user.getPassword().equals(SecureUtil.md5(oldPassword))) {
            return Result.error("原密码错误");
        }
        user.setPassword(SecureUtil.md5(newPassword));
        userMapper.updateById(user);
        return Result.success("密码修改成功", null);
    }

    /**
     * 管理员分页查询用户列表
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 1) {
            return Result.error(403, "无权限访问");
        }

        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(page, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));

        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    /**
     * 管理员禁用/启用用户
     */
    @PutMapping("/toggleStatus/{id}")
    public Result<String> toggleStatus(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 1) {
            return Result.error(403, "无权限访问");
        }
        Long currentUserId = (Long) request.getAttribute("userId");
        if (id.equals(currentUserId)) {
            return Result.error("不能禁用当前登录账号");
        }
        User target = userMapper.selectById(id);
        if (target != null && target.getRole() != null && target.getRole() >= 2 && role < 2) {
            return Result.error(403, "只有超级管理员可以操作超级管理员账号");
        }
        User user = userMapper.selectById(id);
        user.setStatus(user.getStatus() == 0 ? 1 : 0);
        userMapper.updateById(user);
        return Result.success("操作成功", null);
    }

    /**
     * 超级管理员分配角色
     */
    @PutMapping("/role/{id}")
    public Result<String> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                     HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 2) {
            return Result.error(403, "只有超级管理员可以分配角色");
        }
        Long currentUserId = (Long) request.getAttribute("userId");
        if (id.equals(currentUserId)) {
            return Result.error("不能修改自己的角色");
        }
        Object value = body.get("role");
        if (value == null) {
            return Result.error("请选择角色");
        }
        int newRole = Integer.parseInt(value.toString());
        if (newRole < 0 || newRole > 2) {
            return Result.error("角色参数不正确");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setRole(newRole);
        userMapper.updateById(user);
        return Result.success("角色已更新", null);
    }

    /**
     * 管理员重置用户密码
     */
    @PutMapping("/resetPasswordByAdmin/{id}")
    public Result<String> resetPasswordByAdmin(@PathVariable Long id,
                                               @RequestBody Map<String, String> body,
                                               HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 1) {
            return Result.error(403, "无权限访问");
        }
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("新密码至少 6 位");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getRole() != null && user.getRole() >= 2 && role < 2) {
            return Result.error(403, "只有超级管理员可以重置超级管理员密码");
        }
        user.setPassword(SecureUtil.md5(newPassword));
        userMapper.updateById(user);
        return Result.success("密码已重置", null);
    }

    /**
     * 管理员删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role < 2) {
            return Result.error(403, "超级管理员权限");
        }
        Long currentUserId = (Long) request.getAttribute("userId");
        if (id.equals(currentUserId)) {
            return Result.error("不能删除当前登录账号");
        }
        User target = userMapper.selectById(id);
        if (target != null && target.getRole() != null && target.getRole() >= 2) {
            return Result.error("不能删除超级管理员账号");
        }
        userMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
