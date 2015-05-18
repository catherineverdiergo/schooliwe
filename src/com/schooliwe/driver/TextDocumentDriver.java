package com.schooliwe.driver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;

public class TextDocumentDriver implements IDocumentDriver {
	
	private static final Logger logger = Logger.getLogger(TextDocumentDriver.class);

	private List<String> availableExtensions = new ArrayList<String>();
	
	public TextDocumentDriver() {
		availableExtensions.add(".doc");
		availableExtensions.add(".odt");
		availableExtensions.add(".docx");
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
					HWPFDocument doc = new HWPFDocument(new FileInputStream("rsc/model.doc"));
					OutputStream out = new FileOutputStream(fileName);
					doc.write(out);
					out.flush();
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
