package com.example.tagihanairpdam;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentPrintReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_print_receipt);

        TextView tvReceipt = findViewById(R.id.tvReceipt);

        // Get data from Intent
        String paymentCode = getIntent().getStringExtra("payment_code");
        int totalAmount = getIntent().getIntExtra("total_amount", 0);

        // Display receipt information
        String receiptText = "Bukti Pembayaran\n\n"
                + "Kode Pembayaran: " + paymentCode + "\n"
                + String.format("Total Bayar: Rp %,d", totalAmount) + "\n\n"
                + "Terima kasih atas pembayaran Anda.";

        tvReceipt.setText(receiptText);
    }
}
