---
name: Project Structure
description: Guide for Project Structure.
---
# Guide for Project Structure

## Overview
puppynote-server는 모놀리스(`apps/legacy`)에서 MSA로 전환 중이다. 새 서비스는 chatplanet-server를 참고한
Gradle 멀티모듈 + pragmatic hexagonal 구조로 만든다. `apps/user`, `apps/notification`이 실제 구현된 예시다.
`apps/legacy`는 옮기는 작업이 끝날 때까지 그대로 둔다 — 구조를 참고하되 legacy 파일은 옮기기 전까지 수정하지 않는다.

## When to use?
- 새 서비스(`apps/*`)를 만들 때, 또는 기존 서비스에 새 서브도메인/기능을 추가할 때
- legacy에서 도메인을 새 서비스로 이관할 때
- 어떤 패키지에 무엇을 둬야 할지 애매할 때

## 모듈 구조

```
puppynote-server/
├── apps/
│   ├── legacy/       # 기존 모놀리스, 원본 그대로 유지 (이관 대상)
│   ├── user/          # 회원가입/로그인/OAuth/이메일인증/세션(리프레시 토큰)
│   ├── notification/  # 알림 설정/알림 내역/디바이스 푸시 토큰/푸시 발송
│   └── ...             # 아직 비어있는 스켈레톤 (pet, community, foodChat 등)
└── contracts/
    └── common/         # 모든 서비스가 공유하는 인프라 (jwt, security, exception, storage, page, querydsl 등)
```

- 서비스(`apps/<service>`)는 각각 독립 배포 단위다. 자체 `build.gradle`(`org.springframework.boot` 플러그인 +
  `implementation project(':contracts:common')`)과 `XxxApplication.java`(`@SpringBootApplication(scanBasePackages = "com.puppynoteserver")`)를 가진다.
- lombok, QueryDSL 어노테이션 프로세서는 루트 `build.gradle`의 `subprojects` 블록에서 전체 모듈에 공통 주입된다
  (`compileOnly`/`annotationProcessor`는 project 의존으로 전파되지 않기 때문 — 각 서비스 build.gradle에 따로 선언할 필요 없음).
  `apps:legacy`만 원래 자체 선언을 쓰도록 제외되어 있다.
- `contracts/common`은 `java-library` + `api` 의존성으로, 여기 넣은 것들(spring-boot-starter류, querydsl-jpa,
  security, jwt 등)은 `implementation project(':contracts:common')` 하나로 컴파일 타임까지 전파된다.

## 서비스 내부 구조 (feature-first, pragmatic hexagonal)

서비스 하나가 서브도메인을 여러 개 가지면(`apps/user`의 `user`+`session`, `apps/notification`의
`alertSetting`+`alertHistory`+`push`+`shared`), 서브도메인별로 각자 헥사고날 레이어를 갖는다.

```
com.puppynoteserver.<service>.<subdomain>/
├── domain/
│   ├── entity/      # JPA 엔티티
│   ├── enums/
│   └── error/       # 도메인 전용 에러 메시지/코드
├── application/
│   ├── port/in/                 # 유스케이스 인터페이스 - 컨트롤러가 이걸 의존
│   │   ├── request/              # 서비스 계층 요청 DTO
│   │   └── response/             # 응답 DTO
│   ├── port/out/
│   │   └── persistence/          # Repository 인터페이스 (또는 out 바로 아래 외부연동 포트)
│   └── XxxService.java           # port/in 구현체 (@Service)
└── adapter/
    ├── in/web/
    │   ├── XxxController.java
    │   └── request/               # 컨트롤러 요청 DTO (toServiceRequest()로 변환)
    └── out/
        ├── persistence/           # JpaRepository + Repository 구현체
        └── client/                # 외부 API 어댑터 (Feign/RestTemplate/RestClient)
```

실제 예시: `apps/user/.../user/`(회원), `apps/user/.../session/`(리프레시 토큰),
`apps/notification/.../alertSetting/`, `.../alertHistory/`, `.../push/`.

### in-port / out-port 네이밍
- **in-port**: 책임 하나당 인터페이스 하나. `UserFinder`/`UserRegister`/`UserUpdater`/`UserRemover`,
  `LoginManager`, `SessionManager`, `AlertSettingFinder`/`AlertSettingUpdater`, `PushManager`처럼
  동사성 이름을 쓴다. 서비스 클래스(`UserService`, `AlertHistoryService` 등)가 필요한 만큼 여러 개를 `implements`한다.
