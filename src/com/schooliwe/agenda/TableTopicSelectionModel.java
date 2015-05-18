package com.schooliwe.agenda;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.schooliwe.action.ActionObjectManager;

public class TableTopicSelectionModel implements ListSelectionListener {
	
	private JTable table;
	
	public TableTopicSelectionModel(JTable table) {
		super();
		this.table = table;
	}

	public void valueChanged(ListSelectionEvent e) {
        int[] selectedRow = this.table.getSelectedRows();
        int[] selectedColumns = this.table.getSelectedColumns();
       	DailyAgenda agenda = (DailyAgenda)ActionObjectManager.getInstance().getActionObject("agenda");
       	agenda.getDailyPanel().getCenterButton().setEnabled(selectedColumns.length==1 &&selectedRow.length==1);
	}

}
