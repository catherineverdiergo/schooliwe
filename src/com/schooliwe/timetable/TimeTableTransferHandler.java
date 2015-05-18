package com.schooliwe.timetable;

import java.awt.Point;

import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.schooliwe.tree.DnDTreeList;
import com.schooliwe.tree.TopicTreeDnDNode;

@SuppressWarnings("serial")
public class TimeTableTransferHandler extends TransferHandler {

	private static final Logger logger = Logger.getLogger(TimeTableTransferHandler.class);

	/**
	 * 
	 * @param supp
	 * @return
	 */
	private Point lastPoint = null;
	private boolean canImport = false;
	
	private boolean pointHasChanged(TransferSupport supp) {
		boolean result = false;
		Point point = supp.getDropLocation().getDropPoint();
		if (lastPoint==null || !lastPoint.equals(point)) {
			result = true;
			lastPoint = point;
		}
		return result;
	}
	
	private boolean isSourceValid(TransferSupport supp) {
		boolean result = false;
		try {
			DnDTreeList list = (DnDTreeList)supp.getTransferable().getTransferData(DnDTreeList.DnDTreeList_FLAVOR);
			if (list!=null && list.getTransferDataFlavors()!=null && list.getNodes().size()!=0)
				result = true;
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	@Override
	public boolean canImport(TransferSupport supp)
	{
		if (pointHasChanged(supp)) {
			// Setup so we can always see what it is we are dropping onto.
			if (supp.isDataFlavorSupported(DnDTreeList.DnDTreeList_FLAVOR) &&
					supp.getComponent() instanceof JTable &&
					isSourceValid(supp)
			   )
			{
				Point point = supp.getDropLocation().getDropPoint();
				JTable table = (JTable)supp.getComponent();
				int row, column;
				row = table.rowAtPoint(point);
				column = table.columnAtPoint(point);
				
				Object value = table.getModel().getValueAt(row, column);
				if (!(value instanceof TimeTableCellTopic))
					canImport = false;
				else
					canImport = true;
			}
			// something prevented this import from going forward
//			System.out.println(canImport+" "+cpt++);
		}
		supp.setShowDropLocation(false);
		return canImport;
	}

	@Override
	public boolean importData(TransferSupport support) {
		try {
			Object o = support.getTransferable().getTransferData(DnDTreeList.DnDTreeList_FLAVOR);
			if (!((DnDTreeList)o).getNodes().isEmpty()) {
				TopicTreeDnDNode data = (TopicTreeDnDNode)((DnDTreeList)o).getNodes().get(0).getLastPathComponent();
				Point point = support.getDropLocation().getDropPoint();
				JTable table = (JTable)support.getComponent();
				int row, column;
				row = table.rowAtPoint(point);
				column = table.columnAtPoint(point);
				if (table.getParent().getParent().getParent() instanceof TimeTable) {
					if (support.getDropAction()==MOVE)
						((TimeTableCellTopic)table.getModel().getValueAt(row, column)).cancelReplaceTopic(data.getDndLabel());
					else if (support.getDropAction()==COPY)
						((TimeTableCellTopic)table.getModel().getValueAt(row, column)).addTopic(data.getDndLabel());
				}
				else
					((TimeTableCellTopic)table.getModel().getValueAt(row, column)).cancelReplaceTopic(data.getDndLabel());
				((TimeTableModel)table.getModel()).fireTableCellUpdated(row, column);
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return super.importData(support);
	}
 
}
