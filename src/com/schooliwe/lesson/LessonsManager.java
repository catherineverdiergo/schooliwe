package com.schooliwe.lesson;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.schooliwe.agenda.LessonComparator;
import com.schooliwe.conf.ConfManager;
import com.schooliwe.resources.ResourcesManager;
import com.schooliwe.swing.util.JDateUtils;
import com.schooliwe.swing.util.MenuItem;
import com.schooliwe.swing.util.ModifiedFlowLayout;
import com.schooliwe.tests.SchoolIWE;
import com.schooliwe.tree.TopicPopupMenu;

@SuppressWarnings("serial")
public class LessonsManager extends JPanel implements MouseListener, ActionListener, KeyListener {

	private static final Logger logger = Logger.getLogger(LessonsManager.class);

	private String currentTopic=null;
	private LessonComponent currentLessonComp = null;
	private JPanel headerPanel = new JPanel(new BorderLayout());
	private JLabel title = new JLabel("",SwingConstants.CENTER);
	private JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
	private JScrollPane scrollPane = new JScrollPane();
	private HashMap<String, TreeSet<Lesson>> lessonsByTopic = new HashMap<String, TreeSet<Lesson>>();
	private JPanel lessonPanel = new JPanel();
	private ModifiedFlowLayout layout = new ModifiedFlowLayout(FlowLayout.LEADING);
	private LessonComponent selectedComp = null;
	private int selectedCompX, selectedCompY;
	private JPopupMenu renamePopup = new JPopupMenu();
	private JTextField renameText = new JTextField();
	private LessonComponent renamedComp = null;
	private JFileChooser fileChooser = new JFileChooser();
	private FileFilter fileFilter = new FileFilter() {
		
		@Override
		public String getDescription() {
			return ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "validschooliwefiles");
		}
		
