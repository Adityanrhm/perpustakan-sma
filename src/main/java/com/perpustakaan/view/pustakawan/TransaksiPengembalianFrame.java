package com.perpustakaan.view.pustakawan;

import com.mysql.cj.x.protobuf.Mysqlx;
import com.perpustakaan.dao.*;
import com.perpustakaan.model.*;
import com.perpustakaan.util.UIHelper;
import com.perpustakaan.util.UserSession;
import com.perpustakaan.util.DateTimeHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * FIXED TransaksiPengembalianFrame - Semua bug sudah diperbaiki
 */
public class TransaksiPengembalianFrame extends JDialog {
    private TransaksiPeminjamanDAO transaksiDAO;
    private DetailTransaksiPeminjamanDAO detailDAO;
    private SiswaDAO siswaDAO;
    private BukuDAO bukuDAO;
    private PustakawanDAO pustakawanDAO;
    private StatusTransaksiDAO statusDAO;
    
    // Components
    private JTable tablePengembalian;
    private DefaultTableModel tableModel;
    private JTable tableDetailItems;
    private DefaultTableModel detailModel;
    private JTable tableReturnItems;
    private DefaultTableModel returnItemsModel;
    private JScrollPane scrollPane, scrollDetailPane, scrollReturnPane;
    
    // Form components
    private JTextField txtKodeTransaksi, txtNisSiswa, txtNamaSiswa;
    private JTextField txtTanggalKembaliAktual;
    private JTextArea txtCatatan;
    private JButton btnCariTransaksi, btnTambahItemKembali, btnHapusItemKembali;
    private JButton btnHitungDenda, btnKembalikan, btnCancel, btnRefresh;
    private JButton btnProsesKembali, btnLihatDetail, btnCetakStruk;
    
    // Selected data
    private TransaksiPeminjaman selectedTransaksi;
    private Pustakawan currentPustakawan;
    private List<ReturnItem> returnItems;
    private BigDecimal dendaPerHari = new BigDecimal("2000");
    
    // Form state
    private boolean isFormVisible = false;
    
