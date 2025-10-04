package com.perpustakaan.view.pustakawan;

import com.perpustakaan.dao.*;
import com.perpustakaan.model.*;
import com.perpustakaan.util.UIHelper;
import com.perpustakaan.util.UserSession;
import com.perpustakaan.util.DateTimeHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced TransaksiPeminjamanFrame - Support Multiple Items
 */
public class TransaksiPeminjamanFrame extends JDialog {
    private TransaksiPeminjamanDAO transaksiDAO;
    private DetailTransaksiPeminjamanDAO detailDAO;
    private SiswaDAO siswaDAO;
    private BukuDAO bukuDAO;
    private PustakawanDAO pustakawanDAO;
    private StatusTransaksiDAO statusDAO;
    
    // Components
    private JTable tableTransaksi;
    private DefaultTableModel transaksiModel;
    private JTable tableDetailItems;
    private DefaultTableModel detailItemsModel;
    private JScrollPane scrollTransaksi, scrollDetailItems;
    
    // Form components
    private JTextField txtNisSiswa, txtNamaSiswa, txtKelasSiswa;
    private JTextField txtTanggalPinjam, txtTanggalKembali;
    private JTextArea txtCatatan;
    private JButton btnCariSiswa, btnTambahItem, btnHapusItem, btnPinjam, btnCancel, btnRefresh;
    private JButton btnTambahTransaksi;
    private JButton btnLihatDetail, btnEditTransaksi; 
    private JPanel formPanel;
    private JTable tableSelectedItems;
    private DefaultTableModel selectedItemModel;


    
    // Selected data
    private Siswa selectedSiswa;
    private Pustakawan currentPustakawan;
    private List<DetailTransaksiPeminjaman> selectedItems;
    
