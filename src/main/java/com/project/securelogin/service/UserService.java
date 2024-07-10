package com.project.securelogin.service;

import com.project.securelogin.domain.User;
import com.project.securelogin.dto.UserRequestDTO;
import com.project.securelogin.dto.UserResponseDTO;
import com.project.securelogin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO signUp(UserRequestDTO userRequestDTO) {
        // 이메일 중복 체크
        if (isEmailAlreadyExists(userRequestDTO.getEmail())) {
            throw new IllegalStateException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = encodePassword(userRequestDTO.getPassword());

        // 회원 정보 생성
        User user = User.builder()
                .username(userRequestDTO.getUsername())
                .password(encodedPassword)
                .email(userRequestDTO.getEmail())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        // 회원 저장
        userRepository.save(user);

        // UserResponseDTO 생성
        return new UserResponseDTO(user.getUsername(), user.getEmail());
    }

    // 회원 정보 조회
    public UserResponseDTO getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return new UserResponseDTO(user.get().getUsername(), user.get().getEmail());
        } else {
            throw new IllegalStateException("사용자를 찾을 수 없습니다.");
        }
    }

    // 회원 정보 수정
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO) {
        return userRepository.findById(userId).map(user -> {
            if (!user.getEmail().equals(userRequestDTO.getEmail()) && isEmailAlreadyExists(userRequestDTO.getEmail())) {
                throw new IllegalStateException("이미 등록된 이메일입니다.");
            }
            user.updateUser(userRequestDTO, passwordEncoder);
            userRepository.save(user);
            return new UserResponseDTO(user.getUsername(), user.getEmail());
        }).orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }

    // 회원 삭제
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new IllegalStateException("사용자를 찾을 수 없습니다.");
        }
    }

    // 이메일 중복 체크 메서드
    public boolean isEmailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // 비밀번호 암호화 메서드
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
