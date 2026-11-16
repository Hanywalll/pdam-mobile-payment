package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class UpdateTransaksiRequest {
    @SerializedName("nosam")
    private int nosam;
    @SerializedName("bulan")
    private int bulan;
    @SerializedName("tahun")
    private int tahun;
    @SerializedName("tglbayar")
    private String tglbayar;
    @SerializedName("rpdenda")
    private double rpdenda;
    @SerializedName("useridt")
    private String useridt;
    @SerializedName("post")
    private String post; // Tambahkan field post

    // Perbarui konstruktor untuk menerima argumen post
    public UpdateTransaksiRequest(int nosam, int bulan, int tahun, String tglbayar, double rpdenda, String useridt, String post) {
        this.nosam = nosam;
        this.bulan = bulan;
        this.tahun = tahun;
        this.tglbayar = tglbayar;
        this.rpdenda = rpdenda;
        this.useridt = useridt;
        this.post = post; // Inisialisasi field post
    }

    // Getters
    public int getNosam() { return nosam; }
    public int getBulan() { return bulan; }
    public int getTahun() { return tahun; }
    public String getTglbayar() { return tglbayar; }
    public double getRpdenda() { return rpdenda; }
    public String getUseridt() { return useridt; }
    public String getPost() { return post; } // Tambahkan getter untuk post
}