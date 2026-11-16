package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class UpdateTransaksiResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}