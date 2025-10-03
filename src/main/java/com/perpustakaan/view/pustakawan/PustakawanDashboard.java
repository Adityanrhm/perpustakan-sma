package com.perpustakaan.view.pustakawan;

import com.perpustakaan.util.UserSession;
import com.perpustakaan.util.UIHelper;
import com.perpustakaan.view.LoginFrame;
import javax.swing.*;
import java.awt.*;

/**
 * Dashboard modular untuk Pustakawan
 * Menggunakan pendekatan modular untuk mengurangi kompleksitas
 */
public class PustakawanDashboard extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuTransaksi, menuBuku, menuSiswa, menuLaporan, menuSistem;
    
    // Menu items
    private JMenuItem miPeminjaman, miPengembalian, miDendaTunggakan, miRiwayatTransaksi;
    private JMenuItem miDaftarBuku, miTambahBuku, miCariBuku, miStokBuku;
    private JMenuItem miDaftarSiswa, miTambahSiswa, miCariSiswa;
    private JMenuItem miLaporanPeminjaman, miLaporanBuku, miLaporanSiswa;
    private JMenuItem miPengaturan, miLogout, miExit;
    
    // Main components
    private JPanel panelMain;
    private JLabel lblWelcome, lblUserInfo, lblDateTime;
    
    public PustakawanDashboard() {
        initComponents();
        setupMenuBar();
        setupLayout();
        setupEventListeners();
        updateUserInfo();
        
        setTitle("Pustakawan Dashboard - Perpustakaan SMA");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        panelMain = new JPanel(new BorderLayout());
        
        lblWelcome = new JLabel("Selamat Datang, Pustakawan!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        lblUserInfo = new JLabel("", SwingConstants.CENTER);
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 16));
        
        lblDateTime = new JLabel("", SwingConstants.CENTER);
        lblDateTime.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDateTime.setForeground(Color.GRAY);
        
        // Update date time every second
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();
        updateDateTime();
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Transaksi
        menuTransaksi = new JMenu("Transaksi");
        miPeminjaman = new JMenuItem("Peminjaman Buku");
        miPengembalian = new JMenuItem("Pengembalian Buku");
        miDendaTunggakan = new JMenuItem("Denda & Tunggakan");
        miRiwayatTransaksi = new JMenuItem("Riwayat Transaksi");
        
        menuTransaksi.add(miPeminjaman);
        menuTransaksi.add(miPengembalian);
        menuTransaksi.add(miDendaTunggakan);
        menuTransaksi.addSeparator();
        menuTransaksi.add(miRiwayatTransaksi);
        
        // Menu Buku - Simplified
        menuBuku = new JMenu("Kelola Buku");
        miDaftarBuku = new JMenuItem("Kelola Data Buku");
        miStokBuku = new JMenuItem("Kelola Stok Buku");
        
        menuBuku.add(miDaftarBuku);
        menuBuku.addSeparator();
        menuBuku.add(miStokBuku);
        
        // Menu Siswa - Simplified
        menuSiswa = new JMenu("Kelola Siswa");
        miDaftarSiswa = new JMenuItem("Kelola Data Siswa");
        
        menuSiswa.add(miDaftarSiswa);
        
        // Menu Laporan
        menuLaporan = new JMenu("Laporan");
        miLaporanPeminjaman = new JMenuItem("Laporan Peminjaman");
        miLaporanBuku = new JMenuItem("Laporan Status Buku");
        miLaporanSiswa = new JMenuItem("Laporan Data Siswa");
        
        menuLaporan.add(miLaporanPeminjaman);
        menuLaporan.add(miLaporanBuku);
        menuLaporan.add(miLaporanSiswa);
        
        // Menu Sistem
        menuSistem = new JMenu("Sistem");
        miPengaturan = new JMenuItem("Pengaturan");
        miLogout = new JMenuItem("Logout");
        miExit = new JMenuItem("Keluar Aplikasi");
        
        menuSistem.add(miPengaturan);
        menuSistem.addSeparator();
        menuSistem.add(miLogout);
        menuSistem.add(miExit);
        
        menuBar.add(menuTransaksi);
        menuBar.add(menuBuku);
        menuBar.add(menuSiswa);
        menuBar.add(menuLaporan);
        menuBar.add(menuSistem);
        
        setJMenuBar(menuBar);
    }
    
    private void setupLayout() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(lblWelcome, BorderLayout.NORTH);
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        infoPanel.add(lblUserInfo);
        infoPanel.add(lblDateTime);
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        
        panelMain.add(centerPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblStatus = new JLabel("Status: Online | Role: Pustakawan");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(lblStatus);
        panelMain.add(statusBar, BorderLayout.SOUTH);
        
        add(panelMain);
    }
    
    private void setupEventListeners() {
        // Menu Transaksi handlers - IMPLEMENTED
        miPeminjaman.addActionListener(e -> openTransaksiPeminjaman());
        miPengembalian.addActionListener(e -> openTransaksiPengembalian());
        miDendaTunggakan.addActionListener(e -> openDendaTunggakan());
        miRiwayatTransaksi.addActionListener(e -> openRiwayatTransaksi());
        
        // Menu Buku handlers - Unified to single handler
        miDaftarBuku.addActionListener(e -> openBukuManagement());
        miStokBuku.addActionListener(e -> openStokManagement());
        
        // Menu Siswa handlers - Unified to single handler  
        miDaftarSiswa.addActionListener(e -> openSiswaManagement());
        
        // Menu Laporan
        miLaporanPeminjaman.addActionListener(e -> openLaporanPeminjaman());
        miLaporanBuku.addActionListener(e -> openLaporanBuku());
        miLaporanSiswa.addActionListener(e -> openLaporanSiswa());
        
        // Menu Sistem
        miPengaturan.addActionListener(e -> openPengaturan());
        miLogout.addActionListener(e -> logout());
        miExit.addActionListener(e -> exitApplication());
        
        // Window closing event
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitApplication();
            }
        });
    }
    
    // Menu Transaksi handlers - IMPLEMENTED
    private void openTransaksiPeminjaman() {
        try {
            new TransaksiPeminjamanFrame(this).setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error opening Transaksi Peminjaman: " + e.getMessage());
        }
    }
    
    private void openTransaksiPengembalian() {
        try {
            new TransaksiPengembalianFrame(this).setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error opening Transaksi Pengembalian: " + e.getMessage());
        }
    }
    
    private void openDendaTunggakan() {
        UIHelper.showWarningMessage(this, "Fitur Denda & Tunggakan akan segera tersedia");
    }
    
    private void openRiwayatTransaksi() {
        UIHelper.showWarningMessage(this, "Fitur Riwayat Transaksi akan segera tersedia");
    }
    
    // Menu Buku handlers - Unified
    private void openBukuManagement() {
        try {
            new BukuManagementFrame(this).setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error opening Buku Management: " + e.getMessage());
        }
    }
    
    private void openStokManagement() {
        UIHelper.showWarningMessage(this, "Fitur Kelola Stok Buku akan segera tersedia");
    }
    
    // Menu Siswa handlers - Unified to single handler
    private void openSiswaManagement() {
        try {
            new SiswaManagementPustakawanFrame(this).setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error opening Siswa Management: " + e.getMessage());
        }
    }
    
    // Menu Laporan handlers
    private void openLaporanPeminjaman() {
        try {
            new PustakawanReportFrame("PEMINJAMAN").setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error opening Laporan: " + e.getMessage());
        }
    }
    
    private void openLaporanBuku() {
        try {
            new PustakawanReportFrame("BUKU").setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error opening Laporan: " + e.getMessage());
        }
    }
    
    private void openLaporanSiswa() {
        try {
            new PustakawanReportFrame("SISWA").setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error opening Laporan: " + e.getMessage());
        }
    }
    
    // Menu Sistem handlers
    private void openPengaturan() {
        UIHelper.showWarningMessage(this, "Fitur Pengaturan akan segera tersedia");
    }
    
    private void updateUserInfo() {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            String userInfo = String.format("Login sebagai: %s | Durasi: %s",
                session.getCurrentUser().getLoginIdentifier(),
                session.getLoginDuration());
            lblUserInfo.setText(userInfo);
        }
    }
    
    private void updateDateTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss");
        lblDateTime.setText(now.format(formatter));
        
        // Update user info as well
        updateUserInfo();
    }
    
    private void logout() {
        if (UIHelper.showConfirmDialog(this, "Apakah Anda yakin ingin logout?")) {
            UserSession.getInstance().endSession();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void exitApplication() {
        if (UIHelper.showConfirmDialog(this, "Apakah Anda yakin ingin keluar dari aplikasi?")) {
            UserSession.getInstance().endSession();
            System.exit(0);
        }
    }
}