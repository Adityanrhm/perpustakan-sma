package com.perpustakaan.view.pustakawan;

import com.perpustakaan.model.*;
import com.perpustakaan.util.DateTimeHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Enhanced Dialog untuk menampilkan detail transaksi dengan multiple items - NEW
 */
public class EnhancedTransaksiDetailDialog extends JDialog {
    private TransaksiPeminjaman transaksi;
    private BigDecimal dendaPerHari = new BigDecimal("2000");
    
    // Components
    private JTextField txtKodeTransaksi, txtNisSiswa, txtNamaSiswa, txtKelasSiswa;
    private JTextField txtTanggalPinjam, txtTotalBuku, txtTotalDenda, txtStatusKeseluruhan;
    private JTextField txtPustakawan;
    private JTextArea txtCatatan;
    private JTable tableDetailItems;
    private DefaultTableModel detailModel;
    private JLabel lblStatusInfo;
    private JButton btnClose, btnPrint;
    
    public EnhancedTransaksiDetailDialog(Dialog parent, TransaksiPeminjaman transaksi) {
        super(parent, "Detail Transaksi Multiple Items", true);
        this.transaksi = transaksi;
        
        initComponents();
        setupLayout();
        populateData();
        setupEventListeners();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Header info fields
        txtKodeTransaksi = new JTextField(20);
        txtKodeTransaksi.setEditable(false);
        txtNisSiswa = new JTextField(15);
        txtNisSiswa.setEditable(false);
        txtNamaSiswa = new JTextField(20);
        txtNamaSiswa.setEditable(false);
        txtKelasSiswa = new JTextField(15);
        txtKelasSiswa.setEditable(false);
        txtTanggalPinjam = new JTextField(12);
        txtTanggalPinjam.setEditable(false);
        txtTotalBuku = new JTextField(10);
        txtTotalBuku.setEditable(false);
        txtTotalDenda = new JTextField(15);
        txtTotalDenda.setEditable(false);
        txtStatusKeseluruhan = new JTextField(15);
        txtStatusKeseluruhan.setEditable(false);
        txtPustakawan = new JTextField(20);
        txtPustakawan.setEditable(false);
        
        // Notes
        txtCatatan = new JTextArea(4, 30);
        txtCatatan.setEditable(false);
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        
        // Detail items table
        String[] columns = {"Kode Buku", "Judul Buku", "Pengarang", "Jml Pinjam", "Jml Kembali", 
                           "Jml Hilang", "Jml Rusak", "Belum Kembali", "Tgl Kembali", "Status", 
                           "Hari Terlambat", "Denda Item"};
        detailModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDetailItems = new JTable(detailModel);
        tableDetailItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Adjust column widths
        tableDetailItems.getColumnModel().getColumn(0).setPreferredWidth(80);  // Kode Buku
        tableDetailItems.getColumnModel().getColumn(1).setPreferredWidth(200); // Judul
        tableDetailItems.getColumnModel().getColumn(2).setPreferredWidth(120); // Pengarang
        tableDetailItems.getColumnModel().getColumn(3).setPreferredWidth(60);  // Jml Pinjam
        tableDetailItems.getColumnModel().getColumn(4).setPreferredWidth(60);  // Jml Kembali
        tableDetailItems.getColumnModel().getColumn(5).setPreferredWidth(60);  // Jml Hilang
        tableDetailItems.getColumnModel().getColumn(6).setPreferredWidth(60);  // Jml Rusak
        tableDetailItems.getColumnModel().getColumn(7).setPreferredWidth(70);  // Belum Kembali
        tableDetailItems.getColumnModel().getColumn(8).setPreferredWidth(90);  // Tgl Kembali
        tableDetailItems.getColumnModel().getColumn(9).setPreferredWidth(80);  // Status
        tableDetailItems.getColumnModel().getColumn(10).setPreferredWidth(80); // Hari Terlambat
        tableDetailItems.getColumnModel().getColumn(11).setPreferredWidth(80); // Denda
        
        // Status info label
        lblStatusInfo = new JLabel("", SwingConstants.CENTER);
        lblStatusInfo.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Buttons
        btnClose = new JButton("Tutup");
        btnPrint = new JButton("Print Detail");
        
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnPrint.setBackground(new Color(23, 162, 184));
        btnPrint.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Informasi Transaksi"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("Kode Transaksi:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtKodeTransaksi, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Pustakawan:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtPustakawan, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("NIS Siswa:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtNisSiswa, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Nama Siswa:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtNamaSiswa, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtKelasSiswa, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Tanggal Pinjam:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtTanggalPinjam, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Total Buku:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtTotalBuku, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Total Denda:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtTotalDenda, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(txtStatusKeseluruhan, gbc);
        
        // Row 5 - Catatan
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Catatan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(new JScrollPane(txtCatatan), gbc);
        
        // Detail items panel
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Detail Item yang Dipinjam"));
        
        JScrollPane scrollPane = new JScrollPane(tableDetailItems);
        scrollPane.setPreferredSize(new Dimension(850, 300));
        detailPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status info panel
        JPanel statusPanel = new JPanel();
        statusPanel.add(lblStatusInfo);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnClose);
        
        // Main layout
        add(headerPanel, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.CENTER);
        add(detailPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Use split pane for better layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, headerPanel, detailPanel);
        splitPane.setDividerLocation(200);
        
        add(splitPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void populateData() {
        if (transaksi == null) return;
        
        // Header information
        txtKodeTransaksi.setText(transaksi.getKodeTransaksi());
        txtPustakawan.setText(transaksi.getPustakawan() != null ? 
            transaksi.getPustakawan().getNamaLengkap() : "");
        
        // Student info
        if (transaksi.getSiswa() != null) {
            txtNisSiswa.setText(transaksi.getSiswa().getNis());
            txtNamaSiswa.setText(transaksi.getSiswa().getNamaLengkap());
            txtKelasSiswa.setText(transaksi.getSiswa().getKelas() != null ? 
                transaksi.getSiswa().getKelas().getNamaKelas() : "");
        }
        
        // Transaction info
        txtTanggalPinjam.setText(DateTimeHelper.formatDate(transaksi.getTanggalPinjam()));
        txtTotalBuku.setText(String.valueOf(transaksi.getTotalBuku()));
        txtTotalDenda.setText("Rp " + (transaksi.getTotalDenda() != null ? transaksi.getTotalDenda() : BigDecimal.ZERO));
        txtStatusKeseluruhan.setText(transaksi.getStatusKeseluruhan() != null ? transaksi.getStatusKeseluruhan() : "");
        txtCatatan.setText(transaksi.getCatatan() != null ? transaksi.getCatatan() : "");
        
        // Detail items
        populateDetailItemsTable();
        
        // Update status info
        updateStatusInfo();
    }
    
    private void populateDetailItemsTable() {
        detailModel.setRowCount(0);
        
        if (transaksi.getDetailItems() == null || transaksi.getDetailItems().isEmpty()) {
            return;
        }
        
        for (DetailTransaksiPeminjaman detail : transaksi.getDetailItems()) {
            // Calculate values
            long hariTerlambat = detail.getHariTerlambat();
            BigDecimal dendaItem = detail.hitungDenda(dendaPerHari);
            String statusDetail = detail.getStatusDetail();
            
            Object[] row = {
                detail.getBuku() != null ? detail.getBuku().getKodeBuku() : "",
                detail.getBuku() != null ? detail.getBuku().getJudul() : "",
                detail.getBuku() != null ? detail.getBuku().getPengarang() : "",
                detail.getJumlahPinjam(),
                detail.getJumlahKembali(),
                detail.getJumlahHilang(),
                detail.getJumlahRusak(),
                detail.getJumlahBelumKembali(),
                DateTimeHelper.formatDate(detail.getTanggalKembaliRencana()),
                statusDetail,
                hariTerlambat > 0 ? hariTerlambat + " hari" : "Tidak terlambat",
                "Rp " + dendaItem
            };
            detailModel.addRow(row);
        }
    }
    
    private void updateStatusInfo() {
        if (transaksi == null) return;
        
        String statusText = "";
        Color statusColor = Color.BLACK;
        
        int totalBelumKembali = transaksi.getTotalBukuBelumKembali();
        boolean hasOverdueItems = transaksi.hasOverdueItems();
        
        if (totalBelumKembali == 0) {
            statusText = "SEMUA ITEM SUDAH DIKEMBALIKAN";
            statusColor = new Color(40, 167, 69); // Green
        } else if (hasOverdueItems) {
            statusText = "TERDAPAT ITEM TERLAMBAT - SISA " + totalBelumKembali + " ITEM";
            statusColor = new Color(220, 53, 69); // Red
        } else {
            statusText = "MASIH AKTIF - SISA " + totalBelumKembali + " ITEM";
            statusColor = new Color(23, 162, 184); // Blue
        }
        
        lblStatusInfo.setText(statusText);
        lblStatusInfo.setForeground(statusColor);
    }
    
    private void setupEventListeners() {
        btnClose.addActionListener(e -> dispose());
        btnPrint.addActionListener(e -> printTransactionDetail());
    }
    
    private void printTransactionDetail() {
        try {
            // Create printable content
            StringBuilder printContent = new StringBuilder();
            
            printContent.append("DETAIL TRANSAKSI PEMINJAMAN MULTIPLE ITEMS\n");
            printContent.append("=========================================\n\n");
            
            printContent.append("Kode Transaksi : ").append(txtKodeTransaksi.getText()).append("\n");
            printContent.append("Tanggal Pinjam : ").append(txtTanggalPinjam.getText()).append("\n");
            printContent.append("Pustakawan     : ").append(txtPustakawan.getText()).append("\n\n");
            
            printContent.append("INFORMASI SISWA:\n");
            printContent.append("NIS            : ").append(txtNisSiswa.getText()).append("\n");
            printContent.append("Nama           : ").append(txtNamaSiswa.getText()).append("\n");
            printContent.append("Kelas          : ").append(txtKelasSiswa.getText()).append("\n\n");
            
            printContent.append("RINGKASAN:\n");
            printContent.append("Total Buku     : ").append(txtTotalBuku.getText()).append("\n");
            printContent.append("Total Denda    : ").append(txtTotalDenda.getText()).append("\n");
            printContent.append("Status         : ").append(txtStatusKeseluruhan.getText()).append("\n\n");
            
            printContent.append("DETAIL ITEM:\n");
            printContent.append("-".repeat(150)).append("\n");
            printContent.append(String.format("%-15s %-25s %-15s %8s %8s %8s %8s %10s %-12s %-10s %12s\n",
                "Kode Buku", "Judul", "Pengarang", "Pinjam", "Kembali", "Hilang", "Rusak", 
                "Belum", "Tgl Kembali", "Status", "Denda"));
            printContent.append("-".repeat(150)).append("\n");
            
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                printContent.append(String.format("%-15s %-25s %-15s %8s %8s %8s %8s %10s %-12s %-10s %12s\n",
                    detailModel.getValueAt(i, 0).toString(),
                    detailModel.getValueAt(i, 1).toString().length() > 25 ? 
                        detailModel.getValueAt(i, 1).toString().substring(0, 22) + "..." : 
                        detailModel.getValueAt(i, 1).toString(),
                    detailModel.getValueAt(i, 2).toString().length() > 15 ? 
                        detailModel.getValueAt(i, 2).toString().substring(0, 12) + "..." : 
                        detailModel.getValueAt(i, 2).toString(),
                    detailModel.getValueAt(i, 3),
                    detailModel.getValueAt(i, 4),
                    detailModel.getValueAt(i, 5),
                    detailModel.getValueAt(i, 6),
                    detailModel.getValueAt(i, 7),
                    detailModel.getValueAt(i, 8),
                    detailModel.getValueAt(i, 9),
                    detailModel.getValueAt(i, 11)
                ));
            }
            
            if (txtCatatan.getText() != null && !txtCatatan.getText().trim().isEmpty()) {
                printContent.append("\nCATATAN:\n");
                printContent.append(txtCatatan.getText()).append("\n");
            }
            
            printContent.append("\n").append("=".repeat(150)).append("\n");
            printContent.append("Dicetak pada: ").append(DateTimeHelper.formatDateTime(java.time.LocalDateTime.now()));
            
            // Show in dialog for printing
            JTextArea printArea = new JTextArea(printContent.toString());
            printArea.setFont(new Font("Courier New", Font.PLAIN, 10));
            printArea.setEditable(false);
            
            JScrollPane scrollPane = new JScrollPane(printArea);
            scrollPane.setPreferredSize(new Dimension(800, 600));
            
            int option = JOptionPane.showConfirmDialog(
                this, 
                scrollPane, 
                "Print Preview - Detail Transaksi", 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (option == JOptionPane.OK_OPTION) {
                printArea.print();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saat mencetak: " + e.getMessage(),
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}