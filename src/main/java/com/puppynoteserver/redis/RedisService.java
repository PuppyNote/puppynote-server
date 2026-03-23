package com.puppynoteserver.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    // String 타입 키에 값을 저장하고 TTL을 설정한다
    public void setValue(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
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

    // Lua 스크립트를 원자적으로 실행한다
    public <T> T execute(RedisScript<T> script, List<String> keys, String... args) {
        return redisTemplate.execute(script, keys, args);
    }
}
