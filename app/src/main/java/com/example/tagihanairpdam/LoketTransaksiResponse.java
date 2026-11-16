package com.example.tagihanairpdam;

import java.util.List;

public class LoketTransaksiResponse {

    private boolean success;
    private String message;
    private List<LoketTransaksi> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<LoketTransaksi> getData() {
        return data;
    }
}