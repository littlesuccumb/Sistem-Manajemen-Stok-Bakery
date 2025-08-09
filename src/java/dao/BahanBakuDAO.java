package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import config.DBConnection;
import model.BahanBaku;

public class BahanBakuDAO {
    
    public BahanBaku getBahanById(int id) throws SQLException {
        String sql = "SELECT * FROM bahan_baku WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BahanBaku(
                        rs.getInt("id"),
                        rs.getString("nama_bahan"),
                        rs.getInt("jumlah"), 
                        rs.getString("satuan"),
                        rs.getInt("harga_satuan"),
                        rs.getDate("tanggal_masuk")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateBahan(BahanBaku bahan) throws SQLException {
        String sql = "UPDATE bahan_baku SET nama_bahan = ?, jumlah = ?, satuan = ?, harga_satuan = ?, tanggal_masuk = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bahan.getNamaBahan());
            stmt.setInt(2, bahan.getJumlah()); 
            stmt.setString(3, bahan.getSatuan());
            stmt.setInt(4, bahan.getHargaSatuan());
            stmt.setDate(5, new java.sql.Date(bahan.getTanggalMasuk().getTime()));
            stmt.setInt(6, bahan.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean isBahanExists(String namaBahan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bahan_baku WHERE LOWER(nama_bahan) = LOWER(?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, namaBahan.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isBahanExists(String namaBahan, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bahan_baku WHERE LOWER(nama_bahan) = LOWER(?) AND id != ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, namaBahan.trim());
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean createBahan(BahanBaku bahan) throws SQLException {
        String sql = "INSERT INTO bahan_baku (nama_bahan, jumlah, satuan, harga_satuan, tanggal_masuk) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bahan.getNamaBahan());
            stmt.setInt(2, bahan.getJumlah()); 
            stmt.setString(3, bahan.getSatuan());
            stmt.setInt(4, bahan.getHargaSatuan());
            stmt.setDate(5, new java.sql.Date(bahan.getTanggalMasuk().getTime()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<BahanBaku> getAllBahan() throws SQLException {
        List<BahanBaku> bahanList = new ArrayList<>();
        String sql = "SELECT * FROM bahan_baku ORDER BY id ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                BahanBaku bahan = new BahanBaku(
                    rs.getInt("id"),
                    rs.getString("nama_bahan"),
                    rs.getInt("jumlah"), 
                    rs.getString("satuan"),
                    rs.getInt("harga_satuan"),
                    rs.getDate("tanggal_masuk")
                );
                bahanList.add(bahan);
            }
        }
        return bahanList;
    }

    public boolean deleteBahan(int id) throws SQLException {
        String sql = "DELETE FROM bahan_baku WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<BahanBaku> searchBahan(String keyword) throws SQLException {
        List<BahanBaku> bahanList = new ArrayList<>();
        String sql = "SELECT * FROM bahan_baku WHERE nama_bahan LIKE ? OR satuan LIKE ? ORDER BY nama_bahan";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BahanBaku bahan = new BahanBaku(
                        rs.getInt("id"),
                        rs.getString("nama_bahan"),
                        rs.getInt("jumlah"), 
                        rs.getString("satuan"),
                        rs.getInt("harga_satuan"),
                        rs.getDate("tanggal_masuk")
                    );
                    bahanList.add(bahan);
                }
            }
        }
        return bahanList;
    }
    
    public List<BahanBaku> getBahanStokMenipis(int minStok) throws SQLException {
        List<BahanBaku> bahanList = new ArrayList<>();
        String sql = "SELECT * FROM bahan_baku WHERE jumlah <= ? ORDER BY jumlah ASC"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, minStok);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BahanBaku bahan = new BahanBaku(
                        rs.getInt("id"),
                        rs.getString("nama_bahan"),
                        rs.getInt("jumlah"), 
                        rs.getString("satuan"),
                        rs.getInt("harga_satuan"),
                        rs.getDate("tanggal_masuk")
                    );
                    bahanList.add(bahan);
                }
            }
        }
        return bahanList;
    }
    
    public List<BahanBaku> getLaporanByPeriode(Date mulai, Date akhir) throws SQLException {
        List<BahanBaku> listBahan = new ArrayList<>();
        String query = "SELECT * FROM bahan_baku WHERE tanggal_masuk BETWEEN ? AND ? ORDER BY tanggal_masuk DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, new java.sql.Date(mulai.getTime()));
            stmt.setDate(2, new java.sql.Date(akhir.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BahanBaku bahan = new BahanBaku();
                    bahan.setId(rs.getInt("id"));
                    bahan.setNamaBahan(rs.getString("nama_bahan"));
                    bahan.setJumlah(rs.getInt("jumlah"));
                    bahan.setSatuan(rs.getString("satuan"));
                    bahan.setHargaSatuan(rs.getInt("harga_satuan"));
                    bahan.setTanggalMasuk(rs.getDate("tanggal_masuk"));
                    listBahan.add(bahan);
                }
            }
        }
        return listBahan;
    }
    
    public boolean cekKetersediaanBahan(int resepId, int jumlahProduksi) throws SQLException {
        String sql = "SELECT dr.jumlah_dibutuhkan, bb.jumlah " + 
                    "FROM detail_resep dr " +
                    "JOIN bahan_baku bb ON dr.bahan_id = bb.id " +
                    "WHERE dr.resep_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, resepId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int jumlahDibutuhkan = rs.getInt("jumlah_dibutuhkan");
                    int stokTersedia = rs.getInt("jumlah"); 
                    int totalDibutuhkan = jumlahDibutuhkan * jumlahProduksi;
                    
                    if (stokTersedia < totalDibutuhkan) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean kurangiStokBahan(int resepId, int jumlahProduksi) throws SQLException {
        String sql = "UPDATE bahan_baku bb " +
                    "JOIN detail_resep dr ON bb.id = dr.bahan_id " +
                    "SET bb.jumlah = bb.jumlah - (dr.jumlah_dibutuhkan * ?) " + 
                    "WHERE dr.resep_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jumlahProduksi);
            stmt.setInt(2, resepId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // NEW METHOD: Cek ketersediaan stok untuk bahan-bahan yang dipilih saat membuat resep
    public String cekKetersediaanStokResep(String[] bahanIds, String[] jumlahDibutuhkan) throws SQLException {
        if (bahanIds == null || jumlahDibutuhkan == null) {
            return null;
        }
        
        String sql = "SELECT nama_bahan, jumlah FROM bahan_baku WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            for (int i = 0; i < bahanIds.length; i++) {
                if (bahanIds[i] != null && !bahanIds[i].isEmpty() && 
                    jumlahDibutuhkan[i] != null && !jumlahDibutuhkan[i].isEmpty()) {
                    
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, Integer.parseInt(bahanIds[i]));
                        
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                int stokTersedia = rs.getInt("jumlah");
                                int stokDibutuhkan = Integer.parseInt(jumlahDibutuhkan[i]);
                                String namaBahan = rs.getString("nama_bahan");
                                
                                if (stokTersedia < stokDibutuhkan) {
                                    return "Stok " + namaBahan + " tidak mencukupi. Tersedia: " + stokTersedia + ", Dibutuhkan: " + stokDibutuhkan;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null; // Semua stok mencukupi
    }
    
    // NEW METHOD: Kurangi stok bahan berdasarkan array bahanIds dan jumlahDibutuhkan
    public boolean kurangiStokBahanResep(String[] bahanIds, String[] jumlahDibutuhkan) throws SQLException {
        if (bahanIds == null || jumlahDibutuhkan == null) {
            return false;
        }
        
        String sql = "UPDATE bahan_baku SET jumlah = jumlah - ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < bahanIds.length; i++) {
                    if (bahanIds[i] != null && !bahanIds[i].isEmpty() && 
                        jumlahDibutuhkan[i] != null && !jumlahDibutuhkan[i].isEmpty()) {
                        
                        stmt.setInt(1, Integer.parseInt(jumlahDibutuhkan[i]));
                        stmt.setInt(2, Integer.parseInt(bahanIds[i]));
                        stmt.addBatch();
                    }
                }
                
                int[] results = stmt.executeBatch();
                conn.commit();
                
                // Cek apakah semua update berhasil
                for (int result : results) {
                    if (result <= 0) {
                        return false;
                    }
                }
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    // NEW METHOD: Update stok individual bahan
    public boolean updateStokBahan(int bahanId, int jumlahBaru) throws SQLException {
        String sql = "UPDATE bahan_baku SET jumlah = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jumlahBaru);
            stmt.setInt(2, bahanId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public void logPenguranganStok(int resepId, int bahanId, String namaBahan, int jumlahDikurangi, String keterangan) throws SQLException {
        String sql = "INSERT INTO log_stok (resep_id, bahan_id, nama_bahan, jumlah_dikurangi, keterangan) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, resepId);
            stmt.setInt(2, bahanId);
            stmt.setString(3, namaBahan);
            stmt.setInt(4, jumlahDikurangi);
            stmt.setString(5, keterangan);

            stmt.executeUpdate();
        }
    }
}