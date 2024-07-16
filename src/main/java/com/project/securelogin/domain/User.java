package com.project.securelogin.domain;

import com.project.securelogin.dto.UserRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @CreationTimestamp
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    private boolean accountNonExpired; // 계정 만료 여부
    private boolean accountNonLocked; // 계정 잠김 여부
    private boolean credentialsNonExpired; // 자격 증명 만료 여부
    private boolean enabled; // 계정 활성화 여부

    private String mailVerificationToken; // 이메일 인증 토큰

    public void updateUser(UserRequestDTO requestDTO, PasswordEncoder passwordEncoder,String mailVerificationToken) {
        this.username = requestDTO.getUsername();
        this.email = requestDTO.getEmail();
        this.mailVerificationToken = mailVerificationToken;
        this.enabled = false;

        // 새로운 비밀번호가 null이 아니고, 기존 비밀번호와 다를 때만 인코딩하여 업데이트
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().equals(this.password)) {
            this.password = passwordEncoder.encode(requestDTO.getPassword());
        }
    }

    public void enableAccount() {
        this.enabled = true;
        this.mailVerificationToken = null;
    }

}
