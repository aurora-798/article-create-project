package com.shuhang.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.shuhang.common.model.dto.image.ImageData;
import com.shuhang.config.CosServiceConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class CosService {


    @Resource
    private CosServiceConfig cosConfig;

    @Resource
    private AmazonS3 cosClient;

    @Resource
    private CosServiceConfig cosServiceConfig;


    private final OkHttpClient httpClient = new OkHttpClient();


    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosServiceConfig.getBucketName(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传文件到 COS 并返回访问 URL
     *
     * @param key  COS对象键（完整路径）
     * @param file 要上传的文件
     * @return 文件的访问URL，失败返回null
     */
    public String uploadFile(String key, File file) {
        // 上传文件
        PutObjectResult result = putObject(key, file);
        if (result != null) {
            // 构建访问 URL
            String url = String.format("%s/%s%s", cosServiceConfig.getEndpoint(), cosServiceConfig.getBucketName(),key);
            log.info("文件上传COS成功: {} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传COS失败，返回结果为空");
            return null;
        }
    }


    /**
     * 直接使用图片 URL（不上传到 COS）
     *
     * @param imageUrl 图片 URL
     * @return 图片 URL
     */
    public String useDirectUrl(String imageUrl) {
        return imageUrl;
    }


    /**
     * 上传 ImageData 到 COS（统一入口）
     * 根据数据类型自动选择上传方式
     *
     * @param imageData 图片数据对象
     * @param folder    文件夹
     * @return COS 图片 URL，上传失败返回 null
     */
    public String uploadImageData(ImageData imageData, String folder) {
        if (imageData == null || !imageData.isValid()) {
            log.warn("ImageData 无效，无法上传");
            return null;
        }

        try {
            return switch (imageData.getDataType()) {
                case BYTES -> uploadBytes(imageData.getBytes(), imageData.getMimeType(), folder);
                case URL -> uploadFromUrl(imageData.getUrl(), folder);
                case DATA_URL -> uploadFromDataUrl(imageData, folder);
            };
        } catch (Exception e) {
            log.error("上传 ImageData 到 COS 失败, dataType={}", imageData.getDataType(), e);
            return null;
        }
    }



    /**
     * 上传字节数据到 COS
     *
     * @param bytes    图片字节数据
     * @param mimeType MIME 类型
     * @param folder   文件夹
     * @return COS 图片 URL
     */
    public String uploadBytes(byte[] bytes, String mimeType, String folder) {
        if (bytes == null || bytes.length == 0) {
            log.warn("字节数据为空，无法上传");
            return null;
        }

        try {
            // 生成文件名
            String extension = getExtensionFromMimeType(mimeType);
            String fileName = folder + "/" + UUID.randomUUID() + extension;

            // 上传到 COS
            try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(bytes.length);
                metadata.setContentType(mimeType != null ? mimeType : "image/png");

                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        cosConfig.getBucketName(), fileName, inputStream, metadata);

                cosClient.putObject(putObjectRequest);

                String cosUrl = buildCosUrl(fileName);
                log.info("字节数据上传成功, size={} bytes, url={}", bytes.length, cosUrl);
                return cosUrl;
            }
        } catch (Exception e) {
            log.error("上传字节数据到 COS 失败", e);
            return null;
        }
    }

    /**
     * 从外部 URL 下载并上传到 COS
     *
     * @param imageUrl 外部图片 URL
     * @param folder   文件夹
     * @return COS 图片 URL
     */
    public String uploadFromUrl(String imageUrl, String folder) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            log.warn("图片 URL 为空，无法上传");
            return null;
        }

        try {
            // 下载图片
            Request request = new Request.Builder().url(imageUrl).build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("下载图片失败: {}, code={}", imageUrl, response.code());
                    return null;
                }

                byte[] imageBytes = response.body().bytes();
                String contentType = response.header("Content-Type", "image/jpeg");

                // 上传字节数据
                return uploadBytes(imageBytes, contentType, folder);
            }
        } catch (IOException e) {
            log.error("从 URL 上传图片到 COS 失败: {}", imageUrl, e);
            return null;
        }
    }

    /**
     * 从 base64 data URL 解码并上传到 COS
     *
     * @param imageData ImageData 对象（包含 data URL）
     * @param folder    文件夹
     * @return COS 图片 URL
     */
    public String uploadFromDataUrl(ImageData imageData, String folder) {
        byte[] bytes = imageData.getImageBytes();
        if (bytes == null || bytes.length == 0) {
            log.warn("解码 data URL 失败，无法上传");
            return null;
        }

        return uploadBytes(bytes, imageData.getMimeType(), folder);
    }



    /**
     * 根据 MIME 类型获取文件扩展名
     */
    private String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null) {
            return ".png";
        }
        return switch (mimeType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "image/svg+xml" -> ".svg";
            default -> ".png";
        };
    }


    /**
     * 构建 COS 访问 URL
     */
    private String buildCosUrl(String fileName) {
        // https://article-ai-project.cn-nb1.rains3.com/svg-diagram/8325c225-35e4-4431-8fb5-a49804a6250b.svg
        return String.format("https://%s.%s/%s",
                cosConfig.getBucketName(), cosConfig.getEndpoint(),fileName);
    }


}
