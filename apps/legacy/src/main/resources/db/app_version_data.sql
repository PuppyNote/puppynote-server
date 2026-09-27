-- 앱 버전 정보 INSERT
-- 실행 전 테이블이 생성되어 있어야 합니다 (서버 최초 실행 후 실행)
-- latest_version/store_url은 기존 하드코딩 값(AppVersionServiceImpl, 커밋 2620364 "1.0.2로 수정")을 그대로 이관했습니다.
-- min_supported_version은 강제 업데이트가 없던 기존 동작을 유지하기 위해 latest_version과 동일하게, force_update는 false로 설정했습니다.
-- build_number는 기존 하드코딩 값에 없던 필드로, 실배포 버전과 짝이 맞는 값이 확인되지 않아 NULL로 둡니다.

INSERT INTO app_versions (platform, latest_version, min_supported_version, build_number, force_update, store_url, created_date, updated_date) VALUES
('IOS', '1.0.2', '1.0.2', NULL, false, 'https://apps.apple.com/kr/app/puppynote/id6760515755', NOW(), NOW()),
('AOS', '1.0.2', '1.0.2', NULL, false, 'https://play.google.com/store/apps/details?id=com.puppynote', NOW(), NOW());
