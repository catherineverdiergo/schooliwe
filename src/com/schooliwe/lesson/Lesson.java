package com.schooliwe.lesson;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.schooliwe.conf.ConfManager;
import com.schooliwe.driver.DocumentDriverManager;
import com.schooliwe.driver.IDocumentDriver;
import com.schooliwe.swing.util.FileUtils;
import com.schooliwe.swing.util.JDateUtils;

public class Lesson {

	private static final Logger logger = Logger.getLogger(Lesson.class);

	private String topic;
	private String name;
	private Date date;
	private HashMap<String, DocumentComponent> files;
	private JPanel component;
	
	public Lesson(String topic, String name, Date date) {
		this.topic = topic;
		this.name = name;
		this.date = date;
		files = new HashMap<String, DocumentComponent>();
		component = new LessonComponent(new BorderLayout());
		((LessonComponent)component).setLesson(this);
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public HashMap<String, DocumentComponent> getFiles() {
		return files;
	}

	/**
	 * Add new file
	 * @param fileName : absolute file path
	 * @return
	 */
	public DocumentComponent addFile(String fileName) {
		DocumentComponent result = null;
		if (!files.containsKey(fileName)) {
			result = new DocumentComponent(this,new BorderLayout());
			result.setLesson(this);
			String fileKey = new File(fileName).getName();
			result.setFileName(fileKey);
			files.put(fileKey,result);
		}
		return result;
	}
	
	/**
	 * remove file
	 * @param file : relative file name
	 */
	public void removeFile(String file) {
		if (files.containsKey(file))
			files.remove(file);
	}

	public JPanel getComponent() {
		return component;
	}
	
	/**
	 * get related file document component
	 * @param fileName : relative file path
	 * @return
	 */
	public DocumentComponent getFileComponent(String fileName) {
		return files.get(fileName);
	}
	
	public static Lesson createLesson(String topic) {
		Lesson newLesson = null;
		try {
			Date dt = new Date();
			String dtStr = JDateUtils.fmtDayFileStr.format(dt);
			// check if parent dir already exists
			String parentDirName = ConfManager.getSavedConfPath()+"/"+topic;
			File parentDir = new File(parentDirName);
			if (!parentDir.exists())
				parentDir.mkdir();
			String newDirName = parentDirName+"/"+dtStr+"_"+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "newlesson");
			String newLessonName = ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "newlesson");
			int i=1;
			String dirName = newDirName;
			String lessonName= newLessonName;
			File dir = new File(dirName);
			while (dir.exists()) {
				dirName = newDirName+" "+i;
				lessonName = newLessonName+" "+i;
				dir = new File(dirName);
				i++;
			}
			dir.mkdir();
			newLesson = new Lesson(topic, lessonName, dt);
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return newLesson;
	}
	
	public DocumentComponent createDocument(String type) {
		DocumentComponent newDoc = null;
		try {
			String label = SoftwareManager.getInstance().getSoftTypeLabels().get(type);
			String ext = SoftwareManager.getInstance().getSoftwareByType(type).getExtension();
			Date dt = new Date();
			String dtStr = JDateUtils.fmtDayFileStr.format(dt);
			String newFileName = ConfManager.getSavedConfPath()+"/"+this.getTopic()+"/"
								+JDateUtils.fmtDayFileStr.format(this.getDate())+"_"+this.getName()+"/"
			                    +dtStr+"_"+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "new")+"_"+label;
			String newDocName = newFileName+ext;
			int i= 1;
			File file = new File(newDocName);
			while (file.exists()) {
				newDocName = newFileName+" "+i+ext;
				file = new File(newDocName);
				i++;
			}
			IDocumentDriver driver = DocumentDriverManager.getInstance().getDriver(ext);
			driver.createDocument(newDocName);
			newDoc = this.addFile(new File(newDocName).getName());
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return newDoc;
	}
	
	public String getCompleteDirName() {
		return JDateUtils.fmtDayFileStr.format(this.date)+"_"+this.name;
	}
	
	public String getAbsDirName() {
		return ConfManager.getSavedConfPath()+"/"+this.topic+"/"+JDateUtils.fmtDayFileStr.format(this.date)+"_"+this.name;
	}
	
	public void remove() {
		Iterator<String> it = files.keySet().iterator();
		while (it.hasNext()) {
			String fileKey = it.next();
			DocumentComponent fileDoc = files.get(fileKey);
			fileDoc.remove();
		}
		files.clear();
		File dir = new File(getAbsDirName());
		if (dir.exists() && dir.isDirectory())
			dir.delete();
	}

	public DocumentComponent importDocument(File newFile) {
		DocumentComponent newDoc = null;
		int extIndex = newFile.getName().lastIndexOf(".");
		String extension = newFile.getName().substring(extIndex,newFile.getName().length());
		Software soft = SoftwareManager.getInstance().getSoftwareByExtension(extension);
		String type = soft.getType();
		try {
			String label = SoftwareManager.getInstance().getSoftTypeLabels().get(type);
			String ext = SoftwareManager.getInstance().getSoftwareByType(type).getExtension();
			Date dt = new Date();
			String dtStr = JDateUtils.fmtDayFileStr.format(dt);
			String newFileName = ConfManager.getSavedConfPath()+"/"+this.getTopic()+"/"
								+JDateUtils.fmtDayFileStr.format(this.getDate())+"_"+this.getName()+"/"
			                    +dtStr+"_"+ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "imported")+"_"+label;
			String newDocName = newFileName+ext;
			int i= 1;
			File file = new File(newDocName);
			while (file.exists()) {
				newDocName = newFileName+" "+i+ext;
				file = new File(newDocName);
				i++;
			}
			FileUtils.copyfile(newFile.getAbsolutePath(), newDocName);
			newDoc = this.addFile(new File(newDocName).getName());
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return newDoc;
	}
	
	/**
	 * Move this lesson to another topic
	 * @param targetTopic
	 */
	public void moveTo(String targetTopic) {
		String srcDirName = getAbsDirName();
		String targetDirParentName = ConfManager.getSavedConfPath()+"/"+targetTopic;
		File targetDirParent = new File(targetDirParentName);
		if (!targetDirParent.exists())
			targetDirParent.mkdir();
		String targetDirName = ConfManager.getSavedConfPath()+"/"+targetTopic+"/"+JDateUtils.fmtDayFileStr.format(this.date)+"_"+this.name;
		File srcDir = new File(srcDirName);
		srcDir.renameTo(new File(targetDirName));
		this.topic = targetTopic;
	}
}
