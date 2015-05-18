package com.schooliwe.action;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.schooliwe.resources.ResourcesManager;

public class ActionObjectManager {
	
	private static final Logger logger = Logger.getLogger(ActionObjectManager.class);
	
	private HashMap<String, Component> actionObjects = new HashMap<String, Component>();
	
	private static ActionObjectManager actionObjectMgr = null;
	
    public static final Dimension minimumSize = new Dimension(200, 100);
    
	public static ActionObjectManager getInstance() {
		if (actionObjectMgr==null)
			actionObjectMgr = new ActionObjectManager();
		return actionObjectMgr;
	}
	
	private ActionObjectManager() {
		Document doc = ResourcesManager.getInstance().getResource(ResourcesManager.ACTION_OBJECT_FILE_ID);
		NodeList actionObjList = doc.getElementsByTagName("object");
		int len = actionObjList.getLength();
		for (int i=0;i<len;i++) {
			Node nd = actionObjList.item(i);
			try {
				if (nd instanceof Element) {
					String className = ((Element)nd).getAttribute("class");
					Constructor<?> actionObjectConstructor = Class.forName(className).getConstructor();
					Component actionObject = (Component)actionObjectConstructor.newInstance();
					actionObject.setMinimumSize(minimumSize);
					actionObjects.put(((Element)nd).getAttribute("id"),actionObject);
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public Component getActionObject(String actionObjId) {
		return actionObjects.get(actionObjId);
	}

}
