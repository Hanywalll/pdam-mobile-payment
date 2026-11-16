package com.example.tagihanairpdam;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inisialisasi ThreeTenABP untuk mendukung java.time di Android
        AndroidThreeTen.init(this);
    }
}