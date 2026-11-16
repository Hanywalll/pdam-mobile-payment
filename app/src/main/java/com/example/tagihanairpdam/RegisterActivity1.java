package com.example.tagihanairpdam;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity1 extends AppCompatActivity {

    private EditText etName, etPhone, etAddress;
    private Button btnNext;
    private TextView tvLogin;

    private boolean nameTouched = false;
    private boolean phoneTouched = false;
    private boolean addressTouched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnNext = findViewById(R.id.btnNext);
        tvLogin = findViewById(R.id.tvLogin);

        // Set default phone number to "08 | " initially (without resetting after edit)
        etPhone.setText("08 | ");
        etPhone.setSelection(etPhone.getText().length());  // Keep cursor at the end

        // Disable the button initially
        btnNext.setEnabled(false);

        // Handle btnNext click
        btnNext.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Remove all non-numeric characters from phone number
            phone = phone.replaceAll("[^\\d]", "");

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(RegisterActivity1.this, "Mohon isi semua field.", Toast.LENGTH_SHORT).show();
            } else {
                // Pass data to next activity
                Intent intent = new Intent(RegisterActivity1.this, RegisterActivity2.class);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone); // Send phone number without " | "
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });

        // Navigate back to login page
        tvLogin.setOnClickListener(v -> startActivity(new Intent(RegisterActivity1.this, LoginActivity.class)));

        etPhone.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(18) });

        // Phone number validation with TextWatcher
        etPhone.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String input = s.toString();

                // Pastikan teks selalu diawali dengan "08 | "
                if (!input.startsWith("08 | ")) {
                    etPhone.setText("08 | ");
                    etPhone.setSelection(etPhone.getText().length());
                } else if (input.length() < 5) { // Jika seluruhnya dihapus
                    etPhone.setText("08 | ");
                    etPhone.setSelection(etPhone.getText().length());
                }

                // Ambil hanya angka dari input
                String phoneNumber = input.replaceAll("[^\\d]", "");

                if (phoneNumber.length() == 2) { // Jika hanya berisi "08"
                    etPhone.setError("Nomor Telepon tidak boleh kosong");
                    phoneTouched = false;
                } else if (phoneNumber.length() < 10) { // Kurang dari panjang minimum
                    etPhone.setError("Nomor Telepon Tidak Valid");
                    phoneTouched = false;
                } else {
                    etPhone.setError(null);
                    phoneTouched = true;
                }

                isEditing = false;
                validateFields(); // Pastikan tombol diperbarui
            }
        });



        // Name validation with TextWatcher
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editable.toString().trim();
                if (!nameTouched) {
                    nameTouched = true;  // Mark the field as touched
                }
                if (name.isEmpty() && nameTouched) {
                    etName.setError("Nama tidak boleh kosong");
                } else {
                    etName.setError(null); // Remove error if name is filled
                }
                validateFields(); // Validate fields after text change
            }
        });

        // Address validation with TextWatcher
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String address = editable.toString().trim();
                if (!addressTouched) {
                    addressTouched = true;  // Mark the field as touched
                }
                if (address.isEmpty() && addressTouched) {
                    etAddress.setError("Alamat tidak boleh kosong");
                } else {
                    etAddress.setError(null); // Remove error if address is filled
                }
                validateFields(); // Validate fields after text change
            }
        });
    }

    // Method to validate fields and give notification
    private void validateFields() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Enable the button only if all fields are filled and valid
        if (nameTouched && !name.isEmpty() && phoneTouched && !phone.isEmpty() && phone.length() >= 14 && !phone.equals("08 | ") && addressTouched && !address.isEmpty()) {
            btnNext.setEnabled(true); // Enable the button if all fields are valid
        } else {
            btnNext.setEnabled(false); // Disable the button if any field is invalid
        }
    }
}