    public TransaksiPeminjamanFrame(Frame parent) {
        super(parent, "Kelola Transaksi Peminjaman (Multiple Items)", true);
        
        transaksiDAO = new TransaksiPeminjamanDAO();
        detailDAO = new DetailTransaksiPeminjamanDAO();
        siswaDAO = new SiswaDAO();
        bukuDAO = new BukuDAO();
        pustakawanDAO = new PustakawanDAO();
        statusDAO = new StatusTransaksiDAO();
        
        selectedItems = new ArrayList<>();
        loadCurrentPustakawan();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1500, 800);
        setLocationRelativeTo(parent);
    }
    
    private void loadCurrentPustakawan() {
        try {
            UserSession session = UserSession.getInstance();
            if (session.isLoggedIn()) {
                currentPustakawan = pustakawanDAO.findByUserId(session.getCurrentUserId());
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading current pustakawan: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // Main transaction table
        String[] transaksiColumns = {"ID", "Kode Transaksi", "NIS", "Nama Siswa", 
                                    "Total Buku", "Belum Kembali", "Status", "Total Denda"};
        transaksiModel = new DefaultTableModel(transaksiColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTransaksi = new JTable(transaksiModel);
        tableTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollTransaksi = new JScrollPane(tableTransaksi);
        scrollTransaksi.setPreferredSize(new Dimension(800, 200));
        
        // Detail items table for selected transaction
        String[] detailColumns = {"ID Detail", "Kode Buku", "Judul Buku", "Jml Pinjam", 
                                 "Jml Kembali", "Belum Kembali", "Tgl Kembali", "Status Detail", "Denda"};
        detailItemsModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDetailItems = new JTable(detailItemsModel);
        tableDetailItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollDetailItems = new JScrollPane(tableDetailItems);
        scrollDetailItems.setPreferredSize(new Dimension(800, 150));
        
        // Form components
        txtNisSiswa = new JTextField(15);
        txtNisSiswa.setEditable(false);
        txtNamaSiswa = new JTextField(20);
        txtNamaSiswa.setEditable(false);
        txtKelasSiswa = new JTextField(10);
        txtKelasSiswa.setEditable(false);
        
        txtTanggalPinjam = new JTextField(12);
        txtTanggalPinjam.setText(DateTimeHelper.formatDate(LocalDate.now()));
        txtTanggalPinjam.setEditable(false);
        
        txtTanggalKembali = new JTextField(12);
        txtTanggalKembali.setEditable(false);
        
        txtCatatan = new JTextArea(3, 30);
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        
        // Buttons
        btnCariSiswa = new JButton("Cari Siswa");
        btnTambahItem = new JButton("Tambah Buku");
        btnHapusItem = new JButton("Hapus Item");
        btnPinjam = new JButton("Proses Peminjaman");
        btnCancel = new JButton("Batal");
        btnRefresh = new JButton("Refresh");
        btnLihatDetail = new JButton("Lihat Detail");
        btnEditTransaksi = new JButton("Edit Transaksi");
        
        // Button styling
        setupButtonColors();
        
        // Initial state
        btnTambahItem.setEnabled(false);
        btnHapusItem.setEnabled(false);
        btnPinjam.setEnabled(false);
        btnCancel.setEnabled(false);
        btnLihatDetail.setEnabled(false);
        btnEditTransaksi.setEnabled(false);
    }
    
    private void setupButtonColors() {
        btnCariSiswa.setBackground(new Color(40, 167, 69));
        btnCariSiswa.setForeground(Color.WHITE);
        btnTambahItem.setBackground(new Color(40, 167, 69));
        btnTambahItem.setForeground(Color.WHITE);
        btnHapusItem.setBackground(new Color(220, 53, 69));
        btnHapusItem.setForeground(Color.WHITE);
        btnPinjam.setBackground(new Color(40, 167, 69));
        btnPinjam.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(23, 162, 184));
        btnRefresh.setForeground(Color.WHITE);
        btnLihatDetail.setBackground(new Color(23, 162, 184));
        btnLihatDetail.setForeground(Color.WHITE);
        btnEditTransaksi.setBackground(new Color(255, 193, 7));
        btnEditTransaksi.setForeground(Color.BLACK);
    }
    
private void setupLayout() {
    setLayout(new BorderLayout());

    // Top panel - transaction list
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBorder(BorderFactory.createTitledBorder("Daftar Transaksi Aktif"));
    topPanel.add(scrollTransaksi, BorderLayout.CENTER);

    JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    btnTambahTransaksi = new JButton("Tambah Transaksi Baru"); // <- simpan ke field
    topButtonPanel.add(btnTambahTransaksi);
    topButtonPanel.add(btnLihatDetail);      // TAMBAHKAN
    topButtonPanel.add(btnEditTransaksi); 
    topButtonPanel.add(btnRefresh);
    topPanel.add(topButtonPanel, BorderLayout.SOUTH);

    // Middle panel - detail items of selected transaction
    JPanel middlePanel = new JPanel(new BorderLayout());
    middlePanel.setBorder(BorderFactory.createTitledBorder("Detail Item Transaksi Terpilih"));
    middlePanel.add(scrollDetailItems, BorderLayout.CENTER);

    JPanel middleButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    middlePanel.add(middleButtonPanel, BorderLayout.SOUTH);

    // Bottom panel - form for new transaction
    formPanel = createFormPanel(); // <- simpan ke field
    formPanel.setVisible(false);   // Initially hidden

    // Split panes for better layout
    JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, middlePanel);
    topSplit.setDividerLocation(250);

    JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, formPanel);
    mainSplit.setDividerLocation(450);

    add(mainSplit, BorderLayout.CENTER);
}

    
private JPanel createFormPanel() {
    formPanel = new JPanel(new BorderLayout());
    formPanel.setBorder(BorderFactory.createTitledBorder("Form Peminjaman Baru (Multiple Items)"));

    // Student info panel
    JPanel siswaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    siswaPanel.add(new JLabel("NIS:"));
    siswaPanel.add(txtNisSiswa);
    siswaPanel.add(new JLabel("Nama:"));
    siswaPanel.add(txtNamaSiswa);
    siswaPanel.add(new JLabel("Kelas:"));
    siswaPanel.add(txtKelasSiswa);
    siswaPanel.add(btnCariSiswa);

    // Items panel with table for selected books
    JPanel itemsPanel = new JPanel(new BorderLayout());
    itemsPanel.setBorder(BorderFactory.createTitledBorder("Buku yang Dipilih"));

    String[] selectedItemColumns = {"Kode Buku", "Judul", "Pengarang", "Jumlah", "Tgl Kembali", "Aksi"};
    selectedItemModel = new DefaultTableModel(selectedItemColumns, 0);
    tableSelectedItems = new JTable(selectedItemModel); // ← simpan ke field

    JScrollPane scrollSelectedItems = new JScrollPane(tableSelectedItems);
    scrollSelectedItems.setPreferredSize(new Dimension(600, 120));

    JPanel itemsButtonPanel = new JPanel(new FlowLayout());
    itemsButtonPanel.add(btnTambahItem);
    itemsButtonPanel.add(btnHapusItem);

    itemsPanel.add(scrollSelectedItems, BorderLayout.CENTER);
    itemsPanel.add(itemsButtonPanel, BorderLayout.SOUTH);

    // Date and notes panel
    JPanel infoPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0; gbc.gridy = 0;
    infoPanel.add(new JLabel("Tgl Pinjam:"), gbc);
    gbc.gridx = 1;
    infoPanel.add(txtTanggalPinjam, gbc);

    gbc.gridx = 2;
    infoPanel.add(new JLabel("Default Tgl Kembali:"), gbc);
    gbc.gridx = 3;
    infoPanel.add(txtTanggalKembali, gbc);

    gbc.gridx = 0; gbc.gridy = 1;
    infoPanel.add(new JLabel("Catatan:"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
    infoPanel.add(new JScrollPane(txtCatatan), gbc);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(btnPinjam);
    buttonPanel.add(btnCancel);

    // Combine all panels
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.add(siswaPanel, BorderLayout.NORTH);
    contentPanel.add(itemsPanel, BorderLayout.CENTER);

    JPanel bottomInfo = new JPanel(new BorderLayout());
    bottomInfo.add(infoPanel, BorderLayout.CENTER);
    bottomInfo.add(buttonPanel, BorderLayout.SOUTH);

    contentPanel.add(bottomInfo, BorderLayout.SOUTH);
    formPanel.add(contentPanel, BorderLayout.CENTER);

    return formPanel;
}

private void setupEventListeners() {
    // Transaction table selection - show details
tableTransaksi.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        int selectedRow = tableTransaksi.getSelectedRow();
        btnLihatDetail.setEnabled(selectedRow >= 0);
        btnEditTransaksi.setEnabled(selectedRow >= 0);
        loadSelectedTransactionDetails();
    }
});

    // Form buttons
    btnCariSiswa.addActionListener(e -> cariSiswa());
    btnTambahItem.addActionListener(e -> tambahBukuItem());
    btnHapusItem.addActionListener(e -> hapusItem());
    btnPinjam.addActionListener(e -> prosesPeminjamanMultiple());
    btnCancel.addActionListener(e -> hideForm());
    btnRefresh.addActionListener(e -> loadData());
    btnLihatDetail.addActionListener(e -> lihatDetailTransaksi());
    btnEditTransaksi.addActionListener(e -> editTransaksi());

    // Tombol tambah transaksi baru
    btnTambahTransaksi.addActionListener(e -> showForm()); // <- ini yang tadinya error
}

