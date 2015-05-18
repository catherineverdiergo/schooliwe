package com.schooliwe.lesson;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.schooliwe.resources.ResourcesManager;

public class SoftwareManager {

	private static SoftwareManager instance = null;
	private HashMap<String, Software> softByType = new HashMap<String, Software>();
	private HashMap<String, Software> softByExtension = new HashMap<String, Software>();
	private HashMap<String, String> softTypeLabels = new HashMap<String, String>();
	
	public static SoftwareManager getInstance() {
		if (instance==null)
			instance = new SoftwareManager();
		return instance;
	}
	
	protected SoftwareManager() {
		// Get Software xml document
		Document softDoc = ResourcesManager.getInstance().getResource(ResourcesManager.SOFTWARES_FILE_ID);
		NodeList appList = softDoc.getElementsByTagName("application");
		int len = appList.getLength();
		for (int i=0;i<len;i++) {
			Element app = (Element)appList.item(i);
			Software soft = new Software();
			soft.setDirectory(app.getAttribute("dir"));
			soft.setWorkingdirectory(app.getAttribute("workingdir"));
			soft.setExeName(app.getAttribute("exe"));
			soft.setExtension(app.getAttribute("extension"));
			soft.setIcon(new ImageIcon("images/"+app.getAttribute("icon")));
			soft.setType(app.getAttribute("type"));
			softByExtension.put(soft.getExtension(), soft);
			softByType.put(soft.getType(), soft);
			softTypeLabels.put(soft.getType(), app.getAttribute("labelType"));
		}
	}
	
	public HashMap<String, String> getSoftTypeLabels() {
		return softTypeLabels;
	}

	public Software getSoftwareByType(String type) {
		return softByType.get(type);
	}

	public Software getSoftwareByExtension(String extension) {
		return softByExtension.get(extension);
	}
	
	public static boolean isValidExtension(String extension) {
		boolean result = false;
		Iterator<String> it = SoftwareManager.getInstance().softByExtension.keySet().iterator();
		while (it.hasNext()) {
			String ext = it.next();
			if (ext.equalsIgnoreCase(extension)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
