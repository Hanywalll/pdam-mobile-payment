package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class FeedbackItem {
    @SerializedName("sender_name")
    private String senderName;
    @SerializedName("sender_email")
    private String senderEmail;
    @SerializedName("sender_role")
    private String senderRole;
    @SerializedName("message")
    private String message;
    @SerializedName("created_at")
    private String createdAt;

    // Getters
    public String getSenderName() { return senderName; }
    public String getSenderEmail() { return senderEmail; }
    public String getSenderRole() { return senderRole; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
}