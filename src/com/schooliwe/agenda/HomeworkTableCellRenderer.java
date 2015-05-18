package com.schooliwe.agenda;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class HomeworkTableCellRenderer extends DefaultTableCellRenderer {
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JScrollPane textScroll = new JScrollPane();
		JTextArea text = new JTextArea();
		Homework homework = (Homework)value;
		label1.setText("         "+homework.getTopic());
		label2.setIcon(HomeworkEditor.getInstance().getIcon(homework.getType()));
		label2.setText(HomeworkEditor.getInstance().getLabel(homework.getType()));
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		textScroll.getViewport().add(text);
//		textScroll.setPreferredSize(new Dimension(100,60));
		result.add(label1);
		result.add(label2);
		result.add(textScroll);
		text.setText(homework.getText());
//		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
//				row, column);
		return result;
	}

}
