package com.perpustakaan.view.siswa;

import com.perpustakaan.util.UserSession;
import com.perpustakaan.view.LoginFrame;
import javax.swing.*;
import java.awt.*;


public class SiswaDashboard extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuBuku, menuTransaksi, menuSistem;
    private JMenuItem miDaftarBuku, miCariBuku;
    private JMenuItem miRiwayatPinjam, miStatusPinjaman;
    private JMenuItem miLogout, miExit;
    
    private JPanel panelMain;
    private JLabel lblWelcome;
    
    public SiswaDashboard() {
        initComponents();
        setupMenuBar();
        setupLayout();
        setupEventListeners();
        
        setTitle("Siswa Dashboard - Perpustakaan SMA");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        panelMain = new JPanel(new BorderLayout());
        lblWelcome = new JLabel("Dashboard Siswa", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Buku
        menuBuku = new JMenu("Buku");
        miDaftarBuku = new JMenuItem("Daftar Buku Tersedia");
        miCariBuku = new JMenuItem("Cari Buku");
        
        menuBuku.add(miDaftarBuku);
        menuBuku.add(miCariBuku);
        
        // Menu Transaksi
        menuTransaksi = new JMenu("Transaksi Saya");
        miRiwayatPinjam = new JMenuItem("Riwayat Peminjaman");
        miStatusPinjaman = new JMenuItem("Status Peminjaman Aktif");
        
        menuTransaksi.add(miRiwayatPinjam);
        menuTransaksi.add(miStatusPinjaman);
        
        // Menu Sistem
        menuSistem = new JMenu("Sistem");
        miLogout = new JMenuItem("Logout");
        miExit = new JMenuItem("Keluar Aplikasi");
        
        menuSistem.add(miLogout);
        menuSistem.add(miExit);
        
        menuBar.add(menuBuku);
        menuBar.add(menuTransaksi);
        menuBar.add(menuSistem);
        
        setJMenuBar(menuBar);
    }
    
    private void setupLayout() {
        panelMain.add(lblWelcome, BorderLayout.CENTER);
        add(panelMain);
    }
    
    private void setupEventListeners() {
        // TODO: Implement event listeners for menu items
        miLogout.addActionListener(e -> logout());
        miExit.addActionListener(e -> exitApplication());
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitApplication();
            }
        });
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