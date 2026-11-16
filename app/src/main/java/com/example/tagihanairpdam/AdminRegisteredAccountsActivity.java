package com.example.tagihanairpdam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRegisteredAccountsActivity extends AppCompatActivity {
    private static final String TAG = "RegisteredAccountsActivity";
    private RecyclerView rvAccounts;
    private AdminCreateAccountAdapter accountAdapter;
    private List<AdminAccountModel> accountList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registered_accounts);

        // Initialize views
        rvAccounts = findViewById(R.id.rvAccounts);
        Button btnRefresh = findViewById(R.id.btnRefresh); // Inisialisasi tombol Refresh
        ImageButton btnKembali = findViewById(R.id.btnKembali); // Inisialisasi tombol Kembali

        // Setup RecyclerView
        accountAdapter = new AdminCreateAccountAdapter(this, accountList);
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        rvAccounts.setAdapter(accountAdapter);

        // Setup Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data...");
        progressDialog.setCancelable(false);

        // Setup Button Listeners
        btnRefresh.setOnClickListener(v -> {
            Log.d(TAG, "btnRefresh clicked");
            loadAccounts(); // Memuat ulang data akun
        });

        // Listener for Back Button
        btnKembali.setOnClickListener(v -> {
            Log.d(TAG, "btnKembali clicked");
            finish(); // Menutup aktivitas saat ini dan kembali ke AdminActivity
        });

        // Load accounts
        loadAccounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("account_deleted", false)) {
            String deletedUsername = intent.getStringExtra("deleted_username");
            if (deletedUsername != null) {
                // Hapus item dari accountList berdasarkan deletedUsername
                for (int i = 0; i < accountList.size(); i++) {
                    if (accountList.get(i).getUsername().equals(deletedUsername)) {
                        accountList.remove(i);
                        accountAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
                // Reset flag agar tidak melakukan penghapusan lagi saat onResume dipanggil berikutnya
                intent.removeExtra("account_deleted");
                intent.removeExtra("deleted_username");
            }
        }
        loadAccounts(); // Tetap panggil loadAccounts untuk mendapatkan data terbaru
    }

    private void loadAccounts() {
        progressDialog.show();
        Log.d(TAG, "Loading accounts");

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<AdminResponseAccountList> call = api.getAccounts();

        call.enqueue(new Callback<AdminResponseAccountList>() {
            @Override
            public void onResponse(Call<AdminResponseAccountList> call, Response<AdminResponseAccountList> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    accountList.clear();
                    accountList.addAll(response.body().getAccounts());
                    accountAdapter.notifyDataSetChanged();
                } else {
                    showToast("Gagal memuat data");
                }
            }

            @Override
            public void onFailure(Call<AdminResponseAccountList> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "API call failed", t);
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}