    public TransaksiPengembalianFrame(Frame parent) {
        super(parent, "Kelola Transaksi Pengembalian (Multiple Items)", true);
        
        transaksiDAO = new TransaksiPeminjamanDAO();
        detailDAO = new DetailTransaksiPeminjamanDAO();
        siswaDAO = new SiswaDAO();
        bukuDAO = new BukuDAO();
        pustakawanDAO = new PustakawanDAO();
        statusDAO = new StatusTransaksiDAO();
        
        returnItems = new ArrayList<>();
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
        // Main transaction table - FIXED: Added correct columns
        String[] transaksiColumns = {"ID", "Kode Transaksi", "NIS", "Nama Siswa", 
                                    "Tgl Pinjam", "Total Items", "Belum Kembali", "Status", "Total Denda"};
        tableModel = new DefaultTableModel(transaksiColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePengembalian = new JTable(tableModel);
        tablePengembalian.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(tablePengembalian);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        
        // Detail items table for selected transaction
        String[] detailColumns = {"ID Detail", "Kode Buku", "Judul Buku", "Jml Pinjam", 
                                 "Jml Kembali", "Belum Kembali", "Jml Hilang", "Jml Rusak", 
                                 "Tgl Kembali Rencana", "Status", "Hari Terlambat", "Denda"};
        detailModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDetailItems = new JTable(detailModel);
        scrollDetailPane = new JScrollPane(tableDetailItems);
        scrollDetailPane.setPreferredSize(new Dimension(800, 150));
        
        // Return items table for form
        String[] returnColumns = {"Kode Buku", "Judul Buku", "Tersisa", "Jml Kembali", 
                                 "Jml Hilang", "Jml Rusak", "Total", "Kondisi", "Denda Item"};
        returnItemsModel = new DefaultTableModel(returnColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReturnItems = new JTable(returnItemsModel);
        scrollReturnPane = new JScrollPane(tableReturnItems);
        
        // Form components
        txtKodeTransaksi = new JTextField(15);
        txtKodeTransaksi.setEditable(false);
        txtNisSiswa = new JTextField(15);
        txtNisSiswa.setEditable(false);
        txtNamaSiswa = new JTextField(20);
        txtNamaSiswa.setEditable(false);
        
        txtTanggalKembaliAktual = new JTextField(12);
        txtTanggalKembaliAktual.setText(DateTimeHelper.formatDate(LocalDate.now()));
        
        txtCatatan = new JTextArea(3, 30);
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        
        // Buttons
        btnProsesKembali = new JButton("Proses Pengembalian Multiple");
        btnLihatDetail = new JButton("Lihat Detail");
        btnCetakStruk = new JButton("Cetak Struk");
        btnCariTransaksi = new JButton("Cari Transaksi");
        btnTambahItemKembali = new JButton("Tambah Item Kembali");
        btnHapusItemKembali = new JButton("Hapus Item");
        btnHitungDenda = new JButton("Hitung Denda");
        btnKembalikan = new JButton("Kembalikan");
        btnCancel = new JButton("Batal");
        btnRefresh = new JButton("Refresh");
        
        // Button styling
        setupButtonColors();
        
        // Initial state
        btnLihatDetail.setEnabled(false);
        btnCetakStruk.setEnabled(false);
        btnKembalikan.setEnabled(false);
        btnCancel.setEnabled(false);
        btnTambahItemKembali.setEnabled(false);
        btnHapusItemKembali.setEnabled(false);
    }
    
    private void setupButtonColors() {
        btnProsesKembali.setBackground(new Color(40, 167, 69));
        btnProsesKembali.setForeground(Color.WHITE);
        btnLihatDetail.setBackground(new Color(23, 162, 184));
        btnLihatDetail.setForeground(Color.WHITE);
        btnCetakStruk.setBackground(new Color(255, 193, 7));
        btnCetakStruk.setForeground(Color.BLACK);
        btnCariTransaksi.setBackground(new Color(40, 167, 69));
        btnCariTransaksi.setForeground(Color.WHITE);
        btnTambahItemKembali.setBackground(new Color(40, 167, 69));
        btnTambahItemKembali.setForeground(Color.WHITE);
        btnHapusItemKembali.setBackground(new Color(220, 53, 69));
        btnHapusItemKembali.setForeground(Color.WHITE);
        btnHitungDenda.setBackground(new Color(255, 193, 7));
        btnHitungDenda.setForeground(Color.BLACK);
        btnKembalikan.setBackground(new Color(40, 167, 69));
        btnKembalikan.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(23, 162, 184));
        btnRefresh.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnProsesKembali);
        topPanel.add(btnLihatDetail);
        topPanel.add(btnCetakStruk);
        topPanel.add(btnRefresh);
        
        // Transaction panel
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transaksi Aktif"));
        transactionPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Detail panel
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Detail Items Transaksi"));
        detailPanel.add(scrollDetailPane, BorderLayout.CENTER);
        
        // Split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                                  transactionPanel, detailPanel);
        mainSplitPane.setDividerLocation(250);
        
        // Form panel
        JPanel formPanel = createEnhancedReturnFormPanel();
        formPanel.setVisible(false);
        
        add(topPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createEnhancedReturnFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Pengembalian Multiple Items"));
        
        // Transaction info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Kode Transaksi:"));
        infoPanel.add(txtKodeTransaksi);
        infoPanel.add(btnCariTransaksi);
        infoPanel.add(new JLabel("NIS:"));
        infoPanel.add(txtNisSiswa);
        infoPanel.add(new JLabel("Nama:"));
        infoPanel.add(txtNamaSiswa);
        
        // Return items panel
        JPanel returnItemsPanel = new JPanel(new BorderLayout());
        returnItemsPanel.setBorder(BorderFactory.createTitledBorder("Item yang Dikembalikan"));
        scrollReturnPane.setPreferredSize(new Dimension(800, 200));
        
        JPanel returnButtonPanel = new JPanel(new FlowLayout());
        returnButtonPanel.add(btnTambahItemKembali);
        returnButtonPanel.add(btnHapusItemKembali);
        returnButtonPanel.add(btnHitungDenda);
        
        returnItemsPanel.add(scrollReturnPane, BorderLayout.CENTER);
        returnItemsPanel.add(returnButtonPanel, BorderLayout.SOUTH);
        
        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(new JLabel("Tgl Kembali:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(txtTanggalKembaliAktual, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(new JLabel("Catatan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(new JScrollPane(txtCatatan), gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnKembalikan);
        buttonPanel.add(btnCancel);
        
        // Combine
        JPanel topFormPanel = new JPanel(new BorderLayout());
        topFormPanel.add(infoPanel, BorderLayout.NORTH);
        topFormPanel.add(returnItemsPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(detailsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        formPanel.add(topFormPanel, BorderLayout.CENTER);
        formPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }
    
    private void setupEventListeners() {
        tablePengembalian.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablePengembalian.getSelectedRow();
                btnLihatDetail.setEnabled(selectedRow >= 0);
                btnCetakStruk.setEnabled(selectedRow >= 0);
                
                if (selectedRow >= 0) {
                    loadDetailItems();
                }
            }
        });
        
        tableReturnItems.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnHapusItemKembali.setEnabled(tableReturnItems.getSelectedRow() >= 0);
            }
        });
        
        btnProsesKembali.addActionListener(e -> showFormPengembalian());
        btnLihatDetail.addActionListener(e -> lihatDetailPengembalian());
        btnCetakStruk.addActionListener(e -> cetakStrukPengembalian());
        btnRefresh.addActionListener(e -> loadData());
        