private void lihatDetailTransaksi() {
    int selectedRow = tableTransaksi.getSelectedRow();
    if (selectedRow < 0) {
        UIHelper.showWarningMessage(this, "Pilih transaksi yang akan dilihat!");
        return;
    }
    
    try {
        int idTransaksi = (int) transaksiModel.getValueAt(selectedRow, 0);
        TransaksiPeminjaman transaksi = transaksiDAO.findByIdWithDetails(idTransaksi);
        
        if (transaksi != null) {
            EnhancedTransaksiDetailDialog dialog = new EnhancedTransaksiDetailDialog(this, transaksi);
            dialog.setVisible(true);
        }
    } catch (SQLException e) {
        UIHelper.showErrorMessage(this, "Error loading detail: " + e.getMessage());
    }
}

private void editTransaksi() {
    int selectedRow = tableTransaksi.getSelectedRow();
    if (selectedRow < 0) {
        UIHelper.showWarningMessage(this, "Pilih transaksi yang akan diedit!");
        return;
    }
    
    try {
        int idTransaksi = (int) transaksiModel.getValueAt(selectedRow, 0);
        TransaksiPeminjaman transaksi = transaksiDAO.findByIdWithDetails(idTransaksi);
        
        if (transaksi != null) {
            // Validasi: hanya transaksi AKTIF yang bisa diedit
            if (!"AKTIF".equals(transaksi.getStatusKeseluruhan())) {
                UIHelper.showWarningMessage(this, 
                    "Hanya transaksi dengan status AKTIF yang bisa diedit!\n" +
                    "Status saat ini: " + transaksi.getStatusKeseluruhan());
                return;
            }
            
            EditTransaksiDialog dialog = new EditTransaksiDialog(this, transaksi);
            dialog.setVisible(true);
            
            // Reload setelah edit
            loadData();
        }
    } catch (SQLException e) {
        UIHelper.showErrorMessage(this, "Error loading transaksi: " + e.getMessage());
    }
}
    
    private void loadData() {
        try {
            List<TransaksiPeminjaman> transaksiList = transaksiDAO.findTransactionsWithPendingReturns();
            populateTransaksiTable(transaksiList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading data: " + e.getMessage());
        }
    }
    
    private void populateTransaksiTable(List<TransaksiPeminjaman> transaksiList) {
        transaksiModel.setRowCount(0);
        
        for (TransaksiPeminjaman transaksi : transaksiList) {
            Object[] row = {
                transaksi.getIdTransaksi(),
                transaksi.getKodeTransaksi(),
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNis() : "",
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNamaLengkap() : "",
                transaksi.getTotalBuku(),
                transaksi.getTotalBukuBelumKembali(),
                transaksi.getStatusKeseluruhan(),
                "Rp " + transaksi.getTotalDenda()
            };
            transaksiModel.addRow(row);
        }
    }
    
    private void loadSelectedTransactionDetails() {
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow < 0) {
            detailItemsModel.setRowCount(0);
            return;
        }
        
        try {
            int idTransaksi = (int) transaksiModel.getValueAt(selectedRow, 0);
            List<DetailTransaksiPeminjaman> details = detailDAO.findByTransaksiId(idTransaksi);
            populateDetailItemsTable(details);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading details: " + e.getMessage());
        }
    }
    
    private void populateDetailItemsTable(List<DetailTransaksiPeminjaman> details) {
        detailItemsModel.setRowCount(0);
        
        for (DetailTransaksiPeminjaman detail : details) {
            Object[] row = {
                detail.getIdDetail(),
                detail.getBuku() != null ? detail.getBuku().getKodeBuku() : "",
                detail.getBuku() != null ? detail.getBuku().getJudul() : "",
                detail.getJumlahPinjam(),
                detail.getJumlahKembali(),
                detail.getJumlahBelumKembali(),
                DateTimeHelper.formatDate(detail.getTanggalKembaliRencana()),
                detail.getStatusDetail(),
                "Rp " + detail.getTotalDendaItem()
            };
            detailItemsModel.addRow(row);
        }
    }
    
    private void showForm() {
        clearForm();
        formPanel.setVisible(true);
        btnCancel.setEnabled(true);

        LocalDate today = LocalDate.now();
        txtTanggalPinjam.setText(DateTimeHelper.formatDate(today));
        txtTanggalKembali.setText(DateTimeHelper.formatDate(today.plusWeeks(1)));

        // force JSplitPane expand
        Container parent = formPanel.getParent();
        if (parent instanceof JSplitPane split) {
            split.setDividerLocation(0.6); // atur supaya form keliatan
        }

        revalidate();
        repaint();
    }

    
    private void hideForm() {
        formPanel.setVisible(false);  // <- langsung pakai field
        btnCancel.setEnabled(false);
        clearForm();
        revalidate();
        repaint();
    }

    private void clearForm() {
        txtNisSiswa.setText("");
        txtNamaSiswa.setText("");
        txtKelasSiswa.setText("");
        txtCatatan.setText("");
        selectedSiswa = null;
        selectedItems.clear();
        updateFormState();
    }
    
    private void cariSiswa() {
        SiswaLookupDialog dialog = new SiswaLookupDialog(this);
        dialog.setVisible(true);
        
        if (dialog.getSelectedSiswa() != null) {
            selectedSiswa = dialog.getSelectedSiswa();
            txtNisSiswa.setText(selectedSiswa.getNis());
            txtNamaSiswa.setText(selectedSiswa.getNamaLengkap());
            txtKelasSiswa.setText(selectedSiswa.getKelas() != null ? selectedSiswa.getKelas().getNamaKelas() : "");
            updateFormState();
        }
    }

    
    private void tambahBukuItem() {
    // FIXED: Buat instance baru setiap kali dialog dibuka
    MultipleBookSelectionDialog dialog = new MultipleBookSelectionDialog(this, selectedItems);
    dialog.setVisible(true);
    
    if (dialog.getSelectedItems() != null && !dialog.getSelectedItems().isEmpty()) {
        for (BookSelectionItem item : dialog.getSelectedItems()) {
            DetailTransaksiPeminjaman detail = new DetailTransaksiPeminjaman();
            detail.setBuku(item.getBuku());
            detail.setJumlahPinjam(item.getJumlah());
            
            LocalDate dueDate = DateTimeHelper.parseDate(txtTanggalKembali.getText());
            if (dueDate == null) {
                dueDate = LocalDate.now().plusWeeks(1);
            }
            detail.setTanggalKembaliRencana(dueDate);
            
            try {
                StatusTransaksi status = statusDAO.findByName("DIPINJAM");
                detail.setStatus(status);
            } catch (SQLException e) {
                UIHelper.showErrorMessage(this, "Error loading status: " + e.getMessage());
            }
            
            selectedItems.add(detail);
        }
        updateSelectedItemsTable();
        updateFormState();
    }
}
    
    
    private void hapusItem() {
        // Implementation for removing selected item from the list
        int selectedRow = getSelectedItemsTable().getSelectedRow();
        if (selectedRow >= 0 && selectedRow < selectedItems.size()) {
            selectedItems.remove(selectedRow);
            updateSelectedItemsTable();
            updateFormState();
        }
    }
    
    private void updateSelectedItemsTable() {
        // Get the selected items table from the form panel
        JTable tableSelectedItems = getSelectedItemsTable();
        DefaultTableModel model = (DefaultTableModel) tableSelectedItems.getModel();
        model.setRowCount(0);
        
        for (DetailTransaksiPeminjaman detail : selectedItems) {
            Object[] row = {
                detail.getBuku().getKodeBuku(),
                detail.getBuku().getJudul(),
                detail.getBuku().getPengarang(),
                detail.getJumlahPinjam(),
                DateTimeHelper.formatDate(detail.getTanggalKembaliRencana()),
                "Hapus"
            };
            model.addRow(row);
        }
    }
    
