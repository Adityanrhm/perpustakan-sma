package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MODIFIED TransaksiPeminjamanDAO - Enhanced untuk Multiple Items Support
 */
public class TransaksiPeminjamanDAO {
    private final DatabaseConfig dbConfig;
    private final SiswaDAO siswaDAO;
    private final BukuDAO bukuDAO;
    private final PustakawanDAO pustakawanDAO;
    private final StatusTransaksiDAO statusDAO;
    private final DetailTransaksiPeminjamanDAO detailDAO; // NEW
    
    public TransaksiPeminjamanDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.siswaDAO = new SiswaDAO();
        this.bukuDAO = new BukuDAO();
        this.pustakawanDAO = new PustakawanDAO();
        this.statusDAO = new StatusTransaksiDAO();
        this.detailDAO = new DetailTransaksiPeminjamanDAO(); // NEW
    }
    
    // EXISTING METHODS - Modified untuk new schema
    public List<TransaksiPeminjaman> findAll() throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi_peminjaman ORDER BY tanggal_pinjam DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                transaksiList.add(mapResultSetToTransaksi(rs));
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
    
    // NEW: Find transaction with detail items loaded
    public TransaksiPeminjaman findByIdWithDetails(int idTransaksi) throws SQLException {
        TransaksiPeminjaman transaksi = findById(idTransaksi);
        if (transaksi != null) {
            List<DetailTransaksiPeminjaman> details = detailDAO.findByTransaksiId(idTransaksi);
            transaksi.setDetailItems(details);
        }
        return transaksi;
    }
    
    // MODIFIED: Find by status - now considers detail items status
    public List<TransaksiPeminjaman> findByStatus(String namaStatus) throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql;
        
        if ("DIPINJAM".equals(namaStatus)) {
            // Find transactions that have unreturned items
            sql = """
                SELECT DISTINCT t.* FROM transaksi_peminjaman t
                JOIN detail_transaksi_peminjaman dt ON t.id_transaksi = dt.id_transaksi
                JOIN status_transaksi s ON dt.id_status = s.id_status
                WHERE dt.jumlah_kembali + dt.jumlah_hilang + dt.jumlah_rusak < dt.jumlah_pinjam
                ORDER BY t.tanggal_pinjam DESC
                """;
        } else {
            // Use transaction-level status
            sql = "SELECT t.* FROM transaksi_peminjaman t WHERE t.status_keseluruhan = ? ORDER BY t.tanggal_pinjam DESC";
        }
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (!"DIPINJAM".equals(namaStatus)) {
                stmt.setString(1, namaStatus);
            }
            
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
    
    // NEW: Find transactions with pending returns (has unreturned items)
    public List<TransaksiPeminjaman> findTransactionsWithPendingReturns() throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql = """
            SELECT DISTINCT t.* FROM transaksi_peminjaman t
            JOIN detail_transaksi_peminjaman dt ON t.id_transaksi = dt.id_transaksi
            WHERE dt.jumlah_kembali + dt.jumlah_hilang + dt.jumlah_rusak < dt.jumlah_pinjam
            ORDER BY t.tanggal_pinjam DESC
            """;
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TransaksiPeminjaman transaksi = mapResultSetToTransaksi(rs);
                // Load details for each transaction
                List<DetailTransaksiPeminjaman> details = detailDAO.findByTransaksiId(transaksi.getIdTransaksi());
                transaksi.setDetailItems(details);
                transaksiList.add(transaksi);
            }
        }
        return transaksiList;
    }
    
    // MODIFIED: Find overdue - now based on detail items
    public List<TransaksiPeminjaman> findOverdueBooks() throws SQLException {
        List<TransaksiPeminjaman> transaksiList = new ArrayList<>();
        String sql = """
            SELECT DISTINCT t.* FROM transaksi_peminjaman t
            JOIN detail_transaksi_peminjaman dt ON t.id_transaksi = dt.id_transaksi
            WHERE dt.jumlah_kembali + dt.jumlah_hilang + dt.jumlah_rusak < dt.jumlah_pinjam
            AND dt.tanggal_kembali_rencana < CURDATE()
            ORDER BY dt.tanggal_kembali_rencana
            """;
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TransaksiPeminjaman transaksi = mapResultSetToTransaksi(rs);
                // Load details
                List<DetailTransaksiPeminjaman> details = detailDAO.findByTransaksiId(transaksi.getIdTransaksi());
                transaksi.setDetailItems(details);
                transaksiList.add(transaksi);
            }
        }
        return transaksiList;
    }
    
    // MODIFIED: Save method - header only now
public TransaksiPeminjaman save(TransaksiPeminjaman transaksi) throws SQLException {
    try (Connection conn = dbConfig.getConnection()) {
        if (transaksi.getIdTransaksi() == 0) {
            return insert(conn, transaksi);
        } else {
            return update(conn, transaksi);
        }
    }
}

    // NEW: Save transaction with all detail items
