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

public class LapanganWelcomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private SharedPreferences sharedPreferences;

    // Mengikuti nama SharedPreferences dari LoginActivity
    private static final String PREF_NAME = "user_data";
    private static final String KEY_FULL_NAME = "full_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapangan_welcome);

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
            Intent intent = new Intent(LapanganWelcomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // CardView menu
        CardView cvTagihan = findViewById(R.id.cvTagihan);
//        CardView cvTerbitkanTagihan = findViewById(R.id.cvTerbitkanTagihan);
//        CardView cvHistori = findViewById(R.id.cvHistori);
        CardView cvInformasiTerbaru = findViewById(R.id.cvInformasiTerbaru);
//        CardView cvBacaMeter = findViewById(R.id.cvBacaMeter);
        CardView cvKritikSaran = findViewById(R.id.cvKritikSaran);

        // Menu click listeners
        cvTagihan.setOnClickListener(v -> showMenu("Tagihan"));
//        cvTerbitkanTagihan.setOnClickListener(v -> showMenu("Terbitkan Tagihan"));
//        cvHistori.setOnClickListener(v -> showMenu("Histori"));
        cvInformasiTerbaru.setOnClickListener(v -> showMenu("Informasi Terbaru"));
//        cvBacaMeter.setOnClickListener(v -> showMenu("Baca Meter"));
        cvKritikSaran.setOnClickListener(v -> showMenu("Kritik & Saran"));
    }

    private void showMenu(String menuName) {
        Intent intent;
        switch (menuName) {
            case "Tagihan":
                intent = new Intent(this, LapanganActivity.class);
                break;
            case "Kritik & Saran":
                intent = new Intent(this, UserFeedbackActivity.class);
                break;
            case "Informasi Terbaru":
                intent = new Intent(this, InformationActivity.class);
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