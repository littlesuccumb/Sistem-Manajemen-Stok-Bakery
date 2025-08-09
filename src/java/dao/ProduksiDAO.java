package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import config.DBConnection;
import model.Produksi;

public class ProduksiDAO {
    
    public boolean createProduksi(Produksi produksi) throws SQLException {
        String sql = "INSERT INTO produksi (resep_id, nama_produk, jumlah_produksi, harga_jual, tanggal_produksi, status, gambar) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produksi.getResepId());
            stmt.setString(2, produksi.getNamaProduk());
            stmt.setInt(3, produksi.getJumlahProduksi());
            stmt.setInt(4, produksi.getHargaJual());
            stmt.setDate(5, new java.sql.Date(produksi.getTanggalProduksi().getTime()));
            stmt.setString(6, produksi.getStatus());
            stmt.setString(7, produksi.getGambar());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public List<Produksi> getAllProduksi() throws SQLException {
        List<Produksi> produksiList = new ArrayList<>();
        String sql = "SELECT p.*, r.nama_resep " +
                    "FROM produksi p " +
                    "JOIN resep r ON p.resep_id = r.id " +
                    "ORDER BY p.tanggal_produksi DESC";
        
        System.out.println("=== ProduksiDAO.getAllProduksi() ===");
        System.out.println("SQL Query: " + sql);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("Connection berhasil, menjalankan query...");
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Baris " + count + ":");
                System.out.println("  - ID: " + rs.getInt("id"));
                System.out.println("  - Nama Produk: " + rs.getString("nama_produk"));
                System.out.println("  - Nama Resep: " + rs.getString("nama_resep"));
                System.out.println("  - Gambar: " + rs.getString("gambar"));
                
                Produksi produksi = new Produksi(
                    rs.getInt("id"),
                    rs.getInt("resep_id"),
                    rs.getString("nama_produk"),
                    rs.getInt("jumlah_produksi"),
                    rs.getInt("harga_jual"),
                    rs.getDate("tanggal_produksi"),
                    rs.getString("status"),
                    rs.getString("gambar")
                );
                // Set nama resep secara manual
                produksi.setNamaResep(rs.getString("nama_resep"));
                produksiList.add(produksi);
            }
            
            System.out.println("Total data yang diambil: " + count);
            System.out.println("Ukuran produksiList: " + produksiList.size());
            
        } catch (SQLException e) {
            System.err.println("ERROR di getAllProduksi: " + e.getMessage());
            throw e;
        }
        
