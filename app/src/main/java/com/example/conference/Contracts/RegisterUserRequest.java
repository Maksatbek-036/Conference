package com.example.conference.Contracts;

public class RegisterUserRequest {
    public String name;
    public String email;
    public String password;

    public RegisterUserRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static RegisterUserRequest Create(String name, String email, String password) {
        return new RegisterUserRequest(name, email, password);
    }

}
