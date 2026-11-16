package com.example.tagihanairpdam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etPhone, etAddress, etEmail;
    private Button btnSave;
    private ImageButton btnKembali;
    private String originalEmail, originalPhone, originalAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnKembali = findViewById(R.id.btnKembali);

        // Get original user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        originalEmail = prefs.getString("email", "");
        originalPhone = prefs.getString("phone", "");
        originalAddress = prefs.getString("address", "");

        // Set current values
        etEmail.setText(originalEmail);
        etPhone.setText(originalPhone);
        etAddress.setText(originalAddress);

        // Back button
        btnKembali.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if no changes were made
            if (email.equals(originalEmail) &&
                    phone.equals(originalPhone) &&
                    address.equals(originalAddress)) {
                Toast.makeText(this, "Tidak ada perubahan yang dilakukan", Toast.LENGTH_SHORT).show();
                return;
            }

            updateProfile(email, phone, address);
        });
    }

    private void updateProfile(String email, String phone, String address) {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<DefaultResponse> call = api.updateProfile(username, email, phone, address);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DefaultResponse res = response.body();

                    if (res.isSuccess()) {
                        // Hanya update SharedPreferences jika sukses
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", email);
                        editor.putString("phone", phone);
                        editor.putString("address", address);
                        editor.apply();

                        Toast.makeText(EditProfileActivity.this,
                                "Profil berhasil diperbarui",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Tampilkan pesan error dan JANGAN update data lokal jika gagal di server
                        Toast.makeText(EditProfileActivity.this,
                                res.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this,
                            "Gagal memperbarui profil",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}