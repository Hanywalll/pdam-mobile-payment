package com.example.tagihanairpdam;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("users_register_page.php")
    Call<Void> register(@Body UserRegistrationRequest request);

//    @POST("admin_register_page.php")
//    Call<AdminResponse> createUsers(@Body AdminRequest adminRequest);

    @POST("developer_register_page.php") // Endpoint untuk registrasi
    Call<DeveloperResponse> createAdmin(@Body DeveloperRequest developerRequest);

    @POST("admin_create_account.php")
    Call<AdminResponseAccount> createAccount(@Body AdminRequestAccount request);

    @FormUrlEncoded
    @POST("check_email.php") // Make sure this endpoint exists
    Call<CheckEmailResponse> checkEmail(
            @Field("email") String email,
            @Field("action") String action
    );


    @GET("admin_get_account.php")
    Call<AdminResponseAccountList> getAccounts();

    @POST("reset_password.php")
    Call<ForgotPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("get_customer.php")
    Call<CustomerResponse> getCustomerData(@Body CustomerRequest request);

    @POST("get_customer.php?action=bayar")
    Call<PaymentResponse> processPayment(@Body Map<String, Object> paymentRequest);

//    @GET("get_customer.php?action=cek_status")
//    Call<PaymentResponse> getPaymentStatus(@Query("kode") String kode, @Query("nosam") Integer nosam);

    @FormUrlEncoded
    @POST("update_last_login.php") // <-- pastikan path endpoint sesuai dengan backend-mu
    Call<DefaultResponse> updateLastLogin(@Field("username") String username);

    @POST("update_first_login.php")
    Call<DefaultResponse> updateFirstLoginData(@Body LoginFirstRequest request);

    @FormUrlEncoded
    @POST("get_profile.php")
    Call<ProfileResponse> getProfile(@Field("username") String username);

    @GET("admin_get_account_details.php")
    Call<AdminResponseDetailAccount> getAccountDetails(@Query("username") String username);
    @FormUrlEncoded
    @POST("admin_delete_account.php")
    Call<AdminDeleteAccountResponse> deleteAccount(@Field("username") String username);

    @FormUrlEncoded
    @POST("update_profile.php")
    Call<DefaultResponse> updateProfile(
            @Field("username") String username,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("address") String address
    );

    @POST("masukan.php")
    Call<FeedbackResponse> sendFeedback(@Body FeedbackRequest request);

    @GET("masukan.php")
    Call<FeedbackListResponse> getFeedback(@Query("role") String role);

    @POST("informasi.php")
    Call<InformationResponse> createInformation(@Body InformationRequest request);
    @GET("informasi.php")
    Call<InformationListResponse> getInfo();
}