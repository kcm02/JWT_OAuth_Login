package com.project.securelogin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JsonResponse {
    private final int statusCode;
    private final String message;
    private UserResponseDTO data;
}
