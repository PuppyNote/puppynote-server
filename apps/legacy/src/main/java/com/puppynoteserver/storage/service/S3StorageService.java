package com.puppynoteserver.storage.service;

import com.puppynoteserver.storage.enums.BucketKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    // 모바일 카메라 원본(수천px)을 그대로 내려주면 로딩이 느려지므로 업로드 시점에 긴 변 기준으로 축소한다.
    private static final int MAX_DIMENSION = 1600;
    private static final float JPEG_QUALITY = 0.85f;

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
        String imageKey = generateObjectKey(file.getOriginalFilename());
        String objectKey = bucketKind.getFolder() + "/" + imageKey;

        String contentType = determineContentType(file);
        byte[] content = readBytes(file);
        byte[] resized = resizeIfNeeded(content, contentType);

        uploadToS3(resized, contentType, objectKey);
        return imageKey;
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

    /**
     * S3에서 파일 삭제
     *
     * @param imageKey   삭제할 이미지 키
     * @param bucketKind 폴더 종류
     */
    public void deleteObject(String imageKey, BucketKind bucketKind) {
        if (imageKey == null || imageKey.isEmpty()) return;
        String objectKey = imageKey.startsWith(bucketKind.getFolder() + "/")
                ? imageKey
                : bucketKind.getFolder() + "/" + imageKey;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());
            log.info("S3 파일 삭제 성공: {}", objectKey);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}, message: {}", objectKey, e.getMessage());
        }
    }

    /**
     * S3에서 파일 목록 삭제
     *
     * @param imageKeys  삭제할 이미지 키 목록
     * @param bucketKind 폴더 종류
     */
    public void deleteObjects(List<String> imageKeys, BucketKind bucketKind) {
        if (imageKeys == null || imageKeys.isEmpty()) return;
        imageKeys.forEach(key -> deleteObject(key, bucketKind));
    }

    private String getCloudFrontUrl(String objectKey) {
        return cloudFrontDomain + "/" + objectKey;
    }

    private void uploadToS3(byte[] content, String contentType, String objectKey) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength((long) content.length)
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(content)
            );

            log.info("S3 파일 업로드 성공: {}, ETag: {}", objectKey, response.eTag());

        } catch (Exception e) {
            log.error("S3 파일 업로드 중 오류 발생: {}", objectKey, e);
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            log.error("파일 읽기 중 IO 오류 발생: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    /**
     * 긴 변이 MAX_DIMENSION을 넘는 JPEG/PNG를 축소한다.
     * GIF(애니메이션 손실 위험)·WebP(JDK 기본 ImageIO 미지원) 등은 원본 그대로 둔다.
     */
    private byte[] resizeIfNeeded(byte[] original, String contentType) {
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            return original;
        }
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(original));
            if (image == null) {
                return original;
            }

            int width = image.getWidth();
            int height = image.getHeight();
            if (Math.max(width, height) <= MAX_DIMENSION) {
                return original;
            }

            double scale = (double) MAX_DIMENSION / Math.max(width, height);
            int newWidth = (int) Math.round(width * scale);
            int newHeight = (int) Math.round(height * scale);

            boolean isPng = "image/png".equals(contentType);
            BufferedImage resized = new BufferedImage(
                    newWidth, newHeight, isPng ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resized.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image, 0, 0, newWidth, newHeight, null);
            graphics.dispose();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (isPng) {
                ImageIO.write(resized, "png", output);
            } else {
                writeJpeg(resized, output);
            }
            return output.toByteArray();
        } catch (IOException e) {
            log.warn("이미지 리사이즈 실패, 원본으로 업로드합니다: {}", e.getMessage());
            return original;
        }
    }

    private void writeJpeg(BufferedImage image, ByteArrayOutputStream output) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(JPEG_QUALITY);
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
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
        if (contentType != null && contentType.startsWith("video/")) {
            throw new IllegalArgumentException("동영상 파일은 업로드할 수 없습니다.");
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
