# PuppyNote Server

반려견 케어 통합 관리 플랫폼의 백엔드 REST API 서버입니다.  
반려견 프로필·산책·용품 관리부터 AI 음식 안전 분석, 커뮤니티까지 하나의 앱에서 제공합니다.

---

## 기술 스택

| 분류 | 기술                                                       |
|---|----------------------------------------------------------|
| Language / Framework | Java 17, Spring Boot 3.4.2                               |
| ORM | Spring Data JPA, QueryDSL 5.0                            |
| Database | MySQL 8 (운영), H2 (테스트)                                   |
| Search | Elasticsearch 8 (커뮤니티 게시글·해시태그 검색)                       |
| Cache | Redis (좋아요 카운트·상태 캐시)                                    |
| AI | Spring AI + Ollama — LG EXAONE 3.5 (7.8b) — 강아지 음식 안전 분석 |
| Security | Spring Security, JWT (jjwt 0.11.5)                       |
| OAuth2 | Kakao / Google — Spring Cloud OpenFeign                  |
| Push | Firebase Cloud Messaging (FCM)                           |
| Storage | AWS S3 + CloudFront CDN                                  |
| Mail | Spring Mail (Gmail SMTP)                                 |
| Monitoring | Spring Actuator, Prometheus, P6Spy                       |
| Docs | Spring REST Docs                                         |
| Container | Docker (eclipse-temurin:17-jre-alpine)                   |

---

## 핵심 기능

### 1. 인증 & 사용자
- 이메일/비밀번호 회원가입 — 인메모리(`ConcurrentHashMap`)에 인증 코드 저장, 5분 만료 처리
- JWT Access Token + Refresh Token 발급 / 갱신 / 블랙리스트
- OAuth2 소셜 로그인 (Kakao, Google) — OpenFeign으로 유저 정보 조회 후 자동 가입
- 이메일 인증 기반 비밀번호 재설정

### 2. 반려견 관리
- 반려견 프로필 CRUD (사진 AWS S3 업로드 → CloudFront URL 반환)
- **가족 구성원 시스템**: 이메일 초대 → 수락 시 같은 반려견을 공동 관리

### 3. 산책 기록
- 산책 데이터(거리·시간·경로 등) 저장·조회
- 월별 캘린더 뷰 / 일별 목록 조회
- **산책 알람 배치**: 등록된 요일·시간 기준으로 10분 전 FCM 푸시 발송 (5분 주기 스케줄러)

### 4. 용품 관리
- 카테고리별 용품 등록 및 구매 이력 관리
- **구매 알림 배치**: 소진 예정일 전날 오전 8시 FCM 푸시 자동 발송

### 5. 커뮤니티 (게시글 + 좋아요)
- 게시글 CRUD — 이미지 다중 업로드(S3), Soft Delete
- **Elasticsearch** 기반 전문 검색 (키워드, 해시태그 자동완성)
- **좋아요 Redis 캐시**: 좋아요 카운트·상태를 Redis에 캐시, 캐시 미스 시만 DB 조회 → 고빈도 토글 부하 최소화

### 6. AI 음식 안전 분석
- 음식명 입력 시 **Ollama (qwen2.5:14b)** 가 수의학적 관점으로 분석
- 안전 등급 3단계: `GOOD(안전)` / `NOTION(주의)` / `BAD(위험)`
- **DB 캐시 전략**: 동일 질문은 DB에서 즉시 반환, 새 질문만 AI 호출 후 저장
- Elasticsearch로 음식 문서 검색 제공

### 7. 날씨
- Open-Meteo 외부 API 연동으로 현재 위치 날씨 제공 (산책 추천에 활용)

### 8. 알림
- FCM 기기 토큰 등록, 알림 히스토리 조회, 읽음 처리
- 알림 유형별 ON/OFF 설정

---

## 데이터 흐름

