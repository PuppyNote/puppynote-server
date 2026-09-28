package com.puppynoteserver.storage.service;

import com.puppynoteserver.storage.enums.BucketKind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class S3StorageServiceTest {

    private final S3Client s3Client = Mockito.mock(S3Client.class);
    private final S3StorageService s3StorageService = new S3StorageService(s3Client);

    {
        ReflectionTestUtils.setField(s3StorageService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3StorageService, "cloudFrontDomain", "https://cdn.test");
    }

    @DisplayName("긴 변이 MAX_DIMENSION을 넘는 JPEG는 축소되어 업로드된다.")
    @Test
    void 큰_JPEG_업로드시_리사이즈된다() throws Exception {
        // given
        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .willReturn(PutObjectResponse.builder().eTag("etag").build());

        MockMultipartFile file = new MockMultipartFile(
                "file", "big.jpg", "image/jpeg", createJpegBytes(2000, 1500));

        // when
        s3StorageService.upload(file, BucketKind.PUPPY_PROFILE);

        // then
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        Mockito.verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());

        try (InputStream uploaded = bodyCaptor.getValue().contentStreamProvider().newStream()) {
            BufferedImage resized = ImageIO.read(uploaded);
            assertThat(Math.max(resized.getWidth(), resized.getHeight())).isEqualTo(1600);
        }
    }

    @DisplayName("MAX_DIMENSION 이하 JPEG는 원본 그대로 업로드된다.")
    @Test
    void 작은_JPEG_업로드시_원본_그대로_업로드된다() throws Exception {
        // given
        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .willReturn(PutObjectResponse.builder().eTag("etag").build());

        byte[] original = createJpegBytes(400, 300);
        MockMultipartFile file = new MockMultipartFile("file", "small.jpg", "image/jpeg", original);

        // when
        s3StorageService.upload(file, BucketKind.PUPPY_PROFILE);

        // then
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        Mockito.verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());

        try (InputStream uploaded = bodyCaptor.getValue().contentStreamProvider().newStream()) {
            BufferedImage untouched = ImageIO.read(uploaded);
            assertThat(untouched.getWidth()).isEqualTo(400);
            assertThat(untouched.getHeight()).isEqualTo(300);
        }
    }

    private byte[] createJpegBytes(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return out.toByteArray();
    }
}
