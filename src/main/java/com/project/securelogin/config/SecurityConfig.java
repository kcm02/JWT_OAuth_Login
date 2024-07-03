package com.project.securelogin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean // 비밀번호 암호화를 위한 PasswordEncoder 빈 생성
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // AuthenticationManager 빈을 생성 (인증 요청 처리)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean // SecurityFilterChain 설정 (스프링 부트 3부터는 FilterChain 방식 사용)
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // REST API에서는 불필요한 CSRF 보안 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // JWT 토큰 인증 시스템을 사용할 것이기에 서버가 세션을 생성하지 않도록 한다.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP 요청에 대한 인가 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인과 회원가입 페이지는 누구나 접근 가능
                        .requestMatchers("/login", "/signup").permitAll()
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                );
//                // 폼 로그인 설정
//                .formLogin(form -> form
//                        .loginPage("/login") // 커스텀 로그인 페이지 설정
//                        .defaultSuccessUrl("/home",true) // 로그인 성공 시 이동할 페이지 설정
//                        .permitAll() // 로그인 페이지는 누구나 접근 가능
//                )
                // 로그아웃 설정
//                .logout(logout -> logout
//                        .logoutUrl("/logout") // 로그아웃 URL 설정
//                        .logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 이동할 페이지 설정
//                        .permitAll() // 로그아웃은 누구나 접근 가능
//                );

        return httpSecurity.build();
    }
}