package com.schooliwe.tree;

import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class TopicTreeNode extends DefaultMutableTreeNode {
	
	private String iconFileName=null;
	private String actionObjectId=null;

	public String getIconFileName() {
		return iconFileName;
	}

	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}

	public String getActionObjectId() {
		return actionObjectId;
	}

	public void setActionObjectId(String actionObjectId) {
		this.actionObjectId = actionObjectId;
	}
	
}
