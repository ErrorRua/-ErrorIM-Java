package com.errorim.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
@Data
@Component
public class COSUtils {

    public static String secretId;
    public static String secretKey;

    public static String region;

    public static String bucketName;

    @Value("${cos.secretId}")
    public void setSecretId(String secretId) {
        COSUtils.secretId = secretId;
    }
    @Value("${cos.secretKey}")
    public void setSecretKey(String secretKey) {
        COSUtils.secretKey = secretKey;
    }
    @Value("${cos.region}")
    public void setRegion(String region) {
        COSUtils.region = region;
    }
    @Value("${cos.bucketName}")
    public void setBucketName(String bucketName) {
        COSUtils.bucketName = bucketName;
    }

    // 创建 COSClient 实例
    public static COSClient cosClient(){
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的区域, COS 地域的简称请参照
        Region region = new Region(COSUtils.region);
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

    // 创建 TransferManager 实例，这个实例用来后续调用高级接口
    public static TransferManager createTransferManager() {
        // 创建一个 COSClient 实例，这是访问 COS 服务的基础实例。
        COSClient cosClient = cosClient();

        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(16);

        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5*1024*1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1*1024*1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    public void shutdownTransferManager(TransferManager transferManager) {
        transferManager.shutdownNow(false);
    }
}
