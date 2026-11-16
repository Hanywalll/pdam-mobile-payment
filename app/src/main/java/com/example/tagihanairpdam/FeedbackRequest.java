package com.example.tagihanairpdam;

public class FeedbackRequest {
    private String sender_name;
    private String sender_email;
    private String sender_role;
    private String message;

    public FeedbackRequest(String sender_name, String sender_email, String sender_role, String message) {
        this.sender_name = sender_name;
        this.sender_email = sender_email;
        this.sender_role = sender_role;
        this.message = message;
    }

    // Getters
    public String getSender_name() { return sender_name; }
    public String getSender_email() { return sender_email; }
    public String getSender_role() { return sender_role; }
    public String getMessage() { return message; }
}
