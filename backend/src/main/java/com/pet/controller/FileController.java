package com.pet.controller;

import com.pet.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传
 */
@RestController
@RequestMapping("/upload")
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.matches("\\.(jpg|jpeg|png|gif|bmp)$")) {
            return Result.error("只支持图片格式");
        }

        // 确保目录存在
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        // 生成随机文件名
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dest = new File(uploadPath + filename);
        file.transferTo(dest);

        Map<String, String> data = new HashMap<>();
        data.put("url", "/upload/" + filename);  // 访问路径
        data.put("filename", filename);
        return Result.success("上传成功", data);
    }

    /**
     * 静态资源访问（实际项目可用Nginx配置，这里做简单映射）
     */
    @GetMapping("/{filename:.+}")
    public void download(@PathVariable String filename, javax.servlet.http.HttpServletResponse response) throws IOException {
        java.io.File file = new java.io.File(uploadPath + filename);
        if (file.exists()) {
            response.setContentType("image/jpeg");
            java.io.InputStream is = new java.io.FileInputStream(file);
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            is.close();
        }
    }
}
