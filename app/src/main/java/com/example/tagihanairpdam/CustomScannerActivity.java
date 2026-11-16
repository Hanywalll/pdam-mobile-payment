package com.example.tagihanairpdam;

import android.os.Bundle;
import android.util.DisplayMetrics;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.Size;

public class CustomScannerActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize BarcodeView
        barcodeView = new DecoratedBarcodeView(this);
        setContentView(barcodeView);

        // Calculate square size (70% of shortest screen dimension)
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) * 0.8);
        Size squareSize = new Size(size, size);

        // Set square scanning area
        barcodeView.getBarcodeView().setFramingRectSize(squareSize);

        // Initialize scanner
        capture = new CaptureManager(this, barcodeView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }
}