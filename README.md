# 💸 MyDuit

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Navigation 3](https://img.shields.io/badge/Navigation_3-RC01-orange?style=for-the-badge)
![Material 3](https://img.shields.io/badge/Material_3-757575?style=for-the-badge&logo=material-design&logoColor=white)
![DataStore](https://img.shields.io/badge/DataStore-Preferences-blue?style=for-the-badge)

**MyDuit** adalah aplikasi Android berbasis Jetpack Compose yang dirancang untuk membantu mencatat dan melacak arus kas (pemasukan dan pengeluaran) harian dengan mudah dan cepat.

---

## 📋 Checklist Tugas PAB

### Week 6
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

### Week 9
- [x] Memilih mekanisme penyimpanan yang sesuai (Preferences DataStore)
- [x] Menjelaskan alasan pemilihan penyimpanan data
- [x] Membuat class `UserPreferencesDataStore` dengan operasi save, read, clear
- [x] Menyimpan username saat login berhasil
- [x] Menampilkan username sebagai sapaan di Dashboard via `collectAsState`
- [x] Menghapus data saat logout dan navigasi kembali ke Login

---

## 🗄️ Penyimpanan Data

Mekanisme penyimpanan yang digunakan adalah **Preferences DataStore** dari Jetpack.

### Kenapa DataStore, bukan yang lain?

**File Storage** tidak cocok karena data yang disimpan hanya berupa username (String sederhana), bukan file besar seperti gambar, video, atau dokumen. File Storage lebih tepat kalau MyDuit mau fitur export transaksi ke `.csv` misalnya.

**SharedPreferences** secara fungsional bisa, tapi Google sudah terang-terangan menyarankan migrasi ke DataStore untuk project baru. Masalah utamanya: API synchronous-nya tidak aman dipanggil dari UI thread, tidak ada mekanisme error handling bawaan, dan tidak mendukung Flow secara native sehingga tidak reaktif.

**Preferences DataStore** dipilih karena:
1. Data yang disimpan bertipe sederhana (String username), cocok dengan key-value storage
2. Async by default lewat Kotlin Coroutine + Flow, aman dari ANR
3. Terintegrasi natural dengan `collectAsState()` di Jetpack Compose
4. Error handling built-in
5. Rekomendasi resmi Google sebagai pengganti SharedPreferences

### Yang Diimplementasikan

- `saveUsername(username)` — menyimpan username saat login berhasil
- `usernameFlow` — membaca username sebagai Flow, ditampilkan sebagai sapaan di Dashboard
- `clearUsername()` — menghapus data saat logout

---

## 📸 Tampilan Aplikasi

|                                                   Login                                                   |                                              Dashboard Utama                                              |                                              Catat Transaksi                                              |                                             Detail Transaksi                                              |
| :-------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------: |
| <img width="395" alt="login" src="https://github.com/user-attachments/assets/19876284-1fd8-4236-913e-96f598ea2e85" /> | <img width="360" alt="image" src="https://github.com/user-attachments/assets/5ecf43ce-7b29-409f-8e1a-3efec927b4a4" /> | <img width="360" alt="image" src="https://github.com/user-attachments/assets/837db808-a590-476b-ba7f-ac68d4fbb683" /> | <img width="370" src="https://github.com/user-attachments/assets/789aaa4f-01c6-4445-a987-a2bf94377728" /> |

---

## 📸 Detail Transaksi & Opsi Tambahan

|                                                         Detail Transaksi                                                         |                                                         Opsi Tambahan                                                         |
| :------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------: |
| <img width="215" alt="Detail Transaksi" src="https://github.com/user-attachments/assets/c1f3be18-4c42-4739-8561-348bd581dd97" /> | <img width="215" alt="Opsi Tambahan" src="https://github.com/user-attachments/assets/dc2a58e0-b05f-4150-ae83-55c4716262c4" /> |

### Penjelasan

**Login** — Form username dan password dengan validasi serta fitur show/hide password.

**Dashboard** — Menampilkan sapaan pengguna dari DataStore, saldo total, ringkasan pemasukan
dan pengeluaran, filter transaksi, serta daftar riwayat transaksi.

**Catat Transaksi** — Alert Dialog untuk mengisi keterangan, nominal, dan jenis transaksi
(masuk/keluar).

**Detail Transaksi** — Informasi lengkap transaksi yang dipilih, tombol hapus dengan
konfirmasi Alert Dialog, dan tombol Opsi Tambahan yang membuka Bottom Sheet.

---

## ✨ Fitur Utama

- 🔐 Login dengan validasi
- 👁️ Show / Hide Password
- 👋 Sapaan username dari DataStore
- 💰 Dashboard saldo total
- 📝 Tambah transaksi
- 📜 Riwayat transaksi
- 🔍 Detail transaksi
- ⚙️ Bottom Sheet opsi tambahan
- 🗑️ Hapus transaksi
- 🚪 Logout dengan clear session
- 🎨 UI Material 3 modern

---

## 🧭 Alur Navigasi

```
LoginScreen
│
│ saveUsername(username) → DataStore
│ backStack.add(Dashboard)
▼
DashboardScreen
│ username ← usernameFlow.collectAsState()
│
│ backStack.add(TransactionDetail(tx.id))
▼
TransactionDetailScreen
│
│ backStack.removeLastOrNull()
▼
DashboardScreen
[Logout]
DashboardScreen
│ clearUsername() → DataStore
│ backStack.removeLastOrNull()
▼
LoginScreen
```

---

## 🛠️ Teknologi yang Digunakan

| Teknologi              | Versi          | Kegunaan                    |
| ---------------------- | -------------- | --------------------------- |
| Kotlin                 | 2.0.21         | Bahasa utama                |
| Jetpack Compose        | BOM 2025.05.00 | UI                          |
| Material Design 3      | -              | UI Design                   |
| Navigation 3           | 1.0.0-rc01     | Navigasi                    |
| ViewModel Nav3         | 2.9.0-alpha03  | State                       |
| Serialization          | 1.7.3          | Routing                     |
| Material Icons         | -              | Icon                        |
| DataStore Preferences  | 1.1.4          | Persistensi sesi pengguna   |

---

## 📁 Struktur Folder

```
└── java/com/example/myduit/
├── core/
│   └── ComposeApp.kt
├── data/
│   └── UserPreferencesDataStore.kt   ← NEW (Week 9)
├── navigation/
│   ├── Compositions.kt
│   └── Routes.kt
├── screens/
│   ├── LoginScreen.kt
│   ├── Dashboard.kt
│   └── TransactionDetail.kt
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── MainActivity.kt
```

---

## 🚀 Cara Menjalankan

1. **Clone repository**
```bash
   git clone https://github.com/Punyaadapaa/MyDuit.git
```

2. **Buka di Android Studio**
   - File → Open → pilih folder `MyDuit`

3. **Sync Gradle**
   - Tunggu hingga proses sync selesai
   - Pastikan koneksi internet aktif saat pertama kali sync

4. **Jalankan aplikasi**
   - Hubungkan device fisik atau jalankan emulator
   - Klik tombol ▶ Run atau `Shift + F10`


---

## 👥 Kelompok 2

Aplikasi ini dikembangkan oleh:

1. [**Daffa Arkhan Aditama**](https://github.com/Punyaadapaa) (L0324010)
2. [**Muhammad Ihsaan Al Fikri**](https://github.com/Ihsaanalf) (L0324024)
3. [**Rizal Arief Zuhdi**](https://github.com/rxl2-wqwq) (L0324031)

---

## 📚 Referensi:
1. https://developer.android.com/guide/navigation/navigation-3?hl=id
2. https://developer.android.com/topic/libraries/architecture/datastore
3. https://github.com/rizalanggoro/ppab-2026/blob/main/week-06/tugas.md
4. https://github.com/rizalanggoro/ppab-2026/blob/main/week-09/materi.md
