package com.example.tagihanairpdam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DeveloperWelcomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private SharedPreferences sharedPreferences;

    // Mengikuti nama SharedPreferences dari LoginActivity
    private static final String PREF_NAME = "user_data";
    private static final String KEY_FULL_NAME = "full_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_welcome);

        // Set status bar color
        setStatusBarColor();

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Inisialisasi TextView Welcome
        tvWelcome = findViewById(R.id.tvWelcome);

        // Ambil nama lengkap dari SharedPreferences
        String fullName = sharedPreferences.getString(KEY_FULL_NAME, "");

        // Set teks Selamat Datang dengan nama lengkap
        if (!fullName.isEmpty()) {
            tvWelcome.setText(fullName);
        } else {
            tvWelcome.setText("Di Aplikasi PDAM");
        }

        // Inisialisasi ImageView Profile
        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DeveloperWelcomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // CardView menu
        CardView cvTagihan = findViewById(R.id.cvTagihan);
//        CardView cvTerbitkanTagihan = findViewById(R.id.cvTerbitkanTagihan);
//        CardView cvHistori = findViewById(R.id.cvHistori);
        CardView cvInformasiTerbaru = findViewById(R.id.cvInformasiTerbaru);
//        CardView cvBacaMeter = findViewById(R.id.cvBacaMeter);
        CardView cvKritikSaran = findViewById(R.id.cvKritikSaran);
        CardView cvPembayaranPelanggan = findViewById(R.id.cvPembayaranPelanggan);
        CardView cvBuatAkun = findViewById(R.id.cvBuatAkun);


        // Menu click listeners
        cvTagihan.setOnClickListener(v -> showMenu("Tagihan"));
//        cvTerbitkanTagihan.setOnClickListener(v -> showMenu("Terbitkan Tagihan"));
//        cvHistori.setOnClickListener(v -> showMenu("Histori"));
        cvInformasiTerbaru.setOnClickListener(v -> showMenu("Buat Informasi"));
//        cvBacaMeter.setOnClickListener(v -> showMenu("Baca Meter"));
        cvKritikSaran.setOnClickListener(v -> showMenu("Kritik & Saran"));
        cvPembayaranPelanggan.setOnClickListener(v -> showMenu("Pembayaran Pelanggan"));

        cvBuatAkun.setOnClickListener(v -> showMenu("Buat Akun"));

    }

    private void showMenu(String menuName) {
        Intent intent;
        switch (menuName) {
            case "Tagihan":
                intent = new Intent(this, LapanganActivity.class);
                break;
            case "Kritik & Saran":
                intent = new Intent(this, DeveloperFeedbackActivity.class);
                break;
            case "Pembayaran Pelanggan":
                intent = new Intent(this, LoketActivity.class);
                break;
            case "Buat Akun":
                intent = new Intent(this, AdminActivity.class);
                break;
            case "Buat Informasi":
                intent = new Intent(this, InformationCreateActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.basic));
        }
    }
}