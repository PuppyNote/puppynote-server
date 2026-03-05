# PuppyNote Server

반려견 케어 관리 플랫폼의 백엔드 REST API 서버입니다.

## 기술 스택

| 분류 | 기술 |
|---|---|
| Framework | Spring Boot 3.4.2, Java 17 |
| ORM | Spring Data JPA, QueryDSL 5.0 |
| DB | MySQL 8 (운영), H2 (테스트) |
| Security | Spring Security, JWT |
| OAuth2 | Kakao, Google (OpenFeign) |
| Push | Firebase FCM |
| Storage | AWS S3 |
| Mail | Spring Mail (Gmail SMTP) |
| Docs | Spring REST Docs |
| Monitoring | P6Spy |
| Container | Docker (eclipse-temurin:17-jre-alpine) |

## 빌드 & 실행

```bash
# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "com.puppynoteserver.ClassName"

# 개발 서버 실행 (dev 프로파일)
./gradlew bootRun --args='--spring.profiles.active=dev'

# REST API 문서 생성
./gradlew asciidoctor

# Docker 이미지용 JAR 빌드
./gradlew bootJar
```

## 환경 변수

`application-dev.yml` 및 `application-prd.yml` 실행 시 아래 환경 변수가 필요합니다.

| 환경 변수 | 설명 |
|---|---|
| `DB_HOST` | MySQL 호스트 |
| `DB_USERNAME` | MySQL 사용자명 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `MONGO_DB_URL` | MongoDB 연결 URL |
| `JWT_SECRET_KEY` | JWT 서명 키 |
| `EMAIL_PASSWORD` | Gmail SMTP 앱 비밀번호 |
| `AWS_ACCESS_KEY` | AWS IAM Access Key |
| `AWS_SECRET_KEY` | AWS IAM Secret Key |
| `PUPPY_PROFILE_BUCKET` | 반려견 프로필 이미지 S3 버킷명 |
| `WALK_PHOTO_BUCKET` | 산책 사진 S3 버킷명 |
| `PET_ITEM_PHOTO_BUCKET` | 용품 이미지 S3 버킷명 |
| `USER_PROFILE_BUCKET` | 사용자 프로필 이미지 S3 버킷명 |

## API 엔드포인트

Base URL: `/puppynote`
모든 응답은 `ApiResponse<T>` 포맷으로 래핑됩니다.

```json
{
  "statusCode": 200,
  "httpStatus": "OK",
  "message": "OK",
  "data": { ... }
}
```

### 인증 (`/api/v1/auth`)

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/auth/login` | 일반 로그인 |
| POST | `/api/v1/auth/oauth/login` | OAuth 소셜 로그인 (Kakao / Google) |

### 사용자 (`/api/v1/user`)

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/user/signup` | 회원가입 |
| POST | `/api/v1/user/email/send` | 이메일 인증 코드 발송 |
| GET | `/api/v1/user/profile` | 프로필 조회 |
| PATCH | `/api/v1/user/profile` | 프로필 수정 |

### 반려견 (`/api/v1/pets`)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/pets` | 반려견 목록 조회 |
| POST | `/api/v1/pets` | 반려견 등록 |
| PATCH | `/api/v1/pets/{petId}` | 반려견 정보 수정 |

### 가족 구성원 (`/api/v1/family-members`)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/family-members` | 가족 구성원 목록 조회 |
| GET | `/api/v1/family-members/search` | 사용자 검색 |
| POST | `/api/v1/family-members/invite` | 가족 구성원 초대 |
| POST | `/api/v1/family-members/register` | 초대 수락 및 등록 |

### 산책 (`/api/v1/walks`)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/walks` | 산책 목록 조회 |
| GET | `/api/v1/walks/{walkId}` | 산책 상세 조회 |
| GET | `/api/v1/walks/calendar` | 산책 캘린더 조회 |
| POST | `/api/v1/walks` | 산책 기록 저장 |

### 산책 알람 (`/api/v1/pet-walk-alarms`)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/pet-walk-alarms` | 산책 알람 목록 조회 |
| POST | `/api/v1/pet-walk-alarms` | 산책 알람 등록 |
| PUT | `/api/v1/pet-walk-alarms` | 산책 알람 수정 |
| PATCH | `/api/v1/pet-walk-alarms/status` | 산책 알람 상태 변경 |
| DELETE | `/api/v1/pet-walk-alarms/{alarmId}` | 산책 알람 삭제 |