### 일반 요청 흐름
```
Client
  │
  ├─ JwtExceptionHandlerFilter   (JWT 예외 선처리)
  ├─ JwtAuthenticationFilter     (토큰 검증 → SecurityContext 저장)
  │
  └─ Controller
       ├─ @Valid 입력 검증
       ├─ request.toServiceRequest()  (Controller DTO → Service DTO 변환)
       └─ Service
            ├─ SecurityService.getCurrentLoginUserInfo()  (userId 추출)
            ├─ 비즈니스 로직
            ├─ request.toEntity()  (Service DTO → JPA Entity 변환)
            └─ Repository (QueryDSL / Spring Data JPA)
                  └─ MySQL
```

### 커뮤니티 좋아요 흐름
```
POST /api/v1/community/posts/{postId}/likes

PostLikeService
  ├─ Redis에 카운트 캐시 없음? → DB countByPostId → Redis setex
  ├─ Redis에 좋아요 상태 없음? → DB findByPostIdAndUserId → Redis setex
  └─ Lua 스크립트 atomic toggle
        ├─ 좋아요 ON  → Redis 카운트 +1 / 상태 true
        └─ 좋아요 OFF → Redis 카운트 -1 / 상태 false
              └─ 응답: { liked, likeCount }
```

### AI 음식 분석 흐름
```
POST /api/v1/foods/ai  { "question": "초콜릿" }

FoodChatService
  ├─ DB에 동일 질문 존재? → 즉시 반환 (캐시 히트)
  └─ 없으면 OllamaService.ask(question)
        └─ Spring AI ChatClient → Ollama (qwen2.5:14b)
              └─ JSON 구조체 파싱 { isFood, safetyLevel, answer }
                    ├─ isFood=false → PuppyNoteException("음식에 관한 질문만 해주세요.")
                    └─ isFood=true  → FoodChatHistory 저장 → 응답 반환
```

### 산책 알람 배치 흐름
```
@Scheduled(fixedRate = 5분)

WalkAlarmBatch
  └─ DB 조회: 현재시각 기준 10분 후 알람 대상 (활성 상태, 해당 요일)
        └─ FCM 푸시 발송
              └─ AlertHistory 저장 (발송 이력 기록)
```

### OAuth2 로그인 흐름
```
POST /api/v1/auth/oauth/login  { provider: "KAKAO", accessToken: "..." }

LoginService
  └─ OAuthApiClient.getUserInfo(accessToken)
        └─ OpenFeign → Kakao/Google API (유저 이메일·이름 조회)
              ├─ 신규 유저 → 자동 회원가입
              └─ 기존 유저 → 정보 갱신
                    └─ JWT AccessToken + RefreshToken 발급
```

---

## API 엔드포인트 요약

Base URL: `/puppynote`  
모든 응답은 `ApiResponse<T>` 포맷으로 래핑됩니다.

```json
{ "statusCode": 200, "httpStatus": "OK", "message": "OK", "data": { ... } }
```

| 도메인 | 경로 |
|---|---|
| 인증 | `POST /api/v1/auth/login` · `/oauth/login` · `/refresh` · `/password/reset` |
| 사용자 | `GET/PATCH /api/v1/user/profile` · `POST /signup` · `/email/send` |
| 반려견 | `GET/POST/PATCH/DELETE /api/v1/pets/{petId}` |
| 가족 구성원 | `GET/POST /api/v1/family-members` · `/invite` · `/register` |
| 산책 | `GET/POST/DELETE /api/v1/walks` · `/calendar` |
| 산책 알람 | `GET/POST/PUT/PATCH/DELETE /api/v1/pet-walk-alarms` |
| 용품 | `GET/POST /api/v1/pet-items` · `/{id}/purchases` |
| 커뮤니티 | `GET/POST/PATCH/DELETE /api/v1/community/posts` · `/hashtags` |
| 좋아요 | `POST /api/v1/community/posts/{postId}/likes` |
| AI 음식 분석 | `GET/POST /api/v1/foods` · `/ai` |
| 날씨 | `GET /api/v1/weather` |
| 알림 설정 | `GET/PATCH /api/v1/alert-setting` |
| 알림 히스토리 | `GET/PATCH /api/v1/alertHistories` |
| 홈 | `GET /api/v1/home` |
| 반려견 팁 | `GET /api/v1/pet-tips/random` |
| 파일 업로드 | `POST /api/v1/storage/{bucketKind}` |

