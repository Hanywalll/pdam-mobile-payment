package com.example.tagihanairpdam;

public class ResetPasswordRequest {
    private String email;

    // Constructor
    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String email) {
        this.email = email;
    }

    // Getter
    public String getEmail() {
        return email;
    }

    // Setter
    public void setEmail(String email) {
        this.email = email;
    }
}
