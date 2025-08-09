package dao;

import java.sql.*;
import java.util.*;
import config.DBConnection;
import java.text.SimpleDateFormat;
import model.Penjualan;
import model.DetailPenjualan;

public class PenjualanDAO {

    public int createPenjualan(Penjualan penjualan) throws SQLException {
        String sql = "INSERT INTO penjualan (kode_transaksi, tanggal_transaksi, total_harga, metode_pembayaran, kasir_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, penjualan.getKodeTransaksi());
            stmt.setTimestamp(2, new Timestamp(penjualan.getTanggalTransaksi().getTime()));
            stmt.setInt(3, penjualan.getTotalHarga());
            stmt.setString(4, penjualan.getMetodePembayaran());
            stmt.setInt(5, penjualan.getKasirId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return 0;
        }
    }

    public List<Penjualan> getAllPenjualan() throws SQLException {
        List<Penjualan> penjualanList = new ArrayList<>();
        String sql = "SELECT p.*, u.nama as nama_kasir " +
                     "FROM penjualan p " +
                     "LEFT JOIN users u ON p.kasir_id = u.id " +
                     "ORDER BY p.tanggal_transaksi DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Penjualan penjualan = new Penjualan(
                    rs.getInt("id"),
                    rs.getString("kode_transaksi"),
                    rs.getTimestamp("tanggal_transaksi"),
                    rs.getInt("total_harga"),
                    rs.getString("metode_pembayaran"),
                    rs.getInt("kasir_id"),
                    rs.getString("nama_kasir")
                );
                penjualanList.add(penjualan);
            }
        }
        return penjualanList;
    }

    public Penjualan getPenjualanById(int id) throws SQLException {
        String sql = "SELECT p.*, u.nama as nama_kasir " +
                     "FROM penjualan p " +
                     "LEFT JOIN users u ON p.kasir_id = u.id " +
                     "WHERE p.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Penjualan(
                        rs.getInt("id"),
                        rs.getString("kode_transaksi"),
                        rs.getTimestamp("tanggal_transaksi"),
                        rs.getInt("total_harga"),
                        rs.getString("metode_pembayaran"),
                        rs.getInt("kasir_id"),
                        rs.getString("nama_kasir")
                    );
                }
            }
        }
        return null;
    }

    // ✅ Gunakan java.util.Date di parameter
    public List<Penjualan> getPenjualanByPeriode(java.util.Date tanggalMulai, java.util.Date tanggalAkhir) throws SQLException {
        List<Penjualan> penjualanList = new ArrayList<>();
        String sql = "SELECT p.*, u.nama as nama_kasir " +
                     "FROM penjualan p " +
                     "LEFT JOIN users u ON p.kasir_id = u.id " +
                     "WHERE DATE(p.tanggal_transaksi) BETWEEN ? AND ? " +
                     "ORDER BY p.tanggal_transaksi DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(tanggalMulai.getTime()));
            stmt.setDate(2, new java.sql.Date(tanggalAkhir.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Penjualan penjualan = new Penjualan(
                        rs.getInt("id"),
                        rs.getString("kode_transaksi"),
                        rs.getTimestamp("tanggal_transaksi"),
                        rs.getInt("total_harga"),
                        rs.getString("metode_pembayaran"),
                        rs.getInt("kasir_id"),
                        rs.getString("nama_kasir")
                    );
                    penjualanList.add(penjualan);
                }
            }
        }
        return penjualanList;
    }

    public List<DetailPenjualan> getDetailPenjualanByPenjualanId(int penjualanId) throws SQLException {
        List<DetailPenjualan> detailList = new ArrayList<>();
        String sql = "SELECT * FROM detail_penjualan WHERE penjualan_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, penjualanId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetailPenjualan detail = new DetailPenjualan();
                    detail.setId(rs.getInt("id"));
                    detail.setPenjualanId(rs.getInt("penjualan_id"));
                    detail.setProduksiId(rs.getInt("produksi_id"));
                    detail.setJumlah(rs.getInt("jumlah"));
                    detail.setHargaSatuan(rs.getInt("harga_satuan"));
                    detail.setSubtotal(rs.getInt("subtotal"));
                    detailList.add(detail);
                }
            }
        }
        return detailList;
    }

    public List<Penjualan> getPenjualanHariIni(int kasirId) throws SQLException {
        List<Penjualan> penjualanList = new ArrayList<>();
        String sql = "SELECT p.*, u.nama as nama_kasir " +
                     "FROM penjualan p " +
                     "LEFT JOIN users u ON p.kasir_id = u.id " +
                     "WHERE DATE(p.tanggal_transaksi) = CURDATE() " +
                     (kasirId > 0 ? "AND p.kasir_id = ? " : "") +
                     "ORDER BY p.tanggal_transaksi DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (kasirId > 0) {
                stmt.setInt(1, kasirId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Penjualan penjualan = new Penjualan(
                        rs.getInt("id"),
                        rs.getString("kode_transaksi"),
                        rs.getTimestamp("tanggal_transaksi"),
                        rs.getInt("total_harga"),
                        rs.getString("metode_pembayaran"),
                        rs.getInt("kasir_id"),
                        rs.getString("nama_kasir")
                    );
                    penjualanList.add(penjualan);
                }
            }
        }
        return penjualanList;
    }

    public String generateKodeTransaksi() throws SQLException {
        String prefix = "TRX";
        // ✅ Hindari Date yang ambigu
        String tanggal = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());

        String sql = "SELECT COUNT(*) + 1 as nomor FROM penjualan WHERE DATE(tanggal_transaksi) = CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int nomor = rs.getInt("nomor");
                return prefix + tanggal + String.format("%03d", nomor);
            }
        }
        return prefix + tanggal + "001";
    }

    public int getTotalPenjualanHariIni() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_harga), 0) as total FROM penjualan WHERE DATE(tanggal_transaksi) = CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public int getJumlahTransaksiHariIni() throws SQLException {
        String sql = "SELECT COUNT(*) as jumlah FROM penjualan WHERE DATE(tanggal_transaksi) = CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("jumlah");
            }
        }
        return 0;
    }

    public boolean deletePenjualan(int id) throws SQLException {
        String sql = "DELETE FROM penjualan WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Penjualan> searchPenjualan(String keyword) throws SQLException {
        List<Penjualan> penjualanList = new ArrayList<>();
        String sql = "SELECT p.*, u.nama as nama_kasir " +
                     "FROM penjualan p " +
                     "LEFT JOIN users u ON p.kasir_id = u.id " +
                     "WHERE p.kode_transaksi LIKE ? OR u.nama LIKE ? " +
                     "ORDER BY p.tanggal_transaksi DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Penjualan penjualan = new Penjualan(
                        rs.getInt("id"),
                        rs.getString("kode_transaksi"),
                        rs.getTimestamp("tanggal_transaksi"),
                        rs.getInt("total_harga"),
                        rs.getString("metode_pembayaran"),
                        rs.getInt("kasir_id"),
                        rs.getString("nama_kasir")
                    );
                    penjualanList.add(penjualan);
                }
            }
        }
        return penjualanList;
    }
    public void updatePenjualan(Penjualan penjualan) throws SQLException {
    String sql = "UPDATE penjualan SET total_harga = ?, metode_pembayaran = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, penjualan.getTotalHarga());
        stmt.setString(2, penjualan.getMetodePembayaran());
        stmt.setInt(3, penjualan.getId());
        stmt.executeUpdate();
    }
}

}
