package org.ofbiz.designer.util;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.Dimension;

public abstract class PopupCompletionMenu extends JPopupMenu{
	public static final int ENTER_CODE = 10;
	public static final int ESC_CODE = 27;
	public static final int LEFT_ARROW_CODE = 37;
	public static final int RIGHT_ARROW_CODE = 39;
	public static final int UP_ARROW_CODE = 38;
	public static final int DOWN_ARROW_CODE = 40;
	public static final int BACKSPACE_CODE = 8;
	public static final int DEL_CODE = 127;
	public static final int J_KEY_CODE = 74;
	public static final int SPACE_KEY_CODE = 32;
	
	final public MyMenuItem menuItem = new MyMenuItem();
	final public JMenuItem header = new JMenuItem("Esc : cancel, Ctrl-J : reInvoke");
}