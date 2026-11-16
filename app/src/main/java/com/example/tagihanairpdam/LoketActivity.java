package com.example.tagihanairpdam;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoketActivity extends AppCompatActivity {
    private static final int UPDATE_REQUEST_CODE = 1001;

    private static final String TAG = "LoketActivity";
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_WRITE_STORAGE_PDF = 113;

    private RecyclerView recyclerView;
    private LoketTransaksiAdapter adapter;
    private EditText etNomorSambungan;
    private ImageButton btnCariNomor, btnKembali, btnClear;
    private Button btnHariIni, btnSemua, btnExportExcel, btnExportPdf;
    private LoketApiService apiService;
    private List<LoketTransaksi> currentTransaksiList;
    private TextView tvEmptyView;
    private ProgressDialog progressDialog;

    // Variabel untuk filter periode
    private TextView tvStartDate, tvEndDate;
    private ImageButton btnStartDate, btnEndDate;
    private LinearLayout layoutPeriode;
    private String activeFilter = "hari_ini";

    private Call<?> currentCall; // Untuk menyimpan panggilan API yang sedang berjalan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loket);

        initViews();
        updateSearchHint();
        setupRecyclerView();
        apiService = RetrofitClient.getClient("https://tagihanairpdam.loca.lt/pdam/mobile_api/").create(LoketApiService.class);

        // Set filter hari ini aktif pertama kali
        btnHariIni.setBackgroundResource(R.drawable.rounded_button_basicpressed_selector);
        btnSemua.setBackgroundResource(R.drawable.rounded_button_basic_selector);
        layoutPeriode.setVisibility(View.GONE);

        showProgressDialog("Memuat data transaksi...");
        fetchTransaksiHariIni();
        setupListeners();

        btnExportPdf = findViewById(R.id.btnExportPdf);
        btnExportPdf.setOnClickListener(v -> checkPermissionAndExportPdf());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("refresh", false)) {
                // Lakukan refresh berdasarkan filter yang aktif
                refreshData();
            }
        }
    }

    private void refreshData() {
        showProgressDialog("Memperbarui data...");

        if ("hari_ini".equals(activeFilter)) {
            fetchTransaksiHariIni();
        } else {
            fetchAllTransaksi();
        }
    }
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etNomorSambungan = findViewById(R.id.etNosam);
        btnCariNomor = findViewById(R.id.btnCari);
        btnClear = findViewById(R.id.btnClear);
        btnKembali = findViewById(R.id.btnKembali);
        btnExportExcel = findViewById(R.id.btnExportExcel);
        btnHariIni = findViewById(R.id.btnHariIni);
        btnSemua = findViewById(R.id.btnSemua);
        tvEmptyView = findViewById(R.id.tvEmptyView);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        layoutPeriode = findViewById(R.id.layoutPeriode);
        btnExportPdf = findViewById(R.id.btnExportPdf);

        tvStartDate.setText("YYYY-MM-DD");
        tvEndDate.setText("YYYY-MM-DD");
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LoketTransaksiAdapter();

        // Tambahkan ini:
        adapter.setOnUpdateClickListener((transaksi, position) -> {
            Intent intent = new Intent(LoketActivity.this, LoketUpdateTransaksiActivity.class);
            intent.putExtra("transaksi", transaksi);
            startActivityForResult(intent, UPDATE_REQUEST_CODE); // Gunakan startActivityForResult
        });

        recyclerView.setAdapter(adapter);
    }
    private void showProgressDialog(String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void setupListeners() {
        btnHariIni.setOnClickListener(v -> {
            resetFilterButtons();
            btnHariIni.setBackgroundResource(R.drawable.rounded_button_basicpressed_selector);
            activeFilter = "hari_ini";
            layoutPeriode.setVisibility(View.GONE);
            updateSearchHint();
            clearInputFields();
            showProgressDialog("Memuat transaksi hari ini...");
            fetchTransaksiHariIni();
        });

        etNomorSambungan.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                tvStartDate.setText("YYYY-MM-DD");
                tvEndDate.setText("YYYY-MM-DD");
                showProgressDialog("Mencari transaksi...");
                cariTransaksi();
                return true; // Berarti event sudah ditangani
            }
            return false;
        });



        btnSemua.setOnClickListener(v -> {
            resetFilterButtons();
            btnSemua.setBackgroundResource(R.drawable.rounded_button_basicpressed_selector);
            activeFilter = "semua";
            layoutPeriode.setVisibility(View.VISIBLE);
            updateSearchHint();
            clearInputFields();
            showProgressDialog("Memuat semua transaksi...");
            fetchAllTransaksi();
        });

        LinearLayout startDateLayout = findViewById(R.id.startDateLayout);
        LinearLayout endDateLayout = findViewById(R.id.endDateLayout);
        startDateLayout.setOnClickListener(v -> showDatePickerDialog(true));
        endDateLayout.setOnClickListener(v -> showDatePickerDialog(false));

        Button btnCariPeriode = findViewById(R.id.btnCariPeriode);
        btnCariPeriode.setOnClickListener(v -> {
            etNomorSambungan.setText("");
            showProgressDialog("Mencari transaksi berdasarkan periode...");
            cariByPeriode();
        });

        btnCariNomor.setOnClickListener(v -> {
            tvStartDate.setText("YYYY-MM-DD");
            tvEndDate.setText("YYYY-MM-DD");
            showProgressDialog("Mencari transaksi...");
            cariTransaksi();
        });

        btnClear.setOnClickListener(v -> {
            etNomorSambungan.getText().clear();
            showProgressDialog("Memuat ulang data...");
            if ("hari_ini".equals(activeFilter)) {
                fetchTransaksiHariIni();
            } else {
                fetchAllTransaksi();
            }
        });

        btnKembali.setOnClickListener(v -> onBackPressed());
        btnExportExcel.setOnClickListener(v -> checkPermissionAndExport());
    }

    private void clearInputFields() {
        etNomorSambungan.setText("");
        tvStartDate.setText("YYYY-MM-DD");
        tvEndDate.setText("YYYY-MM-DD");
        etNomorSambungan.clearFocus();
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    if (isStartDate) {
                        tvStartDate.setText(selectedDate);
                    } else {
                        tvEndDate.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void resetFilterButtons() {
        btnHariIni.setBackgroundResource(R.drawable.rounded_button_basic_selector);
        btnSemua.setBackgroundResource(R.drawable.rounded_button_basic_selector);
    }

    private void checkPermissionAndExport() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            exportToCsv();
        }
    }

    private void checkPermissionAndExportPdf() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PDF);
        } else {
            exportToPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportToCsv();
            } else {
                showErrorDialog("Izin penyimpanan diperlukan untuk mengekspor data");
            }
        } else if (requestCode == REQUEST_WRITE_STORAGE_PDF) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportToPdf();
            } else {
                showErrorDialog("Izin penyimpanan diperlukan untuk mengekspor PDF");
            }
        }
    }

    private void exportToCsv() {
        if (currentTransaksiList == null || currentTransaksiList.isEmpty()) {
            showErrorDialog("Tidak ada data untuk diekspor");
            return;
        }

        showProgressDialog("Mengekspor data ke CSV...");

        new Thread(() -> {
            try {
                String csvHeader = "No,Nosam,Nama Pelanggan,Alamat,Periode,Jumlah Tagihan,Denda,Total Bayar,Metode Bayar,Kode Pembayaran,Tanggal Bayar,Status,Petugas,Post,Tanggal Dibuat\n";
                StringBuilder csvContent = new StringBuilder();

                int no = 1;
                for (LoketTransaksi trans : currentTransaksiList) {
                    csvContent.append(no++).append(",")
                            .append(trans.getNosam()).append(",")
                            .append("\"").append(escapeCsvValue(trans.getNamaPelanggan())).append("\",")
                            .append("\"").append(escapeCsvValue(trans.getAlamat())).append("\",")
                            .append(trans.getPeriode()).append(",")
                            .append(trans.getJumlahTagihan()).append(",")
                            .append(trans.getDenda()).append(",")
                            .append(trans.getTotalBayar()).append(",")
                            .append(trans.getMetodeBayar()).append(",")
                            .append(trans.getKodePembayaran()).append(",")
                            .append(formatTanggal(trans.getTanggalBayar())).append(",")
                            .append(trans.getStatus()).append(",")
                            .append("\"").append(escapeCsvValue(
                                    trans.getPetugasNama() != null ? trans.getPetugasNama() :
                                            (trans.getPetugas() != null ? trans.getPetugas() : "N/A")
                            )).append("\",")
                            .append(trans.getPost() != null ? trans.getPost() : "").append(",")
                            .append(formatTanggal(trans.getCreatedAt())).append("\n");
                }

                String csvData = csvHeader + csvContent.toString();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Transaksi_Pembayaran_" + timeStamp + ".csv";
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);

                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(csvData.getBytes());
                outputStream.close();

                runOnUiThread(() -> {
                    hideProgressDialog();
                    showSuccessDialog("File CSV berhasil disimpan di folder Download");
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showErrorDialog("Gagal mengekspor data: " + e.getMessage());
                });
            }
        }).start();
    }
    private void exportToPdf() {
        if (currentTransaksiList == null || currentTransaksiList.isEmpty()) {
            showErrorDialog("Tidak ada data untuk diekspor");
            return;
        }

        showProgressDialog("Mengekspor data ke PDF...");

        new Thread(() -> {
            try {
                PdfDocument pdfDocument = new PdfDocument();
                // Ukuran kertas A0 Landscape: 1189 mm x 841 mm, diubah ke points (1 mm = 2.83465 points)
                int pageWidthA0Landscape = (int) (700 * 2.83465); // Sekitar 3370 points
                int pageHeightA0Landscape = (int) (841 * 2.83465); // Sekitar 2384 points
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidthA0Landscape, pageHeightA0Landscape, 1).create();
                PdfDocument.Page pdfPage = pdfDocument.startPage(pageInfo);
                Canvas canvas = pdfPage.getCanvas();
                Paint paint = new Paint();

                // Set margin (sesuaikan jika perlu)
                int marginHorizontal = 80;
                int marginVertical = 80;
                int currentY = marginVertical + 60;
                int pageWidth = pageInfo.getPageWidth();
                int centerX = pageWidth / 2;

                // Font sizes (sesuaikan agar proporsional dengan ukuran kertas)
                int titleSize = 24;
                int headerSize = 16;
                int contentSize = 12;
                int rowHeight = 30;

                // Judul Laporan
                paint.setTextSize(titleSize);
                paint.setFakeBoldText(true);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("LAPORAN TRANSAKSI PEMBAYARAN", centerX, currentY, paint);
                currentY += 30;
                paint.setTextSize(titleSize - 4);
                canvas.drawText("PDAM TIRTA DHAHA KEDIRI", centerX, currentY, paint);
                currentY += 50;

                // Informasi Filter
                paint.setFakeBoldText(false);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTextSize(contentSize);
                String filterInfo = getFilterInfo();
                canvas.drawText(filterInfo, marginHorizontal, currentY, paint);
                currentY += 30;

                // Header Tabel
                paint.setTextSize(headerSize);
                paint.setFakeBoldText(true);

                // Kolom widths (disesuaikan agar semua data bisa masuk di kertas lebar)
                int[] colWidths = {50, 100, 200, 240, 120, 100, 80, 100, 140, 160, 160, 80, 160, 80};
                String[] headers = {"No", "Nosam", "Nama", "Alamat", "Periode", "Tagihan", "Denda",
                        "Total", "Metode", "Kode", "Tgl Bayar", "Status", "Petugas", "Post"};

                // Draw headers
                int startX = marginHorizontal;
                for (int i = 0; i < headers.length; i++) {
                    canvas.drawText(headers[i], startX, currentY, paint);
                    startX += colWidths[i];
                }

                paint.setFakeBoldText(false);
                currentY += rowHeight;

                // Data Tabel
                paint.setTextSize(contentSize);
                int pageNumber = 1;
                int no = 1;

                for (LoketTransaksi trans : currentTransaksiList) {
                    // Cek jika perlu halaman baru (dengan toleransi lebih besar karena kertas lebar)
                    if (currentY > pageInfo.getPageHeight() - marginVertical - rowHeight * 2) {
                        pdfDocument.finishPage(pdfPage);
                        pageNumber++;
                        pdfPage = pdfDocument.startPage(pageInfo);
                        canvas = pdfPage.getCanvas();
                        currentY = marginVertical + 60;

                        // Draw header lagi di halaman baru
                        paint.setTextSize(headerSize);
                        paint.setFakeBoldText(true);
                        startX = marginHorizontal;
                        for (int i = 0; i < headers.length; i++) {
                            canvas.drawText(headers[i], startX, currentY, paint);
                            startX += colWidths[i];
                        }
                        paint.setFakeBoldText(false);
                        currentY += rowHeight;
                        paint.setTextSize(contentSize);
                    }

                    // Draw row data
                    startX = marginHorizontal;

                    // No
                    canvas.drawText(String.valueOf(no++), startX, currentY, paint);
                    startX += colWidths[0];

                    // Nosam
                    canvas.drawText(String.valueOf(trans.getNosam()), startX, currentY, paint);
                    startX += colWidths[1];

                    // Nama (full tanpa dipotong)
                    canvas.drawText(trans.getNamaPelanggan(), startX, currentY, paint);
                    startX += colWidths[2];

                    // Alamat (full tanpa dipotong)
                    canvas.drawText(trans.getAlamat(), startX, currentY, paint);
                    startX += colWidths[3];

                    // Periode
                    canvas.drawText(trans.getPeriode(), startX, currentY, paint);
                    startX += colWidths[4];

                    // Jumlah Tagihan
                    canvas.drawText(String.valueOf(trans.getJumlahTagihan()), startX, currentY, paint);
                    startX += colWidths[5];

                    // Denda
                    canvas.drawText(String.valueOf(trans.getDenda()), startX, currentY, paint);
                    startX += colWidths[6];

                    // Total Bayar
                    canvas.drawText(String.valueOf(trans.getTotalBayar()), startX, currentY, paint);
                    startX += colWidths[7];

                    // Metode Bayar
                    canvas.drawText(trans.getMetodeBayar(), startX, currentY, paint);
                    startX += colWidths[8];

                    // Kode Pembayaran
                    canvas.drawText(trans.getKodePembayaran(), startX, currentY, paint);
                    startX += colWidths[9];

                    // Tanggal Bayar
                    canvas.drawText(formatTanggalShort(trans.getTanggalBayar()), startX, currentY, paint);
                    startX += colWidths[10];

                    // Status
                    canvas.drawText(trans.getStatus(), startX, currentY, paint);
                    startX += colWidths[11];

                    // Petugas (full tanpa dipotong)
                    canvas.drawText(trans.getPetugasNama() != null ? trans.getPetugasNama() :
                            (trans.getPetugas() != null ? trans.getPetugas() : "N/A"), startX, currentY, paint);
                    startX += colWidths[12];

                    // Post
                    canvas.drawText(trans.getPost() != null ? trans.getPost() : "-", startX, currentY, paint);

                    currentY += rowHeight;
                }

                pdfDocument.finishPage(pdfPage);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Laporan_Transaksi_" + timeStamp + "_A0.pdf";
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);

                FileOutputStream outputStream = new FileOutputStream(file);
                pdfDocument.writeTo(outputStream);
                outputStream.close();
                pdfDocument.close();

                runOnUiThread(() -> {
                    hideProgressDialog();
                    showSuccessDialog("File PDF berhasil disimpan di folder Download");
                });

            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showErrorDialog("Gagal membuat PDF: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }


    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        } else {
            return text.substring(0, maxLength - 3) + "...";
        }
    }

    private String formatTanggalShort(String tanggal) {
        if (tanggal == null || tanggal.isEmpty()) {
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(tanggal);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(tanggal);
                return outputFormat.format(date);
            } catch (Exception ex) {
                return tanggal;
            }
        }
    }

    private String getFilterInfo() {
        StringBuilder info = new StringBuilder("Filter: ");

        if ("hari_ini".equals(activeFilter)) {
            info.append("Transaksi Hari Ini");
        } else if ("semua".equals(activeFilter)) {
            String startDate = tvStartDate.getText().toString();
            String endDate = tvEndDate.getText().toString();
            if (!startDate.equals("YYYY-MM-DD") || !endDate.equals("YYYY-MM-DD")) {
                info.append("Periode ");
                if (!startDate.equals("YYYY-MM-DD")) {
                    info.append("dari ").append(startDate);
                }
                if (!endDate.equals("YYYY-MM-DD")) {
                    if (!startDate.equals("YYYY-MM-DD")) {
                        info.append("  s/d  ");
                    }
                    info.append(endDate);
                }
            } else {
                info.append("Semua Transaksi");
            }
        } else {
            info.append("Berdasarkan Nomor Sambungan: ").append(etNomorSambungan.getText().toString());
        }

        return info.toString();
    }

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Sukses")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String formatTanggal(String tanggal) {
        if (tanggal == null || tanggal.isEmpty()) {
            return "";
        }

        // Normalisasi format tanggal sebelum parsing
        String normalizedTanggal = tanggal;
        if (!tanggal.contains(":")) {
            normalizedTanggal += " 00:00:00";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(normalizedTanggal);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(tanggal);
                return outputFormat.format(date);
            } catch (Exception ex) {
                return tanggal;
            }
        }
    }
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }

    private void cariTransaksi() {
        String nosamStr = etNomorSambungan.getText().toString().trim();
        if (!nosamStr.isEmpty()) {
            try {
                int nosam = Integer.parseInt(nosamStr);
                fetchTransaksiByNosam(nosam);
            } catch (NumberFormatException e) {
                hideProgressDialog();
                showErrorDialog("Nomor sambungan harus berupa angka");
            }
        } else {
            hideProgressDialog();
            showErrorDialog("Masukkan nomor sambungan!");
        }
    }

    private void cariByPeriode() {
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();

        if (startDate.equals("YYYY-MM-DD") && endDate.equals("YYYY-MM-DD")) {
            hideProgressDialog();
            showErrorDialog("Masukkan periode tagihan");
            return;
        }

        if (!startDate.equals("YYYY-MM-DD") && !endDate.equals("YYYY-MM-DD")) {
            fetchTransaksiByPeriode(startDate, endDate);
        } else if (!startDate.equals("YYYY-MM-DD")) {
            fetchTransaksiByPeriode(startDate, null);
        } else {
            fetchTransaksiByPeriode(null, endDate);
        }
    }

    private void fetchTransaksiHariIni() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Call<LoketTransaksiResponse> call = apiService.getTransaksiByDate(today, today);
        if (currentCall != null) currentCall.cancel(); // Batalkan panggilan sebelumnya
        currentCall = call;
        call.enqueue(new Callback<LoketTransaksiResponse>() {
            @Override
            public void onResponse(Call<LoketTransaksiResponse> call, Response<LoketTransaksiResponse> response) {
                currentCall = null;
                handleResponse(response, "Hari ini belum terdapat transaksi pembayaran");
            }

            @Override
            public void onFailure(Call<LoketTransaksiResponse> call, Throwable t) {
                currentCall = null;
                handleFailure(t, () -> fetchTransaksiHariIni());
            }
        });
    }

    private void fetchAllTransaksi() {
        Call<LoketTransaksiResponse> call = apiService.getTransaksiPembayaran(null, null, null);
        if (currentCall != null) currentCall.cancel(); // Batalkan panggilan sebelumnya
        currentCall = call;
        call.enqueue(new Callback<LoketTransaksiResponse>() {
            @Override
            public void onResponse(Call<LoketTransaksiResponse> call, Response<LoketTransaksiResponse> response) {
                currentCall = null;
                handleResponse(response, "Tidak ada data transaksi");
            }

            @Override
            public void onFailure(Call<LoketTransaksiResponse> call, Throwable t) {
                currentCall = null;
                handleFailure(t, () -> fetchAllTransaksi());
            }
        });
    }

    private void fetchTransaksiByPeriode(String startDate, String endDate) {
        Call<LoketTransaksiResponse> call;
        if (startDate != null && endDate != null) {
            call = apiService.getTransaksiByDate(startDate, endDate);
        } else if (startDate != null) {
            call = apiService.getTransaksiByDate(startDate, null);
        } else {
            call = apiService.getTransaksiByDate(null, endDate);
        }
        if (currentCall != null) currentCall.cancel(); // Batalkan panggilan sebelumnya
        currentCall = call;
        call.enqueue(new Callback<LoketTransaksiResponse>() {
            @Override
            public void onResponse(Call<LoketTransaksiResponse> call, Response<LoketTransaksiResponse> response) {
                currentCall = null;
                handleResponse(response, "Tidak ada transaksi pada periode tersebut");
                resetFilterButtons();
                btnSemua.setBackgroundResource(R.drawable.rounded_button_basicpressed_selector);
            }

            @Override
            public void onFailure(Call<LoketTransaksiResponse> call, Throwable t) {
                currentCall = null;
                handleFailure(t, () -> fetchTransaksiByPeriode(startDate, endDate));
            }
        });
    }

    private void updateSearchHint() {
        etNomorSambungan.setHint(activeFilter.equals("hari_ini") ?
                "Cari Nosam - Transaksi Hari Ini" : "Cari Nosam - Semua Transaksi");
    }

    private void fetchTransaksiByNosam(int nosam) {
        Call<LoketTransaksiResponse> call = activeFilter.equals("hari_ini") ?
                apiService.getTransaksiByNosamAndDate(nosam,
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()),
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())) :
                apiService.getTransaksiPembayaran(nosam, null, null);
        if (currentCall != null) currentCall.cancel(); // Batalkan panggilan sebelumnya
        currentCall = call;
        call.enqueue(new Callback<LoketTransaksiResponse>() {
            @Override
            public void onResponse(Call<LoketTransaksiResponse> call, Response<LoketTransaksiResponse> response) {
                currentCall = null;
                handleResponse(response, activeFilter.equals("hari_ini") ?
                        "Tidak ada transaksi untuk nomor sambungan ini hari ini" :
                        "Tidak ada transaksi untuk nomor sambungan ini");
            }

            @Override
            public void onFailure(Call<LoketTransaksiResponse> call, Throwable t) {
                currentCall = null;
                handleFailure(t, () -> fetchTransaksiByNosam(nosam));
            }
        });
    }

    private void handleResponse(Response<LoketTransaksiResponse> response, String emptyMessage) {
        hideProgressDialog();

        if (response.isSuccessful() && response.body() != null) {
            // Data akan di-sort oleh adapter
            currentTransaksiList = response.body().getData();
            adapter.setTransaksiList(currentTransaksiList);

            if (currentTransaksiList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyView.setVisibility(View.VISIBLE);
                tvEmptyView.setText(emptyMessage);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyView.setVisibility(View.GONE);
            }
        } else {
            showErrorDialog("Gagal memuat data transaksi");
        }
    }
    private void handleFailure(Throwable t, Runnable retryAction) {
        hideProgressDialog();
        String errorMessage = "Kesalahan jaringan: " + t.getMessage();
        if (t instanceof SocketTimeoutException) {
            errorMessage = "Koneksi timeout. Silakan coba lagi.";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Koneksi Bermasalah")
                .setMessage(errorMessage)
                .setPositiveButton("Coba Lagi", (dialog, which) -> retryAction.run())
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
        if (currentCall != null) {
            currentCall.cancel(); // Pastikan semua panggilan API dibatalkan saat activity dihancurkan
        }
    }
}

