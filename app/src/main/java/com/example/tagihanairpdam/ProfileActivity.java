package com.example.tagihanairpdam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserRole, tvFullName, tvEmail, tvPhone, tvAddress;
    private Button btnEditProfile;
    private ImageButton btnKembali;
    private static final String TAG = "ProfileActivity";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi UI
        tvUserName = findViewById(R.id.tvUserName);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnKembali = findViewById(R.id.btnKembali);

        // Initialize and show ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sedang Memuat Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Set OnClickListener untuk tombol kembali
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Panggil metode onBackPressed untuk kembali ke aktivitas sebelumnya
            }
        });

        // Load user data
        loadUserData();

        // Edit profile button
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService api = retrofit.create(ApiService.class);
            Call<ProfileResponse> call = api.getProfile(username);

            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    // Dismiss ProgressDialog on response
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        ProfileModel profile = response.body().getData();

                        // Update UI
                        tvUserName.setText(profile.getUsername());
                        tvUserRole.setText(profile.getRole());
                        tvFullName.setText(profile.getFullName());
                        tvEmail.setText(profile.getEmail());
                        tvPhone.setText(profile.getPhone());
                        tvAddress.setText(profile.getAddress());

                        // Simpan data profil ke SharedPreferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("full_name", profile.getFullName());
                        editor.putString("email", profile.getEmail());
                        editor.putString("phone", profile.getPhone());
                        editor.putString("address", profile.getAddress());
                        editor.apply();

                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "Gagal memuat profil: " + (response.body() != null ?
                                        response.body().getMessage() : "Response kosong"),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    // Dismiss ProgressDialog on failure
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ProfileActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Dismiss ProgressDialog if username is null
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, "Gagal mengambil username dari SharedPreferences", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from EditProfileActivity
        // We don't show loading dialog again on resume, as data should be cached
        loadUserData();
    }

    @Override
    protected void onDestroy() {
        // Dismiss ProgressDialog to prevent window leaks
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}