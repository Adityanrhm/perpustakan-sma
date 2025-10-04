package com.perpustakaan.view.pustakawan;

import com.perpustakaan.dao.*;
import com.perpustakaan.model.*;
import com.perpustakaan.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.math.BigDecimal;

/**
 * Dialog untuk edit transaksi peminjaman existing
 * Fitur: Edit catatan, tambah/hapus item, update jumlah & tanggal
 */
public class EditTransaksiDialog extends JDialog {
    private TransaksiPeminjaman transaksi;
    private TransaksiPeminjamanDAO transaksiDAO;
    private DetailTransaksiPeminjamanDAO detailDAO;
    private BukuDAO bukuDAO;
    private StatusTransaksiDAO statusDAO;
    
    // Components
    private JTextField txtKodeTransaksi, txtNisSiswa, txtNamaSiswa, txtTanggalPinjam;
    private JTextArea txtCatatan;
    private JTable tableItems;
    private DefaultTableModel itemsModel;
    private JButton btnTambahItem, btnHapusItem, btnEditItem, btnSimpan, btnBatal;
    private JLabel lblTotalBuku, lblStatus, lblInfo;
    
    // Data
    private List<DetailTransaksiPeminjaman> currentDetails;
    private List<DetailTransaksiPeminjaman> detailsToDelete;
    private List<DetailTransaksiPeminjaman> newDetails;
    
    public EditTransaksiDialog(Dialog parent, TransaksiPeminjaman transaksi) {
        super(parent, "Edit Transaksi Peminjaman - " + transaksi.getKodeTransaksi(), true);
        
        this.transaksi = transaksi;
        this.transaksiDAO = new TransaksiPeminjamanDAO();
        this.detailDAO = new DetailTransaksiPeminjamanDAO();
        this.bukuDAO = new BukuDAO();
        this.statusDAO = new StatusTransaksiDAO();
        
        this.currentDetails = new ArrayList<>(transaksi.getDetailItems());
        this.detailsToDelete = new ArrayList<>();
        this.newDetails = new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        populateData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Info fields
        txtKodeTransaksi = new JTextField(20);
        txtKodeTransaksi.setEditable(false);
        txtKodeTransaksi.setBackground(new Color(240, 240, 240));
        
        txtNisSiswa = new JTextField(15);
        txtNisSiswa.setEditable(false);
        txtNisSiswa.setBackground(new Color(240, 240, 240));
        
        txtNamaSiswa = new JTextField(25);
        txtNamaSiswa.setEditable(false);
        txtNamaSiswa.setBackground(new Color(240, 240, 240));
        
        txtTanggalPinjam = new JTextField(12);
        txtTanggalPinjam.setEditable(false);
        txtTanggalPinjam.setBackground(new Color(240, 240, 240));
        
        txtCatatan = new JTextArea(3, 40);
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        
        // Table
        String[] columns = {"ID", "Kode Buku", "Judul Buku", "Jml Pinjam", "Jml Kembali", 
                           "Belum Kembali", "Tgl Kembali Rencana", "Status", "Tipe"};
        itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableItems = new JTable(itemsModel);
        tableItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableItems.getColumnModel().getColumn(0).setMaxWidth(50);
        tableItems.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableItems.getColumnModel().getColumn(2).setPreferredWidth(200);
        
        // Labels
        lblTotalBuku = new JLabel("Total Buku: 0");
        lblTotalBuku.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblStatus = new JLabel("Status: AKTIF");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(new Color(40, 167, 69));
        
        lblInfo = new JLabel("⚠ Perhatian: Hanya item yang belum ada pengembalian yang bisa dihapus/diubah");
        lblInfo.setForeground(new Color(220, 53, 69));
        
        // Buttons
        btnTambahItem = new JButton("Tambah Item Baru");
        btnHapusItem = new JButton("Hapus Item");
        btnEditItem = new JButton("Edit Item");
        btnSimpan = new JButton("Simpan Semua Perubahan");
        btnBatal = new JButton("Batal");
        
        btnTambahItem.setBackground(new Color(40, 167, 69));
        btnTambahItem.setForeground(Color.WHITE);
        btnHapusItem.setBackground(new Color(220, 53, 69));
        btnHapusItem.setForeground(Color.WHITE);
        btnEditItem.setBackground(new Color(255, 193, 7));
        btnEditItem.setForeground(Color.BLACK);
        btnSimpan.setBackground(new Color(40, 167, 69));
        btnSimpan.setForeground(Color.WHITE);
        btnBatal.setBackground(new Color(108, 117, 125));
        btnBatal.setForeground(Color.WHITE);
        
        btnHapusItem.setEnabled(false);
        btnEditItem.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Informasi Transaksi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("Kode Transaksi:"), gbc);
        gbc.gridx = 1;
        headerPanel.add(txtKodeTransaksi, gbc);
        
