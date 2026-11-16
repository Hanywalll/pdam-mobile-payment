package com.example.tagihanairpdam;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLHandshakeException;

public class DeveloperActivity extends AppCompatActivity {

    private static final String TAG = "DeveloperActivity";
    private EditText etUsername, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerDeveloper());
    }

    private void registerDeveloper() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Semua field wajib diisi");
            return;
        }

        showToast("Mendaftarkan admin...");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<DeveloperResponse> call = apiService.createAdmin(
                new DeveloperRequest(username, password, "admin")
        );

        call.enqueue(new Callback<DeveloperResponse>() {
            @Override
            public void onResponse(Call<DeveloperResponse> call, Response<DeveloperResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Success: " + response.body().getMessage());
                    showToast("Admin berhasil dibuat!");
                    clearForm();
                } else {
                    handleServerError(response);
                }
            }

            @Override
            public void onFailure(Call<DeveloperResponse> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void handleServerError(Response<DeveloperResponse> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";

            Log.e(TAG, "Server error: " + response.code() + " - " + errorBody);

            switch (response.code()) {
                case 400:
                    showToast("Request tidak valid: " + errorBody);
                    break;
                case 401:
                    showToast("Autentikasi gagal");
                    break;
                case 500:
                    showToast("Server error, coba lagi nanti");
                    break;
                default:
                    showToast("Gagal membuat akun (" + response.code() + ")");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error parsing error response", e);
            showToast("Error parsing server response");
        }
    }

    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error", t);

        if (t instanceof SocketTimeoutException) {
            showToast("Timeout, periksa koneksi internet Anda");
        } else if (t instanceof ConnectException || t instanceof UnknownHostException) {
            showToast("Tidak dapat terhubung ke server");
        } else if (t instanceof SSLHandshakeException) {
            showToast("Error keamanan koneksi");
        } else {
            showToast("Gagal terhubung: " + t.getMessage());
        }
    }

    private void showToast(String message) {
        runOnUiThread(() ->
                Toast.makeText(DeveloperActivity.this, message, Toast.LENGTH_LONG).show()
        );
    }

    private void clearForm() {
        runOnUiThread(() -> {
            etUsername.setText("");
            etPassword.setText("");
        });
    }
}