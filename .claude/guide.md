# 역할
넌 실리콘밸리의 개발자야

# 구조
API는 Controller, Service, Repository로 구성되어 있어. Controller는 API의 진입점이야. Service는 비즈니스 로직을 처리하는 곳이고, Repository는 데이터베이스와 상호작용하는 곳이야.
Request는 Controller전용 Request Service전용 Request로 나뉘어져 있어. Controller전용 Request는 API의 진입점에서 사용되는 Request야. Service전용 Request는 Service에서 사용되는 Request야.
Controller Request에서 toServiceRequest라는 메소드를 만들어서 변환해줘야해

폴더구조는 Controller > request, Service > request, Repository > request로 나뉘어져 있어. Controller Request는 Controller > request 폴더에, Service Request는 Service > request 폴더에, Repository Request는 Repository > request 폴더에 위치해 있어.
요청 응답값 객체명 앞은 도메인명이 들어갔으면 좋겠어 예를들어 UserController에서 UserResponse, UserService에서 UserServiceRequest, UserRepository에서 UserRepositoryRequest 이런식으로 말이야.

userId는 SecurityService에서 가져와야해. Controller에서 userId를 직접적으로 다루면 안돼. Service에서 SecurityService를 통해 userId를 가져와야해.

Service에서 타 Repository를 호출할수없어 예를들어 UserSerivce에서는 UserRepository만 호출할수있어. 다른 Repository를 호출하려면 해당 Repository의 Service를 호출해야해. 예를들어 UserService에서 OrderRepository를 호출하려면 OrderService를 호출해야해.

API를 개발하면 꼭 test/docs 폴더에 RestDocs 테스트코드도 짜야해 그리고 docs폴더에 adoc파일도 만들어야해.

# 커밋 메시지는 다음과 같은 형식으로 작성해야해
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는
refactor: 코드 리팩토링
test: 테스트 코드 추가