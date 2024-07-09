package com.project.securelogin.jwt;

import com.project.securelogin.domain.CustomUserDetails;
import com.project.securelogin.repository.JwtTokenRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey; // 이미 Base64로 인코딩된 시크릿키

    @Value("${jwt.token-validity-in-seconds}")
    private long accessTokenValiditySeconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValiditySeconds;

    private final JwtTokenRedisRepository jwtTokenRedisRepository;
    private final UserDetailsService userDetailsService;

    // 주어진 Authentication 객체를 기반으로 JWT 토큰을 생성한다.
    public String createToken(Authentication authentication) {
        return generateToken(authentication, accessTokenValiditySeconds);
    }

    // 주어진 Authentication 객체를 기반으로 Refresh JWT 토큰을 생성한다.
    public String createRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshTokenValiditySeconds);
    }

    // JWT 토큰의 유효성 검사
    // 유효한 토큰일 경우 true, 그렇지 않으면 false 리턴
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 리프레시 토큰 검증
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT 토큰에서 사용자 이름을 추출
    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // HttpServletRequest에서 Authorization 헤더에서 JWT 토큰을 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Bearer 토큰을 제외한 JWT 토큰 리턴
        }
        return null;
    }

    // HttpServletRequest에서 Refresh 토큰을 추출
    public String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        System.out.println(refreshToken);
        return null;
    }


    // Refresh 토큰을 블랙리스트에 추가하고, 성공적으로 추가되면 true를 반환한다.
    public boolean blacklistRefreshToken(String refreshToken) {
        try {
            String tokenId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody().getId();

            if (tokenId == null) {
                throw new IllegalArgumentException("토큰에 jti 클레임이 포함되어 있지 않습니다.");
            }

            return jwtTokenRedisRepository.addTokenToBlacklist(tokenId, refreshTokenValiditySeconds);
        } catch (Exception e) {
            return false;
        }
    }


    // 블랙리스트에 있는지 확인 (Refresh Token)
    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        String tokenId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody().getId();
        return jwtTokenRedisRepository.isTokenBlacklisted(tokenId);
    }

    // Refresh 토큰을 사용하여 새로운 Access 토큰 생성
    public String createTokenFromRefreshToken(String refreshToken) {
        if (!validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = getEmail(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        return generateToken(new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()), accessTokenValiditySeconds);
    }

    // JWT 토큰 생성
    private String generateToken(Authentication authentication, long validitySeconds) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Claims claims = Jwts.claims().setSubject(userDetails.getEmail());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validitySeconds * 1000);

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setClaims(claims)
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
