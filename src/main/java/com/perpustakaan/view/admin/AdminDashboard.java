package com.perpustakaan.view.admin;

import com.perpustakaan.util.UserSession;
import com.perpustakaan.view.LoginFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dashboard untuk Admin
 */
public class AdminDashboard extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuMaster, menuLaporan, menuSistem;
    private JMenuItem miPustakawan, miSiswa;
    private JMenuItem miLaporanBuku, miLaporanTransaksi, miLaporanSiswa;
    private JMenuItem miPengaturan, miLogout, miExit;
    
    private JPanel panelMain;
    private JLabel lblWelcome;
    private JLabel lblUserInfo;
    private JLabel lblDateTime;
    
    public AdminDashboard() {
        initComponents();
        setupMenuBar();
        setupLayout();
        setupEventListeners();
        updateUserInfo();
        
        setTitle("Admin Dashboard - Perpustakaan SMA");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));
        
        // Center the frame
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        panelMain = new JPanel(new BorderLayout());
        
        lblWelcome = new JLabel("Selamat Datang, Administrator!", SwingConstants.CENTER);
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
        
        // Menu Master Data
        menuMaster = new JMenu("Master Data");
        miPustakawan = new JMenuItem("Kelola Pustakawan");
        miSiswa = new JMenuItem("Kelola Siswa");
        
        menuMaster.add(miPustakawan);
        menuMaster.add(miSiswa);
        
        // Menu Laporan
        menuLaporan = new JMenu("Laporan");
        miLaporanBuku = new JMenuItem("Laporan Data Buku");
        miLaporanTransaksi = new JMenuItem("Laporan Transaksi Peminjaman");
        miLaporanSiswa = new JMenuItem("Laporan Data Siswa");
        
        menuLaporan.add(miLaporanBuku);
        menuLaporan.add(miLaporanTransaksi);
        menuLaporan.add(miLaporanSiswa);
        
        // Menu Sistem
        menuSistem = new JMenu("Sistem");
        miPengaturan = new JMenuItem("Pengaturan Sistem");
        miLogout = new JMenuItem("Logout");
        miExit = new JMenuItem("Keluar Aplikasi");
        
        menuSistem.add(miPengaturan);
        menuSistem.addSeparator();
        menuSistem.add(miLogout);
        menuSistem.add(miExit);
        
        menuBar.add(menuMaster);
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
        JLabel lblStatus = new JLabel("Status: Online | Role: Administrator");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(lblStatus);
        panelMain.add(statusBar, BorderLayout.SOUTH);
        
        add(panelMain);
    }
    
    private void setupEventListeners() {
        // Master Data menu items
        miPustakawan.addActionListener(e -> {
            try {
                new PustakawanManagementFrame(this).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error opening Pustakawan Management: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        miSiswa.addActionListener(e -> {
            try {
                new SiswaManagementFrame(this).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error opening Siswa Management: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Laporan menu items - Gabungkan semua laporan dalam satu frame
        ActionListener openReportsListener = e -> {
            try {
                new ReportFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error opening Reports: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        
        miLaporanBuku.addActionListener(openReportsListener);
        miLaporanTransaksi.addActionListener(openReportsListener);
        miLaporanSiswa.addActionListener(openReportsListener);
        
        // Sistem menu items
        miPengaturan.addActionListener(e -> {
            // TODO: Open Pengaturan Sistem
            JOptionPane.showMessageDialog(this, "Fitur Pengaturan Sistem akan segera tersedia", 
                                        "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        
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
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.getInstance().endSession();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin keluar dari aplikasi?",
            "Konfirmasi Keluar",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.getInstance().endSession();
            System.exit(0);
        }
    }
}