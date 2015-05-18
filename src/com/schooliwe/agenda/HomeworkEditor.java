package com.schooliwe.agenda;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.schooliwe.conf.ConfManager;
import com.schooliwe.resources.ResourcesManager;
import com.schooliwe.swing.util.JDateUtils;
import com.schooliwe.tests.SchoolIWE;

@SuppressWarnings("serial")
public class HomeworkEditor extends JDialog implements ActionListener, KeyListener {
	
	private static HomeworkEditor instance = null;
	private JPanel panel = new JPanel();
	private GridBagLayout layout = new GridBagLayout();
	private JLabel forDate = new JLabel("for date:");
	private JLabel topic = new JLabel("topic");
	private JTextArea text = new JTextArea();
	private JScrollPane textScroll = new JScrollPane(text);
	private ButtonGroup typeGroup = new ButtonGroup();
	private JRadioButton[] types = null;
	private JLabel[] typesIcons = null;
	private int defaultIndex = -1;
	private Homework homework = null;
	private Date homeworkDate = null;
	private HashMap<String, Icon> mapIcons = new HashMap<String, Icon>();
	private HashMap<String, String> mapLabels = new HashMap<String, String>();
	
	private JButton ok = new JButton(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "save"));
	private JButton cancel = new JButton(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "cancel"));
	
	
	private HomeworkEditor() {
		super(SchoolIWE.getMainFrame());
		panel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		panel.add(topic,c);
		c.gridy = 1;
		panel.add(forDate,c);
		// Create radioButtons from homework.xml
		Document doc = ResourcesManager.getInstance().getResource(ResourcesManager.HOMEWORK_FILE_ID);
		NodeList homeworkTypes = doc.getElementsByTagName("homework");
		int len = homeworkTypes.getLength();
		types = new JRadioButton[len];
		typesIcons = new JLabel[len];
		int nbRows = len/2 + len%2;
		int cptRow = 2;
		for (int i=0;i<len;i++) {
			Element homeworkType = (Element)homeworkTypes.item(i);
			types[i] = new JRadioButton(homeworkType.getAttribute("label"));
			if (defaultIndex==-1 && "yes".equals(homeworkType.getAttribute("default")))
				defaultIndex = i;
			typesIcons[i] = new JLabel();
			typesIcons[i].setIcon(new ImageIcon("images/"+homeworkType.getAttribute("icon")));
			types[i].setActionCommand(homeworkType.getAttribute("type"));
			types[i].addKeyListener(this);
			mapIcons.put(types[i].getActionCommand(), typesIcons[i].getIcon());
			mapLabels.put(types[i].getActionCommand(), types[i].getText());
			typeGroup.add(types[i]);
			c.gridwidth = 1;
			c.gridy = cptRow;
			panel.add(typesIcons[i],c);
			if (i%2==0 && i==len-1)
				c.gridwidth = 3;
			c.gridx++;
			panel.add(types[i],c);
			if (i%2!=0) {
				cptRow++;
				c.gridx = 0;
			}
			else
				c.gridx++;
		}
		types[defaultIndex].setSelected(true);
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy++;
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		textScroll.getViewport().add(text);
		textScroll.setPreferredSize(new Dimension(360,160));
		panel.add(textScroll,c);
		c.gridwidth = 2;
		c.gridy++;
		panel.add(ok,c);
		c.gridx = 3;
		panel.add(cancel,c);
		this.setModal(true);
		int x = (SchoolIWE.getMainFrame().getWidth()-400)/2;
		int y = (SchoolIWE.getMainFrame().getHeight()-(360+20*nbRows))/2;
		this.setBounds(x, y, 400, 360+20*nbRows);
		this.getContentPane().add(panel);
		this.setTitle(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "newHW"));
		ok.addActionListener(this);
		cancel.addActionListener(this);
		text.addKeyListener(this);
	}
	
	public static HomeworkEditor getInstance() {
		if (instance==null)
			instance = new HomeworkEditor();
		return instance;
	}
	
	public void setData(Homework data) {
		this.forDate.setText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "for")+" "+JDateUtils.fmtDayLongStr.format(data.getForDate()));
		this.topic.setText(data.getTopic());
		int len = types.length;
		for (int i=0;i<len;i++) {
			if (types[i].getActionCommand().equals(data.getType())) {
				types[i].setSelected(true);
				break;
			}
		}
		this.text.setText(data.getText());
		this.homework = data;
		this.homeworkDate = data.getForDate();
	}
	
	public void setNewData(Date dt, String topic) {
		this.forDate.setText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "for")+" "+JDateUtils.fmtDayLongStr.format(dt));
		this.topic.setText(topic);
		types[defaultIndex].setSelected(true);
		this.text.setText("");
		this.homework = null;
		this.homeworkDate = dt;
	}
	
	private void recordHomework() {
		if (this.homework==null)
			this.homework = new Homework();
		this.homework.setForDate(homeworkDate);
		this.homework.setText(this.text.getText());
		this.homework.setTopic(topic.getText());
		this.homework.setType(this.typeGroup.getSelection().getActionCommand());
		this.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==ok) {
			recordHomework();
		}
		else if (e.getSource()==cancel)
			this.setVisible(false);
	}

	public Homework getHomework() {
		return homework;
	}
	
	public Icon getIcon(String type) {
		return mapIcons.get(type);
	}

	public String getLabel(String type) {
		return mapLabels.get(type);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==(KeyEvent.VK_CONTROL&KeyEvent.VK_S) && !"".equals(this.text.getText()))
			recordHomework();
		else if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
			this.setVisible(false);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
