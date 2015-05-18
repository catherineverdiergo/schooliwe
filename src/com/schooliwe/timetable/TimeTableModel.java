package com.schooliwe.timetable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.schooliwe.conf.ConfManager;
import com.schooliwe.tree.TopicTree;

@SuppressWarnings("serial")
public class TimeTableModel extends AbstractTableModel {

	private static final Logger logger = Logger.getLogger(TimeTableModel.class);

	private Document xmlDocument;
	private String[] columns = null;
	private Object[][] data = null;
	private int nbRows = -1;
	private Date todayTimeZero = null;
	private Date timeunit = null;
	
	private SimpleDateFormat dfmt = null;
	private SimpleDateFormat dfmtTimeZero = new SimpleDateFormat("HH:mm");

    public TimeTableModel(Document xmlDocument) {
		super();
		this.setXmlDocument(xmlDocument);
	}
	
	public void setXmlDocument(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
        this.xmlDocument.getDocumentElement().normalize();
        String todayTimeZeroStr = "00:00";
        try {
        	todayTimeZero = dfmtTimeZero.parse(todayTimeZeroStr);
        }
        catch (ParseException e) {
        	logger.error(e.getMessage(),e);
        }
	}
	
	public SimpleDateFormat getDfmt() {
		if (dfmt==null) {
			NodeList nl = xmlDocument.getElementsByTagName("timeformat");
			Element dfmtElement = (Element)nl.item(0);
			dfmt = new SimpleDateFormat(dfmtElement.getAttribute("value"));
		}
		return dfmt;
	}
	
	public String[] getColumns() {
		if (columns==null) {
			NodeList days = xmlDocument.getElementsByTagName("day");
	    	int nbDays= days.getLength();
	    	columns = new String[nbDays+1];
	    	columns[0] = ((Element)xmlDocument.getElementsByTagName("timetitle").item(0)).getAttribute("value").toString();
	        for (int i=0;i<nbDays;i++) {
	        	Element day = (Element)days.item(i);
	        	columns[i+1] = day.getAttribute("name");
	        }
		}
		return columns;
	}
	
	public Date getTimeUnit() {
		if (timeunit==null) {
			Element timeUnitElement = (Element)xmlDocument.getElementsByTagName("timeunit").item(0);
			try {
				timeunit = getDfmt().parse(timeUnitElement.getAttribute("value"));
				long timeunitval = timeunit.getTime()-todayTimeZero.getTime();
				timeunit.setTime(timeunitval);
			}
			catch(Exception e) {
	        	logger.error(e.getMessage(),e);
			}
		}
		return timeunit;
	}
	
	public Date getDate(Element timeInterval, String dateName) {
		Date dt = null;
		try {
			dt = getDfmt().parse(timeInterval.getAttribute(dateName));
		}
		catch(Exception e) {
        	logger.error(e.getMessage(),e);
		}
		return dt;
	}
	
	public int getNbRows(Element interval) {
		Date open = getDate(interval, "opentime"); 
		Date close = getDate(interval, "closetime");
		long linterval = close.getTime()-open.getTime();
		return (int)(linterval/getTimeUnit().getTime());
	}
	
	public int getRowCount() {
		if (nbRows==-1) {
			NodeList intervals = xmlDocument.getElementsByTagName("timeinterval");
			int len = intervals.getLength();
			nbRows = len-1;
			for (int i=0;i<len;i++) {
				Element interval = (Element)intervals.item(i);
				nbRows += getNbRows(interval);
			}
		}
		return nbRows;
	}
	
	public int getColumnCount() {
		return getColumns().length;
	}
	
