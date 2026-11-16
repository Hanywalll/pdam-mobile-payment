package com.example.tagihanairpdam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
    private List<CustomerResponse.Transaksi> transaksiList = new ArrayList<>();
    private Context context;

    public TransaksiAdapter(Context context) {
        this.context = context;
    }

    // Method baru untuk mendapatkan daftar transaksi
    public List<CustomerResponse.Transaksi> getTransaksiList() {
        return transaksiList;
    }

    public void setTransaksiList(List<CustomerResponse.Transaksi> list) {
        this.transaksiList.clear();
        if (list != null) {
            this.transaksiList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerResponse.Transaksi transaksi = transaksiList.get(position);

        // Set periode tagihan dengan format baru
        String periodeTagihanFormatted = formatTanggal(transaksi.getPeriodeTagihan());
        holder.tvPeriodeTagihan.setText(periodeTagihanFormatted);

        // Format total tagihan
        holder.tvTotalTagihan.setText(formatCurrency(transaksi.getTotalTagihan()));

        // Status pembayaran
        boolean isPaid = transaksi.getTanggalBayar() != null && !transaksi.getTanggalBayar().isEmpty();
        holder.tvStatusPembayaran.setText(isPaid ? "Sudah Dibayar" : "Belum Dibayar");

        // Atur warna dan ikon berdasarkan status pembayaran
        if (isPaid) {
            // Sudah Dibayar
            holder.tvPeriodeTagihan.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.ivCalender.setImageResource(R.drawable.calender_green);
            holder.ivCalender.setColorFilter(ContextCompat.getColor(context, R.color.status_paid));
            holder.tvStatusPembayaran.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.ivStatusIcon.setImageResource(R.drawable.checklist_green);
            holder.btnDetailTransaksi.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.btnDetailTransaksi.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.arrow_right_green), null);
            holder.btnDetailTransaksi.setCompoundDrawableTintList(ContextCompat.getColorStateList(context, R.color.status_paid));
        } else {
            // Belum Dibayar
            holder.tvPeriodeTagihan.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.ivCalender.setImageResource(R.drawable.calender_red);
            holder.ivCalender.setColorFilter(ContextCompat.getColor(context, R.color.status_unpaid));
            holder.tvStatusPembayaran.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.ivStatusIcon.setImageResource(R.drawable.x_icon_red);
            holder.btnDetailTransaksi.setTextColor(ContextCompat.getColor(context, R.color.status_unpaid));
            holder.btnDetailTransaksi.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.arrow_right_red), null);
            holder.btnDetailTransaksi.setCompoundDrawableTintList(ContextCompat.getColorStateList(context, R.color.status_unpaid));
        }

        // Listener untuk tombol detail transaksi
        holder.btnDetailTransaksi.setOnClickListener(v -> {
            showTransactionDetails(transaksi);
        });
    }
    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPeriodeTagihan, tvTotalTagihan, tvStatusPembayaran;
        Button btnDetailTransaksi;
        ImageView ivCalender, ivStatusIcon;

        ViewHolder(View view) {
            super(view);
            tvPeriodeTagihan = view.findViewById(R.id.tvPeriodeTagihan);
            tvTotalTagihan = view.findViewById(R.id.tvTotalTagihan);
            tvStatusPembayaran = view.findViewById(R.id.tvStatusPembayaran);
            btnDetailTransaksi = view.findViewById(R.id.btnDetailTransaksi);
            ivCalender = view.findViewById(R.id.ivCalender);
            ivStatusIcon = view.findViewById(R.id.ivStatusIcon);
        }
    }

    private void showTransactionDetails(CustomerResponse.Transaksi transaksi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_transaction_details, null);

        // Inisialisasi TextView untuk data yang sudah ada dan yang baru
        TextView tvMeterKemarin = dialogView.findViewById(R.id.tvMeterKemarin);
        TextView tvMeterSekarang = dialogView.findViewById(R.id.tvMeterSekarang);
        TextView tvPemakaianAir = dialogView.findViewById(R.id.tvPemakaianAir);
        TextView tvRekeningAir = dialogView.findViewById(R.id.tvRekeningAirDetail); // ID baru untuk Rekening Air di dialog
        TextView tvMaintenance = dialogView.findViewById(R.id.tvMaintenanceDetail);
        TextView tvResKebersihan = dialogView.findViewById(R.id.tvResKebersihanDetail);
        TextView tvTanggalBayar = dialogView.findViewById(R.id.tvTanggalBayar);
        TextView tvDenda = dialogView.findViewById(R.id.tvDenda);
        TextView tvTotalTagihan = dialogView.findViewById(R.id.tvTotalTagihan);

        // Set teks untuk data, gunakan "-" jika nilai nol
        tvMeterKemarin.setText(formatValueOrHyphen(transaksi.getMeterKemarin(), false));
        tvMeterSekarang.setText(formatValueOrHyphen(transaksi.getMeterSekarang(), false));
        tvPemakaianAir.setText(formatValueOrHyphen(transaksi.getPemakaianAir(), false) + " m³");
        tvRekeningAir.setText(formatValueOrHyphen(transaksi.getRekeningAir(), true)); // Set teks untuk Rekening Air
        tvMaintenance.setText(formatValueOrHyphen(transaksi.getMaintenance(), true));
        tvResKebersihan.setText(formatValueOrHyphen(transaksi.getResKebersihan(), true));
        tvTanggalBayar.setText((transaksi.getTanggalBayar() != null && !transaksi.getTanggalBayar().isEmpty() ? transaksi.getTanggalBayar() : "-"));
        tvDenda.setText(formatValueOrHyphen(transaksi.getDenda(), true));

        // Hitung total tagihan untuk periode ini, termasuk semua komponen
        int total = transaksi.getRekeningAir() + transaksi.getDenda() + transaksi.getMaintenance() + transaksi.getResKebersihan();
        tvTotalTagihan.setText(formatCurrency(total));


        builder.setView(dialogView)
                .setPositiveButton("OK", null);
        builder.show();
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

    public String formatTanggal(String tanggalAwal) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID")); // Perbaiki format tahun menjadi 'yyyy'

        try {
            Date date = inputFormat.parse(tanggalAwal);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Format Tanggal Salah";
        }
    }
}
