package com.schooliwe.driver;

import java.util.List;

public interface IDocumentDriver {
	
	public List<String> getAvailableExtensions();
	
	public void createDocument(String fileName);
	
	public void addExtension(String extension);
	
}
