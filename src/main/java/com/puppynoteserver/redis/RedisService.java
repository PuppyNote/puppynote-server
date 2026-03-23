package com.puppynoteserver.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // 지정한 키가 존재하는지 확인한다
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 지정한 키를 삭제한다
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // fromKey를 toKey로 원자적으로 변경한다 (RENAME)
    // 처리 중 새 항목이 원본 키에 계속 쌓이도록 dirty set 교체 시 활용
    public void rename(String fromKey, String toKey) {
        redisTemplate.rename(fromKey, toKey);
    }

    // 지정한 키에 TTL을 설정한다
    public void expire(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }

    // String 타입 키의 값을 조회한다 (GET)
    // 키가 존재하지 않으면 null을 반환한다
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // String 타입 키에 값을 저장하고 TTL을 설정한다
    public void setValue(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    // 여러 String 키의 값을 한 번의 네트워크 호출로 조회한다 (MGET)
    // 존재하지 않는 키는 null로 반환된다
        return redisTemplate.opsForValue().multiGet(keys);
    }

    // Set 타입 키에 하나 이상의 멤버를 추가한다 (SADD)
    public void sAdd(String key, String... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    // Set 타입 키에서 특정 멤버를 제거한다 (SREM)
    public void sRem(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    // Set 타입 키의 모든 멤버를 반환한다 (SMEMBERS)
    public Set<String> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // Set 타입 키에 특정 값이 멤버로 존재하는지 확인한다 (SISMEMBER)
    public boolean sIsMember(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    // Set 타입 키를 커서 기반으로 청크 단위 순회해 전체 멤버를 반환한다 (SSCAN)
    // SMEMBERS 대신 사용해 대용량 Set 조회 시 Redis 블로킹을 방지한다
    public Set<String> sScan(String key, int chunkSize) {
        Set<String> result = new HashSet<>();
        try (Cursor<String> cursor = redisTemplate.opsForSet()
                .scan(key, ScanOptions.scanOptions().count(chunkSize).build())) {
            cursor.forEachRemaining(result::add);
        }
        return result;
    }

    // Lua 스크립트를 원자적으로 실행한다
    public <T> T execute(RedisScript<T> script, List<String> keys, String... args) {
        return redisTemplate.execute(script, keys, (Object[]) args);
    }
}
