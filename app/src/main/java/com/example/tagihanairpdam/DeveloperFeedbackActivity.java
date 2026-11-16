package com.example.tagihanairpdam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class DeveloperFeedbackActivity extends AppCompatActivity {

    private ListView lvFeedback;
    private List<String> feedbackList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private ImageButton btnKembali;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_feedback);

        lvFeedback = findViewById(R.id.lvFeedback);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, feedbackList);
        lvFeedback.setAdapter(adapter);
        btnKembali = findViewById(R.id.btnKembali);

        // Set OnClickListener untuk tombol kembali
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Panggil metode onBackPressed untuk kembali ke aktivitas sebelumnya
            }
        });


        loadFeedback();
    }

    private void loadFeedback() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");

        if (!role.equalsIgnoreCase("developer")) {
            Toast.makeText(this, "Hanya developer yang bisa mengakses", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<FeedbackListResponse> call = apiService.getFeedback(role);

        call.enqueue(new Callback<FeedbackListResponse>() {
            @Override
            public void onResponse(Call<FeedbackListResponse> call, Response<FeedbackListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    feedbackList.clear();
                    for (FeedbackItem item : response.body().getData()) {
                        String feedbackStr = String.format("%s (%s - %s)\n%s\n%s",
                                item.getSenderName(),
                                item.getSenderRole(),
                                item.getSenderEmail(),
                                item.getMessage(),
                                item.getCreatedAt());
                        feedbackList.add(feedbackStr);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DeveloperFeedbackActivity.this,
                            "Gagal memuat masukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FeedbackListResponse> call, Throwable t) {
                Toast.makeText(DeveloperFeedbackActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}