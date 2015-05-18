package com.schooliwe.timetable;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class SubTimeTableModel extends DefaultTableModel {

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

}
