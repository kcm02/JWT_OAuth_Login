package com.project.securelogin.service;

import com.project.securelogin.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public Authentication authenticate(String email, String password) throws AuthenticationException {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    public String createAccessToken(Authentication authentication) {
        return jwtTokenProvider.createToken(authentication);
    }

    public String createRefreshToken(Authentication authentication) {
        return jwtTokenProvider.createRefreshToken(authentication);
    }

    public void storeTokens(String email, String accessToken, String refreshToken) {
        redisTemplate.opsForValue().set("ACCESS_TOKEN:" + email, accessToken, 1, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("REFRESH_TOKEN:" + email, refreshToken, 7, TimeUnit.DAYS);
    }

    public void login(String email, String password) {
        Authentication authentication = authenticate(email, password);
        String accessToken = createAccessToken(authentication);
        String refreshToken = createRefreshToken(authentication);
        storeTokens(email, accessToken, refreshToken);
    }

    public String getAccessToken(String email) {
        return redisTemplate.opsForValue().get("ACCESS_TOKEN:" + email);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get("REFRESH_TOKEN:" + email);
    }
}
