package com.puppynoteserver.global.test;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Profile("dev")
@RequestMapping("/api/v1/test")
public class MemoryTestController {

    private final List<byte[]> memoryHolder = new ArrayList<>();

    // 메모리를 채워 GC 유발 후에도 80% 이상 유지
    @PostMapping("/memory/pressure")
    public String fillMemory() {
        // 10MB 씩 20번 = 200MB 할당
        for (int i = 0; i < 20; i++) {
            memoryHolder.add(new byte[10 * 1024 * 1024]);
        }

        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        long usagePercent = used * 100 / max;

        return String.format("메모리 할당 완료 - 사용률: %d%% (%dMB / %dMB)",
                usagePercent, used / 1024 / 1024, max / 1024 / 1024);
    }

    // GC 강제 실행 (메모리는 유지)
    @PostMapping("/memory/gc")
    public String forceGc() {
        System.gc();

        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        long usagePercent = used * 100 / max;

        return String.format("GC 실행 완료 - 사용률: %d%% (%dMB / %dMB)",
                usagePercent, used / 1024 / 1024, max / 1024 / 1024);
    }

    // 메모리 해제
    @DeleteMapping("/memory/release")
    public String releaseMemory() {
        memoryHolder.clear();
        System.gc();

        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        long usagePercent = used * 100 / max;

        return String.format("메모리 해제 완료 - 사용률: %d%% (%dMB / %dMB)",
                usagePercent, used / 1024 / 1024, max / 1024 / 1024);
    }
}
