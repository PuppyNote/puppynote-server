---
name: Restdocs
description: Restdocs 테스트 코드 작성 가이드
---
# Restdocs 테스트 코드 작성 가이드

## 개요
Controller, Service, Repository 등 각 계층의 단위 테스트를 작성할 때 Junit을 사용하여 테스트 코드를 작성하는 방법에 대한 가이드입니다.


## 언제 사용하나요?
- 사용자가 Restdocs 테스트 코드를 작성해달라고 요청했을 때
- 사용자가 기능 추가를 요청했을 때
- 버그가 발생했을 때
- 사용자가 기능 수정을 요청했을 때

## Restdocs 테스트 코드 작성 방법
- Controller의 명세를 test/java/docs 폴더 하위에 작성합니다.
- 패키지 구조는 실제 Controller의 패키지 구조와 동일하게 맞춰줍니다.
- Service는 MockBean을 사용하여 Service 레이어를 Mocking하여 테스트합니다.
- src/docs/asciidoc 폴더에 API 명세를 작성합니다.
- API 명세는 실제 API 명세와 동일하게 작성합니다.