	public Object[][] getData() {
		if (data==null) {
			data = new Object[getRowCount()][getColumnCount()];
			NodeList intervals = xmlDocument.getElementsByTagName("timeinterval");
			int len = intervals.getLength();
			int row = 0;
			int nbrows = 0;
			for (int i=0;i<len;i++) {
				Element interval = (Element)intervals.item(i);
				nbrows = getNbRows(interval);
				for (int j=row;j<row+nbrows;j++) {
					Date dt = getDate(interval, "opentime");
					TimeTableCell cell = new TimeTableCell();
					cell.setDtfmt(getDfmt());
					long time = dt.getTime()+(j-row)*getTimeUnit().getTime();
					cell.setOpenTime(new Date(time));
					time = dt.getTime()+(j-row+1)*getTimeUnit().getTime();
					cell.setCloseTime(new Date(time));
					data[j][0] = cell;
					for (int k=1;k<getColumnCount();k++) {
						data[j][k] = new TimeTableCellTopic();
					}
				}
				row += nbrows;
				if (row < getRowCount()) {
					for (int k=0;k<getColumnCount();k++) {
						data[row][k] = new JTextField("");
					}
				}
				row++;
			}
		}
		return data;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (data==null)
			getData();
		Object result = data[rowIndex][columnIndex];
		return result;
	}
	
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = value;
	}

	@Override
	public String getColumnName(int column) {
		return getColumns()[column];
	}
	
	private static final int HEADER_STYLE = 0;
	private static final int EMPTY_STYLE = 1;
	private static final int TOPIC_STYLE = 2;
	private static final short BORDER_SIZE = HSSFCellStyle.BORDER_THIN;
	
	private HSSFCellStyle[] createStyles(HSSFWorkbook wb) {
		HSSFCellStyle[] result = new HSSFCellStyle[3];
		
		result[HEADER_STYLE] = wb.createCellStyle();
		result[HEADER_STYLE].setAlignment(CellStyle.ALIGN_CENTER);
		result[HEADER_STYLE].setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		result[HEADER_STYLE].setFillPattern(HSSFCellStyle.FINE_DOTS);
		result[HEADER_STYLE].setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
		result[HEADER_STYLE].setBorderBottom(BORDER_SIZE);
		result[HEADER_STYLE].setBorderLeft(BORDER_SIZE);
		result[HEADER_STYLE].setBorderRight(BORDER_SIZE);
		result[HEADER_STYLE].setBorderTop(BORDER_SIZE);
		HSSFFont f1 = wb.createFont();
		f1.setFontName("Arial");
		f1.setBoldweight(Font.BOLDWEIGHT_BOLD);
		f1.setFontHeight((short)250);
		result[HEADER_STYLE].setFont(f1);
		
		result[EMPTY_STYLE] = wb.createCellStyle();
		result[EMPTY_STYLE].setFillPattern(HSSFCellStyle.FINE_DOTS);
		result[EMPTY_STYLE].setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		
		result[TOPIC_STYLE] = wb.createCellStyle();
		result[TOPIC_STYLE].setAlignment(CellStyle.ALIGN_CENTER);
		result[TOPIC_STYLE].setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		result[TOPIC_STYLE].setBorderBottom(BORDER_SIZE);
		result[TOPIC_STYLE].setBorderLeft(BORDER_SIZE);
		result[TOPIC_STYLE].setBorderRight(BORDER_SIZE);
		result[TOPIC_STYLE].setBorderTop(BORDER_SIZE);
//		result[TOPIC_STYLE].setFillPattern(HSSFCellStyle.FINE_DOTS);
//		result[TOPIC_STYLE].setFillBackgroundColor(HSSFColor.WHITE.index);
		HSSFFont f2 = wb.createFont();
		f2.setFontName("Arial");
		f2.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		f2.setFontHeight((short)200);
		result[TOPIC_STYLE].setFont(f2);
		return result;
	}
	
	public void save() throws IOException {
		String timeTablePath = ConfManager.getSavedConfPath()+"/timetable.xls";
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.getPrintSetup().setLandscape(true);
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle[] styles = createStyles(wb);
		row.setHeight((short)500);
		for (int j=0;j<this.getColumnCount();j++) {
			if (j==0)
				sheet.setColumnWidth(j, 4500);
			else
				sheet.setColumnWidth(j, 5000);
		}
		for (int i=0;i<this.getColumnCount();i++) {
			String title = this.getColumns()[i];
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(title);
			cell.setCellStyle(styles[HEADER_STYLE]);
		}
		for (int i=0;i<this.getRowCount();i++) {
			row = sheet.createRow(i+1);
			Object value = this.getValueAt(i, 0);
			if (value instanceof TimeTableCell)
				row.setHeight((short)1000);
			for (int j=0;j<this.getColumnCount();j++) {
				HSSFCell cell = row.createCell(j);
				String valueStr = "";
				value = this.getValueAt(i, j);
				if (value instanceof TimeTableCellTopic) {
					valueStr = ((TimeTableCellTopic)value).toPrintableString();
					cell.setCellStyle(styles[TOPIC_STYLE]);
				}
				else if (value instanceof TimeTableCell) {
					valueStr = value.toString();
					if (j==0)
						cell.setCellStyle(styles[HEADER_STYLE]);
					else
						cell.setCellStyle(styles[TOPIC_STYLE]);
				}
				else if (value instanceof JTextField) {
					valueStr = ((JTextField)value).getText();
					cell.setCellStyle(styles[EMPTY_STYLE]);
				}
				cell.setCellValue(valueStr);
			}
		}
		FileOutputStream fileOut = new FileOutputStream(timeTablePath);
		wb.write(fileOut);
		fileOut.close();
	}
	
	public void load() throws IOException {
		String timeTablePath = ConfManager.getSavedConfPath()+"/timetable.xls";
		File timeTableFile = new File(timeTablePath);
		if (timeTableFile.exists() && timeTableFile.isFile()) {
			InputStream is = new FileInputStream(timeTableFile);
			HSSFWorkbook wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			for (int rowNb=1;rowNb<10;rowNb++) {
				if (rowNb!=5) {
					HSSFRow row = sheet.getRow(rowNb);
					for (int cellNb=1;cellNb<6;cellNb++) {
						HSSFCell cell = row.getCell(cellNb);
						String valueStr = cell.getStringCellValue();
						String[] cellValues = valueStr.split("\n");
						for (int topicNb=0;topicNb<cellValues.length;topicNb++) {
							String topic = cellValues[topicNb].replace(" "+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID,"msg_semB"), "");
							topic = topic.replace(" "+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID,"msg_semA"), "");
							((TimeTableCellTopic)this.getValueAt(rowNb-1, cellNb)).addTopic(topic);
						}
					}
				}
			} 
			is.close();
		}
	}
	
	public List<String> getTopic4Day(String localDay) {
		List<String> result = new ArrayList<String>();
		int len = this.getColumnCount();
		for (int i=1;i<len;i++) {
			if (this.getColumnName(i).equals(localDay)) {
				int nbRows = this.getRowCount();
				for (int j=0;j<nbRows;j++) {
					Object value = this.getValueAt(j, i);
					if (value!=null && value instanceof TimeTableCellTopic) {
						if (((TimeTableCellTopic)value).getNbTopic() > 0) {
							String[] topics = ((TimeTableCellTopic)value).getTopicsStrings();
							for (int k=0;k<topics.length;k++) {
								if (topics[k]!=null && !"".equals(topics[k]) && !TopicTree.isVacantTopic(topics[k]) && !result.contains(topics[k]))
									result.add(topics[k]);
							}
						}
					}
				}
			}
		}
		return result;
	}

}
