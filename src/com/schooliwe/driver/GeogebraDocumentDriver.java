package com.schooliwe.driver;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.schooliwe.swing.util.FileUtils;

public class GeogebraDocumentDriver implements IDocumentDriver {
	
	private static final Logger logger = Logger.getLogger(GeogebraDocumentDriver.class);

	private List<String> availableExtensions = new ArrayList<String>();
	
	public GeogebraDocumentDriver() {
		availableExtensions.add(".ggb");
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
					FileUtils.copyfile("rsc/model.ggb",fileName);
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

}
