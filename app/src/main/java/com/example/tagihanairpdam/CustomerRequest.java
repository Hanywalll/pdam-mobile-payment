package com.example.tagihanairpdam;

public class CustomerRequest {
    private String nosam;

    public CustomerRequest(String nosam) {
        this.nosam = nosam;
    }

    public String getNosam() {
        return nosam;
    }

    public void setNosam(String nosam) {
        this.nosam = nosam;
    }
}