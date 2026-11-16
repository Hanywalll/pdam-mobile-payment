package com.example.tagihanairpdam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDetailAccountActivity extends AppCompatActivity {

    private static final String TAG = "AdminDetailAccount";
    private TextView tvFullName, tvEmail, tvPhone, tvAddress, tvUsername, tvRole;
    private Button btnHapusAkun;
    private ImageButton btnKembali;
    private String username;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail_account);

        // Inisialisasi views
        tvFullName = findViewById(R.id.tvFullNameDetail);
        tvEmail = findViewById(R.id.tvEmailDetail);
        tvPhone = findViewById(R.id.tvPhoneDetail);
        tvAddress = findViewById(R.id.tvAddressDetail);
        tvUsername = findViewById(R.id.tvUsernameDetail);
        tvRole = findViewById(R.id.tvRoleDetail);
        btnHapusAkun = findViewById(R.id.btnHapusAkun);
        btnKembali = findViewById(R.id.btnKembaliDetail);

        // Inisialisasi Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat detail akun...");
        progressDialog.setCancelable(false);

        // Mendapatkan username dari Intent
        username = getIntent().getStringExtra("username");

        // Load detail akun berdasarkan username
        if (username != null && !username.isEmpty()) {
            loadAccountDetails(username);
        } else {
            showToast("Username tidak valid");
            finish();
        }

        // Listener untuk tombol kembali
        btnKembali.setOnClickListener(v -> finish());

        // Listener untuk tombol hapus akun
        btnHapusAkun.setOnClickListener(v -> showDeleteConfirmationDialog(username));
    }

    private void loadAccountDetails(String username) {
        progressDialog.show();
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<AdminResponseDetailAccount> call = api.getAccountDetails(username);

        call.enqueue(new Callback<AdminResponseDetailAccount>() {
            @Override
            public void onResponse(Call<AdminResponseDetailAccount> call, Response<AdminResponseDetailAccount> response) {
                progressDialog.dismiss();

                Log.d(TAG, "Response Code: " + response.code());
                if (response.body() != null) {
                    Log.d(TAG, "Full API Response: " + new Gson().toJson(response.body()));
                    if (response.body().getAccount() != null) {
                        Log.d(TAG, "Account Object: " + response.body().getAccount().toString());
                        Log.d(TAG, "Full Name from API: " + response.body().getAccount().getFullName());
                    }
                }

                if (response.isSuccessful() && response.body() != null && response.body().getAccount() != null) {
                    AdminAccountDetailModel account = response.body().getAccount();

                    // Handle potential null values
                    tvFullName.setText(account.getFullName() != null ? account.getFullName() : "N/A");
                    tvEmail.setText(account.getEmail() != null ? account.getEmail() : "N/A");
                    tvPhone.setText(account.getPhone() != null ? account.getPhone() : "N/A");
                    tvAddress.setText(account.getAddress() != null ? account.getAddress() : "N/A");
                    tvUsername.setText(account.getUsername() != null ? account.getUsername() : "N/A");
                    tvRole.setText(account.getRole() != null ? account.getRole() : "N/A");
                } else {
                    showToast("Gagal memuat detail akun");
                    Log.e(TAG, "Error loading details: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AdminResponseDetailAccount> call, Throwable t) {
                progressDialog.dismiss();
                showToast("Error: " + t.getMessage());
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void showDeleteConfirmationDialog(String username) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Akun")
                .setMessage("Apakah Anda yakin ingin menghapus akun " + username + "?")
                .setPositiveButton("Ya", (dialog, which) -> deleteAccount(username))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteAccount(String username) {
        progressDialog.setMessage("Menghapus akun...");
        progressDialog.show();
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<AdminDeleteAccountResponse> call = api.deleteAccount(username);

        call.enqueue(new Callback<AdminDeleteAccountResponse>() {
            @Override
            public void onResponse(Call<AdminDeleteAccountResponse> call, Response<AdminDeleteAccountResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    Intent intent = new Intent(AdminDetailAccountActivity.this, AdminRegisteredAccountsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("account_deleted", true);
                    intent.putExtra("deleted_username", username);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("Gagal menghapus akun: " + (response.body() != null ? response.body().getMessage() : response.message()));
                    Log.e(TAG, "Error deleting account: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error Body (Delete): " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body (Delete)", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AdminDeleteAccountResponse> call, Throwable t) {
                progressDialog.dismiss();
                showToast("Error: " + t.getMessage());
                Log.e(TAG, "API call failed (Delete)", t);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}