        btnCariTransaksi.addActionListener(e -> cariTransaksi());
        btnTambahItemKembali.addActionListener(e -> tambahItemKembali());
        btnHapusItemKembali.addActionListener(e -> hapusItemKembali());
        btnHitungDenda.addActionListener(e -> hitungTotalDenda());
        btnKembalikan.addActionListener(e -> prosesPengembalianMultiple());
        btnCancel.addActionListener(e -> hideFormPengembalian());
    }
    
    private void loadData() {
        try {
            List<TransaksiPeminjaman> transaksiList = transaksiDAO.findTransactionsWithPendingReturns();
            populateTransaksiTable(transaksiList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading data: " + e.getMessage());
        }
    }
    
    // FIXED: Correct calculation for belum kembali
    private void populateTransaksiTable(List<TransaksiPeminjaman> transaksiList) {
        tableModel.setRowCount(0);
        
        for (TransaksiPeminjaman transaksi : transaksiList) {
            int totalBelumKembali = transaksi.getTotalBukuBelumKembali();
            
            Object[] row = {
                transaksi.getIdTransaksi(),
                transaksi.getKodeTransaksi(),
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNis() : "",
                transaksi.getSiswa() != null ? transaksi.getSiswa().getNamaLengkap() : "",
                DateTimeHelper.formatDate(transaksi.getTanggalPinjam()),
                transaksi.getTotalBuku(),
                totalBelumKembali,
                transaksi.getStatusKeseluruhan(),
                "Rp " + (transaksi.getTotalDenda() != null ? transaksi.getTotalDenda() : BigDecimal.ZERO)
            };
            tableModel.addRow(row);
        }
    }
    
    // FIXED: Complete detail items display with proper calculations
    private void loadDetailItems() {
        int selectedRow = tablePengembalian.getSelectedRow();
        if (selectedRow < 0) {
            detailModel.setRowCount(0);
            return;
        }
        
        try {
            int idTransaksi = (int) tableModel.getValueAt(selectedRow, 0);
            List<DetailTransaksiPeminjaman> details = detailDAO.findByTransaksiId(idTransaksi);
            populateDetailItemsTable(details);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading details: " + e.getMessage());
        }
    }
    
    private void populateDetailItemsTable(List<DetailTransaksiPeminjaman> details) {
        detailModel.setRowCount(0);
        
        for (DetailTransaksiPeminjaman detail : details) {
            long hariTerlambat = detail.getHariTerlambat();
            BigDecimal itemFine = detail.hitungDenda(dendaPerHari);
            
            Object[] row = {
                detail.getIdDetail(),
                detail.getBuku() != null ? detail.getBuku().getKodeBuku() : "",
                detail.getBuku() != null ? detail.getBuku().getJudul() : "",
                detail.getJumlahPinjam(),
                detail.getJumlahKembali(),
                detail.getJumlahBelumKembali(), // FIXED: Using correct method
                detail.getJumlahHilang(),
                detail.getJumlahRusak(),
                DateTimeHelper.formatDate(detail.getTanggalKembaliRencana()),
                detail.getStatusDetail(),
                hariTerlambat > 0 ? hariTerlambat + " hari" : "-",
                "Rp " + itemFine
            };
            detailModel.addRow(row);
        }
    }
    
    private void showFormPengembalian() {
        clearReturnForm();
        isFormVisible = true;
        getContentPane().getComponent(2).setVisible(true);
        btnProsesKembali.setEnabled(false);
        btnCancel.setEnabled(true);
        txtTanggalKembaliAktual.setText(DateTimeHelper.formatDate(LocalDate.now()));
        revalidate();
        repaint();
    }
    
    private void hideFormPengembalian() {
        isFormVisible = false;
        getContentPane().getComponent(2).setVisible(false);
        btnProsesKembali.setEnabled(true);
        btnCancel.setEnabled(false);
        clearReturnForm();
        revalidate();
        repaint();
    }
    
    private void clearReturnForm() {
        txtKodeTransaksi.setText("");
        txtNisSiswa.setText("");
        txtNamaSiswa.setText("");
        txtCatatan.setText("");
        selectedTransaksi = null;
        returnItems.clear();
        updateReturnItemsTable();
        updateReturnFormState();
    }
    
    private void cariTransaksi() {
        TransaksiLookupDialog dialog = new TransaksiLookupDialog(this);
        dialog.setVisible(true);
        
        if (dialog.getSelectedTransaksi() != null) {
            selectedTransaksi = dialog.getSelectedTransaksi();
            populateReturnForm(selectedTransaksi);
            updateReturnFormState();
        }
    }
    
    private void populateReturnForm(TransaksiPeminjaman transaksi) {
        txtKodeTransaksi.setText(transaksi.getKodeTransaksi());
        
        if (transaksi.getSiswa() != null) {
            txtNisSiswa.setText(transaksi.getSiswa().getNis());
            txtNamaSiswa.setText(transaksi.getSiswa().getNamaLengkap());
        }
        
        if (transaksi.getCatatan() != null) {
            txtCatatan.setText(transaksi.getCatatan());
        }
    }
    
    // FIXED: Complete validation and proper item selection
private void tambahItemKembali() {
    if (selectedTransaksi == null) {
        UIHelper.showWarningMessage(this, "Pilih transaksi terlebih dahulu!");
        return;
    }
    
    try {
        List<DetailTransaksiPeminjaman> availableItems = detailDAO.findByTransaksiId(selectedTransaksi.getIdTransaksi())
            .stream()
            .filter(detail -> detail.getJumlahBelumKembali() > 0)
            .collect(java.util.stream.Collectors.toList());
        
        if (availableItems.isEmpty()) {
            UIHelper.showWarningMessage(this, "Tidak ada item yang bisa dikembalikan!");
            return;
        }
        
        // FIXED: Pass returnItems yang sudah ada
        ReturnItemSelectionDialog dialog = new ReturnItemSelectionDialog(this, availableItems, returnItems);
        dialog.setVisible(true);
        
        if (dialog.getSelectedReturnItems() != null && !dialog.getSelectedReturnItems().isEmpty()) {
            returnItems.addAll(dialog.getSelectedReturnItems());
            updateReturnItemsTable();
            updateReturnFormState();
        }
        
    } catch (SQLException e) {
        UIHelper.showErrorMessage(this, "Error loading available items: " + e.getMessage());
    }
}
    
    private void hapusItemKembali() {
        int selectedRow = tableReturnItems.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < returnItems.size()) {
            returnItems.remove(selectedRow);
            updateReturnItemsTable();
            updateReturnFormState();
        }
    }
    
    // FIXED: Show complete information including total
    private void updateReturnItemsTable() {
        returnItemsModel.setRowCount(0);
        
        for (ReturnItem item : returnItems) {
            BigDecimal itemFine = item.calculateFine(dendaPerHari);
            int totalReturn = item.getJumlahTotal();
            
            Object[] row = {
                item.getDetailTransaksi().getBuku().getKodeBuku(),
                item.getDetailTransaksi().getBuku().getJudul(),
                item.getDetailTransaksi().getJumlahBelumKembali(),
                item.getJumlahKembali(),
                item.getJumlahHilang(),
                item.getJumlahRusak(),
                totalReturn,
                item.getKondisi(),
                "Rp " + itemFine
            };
            returnItemsModel.addRow(row);
        }
    }
    
    private void updateReturnFormState() {
        btnTambahItemKembali.setEnabled(selectedTransaksi != null);
        btnHapusItemKembali.setEnabled(!returnItems.isEmpty());
        btnKembalikan.setEnabled(selectedTransaksi != null && !returnItems.isEmpty());
    }
    
    private void hitungTotalDenda() {
        if (returnItems.isEmpty()) {
            UIHelper.showWarningMessage(this, "Tidak ada item untuk dihitung dendanya!");
            return;
        }
        
        BigDecimal totalDenda = BigDecimal.ZERO;
        StringBuilder detailDenda = new StringBuilder("Detail Perhitungan Denda:\n\n");
        
        for (ReturnItem item : returnItems) {
            BigDecimal itemFine = item.calculateFine(dendaPerHari);
            totalDenda = totalDenda.add(itemFine);
            
            detailDenda.append("- ").append(item.getDetailTransaksi().getBuku().getJudul())
                      .append(" (x").append(item.getJumlahTotal()).append("): Rp ")
                      .append(itemFine).append("\n");
                      
            // Show breakdown
            long hariTerlambat = item.getDetailTransaksi().getHariTerlambat();
            if (hariTerlambat > 0) {
                detailDenda.append("  - Keterlambatan: ").append(hariTerlambat)
                          .append(" hari x Rp ").append(dendaPerHari).append("\n");
            }
            if (item.getJumlahRusak() > 0) {
                detailDenda.append("  - Rusak: ").append(item.getJumlahRusak())
                          .append(" x Rp 10,000\n");
            }
            if (item.getJumlahHilang() > 0) {
                BigDecimal hargaBuku = item.getDetailTransaksi().getBuku().getHarga() != null ?
                    item.getDetailTransaksi().getBuku().getHarga() : new BigDecimal("100000");
                detailDenda.append("  - Hilang: ").append(item.getJumlahHilang())
                          .append(" x Rp ").append(hargaBuku).append("\n");
            }
        }
        
        detailDenda.append("\n=============================\n");
        detailDenda.append("TOTAL DENDA: Rp ").append(totalDenda);
        
        JTextArea textArea = new JTextArea(detailDenda.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Perhitungan Denda", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // FIXED: Complete processing with proper validation and status updates
    private void prosesPengembalianMultiple() {
        if (selectedTransaksi == null || returnItems.isEmpty()) {
            UIHelper.showWarningMessage(this, "Pilih transaksi dan item yang dikembalikan!");
            return;
        }
        
        if (currentPustakawan == null) {
            UIHelper.showErrorMessage(this, "Data pustakawan tidak ditemukan!");
            return;
        }
        
        // FIXED: Validate all return items
        for (ReturnItem item : returnItems) {
            if (!item.isValid()) {
                UIHelper.showWarningMessage(this, 
                    "Total pengembalian untuk buku '" + item.getDetailTransaksi().getBuku().getJudul() + 
                    "' melebihi jumlah yang belum dikembalikan!");
                return;
            }
        }
        
        try {
            LocalDate tanggalKembali = DateTimeHelper.parseDate(txtTanggalKembaliAktual.getText());
            if (tanggalKembali == null) {
                UIHelper.showErrorMessage(this, "Format tanggal kembali tidak valid!");
                return;
            }
            
            // Calculate total fine
            BigDecimal totalDenda = returnItems.stream()
                .map(item -> item.calculateFine(dendaPerHari))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Show confirmation
            StringBuilder itemsList = new StringBuilder();
            for (ReturnItem item : returnItems) {
                itemsList.append("- ").append(item.getDetailTransaksi().getBuku().getJudul())
                        .append(": Kembali=").append(item.getJumlahKembali())
                        .append(", Hilang=").append(item.getJumlahHilang())
                        .append(", Rusak=").append(item.getJumlahRusak())
                        .append(" (").append(item.getKondisi()).append(")\n");
            }
            
            String confirmMessage = "Konfirmasi Pengembalian Multiple Items:\n\n" +
                "Kode Transaksi: " + selectedTransaksi.getKodeTransaksi() + "\n" +
                "Siswa: " + selectedTransaksi.getSiswa().getNamaLengkap() + "\n\n" +
                "Item yang dikembalikan:\n" + itemsList.toString() + "\n" +
                "Total Denda: Rp " + totalDenda + "\n\n" +
                "Proses pengembalian?";
            
            if (!UIHelper.showConfirmDialog(this, confirmMessage)) {
                return;
            }
            
            // FIXED: Process each return item with proper status updates
            for (ReturnItem item : returnItems) {
                DetailTransaksiPeminjaman detail = item.getDetailTransaksi();
                
                // Update quantities
                detail.setJumlahKembali(detail.getJumlahKembali() + item.getJumlahKembali());
                detail.setJumlahHilang(detail.getJumlahHilang() + item.getJumlahHilang());
                detail.setJumlahRusak(detail.getJumlahRusak() + item.getJumlahRusak());
                
                // FIXED: Set return date only if fully returned
                if (detail.getJumlahBelumKembali() <= 0) {
                    detail.setTanggalKembaliAktual(tanggalKembali);
                }
                
                // FIXED: Update status based on condition with proper logic
                StatusTransaksi newStatus;
                if (detail.getJumlahBelumKembali() <= 0) {
                    // Fully returned - check condition
                    if (item.getJumlahHilang() > 0) {
                        newStatus = statusDAO.findByName("HILANG");
                    } else if (item.getJumlahRusak() > 0) {
                        newStatus = statusDAO.findByName("RUSAK");
                    } else {
                        newStatus = statusDAO.findByName("DIKEMBALIKAN");
                    }
                } else {
                    // Partial return - still borrowed
                    newStatus = statusDAO.findByName("DIPINJAM");
                }
                
                detail.setStatus(newStatus);
                
                // Calculate and set fine
                BigDecimal itemFine = item.calculateFine(dendaPerHari);
                BigDecimal currentDenda = detail.getTotalDendaItem() != null ? detail.getTotalDendaItem() : BigDecimal.ZERO;
                detail.setTotalDendaItem(currentDenda.add(itemFine));
                
                // Add return note
                String returnNote = "Dikembalikan " + item.getJumlahTotal() + " item pada " + 
                                  DateTimeHelper.formatDate(tanggalKembali) + 
                                  " (Kembali: " + item.getJumlahKembali() +
                                  ", Hilang: " + item.getJumlahHilang() +
                                  ", Rusak: " + item.getJumlahRusak() +
                                  ") - Kondisi: " + item.getKondisi();
                
                if (itemFine.compareTo(BigDecimal.ZERO) > 0) {
                    returnNote += " | Denda: Rp " + itemFine;
                }
                
                String currentNote = detail.getCatatanDetail() != null ? detail.getCatatanDetail() : "";
                detail.setCatatanDetail(currentNote + (currentNote.isEmpty() ? "" : "\n") + returnNote);
                Connection conn = null;
                
                // Save detail
                detailDAO.save(detail);
            }
            
            // Add general notes to transaction
            String currentNote = selectedTransaksi.getCatatan() != null ? selectedTransaksi.getCatatan() : "";
            String newNote = currentNote + (currentNote.isEmpty() ? "" : "\n") +
                           "Pengembalian " + returnItems.size() + " item(s) pada " + 
                           DateTimeHelper.formatDate(tanggalKembali) + 
                           " oleh " + currentPustakawan.getNamaLengkap();
            
            if (!txtCatatan.getText().trim().isEmpty()) {
                newNote += " | Catatan: " + txtCatatan.getText().trim();
            }
            
            selectedTransaksi.setCatatan(newNote);
            transaksiDAO.save(selectedTransaksi);
            
            UIHelper.showSuccessMessage(this, 
                "Pengembalian multiple items berhasil diproses!\n" +
                "Kode Transaksi: " + selectedTransaksi.getKodeTransaksi() + "\n" +
                "Total Item: " + returnItems.size() + "\n" +
                "Total Denda: Rp " + totalDenda);
            
            hideFormPengembalian();
            loadData();
            
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error processing return: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void lihatDetailPengembalian() {
        int selectedRow = tablePengembalian.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showWarningMessage(this, "Pilih transaksi yang akan dilihat!");
            return;
        }
        
        try {
            int idTransaksi = (int) tableModel.getValueAt(selectedRow, 0);
            TransaksiPeminjaman transaksi = transaksiDAO.findByIdWithDetails(idTransaksi);
            
            if (transaksi != null) {
                EnhancedTransaksiDetailDialog dialog = new EnhancedTransaksiDetailDialog(this, transaksi);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading detail: " + e.getMessage());
        }
    }
    
    private void cetakStrukPengembalian() {
        UIHelper.showWarningMessage(this, "Fitur cetak struk akan segera tersedia");
    }
}

/**
 * FIXED ReturnItem - Complete validation and calculation logic
 */
class ReturnItem {
    private DetailTransaksiPeminjaman detailTransaksi;
    private int jumlahKembali;
    private int jumlahHilang;
    private int jumlahRusak;
    private String kondisi;
    
    public ReturnItem(DetailTransaksiPeminjaman detail) {
        this.detailTransaksi = detail;
        this.jumlahKembali = 0;
        this.jumlahHilang = 0;
        this.jumlahRusak = 0;
        this.kondisi = "BAIK";
    }
    
    public DetailTransaksiPeminjaman getDetailTransaksi() { return detailTransaksi; }
    public void setDetailTransaksi(DetailTransaksiPeminjaman detailTransaksi) { this.detailTransaksi = detailTransaksi; }
    
    public int getJumlahKembali() { return jumlahKembali; }
    public void setJumlahKembali(int jumlahKembali) { this.jumlahKembali = jumlahKembali; }
    
    public int getJumlahHilang() { return jumlahHilang; }
    public void setJumlahHilang(int jumlahHilang) { this.jumlahHilang = jumlahHilang; }
    
    public int getJumlahRusak() { return jumlahRusak; }
    public void setJumlahRusak(int jumlahRusak) { this.jumlahRusak = jumlahRusak; }
    
    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }
    
    public int getJumlahTotal() {
        return jumlahKembali + jumlahHilang + jumlahRusak;
    }
    
    // FIXED: Complete validation
    public boolean isValid() {
        int total = getJumlahTotal();
        int tersisa = detailTransaksi.getJumlahBelumKembali();
        return total > 0 && total <= tersisa;
    }
    
    // FIXED: Complete fine calculation with all scenarios
    public BigDecimal calculateFine(BigDecimal dendaPerHari) {
        BigDecimal totalFine = BigDecimal.ZERO;
        int totalItems = getJumlahTotal();
        
        if (totalItems == 0) return totalFine;
        
        // Denda keterlambatan - applicable to all items being returned
        long hariTerlambat = detailTransaksi.getHariTerlambat();
        if (hariTerlambat > 0) {
            totalFine = totalFine.add(dendaPerHari.multiply(BigDecimal.valueOf(hariTerlambat * totalItems)));
        }
        
        // Denda rusak: Rp 10,000 per item
        if (jumlahRusak > 0) {
            totalFine = totalFine.add(new BigDecimal("10000").multiply(BigDecimal.valueOf(jumlahRusak)));
        }
        
        // Denda hilang: harga buku atau default Rp 100,000
        if (jumlahHilang > 0) {
            BigDecimal hargaBuku = detailTransaksi.getBuku() != null && detailTransaksi.getBuku().getHarga() != null ? 
                                  detailTransaksi.getBuku().getHarga() : new BigDecimal("100000");
            totalFine = totalFine.add(hargaBuku.multiply(BigDecimal.valueOf(jumlahHilang)));
        }
        
        return totalFine;
    }
}

/**
 * FIXED ReturnItemSelectionDialog - Proper item selection with validation
 */
/**
 * FIXED ReturnItemSelectionDialog - dengan tracking real-time
 */
class ReturnItemSelectionDialog extends JDialog {
    private List<DetailTransaksiPeminjaman> availableItems;
    private List<ReturnItem> selectedReturnItems;
    private List<ReturnItem> alreadySelectedInForm;
    
    private JTable tableAvailable;
    private DefaultTableModel availableModel;
    private JTable tableReturn;
    private DefaultTableModel returnModel;
    private JSpinner spinnerKembali, spinnerHilang, spinnerRusak;
    private JComboBox<String> cbKondisi;
    private JButton btnAdd, btnRemove, btnOK, btnCancel;
    
    public ReturnItemSelectionDialog(Dialog parent, List<DetailTransaksiPeminjaman> availableItems, 
                                     List<ReturnItem> alreadySelected) {
        super(parent, "Pilih Item untuk Dikembalikan", true);
        this.availableItems = availableItems;
        this.selectedReturnItems = new ArrayList<>();
        this.alreadySelectedInForm = alreadySelected != null ? alreadySelected : new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadAvailableItems();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1200, 650);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Available items table - FIXED: Tambah kolom "Sisa Tersedia"
        String[] availableColumns = {"Kode Buku", "Judul", "Jml Pinjam", "Jml Kembali", 
                                     "Belum Kembali", "Sisa Tersedia", "Hilang", "Rusak", "Terlambat"};
        availableModel = new DefaultTableModel(availableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAvailable = new JTable(availableModel);
        tableAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Return items table
        String[] returnColumns = {"Kode Buku", "Judul", "Kembali", "Hilang", "Rusak", 
                                 "Total", "Kondisi", "Denda"};
        returnModel = new DefaultTableModel(returnColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReturn = new JTable(returnModel);
        
        // Input components
        spinnerKembali = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        spinnerHilang = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        spinnerRusak = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        cbKondisi = new JComboBox<>(new String[]{"BAIK", "RUSAK RINGAN", "RUSAK BERAT", "HILANG"});
        
        btnAdd = new JButton("Tambah >>");
        btnRemove = new JButton("<< Hapus");
        btnOK = new JButton("OK");
        btnCancel = new JButton("Batal");
        
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnRemove.setBackground(new Color(220, 53, 69));
        btnRemove.setForeground(Color.WHITE);
        btnOK.setBackground(new Color(40, 167, 69));
        btnOK.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
    }
    
    // Setup layout sama seperti sebelumnya...
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Jumlah Pengembalian"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Jml Kembali:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(spinnerKembali, gbc);
        
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Jml Hilang:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(spinnerHilang, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Jml Rusak:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(spinnerRusak, gbc);
        
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Kondisi:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(cbKondisi, gbc);
        
        JPanel tablesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Item Tersedia"));
        availablePanel.add(new JScrollPane(tableAvailable), BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        controlPanel.add(new JLabel(""));
        controlPanel.add(new JLabel(""));
        controlPanel.add(btnAdd);
        controlPanel.add(btnRemove);
        controlPanel.add(new JLabel(""));
        controlPanel.add(new JLabel(""));
        
        JPanel returnPanel = new JPanel(new BorderLayout());
        returnPanel.setBorder(BorderFactory.createTitledBorder("Item Dikembalikan"));
        returnPanel.add(new JScrollPane(tableReturn), BorderLayout.CENTER);
        
        tablesPanel.add(availablePanel);
        tablesPanel.add(controlPanel);
        tablesPanel.add(returnPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnOK);
        buttonPanel.add(btnCancel);
        
        add(inputPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        btnAdd.addActionListener(e -> addReturnItem());
        btnRemove.addActionListener(e -> removeReturnItem());
        btnOK.addActionListener(e -> {
            if (!selectedReturnItems.isEmpty()) {
                dispose();
            } else {
                UIHelper.showWarningMessage(this, "Pilih minimal satu item untuk dikembalikan!");
            }
        });
        btnCancel.addActionListener(e -> {
            selectedReturnItems.clear();
            dispose();
        });
        
        spinnerHilang.addChangeListener(e -> {
            if ((Integer) spinnerHilang.getValue() > 0) {
                cbKondisi.setSelectedItem("HILANG");
            }
        });
        
        spinnerRusak.addChangeListener(e -> {
            if ((Integer) spinnerRusak.getValue() > 0 && (Integer) spinnerHilang.getValue() == 0) {
                cbKondisi.setSelectedItem("RUSAK RINGAN");
            }
        });
    }
    
    // FIXED: Load dengan kolom "Sisa Tersedia"
    private void loadAvailableItems() {
        availableModel.setRowCount(0);
        
        for (DetailTransaksiPeminjaman detail : availableItems) {
            String terlambat = detail.isTerlambat() ? 
                "Ya (" + detail.getHariTerlambat() + " hari)" : "Tidak";
            
            int belumKembali = detail.getJumlahBelumKembali();
            int sisaTersedia = calculateSisaTersedia(detail); // FIXED: Hitung sisa real-time
            
            Object[] row = {
                detail.getBuku().getKodeBuku(),
                detail.getBuku().getJudul(),
                detail.getJumlahPinjam(),
                detail.getJumlahKembali(),
                belumKembali,
                sisaTersedia, // FIXED: Kolom baru
                detail.getJumlahHilang(),
                detail.getJumlahRusak(),
                terlambat
            };
            availableModel.addRow(row);
        }
    }
    
    // FIXED: Method untuk hitung sisa tersedia setelah dikurangi yang sudah dipilih
private int calculateSisaTersedia(DetailTransaksiPeminjaman detail) {
    int belumKembali = detail.getJumlahBelumKembali();
    
    // Kurangi dengan yang sudah dipilih di form utama
    for (ReturnItem returnItem : alreadySelectedInForm) {
        if (returnItem.getDetailTransaksi().getIdDetail() == detail.getIdDetail()) {
            belumKembali -= returnItem.getJumlahTotal();
        }
    }
    
    // Kurangi dengan yang sudah dipilih di dialog ini
    for (ReturnItem returnItem : selectedReturnItems) {
        if (returnItem.getDetailTransaksi().getIdDetail() == detail.getIdDetail()) {
            belumKembali -= returnItem.getJumlahTotal();
        }
    }
    
    return Math.max(0, belumKembali);
}
    
    // FIXED: Validasi menggunakan "Sisa Tersedia" bukan "Belum Kembali"
    private void addReturnItem() {
        int selectedRow = tableAvailable.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showWarningMessage(this, "Pilih item yang akan dikembalikan!");
            return;
        }
        
        DetailTransaksiPeminjaman detail = availableItems.get(selectedRow);
        
        int jumlahKembali = (Integer) spinnerKembali.getValue();
        int jumlahHilang = (Integer) spinnerHilang.getValue();
        int jumlahRusak = (Integer) spinnerRusak.getValue();
        int totalReturn = jumlahKembali + jumlahHilang + jumlahRusak;
        
        if (totalReturn == 0) {
            UIHelper.showWarningMessage(this, "Tentukan jumlah item yang dikembalikan!");
            return;
        }
        
        // FIXED: Gunakan sisa tersedia yang sudah dikurangi dengan selected items
        int sisaTersedia = calculateSisaTersedia(detail);
        
        if (totalReturn > sisaTersedia) {
            UIHelper.showWarningMessage(this, 
                "Total pengembalian melebihi sisa yang tersedia!\n" +
                "Belum dikembalikan: " + detail.getJumlahBelumKembali() + " item\n" +
                "Sudah dipilih sebelumnya: " + (detail.getJumlahBelumKembali() - sisaTersedia) + " item\n" +
                "Sisa tersedia: " + sisaTersedia + " item\n" +
                "Diminta sekarang: " + totalReturn + " item");
            return;
        }
        
        // FIXED: Check if item already exists, update atau add new
        boolean itemExists = false;
        for (ReturnItem existingItem : selectedReturnItems) {
            if (existingItem.getDetailTransaksi().getIdDetail() == detail.getIdDetail()) {
                // Update existing item
                existingItem.setJumlahKembali(existingItem.getJumlahKembali() + jumlahKembali);
                existingItem.setJumlahHilang(existingItem.getJumlahHilang() + jumlahHilang);
                existingItem.setJumlahRusak(existingItem.getJumlahRusak() + jumlahRusak);
                existingItem.setKondisi((String) cbKondisi.getSelectedItem());
                itemExists = true;
                break;
            }
        }
        
        if (!itemExists) {
            // Add new item
            ReturnItem returnItem = new ReturnItem(detail);
            returnItem.setJumlahKembali(jumlahKembali);
            returnItem.setJumlahHilang(jumlahHilang);
            returnItem.setJumlahRusak(jumlahRusak);
            returnItem.setKondisi((String) cbKondisi.getSelectedItem());
            selectedReturnItems.add(returnItem);
        }
        
        // FIXED: Refresh BOTH tables
        loadAvailableItems(); // Update kolom "Sisa Tersedia"
        updateReturnTable();
        resetInputs();
    }
    
    // FIXED: Setelah remove, refresh available items juga
    private void removeReturnItem() {
        int selectedRow = tableReturn.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < selectedReturnItems.size()) {
            selectedReturnItems.remove(selectedRow);
            
            // FIXED: Refresh both tables
            loadAvailableItems(); // Update kolom "Sisa Tersedia"
            updateReturnTable();
        }
    }
    
    private void updateReturnTable() {
        returnModel.setRowCount(0);
        BigDecimal dendaPerHari = new BigDecimal("2000");
        
        for (ReturnItem item : selectedReturnItems) {
            BigDecimal itemFine = item.calculateFine(dendaPerHari);
            
            Object[] row = {
                item.getDetailTransaksi().getBuku().getKodeBuku(),
                item.getDetailTransaksi().getBuku().getJudul(),
                item.getJumlahKembali(),
                item.getJumlahHilang(),
                item.getJumlahRusak(),
                item.getJumlahTotal(),
                item.getKondisi(),
                "Rp " + itemFine
            };
            returnModel.addRow(row);
        }
    }
    
    private void resetInputs() {
        spinnerKembali.setValue(0);
        spinnerHilang.setValue(0);
        spinnerRusak.setValue(0);
        cbKondisi.setSelectedIndex(0);
    }
    
    public List<ReturnItem> getSelectedReturnItems() {
        return selectedReturnItems;
    }
}