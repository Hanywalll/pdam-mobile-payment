package com.example.tagihanairpdam;

import android.content.Intent;
import android.content.SharedPreferences;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ImageView ivTogglePassword;
    private static final String TAG = "LoginActivity";
    private boolean isUsernameValid = true;
    private boolean isPasswordValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi UI
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        handleDeepLink(getIntent());

        // Warna teks
        etUsername.setTextColor(getResources().getColor(R.color.black));
        etUsername.setHintTextColor(getResources().getColor(R.color.gray));
        etPassword.setTextColor(getResources().getColor(R.color.black));
        etPassword.setHintTextColor(getResources().getColor(R.color.gray));

        btnLogin.setEnabled(false);
        updateLoginButtonState();

        // Validasi username
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                String username = editable.toString().trim();
                if (username.isEmpty()) {
                    etUsername.setError("Username tidak boleh kosong");
                    isUsernameValid = false;
                } else if (username.length() < 3) {
                    etUsername.setError("Username minimal 3 karakter");
                    isUsernameValid = false;
                } else {
                    etUsername.setError(null);
                    isUsernameValid = true;
                }
                updateLoginButtonState();
            }
        });

        // Validasi password
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                String password = editable.toString().trim();
                if (password.isEmpty()) {
                    etPassword.setError("Password tidak boleh kosong");
                    isPasswordValid = false;
                } else {
                    etPassword.setError(null);
                    isPasswordValid = true;
                }
                updateLoginButtonState();
            }
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Mohon isi semua field", Toast.LENGTH_SHORT).show();
            } else if (!isUsernameValid) {
                Toast.makeText(LoginActivity.this, "Username tidak valid", Toast.LENGTH_SHORT).show();
            } else if (!isPasswordValid) {
                Toast.makeText(LoginActivity.this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                login(username, password);
            }
        });

        ivTogglePassword.setOnClickListener(v -> {
            if (etPassword.getInputType() == 129) {
                etPassword.setInputType(144 | 1);
                ivTogglePassword.setImageResource(R.drawable.eye_password_hidden);
            } else {
                etPassword.setInputType(129);
                ivTogglePassword.setImageResource(R.drawable.eye_password_show);
            }
            etPassword.setTypeface(null);
            etPassword.setSelection(etPassword.getText().length());
        });

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String path = data.getPath();
            if ("/login".equals(path)) {
                String username = data.getQueryParameter("username");
                if (username != null && !username.isEmpty()) {
                    etUsername.setText(username);
                    Toast.makeText(this, "Silakan masukkan password Anda", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void login(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if ("Login berhasil".equals(loginResponse.getMessage())) {
                        if (loginResponse.getUser() == null) {
                            Log.e(TAG, "User object is null");
                            Toast.makeText(LoginActivity.this, "Data user tidak lengkap!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Simpan nama lengkap ke SharedPreferences
                        saveUserData(loginResponse.getUser().getName(), username);

                        // Update last login
                        updateLastLogin(username);

                        // Periksa apakah ini login pertama
                        if (loginResponse.getUser().isFirstLogin()) {
                            // Arahkan ke LoginFirstActivity
                            Intent intent = new Intent(LoginActivity.this, LoginFirstActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("fullName", loginResponse.getUser().getName());
                            intent.putExtra("email", loginResponse.getUser().getEmail());
                            startActivity(intent);
                            finish();
                            return;
                        }

                        String role = (loginResponse.getUser().getRole() != null) ? loginResponse.getUser().getRole().trim() : "";
                        if (role.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Role kosong atau tidak valid!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Simpan peran pengguna ke SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("role", role.toLowerCase()); // Simpan dalam huruf kecil
                        editor.apply();

                        Intent intent;
                        switch (role.toLowerCase()) {
                            case "developer":
                                intent = new Intent(LoginActivity.this, DeveloperWelcomeActivity.class);
                                break;
                            case "lapangan":
                                intent = new Intent(LoginActivity.this, LapanganWelcomeActivity.class);
                                break;
                            case "loket":
                                intent = new Intent(LoginActivity.this, LoketWelcomeActivity.class);
                                break;
                            case "admin":
                                intent = new Intent(LoginActivity.this, AdminWelcomeActivity.class);
                                break;
                            default:
                                Toast.makeText(LoginActivity.this, "Role tidak dikenali", Toast.LENGTH_SHORT).show();
                                return;
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Username atau password salah", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response gagal: " + response.errorBody());
                    Toast.makeText(LoginActivity.this, "Login gagal, periksa kembali data Anda", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Error koneksi: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Koneksi gagal, cek jaringan Anda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk menyimpan data login dan nama lengkap
    private void saveUserData(String fullName, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("full_name", fullName);
        editor.putString("username", username);

        editor.apply();
    }

    // Method untuk update last login
    private void updateLastLogin(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdamtidha.my.id/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<DefaultResponse> call = apiService.updateLastLogin(username);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Update status berhasil: " + response.body().getMessage());
                } else {
                    Log.e(TAG, "Gagal update status: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Log.e(TAG, "Gagal koneksi update status: " + t.getMessage());
            }
        });
    }

    private void updateLoginButtonState() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        btnLogin.setEnabled(isUsernameValid && isPasswordValid && !username.isEmpty() && !password.isEmpty());
    }
}