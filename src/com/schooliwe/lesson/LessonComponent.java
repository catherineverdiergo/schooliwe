package com.schooliwe.lesson;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import com.schooliwe.conf.ConfManager;
import com.schooliwe.swing.util.JDateUtils;

@SuppressWarnings("serial")
public class LessonComponent extends JPanel {

	private static final Logger logger = Logger.getLogger(LessonComponent.class);

	private static final Border componentBorderUnselected = BorderFactory.createRaisedBevelBorder();
	private static final Border componentBorderSelected = BorderFactory.createLoweredBevelBorder();
	protected JLabel dateLabel = new JLabel("",SwingConstants.CENTER);
	protected JLabel titleLabel = new JLabel("",SwingConstants.CENTER);
	private JMenuItem menuItem = null;

	private Lesson lesson;
	
	public LessonComponent() {
		super();
		this.add(dateLabel,BorderLayout.CENTER);
		this.add(titleLabel,BorderLayout.PAGE_END);
		this.setBorder(componentBorderUnselected);
		this.setSize(50, 50);
	}

	public LessonComponent(boolean arg0) {
		super(arg0);
		this.add(dateLabel,BorderLayout.CENTER);
		this.add(titleLabel,BorderLayout.PAGE_END);
		this.setBorder(componentBorderUnselected);
		this.setSize(50, 50);
	}

	public LessonComponent(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		this.add(dateLabel,BorderLayout.CENTER);
		this.add(titleLabel,BorderLayout.PAGE_END);
		this.setBorder(componentBorderUnselected);
		this.setSize(50, 50);
	}

	public LessonComponent(LayoutManager arg0) {
		super(arg0);
		this.add(dateLabel,BorderLayout.CENTER);
		this.add(titleLabel,BorderLayout.PAGE_END);
		this.setBorder(componentBorderUnselected);
		this.setSize(50, 50);
	}
	
	public void switchBorder() {
		if (this.getBorder()==componentBorderUnselected)
			this.setBorder(componentBorderSelected);
		else
			this.setBorder(componentBorderUnselected);
	}
	
	public void setUnselected() {
		this.setBorder(componentBorderUnselected);
	}

	public Lesson getLesson() {
		return lesson;
	}
	
	public void setDate(String date) {
		dateLabel.setAlignmentX(SwingConstants.CENTER);
		dateLabel.setText(date);
	}
	
	public void setTitle(String title) {
		titleLabel.setAlignmentX(SwingConstants.CENTER);
		titleLabel.setText(title);
		if (menuItem==null)
			menuItem = new JMenuItem();
		menuItem.setText(title+ " "+dateLabel.getText());
	}
	
	public String getName() {
		return titleLabel.getText().trim();
	}

	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
		this.add(new JLabel(new ImageIcon("images/folder.png"), SwingConstants.CENTER),BorderLayout.PAGE_START);
		setDate(JDateUtils.fmtDayFileStr.format(lesson.getDate()));
		setTitle(" "+lesson.getName()+" ");
	}
	
	public void rename(String topic,String newName) {
		try {
			String folderName = ConfManager.getSavedConfPath()+"/"+topic+"/"+dateLabel.getText()+"_"+getName();
			String targetName = ConfManager.getSavedConfPath()+"/"+topic+"/"+dateLabel.getText()+"_"+newName;
			File folder = new File(folderName);
			if (folder.exists() && folder.isDirectory())
				folder.renameTo(new File(targetName));
			setTitle(newName);
			lesson.setName(newName);
			this.repaint();
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	public JMenuItem getMenuItem() {
		return menuItem;
	}

}
