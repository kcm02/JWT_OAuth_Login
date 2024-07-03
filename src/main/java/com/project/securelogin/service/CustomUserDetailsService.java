package com.project.securelogin.service;

import com.project.securelogin.domain.CustomUserDetails;
import com.project.securelogin.domain.User;
import com.project.securelogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override // 이메일을 기준으로 사용자를 로드하는 메서드
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 조회된 사용자 정보를 기반으로 CustomUserDetails 객체 생성 후 반환
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isAccountNonExpired(), // 계정 만료 여부
                user.isAccountNonLocked(), // 계정 잠김 여부
                user.isCredentialsNonExpired(), // 자격 증명 만료 여부
                user.isEnabled(), // 계정 활성화 여부
                Collections.emptyList()
        );

    }
}
