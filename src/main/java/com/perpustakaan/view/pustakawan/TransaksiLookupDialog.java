package com.perpustakaan.view.pustakawan;

import com.perpustakaan.dao.TransaksiPeminjamanDAO;
import com.perpustakaan.model.TransaksiPeminjaman;
import com.perpustakaan.util.UIHelper;
import com.perpustakaan.util.DateTimeHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Dialog untuk memilih transaksi untuk pengembalian
 */
public class TransaksiLookupDialog extends JDialog {
    private TransaksiPeminjamanDAO transaksiDAO;
    private TransaksiPeminjaman selectedTransaksi;
    
    // Components
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField txtSearch;
    private JComboBox<String> cbSearchBy;
    private JButton btnSearch, btnSelect, btnCancel;
    
    public TransaksiLookupDialog(Dialog parent) {
        super(parent, "Pilih Transaksi", true);
        transaksiDAO = new TransaksiPeminjamanDAO();
        selectedTransaksi = null;
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Table
        String[] columns = {"Kode Transaksi", "NIS", "Nama Siswa", 
                           "Tgl Pinjam", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTransaksi = new JTable(tableModel);
        tableTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTransaksi.getColumnModel().getColumn(0).setPreferredWidth(120);
        tableTransaksi.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableTransaksi.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableTransaksi.getColumnModel().getColumn(3).setPreferredWidth(200);
        scrollPane = new JScrollPane(tableTransaksi);
        
        // Search components
        txtSearch = new JTextField(20);
        cbSearchBy = new JComboBox<>(new String[]{
            "Kode Transaksi", "NIS Siswa", "Nama Siswa"
        });
        
        // Buttons
        btnSearch = new JButton("Cari");
        btnSelect = new JButton("Pilih");
        btnCancel = new JButton("Batal");
        
        btnSearch.setBackground(new Color(40, 167, 69));
        btnSearch.setForeground(Color.WHITE);
        btnSelect.setBackground(new Color(40, 167, 69));
        btnSelect.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        
        btnSelect.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Cari Transaksi Aktif"));
        topPanel.add(new JLabel("Cari berdasarkan:"));
        topPanel.add(cbSearchBy);
        topPanel.add(new JLabel("Kata kunci:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.add(new JLabel("* Hanya menampilkan transaksi yang belum dikembalikan"));
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnSelect);
        bottomPanel.add(btnCancel);
        
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.CENTER);
        topContainer.add(infoPanel, BorderLayout.SOUTH);
        
        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Table selection
        tableTransaksi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnSelect.setEnabled(tableTransaksi.getSelectedRow() >= 0);
            }
        });
        
        // Double click to select
        tableTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tableTransaksi.getSelectedRow() >= 0) {
                    selectTransaksi();
                }
            }
        });
        
        // Buttons
        btnSearch.addActionListener(e -> searchTransaksi());
        btnSelect.addActionListener(e -> selectTransaksi());
        btnCancel.addActionListener(e -> dispose());
        
        // Enter key for search
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    searchTransaksi();
                }
            }
        });
    }
    
    private void loadData() {
        try {
            List<TransaksiPeminjaman> transaksiList = transaksiDAO.findByStatus("DIPINJAM");
            populateTable(transaksiList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading transaksi data: " + e.getMessage());
        }
    }
    
    private void populateTable(List<TransaksiPeminjaman> transaksiList) {
        tableModel.setRowCount(0);
        
        for (TransaksiPeminjaman transaksi : transaksiList) {
            String status = "DIPINJAM";
            if (transaksi.isTerlambat()) {
                status = "TERLAMBAT";
            }
            
            Object[] row = {
                transaksi.getKodeTransaksi(),
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNis() : "",
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNamaLengkap() : "",
                DateTimeHelper.formatDate(transaksi.getTanggalPinjam()),
                status
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchTransaksi() {
        String keyword = txtSearch.getText().trim();
        String searchBy = (String) cbSearchBy.getSelectedItem();
        
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<TransaksiPeminjaman> transaksiList = transaksiDAO.findByStatus("DIPINJAM");
            
            // Filter based on search criteria
            transaksiList = transaksiList.stream()
                .filter(transaksi -> matchesSearchCriteria(transaksi, keyword, searchBy))
                .collect(java.util.stream.Collectors.toList());
            
            populateTable(transaksiList);
            
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error searching transaksi: " + e.getMessage());
        }
    }
    
    private boolean matchesSearchCriteria(TransaksiPeminjaman transaksi, String keyword, String searchBy) {
        String lowerKeyword = keyword.toLowerCase();
        
        switch (searchBy) {
            case "Kode Transaksi":
                return transaksi.getKodeTransaksi().toLowerCase().contains(lowerKeyword);
            case "NIS Siswa":
                return transaksi.getSiswa() != null && 
                       transaksi.getSiswa().getNis().toLowerCase().contains(lowerKeyword);
            case "Nama Siswa":
                return transaksi.getSiswa() != null && 
                       transaksi.getSiswa().getNamaLengkap().toLowerCase().contains(lowerKeyword);
            default:
                return false;
        }
    }
    
    private void selectTransaksi() {
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow < 0) return;
        
        String kodeTransaksi = (String) tableModel.getValueAt(selectedRow, 0);
        
        try {
            List<TransaksiPeminjaman> transaksiList = transaksiDAO.findByStatus("DIPINJAM");
            selectedTransaksi = transaksiList.stream()
                .filter(t -> t.getKodeTransaksi().equals(kodeTransaksi))
                .findFirst()
                .orElse(null);
            
            if (selectedTransaksi != null) {
                dispose();
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading selected transaksi: " + e.getMessage());
        }
    }
    
    public TransaksiPeminjaman getSelectedTransaksi() {
        return selectedTransaksi;
    }
}