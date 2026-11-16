package com.example.tagihanairpdam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity2 extends AppCompatActivity {

    private static final String TAG = "RegisterActivity2";

    private EditText etEmail, etPassword, etConfirmPassword;
    private Spinner spRole;
    private Button btnRegister;
    private TextView tvLogin;

    private String name, phone, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        Log.d(TAG, "Memulai RegisterActivity2...");

        // Retrieve data from previous activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        address = intent.getStringExtra("address");

        Log.d(TAG, "Data dari RegisterActivity1: Name=" + name + ", Phone=" + phone + ", Address=" + address);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spRole = findViewById(R.id.spRole);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false); // Start with button disabled!
        tvLogin = findViewById(R.id.tvLogin);

        setupRoleSpinner();
        setupTextChangedListeners();
        setupRegisterButton();
        setupLoginLink();
    }

    private void setupRoleSpinner() {
        // Set up role spinner
        String[] roles = getResources().getStringArray(R.array.roles);
        NonSelectableSpinnerAdapter adapter = new NonSelectableSpinnerAdapter(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);
        spRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Spinner selected position: " + position + ", value: " + spRole.getSelectedItem().toString());
                if (position != 0) {  // Validasi hanya jika selain "Pilih Peran"
                    validateFields();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak perlu validasi otomatis jika tidak ada pilihan
            }
        });
    }

    private void setupTextChangedListeners() {
        // Real-time validation for each EditText field individually
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                validateEmailField();
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                validatePasswordField();
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                validateConfirmPasswordField();
            }
        });
    }




    private void setupRegisterButton() {
        // Handle register button click
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String role = spRole.getSelectedItem().toString();

            Log.d(TAG, "btnRegister.setOnClickListener: role = " + role);

            // No need to repeat validation here, validateFields() already did it.
            registerUser(email, password, role);
        });
    }

    private void setupLoginLink() {
        // Handle login text click
        tvLogin.setOnClickListener(v -> {
            Log.i(TAG, "Navigasi ke LoginActivity...");
            startActivity(new Intent(RegisterActivity2.this, LoginActivity.class));
        });
    }

    // Method to validate fields and enable/disable the register button
    private void validateFields() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = spRole.getSelectedItem().toString();

        Log.d(TAG, "validateFields: email=" + email + ", password=" + password + ", confirmPassword=" + confirmPassword + ", role=" + role);

        boolean isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isPasswordValid = password.length() >= 6;
        boolean isConfirmPasswordValid = password.equals(confirmPassword);
        boolean isRoleValid = spRole.getSelectedItemPosition() != 0; // 0 is "Pilih Peran"

        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
        } else if (!isEmailValid) {
            etEmail.setError("Format email tidak valid");
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
        } else if (!isPasswordValid) {
            etPassword.setError("Password minimal 6 karakter");
        } else {
            etPassword.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
        } else if (!isConfirmPasswordValid) {
            etConfirmPassword.setError("Password tidak cocok");
        } else {
            etConfirmPassword.setError(null);
        }

        // Enable the register button only if all fields are valid
        btnRegister.setEnabled(isEmailValid && isPasswordValid && isConfirmPasswordValid && isRoleValid &&
                !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !role.equals("Pilih Peran"));

        Log.d(TAG, "validateFields: btnRegister.isEnabled()=" + btnRegister.isEnabled());
    }

    // Validate email field
    private void validateEmailField() {
        String email = etEmail.getText().toString().trim();
        boolean isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
        } else if (!isEmailValid) {
            etEmail.setError("Format email tidak valid");
        } else {
            etEmail.setError(null);
        }
    }

    // Validate password field
    private void validatePasswordField() {
        String password = etPassword.getText().toString().trim();
        boolean isPasswordValid = password.length() >= 6;
        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
        } else if (!isPasswordValid) {
            etPassword.setError("Password minimal 6 karakter");
        } else {
            etPassword.setError(null);
        }
    }

    // Validate confirm password field
    private void validateConfirmPasswordField() {
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        boolean isConfirmPasswordValid = password.equals(confirmPassword);
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
        } else if (!isConfirmPasswordValid) {
            etConfirmPassword.setError("Password tidak cocok");
        } else {
            etConfirmPassword.setError(null);
        }
    }

    private void registerUser(String email, String password, String role) {
        Log.d(TAG, "Membuat objek Retrofit dan ApiService...");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Create UserRegistrationRequest object
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName(name);
        request.setPhone(phone);
        request.setAddress(address);
        request.setEmail(email);
        request.setPassword(password);
        request.setRole(role);

        Log.i(TAG, "Mengirim permintaan ke backend: " + request.toString());

        Call<Void> call = apiService.register(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Respon dari backend: Pendaftaran berhasil");
                    Toast.makeText(RegisterActivity2.this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity2.this, LoginActivity.class));
                } else {
                    Log.e(TAG, "Respon dari backend: " + response.code() + " " + response.message());
                    Toast.makeText(RegisterActivity2.this, "Pendaftaran gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(RegisterActivity2.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter kustom
    private static class NonSelectableSpinnerAdapter extends ArrayAdapter<String> {

        private Context context;
        private String[] items;

        NonSelectableSpinnerAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.context = context;
            this.items = objects;
        }

        @Override
        public boolean isEnabled(int position) {
            // Menonaktifkan item pertama
            return position != 0;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            // Mengatur warna teks item pertama menjadi abu-abu
            if (position == 0) {
                tv.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            } else {
                tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
            return view;
        }
    }
}
