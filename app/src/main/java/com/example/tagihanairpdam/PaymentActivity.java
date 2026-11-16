package com.example.tagihanairpdam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity implements PaymentAdapter.PaymentClickListener {
    private PaymentAdapter paymentAdapter;
    private List<CustomerResponse.Transaksi> selectedTransactions = new ArrayList<>();
    private String nosam;
    private String nama;
    private String alamat; // Tambahkan variabel alamat
    private AlertDialog progressDialog;
    private String lastPaymentCode;
    private int lastTotalAmount;
    private AlertDialog receiptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get data from intent
        Intent intent = getIntent();
        nosam = intent.getStringExtra("nosam");
        nama = intent.getStringExtra("nama");
        alamat = intent.getStringExtra("alamat"); // Dapatkan alamat dari intent

        // Initialize empty list if null
        List<CustomerResponse.Transaksi> transactions = new ArrayList<>();
        if (intent.getParcelableArrayListExtra("transaksi") != null) { // Ganti ke getParcelableArrayListExtra
            transactions = intent.getParcelableArrayListExtra("transaksi");
        }


        // Initialize views
        TextView tvLabel = findViewById(R.id.tvPaymentLabel);
        TextView tvName = findViewById(R.id.tvCustomerName);
        TextView tvTotal = findViewById(R.id.tvTotalPayment);
        RecyclerView recyclerView = findViewById(R.id.rvPaymentList);
        Button btnConfirm = findViewById(R.id.btnConfirmPayment);

        // Set customer data
        tvLabel.setText("Pembayaran atas nama:");
        tvName.setText(nama);
        tvTotal.setText("Total: Rp 0");

        // Setup RecyclerView dengan pengecekan null
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        paymentAdapter = new PaymentAdapter(transactions, this);
        recyclerView.setAdapter(paymentAdapter);

        // Set button listeners
        btnConfirm.setOnClickListener(v -> processPayment());

        findViewById(R.id.btnKembali).setOnClickListener(v -> {
            // Kirim result bahwa activity selesai tanpa pembayaran
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }


    @Override
    public void onPaymentItemClick(CustomerResponse.Transaksi transaksi, int position, boolean isSelected) {
        if (isSelected) {
            if (!selectedTransactions.contains(transaksi)) {
                selectedTransactions.add(transaksi);
            }
            paymentAdapter.onItemSelected(position);
        } else {
            // Hapus transaksi yang di-unselect dan semua setelahnya
            removeTransactionsFromPosition(position);
            paymentAdapter.onItemUnselected(position);
        }
        updateTotal();
    }

    private void removeTransactionsFromPosition(int position) {
        // Dapatkan transaksi yang di-unselect dari adapter
        CustomerResponse.Transaksi unselectedTransaksi = paymentAdapter.getTransactions().get(position);
        int bulan = unselectedTransaksi.getBulan();
        int tahun = unselectedTransaksi.getTahun();

        // Buat iterator untuk selectedTransactions
        Iterator<CustomerResponse.Transaksi> iterator = selectedTransactions.iterator();

        // Iterasi dan hapus transaksi yang sesuai
        while (iterator.hasNext()) {
            CustomerResponse.Transaksi t = iterator.next();
            if ((t.getBulan() == bulan && t.getTahun() == tahun) ||
                    isAfter(t.getBulan(), t.getTahun(), bulan, tahun)) {
                iterator.remove();
            }
        }
    }
    private boolean isAfter(int bulan1, int tahun1, int bulan2, int tahun2) {
        // Cek apakah transaksi1 setelah transaksi2 secara kronologis
        return tahun1 > tahun2 || (tahun1 == tahun2 && bulan1 > bulan2);
    }    private void updateTotal() {
        int total = 0;
        Log.d("PaymentActivity", "Jumlah transaksi saat ini: " + selectedTransactions.size());
        for (CustomerResponse.Transaksi t : selectedTransactions) {
            Log.d("PaymentActivity", "Transaksi: " + t.getBulan() + "/" + t.getTahun() + ", Total: " + t.getTotalTagihan());
            total += t.getTotalTagihan();
        }

        TextView tvTotal = findViewById(R.id.tvTotalPayment);
        tvTotal.setText(String.format(Locale.getDefault(), "Total: Rp %,d", total));

        Button btnConfirm = findViewById(R.id.btnConfirmPayment);
        btnConfirm.setEnabled(!selectedTransactions.isEmpty());
    }

    private void processPayment() {
        if (selectedTransactions.isEmpty()) {
            Toast.makeText(this, "Pilih minimal 1 tagihan untuk dibayar", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CustomerResponse.Transaksi t : selectedTransactions) {
            if (t.getDenda() < 0) {
                Toast.makeText(this, "Denda tidak valid untuk transaksi " + t.getBulan() + "/" + t.getTahun(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Pembayaran")
                .setMessage("Apakah Anda yakin ingin melakukan pembayaran untuk transaksi yang dipilih?")
                .setPositiveButton("Ya", (dialog, which) -> processPaymentToServer())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showProgressDialog() {
        progressDialog = new AlertDialog.Builder(this)
                .setMessage("Memproses pembayaran...")
                .setCancelable(false)
                .create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void createPdf(String paymentCode, int totalAmount) {
        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page pdfPage = pdfDocument.startPage(pageInfo);
            Canvas canvas = pdfPage.getCanvas();
            Paint paint = new Paint();

            int margin = 50;
            int centerX = pageInfo.getPageWidth() / 2;
            int currentY = margin + 40;

            // Judul
            paint.setTextSize(18);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("BUKTI PEMBAYARAN", centerX, currentY, paint);
            currentY += 22;
            paint.setTextSize(14);
            canvas.drawText("PDAM TIRTA DHAHA KEDIRI", centerX, currentY, paint);
            currentY += 18;

            // Garis bawah judul
            paint.setStrokeWidth(1f);
            canvas.drawLine(margin, currentY, pageInfo.getPageWidth() - margin, currentY, paint);
            currentY += 15;

            // Informasi Pelanggan
            paint.setTextSize(12);
            paint.setFakeBoldText(false);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("No. Sambungan: " + nosam, margin, currentY, paint);
            currentY += 20;
            canvas.drawText("Nama Pelanggan: " + nama, margin, currentY, paint);
            currentY += 20;
            canvas.drawText("Alamat: " + alamat, margin, currentY, paint); // Tambahkan alamat
            currentY += 20;


            // Tanggal pembayaran (Gabungkan dan format ulang)
            String dateTimeFormatted = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("id", "ID")).format(new Date());
            // This line is moved below in the transaction details
            // canvas.drawText("Tanggal Bayar: " + dateTimeFormatted, margin, currentY, paint);
            // currentY += 18;

            // Garis pemisah
            paint.setStrokeWidth(1f);
            canvas.drawLine(margin, currentY, pageInfo.getPageWidth() - margin, currentY, paint);
            currentY += 15;

            // Header detail transaksi
            paint.setTextSize(12);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("DETAIL TRANSAKSI", centerX, currentY, paint);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setFakeBoldText(false);
            currentY += 20;

            // Build consolidated transaction details
            int lineHeight = 20;
            paint.setTextSize(12);

            // Mendapatkan data dari transaksi pertama (asumsi semua transaksi punya meter & maintenance yang serupa jika dibayar bersamaan)
            CustomerResponse.Transaksi firstTransaction = selectedTransactions.isEmpty() ? null : selectedTransactions.get(0);

            StringBuilder periodBuilder = new StringBuilder();
            int totalWaterBill = 0;
            int totalPenalty = 0;
            int totalMaintenance = 0;
            int totalResKebersihan = 0;
            int totalPemakaianAir = 0;

            for (CustomerResponse.Transaksi t : selectedTransactions) {
                if (periodBuilder.length() > 0) {
                    periodBuilder.append(", ");
                }
                // Ubah format periode dari MM/yyyy menjadi MM-yyyy
                periodBuilder.append(String.format(Locale.getDefault(), "%02d-%d", t.getBulan(), t.getTahun()));
                totalWaterBill += t.getRekeningAir();
                totalPenalty += t.getDenda();
                totalMaintenance += t.getMaintenance();
                totalResKebersihan += t.getResKebersihan();
                totalPemakaianAir += t.getPemakaianAir();
            }

            // --- Detail Transaksi di PDF ---
            if (firstTransaction != null) {
                canvas.drawText("Meter Bulan Kemarin: " + formatValueOrHyphen(firstTransaction.getMeterKemarin(), false), margin, currentY, paint);
                currentY += lineHeight;
                canvas.drawText("Meter Bulan Sekarang: " + formatValueOrHyphen(firstTransaction.getMeterSekarang(), false), margin, currentY, paint);
                currentY += lineHeight;
            }
            canvas.drawText("Pemakaian Air (m³): " + formatValueOrHyphen(totalPemakaianAir, false), margin, currentY, paint);
            currentY += lineHeight;
            canvas.drawText("Maintenance: " + formatValueOrHyphen(totalMaintenance, true), margin, currentY, paint);
            currentY += lineHeight;
            canvas.drawText("Res.Kebersihan: " + formatValueOrHyphen(totalResKebersihan, true), margin, currentY, paint);
            currentY += lineHeight;
            canvas.drawText("Periode: " + periodBuilder.toString(), margin, currentY, paint);
            currentY += lineHeight;
            canvas.drawText("Rekening Air: " + formatCurrency(totalWaterBill), margin, currentY, paint);
            currentY += lineHeight;
            canvas.drawText("Denda: " + formatCurrency(totalPenalty), margin, currentY, paint);
            currentY += lineHeight;
            // Pindahkan Tanggal Bayar ke bawah Denda
            canvas.drawText("Tanggal Bayar: " + dateTimeFormatted, margin, currentY, paint);
            currentY += lineHeight;
            // --- Akhir Detail Transaksi di PDF ---

            currentY += 30;
            paint.setTextSize(12);
            paint.setFakeBoldText(true);
            canvas.drawText("TOTAL PEMBAYARAN: " + formatCurrency(totalAmount), margin, currentY, paint); // Gunakan formatCurrency
            paint.setFakeBoldText(false);
            currentY += 35;

            // Footer
            paint.setTextSize(10);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Terima kasih telah melakukan pembayaran", centerX, currentY, paint);
            currentY += 15;
            canvas.drawText("PDAM Tirta Dhaha Kediri", centerX, currentY, paint);

            pdfDocument.finishPage(pdfPage);

            // Save to Downloads folder
            File downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "Pembayaran_" + paymentCode + ".pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            pdfDocument.writeTo(fileOutputStream);
            fileOutputStream.close();
            pdfDocument.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuat PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void processPaymentToServer() {
        showProgressDialog();
        List<Map<String, Object>> paymentData = new ArrayList<>();
        final int[] paymentTotals = new int[5]; // Ubah ukuran array untuk menyimpan maintenance dan res_kebersihan

        for (CustomerResponse.Transaksi t : selectedTransactions) {
            Map<String, Object> item = new HashMap<>();
            item.put("bulan", t.getBulan());
            item.put("tahun", t.getTahun());
            item.put("rekening_air", t.getRekeningAir());
            item.put("denda", t.getDenda());
            item.put("maintenance", t.getMaintenance()); // Tambahkan maintenance
            item.put("res_kebersihan", t.getResKebersihan()); // Tambahkan res_kebersihan
            item.put("total_tagihan", t.getTotalTagihan());
            paymentData.add(item);
            paymentTotals[0] += t.getTotalTagihan();
            paymentTotals[1] += t.getDenda();
            paymentTotals[2] += t.getRekeningAir();
            paymentTotals[3] += t.getMaintenance(); // Akumulasikan maintenance
            paymentTotals[4] += t.getResKebersihan(); // Akumulasikan res_kebersihan
        }

        String username = getLoggedInUsername();

        Map<String, Object> request = new HashMap<>();
        request.put("nosam", nosam);
        request.put("transactions", paymentData);
        request.put("metode_bayar", "PDAM");
        request.put("total_bayar", paymentTotals[0]);
        request.put("total_denda", paymentTotals[1]);
        request.put("total_rekening_air", paymentTotals[2]);
        request.put("total_maintenance", paymentTotals[3]); // Kirim total maintenance
        request.put("total_res_kebersihan", paymentTotals[4]); // Kirim total res_kebersihan
        request.put("username", username);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tagihanairpdam.loca.lt/pdam/mobile_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<PaymentResponse> call = apiService.processPayment(request);

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();
                    if (paymentResponse.isSuccess()) {
                        // Update status transaksi yang dibayar
                        for (CustomerResponse.Transaksi t : selectedTransactions) {
                            t.setStatusPembayaran("Sudah Dibayar");
                            t.setSelected(false);
                        }

                        lastPaymentCode = paymentResponse.getData().getKodePembayaran();
                        lastTotalAmount = paymentTotals[0];
                        // Panggil showReceiptPreview dengan semua data yang relevan
                        showReceiptPreview(paymentResponse.getData().getKodePembayaran(), lastTotalAmount);
                    } else {
                        showPaymentError(paymentResponse.getMessage() != null ?
                                paymentResponse.getMessage() : "Pembayaran gagal diproses");
                    }
                } else {
                    showPaymentError("Terjadi kesalahan pada server");
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                dismissProgressDialog();
                showPaymentError("Gagal terhubung ke server: " + t.getMessage());
                Log.e("PaymentError", "Error: " + t.getMessage());
            }
        });
    }

    private void showReceiptPreview(String paymentCode, int totalAmount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_receipt_preview, null);
        builder.setView(view);

        // Inisialisasi TextView
        ImageView ivSuccessIcon = view.findViewById(R.id.ivSuccessIcon);
        TextView tvCustomerInfo = view.findViewById(R.id.tvCustomerInfo);
        TextView tvPaymentInfo = view.findViewById(R.id.tvPaymentInfo);
        TextView tvSimpan = view.findViewById(R.id.tvPrint);
        TextView tvBagikan = view.findViewById(R.id.tvShare);
        TextView tvOk = view.findViewById(R.id.tvOke);

        // Inisialisasi TextView untuk bagian detail transaksi di GridLayout
        TextView tvPeriode = view.findViewById(R.id.tvPeriodeReceipt);
        TextView tvMeterKemarin = view.findViewById(R.id.tvMeterKemarinReceipt);
        TextView tvMeterSekarang = view.findViewById(R.id.tvMeterSekarangReceipt);
        TextView tvPemakaianAir = view.findViewById(R.id.tvPemakaianAirReceipt);
        TextView tvMaintenance = view.findViewById(R.id.tvMaintenanceReceipt);
        TextView tvResKebersihan = view.findViewById(R.id.tvResKebersihanReceipt);
        TextView tvRekeningAir = view.findViewById(R.id.tvRekeningAirReceipt);
        TextView tvDenda = view.findViewById(R.id.tvDendaReceipt);
        TextView tvTanggalBayar = view.findViewById(R.id.tvTanggalBayarReceipt); // Dipindahkan ke sini

        // Set ikon centang
        ivSuccessIcon.setImageResource(R.drawable.ic_checklist);

        // Get the first selected transaction to retrieve meter and additional charges
        final CustomerResponse.Transaksi firstTransaction = selectedTransactions.isEmpty() ? null : selectedTransactions.get(0); // Make it final

        // Format Tanggal dan Waktu Gabungan
        String combinedDateTimeFormatted = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("id", "ID")).format(new Date());

        // Build customer info (tanpa tanggal bayar)
        String customerInfo = "Nosam: " + nosam + "\n" +
                "Nama: " + nama + "\n" +
                "Alamat: " + alamat; // Alamat tetap di sini
        // "Kode Pembayaran" dihilangkan


        // Build transaction details (untuk diisi ke TextView GridLayout)
        final StringBuilder periodsBuilder = new StringBuilder();
        int totalWaterBillValue = 0;
        int totalPenaltyValue = 0;
        int totalMaintenanceValue = 0;
        int totalResKebersihanValue = 0;
        int totalPemakaianAirValue = 0;

        for (CustomerResponse.Transaksi t : selectedTransactions) {
            if (periodsBuilder.length() > 0) {
                periodsBuilder.append(", ");
            }
            // Ubah format periode dari MM/yyyy menjadi MM-yyyy
            periodsBuilder.append(String.format(Locale.getDefault(), "%02d-%d", t.getBulan(), t.getTahun()));
            totalWaterBillValue += t.getRekeningAir();
            totalPenaltyValue += t.getDenda();
            totalMaintenanceValue += t.getMaintenance();
            totalResKebersihanValue += t.getResKebersihan();
            totalPemakaianAirValue += t.getPemakaianAir(); // Akumulasikan pemakaian air
        }

        final int finalTotalWaterBillValue = totalWaterBillValue;
        final int finalTotalPenaltyValue = totalPenaltyValue;
        final int finalTotalMaintenanceValue = totalMaintenanceValue;
        final int finalTotalResKebersihanValue = totalResKebersihanValue;
        final int finalTotalPemakaianAirValue = totalPemakaianAirValue;
        final int finalMeterKemarin = (firstTransaction != null ? firstTransaction.getMeterKemarin() : 0);
        final int finalMeterSekarang = (firstTransaction != null ? firstTransaction.getMeterSekarang() : 0);
        final String finalCombinedDateTimeFormatted = combinedDateTimeFormatted; // Final variable for lambda


        // Set teks untuk TextView di GridLayout
        tvCustomerInfo.setText(customerInfo); // Set informasi pelanggan
        tvPeriode.setText(periodsBuilder.toString());
        tvMeterKemarin.setText(formatValueOrHyphen(finalMeterKemarin, false));
        tvMeterSekarang.setText(formatValueOrHyphen(finalMeterSekarang, false));
        tvPemakaianAir.setText(formatValueOrHyphen(finalTotalPemakaianAirValue, false) + " m³");
        tvMaintenance.setText(formatValueOrHyphen(finalTotalMaintenanceValue, true));
        tvResKebersihan.setText(formatValueOrHyphen(finalTotalResKebersihanValue, true));
        tvRekeningAir.setText(formatValueOrHyphen(finalTotalWaterBillValue, true));
        tvDenda.setText(formatValueOrHyphen(finalTotalPenaltyValue, true));
        tvTanggalBayar.setText(finalCombinedDateTimeFormatted); // Set tanggal bayar yang sudah digabung

        tvPaymentInfo.setText(formatCurrency(totalAmount));


        receiptDialog = builder.create();
        receiptDialog.setCancelable(false);
        receiptDialog.setCanceledOnTouchOutside(false);

        // Handle tombol kembali fisik
        receiptDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                dialog.dismiss();
                refreshPaymentActivity();
                return true; // Event sudah ditangani
            }
            return false;
        });

        tvSimpan.setOnClickListener(v -> {
            createPdf(paymentCode, totalAmount);
            receiptDialog.dismiss();
            new AlertDialog.Builder(this)
                    .setTitle("PDF Berhasil Disimpan")
                    .setMessage("File PDF bukti pembayaran telah disimpan di folder Download.")
                    .setPositiveButton("OK", (d, which) -> refreshPaymentActivity())
                    .setCancelable(false)
                    .show();
        });

        tvOk.setOnClickListener(v -> {
            receiptDialog.dismiss();
            refreshPaymentActivity();
        });

        tvBagikan.setOnClickListener(v -> {
            // Perbarui parameter shareViaWhatsApp
            shareViaWhatsApp(paymentCode, totalAmount, periodsBuilder.toString(), finalTotalWaterBillValue, finalTotalPenaltyValue, finalTotalMaintenanceValue, finalTotalResKebersihanValue, finalTotalPemakaianAirValue,
                    finalMeterKemarin,
                    finalMeterSekarang,
                    finalCombinedDateTimeFormatted); // Tambahkan tanggal bayar gabungan
        });

        receiptDialog.show();
    }

    // Helper method untuk memformat nilai atau menampilkan "-" jika nol
    private String formatValueOrHyphen(int amount, boolean isCurrency) {
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

        java.text.DecimalFormatSymbols dfs = new java.text.DecimalFormatSymbols(localeID);
        dfs.setCurrencySymbol("Rp.");
        java.text.DecimalFormat df = (java.text.DecimalFormat) java.text.NumberFormat.getCurrencyInstance(localeID);
        df.setDecimalFormatSymbols(dfs);
        df.setNegativePrefix("(" + df.getPositivePrefix());
        df.setNegativeSuffix(df.getPositiveSuffix() + ")");

        return df.format(amount).replace("Rp.", "Rp. ");
    }


    private void shareViaWhatsApp(String paymentCode, int totalAmount, String periods, int totalWaterBill, int totalPenalty,
                                  int totalMaintenance, int totalResKebersihan, int totalPemakaianAir,
                                  int meterKemarin, int meterSekarang, String dateTimeBayar) { // Tambahkan dateTimeBayar
        try {
            // First create the PDF file
            File downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS);
            File pdfFile = new File(downloadsDir, "Pembayaran_" + paymentCode + ".pdf");

            // Create the PDF if it doesn't exist
            if (!pdfFile.exists()) {
                createPdf(paymentCode, totalAmount);
            }

            // Create URI for the PDF file
            android.net.Uri pdfUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    pdfFile);

            // Add optional text message with new details
            String message = "Bukti Pembayaran PDAM\n" +
                    "No. Sambungan: " + nosam + "\n" +
                    "Nama: " + nama + "\n" +
                    "Alamat: " + alamat + "\n" +
                    "Periode: " + periods + "\n" +
                    "Meter Bulan Kemarin: " + formatValueOrHyphen(meterKemarin, false) + "\n" +
                    "Meter Bulan Sekarang: " + formatValueOrHyphen(meterSekarang, false) + "\n" +
                    "Pemakaian Air (m³): " + formatValueOrHyphen(totalPemakaianAir, false) + "\n" +
                    "Maintenance: " + formatCurrency(totalMaintenance) + "\n" +
                    "Res.Kebersihan: " + formatCurrency(totalResKebersihan) + "\n" +
                    "Rekening Air: " + formatCurrency(totalWaterBill) + "\n" +
                    "Denda: " + formatCurrency(totalPenalty) + "\n" +
                    "Tanggal Bayar: " + dateTimeBayar + "\n" + // Tanggal bayar gabungan
                    "Total Pembayaran: " + formatCurrency(totalAmount) + "\n" +
                    "Terima kasih atas pembayaran Anda.";

            // Create share intent
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setPackage("com.whatsapp");

            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(shareIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Gagal berbagi PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("WhatsAppShare", "Error sharing PDF", e);
        }
    }


    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        return sharedPreferences.getString("username", "unknown");
    }

    private void showPaymentSuccess(String paymentCode, int totalAmount) {
        showReceiptPreview(paymentCode, totalAmount);
    }

    private void refreshPaymentActivity() {
        // Hapus transaksi yang sudah dibayar dari daftar lokal
        List<CustomerResponse.Transaksi> updatedTransactions = new ArrayList<>();
        for (CustomerResponse.Transaksi t : paymentAdapter.getTransactions()) {
            if (!selectedTransactions.contains(t)) {
                updatedTransactions.add(t);
            }
        }

        // Reset selectedTransactions
        selectedTransactions.clear();

        // Update adapter dengan data yang sudah difilter
        paymentAdapter = new PaymentAdapter(updatedTransactions, this);
        RecyclerView recyclerView = findViewById(R.id.rvPaymentList);
        recyclerView.setAdapter(paymentAdapter);

        // Update total
        updateTotal();

        // Jika semua tagihan sudah dibayar
        if (updatedTransactions.isEmpty()) {
            Toast.makeText(this, "Semua tagihan telah dibayar", Toast.LENGTH_SHORT).show();

            // Kirim result ke LapanganActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("nosam", nosam);
            resultIntent.putExtra("refresh", true);  // Tambahkan flag refresh
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
    private void showPaymentError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Pembayaran Gagal")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
}
