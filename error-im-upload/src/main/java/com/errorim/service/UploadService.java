package com.errorim.service;

import com.errorim.entity.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */

public interface UploadService {

    /**
     * @description: 上传头像
     * @param avatar:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult uploadAvatar(MultipartFile avatar);

    /**
     * @description: 上传图片
     * @param image:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult uploadImage(MultipartFile image);

    /**
     * @description: 上传视频
     * @param video:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult uploadVideo(MultipartFile video);

    /**
     * @description: 上传文件
     * @param file:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/12/1
     */
    ResponseResult uploadFile(MultipartFile file);
}
