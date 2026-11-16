package com.example.tagihanairpdam;

public class AdminRequestAccount {
    private final String fullName;
    private final String email;
    private final String username;
    private final String password;
    private final String role;

    public AdminRequestAccount(String fullName, String email, String username,
                               String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}