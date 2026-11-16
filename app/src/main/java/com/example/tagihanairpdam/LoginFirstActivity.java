package com.example.tagihanairpdam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginFirstActivity extends AppCompatActivity {
    private static final String TAG = "LoginFirstActivity"; // Tambahkan TAG
    private TextView tvFullName, tvEmail;
    private EditText etPhone, etAddress, etNewPassword, etConfirmPassword;
    private Button btnSubmit;
    private String username, fullName, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        // Get data from intent
        username = getIntent().getStringExtra("username");
        fullName = getIntent().getStringExtra("fullName");
        email = getIntent().getStringExtra("email");

        Log.d(TAG, "onCreate: Received username: " + username + ", fullName: " + fullName + ", email: " + email); // Log data intent

        // Initialize views
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set non-editable user info
        tvFullName.setText(fullName);
        tvEmail.setText(email);

        btnSubmit.setOnClickListener(v -> {
            Log.d(TAG, "btnSubmit: Button clicked"); // Log tombol submit ditekan
            validateAndSubmit();
        });
    }

    private void validateAndSubmit() {
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        Log.d(TAG, "validateAndSubmit: Validating input - Phone: " + phone + ", Address: " + address); // Log input validasi

        if (phone.isEmpty()) {
            etPhone.setError("Nomor telepon wajib diisi");
            return;
        }

        if (address.isEmpty()) {
            etAddress.setError("Alamat wajib diisi");
            return;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError("Password baru wajib diisi");
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password minimal 6 karakter");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            return;
        }

        Log.d(TAG, "validateAndSubmit: Validation successful, calling updateUserData"); // Log validasi berhasil
        updateUserData(username, phone, address, newPassword);
    }

    private void updateUserData(String username, String phone, String address, String newPassword) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Pastikan model request sesuai
        LoginFirstRequest request = new LoginFirstRequest();
        request.setUsername(username);
        request.setPhone(phone);
        request.setAddress(address);
        request.setNewPassword(newPassword);

        // Tambahkan logging untuk debug
        Log.d(TAG, "Sending update request: " + new Gson().toJson(request));

        Call<DefaultResponse> call = apiService.updateFirstLoginData(request);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                progressDialog.dismiss();

                // Tambahkan logging response
                if (response.body() != null) {
                    Log.d(TAG, "Response: " + new Gson().toJson(response.body()));
                } else {
                    Log.e(TAG, "Empty response body");
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(LoginFirstActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginFirstActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Gagal memperbarui data";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(LoginFirstActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error response: " + response.code() + " - " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginFirstActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network failure: " + t.getMessage());
            }
        });
    }
}