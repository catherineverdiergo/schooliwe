package com.schooliwe.timetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class TimeTableCellRenderer implements TableCellRenderer {

	public TimeTableCellRenderer() {
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Object result = table.getModel().getValueAt(row, column);

		if (column==0) {
			((JTextField)result).setFont(new Font("Arial", Font.BOLD, 16));
			if ("".equals(((JTextField)result).getText()))
				((JTextField)result).setBackground(Color.gray);
			else
				((JTextField)result).setBackground(Color.LIGHT_GRAY);
			((JTextField)result).setHorizontalAlignment(JTextField.CENTER);
		}
		else {
			if (result instanceof JTextField) {
				((JTextField)result).setBackground(Color.gray);
				((JTextField)result).setHorizontalAlignment(JTextField.CENTER);
			}
			else {
				((TimeTableCellTopic)result).setFont(new Font("Arial", Font.ITALIC, 12));
				if (isSelected)
					((TimeTableCellTopic)result).setBackground(Color.CYAN);
				((TimeTableCellTopic)result).setHorizontalAlignment(JTextField.CENTER);
			}
		}
		return (Component)result;
	}
	
}
