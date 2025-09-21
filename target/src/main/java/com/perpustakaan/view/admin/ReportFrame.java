package com.perpustakaan.view.admin;

import com.perpustakaan.dao.BukuDAO;
import com.perpustakaan.dao.SiswaDAO;
import com.perpustakaan.dao.PustakawanDAO;
import com.perpustakaan.dao.TransaksiPeminjamanDAO;
import com.perpustakaan.model.Buku;
import com.perpustakaan.model.Siswa;
import com.perpustakaan.model.Pustakawan;
import com.perpustakaan.model.TransaksiPeminjaman;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Frame untuk menampilkan berbagai laporan
 */
public class ReportFrame extends JFrame {
    private BukuDAO bukuDAO;
    private SiswaDAO siswaDAO;
    private PustakawanDAO pustakawanDAO; // ADD pustakawan DAO
    private TransaksiPeminjamanDAO transaksiDAO;
    
    // Components
    private JTabbedPane tabbedPane;
    private JTable tableBuku, tablePustakawan, tableSiswa, tableTransaksi;
    private DefaultTableModel modelBuku, modelPustakawan, modelSiswa, modelTransaksi;
    private JButton btnExportBuku, btnExportPustakawan, btnExportSiswa, btnExportTransaksi;
    private JButton btnRefreshBuku, btnRefreshPustakawan, btnRefreshSiswa, btnRefreshTransaksi;

    
    // Filter components for transaction report
    private JTextField txtTanggalMulai, txtTanggalAkhir;
    private JComboBox<String> cbStatusTransaksi;
    private JButton btnFilterTransaksi;
    