public TransaksiPeminjaman saveWithDetails(TransaksiPeminjaman transaksi) throws SQLException {
    Connection conn = null;
    try {
        conn = dbConfig.getConnection();
        conn.setAutoCommit(false);

        // --- insert/update header pakai conn ---
        if (transaksi.getIdTransaksi() == 0) {
            transaksi = insert(conn, transaksi);
        } else {
            transaksi = update(conn, transaksi);
        }

        // --- Aggregate total per book untuk validasi ---
        Map<Integer, Integer> totalPerBook = new java.util.HashMap<>();
        for (DetailTransaksiPeminjaman detail : transaksi.getDetailItems()) {
            int idBuku = detail.getBuku().getIdBuku();
            totalPerBook.merge(idBuku, detail.getJumlahPinjam(), Integer::sum);
        }

        // Lock & validate setiap buku (tanpa update stok di sini, biar trigger yang handle)
        List<Integer> bookIds = new ArrayList<>(totalPerBook.keySet());
        Collections.sort(bookIds);

        String sqlSelectForUpdate = "SELECT jumlah_tersedia FROM buku WHERE id_buku = ? FOR UPDATE";

        for (Integer idBuku : bookIds) {
            int requested = totalPerBook.get(idBuku);

            // 1) baca stok current (lock barisnya)
            int available;
            try (PreparedStatement ps = conn.prepareStatement(sqlSelectForUpdate)) {
                ps.setInt(1, idBuku);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        available = rs.getInt("jumlah_tersedia");
                    } else {
                        conn.rollback();
                        throw new SQLException("Buku id " + idBuku + " tidak ditemukan.");
                    }
                }
            }

            // 2) validasi
            if (requested > available) {
                conn.rollback();
                throw new SQLException("Stok tidak mencukupi untuk buku id " + idBuku +
                                       " (tersedia: " + available + ", diminta: " + requested + ").");
            }
        }

        // --- Simpan semua detail pakai koneksi yang sama ---
        for (DetailTransaksiPeminjaman detail : transaksi.getDetailItems()) {
            detail.setTransaksi(transaksi);
            detailDAO.save(conn, detail); // trigger yang akan mengurangi stok
        }

        conn.commit();
        return transaksi;
    } catch (SQLException ex) {
        if (conn != null) conn.rollback();
        throw ex;
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); } catch (Exception ignore) {}
            try { conn.close(); } catch (Exception ignore) {}
        }
    }
}


    
    // MODIFIED: Insert - new schema
