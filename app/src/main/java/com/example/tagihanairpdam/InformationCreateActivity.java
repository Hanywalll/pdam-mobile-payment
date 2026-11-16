package com.example.tagihanairpdam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InformationCreateActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;
    private Button btnCreateInfo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_information);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnCreateInfo = findViewById(R.id.btnCreateInfo);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        btnCreateInfo.setOnClickListener(v -> createInfo());
    }

    private void createInfo() {
        String title = etTitle.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Judul dan pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = sharedPreferences.getString("full_name", "");
        String role = sharedPreferences.getString("role", "");

        if (!role.equalsIgnoreCase("developer")) {
            Toast.makeText(this, "Hanya developer yang bisa membuat informasi", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        InformationRequest request = new InformationRequest (title, message, fullName, role);

        Call<InformationResponse> call = apiService.createInformation(request);
        call.enqueue(new Callback<InformationResponse>() {
            @Override
            public void onResponse(Call<InformationResponse> call, Response<InformationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(InformationCreateActivity.this,
                                "Informasi berhasil dibuat", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(InformationCreateActivity.this,
                                "Gagal membuat informasi: " + response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InformationCreateActivity.this,
                            "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InformationResponse> call, Throwable t) {
                Toast.makeText(InformationCreateActivity.this,
                        "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}