# Log Agent Fix Suggestion — Record #1

## 에러 원인
변수 `value`가 `null`인 상태에서 `.toUpperCase()` 메서드를 호출하여 발생한 NullPointerException

## 병목 지점
TestErrorController.triggerNpe() 내의 객체 null 체크 누락

## 수정 제안
```
```java
    @GetMapping("/npe")
    public String triggerNpe() {
        String value = null;
        // Null 안전성을 확보하기 위해 조건문이나 Optional 활용
        return (value != null) ? value.toUpperCase() : "DEFAULT_VALUE";
    }
    ```
```

---
*이 파일은 Log Agent에 의해 자동 생성되었습니다.*
*트리거: `NullPointerException: Cannot invoke "String.toUpperCase()" because "value" is null`*
