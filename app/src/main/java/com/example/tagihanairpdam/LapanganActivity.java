package com.example.tagihanairpdam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LapanganActivity extends AppCompatActivity {
    private static final String TAG = "LapanganActivity";
    private static final int REQUEST_CODE_PAYMENT = 1001;

    private EditText etNomorSambungan;
    private ImageButton btnCariNomor, btnScanBarcode, btnClear;
    private TextView tvNama, tvAlamat, tvKodeGolongan, tvStatus;
    private TransaksiAdapter transaksiAdapter;
    private TextView tvTunggakan, tvPemakaianAir, tvRekeningAir, tvDenda, tvJumlahTagihan;
    private TextView tvMaintenance, tvResKebersihan;
    private ProgressBar progressBar;
    private Button btnDetailTransaksi, btnBayar;
    private String currentNosam;
    private SharedPreferences sharedPreferences;
    private String loggedInRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapangan);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        loggedInRole = sharedPreferences.getString("role", ""); // Ambil peran pengguna

        // Initialize Views
        initializeViews();

        // Initialize Adapter
        transaksiAdapter = new TransaksiAdapter(this);

        // Setup Listeners
        setupListeners();

        // Atur visibilitas awal btnClear
        btnClear.setVisibility(View.GONE);

        // Kontrol visibilitas awal tombol Bayar
        updateBayarButtonVisibility(0); // Inisialisasi dengan jumlah tagihan 0
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Jika ada nosam yang sedang aktif, refresh data
        if (currentNosam != null && !currentNosam.isEmpty()) {
            fetchDataFromServer(currentNosam);
        }
    }
    private void initializeViews() {
        etNomorSambungan = findViewById(R.id.etNomorSambungan);
        btnCariNomor = findViewById(R.id.btnCariNomor);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        tvNama = findViewById(R.id.tvNama);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvKodeGolongan = findViewById(R.id.tvKodeGolongan);
        tvStatus = findViewById(R.id.tvStatus);
        tvTunggakan = findViewById(R.id.tvTunggakan);
        tvPemakaianAir = findViewById(R.id.tvPemakaianAir);
        tvRekeningAir = findViewById(R.id.tvRekeningAir);
        tvDenda = findViewById(R.id.tvDenda);
        tvJumlahTagihan = findViewById(R.id.tvJumlahTagihan);
        tvMaintenance = findViewById(R.id.tvMaintenance);
        tvResKebersihan = findViewById(R.id.tvResKebersihan);
        progressBar = findViewById(R.id.progressBar);
        btnDetailTransaksi = findViewById(R.id.btnDetailTransaksi);
        btnBayar = findViewById(R.id.btnBayar);
        btnClear = findViewById(R.id.btnClear);
    }

    private void setupListeners() {
        // Listener for Cari Nomor button
        btnCariNomor.setOnClickListener(v -> {
            String nosamStr = etNomorSambungan.getText().toString().trim();
            if (!nosamStr.isEmpty()) {
                currentNosam = nosamStr;
                fetchDataFromServer(nosamStr);
            } else {
                Toast.makeText(this, "Nomor sambungan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for Scan Barcode button
        btnScanBarcode.setOnClickListener(v -> startBarcodeScanner());

        // Listener for Clear button
        btnClear.setOnClickListener(v -> etNomorSambungan.setText(""));

        // Listener for Detail Transaksi button
        btnDetailTransaksi.setOnClickListener(v -> {
            if (transaksiAdapter.getItemCount() > 0) {
                startActivity(DetailTransaksiActivity.newIntent(this, transaksiAdapter.getTransaksiList()));
            } else {
                Toast.makeText(this, "Tidak ada detail transaksi.", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for Bayar button
        btnBayar.setOnClickListener(v -> {
            if (currentNosam != null && !currentNosam.isEmpty()) {
                List<CustomerResponse.Transaksi> unpaidTransactions = getUnpaidTransactions();
                if (!unpaidTransactions.isEmpty()) {
                    Intent intent = new Intent(this, PaymentActivity.class);
                    intent.putExtra("nosam", currentNosam);
                    intent.putExtra("nama", tvNama.getText().toString());
                    intent.putExtra("alamat", tvAlamat.getText().toString()); // Tambahkan baris ini
                    intent.putParcelableArrayListExtra("transaksi", new ArrayList<>(unpaidTransactions));
                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                } else {
                    Toast.makeText(this, "Tidak ada tagihan yang belum dibayar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Silakan cari nomor sambungan terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
        // TextWatcher for Clear button visibility
        etNomorSambungan.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Listener for EditText action (Cari)
        etNomorSambungan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String nosamStr = etNomorSambungan.getText().toString().trim();
                    if (!nosamStr.isEmpty()) {
                        currentNosam = nosamStr;
                        fetchDataFromServer(nosamStr);
                    } else {
                        Toast.makeText(LapanganActivity.this, "Nomor sambungan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        ImageButton btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Panggil metode onBackPressed untuk kembali ke aktivitas sebelumnya
            }
        });
    }

    private void startBarcodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan Barcode");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK && data != null) {
                boolean shouldRefresh = data.getBooleanExtra("refresh", false);
                String updatedNosam = data.getStringExtra("nosam");

                if (shouldRefresh && updatedNosam != null) {
                    // Beri sedikit delay untuk memastikan server sudah update
                    new Handler().postDelayed(() -> {
                        fetchDataFromServer(updatedNosam);
                        Toast.makeText(this, "Memperbarui data setelah pembayaran...", Toast.LENGTH_SHORT).show();
                    }, 1000); // Delay 1 detik
                }
            }
        } else {
            // Handle barcode scanner
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                currentNosam = result.getContents();
                etNomorSambungan.setText(currentNosam);
                fetchDataFromServer(currentNosam);
            }
        }
    }

    private void fetchDataFromServer(String nosam) {
        progressBar.setVisibility(View.VISIBLE);
        btnBayar.setVisibility(View.GONE); // Sembunyikan tombol saat loading

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<CustomerResponse> call = apiService.getCustomerData(new CustomerRequest(nosam));

        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(@NonNull Call<CustomerResponse> call, @NonNull Response<CustomerResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    showError("Gagal memuat data pelanggan");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }
    private void updateUI(CustomerResponse response) {
        if (response == null || response.getCustomer() == null) {
            clearUI();
            return;
        }

        // Update customer info
        CustomerResponse.Customer customer = response.getCustomer();
        tvNama.setText(customer.getNama());
        tvAlamat.setText(customer.getAlamat());
        tvKodeGolongan.setText(customer.getKodeGolongan());
        tvStatus.setText(customer.getStatus() == 1 ? "Aktif" : "Tidak Aktif");

        // Update transactions list
        if (response.getTransaksiList() != null && !response.getTransaksiList().isEmpty()) {
            transaksiAdapter.setTransaksiList(response.getTransaksiList());
            btnDetailTransaksi.setVisibility(View.VISIBLE);
        } else {
            transaksiAdapter.setTransaksiList(null);
            btnDetailTransaksi.setVisibility(View.GONE);
        }

        // Update bill summary and payment button
        if (response.getRekaptulasi() != null) {
            CustomerResponse.Rekaptulasi rekaptulasi = response.getRekaptulasi();

            // Memastikan "bulan" ditampilkan di sini jika tidak 0
            tvTunggakan.setText(rekaptulasi.getTunggakan() == 0 ? "-" : rekaptulasi.getTunggakan() + " bulan");

            // Perbaikan untuk Pemakaian Air: hanya tambahkan "m³" jika nilainya bukan 0
            int pemakaianAirValue = rekaptulasi.getPemakaianAir();
            if (pemakaianAirValue == 0) {
                tvPemakaianAir.setText("-");
            } else {
                tvPemakaianAir.setText(formatValueOrHyphenForSummary(pemakaianAirValue, false) + " m³");
            }

            tvRekeningAir.setText(formatValueOrHyphenForSummary(rekaptulasi.getRekeningAir(), true));
            tvDenda.setText(formatValueOrHyphenForSummary(rekaptulasi.getDenda(), true));
            tvMaintenance.setText(formatValueOrHyphenForSummary(rekaptulasi.getMaintenance(), true));
            tvResKebersihan.setText(formatValueOrHyphenForSummary(rekaptulasi.getResKebersihan(), true));
            tvJumlahTagihan.setText(formatValueOrHyphenForSummary(rekaptulasi.getJumlahTagihan(), true));

            // Update payment button based on conditions
            boolean showPayButton = loggedInRole.equalsIgnoreCase("lapangan")
                    && rekaptulasi.getJumlahTagihan() > 0
                    && !getUnpaidTransactions().isEmpty();

            btnBayar.setVisibility(showPayButton ? View.VISIBLE : View.GONE);
        } else {
            btnBayar.setVisibility(View.GONE);
        }
    }


    private void clearUI() {
        tvNama.setText("-");
        tvAlamat.setText("-");
        tvKodeGolongan.setText("-");
        tvStatus.setText("-");
        tvTunggakan.setText("-");
        tvPemakaianAir.setText("-");
        tvRekeningAir.setText("-");
        tvDenda.setText("-");
        tvMaintenance.setText("-");
        tvResKebersihan.setText("-");
        tvJumlahTagihan.setText("-");
        transaksiAdapter.setTransaksiList(null);
        btnBayar.setVisibility(View.GONE);
        btnDetailTransaksi.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        clearUI();
        updateBayarButtonVisibility(0); // Pastikan tombol Bayar disembunyikan saat error
    }

    // Helper method untuk memformat nilai di bagian summary atau menampilkan "-" jika nol
    private String formatValueOrHyphenForSummary(int amount, boolean isCurrency) {
        if (amount == 0) {
            return "-";
        }
        if (isCurrency) {
            return formatCurrency(amount);
        } else {
            return String.valueOf(amount);
        }
    }

    private String formatCurrency(int amount) {
        Locale localeID = new Locale("in", "ID");
        java.text.NumberFormat rupiahFormat = java.text.NumberFormat.getCurrencyInstance(localeID);

        // Modifikasi format agar sesuai dengan "Rp. 500.000,00"
        java.text.DecimalFormatSymbols dfs = new java.text.DecimalFormatSymbols(localeID);
        dfs.setCurrencySymbol("Rp.");
        java.text.DecimalFormat df = (java.text.DecimalFormat) java.text.NumberFormat.getCurrencyInstance(localeID);
        df.setDecimalFormatSymbols(dfs);
        df.setNegativePrefix("(" + df.getPositivePrefix());
        df.setNegativeSuffix(df.getPositiveSuffix() + ")");

        return df.format(amount).replace("Rp.", "Rp. ");
    }

    private List<CustomerResponse.Transaksi> getUnpaidTransactions() {
        List<CustomerResponse.Transaksi> unpaid = new ArrayList<>();
        if (transaksiAdapter.getTransaksiList() != null) {
            for (CustomerResponse.Transaksi transaksi : transaksiAdapter.getTransaksiList()) {
                if ("Belum Dibayar".equals(transaksi.getStatusPembayaran())) {
                    unpaid.add(transaksi);
                }
            }
        }
        return unpaid;
    }

    private void updateBayarButtonVisibility(int jumlahTagihan) {
        // Tampilkan tombol Bayar hanya jika:
        // 1. Role pengguna adalah "lapangan"
        // 2. Jumlah tagihan > 0
        if (loggedInRole != null && loggedInRole.equalsIgnoreCase("Lapangan") && jumlahTagihan > 0) {
            btnBayar.setVisibility(View.VISIBLE);
        } else {
            btnBayar.setVisibility(View.GONE);
        }
    }
}
