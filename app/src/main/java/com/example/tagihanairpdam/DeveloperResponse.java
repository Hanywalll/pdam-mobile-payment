package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class DeveloperResponse {
    @SerializedName("message")
    private String message;

    public DeveloperResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}