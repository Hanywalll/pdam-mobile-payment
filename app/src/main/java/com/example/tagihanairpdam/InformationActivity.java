package com.example.tagihanairpdam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InformationActivity extends AppCompatActivity {

    private ListView lvInfo;
    private InformationAdapter adapter;
    private List<InformationModel> infoList = new ArrayList<>();
    private ImageButton btnKembali;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        lvInfo = findViewById(R.id.lvInfo);
        adapter = new InformationAdapter(this, infoList);
        lvInfo.setAdapter(adapter);
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
                onBackPressed();
            }
        });

        loadInfo();
    }

    private void loadInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<InformationListResponse> call = apiService.getInfo();

        call.enqueue(new Callback<InformationListResponse>() {
            @Override
            public void onResponse(Call<InformationListResponse> call, Response<InformationListResponse> response) {
                // Dismiss ProgressDialog only after data is processed
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    infoList.clear();
                    infoList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(InformationActivity.this,
                            "Gagal memuat informasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InformationListResponse> call, Throwable t) {
                // Dismiss ProgressDialog on failure
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(InformationActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Ensure ProgressDialog is dismissed if activity is destroyed
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}