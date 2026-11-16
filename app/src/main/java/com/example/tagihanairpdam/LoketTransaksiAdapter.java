package com.example.tagihanairpdam;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoketTransaksiAdapter extends RecyclerView.Adapter<LoketTransaksiAdapter.TransaksiViewHolder> {

    private List<LoketTransaksi> transaksiList = new ArrayList<>();
    private OnUpdateClickListener updateClickListener;
    // LoketApiService apiService; // Tidak digunakan lagi di adapter karena logic update pindah ke Activity

    public interface OnUpdateClickListener {
        void onUpdateClick(LoketTransaksi transaksi, int position);
    }

    // Constructor (apiService tidak lagi diinisialisasi di sini)
    public LoketTransaksiAdapter() {
        // Tidak perlu inisialisasi apiService di sini
    }

    public void setOnUpdateClickListener(OnUpdateClickListener listener) {
        this.updateClickListener = listener;
    }

    @NonNull
    @Override
    public TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loket_transaksi, parent, false);
        return new TransaksiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiViewHolder holder, int position) {
        LoketTransaksi transaksi = transaksiList.get(position);
        // ... (set text views seperti sebelumnya)
        holder.tvNosam.setText(" " + transaksi.getNosam());

        Locale localeID = new Locale("in", "ID");
        java.text.NumberFormat rupiahFormat = java.text.NumberFormat.getCurrencyInstance(localeID);

        // Modifikasi format agar sesuai dengan "Rp. 500.000,00"
        java.text.DecimalFormatSymbols dfs = new java.text.DecimalFormatSymbols(localeID);
        dfs.setCurrencySymbol("Rp.");
        java.text.DecimalFormat df = (java.text.DecimalFormat) java.text.NumberFormat.getCurrencyInstance(localeID);
        df.setDecimalFormatSymbols(dfs);
        df.setNegativePrefix("(" + df.getPositivePrefix());
        df.setNegativeSuffix(df.getPositiveSuffix() + ")");

        holder.tvTagihan.setText(df.format(transaksi.getJumlahTagihan()).replace("Rp.", "Rp. "));
        holder.tvDenda.setText(df.format(transaksi.getDenda()).replace("Rp.", "Rp. "));
        holder.tvKodePembayaran.setText(transaksi.getKodePembayaran());
        holder.tvMetodeBayar.setText(transaksi.getMetodeBayar());
        holder.tvTanggalBayar.setText(transaksi.getTanggalBayar());
        holder.tvNama.setText(transaksi.getNamaPelanggan());
        holder.tvAlamat.setText(transaksi.getAlamat());
        holder.tvPeriode.setText(transaksi.getPeriode());
        holder.tvTotalBayar.setText(df.format(transaksi.getTotalBayar()).replace("Rp.", "Rp. "));
        holder.tvStatus.setText(transaksi.getStatus());
        holder.tvCreatedAt.setText(transaksi.getCreatedAt());
        holder.tvPetugas.setText(transaksi.getPetugas());
        holder.tvPost.setText(transaksi.getPost() != null ? transaksi.getPost() : "-");
        holder.btnUpdate.setOnClickListener(v -> {
            if (updateClickListener != null) {
                Log.d("Adapter_Debug", "Data dikirim ke listener: NOSAM=" + transaksi.getNosam() +
                        ", TglBayar=" + transaksi.getTanggalBayar() +
                        ", Denda=" + transaksi.getDenda() +
                        ", Petugas=" + transaksi.getPetugas());
                updateClickListener.onUpdateClick(transaksi, holder.getAdapterPosition());
            }
        });
    }
    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public void setTransaksiList(List<LoketTransaksi> transaksiList) {
        // Sort data berdasarkan tahun DESC, bulan DESC, dan tanggal bayar DESC
//        Collections.sort(transaksiList, new Comparator<LoketTransaksi>() {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//
//            @Override
//            public int compare(LoketTransaksi t1, LoketTransaksi t2) {
//                // Urutkan berdasarkan tahun (terbaru dulu)
//                if (t2.getTahun() != t1.getTahun()) {
//                    return t2.getTahun() - t1.getTahun();
//                }
//
//                // Jika tahun sama, urutkan berdasarkan bulan (terbaru dulu)
//                if (t2.getBulan() != t1.getBulan()) {
//                    return t2.getBulan() - t1.getBulan();
//                }
//
//                // Jika bulan sama, urutkan berdasarkan tanggal bayar (terbaru dulu)
//                try {
//                    // Normalisasi format tanggal
//                    String dateStr1 = t1.getTanggalBayar();
//                    String dateStr2 = t2.getTanggalBayar();
//
//                    // Jika format tanggal tanpa waktu, tambahkan waktu default
//                    if (dateStr1 != null && !dateStr1.contains(":")) {
//                        dateStr1 += " 00:00:00";
//                    }
//                    if (dateStr2 != null && !dateStr2.contains(":")) {
//                        dateStr2 += " 00:00:00";
//                    }
//
//                    Date date1 = sdf.parse(dateStr1);
//                    Date date2 = sdf.parse(dateStr2);
//
//                    return date2.compareTo(date1);
//                } catch (ParseException e) {
//                    Log.e("Adapter_Sort", "Error parsing date: " + e.getMessage());
//                    return 0;
//                }
//            }
//        });

        this.transaksiList.clear();
        this.transaksiList.addAll(transaksiList);
        notifyDataSetChanged();
    }    static class TransaksiViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvAlamat, tvPeriode, tvTotalBayar, tvStatus, tvCreatedAt, tvNosam, tvTagihan, tvDenda, tvKodePembayaran, tvMetodeBayar,tvPost, tvTanggalBayar, tvPetugas;
        Button btnUpdate;

        public TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            // ... (findViewById seperti sebelumnya)
            tvNosam = itemView.findViewById(R.id.tvNosam);
            tvTagihan = itemView.findViewById(R.id.tvTagihan);
            tvDenda = itemView.findViewById(R.id.tvDenda);
            tvKodePembayaran = itemView.findViewById(R.id.tvKodePembayaran);
            tvMetodeBayar = itemView.findViewById(R.id.tvMetodeBayar);
            tvTanggalBayar = itemView.findViewById(R.id.tvTanggalBayar);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvPeriode = itemView.findViewById(R.id.tvPeriode);
            tvTotalBayar = itemView.findViewById(R.id.tvTotalBayar);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvPetugas = itemView.findViewById(R.id.tvPetugas);
            tvPost = itemView.findViewById(R.id.tvPost);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}