private JTable getSelectedItemsTable() {
    return tableSelectedItems; // ← langsung return field
}

    
    private void updateFormState() {
        btnTambahItem.setEnabled(selectedSiswa != null);
        btnHapusItem.setEnabled(!selectedItems.isEmpty());
        btnPinjam.setEnabled(selectedSiswa != null && !selectedItems.isEmpty());
    }
    
    private void prosesPeminjamanMultiple() {
        if (selectedSiswa == null || selectedItems.isEmpty()) {
            UIHelper.showWarningMessage(this, "Pilih siswa dan minimal satu buku!");
            return;
        }
        
        if (currentPustakawan == null) {
            UIHelper.showErrorMessage(this, "Data pustakawan tidak ditemukan!");
            return;
        }
        
        // Validate stock availability for all items
        for (DetailTransaksiPeminjaman detail : selectedItems) {
            if (detail.getBuku().getJumlahTersedia() < detail.getJumlahPinjam()) {
                UIHelper.showWarningMessage(this, 
                    "Stok tidak mencukupi untuk buku: " + detail.getBuku().getJudul() + 
                    "\nTersedia: " + detail.getBuku().getJumlahTersedia() + 
                    ", Diminta: " + detail.getJumlahPinjam());
                return;
            }
        }
        
        // Check student's active loans limit
        try {
            int activeLoanCount = transaksiDAO.countActiveLoansBySiswa(selectedSiswa.getIdSiswa());
            int totalRequestedBooks = selectedItems.stream().mapToInt(DetailTransaksiPeminjaman::getJumlahPinjam).sum();
            
            if (activeLoanCount + totalRequestedBooks > 5) { // Max 5 books
                UIHelper.showWarningMessage(this, 
                    "Siswa sudah meminjam " + activeLoanCount + " buku. " +
                    "Maksimal total 5 buku. Permintaan saat ini: " + totalRequestedBooks + " buku.");
                return;
            }
            
            // Confirmation
            String itemsList = selectedItems.stream()
                .map(d -> "- " + d.getBuku().getJudul() + " (x" + d.getJumlahPinjam() + ")")
                .collect(java.util.stream.Collectors.joining("\n"));
                
            String confirmMessage = "Konfirmasi Peminjaman Multiple Items:\n\n" +
                "Siswa: " + selectedSiswa.getNamaLengkap() + " (" + selectedSiswa.getNis() + ")\n" +
                "Total Buku: " + totalRequestedBooks + "\n\n" +
                "Daftar Buku:\n" + itemsList + "\n\n" +
                "Proses peminjaman?";
            
            if (!UIHelper.showConfirmDialog(this, confirmMessage)) {
                return;
            }
            
            // Create transaction
            TransaksiPeminjaman transaksi = new TransaksiPeminjaman();
            transaksi.setKodeTransaksi(transaksiDAO.generateKodeTransaksi());
            transaksi.setSiswa(selectedSiswa);
            transaksi.setPustakawan(currentPustakawan);
            
            LocalDate tanggalPinjam = DateTimeHelper.parseDate(txtTanggalPinjam.getText());
            if (tanggalPinjam == null) {
                tanggalPinjam = LocalDate.now();
            }
            transaksi.setTanggalPinjam(tanggalPinjam);
            
            transaksi.setCatatan(txtCatatan.getText().trim());
            transaksi.setTotalBuku(totalRequestedBooks);
            transaksi.setTotalDenda(BigDecimal.ZERO);
            transaksi.setStatusKeseluruhan("AKTIF");
            
            // Set detail items
            transaksi.setDetailItems(selectedItems);
            
            // Save transaction with details
            transaksiDAO.saveWithDetails(transaksi);
            
            UIHelper.showSuccessMessage(this, 
                "Peminjaman multiple items berhasil diproses!\n" +
                "Kode Transaksi: " + transaksi.getKodeTransaksi() + "\n" +
                "Total Buku: " + totalRequestedBooks);
            
            hideForm();
            loadData();
            
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error processing peminjaman: " + e.getMessage());
        }
    }
}

