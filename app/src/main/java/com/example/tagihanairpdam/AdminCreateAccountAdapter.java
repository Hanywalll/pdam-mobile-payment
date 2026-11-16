package com.example.tagihanairpdam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminCreateAccountAdapter extends RecyclerView.Adapter<AdminCreateAccountAdapter.ViewHolder> {
    private final List<AdminAccountModel> accounts;
    private final Context context; // Tambahkan context

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvRole, tvEmail, tvStatus;
        public Button btnDetailAkun;

        public ViewHolder(View view) {
            super(view);
            tvUsername = view.findViewById(R.id.tvUsername);
            tvRole = view.findViewById(R.id.tvRole);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvStatus = view.findViewById(R.id.tvStatus);
            btnDetailAkun = view.findViewById(R.id.btnDetailAkun);
        }
    }

    public AdminCreateAccountAdapter(Context context, List<AdminAccountModel> accounts) {
        this.accounts = accounts;
        this.context = context; // Inisialisasi context
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminAccountModel account = accounts.get(position);

        // Bind data to views
        holder.tvUsername.setText(account.getUsername());
        holder.tvRole.setText(account.getRole());
        holder.tvEmail.setText(account.getEmail());
        holder.tvStatus.setText(account.getStatus());

        // Set status color based on status text
        if ("Sudah Aktif".equalsIgnoreCase(account.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#479028")); // Hijau (#479028)
        } else if ("Belum Aktif".equalsIgnoreCase(account.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#973B3B")); // Merah Tua (#973B3B)
        } else {
            holder.tvStatus.setTextColor(Color.GRAY); // Default warna abu-abu jika status tidak dikenali
        }

        // Set OnClickListener untuk tombol Detail Akun
        holder.btnDetailAkun.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminDetailAccountActivity.class);
            intent.putExtra("username", account.getUsername());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}