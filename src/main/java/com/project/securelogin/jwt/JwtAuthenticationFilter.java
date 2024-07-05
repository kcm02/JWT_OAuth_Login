package com.project.securelogin.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 토큰을 사용해 인증을 처리하는 필터
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    // HTTP 요청을 필터링하여 JWT 토큰을 검증하고, 사용자를 인증한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 요청에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 추출한 토큰의 유효성 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 통과하면 토큰에서 이메일을 가져온다.
            String email = jwtTokenProvider.getEmail(token);

            // 이메일을 사용해 사용자 정보를 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 사용자 정보와 권한을 사용해 인증 객체를 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            // 인증 객체에 요청의 세부 정보를 추가
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContextHolder를 사용하여 인증 객체를 설정
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 다음 필터 체인으로 제어를 넘긴다.
        chain.doFilter(request, response);
    }
}