    public ReportFrame() {
        bukuDAO = new BukuDAO();
        siswaDAO = new SiswaDAO();
        pustakawanDAO = new PustakawanDAO(); // ADD pustakawan DAO
        transaksiDAO = new TransaksiPeminjamanDAO();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadAllReports();
        
        setTitle("Laporan Perpustakaan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Initialize tables and models
        initBukuReport();
        initSiswaReport();
        initPustakawanReport(); // ADD pustakawan report
        initTransaksiReport();
    }
    
    private void initBukuReport() {
        String[] columnsBuku = {"Kode Buku", "Judul", "Pengarang", "Penerbit", "Tahun", 
                               "Kategori", "Rak", "Lokasi Rak", "Total", "Tersedia", "Dipinjam"};
        modelBuku = new DefaultTableModel(columnsBuku, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBuku = new JTable(modelBuku);
        tableBuku.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableBuku.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableBuku.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableBuku.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        btnExportBuku = new JButton("Export CSV");
        btnRefreshBuku = new JButton("Refresh");
        
        btnExportBuku.setBackground(new Color(40, 167, 69));
        btnExportBuku.setForeground(Color.WHITE);
        btnRefreshBuku.setBackground(new Color(23, 162, 184));
        btnRefreshBuku.setForeground(Color.WHITE);
    }
    
    private void initSiswaReport() {
        // REVISED - hanya field yang diinput
        String[] columnsSiswa = {"NIS", "Nama Lengkap", "Kelas", "Jenis Kelamin", "Username", "Status"};
        modelSiswa = new DefaultTableModel(columnsSiswa, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSiswa = new JTable(modelSiswa);
        tableSiswa.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableSiswa.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableSiswa.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableSiswa.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        btnExportSiswa = new JButton("Export CSV");
        btnRefreshSiswa = new JButton("Refresh");
        
        btnExportSiswa.setBackground(new Color(40, 167, 69));
        btnExportSiswa.setForeground(Color.WHITE);
        btnRefreshSiswa.setBackground(new Color(23, 162, 184));
        btnRefreshSiswa.setForeground(Color.WHITE);
    }
    
    private void initPustakawanReport() {
        // REVISED - hanya field yang diinput  
        String[] columnsPustakawan = {"NIP", "Nama Lengkap", "Email", "No. Telepon", "Username", "Status"};
        modelPustakawan = new DefaultTableModel(columnsPustakawan, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePustakawan = new JTable(modelPustakawan);
        tablePustakawan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablePustakawan.getColumnModel().getColumn(0).setPreferredWidth(120);
        tablePustakawan.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        btnExportPustakawan = new JButton("Export CSV");
        btnRefreshPustakawan = new JButton("Refresh");
        
        btnExportPustakawan.setBackground(new Color(40, 167, 69));
        btnExportPustakawan.setForeground(Color.WHITE);
        btnRefreshPustakawan.setBackground(new Color(23, 162, 184));
        btnRefreshPustakawan.setForeground(Color.WHITE);
    }
    
    private void initTransaksiReport() {
        String[] columnsTransaksi = {"Kode Transaksi", "NIS Siswa", "Nama Siswa", 
                                   "Kode Buku", "Judul Buku", "Tanggal Pinjam", 
                                   "Tanggal Kembali", "Tanggal Kembali Aktual", 
                                   "Status", "Denda", "Pustakawan"};
        modelTransaksi = new DefaultTableModel(columnsTransaksi, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTransaksi = new JTable(modelTransaksi);
        tableTransaksi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableTransaksi.getColumnModel().getColumn(0).setPreferredWidth(120);
        tableTransaksi.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableTransaksi.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        // Filter components
        txtTanggalMulai = new JTextField(10);
        txtTanggalAkhir = new JTextField(10);
        cbStatusTransaksi = new JComboBox<>(new String[]{"Semua", "DIPINJAM", "DIKEMBALIKAN", "TERLAMBAT", "HILANG", "RUSAK"});
        btnFilterTransaksi = new JButton("Filter");
        btnExportTransaksi = new JButton("Export CSV");
        btnRefreshTransaksi = new JButton("Refresh");
        
        txtTanggalMulai.setToolTipText("Format: YYYY-MM-DD");
        txtTanggalAkhir.setToolTipText("Format: YYYY-MM-DD");
        
        btnFilterTransaksi.setBackground(new Color(255, 193, 7));
        btnFilterTransaksi.setForeground(Color.BLACK);
        btnExportTransaksi.setBackground(new Color(40, 167, 69));
        btnExportTransaksi.setForeground(Color.WHITE);
        btnRefreshTransaksi.setBackground(new Color(23, 162, 184));
        btnRefreshTransaksi.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Buku Report Panel
        JPanel panelBuku = new JPanel(new BorderLayout());
        JPanel topPanelBuku = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanelBuku.add(new JLabel("Laporan Data Buku"));
        topPanelBuku.add(Box.createHorizontalStrut(20));
        topPanelBuku.add(btnRefreshBuku);
        topPanelBuku.add(btnExportBuku);
        
        panelBuku.add(topPanelBuku, BorderLayout.NORTH);
        panelBuku.add(new JScrollPane(tableBuku), BorderLayout.CENTER);
        
        // Pustakawan Report Panel - NEW
        JPanel panelPustakawan = new JPanel(new BorderLayout());
        JPanel topPanelPustakawan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanelPustakawan.add(new JLabel("Laporan Data Pustakawan"));
        topPanelPustakawan.add(Box.createHorizontalStrut(20));
        topPanelPustakawan.add(btnRefreshPustakawan);
        topPanelPustakawan.add(btnExportPustakawan);
        
        panelPustakawan.add(topPanelPustakawan, BorderLayout.NORTH);
        panelPustakawan.add(new JScrollPane(tablePustakawan), BorderLayout.CENTER);
        
        // Siswa Report Panel
        JPanel panelSiswa = new JPanel(new BorderLayout());
        JPanel topPanelSiswa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanelSiswa.add(new JLabel("Laporan Data Siswa"));
        topPanelSiswa.add(Box.createHorizontalStrut(20));
        topPanelSiswa.add(btnRefreshSiswa);
        topPanelSiswa.add(btnExportSiswa);
        
        panelSiswa.add(topPanelSiswa, BorderLayout.NORTH);
        panelSiswa.add(new JScrollPane(tableSiswa), BorderLayout.CENTER);
        
        // Transaksi Report Panel
        JPanel panelTransaksi = new JPanel(new BorderLayout());
        JPanel topPanelTransaksi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanelTransaksi.add(new JLabel("Tanggal Mulai:"));
        topPanelTransaksi.add(txtTanggalMulai);
        topPanelTransaksi.add(new JLabel("Tanggal Akhir:"));
        topPanelTransaksi.add(txtTanggalAkhir);
        topPanelTransaksi.add(new JLabel("Status:"));
        topPanelTransaksi.add(cbStatusTransaksi);
        topPanelTransaksi.add(btnFilterTransaksi);
        topPanelTransaksi.add(Box.createHorizontalStrut(10));
        topPanelTransaksi.add(btnRefreshTransaksi);
        topPanelTransaksi.add(btnExportTransaksi);
        
        panelTransaksi.add(topPanelTransaksi, BorderLayout.NORTH);
        panelTransaksi.add(new JScrollPane(tableTransaksi), BorderLayout.CENTER);
        
        // Add tabs - REVISED order
        tabbedPane.addTab("Laporan Buku", panelBuku);
        tabbedPane.addTab("Laporan Pustakawan", panelPustakawan);
        tabbedPane.addTab("Laporan Siswa", panelSiswa);
        tabbedPane.addTab("Laporan Transaksi", panelTransaksi);
        
        add(tabbedPane);
    }
    
    private void setupEventListeners() {
        btnRefreshBuku.addActionListener(e -> loadBukuReport());
        btnRefreshPustakawan.addActionListener(e -> loadPustakawanReport()); // NEW
        btnRefreshSiswa.addActionListener(e -> loadSiswaReport());
        btnRefreshTransaksi.addActionListener(e -> loadTransaksiReport());
        
        btnExportBuku.addActionListener(e -> exportBukuReport());
        btnExportPustakawan.addActionListener(e -> exportPustakawanReport()); // NEW
        btnExportSiswa.addActionListener(e -> exportSiswaReport());
        btnExportTransaksi.addActionListener(e -> exportTransaksiReport());
        
        btnFilterTransaksi.addActionListener(e -> filterTransaksiReport());
    }
    
    private void loadAllReports() {
        loadBukuReport();
        loadPustakawanReport(); // NEW
        loadSiswaReport();
        loadTransaksiReport();
    }
    
    private void loadBukuReport() {
        try {
            List<Buku> bukuList = bukuDAO.findAll();
            modelBuku.setRowCount(0);
            
            for (Buku buku : bukuList) {
                Object[] row = {
                    buku.getKodeBuku(),
                    buku.getJudul(),
                    buku.getPengarang(),
                    buku.getPenerbit(),
                    buku.getTahunTerbit(),
                    buku.getKategori() != null ? buku.getKategori().getNamaKategori() : "",
                    buku.getRak() != null ? buku.getRak().getKodeRak() : "",
                    buku.getRak() != null ? buku.getRak().getLokasi() : "",
                    buku.getJumlahTotal(),
                    buku.getJumlahTersedia(),
                    buku.getJumlahDipinjam()
                };
                modelBuku.addRow(row);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Laporan buku berhasil dimuat. Total: " + bukuList.size() + " buku",
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading buku report: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSiswaReport() {
        try {
            List<Siswa> siswaList = siswaDAO.findAll();
            modelSiswa.setRowCount(0);
            
            for (Siswa siswa : siswaList) {
                Object[] row = {
                    siswa.getNis(),
                    siswa.getNamaLengkap(),
                    siswa.getKelas() != null ? siswa.getKelas().getNamaKelas() : "", // Will show X-IPA-1
                    "L".equals(siswa.getJenisKelamin()) ? "Laki-laki" : "Perempuan",
                    siswa.getUser() != null ? siswa.getUser().getUsername() : "",
                    siswa.isStatusAktif() ? "Aktif" : "Tidak Aktif"
                };
                modelSiswa.addRow(row);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Laporan siswa berhasil dimuat. Total: " + siswaList.size() + " siswa",
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading siswa report: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPustakawanReport() {
        try {
            List<Pustakawan> pustakawanList = pustakawanDAO.findAll();
            modelPustakawan.setRowCount(0);
            
            for (Pustakawan pustakawan : pustakawanList) {
                Object[] row = {
                    pustakawan.getNip(),
                    pustakawan.getNamaLengkap(),
                    pustakawan.getEmail() != null ? pustakawan.getEmail() : "",
                    pustakawan.getNoTelepon() != null ? pustakawan.getNoTelepon() : "",
                    pustakawan.getUser() != null ? pustakawan.getUser().getUsername() : "",
                    pustakawan.getUser() != null && pustakawan.getUser().isActive() ? "Aktif" : "Tidak Aktif"
                };
                modelPustakawan.addRow(row);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Laporan pustakawan berhasil dimuat. Total: " + pustakawanList.size() + " pustakawan",
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading pustakawan report: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTransaksiReport() {
        try {
            List<TransaksiPeminjaman> transaksiList = transaksiDAO.findAll();
            modelTransaksi.setRowCount(0);
            
            for (TransaksiPeminjaman transaksi : transaksiList) {
                Object[] row = {
                    transaksi.getKodeTransaksi(),
                    transaksi.getSiswa() != null ? transaksi.getSiswa().getNis() : "",
                    transaksi.getSiswa() != null ? transaksi.getSiswa().getNamaLengkap() : "",
                    transaksi.getBuku() != null ? transaksi.getBuku().getKodeBuku() : "",
                    transaksi.getBuku() != null ? transaksi.getBuku().getJudul() : "",
                    transaksi.getTanggalPinjam() != null ? transaksi.getTanggalPinjam().toString() : "",
                    transaksi.getTanggalKembaliRencana() != null ? transaksi.getTanggalKembaliRencana().toString() : "",
                    transaksi.getTanggalKembaliAktual() != null ? transaksi.getTanggalKembaliAktual().toString() : "",
                    transaksi.getStatus() != null ? transaksi.getStatus().getNamaStatus() : "",
                    transaksi.getDenda() != null ? "Rp " + transaksi.getDenda().toString() : "Rp 0",
                    transaksi.getPustakawan() != null ? transaksi.getPustakawan().getNamaLengkap() : ""
                };
                modelTransaksi.addRow(row);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Laporan transaksi berhasil dimuat. Total: " + transaksiList.size() + " transaksi",
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading transaksi report: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterTransaksiReport() {
        String tanggalMulai = txtTanggalMulai.getText().trim();
        String tanggalAkhir = txtTanggalAkhir.getText().trim();
        String status = (String) cbStatusTransaksi.getSelectedItem();
        
        try {
            List<TransaksiPeminjaman> transaksiList;
            
            if ("Semua".equals(status)) {
                transaksiList = transaksiDAO.findAll();
            } else {
                transaksiList = transaksiDAO.findByStatus(status);
            }
            
            modelTransaksi.setRowCount(0);
            
            for (TransaksiPeminjaman transaksi : transaksiList) {
                // Filter by date if provided
                boolean includeRecord = true;
                
                if (!tanggalMulai.isEmpty() && transaksi.getTanggalPinjam() != null) {
                    LocalDate startDate = LocalDate.parse(tanggalMulai);
                    if (transaksi.getTanggalPinjam().isBefore(startDate)) {
                        includeRecord = false;
                    }
                }
                
                if (!tanggalAkhir.isEmpty() && transaksi.getTanggalPinjam() != null) {
                    LocalDate endDate = LocalDate.parse(tanggalAkhir);
                    if (transaksi.getTanggalPinjam().isAfter(endDate)) {
                        includeRecord = false;
                    }
                }
                
                if (includeRecord) {
                    Object[] row = {
                        transaksi.getKodeTransaksi(),
                        transaksi.getSiswa() != null ? transaksi.getSiswa().getNis() : "",
                        transaksi.getSiswa() != null ? transaksi.getSiswa().getNamaLengkap() : "",
                        transaksi.getBuku() != null ? transaksi.getBuku().getKodeBuku() : "",
                        transaksi.getBuku() != null ? transaksi.getBuku().getJudul() : "",
                        transaksi.getTanggalPinjam() != null ? transaksi.getTanggalPinjam().toString() : "",
                        transaksi.getTanggalKembaliRencana() != null ? transaksi.getTanggalKembaliRencana().toString() : "",
                        transaksi.getTanggalKembaliAktual() != null ? transaksi.getTanggalKembaliAktual().toString() : "",
                        transaksi.getStatus() != null ? transaksi.getStatus().getNamaStatus() : "",
                        transaksi.getDenda() != null ? "Rp " + transaksi.getDenda().toString() : "Rp 0",
                        transaksi.getPustakawan() != null ? transaksi.getPustakawan().getNamaLengkap() : ""
                    };
                    modelTransaksi.addRow(row);
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                "Filter berhasil diterapkan. Total: " + modelTransaksi.getRowCount() + " transaksi",
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error filtering transaksi: " + e.getMessage(), 
                "Filter Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportBukuReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Laporan Buku");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        String defaultFileName = "laporan_buku_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write header
                writer.append("Kode Buku,Judul,Pengarang,Penerbit,Tahun,Kategori,Rak,Lokasi Rak,Total,Tersedia,Dipinjam\n");
                
                // Write data
                for (int i = 0; i < modelBuku.getRowCount(); i++) {
                    for (int j = 0; j < modelBuku.getColumnCount(); j++) {
                        Object value = modelBuku.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < modelBuku.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Laporan buku berhasil diekspor ke: " + file.getAbsolutePath(),
                    "Export Berhasil", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportSiswaReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Laporan Siswa");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        String defaultFileName = "laporan_siswa_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write header - REVISED
                writer.append("NIS,Nama Lengkap,Kelas,Jenis Kelamin,Username,Status\n");
                
                // Write data
                for (int i = 0; i < modelSiswa.getRowCount(); i++) {
                    for (int j = 0; j < modelSiswa.getColumnCount(); j++) {
                        Object value = modelSiswa.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < modelSiswa.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Laporan siswa berhasil diekspor ke: " + file.getAbsolutePath(),
                    "Export Berhasil", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportPustakawanReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Laporan Pustakawan");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        String defaultFileName = "laporan_pustakawan_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write header - NEW
                writer.append("NIP,Nama Lengkap,Email,No. Telepon,Username,Status\n");
                
                // Write data
                for (int i = 0; i < modelPustakawan.getRowCount(); i++) {
                    for (int j = 0; j < modelPustakawan.getColumnCount(); j++) {
                        Object value = modelPustakawan.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < modelPustakawan.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Laporan pustakawan berhasil diekspor ke: " + file.getAbsolutePath(),
                    "Export Berhasil", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportTransaksiReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Laporan Transaksi");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        String defaultFileName = "laporan_transaksi_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write header
                writer.append("Kode Transaksi,NIS Siswa,Nama Siswa,Kode Buku,Judul Buku,Tanggal Pinjam,Tanggal Kembali,Tanggal Kembali Aktual,Status,Denda,Pustakawan\n");
                
                // Write data
                for (int i = 0; i < modelTransaksi.getRowCount(); i++) {
                    for (int j = 0; j < modelTransaksi.getColumnCount(); j++) {
                        Object value = modelTransaksi.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < modelTransaksi.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Laporan transaksi berhasil diekspor ke: " + file.getAbsolutePath(),
                    "Export Berhasil", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}