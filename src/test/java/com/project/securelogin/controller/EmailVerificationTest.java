package com.project.securelogin.controller;

import com.project.securelogin.dto.JsonResponse;
import com.project.securelogin.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EmailVerificationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testEmailVerificationLink() throws Exception {
        String token = "67b9c374-f196-47db-9e88-7641c85912e7";
        String verificationUrl = "/user/verify/" + token;

        // Mock 객체 설정
        JsonResponse jsonResponse = new JsonResponse(HttpStatus.OK.value(), "이메일 인증이 완료되었습니다.", null);
        when(userService.verifyEmail(anyString())).thenReturn(jsonResponse.getData());

        // MockMvc를 이용한 요청 및 응답 검증
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(MockMvcRequestBuilders.get(verificationUrl))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"status\":200,\"message\":\"이메일 인증이 완료되었습니다.\",\"data\":null}"));
    }
}
