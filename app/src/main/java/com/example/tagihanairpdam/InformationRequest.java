package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class InformationRequest {
    private String title;
    private String message;
    @SerializedName("created_by") // Pastikan sesuai dengan yang diharapkan server
    private String createdBy;
    private String role;

    public InformationRequest(String title, String message, String createdBy, String role) {
        this.title = title;
        this.message = message;
        this.createdBy = createdBy;
        this.role = role;
    }

    // Getter methods
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCreatedBy() { return createdBy; }
    public String getRole() { return role; }
}