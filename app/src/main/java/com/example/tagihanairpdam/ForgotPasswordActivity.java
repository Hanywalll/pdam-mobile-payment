package com.example.tagihanairpdam;

import android.app.AlertDialog;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    private EditText etEmail;
    private Button btnResetPassword;
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sedang mengirim email...")
                .setCancelable(false);
        loadingDialog = builder.create();

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Mohon masukkan email", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        loadingDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(email);

        Call<ForgotPasswordResponse> call = apiService.resetPassword(request);
        Log.d(TAG, "Mengirim permintaan reset password untuk email: " + email);

        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse forgotPasswordResponse = response.body();
                    String message = forgotPasswordResponse.getMessage();

                    if (message.equals("Link reset password telah dikirim")) {
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                        finish(); // Atau arahkan ke halaman informasi
                    } else if (message.equals("Email tidak ditemukan")) {
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Gagal: " + message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Gagal: Response Code " + response.code());
                    Toast.makeText(ForgotPasswordActivity.this, "Gagal mengirim email reset password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.e(TAG, "Error API: " + t.getMessage());
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}