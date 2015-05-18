package com.schooliwe.driver;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.schooliwe.resources.ResourcesManager;

public class DocumentDriverManager {
	
	private static final Logger logger = Logger.getLogger(DocumentDriverManager.class);

	private HashMap<String, IDocumentDriver> drivers = new HashMap<String, IDocumentDriver>();
	
	private static DocumentDriverManager driverMgr = null;
	
	public static DocumentDriverManager getInstance() {
		if (driverMgr==null)
			driverMgr = new DocumentDriverManager();
		return driverMgr;
	}
	
	private DocumentDriverManager() {
		Document doc = ResourcesManager.getInstance().getResource(ResourcesManager.DRIVERS_FILE_ID);
		NodeList driversList = doc.getElementsByTagName("driver");
		int len = driversList.getLength();
		for (int i=0;i<len;i++) {
			Node nd = driversList.item(i);
			try {
				if (nd instanceof Element) {
					String className = ((Element)nd).getAttribute("class");
					String model = ((Element)nd).getAttribute("model");
					Constructor<?> driverConstructor = Class.forName(className).getConstructor();
					IDocumentDriver driver = (IDocumentDriver)driverConstructor.newInstance();
					String extParam = ((Element) nd).getAttribute("extensions");
					String[] extensions = extParam.split(",");
					for (int j=0;j<extensions.length;j++) {
						driver.addExtension(extensions[j]);
						drivers.put(extensions[j],driver);
					}
					if (model != null && driver instanceof CopyModelDocumentDriver)
						((CopyModelDocumentDriver)driver).setModel(model);
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public IDocumentDriver getDriver(String extension) {
		return drivers.get(extension);
	}

}
