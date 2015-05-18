package com.schooliwe.timetable;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.schooliwe.conf.ConfManager;
import com.schooliwe.resources.ResourcesManager;

@SuppressWarnings("serial")
public class TimeTable extends JPanel implements KeyListener, ActionListener {

	private static final Logger logger = Logger.getLogger(TimeTable.class);

	private Document xmlDocument;
	private JTable table;
	private TimeTableModel model = null;
	private boolean shiftPressed = false;
	private JPanel bPanel = new JPanel();
	private JButton bSave = null;
	private JButton bPrint = null;
	private JScrollPane tablePane = null;
	
    private static final int defaultDocumentId = ResourcesManager.TIMETABLE_FILE_ID;

    public TimeTable() {
          this(ResourcesManager.getInstance().getResource(defaultDocumentId));
    }
    
	public void setXmlDocument(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
        this.xmlDocument.getDocumentElement().normalize();
	}
	
	public TimeTable(Document xmlDocument) {
		super();
		this.setXmlDocument(xmlDocument);
		this.table = new JTable();
		this.model = new TimeTableModel(xmlDocument);
		this.table.setModel(model);
		try {
			model.load();
		}
		catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		int nbRows = model.getRowCount();
		this.table.getTableHeader().setPreferredSize(new Dimension(30,50));
		this.table.getTableHeader().setDefaultRenderer(new TimeTableHeaderRenderer());
		for(int i=0;i<nbRows;i++) {
			if (model.getValueAt(i, 0) instanceof JTextField && "".equals(((JTextField)model.getValueAt(i,0)).getText()))
				this.table.setRowHeight(i,20);
			else
				this.table.setRowHeight(i,60);
		}
		this.table.setDefaultRenderer(Object.class, new TimeTableCellRenderer());
		this.table.setTransferHandler(new TimeTableTransferHandler());
		this.table.setCellSelectionEnabled(true);
		this.table.addKeyListener(this);
		this.tablePane = new JScrollPane();
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		this.tablePane.getViewport().add(table);
		bSave = new JButton(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "save"));
		bSave.addActionListener(this);
		bSave.setPreferredSize(new Dimension(100, 40));
		bPrint = new JButton(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "print"));
		bPrint.addActionListener(this);
		bPrint.setPreferredSize(new Dimension(100, 40));
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		this.add(tablePane,c);
		c.fill = GridBagConstraints.NONE;
		c.gridy = 1;
		c.weightx = 0.2;
		c.weighty = 0.2;
		this.add(bPanel,c);
		bPanel.setLayout(new FlowLayout());
		bPanel.add(bSave);
		bPanel.add(bPrint);
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public TimeTableModel getModel() {
		return model;
	}

	public void setModel(TimeTableModel model) {
		this.model = model;
	}

	public Document getXmlDocument() {
		return xmlDocument;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_DELETE && !this.table.getSelectionModel().isSelectionEmpty()) {
			// Clear all selected cells
		    int rowIndexStart = table.getSelectedRow();
		    int rowIndexEnd = table.getSelectionModel().getMaxSelectionIndex();
		    int colIndexStart = table.getSelectedColumn();
		    int colIndexEnd = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();
		    for (int i=rowIndexStart;i<=rowIndexEnd;i++) {
		    	for (int j=colIndexStart;j<=colIndexEnd;j++) {
		    		((TimeTableCellTopic)table.getValueAt(i, j)).clearTopics();
				    ((TimeTableModel)table.getModel()).fireTableCellUpdated(i, j);
		    	}
		    }
		}
		else if (e.getKeyCode()==KeyEvent.VK_SHIFT)
			shiftPressed = true;
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT)
			shiftPressed = false;
	}

	public boolean isShiftPressed() {
		return shiftPressed;
	}

//	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource()==bSave)
				((TimeTableModel)(this.table.getModel())).save();
			else if (e.getSource()==bPrint) {
				PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
				aset.add(OrientationRequested.LANDSCAPE);
				aset.add(new MediaPrintableArea(0f, 0f, getWidth(), getHeight(), MediaPrintableArea.INCH));
				this.table.print(JTable.PrintMode.NORMAL,null,null,true,aset,true);
			}
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
		}
	}
	
	public JTable getTopicsTable4Day(String localDay) {
		JTable result = new JTable();
		List<String> topics = ((TimeTableModel)this.getModel()).getTopic4Day(localDay);
		SubTimeTableModel model = new SubTimeTableModel();
		model.addColumn(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID,"topics"));
		if (topics.size() > 0) {
			int len = topics.size();
			for (int i=0;i<len;i++) {
				Object[] row = new Object[1];
				row[0] = topics.get(i);
				model.addRow(row);
			}
		}
		result.setModel(model);
		result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return result;
	}
	
}
