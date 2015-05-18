package com.schooliwe.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.schooliwe.action.ActionObjectManager;
import com.schooliwe.lesson.Lesson;
import com.schooliwe.lesson.LessonComponent;
import com.schooliwe.lesson.LessonsManager;
import com.schooliwe.resources.ResourcesManager;

@SuppressWarnings("serial")
public class TopicPopupMenu extends JPopupMenu {
	
	private static TopicPopupMenu instance=null;
	
	private HashMap<String, JMenu> menusByTopic = new HashMap<String, JMenu>();
	private HashMap<String, JMenuItem> itemsByTopic = new HashMap<String, JMenuItem>();
	
	public static final TopicPopupMenu getInstance() {
		if (instance==null)
			instance = new TopicPopupMenu();
		return instance;
	}
	
	private TopicPopupMenu() {
		super();
		Document doc = ResourcesManager.getInstance().getResource(ResourcesManager.TOPICS_FILE_ID);
		createTopics(doc.getDocumentElement());
	}

    private void createTopics(Element topicsTop) {
    	NodeList topics = topicsTop.getChildNodes();
    	int nbTopics = topics.getLength();  
        for (int i=0;i<nbTopics;i++) {
        	Node node =  topics.item(i);
        	if (node instanceof Element) {
	        	Element elementTopic = (Element)node;
	        	JMenu topic = null;
	        	JMenuItem topicItem = null;
	        	if ("y".equals(elementTopic.getAttribute("lesson"))) {
	        		topic = new JMenu();
		        	topic.setText(elementTopic.getAttribute("name"));
		        	topic.setIcon(new ImageIcon("images/"+elementTopic.getAttribute("icon")));
	        		topicItem = new JMenuItem();
	        		topicItem.addActionListener((LessonsManager)ActionObjectManager.getInstance().getActionObject("lessonsManager"));
		        	topicItem.setText(elementTopic.getAttribute("name"));
		        	topicItem.setIcon(new ImageIcon("images/"+elementTopic.getAttribute("icon")));
		        	menusByTopic.put(elementTopic.getAttribute("name"), topic);
		        	itemsByTopic.put(elementTopic.getAttribute("name"), topicItem);
		        	this.add(topicItem);
	        	}
        	}
        }
    }
    
    public void addMenuItems(HashMap<String, TreeSet<Lesson>> lessonsByTopic, String excludedTopic) {
		this.removeAll();
    	Iterator<String> itTopic = lessonsByTopic.keySet().iterator();
    	while (itTopic.hasNext()) {
    		String topic = itTopic.next();
    		if (!topic.equals(excludedTopic)) {
	    		JMenu topicMenu = menusByTopic.get(topic);
				TreeSet<Lesson> lessons = lessonsByTopic.get(topic);
	    		if (topicMenu != null && lessons!=null && lessons.size()!=0) {
	    			topicMenu.removeAll();
	    			this.add(topicMenu);
	    			Iterator<Lesson> itLesson = lessons.iterator();
	    			while (itLesson.hasNext()) {
	    				Lesson lesson = itLesson.next();
	    				JMenuItem item = ((LessonComponent)lesson.getComponent()).getMenuItem();
	    				topicMenu.add(item);
	    				if (item.getActionListeners().length==0)
	    					item.addActionListener((LessonsManager)ActionObjectManager.getInstance().getActionObject("lessonsManager"));
	    			}
	    		}
    		}
    	}
    }
    
    public void removeMenuItems(String excludedTopic) {
		this.removeAll();
    	Iterator<String> itTopic = itemsByTopic.keySet().iterator();
    	while(itTopic.hasNext()) {
    		String topic = itTopic.next();
    		if (!topic.equals(excludedTopic)) {
	    		JMenuItem topicItem = itemsByTopic.get(topic);
	    		this.add(topicItem);
    		}
    	}
    }

}
