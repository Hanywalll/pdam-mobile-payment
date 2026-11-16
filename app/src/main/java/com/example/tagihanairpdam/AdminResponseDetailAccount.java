package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class AdminResponseDetailAccount {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("account")
    private AdminAccountDetailModel account;

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public AdminAccountDetailModel getAccount() {
        return account;
    }
}