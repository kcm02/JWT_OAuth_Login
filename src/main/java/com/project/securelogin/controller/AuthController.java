package com.project.securelogin.controller;

import com.project.securelogin.dto.JsonResponse;
import com.project.securelogin.exception.UserAccountLockedException;
import com.project.securelogin.exception.UserNotEnabledException;
import com.project.securelogin.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            JsonResponse response = new JsonResponse(HttpStatus.OK.value(), "로그인에 성공했습니다.", null);
            return ResponseEntity.ok().headers(headers).body(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JsonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (UserNotEnabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JsonResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
        } catch (UserAccountLockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new JsonResponse(HttpStatus.LOCKED.value(), e.getMessage(), null));
        } catch (AuthenticationException e) {
            int remainingAttempts = authService.getRemainingLoginAttempts(authRequest.getEmail());
            String message = "이메일 주소나 비밀번호가 올바르지 않습니다. " +
                    remainingAttempts + "번 더 로그인에 실패하면 계정이 잠길 수 있습니다.";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JsonResponse(HttpStatus.UNAUTHORIZED.value(), message, null));
        }
    }


    @DeleteMapping("/logout")
    public ResponseEntity<JsonResponse> logout(@RequestHeader(name = "Refresh-Token") String refreshToken) {
        boolean logoutSuccess = authService.logout(refreshToken);

        if (logoutSuccess) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 접근입니다.", null));
        }
    }

    @Getter
    public static class AuthRequest {
        private String email;
        private String password;
    }
}

