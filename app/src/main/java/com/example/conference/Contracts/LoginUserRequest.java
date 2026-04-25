package com.example.conference.Contracts;

public class LoginUserRequest {
    public String name;
    public String email;
    public String password;

    public LoginUserRequest( String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static LoginUserRequest Create(String email, String password) {
        return new LoginUserRequest( email, password);
    }

}