---

## 프로젝트 구조

```
src/main/java/com/puppynoteserver/
├── global/          # 공통 (보안 설정, 예외 처리, ApiResponse, 이메일, 페이징)
├── jwt/             # JWT 생성·검증·필터
├── batch/           # 스케줄 배치 (산책 알람, 용품 구매 알림)
├── firebase/        # FCM 푸시 알림
├── redis/           # Redis 서비스 (좋아요 캐시 등)
├── storage/         # AWS S3 파일 업로드
├── user/            # 사용자, 인증, OAuth, 알림 카테고리
├── pet/             # 반려견, 가족구성원, 산책, 알람, 용품
├── community/       # 게시글, 좋아요
├── foodChat/        # AI 음식 분석 (Ollama + Elasticsearch)
├── alertSetting/    # 알림 설정
├── alertHistory/    # 알림 히스토리
├── weather/         # 날씨 (Open-Meteo API)
├── home/            # 홈 화면 집계
└── petTip/          # 랜덤 반려견 팁
```

---

## 환경 변수

`application-dev.yml` / `application-prd.yml` 실행 시 아래 환경 변수가 필요합니다.

| 변수명 | 설명 |
|---|---|
| `DB_HOST` | MySQL 호스트 |
| `DB_USERNAME` | MySQL 사용자명 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `JWT_SECRET_KEY` | JWT 서명 키 |
| `EMAIL_PASSWORD` | Gmail SMTP 앱 비밀번호 |
| `AWS_ACCESS_KEY` | AWS IAM Access Key |
| `AWS_SECRET_KEY` | AWS IAM Secret Key |
| `CLOUDFRONT_DOMAIN` | CloudFront 배포 도메인 |
| `ES_HOST` | Elasticsearch 호스트 |
| `ES_USERNAME` | Elasticsearch 사용자명 |
| `ES_PASSWORD` | Elasticsearch 비밀번호 |
| `REDIS_HOST` | Redis 호스트 |
| `REDIS_PASSWORD` | Redis 비밀번호 |
| `OLLAMA_HOST` | Ollama 서버 호스트 |

---

## 빌드 & 실행

```bash
# 빌드
./gradlew build

# 개발 서버 실행 (dev 프로파일)
./gradlew bootRun --args='--spring.profiles.active=dev'

# 테스트 실행
./gradlew test

# REST Docs 문서 생성
./gradlew asciidoctor

# Docker 이미지용 JAR 빌드
./gradlew bootJar
```

```bash
# Docker 실행
docker build -t puppynote-server .
docker run -p 8080:8080 \
  -e DB_HOST=... -e DB_USERNAME=... -e DB_PASSWORD=... \
  -e JWT_SECRET_KEY=... \
  puppynote-server
```

---

## 아키텍처 원칙

- **Request 변환 분리**: `Controller Request → toServiceRequest() → Service Request → toEntity() → Entity` 단방향 변환
- **사용자 컨텍스트**: Controller에서 userId를 직접 받지 않고 `SecurityService.getCurrentLoginUserInfo()`로만 획득
- **도메인 격리**: 서비스는 자신의 Repository만 직접 접근, 타 도메인 데이터는 해당 도메인 Service 호출
- **테스트**: `ControllerTestSupport`(WebMvcTest), `IntegrationTestSupport`(Full context), `RestDocsSupport`(REST Docs) 베이스 클래스 제공
