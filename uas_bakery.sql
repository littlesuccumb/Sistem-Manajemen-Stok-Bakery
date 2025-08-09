-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 01 Agu 2025 pada 04.40
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `uas_bakery`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `bahan_baku`
--

CREATE TABLE `bahan_baku` (
  `id` int(11) NOT NULL,
  `nama_bahan` varchar(100) NOT NULL,
  `jumlah` int(11) NOT NULL DEFAULT 0,
  `satuan` varchar(20) NOT NULL,
  `harga_satuan` int(11) DEFAULT 0,
  `tanggal_masuk` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `bahan_baku`
--

INSERT INTO `bahan_baku` (`id`, `nama_bahan`, `jumlah`, `satuan`, `harga_satuan`, `tanggal_masuk`) VALUES
(1, 'Tepung Terigu', 46150, 'gram', 100, '2025-07-18'),
(2, 'Gula Pasir', 18900, 'gram', 90, '2025-07-17'),
(3, 'Ragi', 4944, 'gram', 300, '2025-07-18'),
(4, 'Mentega', 18975, 'gram', 250, '2025-07-18'),
(5, 'Telur', 2481, 'pcs', 2000, '2025-06-30'),
(6, 'Coklat Bubuk', 2930, 'gram', 500, '2025-06-30'),
(7, 'Coklat Batang', 3990, 'gram', 600, '2025-07-01'),
(8, 'Keju', 4900, 'gram', 800, '2025-06-19'),
(9, 'Susu Bubuk', 9850, 'gram', 400, '2025-06-19'),
(10, 'Minyak Zaitun', 1980, 'ml', 500, '2025-06-18'),
(11, 'Garam', 1984, 'gram', 50, '2025-06-25'),
(12, 'Sosis', 1980, 'pcs', 2500, '2025-07-16'),
(13, 'Air Dingin/Hangat', 49150, 'ml', 50, '2025-07-21'),
(14, 'Gula Halus', 30000, 'gram', 100, '2025-06-14'),
(15, 'ChocoChip', 7950, 'gram', 650, '2025-07-24'),
(16, 'Pasta Pandan', 3500, 'ml', 1000, '2025-06-29'),
(17, 'Saus Tomat', 1470, 'botol', 4000, '2025-06-28'),
(18, 'Susu Cair', 5950, 'ml', 500, '2025-07-31'),
(19, 'Santan', 3000, 'ml', 200, '2025-07-31'),
(20, 'Vanilli Bubuk', 1999, 'pcs', 500, '2025-07-31');

-- --------------------------------------------------------

--
-- Struktur dari tabel `detail_penjualan`
--

CREATE TABLE `detail_penjualan` (
  `id` int(11) NOT NULL,
  `penjualan_id` int(11) NOT NULL,
  `produksi_id` int(11) NOT NULL,
  `jumlah` int(11) NOT NULL,
  `harga_satuan` int(11) NOT NULL,
  `subtotal` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `detail_penjualan`
--

INSERT INTO `detail_penjualan` (`id`, `penjualan_id`, `produksi_id`, `jumlah`, `harga_satuan`, `subtotal`) VALUES
(1, 1, 8, 2, 40000, 80000),
(2, 2, 1, 4, 30000, 120000),
(3, 3, 4, 10, 15000, 150000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `detail_resep`
--

CREATE TABLE `detail_resep` (
  `id` int(11) NOT NULL,
  `resep_id` int(11) NOT NULL,
  `bahan_id` int(11) NOT NULL,
  `jumlah_dibutuhkan` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `detail_resep`
--

INSERT INTO `detail_resep` (`id`, `resep_id`, `bahan_id`, `jumlah_dibutuhkan`) VALUES
(1, 1, 1, 500),
(2, 1, 2, 50),
(3, 1, 3, 10),
(4, 1, 4, 50),
(5, 1, 5, 2),
(6, 1, 9, 20),
(7, 2, 1, 500),
(8, 2, 2, 60),
(9, 2, 3, 11),
(10, 2, 4, 50),
(11, 2, 5, 2),
(12, 2, 13, 200),
(13, 3, 1, 250),
(14, 3, 2, 300),
(15, 3, 4, 150),
(16, 3, 5, 3),
(17, 3, 6, 50),
(18, 3, 18, 50),
(24, 4, 1, 200),
(25, 4, 2, 150),
(26, 4, 4, 100),
(27, 4, 5, 3),
(28, 4, 9, 30),
(29, 4, 20, 1),
(30, 5, 1, 500),
(31, 5, 2, 50),
(32, 5, 3, 10),
(33, 5, 4, 300),
(34, 5, 11, 10),
(35, 5, 13, 250),
(36, 6, 1, 300),
(37, 6, 2, 10),
(38, 6, 3, 5),
(39, 6, 8, 50),
(40, 6, 10, 20),
(41, 6, 11, 5),
(42, 6, 12, 20),
(43, 6, 13, 150),
(44, 6, 17, 30),
(45, 7, 1, 500),
(46, 7, 2, 50),
(47, 7, 3, 10),
(48, 7, 4, 25),
(49, 7, 9, 20),
(50, 7, 11, 1),
(51, 7, 13, 250),
(52, 8, 1, 200),
(53, 8, 2, 150),
(54, 8, 4, 100),
(55, 8, 5, 3),
(56, 8, 6, 20),
(57, 8, 9, 30),
(58, 9, 1, 200),
(59, 9, 2, 80),
(60, 9, 4, 100),
(61, 9, 5, 1),
(62, 9, 7, 10),
(63, 9, 15, 50),
(64, 10, 1, 500),
(65, 10, 2, 50),
(66, 10, 3, 10),
(67, 10, 4, 50),
(68, 10, 5, 2),
(69, 10, 8, 50),
(70, 10, 9, 20);

-- --------------------------------------------------------

--
-- Struktur dari tabel `log_stok`
--

CREATE TABLE `log_stok` (
  `id` int(11) NOT NULL,
  `resep_id` int(11) DEFAULT NULL,
  `bahan_id` int(11) DEFAULT NULL,
  `nama_bahan` varchar(100) DEFAULT NULL,
  `jumlah_dikurangi` int(11) DEFAULT NULL,
  `tanggal` timestamp NOT NULL DEFAULT current_timestamp(),
  `keterangan` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `penjualan`
--

CREATE TABLE `penjualan` (
  `id` int(11) NOT NULL,
  `kode_transaksi` varchar(20) NOT NULL,
  `tanggal_transaksi` date NOT NULL,
  `total_harga` int(11) NOT NULL,
  `metode_pembayaran` enum('tunai','qris','kartu_kredit') NOT NULL,
  `kasir_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `penjualan`
--

INSERT INTO `penjualan` (`id`, `kode_transaksi`, `tanggal_transaksi`, `total_harga`, `metode_pembayaran`, `kasir_id`) VALUES
(1, 'TRX20250801001', '2025-08-01', 80000, 'tunai', 3),
(2, 'TRX20250801002', '2025-08-01', 120000, 'tunai', 3),
(3, 'TRX20250801003', '2025-08-01', 150000, 'tunai', 3);

-- --------------------------------------------------------

--
-- Struktur dari tabel `produksi`
--

CREATE TABLE `produksi` (
  `id` int(11) NOT NULL,
  `resep_id` int(11) NOT NULL,
  `nama_produk` varchar(100) NOT NULL,
  `jumlah_produksi` int(11) NOT NULL,
  `harga_jual` int(11) NOT NULL,
  `tanggal_produksi` date NOT NULL,
  `status` enum('tersedia','habis') DEFAULT 'tersedia',
  `gambar` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `produksi`
--

INSERT INTO `produksi` (`id`, `resep_id`, `nama_produk`, `jumlah_produksi`, `harga_jual`, `tanggal_produksi`, `status`, `gambar`) VALUES
(1, 5, 'Crossaint', 21, 30000, '2025-07-30', 'tersedia', '1753992830204_Crosaint.png'),
(2, 3, 'Cake Coklat', 20, 50000, '2025-07-30', 'tersedia', '1753992858658_kue coklat.png'),
(3, 2, 'Donat Pinky', 30, 7000, '2025-07-26', 'tersedia', '1753992869267_Donat pink.png'),
(4, 4, 'Cupcake Vanilla', 10, 15000, '2025-07-26', 'tersedia', '1753992880300_cupcake vanila.png'),
(5, 8, 'Cupcake Coklat', 25, 15000, '2025-07-26', 'tersedia', '1753992893752_Cupcake coklat.png'),
(6, 7, 'Roti Tawar', 50, 2000, '2025-07-20', 'tersedia', '1753992905657_Roti tawar.png'),
(7, 9, 'Cookies Coklat', 40, 4000, '2025-07-20', 'tersedia', '1753992918560_cokis coklat.png'),
(8, 6, 'Pizza Mini', 8, 40000, '2025-07-31', 'tersedia', '1753992761793_Download_free_image_of_Delicious_pepperoni_pizza_slices_by_Pinn_about_pizza_slices__food__cheese__pizza__and_cheese_pizza_15276632-removebg-preview.png'),
(9, 10, 'Cheese Bread', 30, 10000, '2025-07-31', 'tersedia', '1753992778790_Download_AI_generated_cheese_toasted_sandwich_png_for_free-removebg-preview.png'),
(10, 2, 'Donat Coklat', 25, 7000, '2025-07-31', 'tersedia', '1753992809270_Chocolate_Donut_Png_Image___TopPNG-removebg-preview.png');

-- --------------------------------------------------------

--
-- Struktur dari tabel `resep`
--

CREATE TABLE `resep` (
  `id` int(11) NOT NULL,
  `nama_resep` varchar(100) NOT NULL,
  `deskripsi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `resep`
--

INSERT INTO `resep` (`id`, `nama_resep`, `deskripsi`) VALUES
(1, 'Adonan Roti Manis', 'Adonan dasar roti dengan tekstur empuk dan rasa manis lembut.'),
(2, 'Adonan Donat', 'Adonan donat empuk dan manis, cocok untuk topping gula halus atau glaze.'),
(3, 'Adonan Cake Coklat', 'Kue bolu coklat dengan tekstur lembut, cocok untuk acara spesial'),
(4, 'Adonan Cupcake Vanilla', 'Kue kecil lembut rasa vanilla, cocok untuk camilan atau pesta.'),
(5, 'Adonan Crossaint', 'Roti lipat berlapis dengan teknik laminasi dan tekstur renyah.'),
(6, 'Adonan Pizza Dasar + Topping', 'Pizza ukuran kecil dengan topping klasik : saus, keju, dan sosis.'),
(7, 'Adonan Roti Tawar', 'Roti putih dengan tesktur padat dan rasa netral, cocok untuk sandwich.'),
(8, 'Adonan Cupcake Coklat', 'Kue kecil lembut rasa coklat, cocok untuk camilan atau pesta.'),
(9, 'Adonan Cookies Coklat', 'Kue kering renyah dengan chocochip, cocok untuk camilan.'),
(10, 'Adonan Roti Dasar + Keju', 'Roti empuk dengan taburan keju leleh di atasnya.');

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('gudang','baker','kasir') NOT NULL,
  `nama` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `nama`) VALUES
(1, 'gudang1', 'g123', 'gudang', 'Petugas Gudang'),
(2, 'baker1', 'b123', 'baker', 'Baker'),
(3, 'kasir1', 'k123', 'kasir', 'Kasir');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `bahan_baku`
--
ALTER TABLE `bahan_baku`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `detail_penjualan`
--
ALTER TABLE `detail_penjualan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `penjualan_id` (`penjualan_id`),
  ADD KEY `produksi_id` (`produksi_id`);

--
-- Indeks untuk tabel `detail_resep`
--
ALTER TABLE `detail_resep`
  ADD PRIMARY KEY (`id`),
  ADD KEY `resep_id` (`resep_id`),
  ADD KEY `bahan_id` (`bahan_id`);

--
-- Indeks untuk tabel `log_stok`
--
ALTER TABLE `log_stok`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `penjualan`
--
ALTER TABLE `penjualan`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode_transaksi` (`kode_transaksi`),
  ADD KEY `kasir_id` (`kasir_id`);

--
-- Indeks untuk tabel `produksi`
--
ALTER TABLE `produksi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `resep_id` (`resep_id`);

--
-- Indeks untuk tabel `resep`
--
ALTER TABLE `resep`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `bahan_baku`
--
ALTER TABLE `bahan_baku`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT untuk tabel `detail_penjualan`
--
ALTER TABLE `detail_penjualan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `detail_resep`
--
ALTER TABLE `detail_resep`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT untuk tabel `log_stok`
--
ALTER TABLE `log_stok`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `penjualan`
--
ALTER TABLE `penjualan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `produksi`
--
ALTER TABLE `produksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `resep`
--
ALTER TABLE `resep`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `detail_penjualan`
--
ALTER TABLE `detail_penjualan`
  ADD CONSTRAINT `detail_penjualan_ibfk_1` FOREIGN KEY (`penjualan_id`) REFERENCES `penjualan` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detail_penjualan_ibfk_2` FOREIGN KEY (`produksi_id`) REFERENCES `produksi` (`id`);

--
-- Ketidakleluasaan untuk tabel `detail_resep`
--
ALTER TABLE `detail_resep`
  ADD CONSTRAINT `detail_resep_ibfk_1` FOREIGN KEY (`resep_id`) REFERENCES `resep` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detail_resep_ibfk_2` FOREIGN KEY (`bahan_id`) REFERENCES `bahan_baku` (`id`);

--
-- Ketidakleluasaan untuk tabel `penjualan`
--
ALTER TABLE `penjualan`
  ADD CONSTRAINT `penjualan_ibfk_1` FOREIGN KEY (`kasir_id`) REFERENCES `users` (`id`);

--
-- Ketidakleluasaan untuk tabel `produksi`
--
ALTER TABLE `produksi`
  ADD CONSTRAINT `produksi_ibfk_1` FOREIGN KEY (`resep_id`) REFERENCES `resep` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
