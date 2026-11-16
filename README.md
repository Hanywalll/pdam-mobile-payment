# PDAM Tirta Dhaha Kediri – Mobile Payment & Customer Portal

A comprehensive Android native application engineered for **PDAM Tirta Dhaha Kediri** to digitalize municipal water utility billing, counter payments, field meter recording, and customer services.

---

## 📌 Project Overview
- **Application Name:** Tagihan Air PDAM (PDAM Mobile Payment)
- **Organization:** Perumda Air Minum Tirta Dhaha Kota Kediri
- **Platform:** Android Native (Java 8 / AndroidX)
- **Architecture:** Role-Based Access Control (RBAC), Retrofit 2 REST Client, Material Design System

---

## 🚀 Key Modules & Capabilities

### 1. Loket & Kasir (Payment Counters)
- **Inquiry Tagihan:** Search water bills by **No. Sambungan (No. SAM)**, customer name, and billing period.
- **Pelunasan & Denda:** Real-time billing status calculation with automatic late fee adjustments.
- **Struk & Bukti Pembayaran:** Automated thermal receipt generation and digital transaction proof.
- **Metode Pembayaran:** Multi-channel payment tracking (Tunai, QRIS, Bank Transfer).

### 2. Petugas Lapangan (Field Meter Reader)
- **Pencatatan Stand Meter:** Input current water meter readings (`m³`) directly from customer locations.
- **Validasi Visual:** Camera integration for capturing physical meter gauge photos to prevent tampering.
- **Rute Pemeriksaan:** Grouped customer listings by zonal distribution areas.

### 3. Administrator & Developer
- **Manajemen Akun:** Role provisioning for Loket, Lapangan, and Admin officers.
- **Laporan Harian:** Consolidated transaction ledger and financial reconciliation reports.
- **Papan Informasi:** Broadcast public announcements regarding water maintenance and service advisories.
- **Kritik & Saran:** Customer feedback monitoring and resolution dashboard.

---

## 🛠️ Technology Stack
- **Android SDK:** Compile SDK 34, Min SDK 24, Target SDK 34
- **Language:** Java 8
- **Networking:** Retrofit 2, OkHttp 3, Gson Converter
- **UI Components:** AndroidX ConstraintLayout, RecyclerView, CardView, Google Material Components
- **QR / Barcode Scanner:** ZXing Android Embedded
- **Async & Threading:** Android Background Services & Handlers

---

## 👨‍💻 Developer
- **Muhammad Burhanudin** ([@Hanywalll](https://github.com/Hanywalll))
- **Email:** mburhanudin149@gmail.com
- **Portfolio:** [burhanywalll.vercel.app](https://burhanywalll.vercel.app)
