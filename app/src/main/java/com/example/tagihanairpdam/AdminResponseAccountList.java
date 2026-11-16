package com.example.tagihanairpdam;

import java.util.List;

public class AdminResponseAccountList {
    private boolean success;
    private String message;
    private List<AdminAccountModel> accounts;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<AdminAccountModel> getAccounts() { return accounts; }
}