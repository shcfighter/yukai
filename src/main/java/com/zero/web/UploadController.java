package com.zero.web;

import com.google.common.collect.Lists;
import com.zero.common.Result;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@PropertySource("classpath:configuration.properties")
@ConfigurationProperties(prefix = "file.upload")
@Data
public class UploadController {

    private static final Logger LOGGER = LogManager.getLogger(UploadController.class);
    private String path;
    private String pathUrl;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile[] files){
        if(Objects.isNull(files) || files.length <= 0){
            return Result.resultFailure("未检查到上传文件！");
        }
        List<String> list = Lists.newArrayList();
        for (MultipartFile file: files) {
            String fileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            File uploadFile = new File(path + fileName);
            try {
                file.transferTo(uploadFile);
            } catch (IOException e) {
                LOGGER.error("文件上传失败！", e);
                return Result.resultFailure("上传文件失败！");
            }
            list.add(pathUrl + fileName);
        }
        return Result.resultSuccess(list);
    }
}
