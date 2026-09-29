package com.puppynoteserver.storage.application.port.out;

import com.puppynoteserver.storage.enums.BucketKind;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * [APPLICATION · OUT PORT] 파일 스토리지.
 *
 * <p>
 * 포트로 뽑아 두는 이유는 제공자가 계약이 아니기 때문이다 — AWS S3, OCI Object Storage 등
 * 구현체를 바꾸거나 여러 개를 동시에 둬도, 이 인터페이스를 호출하는 도메인 코드는 변경되지 않는다.
 */
public interface FileStorage {

    /**
     * 파일 업로드 후 저장된 객체 키(CloudFront URL이 아닌 순수 파일명)를 반환한다.
     */
    String upload(MultipartFile file, BucketKind bucketKind);

    /**
     * 객체 키로부터 CloudFront URL을 조립해 반환한다.
     */
    String getCloudFrontUrl(String objectKey, BucketKind bucketKind);

    void deleteObject(String imageKey, BucketKind bucketKind);

    void deleteObjects(List<String> imageKeys, BucketKind bucketKind);
}