        gbc.gridx = 2;
        headerPanel.add(new JLabel("Tanggal Pinjam:"), gbc);
        gbc.gridx = 3;
        headerPanel.add(txtTanggalPinjam, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        headerPanel.add(new JLabel("NIS Siswa:"), gbc);
        gbc.gridx = 1;
        headerPanel.add(txtNisSiswa, gbc);
        
        gbc.gridx = 2;
        headerPanel.add(new JLabel("Nama Siswa:"), gbc);
        gbc.gridx = 3;
        headerPanel.add(txtNamaSiswa, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        headerPanel.add(new JLabel("Catatan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(new JScrollPane(txtCatatan), gbc);
        
        // Row 3 - Status info
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(lblTotalBuku);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(lblStatus);
        headerPanel.add(statusPanel, gbc);
        
        // Items panel
        JPanel itemsPanel = new JPanel(new BorderLayout(5, 5));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Detail Item Peminjaman"));
        
        JScrollPane scrollPane = new JScrollPane(tableItems);
        scrollPane.setPreferredSize(new Dimension(950, 250));
        itemsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel itemButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemButtonPanel.add(btnTambahItem);
        itemButtonPanel.add(btnEditItem);
        itemButtonPanel.add(btnHapusItem);
        itemsPanel.add(itemButtonPanel, BorderLayout.SOUTH);
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(lblInfo);
        
        // Bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        // Main layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(itemsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        btnTambahItem.addActionListener(e -> tambahItem());
        btnEditItem.addActionListener(e -> editItem());
        btnHapusItem.addActionListener(e -> hapusItem());
        btnSimpan.addActionListener(e -> simpanPerubahan());
        btnBatal.addActionListener(e -> dispose());
        
        tableItems.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }
    
    private void populateData() {
        txtKodeTransaksi.setText(transaksi.getKodeTransaksi());
        txtTanggalPinjam.setText(DateTimeHelper.formatDate(transaksi.getTanggalPinjam()));
        
        if (transaksi.getSiswa() != null) {
            txtNisSiswa.setText(transaksi.getSiswa().getNis());
            txtNamaSiswa.setText(transaksi.getSiswa().getNamaLengkap());
        }
        
        txtCatatan.setText(transaksi.getCatatan() != null ? transaksi.getCatatan() : "");
        lblStatus.setText("Status: " + transaksi.getStatusKeseluruhan());
        
        updateItemsTable();
        updateSummary();
    }
    
    private void updateItemsTable() {
        itemsModel.setRowCount(0);
        
        for (DetailTransaksiPeminjaman detail : currentDetails) {
            String tipe = newDetails.contains(detail) ? "BARU" : "EXISTING";
            
            Object[] row = {
                detail.getIdDetail(),
                detail.getBuku().getKodeBuku(),
                detail.getBuku().getJudul(),
                detail.getJumlahPinjam(),
                detail.getJumlahKembali(),
                detail.getJumlahBelumKembali(),
                DateTimeHelper.formatDate(detail.getTanggalKembaliRencana()),
                detail.getStatus() != null ? detail.getStatus().getNamaStatus() : "DIPINJAM",
                tipe
            };
            itemsModel.addRow(row);
        }
    }
    
    private void updateSummary() {
        int totalBuku = currentDetails.stream().mapToInt(DetailTransaksiPeminjaman::getJumlahPinjam).sum();
        lblTotalBuku.setText("Total Buku: " + totalBuku);
    }
    
    private void updateButtonStates() {
        int selectedRow = tableItems.getSelectedRow();
        if (selectedRow < 0) {
            btnHapusItem.setEnabled(false);
            btnEditItem.setEnabled(false);
            return;
        }
        
        DetailTransaksiPeminjaman detail = currentDetails.get(selectedRow);
        
        // Bisa edit/hapus jika belum ada pengembalian
        boolean canModify = detail.getJumlahKembali() == 0 && 
                           detail.getJumlahHilang() == 0 && 
                           detail.getJumlahRusak() == 0;
        
        btnHapusItem.setEnabled(canModify);
        btnEditItem.setEnabled(canModify);
    }
    
    private void tambahItem() {
        // Pass currentDetails agar dialog tahu item mana yang sudah dipilih
        MultipleBookSelectionDialog dialog = new MultipleBookSelectionDialog(this, currentDetails);
        dialog.setVisible(true);
        
        if (dialog.getSelectedItems() != null && !dialog.getSelectedItems().isEmpty()) {
            try {
                StatusTransaksi statusDipinjam = statusDAO.findByName("DIPINJAM");
                
                for (BookSelectionItem item : dialog.getSelectedItems()) {
                    DetailTransaksiPeminjaman detail = new DetailTransaksiPeminjaman();
                    detail.setBuku(item.getBuku());
                    detail.setJumlahPinjam(item.getJumlah());
                    detail.setTransaksi(transaksi);
                    
                    // Default due date: 1 minggu dari tanggal pinjam
                    LocalDate dueDate = transaksi.getTanggalPinjam().plusWeeks(1);
                    detail.setTanggalKembaliRencana(dueDate);
                    
                    detail.setStatus(statusDipinjam);
                    detail.setJumlahKembali(0);
                    detail.setJumlahHilang(0);
                    detail.setJumlahRusak(0);
                    detail.setDendaPerItem(BigDecimal.ZERO);
                    detail.setTotalDendaItem(BigDecimal.ZERO);
                    
                    currentDetails.add(detail);
                    newDetails.add(detail);
                }
                
                updateItemsTable();
                updateSummary();
                UIHelper.showSuccessMessage(this, 
                    dialog.getSelectedItems().size() + " item berhasil ditambahkan!");
                
            } catch (SQLException e) {
                UIHelper.showErrorMessage(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void editItem() {
        int selectedRow = tableItems.getSelectedRow();
        if (selectedRow < 0) return;
        
        DetailTransaksiPeminjaman detail = currentDetails.get(selectedRow);
        
        if (detail.getJumlahKembali() > 0 || detail.getJumlahHilang() > 0 || detail.getJumlahRusak() > 0) {
            UIHelper.showWarningMessage(this, 
                "Item ini sudah ada pengembalian, tidak bisa diedit!\n" +
                "Kembali: " + detail.getJumlahKembali() + ", " +
                "Hilang: " + detail.getJumlahHilang() + ", " +
                "Rusak: " + detail.getJumlahRusak());
            return;
        }
        
        EditDetailItemDialog dialog = new EditDetailItemDialog(this, detail);
        dialog.setVisible(true);
        
        if (dialog.isUpdated()) {
            updateItemsTable();
            updateSummary();
        }
    }
    
    private void hapusItem() {
        int selectedRow = tableItems.getSelectedRow();
        if (selectedRow < 0) return;
        
        DetailTransaksiPeminjaman detail = currentDetails.get(selectedRow);
        
        if (detail.getJumlahKembali() > 0 || detail.getJumlahHilang() > 0 || detail.getJumlahRusak() > 0) {
            UIHelper.showWarningMessage(this, 
                "Item ini sudah ada pengembalian, tidak bisa dihapus!");
            return;
        }
        
        String message = "Hapus item ini?\n\n" +
                        "Buku: " + detail.getBuku().getJudul() + "\n" +
                        "Jumlah: " + detail.getJumlahPinjam() + " buku";
        
        if (UIHelper.showConfirmDialog(this, message)) {
            // Jika item existing (punya ID), mark untuk delete
            if (detail.getIdDetail() > 0) {
                detailsToDelete.add(detail);
            }
            
            currentDetails.remove(selectedRow);
            newDetails.remove(detail);
            
            updateItemsTable();
            updateSummary();
            UIHelper.showSuccessMessage(this, "Item berhasil dihapus!");
        }
    }
    
    private void simpanPerubahan() {
        if (currentDetails.isEmpty()) {
            UIHelper.showWarningMessage(this, "Transaksi harus memiliki minimal 1 item!");
            return;
        }
        
        // Konfirmasi
        StringBuilder summary = new StringBuilder();
        summary.append("Ringkasan Perubahan:\n\n");
        summary.append("Total Item: ").append(currentDetails.size()).append("\n");
        summary.append("Item Baru: ").append(newDetails.size()).append("\n");
        summary.append("Item Dihapus: ").append(detailsToDelete.size()).append("\n\n");
        summary.append("Simpan perubahan?");
        
        if (!UIHelper.showConfirmDialog(this, summary.toString())) {
            return;
        }
        
        try {
            // 1. Delete removed items first
            for (DetailTransaksiPeminjaman detail : detailsToDelete) {
                detailDAO.delete(detail.getIdDetail());
            }
            
            // 2. Save/update all current items
            for (DetailTransaksiPeminjaman detail : currentDetails) {
                detail.setTransaksi(transaksi);
                detailDAO.save(detail);
            }
            
            // 3. Update transaction header
            transaksi.setCatatan(txtCatatan.getText().trim());
            int totalBuku = currentDetails.stream().mapToInt(DetailTransaksiPeminjaman::getJumlahPinjam).sum();
            transaksi.setTotalBuku(totalBuku);
            transaksiDAO.save(transaksi);
            
            UIHelper.showSuccessMessage(this, 
                "Perubahan berhasil disimpan!\n\n" +
                "Item ditambah: " + newDetails.size() + "\n" +
                "Item dihapus: " + detailsToDelete.size());
            
            dispose();
            
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error menyimpan perubahan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Dialog untuk edit detail item (jumlah pinjam dan tanggal kembali)
 */
class EditDetailItemDialog extends JDialog {
    private DetailTransaksiPeminjaman detail;
    private boolean updated = false;
    
    private JTextField txtKodeBuku, txtJudul, txtJumlahPinjamLama;
    private JSpinner spinnerJumlahBaru;
    private JDateChooser dateKembali;
    private JButton btnSimpan, btnBatal;
    
    public EditDetailItemDialog(Dialog parent, DetailTransaksiPeminjaman detail) {
        super(parent, "Edit Detail Item", true);
        this.detail = detail;
        
        initComponents();
        setupLayout();
        setupEventListeners();
        populateData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        txtKodeBuku = new JTextField(15);
        txtKodeBuku.setEditable(false);
        txtKodeBuku.setBackground(new Color(240, 240, 240));
        
        txtJudul = new JTextField(25);
        txtJudul.setEditable(false);
        txtJudul.setBackground(new Color(240, 240, 240));
        
        txtJumlahPinjamLama = new JTextField(10);
        txtJumlahPinjamLama.setEditable(false);
        txtJumlahPinjamLama.setBackground(new Color(240, 240, 240));
        
        spinnerJumlahBaru = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        
        dateKembali = new JDateChooser();
        dateKembali.setDateFormatString("dd/MM/yyyy");
        dateKembali.setPreferredSize(new Dimension(150, 25));
        
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");
        
        btnSimpan.setBackground(new Color(40, 167, 69));
        btnSimpan.setForeground(Color.WHITE);
        btnBatal.setBackground(new Color(108, 117, 125));
        btnBatal.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Kode Buku:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtKodeBuku, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Judul Buku:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtJudul, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jumlah Lama:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtJumlahPinjamLama, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jumlah Baru:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(spinnerJumlahBaru, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Tgl Kembali Rencana:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dateKembali, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
    }
    
    private void populateData() {
        txtKodeBuku.setText(detail.getBuku().getKodeBuku());
        txtJudul.setText(detail.getBuku().getJudul());
        txtJumlahPinjamLama.setText(String.valueOf(detail.getJumlahPinjam()));
        spinnerJumlahBaru.setValue(detail.getJumlahPinjam());
        
        if (detail.getTanggalKembaliRencana() != null) {
            dateKembali.setDate(java.sql.Date.valueOf(detail.getTanggalKembaliRencana()));
        }
    }
    
    private void simpan() {
        int jumlahBaru = (Integer) spinnerJumlahBaru.getValue();
        
        if (jumlahBaru <= 0) {
            UIHelper.showWarningMessage(this, "Jumlah harus lebih dari 0!");
            return;
        }
        
        if (jumlahBaru > detail.getBuku().getJumlahTersedia() + detail.getJumlahPinjam()) {
            UIHelper.showWarningMessage(this, 
                "Jumlah melebihi stok tersedia!\n" +
                "Stok: " + (detail.getBuku().getJumlahTersedia() + detail.getJumlahPinjam()));
            return;
        }
        
        if (dateKembali.getDate() == null) {
            UIHelper.showWarningMessage(this, "Tanggal kembali harus diisi!");
            return;
        }
        
        LocalDate tanggalKembali = new java.sql.Date(dateKembali.getDate().getTime()).toLocalDate();
        
        if (tanggalKembali.isBefore(detail.getTransaksi().getTanggalPinjam())) {
            UIHelper.showWarningMessage(this, 
                "Tanggal kembali tidak boleh sebelum tanggal pinjam!");
            return;
        }
        
        detail.setJumlahPinjam(jumlahBaru);
        detail.setTanggalKembaliRencana(tanggalKembali);
        
        updated = true;
        UIHelper.showSuccessMessage(this, "Perubahan disimpan!");
        dispose();
    }
    
    public boolean isUpdated() {
        return updated;
    }
}
