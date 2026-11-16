package com.example.tagihanairpdam;

public class User {
    private String username;
    private String role;

    // Konstruktor
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public User() {} //Tambahkan konstruktor default

    // Getter untuk username
    public String getUsername() {
        return username;
    }

    // Getter dan Setter untuk username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter untuk role (opsional, mungkin tidak perlu di UI)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return username; // Agar spinner menampilkan username
    }
}
