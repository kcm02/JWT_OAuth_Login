package com.project.securelogin.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtTokenRedisRepository {

    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";
    private final RedisTemplate<String, String> redisTemplate;

    // 주어진 JWT 토큰을 블랙리스트에 추가한다. 이미 추가된 경우 false를 반환하고, 성공적으로 추가된 경우 true를 반환한다.
    public boolean addTokenToBlacklist(String tokenId, long expireInSeconds) {
        try {
            String key = BLACKLIST_KEY_PREFIX + tokenId;
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "true", expireInSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }


    // 주어진 JWT 토큰이 블랙리스트에 있는지 여부를 확인한다.
    public boolean isTokenBlacklisted(String tokenId) {
        if (redisTemplate == null) {
            return false;
        }
        String key = BLACKLIST_KEY_PREFIX + tokenId;
        return redisTemplate.hasKey(key);
    }

}
