package com.secondproject.secondproject.handler;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String token;
    private String name;

    public LoginResponseDto(String token, String name) {
        this.token = token;
        this.name = name;
    }
}
