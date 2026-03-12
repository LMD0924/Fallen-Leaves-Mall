package org.example.commonbackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/*
 * @Author:总会落叶
 * @Date:2026/3/11
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("api/file")
public class FileUploadController {

    // 从配置文件读取图片保存路径
    @Value("${file.upload.path}")
    private String uploadPath;

    // 从配置文件读取图片访问前缀
    @Value("${file.upload.access-path}")
    private String accessPath;

    /**
     * 单张图片上传接口
     * @param file 前端传递的图片文件（参数名需和前端一致，如 "file"）
     * @return 上传结果（包含访问路径、文件名等）
     */
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        // 1. 校验文件是否为空
        if (file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "上传文件不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        // 2. 校验文件格式（仅允许 jpg、png、jpeg、gif）
        String originalFilename = file.getOriginalFilename();
        String fileExt = StringUtils.getFilenameExtension(originalFilename);
        if (fileExt == null || !fileExt.matches("^(jpg|png|jpeg|gif)$")) {
            result.put("code", 400);
            result.put("msg", "仅支持 jpg、png、jpeg、gif 格式的图片");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            // 3. 创建保存目录（按日期分目录，如 upload/images/20260311/）
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fullDir = uploadPath + dateDir + "/";
            File dirFile = new File(fullDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs(); // 递归创建目录
            }

            // 4. 生成唯一文件名（UUID + 后缀，避免重复）
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExt;
            String fullFilePath = fullDir + uniqueFileName;

            // 5. 保存文件到服务器
            Path path = Paths.get(fullFilePath);
            Files.write(path, file.getBytes());

            // 6. 组装返回结果（前端可通过 accessPath 访问图片）
            String fileAccessUrl = accessPath + dateDir + "/" + uniqueFileName;
            result.put("code", 200);
            result.put("msg", "上传成功");
            result.put("data", Map.of(
                    "fileName", uniqueFileName,
                    "originalName", originalFilename,
                    "fileSize", file.getSize(),
                    "fileUrl", fileAccessUrl
            ));
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            result.put("code", 500);
            result.put("msg", "文件上传失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
