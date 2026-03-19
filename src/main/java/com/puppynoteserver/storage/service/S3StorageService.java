package com.puppynoteserver.storage.service;

import com.puppynoteserver.storage.enums.BucketKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    /**
     * S3에 파일 업로드 후 CloudFront URL 반환
     *
     * @param file       업로드할 파일
     * @param bucketKind 폴더 종류
     * @return 업로드된 파일의 CloudFront URL
     */
    public String upload(MultipartFile file, BucketKind bucketKind) {
        validateImageFile(file);
        String objectKey = bucketKind.getFolder() + "/" + generateObjectKey(file.getOriginalFilename());
        uploadToS3(file, objectKey);
        return getCloudFrontUrl(objectKey);
    }

    /**
     * CloudFront URL 반환
     *
     * @param objectKey  S3 객체 키 (폴더 포함)
     * @param bucketKind 폴더 종류
     * @return CloudFront URL
     */
    public String getCloudFrontUrl(String objectKey, BucketKind bucketKind) {
        if (objectKey == null || objectKey.isEmpty()) {
            return objectKey;
        }
        // 이미 폴더 prefix가 포함된 경우 그대로 사용, 없으면 추가
        String key = objectKey.startsWith(bucketKind.getFolder() + "/")
                ? objectKey
                : bucketKind.getFolder() + "/" + objectKey;
        return getCloudFrontUrl(key);
    }

    private String getCloudFrontUrl(String objectKey) {
        return cloudFrontDomain + "/" + objectKey;
    }

    private void uploadToS3(MultipartFile file, String objectKey) {
        validateFile(file);
        try {
            String contentType = determineContentType(file);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("S3 파일 업로드 성공: {}, ETag: {}", objectKey, response.eTag());

        } catch (IOException e) {
            log.error("파일 업로드 중 IO 오류 발생: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드 실패", e);
        } catch (Exception e) {
            log.error("S3 파일 업로드 중 오류 발생: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
    }

    private void validateImageFile(MultipartFile file) {
        validateFile(file);

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        boolean isAllowed = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (JPEG, PNG, GIF, WebP만 지원)");
        }
    }

    private String generateObjectKey(String originalFilename) {
        String filename = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFilename);
        if (!filename.endsWith(extension)) {
            filename += extension;
        }
        return filename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    private String determineContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            return contentType;
        }
        String extension = getFileExtension(file.getOriginalFilename());
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".pdf" -> "application/pdf";
            case ".txt" -> "text/plain";
            case ".json" -> "application/json";
            default -> "application/octet-stream";
        };
    }
}
