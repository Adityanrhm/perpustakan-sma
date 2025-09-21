
package com.perpustakaan.view.pustakawan;

import com.perpustakaan.util.UserSession;
import com.perpustakaan.view.LoginFrame;
import javax.swing.*;
import java.awt.*;

/**
 * Dashboard untuk Pustakawan
 */
public class PustakawanDashboard extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuTransaksi, menuBuku, menuLaporan, menuSistem;
    private JMenuItem miPeminjaman, miPengembalian, miDendaTunggakan;
    private JMenuItem miDaftarBuku, miCariBuku, miTambahBuku;
    private JMenuItem miLaporanPeminjaman, miLaporanBuku;
    private JMenuItem miLogout, miExit;
    
    private JPanel panelMain;
    private JLabel lblWelcome;
    
    public PustakawanDashboard() {
        initComponents();
        setupMenuBar();
        setupLayout();
        setupEventListeners();
        
        setTitle("Pustakawan Dashboard - Perpustakaan SMA");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        panelMain = new JPanel(new BorderLayout());
        lblWelcome = new JLabel("Dashboard Pustakawan", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Transaksi
        menuTransaksi = new JMenu("Transaksi");
        miPeminjaman = new JMenuItem("Peminjaman Buku");
        miPengembalian = new JMenuItem("Pengembalian Buku");
        miDendaTunggakan = new JMenuItem("Denda & Tunggakan");
        
        menuTransaksi.add(miPeminjaman);
        menuTransaksi.add(miPengembalian);
        menuTransaksi.add(miDendaTunggakan);
        
        // Menu Buku
        menuBuku = new JMenu("Buku");
        miDaftarBuku = new JMenuItem("Daftar Buku");
        miCariBuku = new JMenuItem("Cari Buku");
        miTambahBuku = new JMenuItem("Tambah Buku Baru");
        
        menuBuku.add(miDaftarBuku);
        menuBuku.add(miCariBuku);
        menuBuku.addSeparator();
        menuBuku.add(miTambahBuku);
        
        // Menu Laporan
        menuLaporan = new JMenu("Laporan");
        miLaporanPeminjaman = new JMenuItem("Laporan Peminjaman");
        miLaporanBuku = new JMenuItem("Laporan Status Buku");
        
        menuLaporan.add(miLaporanPeminjaman);
        menuLaporan.add(miLaporanBuku);
        
        // Menu Sistem
        menuSistem = new JMenu("Sistem");
        miLogout = new JMenuItem("Logout");
        miExit = new JMenuItem("Keluar Aplikasi");
        
        menuSistem.add(miLogout);
        menuSistem.add(miExit);
        
        menuBar.add(menuTransaksi);
        menuBar.add(menuBuku);
        menuBar.add(menuLaporan);
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