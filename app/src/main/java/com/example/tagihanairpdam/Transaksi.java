package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

public class Transaksi {
    @SerializedName("periode_tagihan")
    private String periodeTagihan;

    @SerializedName("pemakaian_air")
    private int pemakaianAir;

    @SerializedName("rekening_air")
    private int rekeningAir;

    @SerializedName("denda")
    private int denda;

    @SerializedName("total_tagihan")
    private int totalTagihan;

    @SerializedName("status_pembayaran")
    private String statusPembayaran;

    @SerializedName("tanggal_bayar")
    private String tanggalBayar;



    // Getters
    public String getPeriodeTagihan() {
        return periodeTagihan;
    }

    public int getPemakaianAir() {
        return pemakaianAir;
    }

    public int getRekeningAir() {
        return rekeningAir;
    }

    public int getDenda() {
        return denda;
    }

    public int getTotalTagihan() {
        return totalTagihan;
    }

    public String getStatus_Pembayaran() {
        return statusPembayaran;
    }

    public String getTanggalBayar() {
        return tanggalBayar;
    }
}