package com.schooliwe.swing.util;

import java.awt.Insets;

import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class TranslucentBorder extends EmptyBorder {

	public TranslucentBorder(Insets arg0) {
		super(arg0);
	}

	public TranslucentBorder(int top, int left, int bottom, int right) {
		super(top, left, bottom, right);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

}
