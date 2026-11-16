package com.example.tagihanairpdam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoketTransaksi implements Serializable {

    private int id;
    private int nosam;
    @SerializedName("nama_pelanggan")
    private String namaPelanggan;
    private String alamat;
    private String periode;
    private int bulan;
    private int tahun;
    @SerializedName("jumlah_tagihan")
    private double jumlahTagihan;
    private double denda;
    @SerializedName("total_bayar")
    private double totalBayar;
    @SerializedName("metode_bayar")
    private String metodeBayar;
    @SerializedName("kode_pembayaran")
    private String kodePembayaran;
    @SerializedName("tanggal_bayar")
    private String tanggalBayar;
    private String status;
    private String petugas; // Field lama untuk USERIDT
    @SerializedName("petugas_nama")
    private String petugasNama; // Field baru untuk nama lengkap petugas

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("post")
    private String post;

    // Constructor kosong (diperlukan untuk Gson)
    public LoketTransaksi() {}

    // Constructor dengan semua parameter (termasuk petugasNama)
    public LoketTransaksi(int id, int nosam, String namaPelanggan, String alamat, String periode,
                          int bulan, int tahun, double jumlahTagihan, double denda, double totalBayar,
                          String metodeBayar, String kodePembayaran, String tanggalBayar,
                          String status, String createdAt, String petugas, String petugasNama) {
        this.id = id;
        this.nosam = nosam;
        this.namaPelanggan = namaPelanggan;
        this.alamat = alamat;
        this.periode = periode;
        this.bulan = bulan;
        this.tahun = tahun;
        this.jumlahTagihan = jumlahTagihan;
        this.denda = denda;
        this.totalBayar = totalBayar;
        this.metodeBayar = metodeBayar;
        this.kodePembayaran = kodePembayaran;
        this.tanggalBayar = tanggalBayar;
        this.status = status;
        this.createdAt = createdAt;
        this.petugas = petugas;
        this.petugasNama = petugasNama;
    }

    // Getter dan Setter untuk semua field
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNosam() { return nosam; }
    public void setNosam(int nosam) { this.nosam = nosam; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getPeriode() { return periode; }
    public void setPeriode(String periode) { this.periode = periode; }

    public int getBulan() { return bulan; }
    public void setBulan(int bulan) { this.bulan = bulan; }

    public int getTahun() { return tahun; }
    public void setTahun(int tahun) { this.tahun = tahun; }

    public double getJumlahTagihan() { return jumlahTagihan; }
    public void setJumlahTagihan(double jumlahTagihan) { this.jumlahTagihan = jumlahTagihan; }

    public double getDenda() { return denda; }
    public void setDenda(double denda) { this.denda = denda; }

    public double getTotalBayar() { return totalBayar; }
    public void setTotalBayar(double totalBayar) { this.totalBayar = totalBayar; }

    public String getMetodeBayar() { return metodeBayar; }
    public void setMetodeBayar(String metodeBayar) { this.metodeBayar = metodeBayar; }

    public String getKodePembayaran() { return kodePembayaran; }
    public void setKodePembayaran(String kodePembayaran) { this.kodePembayaran = kodePembayaran; }

    public String getTanggalBayar() { return tanggalBayar; }
    public void setTanggalBayar(String tanggalBayar) { this.tanggalBayar = tanggalBayar; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getPetugas() { return petugas; }
    public void setPetugas(String petugas) { this.petugas = petugas; }

    public String getPetugasNama() { return petugasNama; }
    public void setPetugasNama(String petugasNama) { this.petugasNama = petugasNama; }

    public String getPost() { return post; }
    public void setPost(String post) { this.post = post; }
    @Override
    public String toString() {
        return "LoketTransaksi{" +
                "id=" + id +
                ", nosam=" + nosam +
                ", namaPelanggan='" + namaPelanggan + '\'' +
                ", alamat='" + alamat + '\'' +
                ", periode='" + periode + '\'' +
                ", bulan=" + bulan +
                ", tahun=" + tahun +
                ", jumlahTagihan=" + jumlahTagihan +
                ", denda=" + denda +
                ", totalBayar=" + totalBayar +
                ", metodeBayar='" + metodeBayar + '\'' +
                ", kodePembayaran='" + kodePembayaran + '\'' +
                ", tanggalBayar='" + tanggalBayar + '\'' +
                ", status='" + status + '\'' +
                ", petugas='" + petugas + '\'' +
                ", petugasNama='" + petugasNama + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", post='" + post + '\'' +
                '}';
    }

}