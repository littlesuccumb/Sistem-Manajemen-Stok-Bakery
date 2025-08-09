package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import config.DBConnection;
import model.DetailPenjualan;

public class DetailPenjualanDAO {
    
    /**
     * Create detail penjualan
     */
    public boolean createDetail(DetailPenjualan detail) throws SQLException {
        String sql = "INSERT INTO detail_penjualan (penjualan_id, produksi_id, jumlah, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detail.getPenjualanId());
            stmt.setInt(2, detail.getProduksiId());
            stmt.setInt(3, detail.getJumlah());
            stmt.setInt(4, detail.getHargaSatuan());
            stmt.setInt(5, detail.getSubtotal());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Get detail penjualan berdasarkan penjualan ID
     */
    public List<DetailPenjualan> getDetailByPenjualanId(int penjualanId) throws SQLException {
        List<DetailPenjualan> detailList = new ArrayList<>();
        String sql = "SELECT dp.*, p.nama_produk, p.gambar " +
                    "FROM detail_penjualan dp " +
                    "JOIN produksi p ON dp.produksi_id = p.id " +
                    "WHERE dp.penjualan_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, penjualanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetailPenjualan detail = new DetailPenjualan(
                        rs.getInt("id"),
                        rs.getInt("penjualan_id"),
                        rs.getInt("produksi_id"),
                        rs.getInt("jumlah"),
                        rs.getInt("harga_satuan"),
                        rs.getInt("subtotal"),
                        rs.getString("nama_produk"),
                        rs.getString("gambar")
                    );
                    detailList.add(detail);
                }
            }
        }
        return detailList;
    }
    
    /**
     * Delete detail penjualan berdasarkan penjualan ID
     */
    public boolean deleteByPenjualanId(int penjualanId) throws SQLException {
        String sql = "DELETE FROM detail_penjualan WHERE penjualan_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, penjualanId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Get produk terlaris (untuk laporan)
     */
    public List<DetailPenjualan> getProdukTerlaris(int limit) throws SQLException {
        List<DetailPenjualan> detailList = new ArrayList<>();
        String sql = "SELECT dp.produksi_id, p.nama_produk, p.gambar, " +
                    "SUM(dp.jumlah) as total_terjual, " +
                    "SUM(dp.subtotal) as total_pendapatan " +
                    "FROM detail_penjualan dp " +
                    "JOIN produksi p ON dp.produksi_id = p.id " +
                    "GROUP BY dp.produksi_id, p.nama_produk, p.gambar " +
                    "ORDER BY total_terjual DESC " +
                    "LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetailPenjualan detail = new DetailPenjualan();
                    detail.setProduksiId(rs.getInt("produksi_id"));
                    detail.setNamaProduk(rs.getString("nama_produk"));
                    detail.setGambar(rs.getString("gambar"));
                    detail.setJumlah(rs.getInt("total_terjual"));
                    detail.setSubtotal(rs.getInt("total_pendapatan"));
                    detailList.add(detail);
                }
            }
        }
        return detailList;
    }
    
    /**
     * Get penjualan produk berdasarkan periode
     */
    public List<DetailPenjualan> getPenjualanProdukByPeriode(java.util.Date tanggalMulai, java.util.Date tanggalAkhir) throws SQLException {
        List<DetailPenjualan> detailList = new ArrayList<>();
        String sql = "SELECT dp.produksi_id, p.nama_produk, p.gambar, " +
                    "SUM(dp.jumlah) as total_terjual, " +
                    "SUM(dp.subtotal) as total_pendapatan, " +
                    "AVG(dp.harga_satuan) as rata_harga " +
                    "FROM detail_penjualan dp " +
                    "JOIN produksi p ON dp.produksi_id = p.id " +
                    "JOIN penjualan pj ON dp.penjualan_id = pj.id " +
                    "WHERE DATE(pj.tanggal_transaksi) BETWEEN ? AND ? " +
                    "GROUP BY dp.produksi_id, p.nama_produk, p.gambar " +
                    "ORDER BY total_terjual DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(tanggalMulai.getTime()));
            stmt.setDate(2, new java.sql.Date(tanggalAkhir.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetailPenjualan detail = new DetailPenjualan();
                    detail.setProduksiId(rs.getInt("produksi_id"));
                    detail.setNamaProduk(rs.getString("nama_produk"));
                    detail.setGambar(rs.getString("gambar"));
                    detail.setJumlah(rs.getInt("total_terjual"));
                    detail.setSubtotal(rs.getInt("total_pendapatan"));
                    detail.setHargaSatuan(rs.getInt("rata_harga"));
                    detailList.add(detail);
                }
            }
        }
        return detailList;
    }
    
    /**
     * Get total penjualan berdasarkan produk ID
     */
    public int getTotalPenjualanProduk(int produksiId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(jumlah), 0) as total FROM detail_penjualan WHERE produksi_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produksiId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }
    
    /**
     * Get detail penjualan dengan informasi lengkap untuk laporan
     */
    public List<DetailPenjualan> getDetailLaporanByPeriode(java.util.Date tanggalMulai, java.util.Date tanggalAkhir) throws SQLException {
        List<DetailPenjualan> detailList = new ArrayList<>();
        String sql = "SELECT dp.*, p.nama_produk, p.gambar, pj.kode_transaksi, pj.tanggal_transaksi " +
                    "FROM detail_penjualan dp " +
                    "JOIN produksi p ON dp.produksi_id = p.id " +
                    "JOIN penjualan pj ON dp.penjualan_id = pj.id " +
                    "WHERE DATE(pj.tanggal_transaksi) BETWEEN ? AND ? " +
                    "ORDER BY pj.tanggal_transaksi DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(tanggalMulai.getTime()));
            stmt.setDate(2, new java.sql.Date(tanggalAkhir.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetailPenjualan detail = new DetailPenjualan(
                        rs.getInt("id"),
                        rs.getInt("penjualan_id"),
                        rs.getInt("produksi_id"),
                        rs.getInt("jumlah"),
                        rs.getInt("harga_satuan"),
                        rs.getInt("subtotal"),
                        rs.getString("nama_produk"),
                        rs.getString("gambar")
                    );
                    detailList.add(detail);
                }
            }
        }
        return detailList;
    }
}