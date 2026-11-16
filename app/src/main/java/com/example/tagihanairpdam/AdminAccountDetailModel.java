package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class AdminAccountDetailModel {
    @SerializedName("id")
    private int id;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("username")
    private String username;
    @SerializedName("role")
    private String role;

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}