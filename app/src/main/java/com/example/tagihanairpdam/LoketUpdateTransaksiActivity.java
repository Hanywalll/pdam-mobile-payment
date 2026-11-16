package com.example.tagihanairpdam;



import android.app.DatePickerDialog;

import android.app.Dialog;

import android.app.ProgressDialog;

import android.app.TimePickerDialog;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import android.view.Gravity;

import android.view.Window;

import android.view.WindowManager;

import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ImageButton;

import android.widget.LinearLayout;

import android.widget.ListView;

import android.widget.Spinner;

import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Calendar;

import java.util.Date;

import java.util.List;

import java.util.Locale;

import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



public class LoketUpdateTransaksiActivity extends AppCompatActivity {



    private static final String TAG = "UpdateLoketActivity";

    private static final int UPDATE_REQUEST_CODE = 1001;

    private TextView tvUpdateNosam, tvUpdatePeriode;

    private EditText etUpdateTanggalBayar, etUpdateDenda, etUpdatePetugas, etUpdatePost;

    private Button btnSimpanUpdate, btnPilihTanggalBayar;

    private ImageButton btnKembali;

    private Spinner spinnerDenda, spinnerPetugas;

    private Button btnUbahDenda, btnUbahPetugas;

    private LinearLayout containerUbahDenda, containerUbahPetugas;

    private Dialog dialogDenda, dialogPetugas;



    private LoketApiService apiService;

    private ProgressDialog progressDialog;



    private int nosam, bulan, tahun;

    private String originalTanggalBayar, originalPetugas, originalPost;

    private double originalDenda;

    private String periode;

    private int transaksiId;



    private Calendar selectedDateTime = Calendar.getInstance();

    private SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private ArrayAdapter<String> dendaAdapter;

    private ArrayAdapter<String> petugasAdapter;

    private List<String> dendaList = new ArrayList<>();

    private List<User> listPetugasLapangan = new ArrayList<>();

    private boolean isSpinnerDendaInitialized = false;

    private boolean isSpinnerPetugasInitialized = false;

    private String petugasPrefix = "";

    private String selectedPetugasUsername = "";

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private List<LoketTransaksi> loketTransaksiList = new ArrayList<>();

    private LoketTransaksiAdapter loketTransaksiAdapter;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_loket_transaksi);



// Inisialisasi Api Service

        apiService = RetrofitClient.getClient("https://tagihanairpdam.loca.lt/pdam/mobile_api/").create(LoketApiService.class);



// Inisialisasi view

        btnKembali = findViewById(R.id.btnKembali);

        tvUpdateNosam = findViewById(R.id.tvUpdateNosam);

        tvUpdatePeriode = findViewById(R.id.tvUpdatePeriode);

        etUpdateTanggalBayar = findViewById(R.id.etUpdateTanggalBayar);

        btnPilihTanggalBayar = findViewById(R.id.btnPilihTanggalBayar);

        etUpdatePost = findViewById(R.id.etUpdatePost);

        etUpdateDenda = findViewById(R.id.etUpdateDenda);

        etUpdatePetugas = findViewById(R.id.etUpdatePetugas);

        btnSimpanUpdate = findViewById(R.id.btnSimpanUpdate);

        btnUbahDenda = findViewById(R.id.btnUbahDenda);

        btnUbahPetugas = findViewById(R.id.btnUbahPetugas);



// Inisialisasi dialog

        dialogDenda = new Dialog(this);

        dialogPetugas = new Dialog(this);



// Set listener untuk tombol kembali

        btnKembali.setOnClickListener(v -> onBackPressed());



// Listener untuk tombol "Ubah" Denda

        btnUbahDenda.setOnClickListener(v -> showDendaDialog());



// Listener untuk tombol "Ubah" Petugas

        btnUbahPetugas.setOnClickListener(v -> showPetugasDialog());



// Setup Spinner Denda dengan hint

        dendaList.add("Ubah Denda");

        dendaList.add("3500.00");

        dendaList.add("7500.00");



        dendaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dendaList) {

            @Override

            public boolean isEnabled(int position) {

                return position != 0;

            }

        };



// Setup Spinner Petugas

        petugasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());



// Panggil API untuk mendapatkan daftar petugas lapangan

        getDaftarPetugasLapangan();



