package com.schooliwe.agenda;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class HomeworkTableModel extends DefaultTableModel {

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
}
