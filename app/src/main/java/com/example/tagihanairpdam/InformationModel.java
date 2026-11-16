package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class InformationModel{
    @SerializedName("title")
    private String title;
    @SerializedName("message")
    private String message;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("created_at")
    private String createdAt;

    // Getters
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
}
