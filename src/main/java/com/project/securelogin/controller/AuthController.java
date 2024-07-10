package com.project.securelogin.controller;

import com.project.securelogin.dto.JsonResponse;
import com.project.securelogin.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JsonResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            HttpHeaders headers = authService.login(authRequest.getEmail(), authRequest.getPassword());

            JsonResponse response = new JsonResponse(HttpStatus.OK.value(),  "로그인에 성공했습니다.", null);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } catch (AuthenticationException e) {
            JsonResponse errorResponse = new JsonResponse(HttpStatus.UNAUTHORIZED.value(), "이메일 주소나 비밀번호가 올바르지 않습니다.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<JsonResponse> logout(@RequestHeader(name = "Refresh-Token") String refreshToken) {
        boolean logoutSuccess = authService.logout(refreshToken);

        if (logoutSuccess) {
            JsonResponse response = new JsonResponse(HttpStatus.NO_CONTENT.value(), "성공적으로 로그아웃되었습니다.", null);
            return ResponseEntity.ok().body(response); // 204 No Content
        } else {
            JsonResponse errorResponse = new JsonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 접근입니다.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // or INTERNAL_SERVER_ERROR
        }
    }

    @Getter
    public static class AuthRequest {
        private String email;
        private String password;
    }

}
