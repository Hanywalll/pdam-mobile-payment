package com.example.tagihanairpdam;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class CustomerResponse implements Parcelable {
    @SerializedName("message")
    private String message;

    @SerializedName("customer")
    private Customer customer;

    @SerializedName("rekaptulasi")
    private Rekaptulasi rekaptulasi;

    @SerializedName("transaksi")
    private java.util.List<Transaksi> transaksiList;

    protected CustomerResponse(Parcel in) {
        message = in.readString();
        customer = in.readParcelable(Customer.class.getClassLoader());
        rekaptulasi = in.readParcelable(Rekaptulasi.class.getClassLoader());
        transaksiList = in.createTypedArrayList(Transaksi.CREATOR);
    }

    public static final Creator<CustomerResponse> CREATOR = new Creator<CustomerResponse>() {
        @Override
        public CustomerResponse createFromParcel(Parcel in) {
            return new CustomerResponse(in);
        }

        @Override
        public CustomerResponse[] newArray(int size) {
            return new CustomerResponse[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Rekaptulasi getRekaptulasi() {
        return rekaptulasi;
    }

    public java.util.List<Transaksi> getTransaksiList() {
        return transaksiList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeParcelable(customer, flags);
        dest.writeParcelable(rekaptulasi, flags);
        dest.writeTypedList(transaksiList);
    }

    public static class Customer implements Parcelable {
        @SerializedName("nama")
        private String nama;

        @SerializedName("alamat")
        private String alamat;

        @SerializedName("kode_golongan")
        private String kodeGolongan;

        @SerializedName("status")
        private int status;

        protected Customer(Parcel in) {
            nama = in.readString();
            alamat = in.readString();
            kodeGolongan = in.readString();
            status = in.readInt();
        }

        public static final Creator<Customer> CREATOR = new Creator<Customer>() {
            @Override
            public Customer createFromParcel(Parcel in) {
                return new Customer(in);
            }

            @Override
            public Customer[] newArray(int size) {
                return new Customer[size];
            }
        };

        public String getNama() {
            return nama;
        }

        public String getAlamat() {
            return alamat;
        }

        public String getKodeGolongan() {
            return kodeGolongan;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(nama);
            dest.writeString(alamat);
            dest.writeString(kodeGolongan);
            dest.writeInt(status);
        }
    }

    public static class Rekaptulasi implements Parcelable {
        @SerializedName("tunggakan")
        private int tunggakan;

        @SerializedName("pemakaian_air")
        private int pemakaianAir;

        @SerializedName("rekening_air")
        private int rekeningAir;

        @SerializedName("denda")
        private int denda;

        @SerializedName("maintenance") // Tambahkan field maintenance
        private int maintenance;

        @SerializedName("res_kebersihan") // Tambahkan field res_kebersihan
        private int resKebersihan;

        @SerializedName("jumlah_tagihan")
        private int jumlahTagihan;

        protected Rekaptulasi(Parcel in) {
            tunggakan = in.readInt();
            pemakaianAir = in.readInt();
            rekeningAir = in.readInt();
            denda = in.readInt();
            maintenance = in.readInt(); // Baca maintenance
            resKebersihan = in.readInt(); // Baca resKebersihan
            jumlahTagihan = in.readInt();
        }

        public static final Creator<Rekaptulasi> CREATOR = new Creator<Rekaptulasi>() {
            @Override
            public Rekaptulasi createFromParcel(Parcel in) {
                return new Rekaptulasi(in);
            }

            @Override
            public Rekaptulasi[] newArray(int size) {
                return new Rekaptulasi[size];
            }
        };

        public int getTunggakan() {
            return tunggakan;
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

        public int getMaintenance() { // Getter untuk maintenance
            return maintenance;
        }

        public int getResKebersihan() { // Getter untuk resKebersihan
            return resKebersihan;
        }

        public int getJumlahTagihan() {
            return jumlahTagihan;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(tunggakan);
            dest.writeInt(pemakaianAir);
            dest.writeInt(rekeningAir);
            dest.writeInt(denda);
            dest.writeInt(maintenance); // Tulis maintenance
            dest.writeInt(resKebersihan); // Tulis resKebersihan
            dest.writeInt(jumlahTagihan);
        }
    }

    public static class Transaksi implements Parcelable {
        @SerializedName("periode_tagihan")
        private String periodeTagihan;

        @SerializedName("stan_lalu")
        private int stanLalu;

        @SerializedName("stan_sekarang")
        private int stanSekarang;

        @SerializedName("pemakaian_air")
        private int pemakaianAir;

        @SerializedName("biaya_pemakaian")
        private int biayaPemakaian;

        @SerializedName("biaya_administrasi")
        private int biayaAdministrasi;

        @SerializedName("biaya_pemeliharaan")
        private int biayaPemeliharaan;

        @SerializedName("rekening_air")
        private int rekeningAir;

        @SerializedName("denda")
        private int denda;

        @SerializedName("maintenance") // Tambahkan field maintenance
        private int maintenance;

        @SerializedName("res_kebersihan") // Tambahkan field res_kebersihan
        private int resKebersihan;

        @SerializedName("meter_kemarin") // Tambahkan field meter bulan kemarin
        private int meterKemarin;

        @SerializedName("meter_sekarang") // Tambahkan field meter bulan sekarang
        private int meterSekarang;

        @SerializedName("total_tagihan")
        private int totalTagihan;

        @SerializedName("status_pembayaran")
        private String statusPembayaran;

        @SerializedName("tanggal_bayar")
        private String tanggalBayar;

        @SerializedName("metode_bayar")
        private String metodeBayar;

        @SerializedName("tahun")
        private int tahun;

        @SerializedName("bulan")
        private int bulan;

        @SerializedName("kode_stan")
        private String kodeStan;

        private boolean isSelected = false;
        private boolean processing = false;

        protected Transaksi(Parcel in) {
            periodeTagihan = in.readString();
            stanLalu = in.readInt();
            stanSekarang = in.readInt();
            pemakaianAir = in.readInt();
            biayaPemakaian = in.readInt();
            biayaAdministrasi = in.readInt();
            biayaPemeliharaan = in.readInt();
            rekeningAir = in.readInt();
            denda = in.readInt();
            maintenance = in.readInt(); // Baca maintenance
            resKebersihan = in.readInt(); // Baca resKebersihan
            meterKemarin = in.readInt(); // Baca meter bulan kemarin
            meterSekarang = in.readInt(); // Baca meter bulan sekarang
            totalTagihan = in.readInt();
            statusPembayaran = in.readString();
            tanggalBayar = in.readString();
            metodeBayar = in.readString();
            tahun = in.readInt();
            bulan = in.readInt();
            kodeStan = in.readString();
            isSelected = in.readByte() != 0;
            processing = in.readByte() != 0;
        }

        public static final Creator<Transaksi> CREATOR = new Creator<Transaksi>() {
            @Override
            public Transaksi createFromParcel(Parcel in) {
                return new Transaksi(in);
            }

            @Override
            public Transaksi[] newArray(int size) {
                return new Transaksi[size];
            }
        };

        // --- TAMBAHKAN GETTER BERIKUT ---
        public int getTahun() {
            return tahun;
        }

        public int getBulan() {
            return bulan;
        }

        // --- LANJUTAN METHOD LAINNYA ---
        public boolean isProcessing() {
            return processing;
        }

        public void setProcessing(boolean processing) {
            this.processing = processing;
        }

        public String getStatusPembayaran() {
            return statusPembayaran;
        }

        public void setStatusPembayaran(String statusPembayaran) {
            this.statusPembayaran = statusPembayaran;
        }

        public int getRpair() {
            return pemakaianAir;
        }

        public String getPeriodeTagihan() {
            return periodeTagihan;
        }

        public int getStanLalu() {
            return stanLalu;
        }

        public int getStanSekarang() {
            return stanSekarang;
        }

        public int getPemakaianAir() {
            return pemakaianAir;
        }

        public int getBiayaPemakaian() {
            return biayaPemakaian;
        }

        public int getBiayaAdministrasi() {
            return biayaAdministrasi;
        }

        public int getBiayaPemeliharaan() {
            return biayaPemeliharaan;
        }

        public int getRekeningAir() {
            return rekeningAir;
        }

        public int getDenda() {
            return denda;
        }

        public int getMaintenance() { // Getter untuk maintenance
            return maintenance;
        }

        public int getResKebersihan() { // Getter untuk resKebersihan
            return resKebersihan;
        }

        public int getMeterKemarin() { // Getter untuk meter bulan kemarin
            return meterKemarin;
        }

        public int getMeterSekarang() { // Getter untuk meter bulan sekarang
            return meterSekarang;
        }

        public int getTotalTagihan() {
            return totalTagihan;
        }

        public String getTanggalBayar() {
            return tanggalBayar;
        }

        public String getMetodeBayar() {
            return metodeBayar;
        }

        public String getKodeStan() {
            return kodeStan;
        }

        public void setKodeStan(String kodeStan) {
            this.kodeStan = kodeStan;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(periodeTagihan);
            dest.writeInt(stanLalu);
            dest.writeInt(stanSekarang);
            dest.writeInt(pemakaianAir);
            dest.writeInt(biayaPemakaian);
            dest.writeInt(biayaAdministrasi);
            dest.writeInt(biayaPemeliharaan);
            dest.writeInt(rekeningAir);
            dest.writeInt(denda);
            dest.writeInt(maintenance); // Tulis maintenance
            dest.writeInt(resKebersihan); // Tulis resKebersihan
            dest.writeInt(meterKemarin); // Tulis meter bulan kemarin
            dest.writeInt(meterSekarang); // Tulis meter bulan sekarang
            dest.writeInt(totalTagihan);
            dest.writeString(statusPembayaran);
            dest.writeString(tanggalBayar);
            dest.writeString(metodeBayar);
            dest.writeInt(tahun);
            dest.writeInt(bulan);
            dest.writeString(kodeStan);
            dest.writeByte((byte) (isSelected ? 1 : 0));
            dest.writeByte((byte) (processing ? 1 : 0));
        }
    }
}