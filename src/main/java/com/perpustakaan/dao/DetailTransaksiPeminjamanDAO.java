
package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk Detail Transaksi Peminjaman - NEW
 */
public class DetailTransaksiPeminjamanDAO {
    private final DatabaseConfig dbConfig;
    private final BukuDAO bukuDAO;
    private final StatusTransaksiDAO statusDAO;
    
    public DetailTransaksiPeminjamanDAO() {
        this.dbConfig = DatabaseConfig.getInstance();   
        this.bukuDAO = new BukuDAO();
        this.statusDAO = new StatusTransaksiDAO();
    }
    
    public List<DetailTransaksiPeminjaman> findByTransaksiId(int idTransaksi) throws SQLException {
        List<DetailTransaksiPeminjaman> detailList = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi_peminjaman WHERE id_transaksi = ? ORDER BY id_detail";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTransaksi); // <-- INI YANG KURANG

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detailList.add(mapResultSetToDetail(rs));
                }
            }
        }
        return detailList;
    }

    
    public DetailTransaksiPeminjaman findById(int idDetail) throws SQLException {
        String sql = "SELECT * FROM detail_transaksi_peminjaman WHERE id_detail = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDetail);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDetail(rs);
                }
            }
        }
        return null;
    }
    
// 1️⃣ Versi dengan Connection (dipakai ketika kita sudah pegang transaksi manual)
public DetailTransaksiPeminjaman save(Connection conn, DetailTransaksiPeminjaman detail) throws SQLException {
    if (detail.getIdDetail() == 0) {
        return insert(conn, detail);
    } else {
        return update(conn, detail);
    }
}

// 2️⃣ Versi tanpa Connection (auto create & close connection sendiri)
public DetailTransaksiPeminjaman save(DetailTransaksiPeminjaman detail) throws SQLException {
    try (Connection conn = dbConfig.getConnection()) {
        return save(conn, detail);
    }
}



