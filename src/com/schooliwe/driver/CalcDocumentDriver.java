package com.schooliwe.driver;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class CalcDocumentDriver implements IDocumentDriver {
	
	private static final Logger logger = Logger.getLogger(CalcDocumentDriver.class);

	private List<String> availableExtensions = new ArrayList<String>();
	
	public CalcDocumentDriver() {
		availableExtensions.add(".xls");
		availableExtensions.add(".ods");
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
					HSSFWorkbook wb = new HSSFWorkbook();
					wb.createSheet();
					FileOutputStream fileOut = new FileOutputStream(fileName);
					wb.write(fileOut);
					fileOut.close();
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
