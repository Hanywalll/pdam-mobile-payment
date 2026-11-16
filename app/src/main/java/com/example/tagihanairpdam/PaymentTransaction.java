package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class PaymentTransaction {

    @SerializedName("id")
    private int id;

    @SerializedName("nosam")
    private int nosam;

    @SerializedName("bulan")
    private int bulan;

    @SerializedName("tahun")
    private int tahun;

    @SerializedName("jumlah_tagihan")
    private double jumlahTagihan;

    @SerializedName("denda")
    private double denda;

    @SerializedName("total_bayar")
    private double totalBayar;

    @SerializedName("metode_bayar")
    private String metodeBayar;

    @SerializedName("kode_pembayaran")
    private String kodePembayaran;

    @SerializedName("tanggal_bayar")
    private String tanggalBayar;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String createdAt; // Tambahkan field created_at

    // Getter dan Setter untuk setiap field
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNosam() {
        return nosam;
    }

    public void setNosam(int nosam) {
        this.nosam = nosam;
    }

    public int getBulan() {
        return bulan;
    }

    public void setBulan(int bulan) {
        this.bulan = bulan;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public double getJumlahTagihan() {
        return jumlahTagihan;
    }

    public void setJumlahTagihan(double jumlahTagihan) {
        this.jumlahTagihan = jumlahTagihan;
    }

    public double getDenda() {
        return denda;
    }

    public void setDenda(double denda) {
        this.denda = denda;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(double totalBayar) {
        this.totalBayar = totalBayar;
    }

    public String getMetodeBayar() {
        return metodeBayar;
    }

    public void setMetodeBayar(String metodeBayar) {
        this.metodeBayar = metodeBayar;
    }

    public String getKodePembayaran() {
        return kodePembayaran;
    }

    public void setKodePembayaran(String kodePembayaran) {
        this.kodePembayaran = kodePembayaran;
    }

    public String getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(String tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}