package com.example.rho_interview_challenge.userModel;

import lombok.Data;

@Data
public class Login {

    private String userName;
    private String password;

    public Login() {

    }

    public Login(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }
}
