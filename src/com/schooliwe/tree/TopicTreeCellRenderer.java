package com.schooliwe.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("serial")
public class TopicTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static Font font = new Font("Arial", Font.BOLD, 12);
	private static Color color = new Color(154,102,154);

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,String description) {
        return new ImageIcon(path, description);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (value instanceof TopicTreeNode) {
			TopicTreeNode topic = (TopicTreeNode)value;
			String path = topic.getIconFileName();
			if (path!=null) {
				ImageIcon icon = createImageIcon("images/"+topic.getIconFileName(),"");
				setIcon(icon);
			}
			else {
				Object obj = topic.getUserObject();
				if (obj instanceof String && "TOP".equals((String)obj)) {
					ImageIcon icon = createImageIcon("images/schoolIWE.png","");
					setIcon(icon);
				}
			}
		}
		result.setFont(font);
		result.setForeground(color);
		return result;
	}
	
}
