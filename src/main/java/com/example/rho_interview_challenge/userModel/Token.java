package com.example.rho_interview_challenge.userModel;

import lombok.Data;

@Data
public class Token {

    private String userId;
    private String accessToken;
    private String refreshToken;

    public Token() {

    }
}
