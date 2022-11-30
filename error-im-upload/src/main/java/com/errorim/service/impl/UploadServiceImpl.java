package com.errorim.service.impl;

import com.alibaba.fastjson2.util.UUIDUtils;
import com.errorim.config.cos.COSConfig;
import com.errorim.dto.UpdateInfoDTO;
import com.errorim.entity.ResponseResult;
import com.errorim.exception.ErrorImException;
import com.errorim.service.UploadService;
import com.errorim.service.UserService;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.errorim.enums.UploadEnum.NOT_IMAGE_FILE;
import static com.errorim.enums.UploadEnum.UPLOAD_AVATAR_FAIL;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private COSConfig cosConfig;

    @Autowired
    private COSClient cosClient;

    @Autowired
    private UserService userService;

    //    图片格式
    public static final String IMG_SUFFIX = ".jpg, .png, .jpeg, .gif, .svg";

    @Override
    public ResponseResult uploadAvatar(MultipartFile avatar) {
        try {
            //获取文件上传的流
            InputStream inputStream = avatar.getInputStream();
            //获取文件名
            String originName = avatar.getOriginalFilename();
            //获取文件后缀
            String suffix = originName.substring(originName.lastIndexOf("."));

            if (!IMG_SUFFIX.contains(suffix)) {
                throw new ErrorImException(NOT_IMAGE_FILE.getCode(), NOT_IMAGE_FILE.getMessage());
            }

            String fileName = "avatar/"
                    + UUID.randomUUID() + "-"
                    + System.currentTimeMillis()
                    + suffix;

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(avatar.getSize());

            cosClient.putObject(cosConfig.getBucketName(),
                    fileName,
                    inputStream,
                    objectMetadata);

            String fileURL = getFileURL(fileName);

            userService.updateUserInfo(new UpdateInfoDTO(){{
                setAvatar(fileURL);
            }});


        } catch (IOException e) {
            throw new ErrorImException(UPLOAD_AVATAR_FAIL.getCode(), UPLOAD_AVATAR_FAIL.getMessage());
        } finally {
            cosClient.shutdown();
        }

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult uploadImage(MultipartFile image) {
        return null;
    }

    @Override
    public ResponseResult uploadVideo(MultipartFile video) {
        return null;
    }

    @Override
    public ResponseResult uploadFile(MultipartFile file) {
        return null;
    }

    private String getFileURL(String fileName) {
        // 不需要验证身份信息
        COSCredentials cred = new AnonymousCOSCredentials();

        ClientConfig clientConfig = new ClientConfig();

        clientConfig.setRegion(new Region(cosConfig.getRegion()));


        COSClient cosClient = new COSClient(cred, clientConfig);


        return cosClient.getObjectUrl(cosConfig.getBucketName(), fileName).toString();
    }
}
