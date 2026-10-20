# 📚 LibraryReg

**Aplikasi Pendaftaran Anggota Perpustakaan** berbasis Android.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)

---

## Soal 1: Deskripsi dan Ruang Lingkup

> Setelah membaca studi kasus, deskripsikan dan buat ruang lingkup aplikasi yang akan dikembangkan.

## A. Deskripsi Aplikasi

**LibraryReg** adalah aplikasi Android untuk pendaftaran anggota perpustakaan perguruan tinggi. Saat ini pendaftaran masih manual dengan formulir kertas, yang menimbulkan empat kendala:

- Antrean pendaftaran yang panjang
- Kesalahan pencatatan data (data tidak lengkap atau salah tulis)
- Kesulitan mencari informasi anggota
- Lambatnya pembuatan kartu anggota

LibraryReg memindahkan proses tersebut ke smartphone. Calon anggota mengisi formulir pendaftaran langsung dari aplikasi, sehingga data lebih rapi dan lengkap. Petugas perpustakaan dapat melihat daftar anggota, membuka detailnya, mencari berdasarkan nama atau nomor induk, serta mengubah dan menghapus data sesuai kebutuhan.

### Tujuan Pengembangan
1. Mempercepat proses pendaftaran anggota dan mengurangi antrean.
2. Mengurangi kesalahan pencatatan melalui validasi data otomatis.
3. Mempermudah pencarian dan pengelolaan data anggota.
4. Menjadi sarana belajar menerapkan konsep pemrograman Android pada kasus nyata, dengan pengembangan fitur secara bertahap.

### Pengguna Aplikasi

| Pengguna | Peran |
|---|---|
| Calon anggota (mahasiswa, dosen, tenaga kependidikan) | Mengisi formulir pendaftaran |
| Petugas perpustakaan | Melihat, mencari, mengubah, dan menghapus data anggota |

## B. Ruang Lingkup Aplikasi

### 1. Fitur yang Termasuk (*In Scope*)

#### a. Formulir Pendaftaran Anggota
Calon anggota mengisi data berikut:
- Nama lengkap
- NIM/NIP
- Program studi/unit
- Alamat
- Nomor telepon
- Email
- Jenis keanggotaan (Mahasiswa, Dosen, Tenaga Kependidikan)

#### b. Validasi Data

| Aturan | Keterangan |
|---|---|
| Kolom wajib | Semua kolom harus terisi |
| Format email | Harus berformat email yang valid |
| Nomor induk (NIM/NIP) | Hanya angka, 8-18 digit, dan tidak boleh sama dengan anggota lain |
| Nomor telepon | Hanya angka, 9-15 digit |

#### c. Manajemen Anggota (oleh Petugas)
- Menampilkan daftar anggota terdaftar
- Melihat detail anggota
- Mencari anggota berdasarkan nama atau nomor induk
- Mengubah (edit) data anggota
- Menghapus data anggota, dengan dialog konfirmasi

### 2. Batasan Aplikasi (*Out of Scope*)

Fitur berikut tidak dibuat pada tahap ini:
- Login dan pembedaan hak akses antara calon anggota dan petugas
- Pembuatan atau pencetakan kartu anggota, dan proses verifikasi dokumen
- Penyimpanan permanen dan sinkronisasi ke server atau cloud. Pada versi saat ini data disimpan di memori aplikasi, sehingga hilang saat aplikasi ditutup
- Fitur perpustakaan lain seperti peminjaman, pengembalian, dan katalog buku
- Notifikasi dan integrasi dengan sistem akademik kampus

### 3. Platform dan Teknologi

| Aspek | Keterangan |
|---|---|
| Platform | Android |
| Bahasa pemrograman | Kotlin |
| Tools | Android Studio |
| Antarmuka | Jetpack Compose (Material 3) |
| Penyimpanan data | Memori aplikasi (sementara) |

### 4. Rencana Pengembangan Bertahap

| Tahap | Fitur |
|---|---|
| 1 | Formulir pendaftaran dan validasi data |
| 2 | Daftar anggota dan pencarian |
| 3 | Detail, edit, dan hapus data |
| Lanjutan (opsional) | Penyimpanan permanen dengan Room, login petugas, kartu anggota digital |

## Soal 2: UI Design

> Buat desain UI yang akan diimplemantasikan.

<img width="146" height="220" alt="Screenshot 2026-10-20 141510" src="https://github.com/user-attachments/assets/a9d12efa-2429-4458-9768-6d959eb9e465" />