// Overload Insert - pakai connection dari luar
private TransaksiPeminjaman insert(Connection conn, TransaksiPeminjaman transaksi) throws SQLException {
    String sql = """
        INSERT INTO transaksi_peminjaman 
        (kode_transaksi, id_siswa, id_pustakawan, tanggal_pinjam, 
         total_buku, total_denda, status_keseluruhan, catatan) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, transaksi.getKodeTransaksi());
        stmt.setInt(2, transaksi.getSiswa().getIdSiswa());
        stmt.setInt(3, transaksi.getPustakawan().getIdPustakawan());
        stmt.setDate(4, Date.valueOf(transaksi.getTanggalPinjam()));
        stmt.setInt(5, transaksi.getTotalBuku());
        stmt.setBigDecimal(6, transaksi.getTotalDenda() != null ? transaksi.getTotalDenda() : BigDecimal.ZERO);
        stmt.setString(7, transaksi.getStatusKeseluruhan() != null ? transaksi.getStatusKeseluruhan() : "AKTIF");
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

// Overload Update - pakai connection dari luar
private TransaksiPeminjaman update(Connection conn, TransaksiPeminjaman transaksi) throws SQLException {
    String sql = """
        UPDATE transaksi_peminjaman SET 
        total_buku = ?, total_denda = ?, status_keseluruhan = ?, catatan = ?
        WHERE id_transaksi = ?
        """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, transaksi.getTotalBuku());
        stmt.setBigDecimal(2, transaksi.getTotalDenda() != null ? transaksi.getTotalDenda() : BigDecimal.ZERO);
        stmt.setString(3, transaksi.getStatusKeseluruhan() != null ? transaksi.getStatusKeseluruhan() : "AKTIF");
        stmt.setString(4, transaksi.getCatatan());
        stmt.setInt(5, transaksi.getIdTransaksi());

        stmt.executeUpdate();
        return transaksi;
    }
}

    
    public String generateKodeTransaksi() throws SQLException {
        String sql = """
            SELECT CONCAT('TXN', YEAR(NOW()), LPAD(MONTH(NOW()), 2, '0'), 
            LPAD(COALESCE(MAX(SUBSTRING(kode_transaksi, 8)) + 1, 1), 4, '0')) 
            FROM transaksi_peminjaman WHERE kode_transaksi LIKE CONCAT('TXN', YEAR(NOW()), LPAD(MONTH(NOW()), 2, '0'), '%')
            """;
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        
        // Fallback
        return "TXN" + java.time.LocalDate.now().getYear() + 
               String.format("%02d", java.time.LocalDate.now().getMonthValue()) + "0001";
    }
    
    // MODIFIED: Count active loans - now counts individual items
    public int countActiveLoansBySiswa(int idSiswa) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(dt.jumlah_pinjam - dt.jumlah_kembali - dt.jumlah_hilang - dt.jumlah_rusak), 0) 
            FROM transaksi_peminjaman t
            JOIN detail_transaksi_peminjaman dt ON t.id_transaksi = dt.id_transaksi
            WHERE t.id_siswa = ? 
            AND dt.jumlah_kembali + dt.jumlah_hilang + dt.jumlah_rusak < dt.jumlah_pinjam
            """;
        
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
    
    // NEW: Get transaction statistics
    public TransactionStatistics getTransactionStatistics() throws SQLException {
        String sql = """
            SELECT 
                COUNT(DISTINCT t.id_transaksi) as total_transaksi,
                SUM(t.total_buku) as total_items_pinjam,
                SUM(CASE WHEN t.status_keseluruhan = 'AKTIF' THEN 1 ELSE 0 END) as transaksi_aktif,
                SUM(CASE WHEN t.status_keseluruhan = 'SELESAI' THEN 1 ELSE 0 END) as transaksi_selesai,
                COALESCE(SUM(t.total_denda), 0) as total_denda_terkumpul,
                COUNT(DISTINCT CASE WHEN dt.tanggal_kembali_rencana < CURDATE() 
                                   AND dt.jumlah_kembali + dt.jumlah_hilang + dt.jumlah_rusak < dt.jumlah_pinjam 
                                   THEN dt.id_detail END) as items_terlambat
            FROM transaksi_peminjaman t
            LEFT JOIN detail_transaksi_peminjaman dt ON t.id_transaksi = dt.id_transaksi
            """;
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return new TransactionStatistics(
                    rs.getInt("total_transaksi"),
                    rs.getInt("total_items_pinjam"),
                    rs.getInt("transaksi_aktif"),
                    rs.getInt("transaksi_selesai"),
                    rs.getBigDecimal("total_denda_terkumpul"),
                    rs.getInt("items_terlambat")
                );
            }
        }
        return new TransactionStatistics(0, 0, 0, 0, BigDecimal.ZERO, 0);
    }
    
    public void delete(int idTransaksi) throws SQLException {
        String sql = "DELETE FROM transaksi_peminjaman WHERE id_transaksi = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTransaksi);
            stmt.executeUpdate();
        }
    }
    
    // MODIFIED: Mapping method - new fields
    private TransaksiPeminjaman mapResultSetToTransaksi(ResultSet rs) throws SQLException {
        TransaksiPeminjaman transaksi = new TransaksiPeminjaman();
        transaksi.setIdTransaksi(rs.getInt("id_transaksi"));
        transaksi.setKodeTransaksi(rs.getString("kode_transaksi"));
        
        // Set date
        Date tanggalPinjam = rs.getDate("tanggal_pinjam");
        if (tanggalPinjam != null) {
            transaksi.setTanggalPinjam(tanggalPinjam.toLocalDate());
        }
        
        // NEW FIELDS
        transaksi.setTotalBuku(rs.getInt("total_buku"));
        transaksi.setTotalDenda(rs.getBigDecimal("total_denda"));
        transaksi.setStatusKeseluruhan(rs.getString("status_keseluruhan"));
        transaksi.setCatatan(rs.getString("catatan"));
        
        // Load related entities
        try {
            transaksi.setSiswa(siswaDAO.findById(rs.getInt("id_siswa")));
            transaksi.setPustakawan(pustakawanDAO.findById(rs.getInt("id_pustakawan")));
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
}

/**
 * Helper class untuk statistik transaksi
 */
class TransactionStatistics {
    private int totalTransaksi;
    private int totalItemsPinjam;
    private int transaksiAktif;
    private int transaksiSelesai;
    private BigDecimal totalDendaTerkumpul;
    private int itemsTerlambat;
    
    public TransactionStatistics(int totalTransaksi, int totalItemsPinjam, int transaksiAktif, 
                               int transaksiSelesai, BigDecimal totalDendaTerkumpul, int itemsTerlambat) {
        this.totalTransaksi = totalTransaksi;
        this.totalItemsPinjam = totalItemsPinjam;
        this.transaksiAktif = transaksiAktif;
        this.transaksiSelesai = transaksiSelesai;
        this.totalDendaTerkumpul = totalDendaTerkumpul;
        this.itemsTerlambat = itemsTerlambat;
    }
    
    // Getters
    public int getTotalTransaksi() { return totalTransaksi; }
    public int getTotalItemsPinjam() { return totalItemsPinjam; }
    public int getTransaksiAktif() { return transaksiAktif; }
    public int getTransaksiSelesai() { return transaksiSelesai; }
    public BigDecimal getTotalDendaTerkumpul() { return totalDendaTerkumpul; }
    public int getItemsTerlambat() { return itemsTerlambat; }
    
    public int getTotalBukuBelumKembali() {
        // This would need separate query, simplified for now
        return transaksiAktif * 2; // Rough estimate
    }
}