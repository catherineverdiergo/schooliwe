package com.schooliwe.driver;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.schooliwe.swing.util.FileUtils;

public class CopyModelDocumentDriver implements IDocumentDriver {
	
	private static final Logger logger = Logger.getLogger(CopyModelDocumentDriver.class);

	private List<String> availableExtensions = new ArrayList<String>();
	
	private String model = null;
	
	public CopyModelDocumentDriver(String model) {
		this.model = model;
	}
	
	public CopyModelDocumentDriver() {
	}
	
	@Override
	public List<String> getAvailableExtensions() {
		return availableExtensions;
	}

	@Override
	public void createDocument(String fileName) {
		// check extension
		try {
			int extIndex = fileName.lastIndexOf('.');
			if (extIndex != -1) {
				String extension = fileName.substring(extIndex, fileName.length());
				if (availableExtensions.contains(extension)) {
					FileUtils.copyfile("rsc/"+model,fileName);
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public void addExtension(String extension) {
		if (extension.charAt(0)=='.') {
			if (!availableExtensions.contains(extension))
				availableExtensions.add(extension);
		}
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}
