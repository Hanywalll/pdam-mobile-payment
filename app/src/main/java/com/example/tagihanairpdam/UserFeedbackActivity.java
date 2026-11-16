package com.example.tagihanairpdam;

import android.app.ProgressDialog;
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

public class UserFeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSendFeedback;
    private ImageButton btnKembali;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        btnKembali = findViewById(R.id.btnKembali);
        etFeedback = findViewById(R.id.etFeedback);
        btnSendFeedback = findViewById(R.id.btnSendFeedback);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        btnSendFeedback.setOnClickListener(v -> sendFeedback());
        btnKembali.setOnClickListener(v -> onBackPressed());
    }

    private void sendFeedback() {
        String message = etFeedback.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Masukan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = sharedPreferences.getString("full_name", "");
        String email = sharedPreferences.getString("email", "");
        String role = sharedPreferences.getString("role", "");

        if (fullName.isEmpty() || email.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Data pengguna tidak lengkap", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize and show ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sedang Mengirim Data...");
        progressDialog.setCancelable(false); // Prevent user from dismissing it
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        FeedbackRequest request = new FeedbackRequest(fullName, email, role, message);

        Call<FeedbackResponse> call = apiService.sendFeedback(request);
        call.enqueue(new Callback<FeedbackResponse>() {
            @Override
            public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {
                // Dismiss ProgressDialog on response
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(UserFeedbackActivity.this,
                                "Masukan berhasil dikirim", Toast.LENGTH_SHORT).show();
                        etFeedback.setText(""); // Clear input field
                    } else {
                        Toast.makeText(UserFeedbackActivity.this,
                                "Gagal mengirim masukan: " + response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserFeedbackActivity.this,
                            "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FeedbackResponse> call, Throwable t) {
                // Dismiss ProgressDialog on failure
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(UserFeedbackActivity.this,
                        "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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