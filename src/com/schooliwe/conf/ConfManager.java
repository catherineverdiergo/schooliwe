package com.schooliwe.conf;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.schooliwe.resources.ResourcesManager;

public class ConfManager {

	private static final Logger logger = Logger.getLogger(ConfManager.class);

	public static final int MESSAGES_FILE_ID = 1;
	
	public static final int NB_CFG = MESSAGES_FILE_ID;
	public static final String CFG_DIRECTORY = "conf";
	
	public static String workingDirectory = null;
	
	private static String[] ConfResourcesName = {
		"messages.properties"
	};
	
	private Object[] ConfResources = null;

	private static ConfManager instance = null;
	
	public static String getFileExtension(String fileName) {
		String ext="";
		int mid = fileName.lastIndexOf(".");
		ext=fileName.substring(mid+1,fileName.length());
		return ext;
	}

	public ConfManager() {
		super();
		workingDirectory = System.getProperty("user.dir");
		ConfResources = new Object[NB_CFG];
		try {
			for (int i=0;i<NB_CFG;i++) {
				if ("properties".equalsIgnoreCase(getFileExtension(ConfResourcesName[i])))
					ConfResources[i] = new PropertiesConfiguration(CFG_DIRECTORY+"/"+ConfResourcesName[i]);
				else if ("xml".equalsIgnoreCase(getFileExtension(ConfResourcesName[i])))
					ConfResources[i] = ResourcesManager.parseXmlFile(CFG_DIRECTORY+"/"+ConfResourcesName[i]);
			}
		}
		catch (ConfigurationException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public static ConfManager getInstance() {
		if (instance == null) {
			instance = new ConfManager();
		}
		return instance;
	}
	
	public static Object getConfResource(int rscId) {
		return ConfManager.getInstance().ConfResources[rscId-1];
	}
	
	public static String getMessage(int cfgFileId, String msgKey) {
		String result = null;
		if (ConfManager.getInstance().ConfResources[cfgFileId-1] instanceof PropertiesConfiguration) {
			result = ((PropertiesConfiguration)ConfManager.getInstance().ConfResources[cfgFileId-1]).getString(msgKey);
		}
		return result;
	}
	
	public static String getSavedConfPath() {
		String result = "";
		if (workingDirectory!=null)
			result+=workingDirectory+"/";
		result += "SchoolIWEData";
		File cfgDir = new File(result);
		if (!cfgDir.exists())
			cfgDir.mkdir();
		return result;
	}

}