// Mendapatkan data transaksi dari intent

        LoketTransaksi transaksi = (LoketTransaksi) getIntent().getSerializableExtra("transaksi");

        if (transaksi != null) {

            transaksiId = transaksi.getId();

            nosam = transaksi.getNosam();

            periode = transaksi.getPeriode();

            originalTanggalBayar = transaksi.getTanggalBayar();

            originalDenda = transaksi.getDenda();

            originalPetugas = transaksi.getPetugas();

            originalPost = transaksi.getPost();



            String[] periodeParts = transaksi.getPeriode().split("/");

            if (periodeParts.length == 2) {

                try {

                    bulan = Integer.parseInt(periodeParts[0]);

                    tahun = Integer.parseInt(periodeParts[1]);

                } catch (NumberFormatException e) {

                    Log.e(TAG, "Error parsing periode: " + transaksi.getPeriode(), e);

                    bulan = -1;

                    tahun = -1;

                }

            }



            tvUpdateNosam.setText(String.valueOf(nosam));

            tvUpdatePeriode.setText(periode);



            if (originalTanggalBayar != null && !originalTanggalBayar.isEmpty()) {

                try {

                    Date date = fullDateFormat.parse(originalTanggalBayar);

                    selectedDateTime.setTime(date);

                    etUpdateTanggalBayar.setText(fullDateFormat.format(date));

                } catch (ParseException e) {

                    Log.e(TAG, "Error parsing original tanggal bayar (full format): " + originalTanggalBayar, e);

                    try {

                        Date date = dateOnlyFormat.parse(originalTanggalBayar);

                        selectedDateTime.setTime(date);

                        selectedDateTime.set(Calendar.HOUR_OF_DAY, 0);

                        selectedDateTime.set(Calendar.MINUTE, 0);

                        etUpdateTanggalBayar.setText(fullDateFormat.format(selectedDateTime.getTime()));

                    } catch (ParseException ex) {

                        Log.e(TAG, "Error parsing original tanggal bayar (date only format): " + originalTanggalBayar, ex);

                        etUpdateTanggalBayar.setText(originalTanggalBayar);

                    }

                }

            } else {

                etUpdateTanggalBayar.setText(fullDateFormat.format(selectedDateTime.getTime()));

            }



            etUpdateDenda.setText(String.format(Locale.getDefault(), "%.2f", originalDenda));

            etUpdatePetugas.setText(originalPetugas);

            etUpdatePost.setText(originalPost);



// Ekstrak prefix dan username dari originalPetugas

            if (originalPetugas != null && originalPetugas.contains(".")) {

                int dotIndex = originalPetugas.indexOf(".");

                petugasPrefix = originalPetugas.substring(0, dotIndex);

                selectedPetugasUsername = originalPetugas.substring(dotIndex + 1);

            } else {

                petugasPrefix = "";

                selectedPetugasUsername = originalPetugas;

            }

        }



