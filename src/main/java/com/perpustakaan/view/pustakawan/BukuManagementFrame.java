package com.perpustakaan.view.pustakawan;

import com.perpustakaan.dao.BukuDAO;
import com.perpustakaan.dao.KategoriBukuDAO;
import com.perpustakaan.dao.RakDAO;
import com.perpustakaan.model.Buku;
import com.perpustakaan.model.KategoriBuku;
import com.perpustakaan.model.Rak;
import com.perpustakaan.util.UIHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

/**
 * Frame untuk mengelola data buku - Role Pustakawan
 */
public class BukuManagementFrame extends JDialog {
    private static final String COVER_IMAGE_PATH = "covers/";
    
    private BukuDAO bukuDAO;
    private KategoriBukuDAO kategoriDAO;
    private RakDAO rakDAO;
    
    // Components
    private JTable tableBuku;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    // Form components
    private JTextField txtKodeBuku, txtIsbn, txtJudul, txtPengarang, txtPenerbit;
    private JTextField txtTahunTerbit, txtJumlahTotal, txtHarga;
    private JComboBox<KategoriBuku> cbKategori;
    private JComboBox<Rak> cbRak;
    private JTextArea txtDeskripsi;
    private JLabel lblCoverPreview;
    private JButton btnBrowseCover, btnRemoveCover;
    private JButton btnAdd, btnEdit, btnDelete, btnSave, btnCancel, btnRefresh;
    
    // Search components
    private JTextField txtSearch;
    private JComboBox<String> cbSearchBy;
    private JButton btnSearch, btnResetFilter;
    
    // File handling
    private String selectedCoverPath = null;
    
    // Form state
    private boolean isEditing = false;
    private int selectedBukuId = 0;
    
