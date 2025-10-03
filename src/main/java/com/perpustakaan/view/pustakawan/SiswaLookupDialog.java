package com.perpustakaan.view.pustakawan;

import com.perpustakaan.dao.SiswaDAO;
import com.perpustakaan.model.Siswa;
import com.perpustakaan.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Dialog untuk memilih siswa dalam transaksi peminjaman
 */
public class SiswaLookupDialog extends JDialog {
    private SiswaDAO siswaDAO;
    private Siswa selectedSiswa;
    
    // Components
    private JTable tableSiswa;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField txtSearch;
    private JButton btnSearch, btnSelect, btnCancel;
    
    public SiswaLookupDialog(Dialog parent) {
        super(parent, "Pilih Siswa", true);
        siswaDAO = new SiswaDAO();
        selectedSiswa = null;
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Table
        String[] columns = {"NIS", "Nama Lengkap", "Kelas", "Jenis Kelamin", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSiswa = new JTable(tableModel);
        tableSiswa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSiswa.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableSiswa.getColumnModel().getColumn(1).setPreferredWidth(200);
        scrollPane = new JScrollPane(tableSiswa);
        
        // Search components
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Cari berdasarkan NIS atau Nama");
        
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
        topPanel.setBorder(BorderFactory.createTitledBorder("Cari Siswa"));
        topPanel.add(new JLabel("Kata kunci:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnSelect);
        bottomPanel.add(btnCancel);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Table selection
        tableSiswa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnSelect.setEnabled(tableSiswa.getSelectedRow() >= 0);
            }
        });
        
        // Double click to select
        tableSiswa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tableSiswa.getSelectedRow() >= 0) {
                    selectSiswa();
                }
            }
        });
        
        // Buttons
        btnSearch.addActionListener(e -> searchSiswa());
        btnSelect.addActionListener(e -> selectSiswa());
        btnCancel.addActionListener(e -> dispose());
        
        // Enter key for search
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    searchSiswa();
                }
            }
        });
    }
    
    private void loadData() {
        try {
            List<Siswa> siswaList = siswaDAO.findAll().stream()
                .filter(Siswa::isStatusAktif) // Only active students
                .collect(java.util.stream.Collectors.toList());
            populateTable(siswaList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading siswa data: " + e.getMessage());
        }
    }
    
    private void populateTable(List<Siswa> siswaList) {
        tableModel.setRowCount(0);
        
        for (Siswa siswa : siswaList) {
            Object[] row = {
                siswa.getNis(),
                siswa.getNamaLengkap(),
                siswa.getKelas() != null ? siswa.getKelas().getNamaKelas() : "",
                "L".equals(siswa.getJenisKelamin()) ? "Laki-laki" : "Perempuan",
                siswa.isStatusAktif() ? "Aktif" : "Tidak Aktif"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchSiswa() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<Siswa> siswaList = siswaDAO.findAll().stream()
                .filter(siswa -> siswa.isStatusAktif() && (
                    siswa.getNis().toLowerCase().contains(keyword.toLowerCase()) ||
                    siswa.getNamaLengkap().toLowerCase().contains(keyword.toLowerCase())
                ))
                .collect(java.util.stream.Collectors.toList());
            
            populateTable(siswaList);
            
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error searching siswa: " + e.getMessage());
        }
    }
    
    private void selectSiswa() {
        int selectedRow = tableSiswa.getSelectedRow();
        if (selectedRow < 0) return;
        
        String nis = (String) tableModel.getValueAt(selectedRow, 0);
        
        try {
            selectedSiswa = siswaDAO.findByNis(nis);
            dispose();
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading selected siswa: " + e.getMessage());
        }
    }
    
    public Siswa getSelectedSiswa() {
        return selectedSiswa;
    }
}