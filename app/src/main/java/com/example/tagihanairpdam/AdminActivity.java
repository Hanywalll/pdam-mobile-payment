package com.example.tagihanairpdam;

import android.app.ProgressDialog;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ImageButton;

import android.widget.Spinner;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;

import android.content.Intent;
import com.google.gson.Gson;
import java.io.IOException;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.Random;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private EditText etFullName, etEmail;

    private Spinner spRole;

    private Button btnCreate, btnViewAccounts;

    private ProgressDialog progressDialog;

    // Map untuk menyimpan counter tertinggi berdasarkan base username (tanpa nomor dan tanpa titik)
    private Map<String, Integer> usernameCounter = new HashMap<>();

    // Pattern untuk mengekstrak nomor di akhir username
    // Mencari 1 atau lebih digit (\d+) di akhir string ($)
    private static final Pattern COUNTER_PATTERN = Pattern.compile("(\\d+)$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d(TAG, "onCreate: Activity started");

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        spRole = findViewById(R.id.spRole);
        btnCreate = findViewById(R.id.btnCreate);
        btnViewAccounts = findViewById(R.id.btnViewAccounts);
        Log.d(TAG, "onCreate: Views initialized");

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles, // Pastikan resource array roles ini ada
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);
        Log.d(TAG, "onCreate: Spinner setup completed");

        // Setup Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);
        Log.d(TAG, "onCreate: ProgressDialog setup completed");

        // Button listeners
        btnCreate.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: btnCreate clicked");
            createAccount();
        });

        btnViewAccounts.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: btnViewAccounts clicked");
            startActivity(new Intent(AdminActivity.this, AdminRegisteredAccountsActivity.class)); // Pastikan activity ini ada
        });

        ImageButton btnKembali = findViewById(R.id.btnKembali); // Pastikan ID tombol kembali ini ada

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Load initial data and initialize counter
        loadAccounts();
        Log.d(TAG, "onCreate: Initial loadAccounts() called");
    }

    // --- PERBAIKAN Logic parsing username dan counter ---
    private void updateCounterFromUsername(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }
        Log.d(TAG, "updateCounterFromUsername: Parsing username: " + username);

        Matcher matcher = COUNTER_PATTERN.matcher(username); // Gunakan pattern regex
        if (matcher.find()) {
            try {
                String counterStr = matcher.group(1); // Ambil group pertama (angka)
                int counter = Integer.parseInt(counterStr);
                // Ambil bagian sebelum angka, lalu hapus semua titik untuk mendapatkan base yang bersih
                String baseUsernameWithPossibleDots = username.substring(0, matcher.start());
                String cleanedBaseUsername = baseUsernameWithPossibleDots.replace(".", ""); // Hapus titik!

                if (!cleanedBaseUsername.isEmpty()) {
                    // Update the highest counter for this cleaned base username
                    if (!usernameCounter.containsKey(cleanedBaseUsername) || usernameCounter.get(cleanedBaseUsername) < counter) {
                        usernameCounter.put(cleanedBaseUsername, counter);
                        Log.d(TAG, "updateCounterFromUsername: Updated counter for '" + cleanedBaseUsername + "' to " + counter);
                    }
                } else {
                    Log.w(TAG, "updateCounterFromUsername: Cleaned base username is empty after removing counter and dots for: " + username);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "updateCounterFromUsername: Error parsing counter from username: " + username, e);
            } catch (Exception e) {
                Log.e(TAG, "updateCounterFromUsername: Unexpected error processing username: " + username, e);
            }
        } else {
            // Logika untuk menangani username yang tidak berakhiran angka jika diperlukan.
            // Berdasarkan format lapmuhaburh01, diasumsikan semua username akan berakhir dengan angka.
            // Jadi, username tanpa angka mungkin merupakan format lama atau tidak relevan untuk counter baru.
            Log.w(TAG, "updateCounterFromUsername: Username does not end with digits (not considered for counter): " + username);
        }
    }
    // --- AKHIR PERBAIKAN Logic parsing username dan counter ---


    private void loadAccounts() {
        progressDialog.show();
        Log.d(TAG, "loadAccounts: Loading accounts");

        // Pastikan ApiClient dan ApiService sudah diinisialisasi dengan endpoint yang benar
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<AdminResponseAccountList> call = api.getAccounts(); // Pastikan method getAccounts() ada di ApiService

        call.enqueue(new Callback<AdminResponseAccountList>() {
            @Override
            public void onResponse(Call<AdminResponseAccountList> call, Response<AdminResponseAccountList> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    // Initialize username counter
                    usernameCounter.clear(); // Bersihkan counter sebelum mengisi ulang
                    List<AdminAccountModel> accounts = response.body().getAccounts(); // Pastikan getAccounts() mengembalikan List<AdminAccountModel>
                    if (accounts != null) {
                        Log.d(TAG, "loadAccounts: Received " + accounts.size() + " accounts.");
                        for (AdminAccountModel account : accounts) {
                            String username = account.getUsername(); // Pastikan AdminAccountModel punya getUsername()
                            if (username != null) {
                                updateCounterFromUsername(username); // Update counter dari setiap username yang ada
                            }
                        }
                        Log.d(TAG, "loadAccounts: Username counters initialized: " + usernameCounter.toString());
                    } else {
                        Log.w(TAG, "loadAccounts: Account list is null in response body.");
                        showToast("Gagal memuat data akun: Daftar kosong");
                    }
                } else {
                    String errorBody = "null";
                    try {
                        if(response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                        Log.e(TAG, "loadAccounts: Error response code " + response.code() + ": " + errorBody);
                        showToast("Gagal memuat data akun. Code: " + response.code());
                    } catch (IOException e) {
                        Log.e(TAG, "loadAccounts: IOException reading error body", e);
                        showToast("Gagal memuat data akun.");
                    }
                }
            }

            @Override
            public void onFailure(Call<AdminResponseAccountList> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "loadAccounts: API call failed", t);
                showToast("Koneksi gagal memuat data: " + t.getMessage());
            }
        });
    }


    private void createAccount() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String role = spRole.getSelectedItem().toString();
        Log.d(TAG, "createAccount: Attempting to create account with fullName=" + fullName + ", email=" + email + ", role=" + role);

        if (fullName.isEmpty() || email.isEmpty()) {
            Log.w(TAG, "createAccount: Nama atau email kosong");
            showToast("Nama dan email wajib diisi");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.w(TAG, "createAccount: Format email tidak valid: " + email);
            showToast("Format email tidak valid");
            return;
        }

        progressDialog.show();
        progressDialog.setMessage("Memeriksa email...");

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<CheckEmailResponse> checkEmailCall = api.checkEmail(email, "check_email");


        checkEmailCall.enqueue(new Callback<CheckEmailResponse>() {
            @Override
            public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                // progressDialog.dismiss(); // Jangan dismiss di sini, pindah ke generateAndSendAccount atau failure

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        CheckEmailResponse checkResponse = response.body();

                        // Debug log the raw response
                        Log.d(TAG, "Email check response: " + new Gson().toJson(response.body()));

                        if (checkResponse.isExists()) {
                            progressDialog.dismiss(); // Dismiss here if email exists
                            showToast("Email telah terdaftar oleh user lain");
                        } else {
                            // Email tidak ada, lanjutkan pembuatan akun
                            // progressDialog akan didismiss di generateAndSendAccount callback
                            generateAndSendAccount(fullName, email, role);
                        }
                    } else {
                        progressDialog.dismiss(); // Dismiss here if response not successful
                        String errorBody = "null";
                        try {
                            if(response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                            Log.e(TAG, "Email check failed: " + errorBody);
                            showToast("Gagal memeriksa email. Code: " + response.code());
                        } catch (IOException e) {
                            Log.e(TAG, "Email check: IOException reading error body", e);
                            showToast("Gagal memeriksa email.");
                        }
                    }
                } catch (Exception e) {
                    progressDialog.dismiss(); // Dismiss here for any other exception
                    Log.e(TAG, "Error processing email check", e);
                    showToast("Error memproses respon periksa email");
                }
            }

            @Override
            public void onFailure(Call<CheckEmailResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "Email check API call failed", t);
                showToast("Koneksi gagal saat memeriksa email: " + t.getMessage());
            }
        });
    }


    private void generateAndSendAccount(String fullName, String email, String role) {
        progressDialog.setMessage("Membuat akun...");

        // Generate sementara di client untuk preview (meskipun server yang final)
        // Username yang di-generate di sini HANYA untuk tujuan menampilkan format yang diharapkan.
        // Server yang bertanggung jawab untuk generating username dan counter FINAL.
        String tempUsernameExpectedFormat = generateUsername(fullName, role);
        String password = generateRandomPassword(8); // Asumsi password tetap di-generate client

        // Log format username yang diharapkan (berdasarkan data lokal)
        Log.d(TAG, "generateAndSendAccount: Expected username format=" + tempUsernameExpectedFormat + ", password=" + password + " (server will generate final username)");


        ApiService api = ApiClient.getClient().create(ApiService.class);
        // Mengirim username KOSONG atau format yang DIHARAPKAN ke server
        // Jika server Anda memerlukan format username dari client:
        // Call<AdminResponseAccount> call = api.createAccount(
        //         new AdminRequestAccount(fullName, email, tempUsernameExpectedFormat, password, role)
        // );
        // Jika server Anda MENGHASILKAN username sendiri (seperti yang disarankan oleh pengiriman username kosong sebelumnya):
        Call<AdminResponseAccount> call = api.createAccount(
                new AdminRequestAccount(fullName, email, "", password, role) // Username kosong, biarkan server generate
        );


        call.enqueue(new Callback<AdminResponseAccount>() {
            @Override
            public void onResponse(Call<AdminResponseAccount> call, Response<AdminResponseAccount> response) {
                progressDialog.dismiss();

                Log.d(TAG, "generateAndSendAccount: Response received - isSuccessful=" + response.isSuccessful() + ", code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AdminResponseAccount res = response.body();
                    if (res.isSuccess()) {
                        // **PENTING**: Update counter berdasarkan username yang dibuat/dikembalikan server
                        String serverUsername = res.getUsername(); // Ambil username yang dikembalikan server
                        if (serverUsername != null && !serverUsername.isEmpty()) {
                            Log.d(TAG, "generateAndSendAccount: Account created successfully - id=" + res.getId() + ", serverUsername=" + serverUsername);
                            // Gunakan username dari server untuk mengupdate counter lokal
                            updateCounterFromUsername(serverUsername);
                            showToast("Akun berhasil dibuat\n"+ serverUsername);
                        } else {
                            Log.w(TAG, "generateAndSendAccount: Account created successfully, but server returned empty username.");
                            // fallback: tampilkan username ekspektasi jika server tidak mengembalikan username
                            showToast("Akun berhasil dibuat (Server tidak mengembalikan username).\nUser: " + tempUsernameExpectedFormat + "\nPass: " + password);
                        }

                        clearForm();
                        loadAccounts(); // Refresh data untuk memastikan counter lokal akurat
                    } else {
                        String errorMessage = res.getMessage();
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "Gagal membuat akun.";
                        }
                        Log.e(TAG, "generateAndSendAccount: Server reported failure: " + errorMessage);
                        showToast(errorMessage);
                    }
                } else {
                    String errorBody = "null";
                    try {
                        if(response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                        Log.e(TAG, "generateAndSendAccount: Failed to create account - response code=" + response.code() + ", error: " + errorBody);
                        showToast("Gagal membuat akun. Code: " + response.code());
                    } catch (IOException e) {
                        Log.e(TAG, "generateAndSendAccount: IOException reading error body", e);
                        showToast("Gagal membuat akun.");
                    }
                }
            }

            @Override
            public void onFailure(Call<AdminResponseAccount> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "generateAndSendAccount: API call failed", t);
                showToast("Koneksi gagal saat membuat akun: " + t.getMessage());
            }
        });
    }

    // --- PERBAIKAN Logic generate username ---
    private String generateUsername(String fullName, String role) {
        // Mengambil prefix berdasarkan role, pastikan lowercase dan tanpa spasi tambahan
        String prefix = role.trim().equalsIgnoreCase("Lapangan") ? "lap" : "lok";

        // Bersihkan nama (hanya huruf) dan ambil maksimal 4 karakter dari 2 kata pertama
        String cleanedFullName = fullName.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
        String[] nameParts = cleanedFullName.split("\\s+");

        StringBuilder baseBuilder = new StringBuilder();
        for (int i = 0; i < Math.min(nameParts.length, 2); i++) {
            if (!nameParts[i].isEmpty()) {
                baseBuilder.append(nameParts[i].substring(0, Math.min(nameParts[i].length(), 4)));
            }
        }

        // Menggabungkan prefix dan base nama TANPA TITIK
        String fullBase = prefix + baseBuilder.toString();
        Log.d(TAG, "generateUsername: Generated base username (without counter): " + fullBase);


        // Mengambil counter tertinggi saat ini untuk base ini dari map lokal
        // Jika base belum ada, gunakan 0, sehingga counter berikutnya adalah 1
        int currentHighestCounter = usernameCounter.getOrDefault(fullBase, 0);
        int nextCounter = currentHighestCounter + 1;
        Log.d(TAG, "generateUsername: Current highest counter for '" + fullBase + "' is " + currentHighestCounter + ", next is " + nextCounter);


        // Menggabungkan base dengan counter yang diformat (dua digit) TANPA TITIK
        return fullBase + String.format("%02d", nextCounter);
    }
    // --- AKHIR PERBAIKAN Logic generate username ---


    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        String generatedPassword = sb.toString();
        Log.d(TAG, "generateRandomPassword: Generated password with length " + length + " is " + generatedPassword);
        return generatedPassword;
    }

    private void clearForm() {
        runOnUiThread(() -> {
            etFullName.setText("");
            etEmail.setText("");
            spRole.setSelection(0);
            Log.d(TAG, "clearForm: Form cleared");
            // Setelah form cleared, mungkin set focus ke etFullName
            etFullName.requestFocus();
        });
    }

    private void showToast(String message) {
        // Pastikan showToast dijalankan di UI thread
        runOnUiThread(() -> {
            Toast.makeText(AdminActivity.this, message, Toast.LENGTH_LONG).show();
            Log.i(TAG, "showToast: Displaying message - " + message);
        });
    }

    // Anda mungkin perlu kelas AdminResponseAccountList, AdminAccountModel,
    // ApiService, ApiClient, CheckEmailResponse, CheckEmailRequest,
    // AdminResponseAccount, AdminRequestAccount
    // yang sesuai dengan struktur data API Anda.
    // Pastikan AdminAccountModel memiliki method getUsername()
}