        return produksiList;
    }
    
    public List<Produksi> getProdukTersedia() throws SQLException {
        List<Produksi> produksiList = new ArrayList<>();
        String sql = "SELECT p.*, r.nama_resep " +
                    "FROM produksi p " +
                    "JOIN resep r ON p.resep_id = r.id " +
                    "WHERE p.status = 'tersedia' AND p.jumlah_produksi > 0 " +
                    "ORDER BY p.nama_produk";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Produksi produksi = new Produksi(
                    rs.getInt("id"),
                    rs.getInt("resep_id"),
                    rs.getString("nama_produk"),
                    rs.getInt("jumlah_produksi"),
                    rs.getInt("harga_jual"),
                    rs.getDate("tanggal_produksi"),
                    rs.getString("status"),
                    rs.getString("gambar")
                );
                produksi.setNamaResep(rs.getString("nama_resep"));
                produksiList.add(produksi);
            }
        }
        return produksiList;
    }
    
    public Produksi getProduksiById(int id) throws SQLException {
        String sql = "SELECT p.*, r.nama_resep " +
                    "FROM produksi p " +
                    "JOIN resep r ON p.resep_id = r.id " +
                    "WHERE p.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Produksi produksi = new Produksi(
                        rs.getInt("id"),
                        rs.getInt("resep_id"),
                        rs.getString("nama_produk"),
                        rs.getInt("jumlah_produksi"),
                        rs.getInt("harga_jual"),
                        rs.getDate("tanggal_produksi"),
                        rs.getString("status"),
                        rs.getString("gambar")
                    );
                    produksi.setNamaResep(rs.getString("nama_resep"));
                    return produksi;
                }
            }
        }
        return null;
    }
    
    // PERBAIKAN: Method updateProduksi yang salah urutan parameter
    public boolean updateProduksi(Produksi produksi) throws SQLException {
        String sql = "UPDATE produksi SET resep_id = ?, nama_produk = ?, jumlah_produksi = ?, harga_jual = ?, tanggal_produksi = ?, status = ?, gambar = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produksi.getResepId());
            stmt.setString(2, produksi.getNamaProduk());
            stmt.setInt(3, produksi.getJumlahProduksi());
            stmt.setInt(4, produksi.getHargaJual());
            stmt.setDate(5, new java.sql.Date(produksi.getTanggalProduksi().getTime()));
            stmt.setString(6, produksi.getStatus());
            stmt.setString(7, produksi.getGambar());
            stmt.setInt(8, produksi.getId()); // ID harus di parameter terakhir
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteProduksi(int id) throws SQLException {
        String sql = "DELETE FROM produksi WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public List<Produksi> searchProduksi(String keyword) throws SQLException {
        List<Produksi> produksiList = new ArrayList<>();
        String sql = "SELECT p.*, r.nama_resep " +
                    "FROM produksi p " +
                    "JOIN resep r ON p.resep_id = r.id " +
                    "WHERE p.nama_produk LIKE ? OR p.id LIKE ? OR r.nama_resep LIKE ? " +
                    "ORDER BY p.tanggal_produksi DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produksi produksi = new Produksi(
                        rs.getInt("id"),
                        rs.getInt("resep_id"),
                        rs.getString("nama_produk"),
                        rs.getInt("jumlah_produksi"),
                        rs.getInt("harga_jual"),
                        rs.getDate("tanggal_produksi"),
                        rs.getString("status"),
                        rs.getString("gambar")
                    );
                    produksi.setNamaResep(rs.getString("nama_resep"));
                    produksiList.add(produksi);
                }
            }
        }
        return produksiList;
    }
    
    public boolean kurangiStokProduksi(int produksiId, int jumlahTerjual) throws SQLException {
        String sql = "UPDATE produksi SET jumlah_produksi = jumlah_produksi - ? WHERE id = ? AND jumlah_produksi >= ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jumlahTerjual);
            stmt.setInt(2, produksiId);
            stmt.setInt(3, jumlahTerjual);
            
            int rowsAffected = stmt.executeUpdate();
            
            // Update status jika stok habis
            if (rowsAffected > 0) {
                updateStatusProduksi(produksiId);
            }
            
            return rowsAffected > 0;
        }
    }
    
    private void updateStatusProduksi(int produksiId) throws SQLException {
        String sql = "UPDATE produksi SET status = CASE WHEN jumlah_produksi <= 0 THEN 'habis' ELSE 'tersedia' END WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produksiId);
            stmt.executeUpdate();
        }
    }
    
    public List<Produksi> getLaporanProduksiByPeriode(Date tanggalMulai, Date tanggalAkhir) throws SQLException {
        List<Produksi> produksiList = new ArrayList<>();
        String sql = "SELECT p.*, r.nama_resep " +
                    "FROM produksi p " +
                    "JOIN resep r ON p.resep_id = r.id " +
                    "WHERE p.tanggal_produksi BETWEEN ? AND ? " +
                    "ORDER BY p.tanggal_produksi DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(tanggalMulai.getTime()));
            stmt.setDate(2, new java.sql.Date(tanggalAkhir.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produksi produksi = new Produksi(
                        rs.getInt("id"),
                        rs.getInt("resep_id"),
                        rs.getString("nama_produk"),
                        rs.getInt("jumlah_produksi"),
                        rs.getInt("harga_jual"),
                        rs.getDate("tanggal_produksi"),
                        rs.getString("status"),
                        rs.getString("gambar")
                    );
                    produksi.setNamaResep(rs.getString("nama_resep"));
                    produksiList.add(produksi);
                }
            }
        }
        return produksiList;
    }
    
    public int getTotalProduksiHari(Date tanggal) throws SQLException {
        String sql = "SELECT COALESCE(SUM(jumlah_produksi), 0) as total FROM produksi WHERE DATE(tanggal_produksi) = DATE(?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(tanggal.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }
    
    public void tambahStokProduksi(int produksiId, int jumlah) throws SQLException {
    String sql = "UPDATE produksi SET jumlah_produksi = jumlah_produksi + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, jumlah);
        stmt.setInt(2, produksiId);
        stmt.executeUpdate();
    }
}

}