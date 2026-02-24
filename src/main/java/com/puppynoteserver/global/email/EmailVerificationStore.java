package com.puppynoteserver.global.email;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmailVerificationStore {

    private final Map<String, VerificationEntry> store = new ConcurrentHashMap<>();

    public void save(String email, String code) {
        store.put(email, new VerificationEntry(code, LocalDateTime.now().plusMinutes(5)));
    }

    public boolean verify(String email, String code) {
        VerificationEntry entry = store.get(email);
        if (entry == null) return false;
        if (entry.expiredAt().isBefore(LocalDateTime.now())) {
            store.remove(email);
            return false;
        }
        return entry.code().equals(code);
    }

    public void remove(String email) {
        store.remove(email);
    }

    private record VerificationEntry(String code, LocalDateTime expiredAt) {}
}