// Inisialisasi dateSetListener di dalam onCreate

        dateSetListener = (view, year, month, dayOfMonth) -> {

            selectedDateTime.set(Calendar.YEAR, year);

            selectedDateTime.set(Calendar.MONTH, month);

            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);



            TimePickerDialog.OnTimeSetListener timeSetListener = (timeView, hourOfDay, minute1) -> {

                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);

                selectedDateTime.set(Calendar.MINUTE, minute1);

                selectedDateTime.set(Calendar.SECOND, 0);



                etUpdateTanggalBayar.setText(fullDateFormat.format(selectedDateTime.getTime()));

            };



            new TimePickerDialog(LoketUpdateTransaksiActivity.this, timeSetListener,

                    selectedDateTime.get(Calendar.HOUR_OF_DAY),

                    selectedDateTime.get(Calendar.MINUTE), true).show();

        };



        btnPilihTanggalBayar.setOnClickListener(v -> showDateTimePickerDialog());

        btnSimpanUpdate.setOnClickListener(v -> attemptUpdate());



        loketTransaksiAdapter = new LoketTransaksiAdapter();

    }



    private void getDaftarPetugasLapangan() {

        showProgressDialog("Memuat daftar petugas...");

        Call<UserListResponse> call = apiService.getPetugasLapangan();

        call.enqueue(new Callback<UserListResponse>() {

            @Override

            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {

                hideProgressDialog();

                if (response.isSuccessful() && response.body() != null) {

                    UserListResponse userListResponse = response.body();

                    if (userListResponse.isSuccess()) {

                        listPetugasLapangan.clear();

                        List<String> usernames = userListResponse.getData();

                        List<String> displayUsernames = new ArrayList<>();

                        displayUsernames.add("Pilih Petugas");



                        for (String fullUsername : usernames) {

                            String usernameOnly = fullUsername;

                            if (fullUsername.contains(".")) {

                                usernameOnly = fullUsername.substring(fullUsername.indexOf(".") + 1);

                            }



                            User user = new User();

                            user.setUsername(usernameOnly);

                            listPetugasLapangan.add(user);

                            displayUsernames.add(usernameOnly);

                        }



                        if (!usernames.isEmpty() && usernames.get(0).contains(".")) {

                            petugasPrefix = usernames.get(0).substring(0, usernames.get(0).indexOf("."));

                        }



                        petugasAdapter.clear();

                        petugasAdapter.addAll(displayUsernames);

                        petugasAdapter.notifyDataSetChanged();



                        if (selectedPetugasUsername != null && !selectedPetugasUsername.isEmpty()) {

                            for (int i = 0; i < displayUsernames.size(); i++) {

                                if (displayUsernames.get(i).equals(selectedPetugasUsername)) {

                                    spinnerPetugas.setSelection(i);

                                    break;

                                }

                            }

                        }

                    } else {

                        Toast.makeText(LoketUpdateTransaksiActivity.this,

                                "Gagal memuat daftar petugas: " + userListResponse.getMessage(),

                                Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(LoketUpdateTransaksiActivity.this,

                            "Gagal memuat daftar petugas. Kode: " + response.code(),

                            Toast.LENGTH_SHORT).show();

                }

            }



            @Override

            public void onFailure(Call<UserListResponse> call, Throwable t) {

                hideProgressDialog();

                Toast.makeText(LoketUpdateTransaksiActivity.this,

                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                Log.e(TAG, "Failure getPetugasLapangan", t);

            }

        });

    }



    private void showDateTimePickerDialog() {

        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);

        int minute = selectedDateTime.get(Calendar.MINUTE);



        new DatePickerDialog(LoketUpdateTransaksiActivity.this, dateSetListener,

                selectedDateTime.get(Calendar.YEAR),

                selectedDateTime.get(Calendar.MONTH),

                selectedDateTime.get(Calendar.DAY_OF_MONTH)).show();

    }



    private void attemptUpdate() {

        String newTanggalBayar = etUpdateTanggalBayar.getText().toString().trim();

        String dendaStr = etUpdateDenda.getText().toString().trim();

        String newPetugas = etUpdatePetugas.getText().toString().trim(); // Sudah berisi prefix + username

        String newPost = etUpdatePost.getText().toString().trim();



// Validasi

        if (newTanggalBayar.isEmpty() || dendaStr.isEmpty() || newPetugas.isEmpty()) {

            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();

            return;

        }



        double newDenda;

        try {

            newDenda = Double.parseDouble(dendaStr.replace(',', '.'));

        } catch (NumberFormatException e) {

            Toast.makeText(this, "Format denda tidak valid", Toast.LENGTH_SHORT).show();

            return;

        }



        showProgressDialog("Menyimpan perubahan...");



        if (bulan <= 0 || tahun <= 0) {

            Toast.makeText(this, "Periode tidak valid", Toast.LENGTH_SHORT).show();

            hideProgressDialog();

            return;

        }



// Gunakan newPetugas yang sudah berisi prefix + username

        UpdateTransaksiRequest request = new UpdateTransaksiRequest(

                nosam,

                bulan,

                tahun,

                newTanggalBayar,

                newDenda,

                newPetugas, // Gunakan nilai lengkap dari EditText

                newPost

        );



        Log.d(TAG, "Mengirim data update: " +

                "NOSAM=" + nosam +

                ", Bulan=" + bulan +

                ", Tahun=" + tahun +

                ", TglBayar=" + newTanggalBayar +

                ", Denda=" + newDenda +

                ", Petugas=" + newPetugas + // Log nilai lengkap

                ", Post=" + newPost);



        Call<UpdateTransaksiResponse> call = apiService.updateTransaksi(request, true);

        call.enqueue(new Callback<UpdateTransaksiResponse>() {

            @Override

            public void onResponse(Call<UpdateTransaksiResponse> call, Response<UpdateTransaksiResponse> response) {

                hideProgressDialog();

                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {

                        Toast.makeText(LoketUpdateTransaksiActivity.this,

                                "Update berhasil!", Toast.LENGTH_SHORT).show();



                        Intent returnIntent = new Intent();

                        returnIntent.putExtra("refresh", true);

                        setResult(RESULT_OK, returnIntent);

                        finish();

                    } else {

                        Toast.makeText(LoketUpdateTransaksiActivity.this,

                                "Gagal: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                } else {

                    try {

                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";

                        Log.e(TAG, "Error response: " + errorBody);

                        Toast.makeText(LoketUpdateTransaksiActivity.this,

                                "Error: " + errorBody, Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                }

            }



            @Override

            public void onFailure(Call<UpdateTransaksiResponse> call, Throwable t) {

                hideProgressDialog();

                Log.e(TAG, "Update failed", t);

                Toast.makeText(LoketUpdateTransaksiActivity.this,

                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }



    private void showProgressDialog(String message) {

        if (progressDialog == null) {

            progressDialog = new ProgressDialog(this);

            progressDialog.setCancelable(false);

        }

        progressDialog.setMessage(message);

        progressDialog.show();

    }



    private void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();

        }

    }



    @Override

    protected void onDestroy() {

        super.onDestroy();

        hideProgressDialog();

    }



    private void showDendaDialog() {

        dialogDenda.setContentView(R.layout.dialog_pilih_denda); // Pastikan ini adalah layout yang benar

        Window window = dialogDenda.getWindow();

        if (window != null) {

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            window.setBackgroundDrawableResource(android.R.color.transparent);

            window.setGravity(Gravity.CENTER);

        }

        dialogDenda.setCancelable(true);



        ListView lvDenda = dialogDenda.findViewById(R.id.lvDenda); // Pastikan ID ini benar

        lvDenda.setAdapter(dendaAdapter);



        lvDenda.setOnItemClickListener((parent, view, position, id) -> {

            String selectedDenda = (String) parent.getItemAtPosition(position);

            if (!selectedDenda.equals("Ubah Denda")) {

                etUpdateDenda.setText(selectedDenda);

                dialogDenda.dismiss();

            }

        });



        dialogDenda.show();

    }



    private void showPetugasDialog() {

        dialogPetugas.setContentView(R.layout.dialog_pilih_petugas); // Layout untuk dialog petugas

        Window window = dialogPetugas.getWindow();

        if (window != null) {

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            window.setBackgroundDrawableResource(android.R.color.transparent);

            window.setGravity(Gravity.CENTER);

        }

        dialogPetugas.setCancelable(true);



        ListView lvPetugas = dialogPetugas.findViewById(R.id.lvPetugas); // Pastikan ID ini benar

        lvPetugas.setAdapter(petugasAdapter);



        lvPetugas.setOnItemClickListener((parent, view, position, id) -> {

            String selectedPetugas = (String) parent.getItemAtPosition(position);

            if (selectedPetugas != null && !selectedPetugas.isEmpty() && !selectedPetugas.equals("Pilih Petugas")) {

// Ambil prefix dari nilai yang sudah ada di EditText (jika ada)

                String currentText = etUpdatePetugas.getText().toString();

                String prefixToUse = petugasPrefix; // Default ke petugasPrefix



                if (currentText.contains(".")) {

// Jika sudah ada prefix di EditText, gunakan itu

                    prefixToUse = currentText.substring(0, currentText.indexOf("."));

                }



// Gabungkan prefix dengan username yang dipilih

                selectedPetugasUsername = selectedPetugas;

                String fullPetugasId = prefixToUse + "." + selectedPetugas;

                etUpdatePetugas.setText(fullPetugasId);



                Log.d(TAG, "Petugas dipilih - Prefix: " + prefixToUse +

                        ", Username: " + selectedPetugas +

                        ", Full ID: " + fullPetugasId);



                dialogPetugas.dismiss();

            } else if (selectedPetugas != null && selectedPetugas.equals("Pilih Petugas")) {

                etUpdatePetugas.setText("");

                selectedPetugasUsername = "";

                dialogPetugas.dismiss();

            }

        });



        dialogPetugas.show();

    }



// Inner class User

    public static class User {

        private String username;



        public User() {

        }



        public User(String username) {

            this.username = username;

        }



        public String getUsername() {

            return username;

        }



        public void setUsername(String username) {

            this.username = username;

        }



        @Override

        public String toString() {

            return username != null ? username : "";

        }

    }

}