package com.schooliwe.timetable;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TimeTableCell extends JTextField {
	
	private Date openTime = null;
	private Date closeTime = null;
	private SimpleDateFormat dtfmt = null;
	
	public Date getOpenTime() {
		return openTime;
	}
	
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	
	public Date getCloseTime() {
		return closeTime;
	}
	
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
		this.setText(this.toString());
	}

	public void setDtfmt(SimpleDateFormat dtfmt) {
		this.dtfmt = dtfmt;
	}
	
	public String toString() {
		String dtStr1 = dtfmt.format(openTime);
		String dtStr2 = dtfmt.format(closeTime);
		return (dtStr1+" - "+dtStr2);
	}
	
}
