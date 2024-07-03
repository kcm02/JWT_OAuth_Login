package com.project.securelogin.controller;

import com.project.securelogin.dto.UserRequestDTO;
import com.project.securelogin.dto.UserResponseDTO;
import com.project.securelogin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    // @Valid 어노테이션을 사용해 `SignUpRequest`의 유효성 검사를 활성화, 통과한 경우 서비스 코드 호출
    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.signUp(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }
}