- **out-port**: 저장소는 `port/out/persistence/XxxRepository`, 외부 시스템 연동은 `port/out/XxxSender`
  같은 이름으로 둔다 (`EmailSender`, `OAuthApiClient`, `PushSender`, `FileStorage`). 제공자가 바뀔 수 있는
  것(이메일 발송, OAuth, 파일 스토리지, 외부 푸시 발송)은 반드시 포트+어댑터로 분리한다 — 어댑터는
  `adapter/out/client`에, 구현 이유는 "제공자는 계약이 아니다".
- 로직 없이 Repository를 한 줄 감싸기만 하는 Read서비스는 만들지 않는다. Repository를 직접 의존해도 된다.

### 엔티티는 다른 서비스의 엔티티를 참조하지 않는다
서비스가 분리되면 서로 다른 DB를 쓰게 되므로, `@ManyToOne`/`@OneToMany`로 다른 서비스 엔티티를 참조할 수 없다.
지금은 한 DB를 같이 쓰고 있어도 **미리 `Long userId`처럼 ID만 저장**한다.
`apps/notification`의 `Push`/`AlertSetting`/`AlertHistory`가 `User`를 참조하지 않고 `userId`만 갖는 것,
`apps/user`의 `User`가 `RefreshToken`/`Push`에 대한 `@OneToMany`를 갖지 않는 것이 그 예다.
(chatplanet-server의 `apps/push`가 `User`를 `(appId, userId)`만 있는 로컬 사본으로 두는 것과 같은 이유.)

### 같은 서비스 안에서 다른 서브도메인 의존
- 다른 서브도메인의 **넓은 범용 Service**(여러 무관한 메서드가 섞인 CRUD 서비스)는 의존하지 않는다.
- 대신 ① 로직 없는 단순 조회/저장이면 그 서브도메인의 **Repository를 직접** 쓰거나,
  ② 재사용할 로직이 있으면 **책임이 좁고 자기 서브도메인 데이터만 건드리는 협력자**(`PushWriteService.upsertByDeviceId`,
  `SessionManager.upsertByDeviceId` 같은 것)를 의존한다.
- 판단 기준은 "Repository냐 Service냐"가 아니라 "이게 좁은 책임 하나만 갖고, 자기 데이터만 건드리는가"다.

### shared 패키지
서브도메인 두 개 이상이 같이 쓰는데 어느 한쪽 소유도 아닌 것(외부 발송 메커니즘, 공통 이벤트, 여러 서브도메인이 쓰는
값 타입)은 `<service>/shared/`에 둔다. `apps/notification/shared`(`PushDispatchService`, `PushSender`
포트, `ExpoPushAdapter`, 이벤트)가 예시 — push(토큰)와 alertHistory(로그) 양쪽 데이터를 다루므로 어느
서브도메인에도 속하지 않는다.

## contracts/common에 둘지 판단하는 기준
**여러 서비스가 공통으로 쓰고, 특정 서브도메인 데이터의 소유물이 아닌 것**만 여기 둔다.
- 이미 있는 것: `jwt`(인증), `global.security`(SecurityConfig/SecurityService), `global.exception`(예외/어드바이스),
  `global.ApiResponse`, `global.BaseTimeEntity`, `global.page`(페이지네이션 유틸), `global.config.QueryDSLConfiguration`,
  `storage`(FileStorage 포트 + S3StorageAdapter).
- 넣을 때는 `java-library` 플러그인의 `api` 의존성으로 선언해야 컴파일 타임까지 전파된다(`implementation`은 런타임에만 전파).
- `compileOnly`/`annotationProcessor`(lombok, querydsl-apt 등)는 어떻게 선언해도 project 의존으로는 전파되지
  않으므로 여기 넣는 게 아니라 루트 `build.gradle`의 `subprojects` 블록에 넣는다.
- 한 서비스에서만 쓰는 것(예: user의 `spring-boot-starter-mail`)은 그 서비스 build.gradle에만 선언한다.

## DTO/변환 컨벤션 (기존과 동일)
- 컨트롤러 요청은 `toServiceRequest()`로 서비스 계층 DTO로 변환한다.
- 응답 DTO는 `of()`/`createResponse()` 정적 팩토리로 엔티티를 변환한다.
- 컨트롤러 DTO와 서비스 DTO를 분리해서 관리한다 (`adapter/in/web/request` vs `application/port/in/request`).
