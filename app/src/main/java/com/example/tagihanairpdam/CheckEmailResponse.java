package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class CheckEmailResponse {
    @SerializedName("exists")
    private boolean exists;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isExists() {
        return exists;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}