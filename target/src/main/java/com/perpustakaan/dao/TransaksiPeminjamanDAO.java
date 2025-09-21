package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransaksiPeminjamanDAO {
    private final DatabaseConfig dbConfig;
    private final SiswaDAO siswaDAO;
    private final BukuDAO bukuDAO;
    private final PustakawanDAO pustakawanDAO;
    private final StatusTransaksiDAO statusDAO;
    
    public TransaksiPeminjamanDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.siswaDAO = new SiswaDAO();
        this.bukuDAO = new BukuDAO();
        this.pustakawanDAO = new PustakawanDAO();
        this.statusDAO = new StatusTransaksiDAO();
    }
    
    public List<TransaksiPeminjaman> findAll() throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM v_transaksi_lengkap ORDER BY tanggal_pinjam DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                transaksiList.add(mapViewResultSetToTransaksi(rs));
            }
        }
        return transaksiList;
    }
    
    public TransaksiPeminjaman findById(int idTransaksi) throws SQLException {
        String sql = "SELECT * FROM transaksi_peminjaman WHERE id_transaksi = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTransaksi);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaksi(rs);
                }
            }
        }
        return null;
    }
    
    public List<TransaksiPeminjaman> findByStatus(String namaStatus) throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql = "SELECT t.* FROM transaksi_peminjaman t " +
                    "JOIN status_transaksi s ON t.id_status = s.id_status " +
                    "WHERE s.nama_status = ? ORDER BY t.tanggal_pinjam DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, namaStatus);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transaksiList.add(mapResultSetToTransaksi(rs));
                }
            }
        }
        return transaksiList;
    }
    
    public List<TransaksiPeminjaman> findBySiswa(int idSiswa) throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi_peminjaman WHERE id_siswa = ? ORDER BY tanggal_pinjam DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSiswa);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transaksiList.add(mapResultSetToTransaksi(rs));
                }
            }
        }
        return transaksiList;
    }
    
    public List<TransaksiPeminjaman> findOverdueBooks() throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi_peminjaman WHERE tanggal_kembali_aktual IS NULL " +
                    "AND tanggal_kembali_rencana < CURDATE() ORDER BY tanggal_kembali_rencana";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                transaksiList.add(mapResultSetToTransaksi(rs));
            }
        }
        return transaksiList;
    }
    
    public TransaksiPeminjaman save(TransaksiPeminjaman transaksi) throws SQLException {
        if (transaksi.getIdTransaksi() == 0) {
            return insert(transaksi);
        } else {
            return update(transaksi);
        }
    }
    
    private TransaksiPeminjaman insert(TransaksiPeminjaman transaksi) throws SQLException {
        String sql = "INSERT INTO transaksi_peminjaman (kode_transaksi, id_siswa, id_buku, " +
                    "id_pustakawan, tanggal_pinjam, tanggal_kembali_rencana, id_status, catatan) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, transaksi.getKodeTransaksi());
            stmt.setInt(2, transaksi.getSiswa().getIdSiswa());
            stmt.setInt(3, transaksi.getBuku().getIdBuku());
            stmt.setInt(4, transaksi.getPustakawan().getIdPustakawan());
            stmt.setDate(5, Date.valueOf(transaksi.getTanggalPinjam()));
            stmt.setDate(6, Date.valueOf(transaksi.getTanggalKembaliRencana()));
            stmt.setInt(7, transaksi.getStatus().getIdStatus());
            stmt.setString(8, transaksi.getCatatan());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaksi.setIdTransaksi(generatedKeys.getInt(1));
                        return transaksi;
                    }
                }
            }
        }
        throw new SQLException("Creating transaksi failed, no ID obtained.");
    }
    
    private TransaksiPeminjaman update(TransaksiPeminjaman transaksi) throws SQLException {
        String sql = "UPDATE transaksi_peminjaman SET tanggal_kembali_aktual = ?, " +
                    "id_status = ?, denda = ?, catatan = ? WHERE id_transaksi = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, transaksi.getTanggalKembaliAktual() != null ? 
                        Date.valueOf(transaksi.getTanggalKembaliAktual()) : null);
            stmt.setInt(2, transaksi.getStatus().getIdStatus());
            stmt.setBigDecimal(3, transaksi.getDenda());
            stmt.setString(4, transaksi.getCatatan());
            stmt.setInt(5, transaksi.getIdTransaksi());
            
            stmt.executeUpdate();
            return transaksi;
        }
    }
    
    public String generateKodeTransaksi() throws SQLException {
        String sql = "SELECT CONCAT('TXN', YEAR(NOW()), MONTH(NOW()), " +
                    "LPAD(COALESCE(MAX(SUBSTRING(kode_transaksi, 8)) + 1, 1), 4, '0')) " +
                    "FROM transaksi_peminjaman WHERE kode_transaksi LIKE CONCAT('TXN', YEAR(NOW()), MONTH(NOW()), '%')";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        
        // Fallback if no records found
        return "TXN" + java.time.LocalDate.now().getYear() + 
               String.format("%02d", java.time.LocalDate.now().getMonthValue()) + "0001";
    }
    
    public int countActiveLoansBySiswa(int idSiswa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transaksi_peminjaman t " +
                    "JOIN status_transaksi s ON t.id_status = s.id_status " +
                    "WHERE t.id_siswa = ? AND s.nama_status = 'DIPINJAM'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSiswa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    private TransaksiPeminjaman mapResultSetToTransaksi(ResultSet rs) throws SQLException {
        TransaksiPeminjaman transaksi = new TransaksiPeminjaman();
        transaksi.setIdTransaksi(rs.getInt("id_transaksi"));
        transaksi.setKodeTransaksi(rs.getString("kode_transaksi"));
        
        // Set tanggal
        Date tanggalPinjam = rs.getDate("tanggal_pinjam");
        if (tanggalPinjam != null) {
            transaksi.setTanggalPinjam(tanggalPinjam.toLocalDate());
        }
        
        Date tanggalKembaliRencana = rs.getDate("tanggal_kembali_rencana");
        if (tanggalKembaliRencana != null) {
            transaksi.setTanggalKembaliRencana(tanggalKembaliRencana.toLocalDate());
        }
        
        Date tanggalKembaliAktual = rs.getDate("tanggal_kembali_aktual");
        if (tanggalKembaliAktual != null) {
            transaksi.setTanggalKembaliAktual(tanggalKembaliAktual.toLocalDate());
        }
        
        transaksi.setDenda(rs.getBigDecimal("denda"));
        transaksi.setCatatan(rs.getString("catatan"));
        
        // Load related entities
        try {
            transaksi.setSiswa(siswaDAO.findById(rs.getInt("id_siswa")));
            transaksi.setBuku(bukuDAO.findById(rs.getInt("id_buku")));
            transaksi.setPustakawan(pustakawanDAO.findById(rs.getInt("id_pustakawan")));
            transaksi.setStatus(statusDAO.findById(rs.getInt("id_status")));
        } catch (SQLException e) {
            System.err.println("Error loading related entities: " + e.getMessage());
        }
        
        // Set timestamps
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            transaksi.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            transaksi.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return transaksi;
    }
    
    private TransaksiPeminjaman mapViewResultSetToTransaksi(ResultSet rs) throws SQLException {
        // Simplified mapping for view results
        TransaksiPeminjaman transaksi = new TransaksiPeminjaman();
        transaksi.setIdTransaksi(rs.getInt("id_transaksi"));
        transaksi.setKodeTransaksi(rs.getString("kode_transaksi"));
        
        Date tanggalPinjam = rs.getDate("tanggal_pinjam");
        if (tanggalPinjam != null) {
            transaksi.setTanggalPinjam(tanggalPinjam.toLocalDate());
        }
        
        Date tanggalKembaliRencana = rs.getDate("tanggal_kembali_rencana");
        if (tanggalKembaliRencana != null) {
            transaksi.setTanggalKembaliRencana(tanggalKembaliRencana.toLocalDate());
        }
        
        Date tanggalKembaliAktual = rs.getDate("tanggal_kembali_aktual");
        if (tanggalKembaliAktual != null) {
            transaksi.setTanggalKembaliAktual(tanggalKembaliAktual.toLocalDate());
        }
        
        transaksi.setDenda(rs.getBigDecimal("denda"));
        transaksi.setCatatan(rs.getString("catatan"));
        
        return transaksi;
    }
}