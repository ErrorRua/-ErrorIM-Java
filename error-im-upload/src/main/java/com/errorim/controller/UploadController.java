package com.errorim.controller;

import com.errorim.entity.ResponseResult;
import com.errorim.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping("/avatar")
    public ResponseResult uploadAvatar(MultipartFile avatar) {
        return uploadService.uploadAvatar(avatar);
    }
}
