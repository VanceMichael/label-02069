package com.secondhand.controller;

import com.secondhand.common.BusinessException;
import com.secondhand.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path:/app/uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/api/uploads/}")
    private String urlPrefix;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("\u8bf7\u9009\u62e9\u6587\u4ef6");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("\u53ea\u652f\u6301\u4e0a\u4f20\u56fe\u7247\u6587\u4ef6");
        }

        // 校验文件大小 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("\u56fe\u7247\u5927\u5c0f\u4e0d\u80fd\u8d85\u8fc75MB");
        }

        // 生成文件名
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 保存文件
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(dir, fileName));
        } catch (IOException e) {
            log.error("\u6587\u4ef6\u4fdd\u5b58\u5931\u8d25", e);
            throw new BusinessException("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25");
        }

        String url = urlPrefix + fileName;
        log.info("\u6587\u4ef6\u4e0a\u4f20\u6210\u529f: {}", url);
        return Result.success(url);
    }
}