### 용품 (`/api/v1/pet-items`)

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/pet-items` | 용품 등록 |
| GET | `/api/v1/pet-items` | 용품 목록 조회 |
| GET | `/api/v1/pet-items/{petItemId}` | 용품 상세 조회 |
| GET | `/api/v1/pet-items/categories` | 용품 카테고리 목록 조회 |
| POST | `/api/v1/pet-items/{petItemId}/purchases` | 용품 구매 기록 저장 |
| GET | `/api/v1/pet-items/{petItemId}/purchases` | 용품 구매 이력 조회 |

### 사용자 용품 카테고리 (`/api/v1/user-item-categories`)

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/user-item-categories` | 사용자 카테고리 저장 |
| GET | `/api/v1/user-item-categories` | 사용자 카테고리 목록 조회 |

### 알림 설정 (`/api/v1/alert-setting`)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/alert-setting` | 알림 설정 조회 |
| PATCH | `/api/v1/alert-setting` | 알림 설정 변경 |

### 알림 히스토리 (`/api/v1/alertHistories`)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/alertHistories` | 알림 히스토리 목록 조회 |
| GET | `/api/v1/alertHistories/unchecked` | 미확인 알림 수 조회 |
| PATCH | `/api/v1/alertHistories/{id}` | 알림 읽음 처리 |

### 홈 / 기타

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/home` | 홈 화면 데이터 조회 |
| GET | `/api/v1/pet-tips/random` | 랜덤 반려견 팁 조회 |
| POST | `/api/v1/storage/{bucketKind}` | 파일 업로드 (Presigned URL 발급) |

## 배치 작업

| 클래스 | 스케줄 | 설명 |
|---|---|---|
| `WalkAlarmBatch` | 5분마다 | 10분 후 산책 예정 알람 대상 조회 후 FCM 발송 |
| `PetItemPurchaseBatch` | 매일 오전 8시 | 내일 구매 예정 용품 FCM 알림 발송 |

## 아키텍처

도메인별 레이어드 패턴을 따릅니다.

```
Controller → Service → Repository
```

- `Controller`: 입력 검증 (`@Valid`), `toServiceRequest()` 변환 후 Service 호출
- `Service`: 비즈니스 로직, 현재 사용자는 `SecurityService.getCurrentLoginUserInfo()` 로 획득
- `Repository`: QueryDSL 기반 데이터 접근, 타 도메인 Repository 직접 접근 금지

## 프로젝트 구조

```
src/main/java/com/puppynoteserver/
├── global/          # 공통 (보안 설정, 예외 처리, 응답 포맷, 이메일, S3)
├── jwt/             # JWT 토큰 생성/검증 및 필터
├── batch/           # 스케줄 배치 (산책 알람, 용품 구매 알림)
├── firebase/        # Firebase FCM 푸시 알림
├── storage/         # 파일 스토리지 (S3 Presigned URL)
├── user/            # 사용자, 인증, OAuth, Push 토큰, 리프레시 토큰
├── pet/             # 반려견, 가족구성원, 산책, 산책알람, 용품, 용품구매
├── alertSetting/    # 알림 설정
├── alertHistory/    # 알림 히스토리
├── home/            # 홈 화면
└── petTip/          # 반려견 팁
```

## 테스트

```bash
# 전체 테스트
./gradlew test

# REST Docs 문서 생성 (테스트 실행 후 asciidoctor 변환)
./gradlew asciidoctor
```

테스트 환경에서는 H2 인메모리 DB와 MongoDB 비활성화(`application-test.yml`)가 적용됩니다.

## Docker

```bash
# JAR 빌드
./gradlew bootJar

# 이미지 빌드
docker build -t puppynote-server .

# 실행
docker run -p 8080:8080 \
  -e DB_HOST=... \
  -e DB_USERNAME=... \
  -e DB_PASSWORD=... \
  -e JWT_SECRET_KEY=... \
  puppynote-server
```
