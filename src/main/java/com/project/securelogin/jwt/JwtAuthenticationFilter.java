package com.project.securelogin.jwt;

import com.project.securelogin.domain.CustomUserDetails;
import com.project.securelogin.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // JWT 토큰 추출
            String jwt = jwtTokenProvider.resolveToken(request);
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                // 토큰에서 사용자 정보 추출
                String email = jwtTokenProvider.getEmail(jwt);

                // 사용자 정보로부터 UserDetails 로드
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 객체 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 처리
            handleTokenExpiration(request, response);
            return;
        } catch (Exception e) {
            // 예외 발생 시 SecurityContext 초기화
            SecurityContextHolder.clearContext();
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // 토큰 만료 처리 로직
    private void handleTokenExpiration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Refresh Token 추출
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            // Refresh Token이 블랙리스트에 없으면 새로운 Access Token 발급
            if (!jwtTokenProvider.isRefreshTokenBlacklisted(refreshToken)) {
                String jwtToken = jwtTokenProvider.createTokenFromRefreshToken(refreshToken);
                response.setHeader("Authorization", "Bearer " + jwtToken);

                // 새로 발급받은 Access Token으로 UserDetails 로드
                String email = jwtTokenProvider.getEmail(jwtToken);
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

                // 인증 객체 생성 및 SecurityContext에 설정
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Refresh Token이 블랙리스트에 있으면 Unauthorized 에러 반환
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token is blacklisted");
            }
        } else {
            // 유효하지 않은 Refresh Token일 경우 SecurityContext 초기화 및 Unauthorized 에러 반환
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token");
        }
    }
}
