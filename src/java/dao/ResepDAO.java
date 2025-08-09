package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import config.DBConnection;
import model.Resep;
import model.DetailResep;

public class ResepDAO {
    
    public boolean createResep(Resep resep) throws SQLException {
        String sql = "INSERT INTO resep (nama_resep, deskripsi) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, resep.getNamaResep());
            stmt.setString(2, resep.getDeskripsi());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public int createResepWithDetails(Resep resep, String[] bahanIds, String[] jumlahDibutuhkan) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert resep
            String sqlResep = "INSERT INTO resep (nama_resep, deskripsi) VALUES (?, ?)";
            int resepId = 0;
            
            try (PreparedStatement stmtResep = conn.prepareStatement(sqlResep, Statement.RETURN_GENERATED_KEYS)) {
                stmtResep.setString(1, resep.getNamaResep());
                stmtResep.setString(2, resep.getDeskripsi());
                
                int rowsAffected = stmtResep.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = stmtResep.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            resepId = generatedKeys.getInt(1);
                        }
                    }
                }
            }
            
            // Insert detail resep
            if (resepId > 0 && bahanIds != null && jumlahDibutuhkan != null) {
                String sqlDetail = "INSERT INTO detail_resep (resep_id, bahan_id, jumlah_dibutuhkan) VALUES (?, ?, ?)";
                
                try (PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail)) {
                    for (int i = 0; i < bahanIds.length; i++) {
                        if (bahanIds[i] != null && !bahanIds[i].isEmpty() && 
                            jumlahDibutuhkan[i] != null && !jumlahDibutuhkan[i].isEmpty()) {
                            
                            stmtDetail.setInt(1, resepId);
                            stmtDetail.setInt(2, Integer.parseInt(bahanIds[i]));
                            stmtDetail.setInt(3, Integer.parseInt(jumlahDibutuhkan[i]));
                            stmtDetail.addBatch();
                        }
                    }
                    stmtDetail.executeBatch();
                }
            }
            
            conn.commit();
            return resepId;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public List<Resep> getAllResep() throws SQLException {
        List<Resep> resepList = new ArrayList<>();
        // Ubah ORDER BY ke DESC agar data terbaru di atas
        String sql = "SELECT * FROM resep ORDER BY id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Resep resep = new Resep(
                    rs.getInt("id"),
                    rs.getString("nama_resep"),
                    rs.getString("deskripsi")
                );
                resepList.add(resep);
            }
        }
        return resepList;
    }
    
    public Resep getResepById(int id) throws SQLException {
        String sql = "SELECT * FROM resep WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Resep(
                        rs.getInt("id"),
                        rs.getString("nama_resep"),
                        rs.getString("deskripsi")
                    );
                }
            }
        }
        return null;
    }
    
    public List<DetailResep> getDetailResepByResepId(int resepId) throws SQLException {
    List<DetailResep> detailList = new ArrayList<>();
    String sql = "SELECT dr.*, bb.nama_bahan, bb.satuan " +
                "FROM detail_resep dr " +
                "JOIN bahan_baku bb ON dr.bahan_id = bb.id " +
                "WHERE dr.resep_id = ? " +
                "ORDER BY bb.nama_bahan";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, resepId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DetailResep detail = new DetailResep(
                    rs.getInt("id"),
                    rs.getInt("resep_id"),
                    rs.getInt("bahan_id"),
                    rs.getInt("jumlah_dibutuhkan"),
                    rs.getString("nama_bahan"),
                    rs.getString("satuan")
                );
                detailList.add(detail);
            }
        }
    }
    return detailList;
}
    
    public boolean updateResep(Resep resep) throws SQLException {
        String sql = "UPDATE resep SET nama_resep = ?, deskripsi = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, resep.getNamaResep());
            stmt.setString(2, resep.getDeskripsi());
            stmt.setInt(3, resep.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteResep(int id) throws SQLException {
        String sql = "DELETE FROM resep WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public List<Resep> searchResep(String keyword) throws SQLException {
        List<Resep> resepList = new ArrayList<>();
        // Ubah ORDER BY ke DESC agar hasil pencarian juga menampilkan data terbaru di atas
        String sql = "SELECT * FROM resep WHERE nama_resep LIKE ? OR deskripsi LIKE ? ORDER BY id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Resep resep = new Resep(
                        rs.getInt("id"),
                        rs.getString("nama_resep"),
                        rs.getString("deskripsi")
                    );
                    resepList.add(resep);
                }
            }
        }
        return resepList;
    }
    
    public boolean deleteDetailResep(int resepId) throws SQLException {
        String sql = "DELETE FROM detail_resep WHERE resep_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, resepId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected >= 0; // Bisa jadi 0 jika tidak ada detail
        }
    }
    
    public boolean createDetailResep(DetailResep detail) throws SQLException {
        String sql = "INSERT INTO detail_resep (resep_id, bahan_id, jumlah_dibutuhkan) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detail.getResepId());
            stmt.setInt(2, detail.getBahanId());
            stmt.setInt(3, detail.getJumlahDibutuhkan());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Method untuk update resep beserta detailnya
public boolean updateResepWithDetails(Resep resep, String[] bahanIds, String[] jumlahDibutuhkan) throws SQLException {
    Connection conn = null;
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        
        // Update resep
        String sqlResep = "UPDATE resep SET nama_resep = ?, deskripsi = ? WHERE id = ?";
        try (PreparedStatement stmtResep = conn.prepareStatement(sqlResep)) {
            stmtResep.setString(1, resep.getNamaResep());
            stmtResep.setString(2, resep.getDeskripsi());
            stmtResep.setInt(3, resep.getId());
            stmtResep.executeUpdate();
        }
        
        // Hapus detail lama
        deleteDetailResep(resep.getId());
        
        // Insert detail baru
        if (bahanIds != null && jumlahDibutuhkan != null) {
            String sqlDetail = "INSERT INTO detail_resep (resep_id, bahan_id, jumlah_dibutuhkan) VALUES (?, ?, ?)";
            
            try (PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail)) {
                for (int i = 0; i < bahanIds.length; i++) {
                    if (bahanIds[i] != null && !bahanIds[i].isEmpty() && 
                        jumlahDibutuhkan[i] != null && !jumlahDibutuhkan[i].isEmpty()) {
                        
                        stmtDetail.setInt(1, resep.getId());
                        stmtDetail.setInt(2, Integer.parseInt(bahanIds[i]));
                        stmtDetail.setInt(3, Integer.parseInt(jumlahDibutuhkan[i]));
                        stmtDetail.addBatch();
                    }
                }
                stmtDetail.executeBatch();
            }
        }
        
        conn.commit();
        return true;
        
    } catch (SQLException e) {
        if (conn != null) {
            conn.rollback();
        }
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}

// Method untuk mendapatkan detail resep yang bisa diedit (untuk form edit)
public List<DetailResep> getDetailResepForEdit(int resepId) throws SQLException {
    List<DetailResep> detailList = new ArrayList<>();
    String sql = "SELECT dr.*, bb.nama_bahan " +
                "FROM detail_resep dr " +
                "JOIN bahan_baku bb ON dr.bahan_id = bb.id " +
                "WHERE dr.resep_id = ? " +
                "ORDER BY bb.nama_bahan";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, resepId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DetailResep detail = new DetailResep();
                detail.setId(rs.getInt("id"));
                detail.setResepId(rs.getInt("resep_id"));
                detail.setBahanId(rs.getInt("bahan_id"));
                detail.setJumlahDibutuhkan(rs.getInt("jumlah_dibutuhkan"));
                detail.setNamaBahan(rs.getString("nama_bahan"));
                detailList.add(detail);
            }
        }
    }
    return detailList;
}
}