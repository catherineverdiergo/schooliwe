package com.schooliwe.tree;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.schooliwe.action.ActionObjectManager;
import com.schooliwe.lesson.LessonsManager;
import com.schooliwe.resources.ResourcesManager;

@SuppressWarnings("serial")
public class TopicTree extends JTree implements TreeSelectionListener, MouseListener {

	private Document xmlDocument;
	
	/**
	 * Used to keep track of selected nodes. This list is automatically updated.
	 */
	protected ArrayList<TreePath> selected;
	
	private static JSplitPane splitPane = null;
	
	private static TopicTree instance = null;
	
	private List<String> vacantTopics = new ArrayList<String>();
 
	public void setXmlDocument(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
	}
	
	public static TopicTree getInstance(JSplitPane splitPane) {
		if (instance == null)
			instance = new TopicTree(splitPane);
		return instance;
	}
	
    private void createTopics(TopicTreeNode top,Element topicsTop) {
    	NodeList topics = topicsTop.getChildNodes();
    	int nbTopics = topics.getLength();  
        for (int i=0;i<nbTopics;i++) {
        	Node node =  topics.item(i);
        	if (node instanceof Element) {
	        	Element elementTopic = (Element)node;
	        	TopicTreeNode topic = null;
	        	if ("y".equals(elementTopic.getAttribute("timetable"))) {
	        		topic = new TopicTreeDnDNode();
	        		String dndLabel = elementTopic.getAttribute("timetableLabel");
	        		if (dndLabel == null || "".equals(dndLabel))
		        		dndLabel = elementTopic.getAttribute("name");
	        		((TopicTreeDnDNode)topic).setDndLabel(dndLabel);
	        	}
	        	else
	        		topic = new TopicTreeNode();
	        	if ("yes".equals(elementTopic.getAttribute("vacant")))
	        		vacantTopics.add(elementTopic.getAttribute("name"));
	        	topic.setUserObject(elementTopic.getAttribute("name"));
	        	topic.setIconFileName(elementTopic.getAttribute("icon"));
	        	topic.setActionObjectId(elementTopic.getAttribute("actionObjectId"));
	        	NodeList subTopicsList = elementTopic.getElementsByTagName("subtopics");
	        	if (subTopicsList!=null && subTopicsList.getLength()!=0) {
	        		Element subTopics = (Element)subTopicsList.item(0);
	        		createTopics(topic, subTopics);
	        	}
	        	top.add(topic);
        	}
        }
    }

    private void createNodes(TopicTreeNode top) {
        xmlDocument.getDocumentElement().normalize();
        Element menuElement = (Element)xmlDocument.getElementsByTagName("menu").item(0);
        // Get all topics
        createTopics(top, menuElement);
    }

	public TopicTree(JSplitPane splitPane) {
		super();
		TopicTree.splitPane = splitPane;
		this.selected = new ArrayList<TreePath>();
		this.xmlDocument = ResourcesManager.getInstance().getResource(ResourcesManager.TOPICS_FILE_ID);
        //Create the nodes.
        TopicTreeNode top = new TopicTreeNode();
        top.setUserObject("TOP");
        createNodes(top);
        // set nodes
        this.setModel(new DefaultTreeModel(top));

        this.setCellRenderer(new TopicTreeCellRenderer());
        this.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        this.addTreeSelectionListener(this);
        this.addMouseListener(this);
		// turn on the JComponent dnd interface
		this.setDragEnabled(true);
		// setup our transfer handler
		this.setTransferHandler(new JTreeTransferHandler(this));
	}
    
    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
    	if (this.getSelectionPaths()!=null) {
	    	for (TreePath treePath:this.getSelectionPaths())
	    		selected.add(treePath);
    	}
    }

	/**
	 * @return the list of selected nodes
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<TreePath> getSelection()
	{
		return (ArrayList<TreePath>) (this.selected).clone();
	}

//	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount()==2) {
	        TopicTreeNode node = (TopicTreeNode) this.getLastSelectedPathComponent();
	        if (node.getActionObjectId()!=null) {
	        	Component actionObj = ActionObjectManager.getInstance().getActionObject(node.getActionObjectId());
	        	if (actionObj instanceof LessonsManager)
	        		((LessonsManager)actionObj).setCurrentTopic(node.getUserObject().toString());
	        	splitPane.setBottomComponent(actionObj);
	        }
		}
	}

//	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

//	@Override
	public void mouseExited(MouseEvent arg0) {
	}

//	@Override
	public void mousePressed(MouseEvent arg0) {
	}

//	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	public List<String> getVacantTopics() {
		return vacantTopics;
	}
	
	public static boolean isVacantTopic(String topic) {
		return TopicTree.getInstance(TopicTree.splitPane).getVacantTopics().contains(topic);
	}
 
}
