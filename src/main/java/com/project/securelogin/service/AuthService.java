package com.project.securelogin.service;

import com.project.securelogin.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public HttpHeaders login(String email, String password) {
        try {
            // 인증 수행
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            userDetailsService.processSuccessfulLogin(email);

            // 토큰 생성
            String accessToken = jwtTokenProvider.createToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.add("Refresh-Token", refreshToken);

            return headers;
        } catch (AuthenticationException e) {
            // 로그인 실패 처리
            userDetailsService.handleAccountStatus(email);
            userDetailsService.processFailedLogin(email);
            // 예외를 던짐
            throw e;
        }
    }

    public boolean logout(String token) {
        return jwtTokenProvider.blacklistRefreshToken(token);
    }

    public int getRemainingLoginAttempts(String email) {
        return userDetailsService.getRemainingLoginAttempts(email);
    }
}
