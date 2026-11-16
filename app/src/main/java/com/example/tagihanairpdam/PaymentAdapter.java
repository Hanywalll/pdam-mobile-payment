package com.example.tagihanairpdam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<CustomerResponse.Transaksi> transactions;
    private PaymentClickListener listener;
    // lastUnlockedPosition sekarang akan melacak posisi maksimum yang *bisa* dipilih.
    // Jika ada item yang dipilih, itu akan menjadi batas, bukan posisi terakhir yang belum dibayar.
    private int lastUnlockedPosition = 0;

    public interface PaymentClickListener {
        void onPaymentItemClick(CustomerResponse.Transaksi transaksi, int position, boolean isSelected);
    }

    public PaymentAdapter(List<CustomerResponse.Transaksi> transactions, PaymentClickListener listener) {
        this.transactions = transactions != null ? new ArrayList<>(transactions) : new ArrayList<>();
        // Mengurutkan transaksi berdasarkan periode tagihan (bulan/tahun)
        Collections.sort(this.transactions, (t1, t2) -> {
            String[] parts1 = t1.getPeriodeTagihan().split("/");
            String[] parts2 = t2.getPeriodeTagihan().split("/");
            int year1 = Integer.parseInt(parts1[1]);
            int year2 = Integer.parseInt(parts2[1]);
            int month1 = Integer.parseInt(parts1[0]);
            int month2 = Integer.parseInt(parts2[0]);

            int yearCompare = Integer.compare(year1, year2);
            if (yearCompare != 0) return yearCompare; // Urutkan berdasarkan tahun
            return Integer.compare(month1, month2); // Jika tahun sama, urutkan berdasarkan bulan
        });

        this.listener = listener;

        // Inisialisasi lastUnlockedPosition.
        // Cari transaksi pertama yang "Belum Dibayar". Itu akan jadi titik awal.
        // Jika tidak ada, biarkan lastUnlockedPosition 0.
        boolean foundFirstUnpaid = false;
        for (int i = 0; i < this.transactions.size(); i++) {
            if ("Belum Dibayar".equals(this.transactions.get(i).getStatusPembayaran())) {
                lastUnlockedPosition = i;
                foundFirstUnpaid = true;
                break;
            }
        }
        // Jika semua sudah dibayar atau list kosong, lastUnlockedPosition tetap 0 atau bisa diatur -1
        // Jika 0, maka item pertama (jika ada) akan tetap dipertimbangkan.
        if (!foundFirstUnpaid && !this.transactions.isEmpty()) {
            lastUnlockedPosition = 0; // Pastikan minimal ada posisi awal yang bisa dipertimbangkan.
        } else if (this.transactions.isEmpty()) {
            lastUnlockedPosition = -1; // Jika tidak ada transaksi, tidak ada yang bisa dipilih.
        }
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        CustomerResponse.Transaksi transaksi = transactions.get(position);

        boolean isSelectable;

        if ("Sudah Dibayar".equals(transaksi.getStatusPembayaran())) {
            // Item sudah dibayar, tidak pernah selectable
            isSelectable = false;
        } else {
            // Logika untuk item "Belum Dibayar"
            if (transaksi.isSelected()) {
                // Jika sudah terpilih, dia harus tetap selectable untuk di-unselect
                isSelectable = true;
            } else {
                // Belum terpilih:
                // selectable jika posisinya <= lastUnlockedPosition DAN tidak ada yang terpilih setelahnya.
                isSelectable = position <= lastUnlockedPosition && !hasSelectedAfter(position);
            }
        }

        holder.bind(transaksi, isSelectable, position, listener);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     * Memeriksa apakah ada item yang terpilih setelah posisi tertentu.
     * Digunakan untuk memastikan pengguna memilih transaksi secara berurutan.
     *
     * @param position Posisi saat ini yang sedang diperiksa.
     * @return true jika ada item yang terpilih setelah posisi ini, false jika tidak ada.
     */
    private boolean hasSelectedAfter(int position) {
        for (int i = position + 1; i < transactions.size(); i++) {
            if (transactions.get(i).isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Metode ini dipanggil dari listener ketika sebuah item *dipilih*.
     * Bertujuan untuk membuka kunci item berikutnya yang belum dibayar.
     *
     * @param selectedPosition Posisi transaksi yang baru saja DIPILIH.
     */
    public void onItemSelected(int selectedPosition) {
        transactions.get(selectedPosition).setSelected(true);

        if (selectedPosition >= lastUnlockedPosition && selectedPosition + 1 < getItemCount()) {
            lastUnlockedPosition = selectedPosition + 1;
        }

        notifyItemChanged(selectedPosition);
        if (selectedPosition + 1 < getItemCount()) {
            notifyItemChanged(selectedPosition + 1);
        }
    }
    public void onItemUnselected(int unselectedPosition) {
        // Update status seleksi untuk item yang di-unselect
        transactions.get(unselectedPosition).setSelected(false);

        // Unselect semua item setelah posisi ini
        for (int i = unselectedPosition + 1; i < transactions.size(); i++) {
            if (transactions.get(i).isSelected()) {
                transactions.get(i).setSelected(false);
                // Panggil listener untuk update total
                if (listener != null) {
                    listener.onPaymentItemClick(transactions.get(i), i, false);
                }
            }
        }

        // Update lastUnlockedPosition
        lastUnlockedPosition = unselectedPosition;

        // Refresh tampilan
        notifyItemRangeChanged(unselectedPosition, getItemCount() - unselectedPosition);
    }
    public List<CustomerResponse.Transaksi> getTransactions() {
        return transactions;
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPeriode, tvAmount, tvDetail, btnPay;
        private DateFormatSymbols dfs = new DateFormatSymbols(new Locale("id", "ID"));

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPeriode = itemView.findViewById(R.id.tvPaymentPeriod);
            tvAmount = itemView.findViewById(R.id.tvPaymentAmount);
            tvDetail = itemView.findViewById(R.id.tvPaymentDetail);
            btnPay = itemView.findViewById(R.id.btnSelectPayment);
        }

        public void bind(CustomerResponse.Transaksi transaksi, boolean isSelectable, int position, PaymentClickListener listener) {
            String[] monthNames = dfs.getMonths();
            String[] parts = transaksi.getPeriodeTagihan().split("/");
            String monthName = monthNames[Integer.parseInt(parts[0]) - 1];
            String formattedPeriod = monthName + " " + parts[1];

            tvPeriode.setText(formattedPeriod);
            tvAmount.setText(String.format("Rp %,d", transaksi.getTotalTagihan()));
            String detail = String.format("Biaya Pemakaian: Rp %,d\nDenda: Rp %,d",
                    transaksi.getRekeningAir(),
                    transaksi.getDenda());
            tvDetail.setText(detail);

            if ("Sudah Dibayar".equals(transaksi.getStatusPembayaran())) {
                btnPay.setText("✓");
                btnPay.setBackgroundResource(R.drawable.rounded_button_green_selector);
                btnPay.setEnabled(false); // Tidak bisa di-klik jika sudah dibayar
                btnPay.setAlpha(1.0f); // Tampilan normal untuk sudah dibayar (tidak transparan)
                btnPay.setOnClickListener(null); // Pastikan tidak ada listener
            } else {
                // Set status enabled dan alpha berdasarkan isSelectable
                btnPay.setEnabled(isSelectable);
                btnPay.setAlpha(isSelectable ? 1.0f : 0.5f); // Sedikit transparan jika tidak selectable

                // Set teks dan background tombol
                if (transaksi.isSelected()) {
                    btnPay.setText("✓");
                    btnPay.setBackgroundResource(R.drawable.rounded_button_green_selector);
                } else {
                    btnPay.setText("Bayar Tagihan");
                    btnPay.setBackgroundResource(R.drawable.roounded_button_red_selector);
                }

                // --- MODIFIKASI DIMASUKKAN DI SINI ---
                // Hanya set OnClickListener jika tombol enabled
                if (isSelectable) {
                    btnPay.setOnClickListener(v -> {
                        boolean newState = !transaksi.isSelected();
                        transaksi.setSelected(newState);

                        // Update tampilan segera
                        if (newState) {
                            btnPay.setText("✓");
                            btnPay.setBackgroundResource(R.drawable.rounded_button_green_selector);
                        } else {
                            btnPay.setText("Bayar Tagihan");
                            btnPay.setBackgroundResource(R.drawable.roounded_button_red_selector);
                        }

                        if (listener != null) {
                            listener.onPaymentItemClick(transaksi, position, newState);
                        }
                    });                } else {
                    btnPay.setOnClickListener(null); // Hapus listener jika tidak selectable
                }
                // --- AKHIR MODIFIKASI ---
            }
        }
    }
}