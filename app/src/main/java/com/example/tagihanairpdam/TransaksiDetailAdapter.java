package com.example.tagihanairpdam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class TransaksiDetailAdapter extends RecyclerView.Adapter<TransaksiDetailAdapter.ViewHolder> {
    private List<CustomerResponse.Transaksi> transaksiList;

    public TransaksiDetailAdapter(List<CustomerResponse.Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaksi_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerResponse.Transaksi transaksi = transaksiList.get(position);

        holder.tvMeterKemarin.setText("Meter Bulan Kemarin: " + transaksi.getMeterKemarin()); // Tampilkan Meter Bulan Kemarin
        holder.tvMeterSekarang.setText("Meter Bulan Sekarang: " + transaksi.getMeterSekarang()); // Tampilkan Meter Bulan Sekarang
        holder.tvPemakaianAir.setText("Pemakaian Air: " + transaksi.getPemakaianAir() + " m³"); // Menggunakan getPemakaianAir dari Transaksi
        holder.tvMaintenance.setText("Maintenance: " + formatCurrency(transaksi.getMaintenance())); // Tampilkan Maintenance
        holder.tvResKebersihan.setText("Res.Kebersihan: " + formatCurrency(transaksi.getResKebersihan())); // Tampilkan Res.Kebersihan
        holder.tvTanggalBayar.setText("Tanggal Bayar: " + (transaksi.getTanggalBayar() != null ? transaksi.getTanggalBayar() : "-"));
        holder.tvDenda.setText("Denda: " + formatCurrency(transaksi.getDenda()));
        holder.tvTotalTagihan.setText("Total Tagihan: " + formatCurrency(transaksi.getTotalTagihan()));
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeterKemarin, tvMeterSekarang, tvPemakaianAir, tvTanggalBayar, tvDenda, tvTotalTagihan, tvMaintenance, tvResKebersihan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeterKemarin = itemView.findViewById(R.id.tvMeterKemarin); // Inisialisasi TextView baru
            tvMeterSekarang = itemView.findViewById(R.id.tvMeterSekarang); // Inisialisasi TextView baru
            tvPemakaianAir = itemView.findViewById(R.id.tvPemakaianAir);
            tvTanggalBayar = itemView.findViewById(R.id.tvTanggalBayar);
            tvDenda = itemView.findViewById(R.id.tvDenda);
            tvTotalTagihan = itemView.findViewById(R.id.tvTotalTagihan);
            tvMaintenance = itemView.findViewById(R.id.tvMaintenance); // Inisialisasi TextView baru
            tvResKebersihan = itemView.findViewById(R.id.tvResKebersihan); // Inisialisasi TextView baru
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
}