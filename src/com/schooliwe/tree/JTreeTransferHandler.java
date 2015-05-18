package com.schooliwe.tree;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
 
/**
 * @author helloworld922
 *         <p>
 * @version 1.0
 *          <p>
 *          copyright 2010 <br>
 * 
 *          You are welcome to use/modify this code for any purposes you want so
 *          long as credit is given to me.
 */
public class JTreeTransferHandler extends TransferHandler
{
	private static final Logger logger = Logger.getLogger(JTreeTransferHandler.class);

	/**
	 * Using tree models allows us to add/remove nodes from a tree and pass the
	 * appropriate messages.
	 */
	protected DefaultTreeModel tree;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6851440217837011463L;
 
	/**
	 * Creates a JTreeTransferHandler to handle a certain tree. Note that this
	 * constructor does NOT set this transfer handler to be that tree's transfer
	 * handler, you must still add it manually.
	 * 
	 * @param tree
	 */
	public JTreeTransferHandler(TopicTree tree)
	{
		super();
		this.tree = (DefaultTreeModel) tree.getModel();
	}
 
	/**
	 * 
	 * @param c
	 * @return
	 */
	@Override
	public int getSourceActions(JComponent c)
	{
		return TransferHandler.COPY_OR_MOVE;
	}
 
	/**
	 * 
	 * @param c
	 * @return null if no nodes were selected, or this transfer handler was not
	 *         added to a DnDJTree. I don't think it's possible because of the
	 *         constructor layout, but one more layer of safety doesn't matter.
	 */
	@Override
	protected Transferable createTransferable(JComponent c)
	{
		if (c instanceof TopicTree)
		{
			ArrayList<TreePath> valid_selection = new ArrayList<TreePath>();
			TopicTreeNode node = (TopicTreeNode)((TopicTree)c).getLastSelectedPathComponent();
			if (node != null && node instanceof TopicTreeDnDNode)
			 valid_selection.add(new TreePath(node.getPath()));
			return new DnDTreeList(valid_selection);
		}
		else
		{
			return null;
		}
	}
 
	/**
	 * @param c
	 * @param t
	 * @param action
	 */
	@Override
	protected void exportDone(JComponent c, Transferable t, int action)
	{
		if (action == TransferHandler.MOVE)
		{
			// we need to remove items imported from the appropriate source.
			try
			{
				// get back the list of items that were transfered
				ArrayList<TreePath> list = ((DnDTreeList) t
						.getTransferData(DnDTreeList.DnDTreeList_FLAVOR)).getNodes();
				for (int i = 0; i < list.size(); i++)
				{
					// remove them
//					this.tree.removeNodeFromParent((DnDNode) list.get(i).getLastPathComponent());
				}
			}
			catch (UnsupportedFlavorException exception)
			{
				// for debugging purposes (and to make the compiler happy). In
				// theory, this shouldn't be reached.
				logger.error(exception.getMessage(),exception);
			}
			catch (IOException exception)
			{
				// for debugging purposes (and to make the compiler happy). In
				// theory, this shouldn't be reached.
				logger.error(exception.getMessage(),exception);
			}
		}
	}
 
	/**
	 * 
	 * @param supp
	 * @return
	 */
	@Override
	public boolean canImport(TransferSupport supp)
	{
		// Setup so we can always see what it is we are dropping onto.
		supp.setShowDropLocation(true);
		if (supp.isDataFlavorSupported(DnDTreeList.DnDTreeList_FLAVOR))
		{
			// at the moment, only allow us to import list of DnDNodes
 
			// Fetch the drop path
			TreePath dropPath = ((JTree.DropLocation) supp.getDropLocation()).getPath();
			if (dropPath == null)
			{
				// Debugging a few anomalies with dropPath being null. In the
				// future hopefully this will get removed.
				logger.info("Drop path somehow came out null");
				return false;
			}
			// Determine whether we accept the location
			if (dropPath.getLastPathComponent() instanceof TopicTreeDnDNode)
			{
				// only allow us to drop onto a DnDNode
				try
				{
					// using the node-defined checker, see if that node will
					// accept
					// every selected node as a child.
					TopicTreeDnDNode parent = (TopicTreeDnDNode) dropPath.getLastPathComponent();
					ArrayList<TreePath> list = ((DnDTreeList) supp.getTransferable()
							.getTransferData(DnDTreeList.DnDTreeList_FLAVOR)).getNodes();
					for (int i = 0; i < list.size(); i++)
					{
						if (parent.getAddIndex((TopicTreeDnDNode) list.get(i).getLastPathComponent()) < 0)
						{
							return false;
						}
					}
 
					return false;
				}
				catch (UnsupportedFlavorException exception)
				{
					// Don't allow dropping of other data types. As of right
					// now,
					// only DnDNode_FLAVOR and DnDTreeList_FLAVOR are supported.
					logger.error(exception.getMessage(),exception);
				}
				catch (IOException exception)
				{
					// to make the compiler happy.
					logger.error(exception.getMessage(),exception);
				}
			}
		}
		// something prevented this import from going forward
		return false;
	}
 
	/**
	 * 
	 * @param supp
	 * @return
	 */
	@Override
	public boolean importData(TransferSupport supp)
	{
		if (this.canImport(supp))
		{
 
			try
			{
				// Fetch the data to transfer
				Transferable t = supp.getTransferable();
				ArrayList<TreePath> list = ((DnDTreeList) t
						.getTransferData(DnDTreeList.DnDTreeList_FLAVOR)).getNodes();
				// Fetch the drop location
				TreePath loc = ((javax.swing.JTree.DropLocation) supp.getDropLocation()).getPath();
				// Insert the data at this location
				for (int i = 0; i < list.size(); i++)
				{
					this.tree.insertNodeInto((TopicTreeDnDNode) list.get(i).getLastPathComponent(),
							(TopicTreeDnDNode) loc.getLastPathComponent(), ((TopicTreeDnDNode) loc
									.getLastPathComponent()).getAddIndex((TopicTreeDnDNode) list.get(i)
									.getLastPathComponent()));
				}
				// success!
				return true;
			}
			catch (UnsupportedFlavorException e)
			{
				// In theory, this shouldn't be reached because we already
				// checked to make sure imports were valid.
				logger.error(e.getMessage(),e);
			}
			catch (IOException e)
			{
				// In theory, this shouldn't be reached because we already
				// checked to make sure imports were valid.
				logger.error(e.getMessage(),e);
			}
		}
		// import isn't allowed at this time.
		return false;
	}
}