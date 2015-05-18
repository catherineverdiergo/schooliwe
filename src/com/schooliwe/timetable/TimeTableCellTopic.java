package com.schooliwe.timetable;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.schooliwe.conf.ConfManager;

@SuppressWarnings("serial")
public class TimeTableCellTopic extends JPanel {
	
	private int nbTopic;
	private JTextField[] topics = null;
	
	public TimeTableCellTopic() {
		nbTopic = 0;
		topics = new JTextField[2];
		topics[0] = new JTextField("");
		topics[1] = new JTextField("");
		this.setLayout(new GridLayout(1, 1));
	}

	public int getNbTopic() {
		return nbTopic;
	}
	
	public void setNbTopic(int nbTopic) {
		this.nbTopic = nbTopic;
	}
	
	public void addTopic(String topic) {
		if (nbTopic<2) {
			if (nbTopic==1) {
				this.setLayout(new GridLayout(2, 1));
				topics[nbTopic].setText(topic+" "+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID,"msg_semB"));
				topics[nbTopic-1].setText(topics[nbTopic-1].getText()+" "+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID,"msg_semA"));
			}
			else
				topics[nbTopic].setText(topic);
			this.add(topics[nbTopic]);
			nbTopic++;
		}
	}

	public void cancelReplaceTopic(String topic) {
		clearTopics();
		addTopic(topic);
	}

	public void clearTopics() {
		topics[0].setText("");
		topics[1].setText("");
		for (int i=0;i<nbTopic;i++)
			this.remove(topics[i]);
		nbTopic = 0;
		this.setLayout(new GridLayout(1, 1));
	}

	public JTextField[] getTopics() {
		return topics;
	}
	
	public void setFont(Font f) {
		super.setFont(f);
		if (nbTopic > 0)
			topics[0].setFont(f);
		if (nbTopic > 1)
			topics[1].setFont(f);
	}
	
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (nbTopic > 0)
			topics[0].setBackground(bg);
		if (nbTopic > 1)
			topics[1].setBackground(bg);
	}
	
	public void setHorizontalAlignment(int alignment) {
		if (nbTopic > 0)
			topics[0].setHorizontalAlignment(alignment);
		if (nbTopic > 1)
			topics[1].setHorizontalAlignment(alignment);
	}
	
	public String toPrintableString() {
		String result = "";
		if (nbTopic==1)
			result = topics[0].getText();
		else if (nbTopic==2)
			result = topics[0].getText()+"\n"+topics[1].getText();
		return result;
	}
	
	public String[] getTopicsStrings() {
		String[] result = new String[nbTopic];
		if (nbTopic==1)
			result[0] = topics[0].getText();
		else if (nbTopic == 2) {
			result[0] = topics[0].getText().replace(" "+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID,"msg_semA"), "");
			result[1] = topics[1].getText().replace(" "+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID,"msg_semB"), "");
		}
		return result;
	}
	
}
