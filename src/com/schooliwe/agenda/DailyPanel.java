package com.schooliwe.agenda;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.schooliwe.conf.ConfManager;
import com.schooliwe.swing.util.JDateUtils;
import com.schooliwe.swing.util.TranslucentBorder;
import com.schooliwe.swing.util.TranslucentPanel;

@SuppressWarnings("serial")
public class DailyPanel extends JPanel implements ActionListener, MouseListener {
	
	private static final Logger logger = Logger.getLogger(DailyPanel.class);
	
	private static final int BORDER_SIZE_X = 22;
	private static final int BORDER_SIZE_Y = 14;

	private static final int BORDER1_X = 60;
	private static final int BORDER1_Y = 30;
	private static final int BORDER2_X = BORDER1_X+30;
	private static final int BORDER2_Y = BORDER1_Y+10;

	private BufferedImage image;
	private int width, height, x, y;
	private Date currentDate=null;
	private JPanel leftPanel = new JPanel();
	private TranslucentPanel centerPanel = new TranslucentPanel();
	private JPanel rightPanel = new JPanel();
	private JScrollPane leftScrollPane= new JScrollPane();
	private JScrollPane rightScrollPane= new JScrollPane();
	private JButton centerButton = new JButton(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "add"));
	private JTable homeworkTable = new JTable(new DefaultTableModel(new String[] {ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "homeworks")},0));
	
	private void setPanelSize(JPanel panel, JScrollPane scrollPane) {
		Dimension d1 = new Dimension(this.width/2-BORDER1_X,this.height-BORDER1_Y);
		panel.setMaximumSize(d1);
		panel.setMinimumSize(d1);
		panel.setPreferredSize(d1);
		if (scrollPane!=null) {
			Dimension d2 = new Dimension(this.width/2-BORDER2_X,this.height-BORDER2_Y);
			scrollPane.getViewport().setPreferredSize(d2);
			scrollPane.getViewport().setMaximumSize(d2);
			scrollPane.getViewport().setMinimumSize(d2);
		}
	}
	
	public DailyPanel() {
		super();
		try {
			image = ImageIO.read(new File("images/agenda.jpg"));
			this.x = this.y = 0;
			this.width = (int)(image.getWidth()*1.3);
			this.height = (int)(image.getHeight()*1.3);
			this.setLayout(new BorderLayout());
			if (this.getHeight()<this.height)
				this.y = 0;
			else {
				this.y = (int)((this.getHeight()-this.height)/2);
			}
			if (this.getWidth()<this.width)
				this.x = 0;
			else {
				this.x = (int)((this.getWidth()-this.width)/2);
			}
			this.setBorder(new TranslucentBorder(y+BORDER_SIZE_Y, x+BORDER_SIZE_X, y+BORDER_SIZE_Y, x+BORDER_SIZE_X));
			centerPanel.setBorder(new TranslucentBorder((this.height-50)/2, (this.width-50)/2, (this.height-50)/2, (this.width-50)/2));
			leftPanel.add(leftScrollPane);
			rightPanel.add(rightScrollPane);
			setPanelSize(leftPanel, leftScrollPane);
			setPanelSize(rightPanel, rightScrollPane);
			int widthCenterPanel = (int)(this.getSize().getWidth()-2*leftPanel.getSize().getWidth());
			int heightCenterPanel = (int)(leftPanel.getSize().getHeight());
			Dimension centerPanelDim = new Dimension(widthCenterPanel,heightCenterPanel);
			centerPanel.setMaximumSize(centerPanelDim);
			centerPanel.setMinimumSize(centerPanelDim);
			centerPanel.setPreferredSize(centerPanelDim);
			this.add(leftPanel,BorderLayout.LINE_START);
			centerPanel.add(centerButton);
			centerButton.addActionListener(this);
			centerButton.setEnabled(false);
			this.add(centerPanel,BorderLayout.CENTER);
			this.add(rightPanel,BorderLayout.LINE_END);
			homeworkTable.getColumnModel().getColumn(0).setCellRenderer(new HomeworkTableCellRenderer());
//			homeworkTable.setModel(new HomeworkTableModel());
			homeworkTable.setRowHeight(150);
			rightScrollPane.getViewport().add(homeworkTable);
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.getHeight()<this.height)
			this.y = 0;
		else {
			this.y = (int)((this.getHeight()-this.height)/2);
		}
		if (this.getWidth()<this.width)
			this.x = 0;
		else {
			this.x = (int)((this.getWidth()-this.width)/2);
		}
		g.drawImage(image, x, y, width, height,null);
		String vacString = JDateUtils.isVacationDate(currentDate);
		if (vacString!=null) {
			leftPanel.setVisible(false);
			rightPanel.setVisible(false);
			centerPanel.setVisible(false);
			String [] strs = vacString.split(";;");
			g.setFont(new Font("Arial",Font.BOLD,12));
			g.drawString(strs[0], x+50, y+50);
			g.drawString(strs[0], x+420, y+50);
			g.drawString(strs[0], x+50, y+this.height-50);
			g.drawString(strs[0], x+420, y+this.height-50);
			if (strs.length > 1) {
				try {
					BufferedImage img = ImageIO.read(new File("images/"+strs[1]));
					g.drawImage(img,x+65,y+67,245,345,null);
					g.drawImage(img,x+435,y+67,245,345,null);
				}
				catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		else {
			leftPanel.setVisible(true);
			centerPanel.setVisible(true);
			rightPanel.setVisible(true);
		}
		this.setBorder(new TranslucentBorder(y+BORDER_SIZE_Y, x+BORDER_SIZE_X, y+BORDER_SIZE_Y, x+BORDER_SIZE_X));
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	
	public void add2LeftScrollPane(JTable dayTable) {
		int len = leftScrollPane.getViewport().getComponentCount();
		for (int i=len-1;i>=0;i--)
			leftScrollPane.getViewport().remove(i);
		leftScrollPane.getViewport().add(dayTable);
		centerButton.setEnabled(false);
	}
	
	public void resizePanels() {
		logger.info("resizePanels ("+this.getWidth()+","+this.getHeight()+")");
		if (this.getHeight()<this.height)
			this.y = 0;
		else {
			this.y = (int)((this.getHeight()-this.height)/2);
		}
		if (this.getWidth()<this.width)
			this.x = 0;
		else {
			this.x = (int)((this.getWidth()-this.width)/2);
		}
		this.setBorder(new TranslucentBorder(y+BORDER_SIZE_Y,x+BORDER_SIZE_X,y+BORDER_SIZE_Y,x+BORDER_SIZE_X));
	}
	
	private void addHomework() {
		JTable topicTable = (JTable)leftScrollPane.getViewport().getComponent(0);
		int selRow = topicTable.getSelectedRow();
		String value = (String)topicTable.getValueAt(selRow, 0);
		HomeworkEditor.getInstance().setNewData(this.currentDate, value);
		HomeworkEditor.getInstance().setVisible(true);
		Homework homework = HomeworkEditor.getInstance().getHomework();
		if (homework != null) {
			Homework[] newData = new Homework[1];
			newData[0] = homework;
			((DefaultTableModel)homeworkTable.getModel()).addRow(newData);
			this.save();
		}		
	}

//	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==centerButton) {
			addHomework();
		}
	}

	public JButton getCenterButton() {
		return centerButton;
	}

	public JTable getHomeworkTable() {
		return homeworkTable;
	}
	
	/**
	 * Save current page for agenda
	 */
	public void save() {
		String month = JDateUtils.fmtMonthLongStr.format(this.currentDate);
		String targetFilePath = ConfManager.getSavedConfPath()+"/"+month+".xls";
		String targetSheetName = JDateUtils.fmtDaySheetStr.format(this.currentDate);
		File targetFile = new File(targetFilePath);
		try {
			HSSFWorkbook wb = null;
			InputStream is = null;
			if (targetFile.exists() && targetFile.isFile()) {
				is = new FileInputStream(targetFile);
				wb = new HSSFWorkbook(is);
			}
			else
				wb = new HSSFWorkbook();
			// retrieve related sheet
			int nbSheets = wb.getNumberOfSheets();
			HSSFSheet sheet = null;
			for (int i=0;i<nbSheets;i++) {
				sheet = wb.getSheetAt(i);
				if (targetSheetName.equals(sheet.getSheetName())) {
					wb.removeSheetAt(i);
					break;
				}
			}
			// create sheet
			sheet = wb.createSheet(targetSheetName);
			int len = homeworkTable.getRowCount();
			for (int i=0;i<len;i++) {
				Homework homework = (Homework)homeworkTable.getValueAt(i, 0);
				HSSFRow row = sheet.createRow(i);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue(homework.getTopic());
				cell = row.createCell(1);
				cell.setCellValue(homework.getType());
				cell = row.createCell(2);
				cell.setCellValue(homework.getText());
			}
			if (is!=null)
				is.close();
			FileOutputStream fileOut = new FileOutputStream(targetFilePath);
			wb.write(fileOut);
			fileOut.close();
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * Save current page for agenda
	 */
	public void load() {
		// clear homeWorkTable
		DefaultTableModel model = ((DefaultTableModel)this.homeworkTable.getModel());
		while(model.getRowCount() > 0)
		    model.removeRow(0);
		model.fireTableDataChanged();
		// Retrieve data from date
		String month = JDateUtils.fmtMonthLongStr.format(this.currentDate);
		String targetFilePath = ConfManager.getSavedConfPath()+"/"+month+".xls";
		String targetSheetName = JDateUtils.fmtDaySheetStr.format(this.currentDate);
		File targetFile = new File(targetFilePath);
		try {
			HSSFWorkbook wb = null;
			InputStream is = null;
			if (targetFile.exists() && targetFile.isFile()) {
				is = new FileInputStream(targetFile);
				wb = new HSSFWorkbook(is);
			}
			if (wb != null) {
				// retrieve related sheet
				int nbSheets = wb.getNumberOfSheets();
				HSSFSheet sheet = null;
				boolean sheetFound = false;
				for (int i=0;i<nbSheets;i++) {
					sheet = wb.getSheetAt(i);
					if (targetSheetName.equals(sheet.getSheetName())) {
						sheetFound = true;
						break;
					}
				}
				if (sheetFound) {
					int nbRows = sheet.getLastRowNum()+1;
					for (int i=0;i<nbRows;i++) {
						HSSFRow row = sheet.getRow(i);
						Homework homework = new Homework();
						homework.setForDate(this.currentDate);
						homework.setTopic(row.getCell(0).getStringCellValue());
						homework.setType(row.getCell(1).getStringCellValue());
						homework.setText(row.getCell(2).getStringCellValue());
						Homework[] newData = new Homework[1];
						newData[0] = homework;
						((DefaultTableModel)homeworkTable.getModel()).addRow(newData);
					}
				}
			}
			if (is!=null)
				is.close();
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount()==2) {
			JTable topicTable = (JTable)leftScrollPane.getViewport().getComponent(0);
			if (topicTable.getSelectedRowCount()>0)
				addHomework();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
}