private DetailTransaksiPeminjaman insert(Connection conn, DetailTransaksiPeminjaman detail) throws SQLException {
    String sql = """
        INSERT INTO detail_transaksi_peminjaman 
        (id_transaksi, id_buku, jumlah_pinjam, tanggal_kembali_rencana, 
         tanggal_kembali_aktual, jumlah_kembali, jumlah_hilang, jumlah_rusak, 
         id_status, denda_per_item, total_denda_item, catatan_detail)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, detail.getTransaksi().getIdTransaksi());
        stmt.setInt(2, detail.getBuku().getIdBuku());
        stmt.setInt(3, detail.getJumlahPinjam());
        stmt.setDate(4, Date.valueOf(detail.getTanggalKembaliRencana()));
        stmt.setDate(5, detail.getTanggalKembaliAktual() != null ? 
                     Date.valueOf(detail.getTanggalKembaliAktual()) : null);
        stmt.setInt(6, detail.getJumlahKembali());
        stmt.setInt(7, detail.getJumlahHilang());
        stmt.setInt(8, detail.getJumlahRusak());
        stmt.setInt(9, detail.getStatus().getIdStatus());
        stmt.setBigDecimal(10, detail.getDendaPerItem());
        stmt.setBigDecimal(11, detail.getTotalDendaItem());
        stmt.setString(12, detail.getCatatanDetail());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detail.setIdDetail(generatedKeys.getInt(1));
                    return detail;
                }
            }
        }
    }
    throw new SQLException("Creating detail failed, no ID obtained.");
}

private DetailTransaksiPeminjaman update(Connection conn, DetailTransaksiPeminjaman detail) throws SQLException {
    // 1️⃣ Ambil jumlah pinjam dari database untuk validasi
    String sqlCheck = "SELECT jumlah_pinjam FROM detail_transaksi_peminjaman WHERE id_detail = ?";
    int jumlahPinjam = 0;
    try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
        checkStmt.setInt(1, detail.getIdDetail());
        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next()) {
                jumlahPinjam = rs.getInt("jumlah_pinjam");
            } else {
                throw new SQLException("Detail transaksi tidak ditemukan untuk ID: " + detail.getIdDetail());
            }
        }
    }

    // 2️⃣ Hitung total pengembalian
    int totalKembali = detail.getJumlahKembali() 
                     + detail.getJumlahHilang() 
                     + detail.getJumlahRusak();

    // 3️⃣ Validasi
    if (totalKembali > jumlahPinjam) {
        throw new SQLException(
            "Pengembalian tidak valid! " +
            "Jumlah dikembalikan (" + totalKembali + 
            ") melebihi jumlah pinjam (" + jumlahPinjam + ")."
        );
    }

    // 4️⃣ Jika valid, lakukan update
    String sql = """
        UPDATE detail_transaksi_peminjaman SET
        tanggal_kembali_rencana = ?, tanggal_kembali_aktual = ?,
        jumlah_kembali = ?, jumlah_hilang = ?, jumlah_rusak = ?,
        id_status = ?, denda_per_item = ?, total_denda_item = ?,
        catatan_detail = ?
        WHERE id_detail = ?
        """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setDate(1, Date.valueOf(detail.getTanggalKembaliRencana()));
        stmt.setDate(2, detail.getTanggalKembaliAktual() != null ? 
                     Date.valueOf(detail.getTanggalKembaliAktual()) : null);
        stmt.setInt(3, detail.getJumlahKembali());
        stmt.setInt(4, detail.getJumlahHilang());
        stmt.setInt(5, detail.getJumlahRusak());
        stmt.setInt(6, detail.getStatus().getIdStatus());
        stmt.setBigDecimal(7, detail.getDendaPerItem());
        stmt.setBigDecimal(8, detail.getTotalDendaItem());
        stmt.setString(9, detail.getCatatanDetail());
        stmt.setInt(10, detail.getIdDetail());

        stmt.executeUpdate();
        return detail;
    }
}


    
    public void delete(int idDetail) throws SQLException {
        String sql = "DELETE FROM detail_transaksi_peminjaman WHERE id_detail = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDetail);
            stmt.executeUpdate();
        }
    }
    
    public void extendDueDate(int idDetail, LocalDate newDueDate, int pustakawanId, String reason) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Get current due date
            DetailTransaksiPeminjaman detail = findById(idDetail);
            if (detail == null) {
                throw new SQLException("Detail not found");
            }
            
            LocalDate oldDueDate = detail.getTanggalKembaliRencana();
            
            // Update due date in detail
            String updateDetailSql = "UPDATE detail_transaksi_peminjaman SET tanggal_kembali_rencana = ? WHERE id_detail = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateDetailSql)) {
                stmt.setDate(1, Date.valueOf(newDueDate));
                stmt.setInt(2, idDetail);
                stmt.executeUpdate();
            }
            
            // Insert perpanjangan record
            String insertPerpanjanganSql = """
                INSERT INTO perpanjangan_peminjaman 
                (id_detail, tanggal_perpanjangan, tanggal_kembali_lama, tanggal_kembali_baru, id_pustakawan, alasan)
                VALUES (?, CURDATE(), ?, ?, ?, ?)
                """;
            try (PreparedStatement stmt = conn.prepareStatement(insertPerpanjanganSql)) {
                stmt.setInt(1, idDetail);
                stmt.setDate(2, Date.valueOf(oldDueDate));
                stmt.setDate(3, Date.valueOf(newDueDate));
                stmt.setInt(4, pustakawanId);
                stmt.setString(5, reason);
                stmt.executeUpdate();
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private DetailTransaksiPeminjaman mapResultSetToDetail(ResultSet rs) throws SQLException {
        DetailTransaksiPeminjaman detail = new DetailTransaksiPeminjaman();
        detail.setIdDetail(rs.getInt("id_detail"));
        detail.setJumlahPinjam(rs.getInt("jumlah_pinjam"));
        detail.setJumlahKembali(rs.getInt("jumlah_kembali"));
        detail.setJumlahHilang(rs.getInt("jumlah_hilang"));
        detail.setJumlahRusak(rs.getInt("jumlah_rusak"));
        detail.setDendaPerItem(rs.getBigDecimal("denda_per_item"));
        detail.setTotalDendaItem(rs.getBigDecimal("total_denda_item"));
        detail.setCatatanDetail(rs.getString("catatan_detail"));
        
        // Set dates
        Date tanggalKembaliRencana = rs.getDate("tanggal_kembali_rencana");
        if (tanggalKembaliRencana != null) {
            detail.setTanggalKembaliRencana(tanggalKembaliRencana.toLocalDate());
        }
        
        Date tanggalKembaliAktual = rs.getDate("tanggal_kembali_aktual");
        if (tanggalKembaliAktual != null) {
            detail.setTanggalKembaliAktual(tanggalKembaliAktual.toLocalDate());
        }
        
        // Load related entities
        try {
            detail.setBuku(bukuDAO.findById(rs.getInt("id_buku")));
            detail.setStatus(statusDAO.findById(rs.getInt("id_status")));
        } catch (SQLException e) {
            System.err.println("Error loading related entities: " + e.getMessage());
        }
        
        // Set timestamps
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            detail.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            detail.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return detail;
    }
}