		@Override
		public boolean accept(File f) {
			if (!f.isFile())
				return true;
			int extIndex = f.getName().lastIndexOf(".");
			if (extIndex == -1)
				return false;
			String extension = f.getName().substring(extIndex,f.getName().length());
			return SoftwareManager.isValidExtension(extension);
		}
	};
	
	private JButton upButton = new JButton(new ImageIcon("images/up.png"));
	private JButton downButton = new JButton(new ImageIcon("images/down.png"));
	private JButton newLessonButton = new JButton(new ImageIcon("images/new_folder.png"));
	private JButton renameButton = new JButton(new ImageIcon("images/rename.png"));
	private JButton delLessonButton = new JButton(new ImageIcon("images/delete_folder.png"));
	private JButton newDocButton = new JButton(new ImageIcon("images/new_document.png"));
	private JButton delDocButton = new JButton(new ImageIcon("images/delete_document.png"));
	private JButton impDocButton = new JButton(new ImageIcon("images/import_document.png"));
	private JButton moveButton = new JButton(new ImageIcon("images/move.png"));
	
	private JPopupMenu newDocMenu = new JPopupMenu();
	
	private JButton[] lessonMenu = {
		upButton,
		downButton,
		newLessonButton,
		renameButton,
		delLessonButton,
		moveButton
	};
	
	private String[] lessonMenuTooltips = {
			"up",
			"down",
			"newlesson",
			"rename",
			"dellesson",
			"move"
	};
	
	private JButton[] docMenu = {
			upButton,
			downButton,
			newDocButton,
			renameButton,
			impDocButton,
			delDocButton,
			moveButton
		};
		
	private String[] docMenuTooltips = {
			"up",
			"down",
			"newdoc",
			"rename",
			"impdoc",
			"deldoc",
			"move"
	};
	
	private void browseTopic(String topic) {
		try {
			String topicDirPath = ConfManager.getSavedConfPath()+"/"+topic;
			File topicDirFile = new File(topicDirPath);
			lessonsByTopic.put(topic, new TreeSet<Lesson>(new LessonComparator()));
			if (topicDirFile.exists() && topicDirFile.isDirectory()) {
				// Get Lessons (date + names)
				File[] lessonsDir = topicDirFile.listFiles();
				for (int i=0;i<lessonsDir.length;i++) {
					File lessonDir = new File(lessonsDir[i].getAbsolutePath());
					if (lessonDir.isDirectory()) {
						String[] fileNameProps = lessonsDir[i].getName().split("_");
						Lesson lesson = new Lesson(topic, fileNameProps[1], JDateUtils.fmtDayFileStr.parse(fileNameProps[0]));
						File lessonFiles[] = lessonDir.listFiles();
						for (int j=0;j<lessonFiles.length;j++) {
							lesson.addFile(lessonFiles[j].getAbsolutePath());
						}
						lessonsByTopic.get(topic).add(lesson);
					}
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public LessonsManager() {
		Document topicDoc = ResourcesManager.getInstance().getResource(ResourcesManager.TOPICS_FILE_ID);
		NodeList topicList = topicDoc.getElementsByTagName("topic");
		int len = topicList.getLength();
		for (int i=0;i<len;i++) {
			Element topicE = (Element)topicList.item(i);
			boolean topicFound = false;
			if ("y".equals(topicE.getAttribute("lesson"))) {
				browseTopic(topicE.getAttribute("name"));
				if (!topicFound) {
					currentTopic = topicE.getAttribute("name");
					topicFound = true;
				}
			}
		}
		lessonPanel.setLayout(layout);
		scrollPane.getViewport().add(lessonPanel);
		if (currentTopic!=null) {
			viewTopic();
		}
		Iterator<String> it = SoftwareManager.getInstance().getSoftTypeLabels().keySet().iterator();
		while(it.hasNext()) {
			String typDoc = it.next();
			String label = SoftwareManager.getInstance().getSoftTypeLabels().get(typDoc);
			ImageIcon img = SoftwareManager.getInstance().getSoftwareByType(typDoc).getIcon();
			MenuItem item = new MenuItem();
			item.setText(label);
			item.setIcon(img);
			item.setCode(typDoc);
			item.addActionListener(this);
			newDocMenu.add(item);
		}
		this.setLayout(new BorderLayout());
		this.add(scrollPane,BorderLayout.CENTER);
		Dimension buttonDim = new Dimension(26,26);
		for (int i=0;i<lessonMenu.length;i++)
			lessonMenu[i].setPreferredSize(buttonDim);
		for (int i=0;i<docMenu.length;i++)
			docMenu[i].setPreferredSize(buttonDim);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		newDocButton.addMouseListener(this);
		newLessonButton.addActionListener(this);
		renameButton.addActionListener(this);
		delLessonButton.addActionListener(this);
		delDocButton.addActionListener(this);
		impDocButton.addActionListener(this);
		moveButton.addMouseListener(this);
		toolPanel.add(upButton);
		toolPanel.add(downButton);
		headerPanel.add(title,BorderLayout.PAGE_START);
		headerPanel.add(toolPanel,BorderLayout.CENTER);
		this.add(headerPanel,BorderLayout.PAGE_START);
		renamePopup.add(renameText);
		renameText.addKeyListener(this);
		fileChooser.setCurrentDirectory( new File( "." ) );  
		fileChooser.setFileFilter(fileFilter);
	}
	
	private void viewLessonMenu() {
		toolPanel.removeAll();
		for (int i=0;i<lessonMenu.length;i++) {
			toolPanel.add(lessonMenu[i]);
			lessonMenu[i].setEnabled(false);
			lessonMenu[i].setToolTipText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, lessonMenuTooltips[i]));
		}
		newLessonButton.setEnabled(true);
		toolPanel.validate();
		toolPanel.repaint();
	}

	private void viewDocMenu() {
		toolPanel.removeAll();
		for (int i=0;i<docMenu.length;i++) {
			toolPanel.add(docMenu[i]);
			docMenu[i].setEnabled(false);
			docMenu[i].setToolTipText(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, docMenuTooltips[i]));
		}
		newDocButton.setEnabled(true);
		impDocButton.setEnabled(true);
		upButton.setEnabled(true);
		toolPanel.validate();
		toolPanel.repaint();
	}

	public String getCurrentTopic() {
		return currentTopic;
	}

	public void setCurrentTopic(String currentTopic) {
		this.currentTopic = currentTopic;
		title.setText(currentTopic);
		viewTopic();
	}
	
	public void viewTopic() {
		lessonPanel.removeAll();
		if (currentTopic!=null) {
			TreeSet<Lesson> topicLessons = lessonsByTopic.get(currentTopic);
			Iterator<Lesson> it = topicLessons.iterator();
			while (it.hasNext()) {
				Lesson lesson = it.next();
				JPanel cmp = (JPanel)lesson.getComponent();
				cmp.addMouseListener(this);
				lessonPanel.add(cmp);
			}
			lessonPanel.validate();
			lessonPanel.repaint();
			scrollPane.validate();
			scrollPane.repaint();
			title.setText(currentTopic);
			currentLessonComp = null;
		}
		viewLessonMenu();
	}
	
	public void viewTopic(LessonComponent lessonCmp) {
		lessonPanel.removeAll();
		if (currentTopic!=null) {
			Lesson lesson = lessonCmp.getLesson();
			currentLessonComp = lessonCmp;
			Iterator<String> it = lesson.getFiles().keySet().iterator();
			while (it.hasNext()) {
				String fileName = it.next();
				DocumentComponent comp = lesson.getFileComponent(fileName);
				comp.addMouseListener(this);
				lessonPanel.add(comp);
			}
			lessonPanel.validate();
			lessonPanel.repaint();
			scrollPane.validate();
			scrollPane.repaint();
			title.setText(currentTopic+" - "+lesson.getName());
			upButton.setEnabled(true);
			downButton.setEnabled(false);
		}
		viewDocMenu();
	}
	
	private void unselectAllLessons() {
		int len = lessonPanel.getComponentCount();
		for (int i=0;i<len;i++) {
			Component c = lessonPanel.getComponent(i);
			if (c instanceof LessonComponent)
				((LessonComponent)c).setUnselected();
		}
		selectedComp = null;
	}

	public void mouseClicked(MouseEvent e) {
		Component c = e.getComponent();
		if (e.getClickCount()==1 && c != null && c instanceof LessonComponent && SwingUtilities.isLeftMouseButton(e)) {
			unselectAllLessons();
			((LessonComponent)c).switchBorder();
			this.selectedComp = (LessonComponent)c;
			selectedCompX = e.getX();
			selectedCompY = e.getY();
			if (!(c instanceof DocumentComponent)) {
				downButton.setEnabled(true);
				upButton.setEnabled(false);
				delLessonButton.setEnabled(true);
			}
			else
				delDocButton.setEnabled(true);
			renameButton.setEnabled(true);
			moveButton.setEnabled(true);
		}
		if (e.getClickCount()==2 && c != null) {
			if (c instanceof LessonComponent && !(c instanceof DocumentComponent)) {
				unselectAllLessons();
				viewTopic((LessonComponent)c);
			}
			else if (c instanceof DocumentComponent) {
				Software soft = SoftwareManager.getInstance().getSoftwareByExtension(((DocumentComponent)c).getExtension());
				if (soft != null)
					soft.launch(((DocumentComponent)c).getAbsFileName());
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
    	Component comp = e.getComponent();
    	if (comp instanceof LessonComponent && SwingUtilities.isRightMouseButton(e)) {
    		renameText.setText(comp.getName());
            renamePopup.show(comp,e.getX(),e.getY());
            renamedComp = (LessonComponent)comp;
        }
		else if (e.getSource()==newDocButton && SwingUtilities.isLeftMouseButton(e)) {
            newDocMenu.show(newDocButton,e.getX(),e.getY());
		}
		else if (e.getSource()==moveButton && SwingUtilities.isLeftMouseButton(e)) {
			if (selectedComp instanceof DocumentComponent)
				TopicPopupMenu.getInstance().addMenuItems(lessonsByTopic,currentTopic);
			else
				TopicPopupMenu.getInstance().removeMenuItems(currentTopic);
			TopicPopupMenu.getInstance().show(moveButton,e.getX(),e.getY());
		}
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==upButton)
			viewTopic();
		else if (e.getSource()==downButton && selectedComp!=null) {
			viewTopic(selectedComp);
		}
		else if (e.getSource()==newLessonButton) {
			Lesson newLesson = Lesson.createLesson(currentTopic);
			this.lessonsByTopic.get(currentTopic).add(newLesson);
			viewTopic();
		}
		else if (e.getSource() instanceof MenuItem) {
			MenuItem item = (MenuItem)e.getSource();
			String type= item.getCode();
			if (currentLessonComp!= null) {
				currentLessonComp.getLesson().createDocument(type);
				viewTopic(currentLessonComp);
			}
		}
		else if (e.getSource()==renameButton) {
			if (selectedComp!=null) {
	    		renameText.setText(selectedComp.getName());
	            renamePopup.show(selectedComp,
	            				selectedCompX,
	            				selectedCompY
	            				);
	            renamedComp = (LessonComponent)selectedComp;
			}
		}
		else if (e.getSource()==delLessonButton) {
			if (selectedComp!=null) {
				int resp = JOptionPane.showConfirmDialog(SchoolIWE.getMainFrame(), 
						String.format(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "realyDeleteLesson"),selectedComp.getLesson().getName()),
						ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "confirm"),
						JOptionPane.YES_NO_OPTION);
				if (resp==0) {
					selectedComp.getLesson().remove();
					lessonsByTopic.get(currentTopic).remove(selectedComp.getLesson());
					viewTopic();
				}
			}
		}
		else if (e.getSource()==delDocButton) {
			if (selectedComp!=null) {
				int resp = JOptionPane.showConfirmDialog(SchoolIWE.getMainFrame(), 
						String.format(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "realyDeleteDocument"),selectedComp.getName()),
						ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "confirm"),
						JOptionPane.YES_NO_OPTION);
				if (resp==0) {
					((DocumentComponent)selectedComp).remove();
					((DocumentComponent)selectedComp).getLesson().removeFile(((DocumentComponent)selectedComp).getFileName());
					viewTopic(currentLessonComp);
				}
			}
		}
		else if (e.getSource()==impDocButton) {
			int returnVal = fileChooser.showOpenDialog(SchoolIWE.getMainFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
				if (currentLessonComp!= null) {
					currentLessonComp.getLesson().importDocument(file);
					viewTopic(currentLessonComp);
				}
			}
		}
		else if (e.getSource() instanceof JMenuItem && selectedComp!=null) {
			// move lesson or document
			if (!(selectedComp instanceof DocumentComponent)) {
				// move lesson
				String targetTopic = ((JMenuItem)e.getSource()).getText();
				int resp = JOptionPane.showConfirmDialog(SchoolIWE.getMainFrame(), 
						String.format(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "movelessonquestion"),
								selectedComp.getLesson().getName(),targetTopic),
						ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "confirm"),
						JOptionPane.YES_NO_OPTION);
				if (resp==0) {
					Lesson lesson = selectedComp.getLesson();
					lesson.moveTo(targetTopic);
					lessonsByTopic.get(currentTopic).remove(lesson);
					lessonsByTopic.get(targetTopic).add(lesson);
					viewTopic();
					selectedComp = null;
				}
			}
			else if (selectedComp instanceof DocumentComponent) {
				// move document
				String targetLesson = ((JMenuItem)e.getSource()).getText();
				String[] parts = targetLesson.split("  ");
				String tgtLesson = parts[1]+"_"+parts[0].trim();
				JPopupMenu menu = (JPopupMenu)((JMenuItem)e.getSource()).getParent();
				String targetTopic = ((JMenu)(menu.getInvoker())).getText();
				Lesson lesson = findLesson(targetTopic, tgtLesson);
				int resp = JOptionPane.showConfirmDialog(SchoolIWE.getMainFrame(), 
						String.format(ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "movedocquestion"),
								((DocumentComponent) selectedComp).getFileName(),targetTopic,lesson.getName()),
						ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "confirm"),
						JOptionPane.YES_NO_OPTION);
				if (resp==0) {
					((DocumentComponent)selectedComp).moveTo(lesson);
					viewTopic(selectedComp);
					selectedComp = null;
				}
			}
		}
	}
	
	private Lesson findLesson(String topic, String lessonName) {
		Lesson result = null;
		TreeSet<Lesson> lessons = lessonsByTopic.get(topic);
		if (lessons != null) {
			Iterator<Lesson> itLesson = lessons.iterator();
			while (itLesson.hasNext()) {
				Lesson lesson = itLesson.next();
				if (lessonName.equals(JDateUtils.fmtDayFileStr.format(lesson.getDate())+"_"+lesson.getName())) {
					result = lesson;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getComponent()==renameText && e.getKeyCode()==KeyEvent.VK_ENTER) {
			if (renamedComp instanceof DocumentComponent)
				((DocumentComponent)renamedComp).rename(renameText.getText());
			else {
				renamedComp.rename(currentTopic, renameText.getText());
			}
			renamePopup.setVisible(false);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
}
