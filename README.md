# 💸 MyDuit

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Navigation 3](https://img.shields.io/badge/Navigation_3-RC01-orange?style=for-the-badge)
![Material 3](https://img.shields.io/badge/Material_3-757575?style=for-the-badge&logo=material-design&logoColor=white)

**MyDuit** adalah aplikasi Android berbasis Jetpack Compose yang dirancang untuk membantu mencatat dan melacak arus kas (pemasukan dan pengeluaran) harian dengan mudah dan cepat.
Pada **Week 6**, aplikasi ini dikembangkan dengan menambahkan komponen UI interaktif berbasis state:

- Lazy List (LazyColumn)
- Alert Dialog
- Bottom Sheet
- State Management

---

## 🎯 Tujuan Praktikum Week 6

Implementasi komponen UI lanjutan berbasis Compose:

- Menampilkan data secara efisien menggunakan **LazyColumn**
- Menampilkan konfirmasi aksi menggunakan **AlertDialog**
- Menampilkan aksi tambahan menggunakan **ModalBottomSheet**
- Mengelola state UI agar aplikasi responsif dan tidak crash

---

## 📋 Checklist Tugas PAB Week 6

- [x] Menggunakan Material 3 dan Jetpack Compose
- [x] Implementasi Navigation 3 (`NavDisplay` + `LocalBackStack`)
- [x] Basic Routing — semua tombol utama berfungsi (`backStack.add`)
- [x] Back Navigation — tombol back arrow berfungsi (`backStack.removeLastOrNull`)
- [x] Passing Parameter — ID transaksi dikirim dari Dashboard ke Detail
- [x] Struktur folder terpisah (`core/`, `navigation/`, `screens/`)
- [x] Conditional Navigation — login hanya berhasil jika field tidak kosong
- [x] **Lazy List** — `LazyColumn` dengan `key` untuk performa optimal
- [x] **Alert Dialog** — form tambah transaksi + konfirmasi hapus
- [x] **Bottom Sheet** — opsi tambahan di halaman detail transaksi
- [x] Aplikasi tidak crash saat perpindahan layar berulang

---

## 📸 Tampilan Aplikasi

|                                                   Login                                                   |                                              Dashboard Utama                                              |                                              Catat Transaksi                                              |                                             Detail Transaksi                                              |
| :-------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------: |
| <img width="360" src="https://github.com/user-attachments/assets/9b18f6c1-0a2b-421c-a529-dea8cd6fc918" /> | <img width="360" src="https://github.com/user-attachments/assets/9e3edd6f-5237-43fb-a8b4-424d8c1fcbeb" /> | <img width="360" src="https://github.com/user-attachments/assets/00afc2da-0f32-4e3a-b622-168d7ec0eeff" /> | <img width="360" src="https://github.com/user-attachments/assets/789aaa4f-01c6-4445-a987-a2bf94377728" /> |

---

## 📸 Detail Transaksi & Opsi Tambahan

|                                                         Detail Transaksi                                                         |                                                         Opsi Tambahan                                                         |
| :------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------: |
| <img width="240" alt="Detail Transaksi" src="https://github.com/user-attachments/assets/c1f3be18-4c42-4739-8561-348bd581dd97" /> | <img width="240" alt="Opsi Tambahan" src="https://github.com/user-attachments/assets/dc2a58e0-b05f-4150-ae83-55c4716262c4" /> |

Halaman Detail Transaksi menampilkan informasi lengkap transaksi yang dipilih, termasuk nama, tanggal, dan nominal. Terdapat tombol hapus (🗑️) di TopAppBar yang memunculkan Alert Dialog konfirmasi sebelum data dihapus.

Di bagian bawah terdapat tombol "Opsi Tambahan" yang membuka Bottom Sheet berisi aksi tambahan seperti Bagikan Transaksi. Halaman ini menerima data melalui passing parameter berupa ID transaksi dari Dashboard.

---

## ✨ Fitur Utama

- 🔐 Login dengan validasi
- 👁️ Show / Hide Password
- 💰 Dashboard saldo total
- 📝 Tambah transaksi
- 📜 Riwayat transaksi
- 🔍 Detail transaksi
- ⚙️ Bottom Sheet opsi tambahan
- 🗑️ Hapus transaksi
- 🎨 UI Material 3 modern

---

## 🧭 Alur Navigasi

```

LoginScreen
│
│ backStack.add(Dashboard)
▼
DashboardScreen
│
│ backStack.add(TransactionDetail(tx.id))
▼
TransactionDetailScreen
│
│ backStack.removeLastOrNull()
▼
DashboardScreen

```

---

## 🛠️ Teknologi yang Digunakan

| Teknologi         | Versi          | Kegunaan     |
| ----------------- | -------------- | ------------ |
| Kotlin            | 2.0.21         | Bahasa utama |
| Jetpack Compose   | BOM 2025.05.00 | UI           |
| Material Design 3 | -              | UI Design    |
| Navigation 3      | 1.0.0-rc01     | Navigasi     |
| ViewModel Nav3    | 2.9.0-alpha03  | State        |
| Serialization     | 1.7.3          | Routing      |
| Material Icons    | -              | Icon         |

---

## 📁 Struktur Folder

```

java/com/example/myduit/
│
├── core/
│   └── ComposeApp.kt
│
├── navigation/
│   ├── Compositions.kt
│   └── Routes.kt
│
├── screens/
│   ├── login/
│   │   └── LoginScreen.kt
│   ├── dashboard/
│   │   └── Dashboard.kt
│   └── transaction/
│       └── TransactionDetail.kt
│
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
└── MainActivity.kt

```

---

## 🚀 Cara Menjalankan

1. Clone repository
   git clone https://github.com/Punyaadapaa/MyDuit.git
2. Buka di Android Studio
3. Sync Gradle
4. Jalankan di Emulator / Device


## 📋 Checklist Tugas

- [x] Material 3
- [x] Navigation 3
- [x] Routing
- [x] Back Navigation
- [x] Passing Parameter
- [x] Conditional Navigation
- [x] Bottom Sheet
- [x] Delete Transaksi

---

## 👥 Kelompok 2

Aplikasi ini dikembangkan oleh:

1. [**Daffa Arkhan Aditama**](https://github.com/Punyaadapaa) (L0324010)
2. [**Muhammad Ihsaan Al Fikri**](https://github.com/Ihsaanalf) (L0324024)
3. [**Rizal Arief Zuhdi**](https://github.com/rxl2-wqwq) (L0324031)

---

📚 Referensi:  
https://developer.android.com/guide/navigation/navigation-3?hl=id  
https://github.com/rizalanggoro/ppab-2026/blob/main/week-06/tugas.md
