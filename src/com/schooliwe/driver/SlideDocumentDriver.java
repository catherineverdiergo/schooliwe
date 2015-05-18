package com.schooliwe.driver;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hslf.usermodel.SlideShow;

public class SlideDocumentDriver implements IDocumentDriver {
	
	private static final Logger logger = Logger.getLogger(SlideDocumentDriver.class);

	private List<String> availableExtensions = new ArrayList<String>();
	
	public SlideDocumentDriver() {
		availableExtensions.add(".ppt");
		availableExtensions.add(".odp");
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
					SlideShow slideShow = new SlideShow();
					slideShow.createSlide();
					FileOutputStream out = new FileOutputStream(fileName);
					slideShow.write(out);
					out.close();
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
