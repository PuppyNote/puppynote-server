package com.puppynoteserver.global;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Profile("prd")
public class TestErrorController {

    @GetMapping("/error")
    public void triggerError() {
        throw new RuntimeException("log-agent 연동 테스트용 강제 에러입니다.");
    }

    @GetMapping("/npe")
    public String triggerNpe(@RequestParam(name = "type") String type) {
        return (type != null) ? type.toUpperCase() : "UNKNOWN";
    }
}