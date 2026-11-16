package com.example.tagihanairpdam;

public class AdminAccountModel {
    private int id;
    private String username;
    private String role;
    private String email;
    private String status;

    public AdminAccountModel(int id, String username, String role, String email, String status) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.status = status;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
}