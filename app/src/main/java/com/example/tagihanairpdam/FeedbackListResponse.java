package com.example.tagihanairpdam;

import java.util.List;

public class FeedbackListResponse {
    private boolean success;
    private String message;
    private List<FeedbackItem> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<FeedbackItem> getData() { return data; }
}