package com.kateryna.httptask.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String access_token;
    private String refresh_token;
}
