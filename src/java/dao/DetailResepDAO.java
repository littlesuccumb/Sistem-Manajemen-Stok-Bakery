package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import config.DBConnection;
import model.DetailResep;

public class DetailResepDAO {
    
    public List<DetailResep> getDetailByResepId(int resepId) throws SQLException {
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
    
    public boolean deleteDetailByResepId(int resepId) throws SQLException {
        String sql = "DELETE FROM detail_resep WHERE resep_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, resepId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected >= 0;
        }
    }
    
    public boolean updateDetailResep(DetailResep detail) throws SQLException {
        String sql = "UPDATE detail_resep SET bahan_id = ?, jumlah_dibutuhkan = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detail.getBahanId());
            stmt.setInt(2, detail.getJumlahDibutuhkan());
            stmt.setInt(3, detail.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public DetailResep getDetailById(int id) throws SQLException {
        String sql = "SELECT dr.*, bb.nama_bahan, bb.satuan " +
                    "FROM detail_resep dr " +
                    "JOIN bahan_baku bb ON dr.bahan_id = bb.id " +
                    "WHERE dr.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new DetailResep(
                        rs.getInt("id"),
                        rs.getInt("resep_id"),
                        rs.getInt("bahan_id"),
                        rs.getInt("jumlah_dibutuhkan"),
                        rs.getString("nama_bahan"),
                        rs.getString("satuan")
                    );
                }
            }
        }
        return null;
    }
    
    public boolean deleteDetailById(int id) throws SQLException {
        String sql = "DELETE FROM detail_resep WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public List<DetailResep> getAllDetailResep() throws SQLException {
        List<DetailResep> detailList = new ArrayList<>();
        String sql = "SELECT dr.*, bb.nama_bahan, bb.satuan " +
                    "FROM detail_resep dr " +
                    "JOIN bahan_baku bb ON dr.bahan_id = bb.id " +
                    "ORDER BY dr.resep_id, bb.nama_bahan";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
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
        return detailList;
    }
}