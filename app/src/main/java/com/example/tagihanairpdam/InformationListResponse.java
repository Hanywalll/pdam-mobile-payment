package com.example.tagihanairpdam;

import java.util.List;

public class InformationListResponse {
    private boolean success;
    private String message;
    private List<InformationModel> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<InformationModel> getData() { return data; }
}