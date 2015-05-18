package com.schooliwe.agenda;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.schooliwe.action.ActionObjectManager;
import com.schooliwe.conf.ConfManager;
import com.schooliwe.swing.util.JDateUtils;
import com.schooliwe.timetable.TimeTable;
import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class DailyAgenda extends JPanel implements PropertyChangeListener, ActionListener {
	
	private BorderLayout layout = new BorderLayout();
	private JDateChooser dateChooser = new JDateChooser();
	private Date selectedDate = new Date();
	private JScrollPane scrollPane = new JScrollPane();
	private DailyPanel dailyPanel = new DailyPanel();
	private JPanel footer = new JPanel();
	private JButton prevWeekButton = new JButton(new ImageIcon("images/previous_week.png"));
	private JButton prevButton = new JButton(new ImageIcon("images/previous.png"));
	private JButton nextWeekButton = new JButton(new ImageIcon("images/next_week.png"));
	private JButton nextButton = new JButton(new ImageIcon("images/next.png"));
	
	public DailyAgenda() {
		super();
		this.setLayout(layout);
		this.add(dateChooser,BorderLayout.PAGE_START);
		scrollPane.getViewport().add(dailyPanel);
		dailyPanel.setCurrentDate(selectedDate);
		this.add(scrollPane,BorderLayout.CENTER);
		footer.setLayout(new FlowLayout());
		footer.add(prevWeekButton);
		prevWeekButton.setToolTipText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "prev_week"));
		footer.add(prevButton);
		prevButton.setToolTipText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "prev"));
		footer.add(nextButton);
		nextButton.setToolTipText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "next"));
		footer.add(nextWeekButton);
		nextWeekButton.setToolTipText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "next_week"));
		this.add(footer,BorderLayout.PAGE_END);
		dateChooser.setDate(selectedDate);
		dateChooser.setFont(new Font("Arial",Font.BOLD,16));
		dateChooser.setForeground(Color.BLUE);
		dateChooser.setDateFormatString("EEEEEEEEEE dd MMMMMMMMM yyyy");
		dateChooser.addPropertyChangeListener(this);
		prevButton.addActionListener(this);
		nextButton.addActionListener(this);
		prevWeekButton.addActionListener(this);
		nextWeekButton.addActionListener(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		Date dt = dateChooser.getDate();
		String localDay = JDateUtils.getLocalDay(dt);
		TimeTable timetable = (TimeTable)ActionObjectManager.getInstance().getActionObject("timetable");
		JTable tb = timetable.getTopicsTable4Day(localDay);
//		int nbRows = tb.getRowCount();
//		for(int i=0;i<nbRows;i++) {
//			tb.setRowHeight(i,60);
//		}
//		tb.setDefaultRenderer(Object.class, new SubTimeTableCellRenderer());
		tb.getSelectionModel().addListSelectionListener(new TableTopicSelectionModel(tb));
		tb.addMouseListener(dailyPanel);
		dailyPanel.add2LeftScrollPane(tb);
		dailyPanel.setCurrentDate(dateChooser.getDate());
		dailyPanel.repaint();
	}
	
	public void resizePanels() {
		dailyPanel.resizePanels();
	}

//	@Override
	public void actionPerformed(ActionEvent e) {
		Date currentDate = this.dateChooser.getDate();
		long currentTime = currentDate.getTime();
		if (e.getSource()==prevWeekButton) {
			this.dateChooser.setDate(new Date(currentTime-7*24*3600*1000));
			this.dailyPanel.load();
		}
		else if (e.getSource()==prevButton) {
			this.dateChooser.setDate(new Date(currentTime-24*3600*1000));
			this.dailyPanel.load();
		}
		else if (e.getSource()==nextButton) {
			this.dateChooser.setDate(new Date(currentTime+24*3600*1000));
			this.dailyPanel.load();
		}
		else if (e.getSource()==nextWeekButton) {
			this.dateChooser.setDate(new Date(currentTime+7*24*3600*1000));
			this.dailyPanel.load();
		}
	}

	public DailyPanel getDailyPanel() {
		return dailyPanel;
	}

}
