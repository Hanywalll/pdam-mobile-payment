package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class PaymentTransactionResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private PaymentTransaction data;

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public PaymentTransaction getData() {
        return data;
    }

    public void setData(PaymentTransaction data) {
        this.data = data;
    }
}