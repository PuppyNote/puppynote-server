package com.puppynoteserver.global;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Profile("prd")
public class TestErrorController {

    @GetMapping("/error")
    public void triggerError() {
        throw new RuntimeException("log-agent 연동 테스트용 강제 에러입니다.");
    }

    @GetMapping("/npe")
        public String triggerNpe(@RequestParam(required = false) String value)
        {
            return (value != null) ? value.toUpperCase() : "";
        }
        String value = null;
        return value.toUpperCase();
    }
}
