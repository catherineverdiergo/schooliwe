package com.schooliwe.driver;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmind.core.Core;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

public class XMindDocumentDriver implements IDocumentDriver {
	
	private static final Logger logger = Logger.getLogger(XMindDocumentDriver.class);

	private List<String> availableExtensions = new ArrayList<String>();
	
	public XMindDocumentDriver() {
		availableExtensions.add(".xmind");
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
					IWorkbookBuilder builder = Core.getWorkbookBuilder();
					IWorkbook workbook = builder.createWorkbook(fileName);
					ISheet defSheet = workbook.getPrimarySheet();
					ITopic rootTopic = defSheet.getRootTopic();
					rootTopic.setTitleText("Root Topic");
					workbook.save(fileName);
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
