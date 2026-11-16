package com.example.tagihanairpdam;



import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}