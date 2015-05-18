package com.schooliwe.lesson;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.schooliwe.conf.ConfManager;

@SuppressWarnings("serial")
public class DocumentComponent extends LessonComponent {

	private static final Logger logger = Logger.getLogger(DocumentComponent.class);

	private String fileName;
	private String extension;
	private Lesson lesson;
	
	public DocumentComponent(Lesson lesson, LayoutManager arg0) {
		super(arg0);
		this.lesson = lesson;
	}

	public String getExtension() {
		return extension;
	}

	private void setExtension(String extension) {
		this.extension = extension;
		Software soft = SoftwareManager.getInstance().getSoftwareByExtension(extension);
		this.add(new JLabel(soft.getIcon(), SwingConstants.CENTER),BorderLayout.PAGE_START);
	}
	
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * set file name
	 * @param fileName : relative file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
		int extIndex = fileName.lastIndexOf('.');
		this.setExtension(fileName.substring(extIndex, fileName.length()));
		this.setDate(fileName.substring(0,8));
		int idx = fileName.indexOf("_");
		idx = Math.max(idx+1, 0);
		this.setTitle(fileName.substring(idx,extIndex));
	}
	
	public void rename(String newName) {
		try {
			String absFileName = getAbsFileName();
			File old = new File(absFileName);
			String newFileName = old.getParent()+"/"+this.fileName.substring(0,8)+"_"+newName+extension;
			old.renameTo(new File(newFileName));
			setTitle(newName);
			this.fileName = this.fileName.substring(0,8)+"_"+newName+extension;
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public String getAbsFileName() {
		return ConfManager.getSavedConfPath()+"/"+lesson.getTopic()+"/"+lesson.getCompleteDirName()+"/"+this.fileName;
	}
	
	public void remove() {
		File fic = new File(this.getAbsFileName());
		if (fic.exists()) {
			fic.delete();
		}
	}

	@Override
	public void setTitle(String title) {
		titleLabel.setAlignmentX(SwingConstants.CENTER);
		titleLabel.setText(title);
	}
	
	/**
	 * Move this lesson to another topic, another lesson
	 * @param targetTopic
	 */
	public void moveTo(Lesson targetLesson) {
		String srcFileName = getAbsFileName();
		String targetFileName = ConfManager.getSavedConfPath()+"/"+targetLesson.getTopic()+"/"+this.fileName;
		File srcFile = new File(srcFileName);
		srcFile.renameTo(new File(targetFileName));
		this.lesson.getFiles().remove(this.fileName);
		this.lesson = targetLesson;
		targetLesson.getFiles().put(this.fileName,this);
	}

}
