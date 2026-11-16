package com.example.tagihanairpdam; // Sesuaikan dengan package Anda

import com.google.gson.annotations.SerializedName;

public class LoginFirstRequest {
    private String username;
    private String phone;
    private String address;
    @SerializedName("password") // Pastikan menggunakan huruf kecil
    private String password;

    // Constructor kosong (diperlukan oleh beberapa framework seperti Gson)
    public LoginFirstRequest() {
    }

    // Constructor dengan semua field
    public LoginFirstRequest(String username, String phone, String address, String newPassword) {
        this.username = username;
        this.phone = phone;
        this.address = address;
        this.password = newPassword;
    }

    // Getter untuk username
    public String getUsername() {
        return username;
    }

    // Setter untuk username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter untuk phone
    public String getPhone() {
        return phone;
    }

    // Setter untuk phone
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter untuk address
    public String getAddress() {
        return address;
    }

    // Setter untuk address
    public void setAddress(String address) {
        this.address = address;
    }

    // Getter untuk newPassword
    public String getNewPassword() {
        return password;
    }

    // Setter untuk newPassword
    public void setNewPassword(String newPassword) {
        this.password = newPassword;
    }
}