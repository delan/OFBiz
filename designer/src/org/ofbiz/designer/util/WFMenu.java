package org.ofbiz.designer.util;

import javax.swing.JMenu;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerListener;
import java.awt.event.ContainerEvent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import java.util.Vector;

public class WFMenu extends JMenu implements WFDynamicMenuInterface {
    public boolean selected = false;
    Color color = Color.magenta;
    Color defaultColor = Color.magenta;
    Color selectColor = Color.lightGray;
    Font defaultFont = null;
    Font hoverFont = null;

    int borderThickness = 0;
    int defaultBorderThickness = 0;
    int hoverBorderThickness = 1;
    
    private Vector actionListeners = new Vector();
	public static WFMenu lastMenu = null;

    public WFMenu(String name){
        super(name);
				//setName(name);				
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        defaultFont = getFont();
        int size = defaultFont.getSize();
        hoverFont = new Font(defaultFont.getName(), defaultFont.PLAIN, size-2);
    }
	
	
	public void setPopupMenuVisible2(boolean value){
		if (lastMenu != null) {
			// This is a bug workaround.  When setting visibility of a popup menu to false, 
			// the current swing API leaves sub-menus visible. 
			MenuElement[] newME = {MenuSelectionManager.defaultManager().getSelectedPath()[0]};
			MenuSelectionManager.defaultManager().setSelectedPath(newME);
		}
		setPopupMenuVisible(value);
	}
	
	public void setPopupMenuVisible(boolean value){
		super.setPopupMenuVisible(value);
		if (value == false)
			lastMenu = null;
		else
			lastMenu = this;
	}
	
    public void paintComponent(Graphics g){
//System.err.println("paintComponent called for " + getText());			
        super.paintComponent(g);
        Color backup = g.getColor();
        Rectangle bounds = getBounds();
        int width = bounds.width;
        int height = bounds.height;
        g.setColor(Color.black);
        drawThickRect(g, 0, 0, width-1, height-1, borderThickness);
        g.setColor(backup);
    }

    public void updateDynamicMenu(String menuName, String[] menuItemNames,
                                  String[] menuItemCommands) {
        Component[] comps = getMenuComponents();
        for (int i=0; i<comps.length; i++){
            if ( comps[i] instanceof WFDynamicMenuInterface ) {
                ((WFDynamicMenuInterface)comps[i]).
                                                   updateDynamicMenu(menuName, menuItemNames, menuItemCommands);
            }
        }
    }

    public void setDynamicMenuName(String menuName) {
        return;  // this is not needed here
    }

    public void mouseEntered(MouseEvent e){
        setFont(hoverFont);
        borderThickness = hoverBorderThickness;
        repaint();
    }

    public void mouseExited(MouseEvent e){
        setFont(defaultFont);
        borderThickness = defaultBorderThickness;
        repaint();
    }

    void drawThickRect(Graphics g, int x, int y, int width, int height, int thickness){
        for (int i=0; i<thickness; i++)
            g.drawRect(x + i, y + i, width - 2*i, height - 2*i);
    }

    protected void processMouseEvent(MouseEvent e) {
        int id = e.getID();
        switch(id) {
        case MouseEvent.MOUSE_EXITED:
            mouseExited(e);
            break;
        case MouseEvent.MOUSE_ENTERED:
            mouseEntered(e);
            break;
        }
        super.processMouseEvent(e);
    }
}
    