package com.project.securelogin.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final String socialType;
    private final String id;
    private final String name;
    private final String email;

    public CustomOAuth2User(OAuth2User oAuth2User, String socialType) {
        this.oAuth2User = oAuth2User;
        this.socialType = socialType;
        this.id = extractId(oAuth2User, socialType);
        this.name = extractName(oAuth2User, socialType);
        this.email = extractEmail(oAuth2User, socialType);
    }

    private String extractId(OAuth2User oAuth2User, String socialType) {
        switch (socialType) {
            case "google":
                return (String) oAuth2User.getAttribute("sub");
            case "kakao":
                Object id = oAuth2User.getAttribute("id");
                return id != null ? id.toString() : null; // Long 타입을 String으로 변환
            case "naver":
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return (String) response.get("id");
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }
    }


    private String extractName(OAuth2User oAuth2User, String socialType) {
        switch (socialType) {
            case "google":
                return (String) oAuth2User.getAttribute("name");
            case "kakao":
                Map<String, Object> account = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                return (String) profile.get("nickname");
            case "naver":
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return (String) response.get("name");
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }
    }

    private String extractEmail(OAuth2User oAuth2User, String socialType) {
        switch (socialType) {
            case "google":
                return (String) oAuth2User.getAttribute("email");
            case "kakao":
                Map<String, Object> account = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
                return (String) account.get("email");
            case "naver":
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return (String) response.get("email");
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Object getAttribute(String name) {
        return oAuth2User.getAttribute(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 빈 권한 목록을 반환합니다.
        return Set.of();
    }
}