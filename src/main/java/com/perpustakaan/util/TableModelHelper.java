
package com.perpustakaan.util;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class untuk membantu operasi table
 */
public class TableModelHelper {
    
    /**
     * Clear all rows from table model
     */
    public static void clearTable(DefaultTableModel model) {
        model.setRowCount(0);
    }
    
    /**
     * Add multiple rows to table model
     */
    public static void addRows(DefaultTableModel model, List<Object[]> rows) {
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }
    
    /**
     * Get selected row data as Object array
     */
    public static Object[] getSelectedRowData(javax.swing.JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return null;
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Object[] rowData = new Object[model.getColumnCount()];
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            rowData[i] = model.getValueAt(selectedRow, i);
        }
        
        return rowData;
    }
    
    /**
     * Search in table model by column value
     */
    public static int findRowByColumnValue(DefaultTableModel model, int columnIndex, Object value) {
        for (int i = 0; i < model.getRowCount(); i++) {
            Object cellValue = model.getValueAt(i, columnIndex);
            if (cellValue != null && cellValue.equals(value)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Update specific cell in table model
     */
    public static void updateCell(DefaultTableModel model, int row, int column, Object value) {
        if (row >= 0 && row < model.getRowCount() && column >= 0 && column < model.getColumnCount()) {
            model.setValueAt(value, row, column);
        }
    }
}