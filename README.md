# 💸 MyDuit

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Navigation 3](https://img.shields.io/badge/Navigation_3-RC01-orange?style=for-the-badge)
![Material 3](https://img.shields.io/badge/Material_3-757575?style=for-the-badge&logo=material-design&logoColor=white)

**MyDuit** adalah aplikasi Android berbasis Jetpack Compose yang dirancang untuk membantu kamu mencatat dan melacak arus kas (pemasukan dan pengeluaran) harian dengan mudah dan cepat.

---

## 📸 Tampilan Aplikasi

| Login | Dashboard Utama | Catat Transasksi | Detail Transaksi |
| :---: | :---: | :---: | :---: |
|<img width="360" alt="Tampilan Login" src="https://github.com/user-attachments/assets/9b18f6c1-0a2b-421c-a529-dea8cd6fc918" /> | <img width="360" alt="Dashboard Utama" src="https://github.com/user-attachments/assets/9e3edd6f-5237-43fb-a8b4-424d8c1fcbeb" /> | <img width="360" alt="Screenshot_20260424_155602" src="https://github.com/user-attachments/assets/00afc2da-0f32-4e3a-b622-168d7ec0eeff" /> | <img width="360" alt="Screenshot_20260424_155214" src="https://github.com/user-attachments/assets/789aaa4f-01c6-4445-a987-a2bf94377728" />|

## 📸 Update Tampilan Aplikasi



---

## ✨ Fitur Utama

- **🔐 Login dengan Validasi** — Halaman login dengan field username dan password. Navigasi ke dashboard hanya bisa dilakukan jika kedua field tidak kosong *(conditional navigation)*.
- **👁️ Show/Hide Password** — Toggle visibilitas password menggunakan icon mata pada field password.
- **💰 Dashboard Saldo Total** — Pantau sisa saldo secara real-time berdasarkan seluruh riwayat transaksi.
- **📝 Catat Transaksi Baru** — Tambahkan data pemasukan atau pengeluaran lengkap dengan nominal dan keterangan.
- **📜 Riwayat Transaksi** — Lihat daftar riwayat keuangan yang bisa difilter berdasarkan jenis transaksi.
- **🔍 Detail Transaksi** — Klik item riwayat untuk melihat detail lengkap transaksi di halaman terpisah *(passing parameter antar layar)*.
- **🎨 UI Modern & Clean** — Dibangun menggunakan Material Design 3.

---

## 🧭 Alur Navigasi

```
LoginScreen
    │
    │ backStack.add(Dashboard)
    │ [hanya jika username & password tidak kosong]
    ▼
DashboardScreen
    │
    │ backStack.add(TransactionDetail(tx.id))
    │ [klik item transaksi → kirim ID sebagai parameter]
    ▼
TransactionDetailScreen
    │
    │ backStack.removeLastOrNull()
    │ [tombol back arrow]
    ▼
DashboardScreen
```

---

## 🛠️ Teknologi yang Digunakan

| Teknologi | Versi | Kegunaan |
| --- | --- | --- |
| Kotlin | 2.0.21 | Bahasa pemrograman utama |
| Jetpack Compose | BOM 2025.05.00 | UI toolkit deklaratif |
| Material Design 3 | - | Sistem desain UI |
| **Jetpack Navigation 3** | **1.0.0-rc01** | **State-driven navigation** |
| lifecycle-viewmodel-navigation3 | 2.9.0-alpha03 | Pengelolaan ViewModel per layar |
| kotlinx-serialization | 1.7.3 | Serialisasi route/key navigasi |
| material-icons-extended | - | Icon tambahan (Visibility, dll) |

---

## 📁 Struktur Folder

```
app/src/main/java/com/example/myduit/
├── core/
│   └── ComposeApp.kt         # Entry point Compose: NavDisplay + entryProvider
├── navigation/
│   ├── Routes.kt             # Definisi semua route (Login, Dashboard, TransactionDetail)
│   └── Compositions.kt       # CompositionLocal untuk LocalBackStack
├── screens/
│   ├── LoginScreen.kt        # Halaman login dengan conditional navigation
│   ├── Dashboard.kt          # Halaman utama + form tambah transaksi
│   └── TransactionDetail.kt  # Halaman detail transaksi (menerima parameter)
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── MainActivity.kt           # Entry point Activity: setContent { ComposeApp() }
```

---

## 🧩 Implementasi Navigation 3

Project ini mengimplementasikan **Jetpack Navigation 3** secara penuh sesuai modul praktikum, meliputi:

### Routes — Definisi Rute (`navigation/Routes.kt`)
```kotlin
@Serializable
object Login : AppRoute          // tanpa parameter → data object

@Serializable
object Dashboard : AppRoute      // tanpa parameter → data object

@Serializable
data class TransactionDetail(    // dengan parameter → data class
    val transactionId: String
) : AppRoute
```

### ComposeApp — NavDisplay + entryProvider (`core/ComposeApp.kt`)
```kotlin
NavDisplay(
    backStack = backStack,
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    ),
    entryProvider = entryProvider {
        entry<Login> { LoginScreen() }
        entry<Dashboard> { DashboardScreen(transactions) { transactions.add(it) } }
        entry<TransactionDetail> {
            val tx = transactions.find { t -> t.id == it.transactionId }
            if (tx != null) TransactionDetailScreen(tx)
        }
    }
)
```

### LocalBackStack — CompositionLocal (`navigation/Compositions.kt`)
```kotlin
val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("No BackStack provided")
}
```

### Aksi Navigasi
| Aksi | Kode |
| --- | --- |
| Pindah ke layar baru | `backStack.add(Dashboard)` |
| Kirim parameter ke layar lain | `backStack.add(TransactionDetail(tx.id))` |
| Kembali ke layar sebelumnya | `backStack.removeLastOrNull()` |
| Conditional navigation | `if (field.isNotBlank()) backStack.add(...)` |

---

## 🚀 Cara Menjalankan Proyek

1. **Clone repositori ini:**
   ```bash
   git clone https://github.com/Punyaadapaa/MyDuit.git
   ```

2. **Buka di Android Studio** (versi Hedgehog atau lebih baru disarankan)

3. **Sync Gradle** — klik **Sync Now** jika muncul notifikasi

4. **Jalankan aplikasi** di Emulator atau Device Fisik (bukan Compose Preview)

> ⚠️ Pastikan menggunakan **Emulator/Device** karena Navigation 3 tidak bisa diuji lewat Compose Preview.

---

## 📋 Checklist Tugas PAB Week 5

- [x] Menggunakan Material 3 dan Jetpack Compose
- [x] Implementasi Navigation 3 (`NavDisplay` + `LocalBackStack`)
- [x] Basic Routing — semua tombol utama berfungsi (`backStack.add`)
- [x] Back Navigation — tombol back arrow berfungsi (`backStack.removeLastOrNull`)
- [x] Passing Parameter — ID transaksi dikirim dari Dashboard ke Detail
- [x] Struktur folder terpisah (`core/`, `navigation/`, `screens/`)
- [x] ⭐ Conditional Navigation — login hanya berhasil jika field tidak kosong

---

## 👥 Kelompok 2

Anggota tim yang berkontribusi dalam pengembangan aplikasi ini:

1. [**Daffa Arkhan Aditama**](https://github.com/Punyaadapaa) — L0324010
2. [**Muhammad Ihsaan Al Fikri**](https://github.com/Ihsaanalf) — L0324024
3. [**Rizal Arief Zuhdi**](https://github.com/rxl2-wqwq) — L0324031

---

> 📚 Referensi: [Jetpack Navigation 3 — Android Developers](https://developer.android.com/guide/navigation/navigation-3?hl=id)

> 📚 Referensi : [Praktikum PAB Pertemuan 5](https://github.com/rizalanggoro/ppab-2026/blob/main/week-06/tugas.md)
