package com.schooliwe.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.schooliwe.tests.DnDNode;

@SuppressWarnings("serial")
public class TopicTreeDnDNode extends TopicTreeNode implements Transferable {

	/**
	 * data flavor used to get back a DnDNode from data transfer
	 */
	public static final DataFlavor DnDNode_FLAVOR = new DataFlavor(DnDNode.class,
			"Drag and drop Node");
 
	/**
	 * list of all flavors that this DnDNode can be transfered as
	 */
	protected static DataFlavor[] flavors = { TopicTreeDnDNode.DnDNode_FLAVOR };
	
	private String dndLabel = null;
 
	public DataFlavor[] getTransferDataFlavors()
	{
		return TopicTreeDnDNode.flavors;
	}
 
	/**
	 * @param flavor
	 * @return
	 **/
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		DataFlavor[] flavs = this.getTransferDataFlavors();
		for (int i = 0; i < flavs.length; i++)
		{
			if (flavs[i].equals(flavor))
			{
				return true;
			}
		}
		return false;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (this.isDataFlavorSupported(flavor))
			return this;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	public String getDndLabel() {
		return dndLabel;
	}

	public void setDndLabel(String dndLabel) {
		this.dndLabel = dndLabel;
	}

	/**
	 * Determines if we can add a certain node as a child of this node.
	 * 
	 * @param node
	 * @return
	 */
	public boolean canAdd(TopicTreeDnDNode node)
	{
		if (node != null)
		{
			if (!this.equals(node.getParent()))
			{
				if ((!this.equals(node)))
				{
					return true;
				}
			}
		}
		return false;
	}
 
	/**
	 * Gets the index node should be inserted at to maintain sorted order. Also
	 * performs checking to see if that node can be added to this node. By
	 * default, DnDNode adds children at the end.
	 * 
	 * @param node
	 * @return the index to add at, or -1 if node can not be added
	 */
	public int getAddIndex(TopicTreeDnDNode node)
	{
		if (!this.canAdd(node))
		{
			return -1;
		}
		return this.getChildCount();
	}
 
}
