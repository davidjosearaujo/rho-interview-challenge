package com.example.rho_interview_challenge.userModel;

import lombok.Data;

@Data
public class SignUp {

    private String userName;
    private String password;

    public SignUp() {

    }

    public SignUp(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }
}