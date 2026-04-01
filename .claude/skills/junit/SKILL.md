---
name: junit
description: Junit 테스트 코드 작성 가이드
---
# Junit 테스트 코드 작성 가이드

## 개요
Controller, Service, Repository 등 각 계층의 단위 테스트를 작성할 때 Junit을 사용하여 테스트 코드를 작성하는 방법에 대한 가이드입니다.


## 언제 사용하나요?
- 사용자가 Junit 테스트 코드를 작성해달라고 요청했을 때
- 사용자가 기능 추가를 요청했을 때
- 버그가 발생했을 때
- 사용자가 기능 수정을 요청했을 때

## Junit 테스트 코드 작성 방법
- 테스트 메소드에는 @DisplayName 어노테이션을 사용하여 테스트의 목적을 한국어로 명확히 나타냅니다.
- 테스트 메소드명은 한국어로 띄어쓰기는 _로 구분하여 작성합니다.
- 각 계층별로 테스트 클래스를 작성합니다.
- 외부 API를 호출하는 부분은 IntegrationTestSupport 클래스에 Mocking하여 테스트합니다.
- Controller를 테스트할때는 Service테스트는 필요없기 때문에 ControllerTestSupport에서 Mocking하여 테스트합니다.
- Assertion은 AssertJ를 사용하여 테스트할 때는 개별적으로 하지말고 contains, containsExactly, containsExactlyInAnyOrder 등을 활용하여 테스트합니다.
- 테스트 메소드 이름은 given_when_then 형식으로 작성하여 테스트의 목적을 명확히 나타냅니다.
- 테스트 메소드 내부에서는 하나의 기능만 테스트하도록 작성합니다.
- 테스트 데이터는 테스트 메소드 내부에서 직접 생성하거나, @BeforeEach 어노테이션을 사용하여 공통적으로 필요한 데이터를 설정합니다.