/**
 * Dialog untuk memilih multiple buku dengan jumlah masing-masing
 */
/**
 * FIXED MultipleBookSelectionDialog - dengan tracking real-time stok tersedia
 */
class MultipleBookSelectionDialog extends JDialog {
    private BukuDAO bukuDAO;
    private List<BookSelectionItem> selectedItems;
    private List<DetailTransaksiPeminjaman> alreadySelectedInForm; 
    
    private JTable tableBuku;
    private DefaultTableModel tableModel;
    private JTable tableSelected;
    private DefaultTableModel selectedModel;
    private JTextField txtSearch;
    private JSpinner spinnerJumlah;
    private JButton btnAdd, btnRemove, btnOK, btnCancel;
    
    public MultipleBookSelectionDialog(Dialog parent, List<DetailTransaksiPeminjaman> alreadySelected) {
        super(parent, "Pilih Multiple Buku", true);
        bukuDAO = new BukuDAO();
        selectedItems = new ArrayList<>();
        this.alreadySelectedInForm = alreadySelected != null ? alreadySelected : new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadAvailableBooks();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Available books table - FIXED: Tambah kolom "Sisa Tersedia"
        String[] columns = {"Kode Buku", "Judul", "Pengarang", "Kategori", "Tersedia", "Sisa Tersedia"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBuku = new JTable(tableModel);
        tableBuku.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Selected books table
        String[] selectedColumns = {"Kode Buku", "Judul", "Jumlah", "Tersedia"};
        selectedModel = new DefaultTableModel(selectedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSelected = new JTable(selectedModel);
        
        // Controls
        txtSearch = new JTextField(20);
        spinnerJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        
        btnAdd = new JButton("Tambah >>");
        btnRemove = new JButton("<< Hapus");
        btnOK = new JButton("OK");
        btnCancel = new JButton("Batal");
        
        // Styling
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnRemove.setBackground(new Color(220, 53, 69));
        btnRemove.setForeground(Color.WHITE);
        btnOK.setBackground(new Color(40, 167, 69));
        btnOK.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Cari Buku:"));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Jumlah:"));
        searchPanel.add(spinnerJumlah);
        
        // Tables panel
        JPanel tablesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Buku Tersedia"));
        availablePanel.add(new JScrollPane(tableBuku), BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        controlPanel.add(new JLabel(""));
        controlPanel.add(btnAdd);
        controlPanel.add(btnRemove);
        controlPanel.add(new JLabel(""));
        
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Buku Dipilih"));
        selectedPanel.add(new JScrollPane(tableSelected), BorderLayout.CENTER);
        
        tablesPanel.add(availablePanel);
        tablesPanel.add(controlPanel);
        tablesPanel.add(selectedPanel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnOK);
        buttonPanel.add(btnCancel);
        
        add(searchPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        btnAdd.addActionListener(e -> addSelectedBook());
        btnRemove.addActionListener(e -> removeSelectedBook());
        btnOK.addActionListener(e -> {
            if (!selectedItems.isEmpty()) {
                dispose();
            }
        });
        btnCancel.addActionListener(e -> {
            selectedItems.clear();
            dispose();
        });
        
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchBooks();
            }
        });
    }
    
    // FIXED: Load dengan kolom "Sisa Tersedia"
    private void loadAvailableBooks() {
        try {
            List<Buku> bukuList = bukuDAO.findAvailableBooks();
            populateAvailableTable(bukuList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading books: " + e.getMessage());
        }
    }
    
    private void populateAvailableTable(List<Buku> bukuList) {
        tableModel.setRowCount(0);
        for (Buku buku : bukuList) {
            int tersedia = buku.getJumlahTersedia();
            int sisaTersedia = calculateSisaTersedia(buku); // FIXED: Real-time calculation
            
            Object[] row = {
                buku.getKodeBuku(),
                buku.getJudul(),
                buku.getPengarang(),
                buku.getKategori() != null ? buku.getKategori().getNamaKategori() : "",
                tersedia,
                sisaTersedia // FIXED: Kolom baru
            };
            tableModel.addRow(row);
        }
    }
    

// FIXED: Hitung dengan memperhitungkan yang sudah dipilih di form utama
private int calculateSisaTersedia(Buku buku) {
    int tersedia = buku.getJumlahTersedia();
    
    // Kurangi dengan yang sudah dipilih di form utama
    for (DetailTransaksiPeminjaman detail : alreadySelectedInForm) {
        if (detail.getBuku().getIdBuku() == buku.getIdBuku()) {
            tersedia -= detail.getJumlahPinjam();
        }
    }
    
    // Kurangi dengan yang sudah dipilih di dialog ini
    for (BookSelectionItem item : selectedItems) {
        if (item.getBuku().getIdBuku() == buku.getIdBuku()) {
            tersedia -= item.getJumlah();
        }
    }
    
    return Math.max(0, tersedia);
}
    
    private void searchBooks() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAvailableBooks();
            return;
        }
        
        try {
            List<Buku> bukuList = bukuDAO.searchBooks(keyword).stream()
                .filter(buku -> buku.getJumlahTersedia() > 0)
                .collect(java.util.stream.Collectors.toList());
            populateAvailableTable(bukuList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error searching books: " + e.getMessage());
        }
    }
    
    // FIXED: Validasi menggunakan "Sisa Tersedia"
    private void addSelectedBook() {
        int selectedRow = tableBuku.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showWarningMessage(this, "Pilih buku terlebih dahulu!");
            return;
        }

        String kodeBuku = (String) tableModel.getValueAt(selectedRow, 0);
        int jumlahDiminta = (Integer) spinnerJumlah.getValue();
        int tersedia = (Integer) tableModel.getValueAt(selectedRow, 4);
        int sisaTersedia = (Integer) tableModel.getValueAt(selectedRow, 5); // FIXED: Ambil dari kolom baru

        // FIXED: Validasi menggunakan sisa tersedia
        if (jumlahDiminta > sisaTersedia) {
            UIHelper.showWarningMessage(this, 
                "Jumlah yang diminta melebihi sisa yang tersedia!\n" +
                "Stok tersedia: " + tersedia + " buku\n" +
                "Sudah dipilih sebelumnya: " + (tersedia - sisaTersedia) + " buku\n" +
                "Sisa tersedia: " + sisaTersedia + " buku\n" +
                "Diminta sekarang: " + jumlahDiminta + " buku");
            return;
        }

        try {
            Buku buku = bukuDAO.findByKode(kodeBuku);
            if (buku != null) {
                boolean found = false;
                for (BookSelectionItem item : selectedItems) {
                    if (item.getBuku().getIdBuku() == buku.getIdBuku()) {
                        // Update existing item
                        int totalBaru = item.getJumlah() + jumlahDiminta;
                        if (totalBaru > tersedia) {
                            UIHelper.showWarningMessage(this, "Total jumlah melebihi stok tersedia!");
                            return;
                        }
                        item.setJumlah(totalBaru);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Add new item
                    selectedItems.add(new BookSelectionItem(buku, jumlahDiminta));
                }

                // FIXED: Refresh BOTH tables
                loadAvailableBooks(); // Update kolom "Sisa Tersedia"
                updateSelectedTable();
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error adding book: " + e.getMessage());
        }
    }
    
    // FIXED: Setelah remove, refresh available table juga
    private void removeSelectedBook() {
        int selectedRow = tableSelected.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < selectedItems.size()) {
            selectedItems.remove(selectedRow);

            // FIXED: Refresh both tables
            loadAvailableBooks(); // Update kolom "Sisa Tersedia"
            updateSelectedTable();
        }
    }
    
    private void updateSelectedTable() {
        selectedModel.setRowCount(0);
        for (BookSelectionItem item : selectedItems) {
            Object[] row = {
                item.getBuku().getKodeBuku(),
                item.getBuku().getJudul(),
                item.getJumlah(),
                item.getBuku().getJumlahTersedia()
            };
            selectedModel.addRow(row);
        }
    }
    
    public List<BookSelectionItem> getSelectedItems() {
        return selectedItems;
    }
}

/**
 * Helper class untuk menyimpan buku yang dipilih dengan jumlahnya
 */
class BookSelectionItem {
    private Buku buku;
    private int jumlah;
    
    public BookSelectionItem(Buku buku, int jumlah) {
        this.buku = buku;
        this.jumlah = jumlah;
    }
    
    public Buku getBuku() { return buku; }
    public void setBuku(Buku buku) { this.buku = buku; }
    
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
}