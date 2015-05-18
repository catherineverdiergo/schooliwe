/**
 * 
 */
package com.schooliwe.agenda;

import java.util.Date;

import com.schooliwe.swing.util.JDateUtils;

/**
 * @author catherine
 *
 */
public class Homework {
	
	private String type = null;
	private Date forDate = null;
	private String text = null;
	private String topic = null;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Date getForDate() {
		return forDate;
	}
	
	public void setForDate(Date forDate) {
		this.forDate = forDate;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String toString() {
		return (JDateUtils.fmtDayLongStr.format(forDate) +","+topic+","+type+","+text);
	}
	
}
