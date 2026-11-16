package com.example.tagihanairpdam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailTransaksiActivity extends AppCompatActivity {

    private RecyclerView rvDetailTransaksi;
    private TransaksiAdapter transaksiAdapter;
    private ImageButton btnKembali; // Tambahkan deklarasi ImageButton

    public static Intent newIntent(Context packageContext, List<CustomerResponse.Transaksi> transaksiList) {
        Intent intent = new Intent(packageContext, DetailTransaksiActivity.class);
        intent.putExtra("transaksiList", new TransaksiListParcelable(transaksiList));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

        rvDetailTransaksi = findViewById(R.id.rvDetailTransaksi);
        rvDetailTransaksi.setLayoutManager(new LinearLayoutManager(this));

        transaksiAdapter = new TransaksiAdapter(this);
        rvDetailTransaksi.setAdapter(transaksiAdapter);

        // Ambil data transaksi dari Intent
        TransaksiListParcelable transaksiListParcelable = getIntent().getParcelableExtra("transaksiList");
        if (transaksiListParcelable != null) {
            List<CustomerResponse.Transaksi> transaksiList = transaksiListParcelable.getTransaksiList();
            transaksiAdapter.setTransaksiList(transaksiList);
        }

        // Temukan ImageButton dan tambahkan OnClickListener
        btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk memulai LapanganActivity
                Intent intent = new Intent(DetailTransaksiActivity.this, LapanganActivity.class);
                startActivity(intent);
                finish(); // Opsional: tutup DetailTransaksiActivity agar tidak kembali ke halaman ini saat menekan tombol kembali di LapanganActivity
            }
        });
    }
}