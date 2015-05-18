package com.schooliwe.swing.util;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
class SubTableModelExtract extends AbstractTableModel {
	
	private JTable table = null;
	private int[] columns=null;
	
	public SubTableModelExtract(JTable table, int[] columns) {
		super();
		this.table = table;
		this.columns = columns;
	}

	public int getRowCount() {
		return table.getModel().getRowCount();
	}

	public int getColumnCount() {
		return columns.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return table.getModel().getValueAt(rowIndex, columns[columnIndex]);
	}
	
}

public class JTableUtils {

	/**
	 * Build a JTable extracted from table parameter with given list of columns
	 * @param table
	 * @param columns
	 * @return
	 */
	public static final JTable subTableExtract(JTable table, int...columns) {
		JTable result = null;
		result = new JTable();
		// add needed columns
		for (int i=0;i<columns.length;i++) {
			String colName = table.getColumnName(i);
			TableColumn col = table.getColumn(colName);
			result.addColumn(col);
			result.setModel(new SubTableModelExtract(table, columns));
			result.getTableHeader().setVisible(false);
		}
		return result;
	}
	
	public static final boolean containsColumn(String column, String...columnNames) {
		boolean result = false;
		for (int i=0;i<columnNames.length;i++) {
			if (column.equalsIgnoreCase(columnNames[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Build a JTable extracted from table parameter with given list of columns
	 * @param table
	 * @param columns
	 * @return
	 */
	public static final JTable subTableExtract(JTable table, String...columnNames) {
		JTable result = null;
		result = new JTable();
		int [] columns = new int[columnNames.length];
		// add needed columns
		int len = table.getColumnCount();
		int cIndex = 0;
		for (int i=0;i<len;i++) {
			String colName = table.getColumnName(i);
			if (JTableUtils.containsColumn(colName,columnNames)) {
				columns[cIndex++] = i;
				TableColumn col = table.getColumn(colName);
				result.addColumn(col);
				result.setModel(new SubTableModelExtract(table, columns));
				result.setTableHeader(null);
			}
		}
		return result;
	}

}
