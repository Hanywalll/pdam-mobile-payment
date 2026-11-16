package com.example.tagihanairpdam;

import java.util.List;

public class UserListResponse {
    private boolean success;
    private List<String> data;
    private String message; // Tambahkan field message

    public boolean isSuccess() {
        return success;
    }

    public List<String> getData() {
        return data;
    }

    public String getMessage() { // Tambahkan getMessage()
        return message;
    }

    public void setMessage(String message) { //Tambahkan setMessage()
        this.message = message;
    }
}