    public BukuManagementFrame(Frame parent) {
        super(parent, "Kelola Data Buku", true);
        
        bukuDAO = new BukuDAO();
        kategoriDAO = new KategoriBukuDAO();
        rakDAO = new RakDAO();
        
        // Create covers directory if not exists
        createCoversDirectory();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadComboBoxData();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1400, 700);
        setLocationRelativeTo(parent);
    }
    
    private void createCoversDirectory() {
        try {
            Path coverPath = Paths.get(COVER_IMAGE_PATH);
            if (!Files.exists(coverPath)) {
                Files.createDirectories(coverPath);
            }
        } catch (IOException e) {
            System.err.println("Error creating covers directory: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // Table
        String[] columns = {"ID", "Kode Buku", "Judul", "Pengarang", "Penerbit", 
                           "Tahun", "Kategori", "Rak", "Total", "Tersedia", "Harga"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBuku = new JTable(tableModel);
        tableBuku.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableBuku.getColumnModel().getColumn(0).setMaxWidth(50);
        tableBuku.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableBuku.getColumnModel().getColumn(2).setPreferredWidth(200);
        scrollPane = new JScrollPane(tableBuku);
        
        // Form components
        txtKodeBuku = new JTextField(20);
        txtIsbn = new JTextField(20);
        txtJudul = new JTextField(25);
        txtPengarang = new JTextField(20);
        txtPenerbit = new JTextField(20);
        txtTahunTerbit = new JTextField(10);
        txtJumlahTotal = new JTextField(10);
        txtHarga = new JTextField(15);
        
        cbKategori = new JComboBox<>();
        cbRak = new JComboBox<>();
        
        txtDeskripsi = new JTextArea(4, 30);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        
        // Cover image components
        lblCoverPreview = new JLabel("No Cover", SwingConstants.CENTER);
        lblCoverPreview.setPreferredSize(new Dimension(120, 160));
        lblCoverPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblCoverPreview.setBackground(Color.LIGHT_GRAY);
        lblCoverPreview.setOpaque(true);
        
        btnBrowseCover = new JButton("Browse Cover");
        btnRemoveCover = new JButton("Remove Cover");
        btnRemoveCover.setEnabled(false);
        
        // Search components
        txtSearch = new JTextField(20);
        cbSearchBy = new JComboBox<>(new String[]{"Semua", "Kode Buku", "Judul", "Pengarang", "ISBN"});
        
        // Action buttons
        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
        btnRefresh = new JButton("Refresh");
        btnSearch = new JButton("Cari");
        btnResetFilter = new JButton("Reset Filter");
        
        // Button styling
        setupButtonColors();
        
        // Initial state
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        setFormEnabled(false);
    }
    
    private void setupButtonColors() {
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(255, 193, 7));
        btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(23, 162, 184));
        btnRefresh.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(40, 167, 69));
        btnSearch.setForeground(Color.WHITE);
        btnResetFilter.setBackground(new Color(108, 117, 125));
        btnResetFilter.setForeground(Color.WHITE);
        btnBrowseCover.setBackground(new Color(23, 162, 184));
        btnBrowseCover.setForeground(Color.WHITE);
        btnRemoveCover.setBackground(new Color(220, 53, 69));
        btnRemoveCover.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Cari berdasarkan:"));
        topPanel.add(cbSearchBy);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnResetFilter);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnDelete);
        topPanel.add(btnRefresh);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Main layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Data Buku"));
        
        // Left panel - form fields
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        leftPanel.add(new JLabel("Kode Buku:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtKodeBuku, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtIsbn, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Judul:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtJudul, gbc);
        gbc.gridwidth = 1;
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Pengarang:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtPengarang, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Penerbit:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtPenerbit, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Tahun Terbit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtTahunTerbit, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtHarga, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(cbKategori, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Rak:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(cbRak, gbc);
        
        // Row 5
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Jumlah Total:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(txtJumlahTotal, gbc);
        
        // Row 6
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        leftPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(new JScrollPane(txtDeskripsi), gbc);
        
        // Right panel - cover image
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(5, 5, 5, 5);
        
        gbcRight.gridx = 0; gbcRight.gridy = 0;
        rightPanel.add(new JLabel("Cover Buku:"), gbcRight);
        
        gbcRight.gridx = 0; gbcRight.gridy = 1;
        rightPanel.add(lblCoverPreview, gbcRight);
        
        JPanel coverButtonPanel = new JPanel(new FlowLayout());
        coverButtonPanel.add(btnBrowseCover);
        coverButtonPanel.add(btnRemoveCover);
        gbcRight.gridx = 0; gbcRight.gridy = 2;
        rightPanel.add(coverButtonPanel, gbcRight);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        // Combine panels
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(leftPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);
        
        formPanel.add(contentPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }
    
    private void setupEventListeners() {
        // Table selection
        tableBuku.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableBuku.getSelectedRow();
                btnEdit.setEnabled(selectedRow >= 0);
                btnDelete.setEnabled(selectedRow >= 0);
            }
        });
        
        // Action buttons
        btnAdd.addActionListener(e -> startAddMode());
        btnEdit.addActionListener(e -> startEditMode());
        btnDelete.addActionListener(e -> deleteBuku());
        btnSave.addActionListener(e -> saveBuku());
        btnCancel.addActionListener(e -> cancelForm());
        btnRefresh.addActionListener(e -> loadData());
        
        // Search functionality
        btnSearch.addActionListener(e -> searchBuku());
        btnResetFilter.addActionListener(e -> resetFilter());
        
        // Cover image buttons
        btnBrowseCover.addActionListener(e -> browseCoverImage());
        btnRemoveCover.addActionListener(e -> removeCoverImage());
        
        // Enter key for search
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    searchBuku();
                }
            }
        });
    }
    
    private void loadComboBoxData() {
        try {
            // Load kategori
            List<KategoriBuku> kategoriList = kategoriDAO.findAll();
            cbKategori.removeAllItems();
            for (KategoriBuku kategori : kategoriList) {
                cbKategori.addItem(kategori);
            }
            
            // Load rak
            List<Rak> rakList = rakDAO.findAll();
            cbRak.removeAllItems();
            for (Rak rak : rakList) {
                cbRak.addItem(rak);
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading combo box data: " + e.getMessage());
        }
    }
    
    private void loadData() {
        try {
            List<Buku> bukuList = bukuDAO.findAll();
            populateTable(bukuList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading data: " + e.getMessage());
        }
    }
    
    private void populateTable(List<Buku> bukuList) {
        tableModel.setRowCount(0);
        
        for (Buku buku : bukuList) {
            Object[] row = {
                buku.getIdBuku(),
                buku.getKodeBuku(),
                buku.getJudul(),
                buku.getPengarang(),
                buku.getPenerbit(),
                buku.getTahunTerbit(),
                buku.getKategori() != null ? buku.getKategori().getNamaKategori() : "",
                buku.getRak() != null ? buku.getRak().getKodeRak() : "",
                buku.getJumlahTotal(),
                buku.getJumlahTersedia(),
                buku.getHarga() != null ? "Rp " + buku.getHarga() : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchBuku() {
        String keyword = txtSearch.getText().trim();
        String searchBy = (String) cbSearchBy.getSelectedItem();
        
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<Buku> bukuList;
            
            if ("Semua".equals(searchBy)) {
                bukuList = bukuDAO.searchBooks(keyword);
            } else {
                // For specific search, we'll use the general search method
                // Could be enhanced with specific search methods in DAO
                bukuList = bukuDAO.searchBooks(keyword);
            }
            
            populateTable(bukuList);
            
            UIHelper.showSuccessMessage(this, 
                "Ditemukan " + bukuList.size() + " buku dengan kata kunci: " + keyword);
                
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error searching data: " + e.getMessage());
        }
    }
    
    private void resetFilter() {
        cbSearchBy.setSelectedIndex(0);
        txtSearch.setText("");
        loadData();
    }
    
    private void browseCoverImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Cover Buku");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try {
                // Validate file size (max 5MB)
                long fileSizeInMB = selectedFile.length() / (1024 * 1024);
                if (fileSizeInMB > 5) {
                    UIHelper.showWarningMessage(this, "Ukuran file terlalu besar! Maksimal 5MB.");
                    return;
                }
                
                selectedCoverPath = selectedFile.getAbsolutePath();
                displayCoverPreview(selectedCoverPath);
                btnRemoveCover.setEnabled(true);
                
            } catch (Exception e) {
                UIHelper.showErrorMessage(this, "Error loading image: " + e.getMessage());
            }
        }
    }
    
    private void removeCoverImage() {
        selectedCoverPath = null;
        lblCoverPreview.setIcon(null);
        lblCoverPreview.setText("No Cover");
        btnRemoveCover.setEnabled(false);
    }
    
    private void displayCoverPreview(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                lblCoverPreview.setIcon(new ImageIcon(scaledImg));
                lblCoverPreview.setText("");
            } else {
                lblCoverPreview.setIcon(null);
                lblCoverPreview.setText("No Cover");
            }
        } catch (Exception e) {
            lblCoverPreview.setIcon(null);
            lblCoverPreview.setText("Error loading image");
        }
    }
    
    private void startAddMode() {
        isEditing = false;
        selectedBukuId = 0;
        clearForm();
        setFormEnabled(true);
        btnSave.setEnabled(true);
        btnCancel.setEnabled(true);
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        txtKodeBuku.requestFocus();
    }
    
    private void startEditMode() {
        int selectedRow = tableBuku.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showWarningMessage(this, "Pilih data yang akan diedit!");
            return;
        }
        
        selectedBukuId = (int) tableModel.getValueAt(selectedRow, 0);
        isEditing = true;
        
        try {
            Buku buku = bukuDAO.findById(selectedBukuId);
            if (buku != null) {
                populateForm(buku);
                setFormEnabled(true);
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
                btnAdd.setEnabled(false);
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                txtJudul.requestFocus();
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading data: " + e.getMessage());
        }
    }
    
    private void deleteBuku() {
        int selectedRow = tableBuku.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showWarningMessage(this, "Pilih data yang akan dihapus!");
            return;
        }
        
        String judul = (String) tableModel.getValueAt(selectedRow, 2);
        if (UIHelper.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus buku: " + judul + "?")) {
            
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                bukuDAO.delete(id);
                UIHelper.showSuccessMessage(this, "Data berhasil dihapus!");
                loadData();
            } catch (SQLException e) {
                UIHelper.showErrorMessage(this, "Error deleting data: " + e.getMessage());
            }
        }
    }
    
    private void saveBuku() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Buku buku = new Buku();
            
            if (isEditing) {
                buku.setIdBuku(selectedBukuId);
            }
            
            // Set basic data
            buku.setKodeBuku(txtKodeBuku.getText().trim());
            buku.setIsbn(txtIsbn.getText().trim());
            buku.setJudul(txtJudul.getText().trim());
            buku.setPengarang(txtPengarang.getText().trim());
            buku.setPenerbit(txtPenerbit.getText().trim());
            buku.setTahunTerbit(Integer.parseInt(txtTahunTerbit.getText().trim()));
            buku.setJumlahTotal(Integer.parseInt(txtJumlahTotal.getText().trim()));
            
            if (isEditing) {
                // Keep existing jumlah_tersedia for editing
                Buku existingBuku = bukuDAO.findById(selectedBukuId);
                buku.setJumlahTersedia(existingBuku.getJumlahTersedia());
            } else {
                // For new book, set tersedia = total
                buku.setJumlahTersedia(buku.getJumlahTotal());
            }
            
            buku.setKategori((KategoriBuku) cbKategori.getSelectedItem());
            buku.setRak((Rak) cbRak.getSelectedItem());
            buku.setDeskripsi(txtDeskripsi.getText().trim());
            
            // Handle price
            String hargaText = txtHarga.getText().trim();
            if (!hargaText.isEmpty()) {
                buku.setHarga(new BigDecimal(hargaText));
            }
            
            // Handle cover image
            if (selectedCoverPath != null) {
                String savedImagePath = saveCoverImage();
                buku.setCoverImage(savedImagePath);
            }
            
            bukuDAO.save(buku);
            
            UIHelper.showSuccessMessage(this, 
                isEditing ? "Data berhasil diperbarui!" : "Data berhasil ditambahkan!");
            
            cancelForm();
            loadData();
            
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error saving data: " + e.getMessage());
        }
    }
    
    private String saveCoverImage() throws IOException {
        if (selectedCoverPath == null) return null;
        
        File sourceFile = new File(selectedCoverPath);
        String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        Path targetPath = Paths.get(COVER_IMAGE_PATH + fileName);
        
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }
    
    private boolean validateForm() {
        if (txtKodeBuku.getText().trim().isEmpty()) {
            UIHelper.showWarningMessage(this, "Kode buku tidak boleh kosong!");
            txtKodeBuku.requestFocus();
            return false;
        }
        
        if (txtJudul.getText().trim().isEmpty()) {
            UIHelper.showWarningMessage(this, "Judul tidak boleh kosong!");
            txtJudul.requestFocus();
            return false;
        }
        
        if (txtPengarang.getText().trim().isEmpty()) {
            UIHelper.showWarningMessage(this, "Pengarang tidak boleh kosong!");
            txtPengarang.requestFocus();
            return false;
        }
        
        try {
            int tahun = Integer.parseInt(txtTahunTerbit.getText().trim());
            if (tahun < 1900 || tahun > java.time.Year.now().getValue()) {
                UIHelper.showWarningMessage(this, "Tahun terbit tidak valid!");
                txtTahunTerbit.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            UIHelper.showWarningMessage(this, "Tahun terbit harus berupa angka!");
            txtTahunTerbit.requestFocus();
            return false;
        }
        
        try {
            int jumlah = Integer.parseInt(txtJumlahTotal.getText().trim());
            if (jumlah <= 0) {
                UIHelper.showWarningMessage(this, "Jumlah total harus lebih dari 0!");
                txtJumlahTotal.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            UIHelper.showWarningMessage(this, "Jumlah total harus berupa angka!");
            txtJumlahTotal.requestFocus();
            return false;
        }
        
        if (cbKategori.getSelectedItem() == null) {
            UIHelper.showWarningMessage(this, "Kategori harus dipilih!");
            return false;
        }
        
        if (cbRak.getSelectedItem() == null) {
            UIHelper.showWarningMessage(this, "Rak harus dipilih!");
            return false;
        }
        
        // Validate kode buku uniqueness
        try {
            if (bukuDAO.isKodeBukuExists(txtKodeBuku.getText().trim(), selectedBukuId)) {
                UIHelper.showWarningMessage(this, "Kode buku sudah terdaftar!");
                txtKodeBuku.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error validating kode buku: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    private void populateForm(Buku buku) {
        txtKodeBuku.setText(buku.getKodeBuku());
        txtIsbn.setText(buku.getIsbn() != null ? buku.getIsbn() : "");
        txtJudul.setText(buku.getJudul());
        txtPengarang.setText(buku.getPengarang());
        txtPenerbit.setText(buku.getPenerbit() != null ? buku.getPenerbit() : "");
        txtTahunTerbit.setText(String.valueOf(buku.getTahunTerbit()));
        txtJumlahTotal.setText(String.valueOf(buku.getJumlahTotal()));
        txtHarga.setText(buku.getHarga() != null ? buku.getHarga().toString() : "");
        txtDeskripsi.setText(buku.getDeskripsi() != null ? buku.getDeskripsi() : "");
        
        // Set combo boxes
        if (buku.getKategori() != null) {
            for (int i = 0; i < cbKategori.getItemCount(); i++) {
                KategoriBuku item = cbKategori.getItemAt(i);
                if (item.getIdKategori() == buku.getKategori().getIdKategori()) {
                    cbKategori.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        if (buku.getRak() != null) {
            for (int i = 0; i < cbRak.getItemCount(); i++) {
                Rak item = cbRak.getItemAt(i);
                if (item.getIdRak() == buku.getRak().getIdRak()) {
                    cbRak.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Display cover image
        if (buku.getCoverImage() != null && !buku.getCoverImage().isEmpty()) {
            String coverPath = COVER_IMAGE_PATH + buku.getCoverImage();
            displayCoverPreview(coverPath);
            btnRemoveCover.setEnabled(true);
        }
    }
    
    private void clearForm() {
        txtKodeBuku.setText("");
        txtIsbn.setText("");
        txtJudul.setText("");
        txtPengarang.setText("");
        txtPenerbit.setText("");
        txtTahunTerbit.setText("");
        txtJumlahTotal.setText("");
        txtHarga.setText("");
        txtDeskripsi.setText("");
        cbKategori.setSelectedIndex(-1);
        cbRak.setSelectedIndex(-1);
        removeCoverImage();
    }
    
    private void setFormEnabled(boolean enabled) {
        txtKodeBuku.setEnabled(enabled);
        txtIsbn.setEnabled(enabled);
        txtJudul.setEnabled(enabled);
        txtPengarang.setEnabled(enabled);
        txtPenerbit.setEnabled(enabled);
        txtTahunTerbit.setEnabled(enabled);
        txtJumlahTotal.setEnabled(enabled);
        txtHarga.setEnabled(enabled);
        txtDeskripsi.setEnabled(enabled);
        cbKategori.setEnabled(enabled);
        cbRak.setEnabled(enabled);
        btnBrowseCover.setEnabled(enabled);
        btnRemoveCover.setEnabled(enabled && selectedCoverPath != null);
    }
    
    private void cancelForm() {
        isEditing = false;
        selectedBukuId = 0;
        clearForm();
        setFormEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}