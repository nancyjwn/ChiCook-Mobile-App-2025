# ChiCook - Aplikasi Resep Makanan

**ChiCook** adalah aplikasi resep makanan yang dirancang untuk membantu Anda menemukan, menyimpan, dan menuliskan resep masakan dengan mudah. Dengan tampilan yang sederhana dan user-friendly, aplikasi ini memberikan kemudahan bagi Anda untuk menemukan resep, belajar memasak, serta menulis kreasi masakan Anda.

## Problem

Seringkali kita bingung saat ingin memasak sesuatu dengan bahan yang terbatas. Banyak aplikasi resep yang tidak menyediakan fitur pencarian yang sesuai, atau sulit untuk menemukan resep yang sesuai dengan preferensi pribadi. Selain itu, tidak semua aplikasi resep memiliki tampilan yang ramah pengguna, membuatnya sulit diakses terutama bagi pengguna baru.

## Solution

**ChiCook** hadir untuk memecahkan masalah tersebut dengan menyediakan:
- **Pencarian berbasis bahan:** Pengguna dapat mencari resep hanya dengan memilih filter atau mencari berdasarkan nama.
- **Fitur bookmark:** Simpan resep favorit untuk akses cepat.
- **Fitur notes:** Pengguna dapat menambahkan, menyimpan, dan mengelola catatan pribadi kapanpun.
- **Mode Tema Gelap/Terang:** Sesuaikan tampilan aplikasi sesuai preferensi visual pengguna.
- **User-friendly Interface:** Tampilan yang sederhana dan mudah digunakan, cocok untuk semua kalangan, mulai dari pemula hingga ahli.

## Tampilan Aplikasi

![fix mockup hp](https://github.com/user-attachments/assets/82e5f96d-8829-4827-8cf7-7e915d005729)


*Gambar di atas menunjukkan antarmuka utama aplikasi ChiCook yang sederhana namun efektif untuk mencari resep.*

## Informasi dalam Membuat Aplikasi

### Teknologi yang Digunakan:
- **Android Studio** (IDE utama)
- **Java**
- **RecyclerView & Adapter**
- **Database**
- **Retrofit untuk API (https://www.themealdb.com/)**
- **SharedPreferences untuk Pengaturan Tema**
- **Glide atau Picasso untuk memuat gambar**

## Fitur Aplikasi 

- **Fitur Home:** Memuat random meals, category meals, dan rekomendasi meals. Dimana dapat melihat detail resepnya.
- **Fitur Search:** Memuat random meals pada tampilan, memuat pencarian berdasarkan nama, dan filter berdasarkan area, category, dan ingridients.
- **Fitur Notes:** Dapat menambahkan, menyimpan, mencari dan mengelola catatan pribadi yang ada. 
- **Fitur Bookmarks:** Memuat tampilan resep yang telah disimpan.

## Cara Penggunaan

1. **Mencari Resep Berdasarkan Bahan:**
   - Ketikkan bahan yang Anda miliki, seperti "Chicken", "Chinese", atau "Lemon", dan aplikasi akan menampilkan resep yang sesuai dengan bahan tersebut.
2. **Menyimpan Resep:**
   - Temukan resep favorit Anda dan tekan tombol **bookmark** untuk menyimpannya ke daftar resep favorit.
3. **Menambahkan Catatan Pribadi:**
   - Pada halaman resep, Anda dapat menambahkan catatan pribadi untuk mengingat perubahan bahan atau tips khusus yang Anda coba.

  
## Dokumentasi API
Aplikasi ini menggunakan API dari [TheMealDB](https://www.themealdb.com/) untuk mengambil data resep. Anda dapat mengunjungi situs mereka untuk dokumentasi lengkap atau mengonfigurasi API key jika diperlukan.


---
### Kontribusi
Jika Anda ingin berkontribusi dalam pengembangan aplikasi ini, silakan lakukan fork repository ini, buat perubahan yang Anda inginkan, dan kirimkan pull request. Setiap kontribusi sangat kami hargai untuk membuat **ChiCook** lebih baik lagi.

### Lisensi
Aplikasi ini dilisensikan di bawah MIT License - lihat [LICENSE](LICENSE) untuk lebih lanjut.

