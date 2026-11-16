package com.example.tagihanairpdam;

public class AdminResponseAccount {
    private boolean success;
    private String message;
    private int id;
    private String username;
    private String email;
    private String role;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}