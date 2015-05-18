package com.schooliwe.timetable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class SubTimeTableCellRenderer implements TableCellRenderer {

	public SubTimeTableCellRenderer() {
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Object result = table.getModel().getValueAt(row, column);

		return (Component)result;
	}
	
}
