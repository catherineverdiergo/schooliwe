package com.schooliwe.lesson;

import java.io.File;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

class SoftLauncher extends Thread {
	
	private static final Logger logger = Logger.getLogger(SoftLauncher.class);

	private String cmd;
	
	public SoftLauncher(String cmd) {
		super();
		this.cmd = cmd;
	}

	@Override
	public void run() {
		try {
		    Runtime rt = Runtime.getRuntime();
		    Process p = rt.exec(cmd);
		    p.waitFor();
		    p.destroy();
		}
		catch (Exception tEx) {
			logger.error(tEx.getMessage(),tEx);
		}
	}
	
}

public class Software {
	
	private static final Logger logger = Logger.getLogger(Software.class);

	private String type = null;
	private String directory = null;
	private String workingdirectory = null;
	private String exeName = null;
	private ImageIcon icon = null;
	private String extension = null;
	
	private String runCmd = null;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDirectory() {
		return directory;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public String getExeName() {
		return exeName;
	}
	
	public void setExeName(String exeName) {
		this.exeName = exeName;
	}
	
	public ImageIcon getIcon() {
		return icon;
	}
	
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public synchronized void launch(String params) {
		if ("Linux".equals(System.getProperty("os.name")))
			launchUnix(params);
		else {
	    	this.runCmd = "\""+this.directory+"/"+this.exeName+"\"";
	    	if (params != null)
	    		runCmd += " \""+params+"\"";
		    try {
		    	SoftLauncher launcher = new SoftLauncher(runCmd);
		    	launcher.start();
		    }
		    catch(Exception exc){
		    	/*handle exception*/
				logger.error(exc.getMessage(),exc);
		    }
		}
	}
	
	public void launchUnix(String params) {
		try {
			String command[];
			String exeParams[] = this.exeName.split(" ");
			if (exeParams.length>1) {
				command = new String[exeParams.length+1];
				for (int i=0;i<=exeParams.length;i++) {
					if (i==exeParams.length)
						command[i] = params;
					else if (i==0)
						command[i] = this.directory+"/"+exeParams[0];
					else
						command[i] = exeParams[i];
				}
			}
			else
				command = new String[] {this.directory+"/"+this.exeName,params};
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			if (this.workingdirectory!=null && !"".equals(this.workingdirectory))
				processBuilder.directory(new File(this.workingdirectory));
			else
				processBuilder.directory(new File(this.directory));
			processBuilder.start();
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	public String getWorkingdirectory() {
		return workingdirectory;
	}

	public void setWorkingdirectory(String workingdirectory) {
		this.workingdirectory = workingdirectory;
	}

}
