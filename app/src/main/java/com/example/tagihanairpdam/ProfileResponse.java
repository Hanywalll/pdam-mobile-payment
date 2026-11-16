package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private ProfileModel data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ProfileModel getData() {
        return data;
    }
}