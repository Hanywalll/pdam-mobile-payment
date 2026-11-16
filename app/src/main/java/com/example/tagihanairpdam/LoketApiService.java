package com.example.tagihanairpdam;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoketApiService {
    @GET("transaksi_pembayaran.php")
    Call<LoketTransaksiResponse> getTransaksiPembayaran(
            @Query("nosam") Integer nosam, // Ubah dari int ke Integer
            @Query("bulan") Integer bulan,
            @Query("tahun") Integer tahun
    );

    @GET("transaksi_pembayaran_filter.php")
    Call<LoketTransaksiResponse> getTransaksiByDate(
            @Query("start_date") String startDate,
            @Query("end_date") String endDate
    );

    @GET("transaksi_pembayaran_nosam_filter.php")
    Call<LoketTransaksiResponse> getTransaksiByNosamAndDate(
            @Query("nosam") int nosam,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate
    );

    @FormUrlEncoded
    @POST("get_customer.php?action=update_tpelang2_fields") // Ensure this path is correct
    Call<LoketUpdateTransaksiResponse> updateTpelang2Fields(
            @Field("nosam") int nosam,
            @Field("bulan") int bulan,
            @Field("tahun") int tahun,
            @Field("new_tanggal_bayar") String newTanggalBayar, // Format: YYYY-MM-DD or YYYY-MM-DD HH:mm:ss
            @Field("new_denda") double newDenda,
            @Field("new_petugas") String newPetugas // This is the USERIDT
    );

    @POST("update_transaksi.php")
    Call<UpdateTransaksiResponse> updateTransaksi(
            @Body UpdateTransaksiRequest request,
            @Query("force_update") boolean forceUpdate // Parameter opsional untuk memaksa update
    );

    @GET("lapangan.php")
    Call<UserListResponse> getPetugasLapangan();

}

