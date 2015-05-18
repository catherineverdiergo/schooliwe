package com.schooliwe.resources;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ResourcesManager {
	
	private static final Logger logger = Logger.getLogger(ResourcesManager.class);

	public static final int TOPICS_FILE_ID = 1;
	public static final int TIMETABLE_FILE_ID = 2;
	public static final int VACATION_FILE_ID = 3;
	public static final int ACTION_OBJECT_FILE_ID = 4;
	public static final int HOMEWORK_FILE_ID = 5;
	public static final int SOFTWARES_FILE_ID = 6;
	public static final int DRIVERS_FILE_ID = 7;
	
	public static final String RSC_DIRECTORY = "rsc";
	
	private static String[] ResourcesName = {
		"topics.xml",
		"timetable.xml",
		"vacation.xml",
		"actionobjects.xml",
		"homework.xml",
		"softwares.xml",
		"documentdrivers.xml"
	};
	
	public static final int NB_RSC = ResourcesName.length;
	
	public static final SimpleDateFormat sdfVacation = new SimpleDateFormat("yyyyMMdd");
	
	private Document[] XMLResources = null;

	private static ResourcesManager instance = null;

	public static Document parseXmlFile(String fileName) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(fileName);

		}catch(ParserConfigurationException pce) {
			logger.error(pce.getMessage(),pce);
		}catch(SAXException se) {
			logger.error(se.getMessage(),se);
		}catch(IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
		}
		return dom;
	}
	
	public ResourcesManager() {
		super();
		XMLResources = new Document[NB_RSC];
		for (int i=0;i<NB_RSC;i++) {
			XMLResources[i] = parseXmlFile(RSC_DIRECTORY+"/"+ResourcesName[i]);
		}
	}
	
	public static ResourcesManager getInstance() {
		if (instance == null) {
			instance = new ResourcesManager();
		}
		return instance;
	}
	
	public Document getResource(int rscId) {
		return XMLResources[rscId-1];
	}

}
