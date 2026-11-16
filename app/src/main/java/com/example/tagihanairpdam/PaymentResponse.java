package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private PaymentTransaction data;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public PaymentTransaction getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}