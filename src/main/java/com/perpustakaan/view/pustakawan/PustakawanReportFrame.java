package com.perpustakaan.view.pustakawan;

import com.perpustakaan.dao.BukuDAO;
import com.perpustakaan.dao.SiswaDAO;
import com.perpustakaan.dao.TransaksiPeminjamanDAO;
import com.perpustakaan.model.Buku;
import com.perpustakaan.model.Siswa;
import com.perpustakaan.model.TransaksiPeminjaman;
import com.perpustakaan.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Frame laporan sederhana untuk pustakawan
 * Menampilkan laporan berdasarkan tipe yang dipilih
 */
public class PustakawanReportFrame extends JFrame {
    private String reportType;
    private BukuDAO bukuDAO;
    private SiswaDAO siswaDAO;
    private TransaksiPeminjamanDAO transaksiDAO;
    
    // Components
    private JTable tableReport;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JButton btnRefresh, btnExport, btnClose;
    
    public PustakawanReportFrame(String reportType) {
        this.reportType = reportType;
        
        bukuDAO = new BukuDAO();
        siswaDAO = new SiswaDAO();
        transaksiDAO = new TransaksiPeminjamanDAO();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadReportData();
        
        setTitle("Laporan " + getReportTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }
    
    private String getReportTitle() {
        switch (reportType) {
            case "BUKU": return "Data Buku";
            case "SISWA": return "Data Siswa";
            case "PEMINJAMAN": return "Transaksi Peminjaman";
            default: return "Laporan";
        }
    }
    
    private void initComponents() {
        // Initialize table based on report type
        String[] columns = getTableColumns();
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReport = new JTable(tableModel);
        scrollPane = new JScrollPane(tableReport);
        
        // Buttons
        btnRefresh = new JButton("Refresh");
        btnExport = new JButton("Export CSV");
        btnClose = new JButton("Tutup");
        
        // Button styling
        btnRefresh.setBackground(new Color(23, 162, 184));
        btnRefresh.setForeground(Color.WHITE);
        btnExport.setBackground(new Color(40, 167, 69));
        btnExport.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
    }
    
    private String[] getTableColumns() {
        switch (reportType) {
            case "BUKU":
                return new String[]{"Kode Buku", "Judul", "Pengarang", "Kategori", "Total", "Tersedia", "Status"};
            case "SISWA":
                return new String[]{"NIS", "Nama Lengkap", "Kelas", "Jenis Kelamin", "Status", "Pinjaman Aktif"};
            case "PEMINJAMAN":
                return new String[]{"Kode Transaksi", "NIS", "Nama Siswa", "Judul Buku", "Tgl Pinjam", "Tgl Kembali", "Status"};
            default:
                return new String[]{"Data"};
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitle = new JLabel("Laporan " + getReportTitle());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblTitle);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(btnRefresh);
        topPanel.add(btnExport);
        topPanel.add(btnClose);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblStatus = new JLabel("Tanggal: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        btnRefresh.addActionListener(e -> loadReportData());
        btnExport.addActionListener(e -> exportReport());
        btnClose.addActionListener(e -> dispose());
    }
    
    private void loadReportData() {
        try {
            switch (reportType) {
                case "BUKU":
                    loadBukuReport();
                    break;
                case "SISWA":
                    loadSiswaReport();
                    break;
                case "PEMINJAMAN":
                    loadPeminjamanReport();
                    break;
                default:
                    UIHelper.showWarningMessage(this, "Tipe laporan tidak dikenal");
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading report data: " + e.getMessage());
        }
    }
    
    private void loadBukuReport() throws SQLException {
        List<Buku> bukuList = bukuDAO.findAll();
        tableModel.setRowCount(0);
        
        for (Buku buku : bukuList) {
            String status;
            if (buku.getJumlahTersedia() > 0) {
                status = "Tersedia";
            } else if (buku.getJumlahTotal() > 0) {
                status = "Dipinjam Semua";
            } else {
                status = "Habis";
            }
            
            Object[] row = {
                buku.getKodeBuku(),
                buku.getJudul(),
                buku.getPengarang(),
                buku.getKategori() != null ? buku.getKategori().getNamaKategori() : "",
                buku.getJumlahTotal(),
                buku.getJumlahTersedia(),
                status
            };
            tableModel.addRow(row);
        }
        
        UIHelper.showSuccessMessage(this, "Laporan buku berhasil dimuat. Total: " + bukuList.size() + " buku");
    }
    
    private void loadSiswaReport() throws SQLException {
        List<Siswa> siswaList = siswaDAO.findAll();
        tableModel.setRowCount(0);
        
        for (Siswa siswa : siswaList) {
            try {
                int activeLoanCount = transaksiDAO.countActiveLoansBySiswa(siswa.getIdSiswa());
                
                Object[] row = {
                    siswa.getNis(),
                    siswa.getNamaLengkap(),
                    siswa.getKelas() != null ? siswa.getKelas().getNamaKelas() : "",
                    "L".equals(siswa.getJenisKelamin()) ? "Laki-laki" : "Perempuan",
                    siswa.isStatusAktif() ? "Aktif" : "Tidak Aktif",
                    activeLoanCount + " buku"
                };
                tableModel.addRow(row);
                
            } catch (SQLException e) {
                Object[] row = {
                    siswa.getNis(),
                    siswa.getNamaLengkap(),
                    siswa.getKelas() != null ? siswa.getKelas().getNamaKelas() : "",
                    "L".equals(siswa.getJenisKelamin()) ? "Laki-laki" : "Perempuan",
                    siswa.isStatusAktif() ? "Aktif" : "Tidak Aktif",
                    "Error"
                };
                tableModel.addRow(row);
            }
        }
        
        UIHelper.showSuccessMessage(this, "Laporan siswa berhasil dimuat. Total: " + siswaList.size() + " siswa");
    }
    
    private void loadPeminjamanReport() throws SQLException {
        List<TransaksiPeminjaman> transaksiList = transaksiDAO.findAll();
        tableModel.setRowCount(0);
        
        for (TransaksiPeminjaman transaksi : transaksiList) {
            Object[] row = {
                transaksi.getKodeTransaksi(),
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNis() : "",
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNamaLengkap() : "",
                transaksi.getBuku() != null ? transaksi.getBuku().getJudul() : "",
                transaksi.getTanggalPinjam() != null ? transaksi.getTanggalPinjam().toString() : "",
                transaksi.getTanggalKembaliRencana() != null ? transaksi.getTanggalKembaliRencana().toString() : "",
                transaksi.getStatus() != null ? transaksi.getStatus().getNamaStatus() : ""
            };
            tableModel.addRow(row);
        }
        
        UIHelper.showSuccessMessage(this, "Laporan peminjaman berhasil dimuat. Total: " + transaksiList.size() + " transaksi");
    }
    
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Laporan");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        String defaultFileName = "laporan_" + reportType.toLowerCase() + "_" + 
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write header
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
                
                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < tableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                UIHelper.showSuccessMessage(this, 
                    "Laporan berhasil diekspor ke: " + file.getAbsolutePath());
                    
            } catch (IOException e) {
                UIHelper.showErrorMessage(this, "Error exporting file: " + e.getMessage());
            }
        }
    }
}