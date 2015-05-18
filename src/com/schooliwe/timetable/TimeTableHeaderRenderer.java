package com.schooliwe.timetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class TimeTableHeaderRenderer extends JTextField implements
		TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
	       // Configure the component with the specified value
		setFont(new Font("Arial",Font.BOLD,16));
		setBackground(Color.LIGHT_GRAY);
		setHorizontalAlignment(JLabel.CENTER);
        setText(value.toString());
        // Set tool tip if desired
        setToolTipText((String)value);
        // Since the renderer is a component, return itself
        return this;
	}

}
