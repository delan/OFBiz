package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import java.awt.event.*;
import javax.swing.event.*;

public class WFButton extends JButton { 
    Font defaultFont = null;
    Font hoverFont = null;

    Border hoverBorder = null;

    int borderThickness = 1;
    int defaultBorderThickness = 1;
    int hoverBorderThickness = 4;

    int shiftState = 0;
    private boolean checkable = false;
    private JCheckBox checkBox = null;
    private JLabel label;

    public WFButton(String name){
        super(name);
        hoverBorder = getBorder();
        defaultFont = getFont();
        int size = defaultFont.getSize();
        hoverFont = new Font(defaultFont.getName(), defaultFont.PLAIN, size-2);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setSelected(true);
        setLayout(new FlowLayout());
    }

    public void setDynamicFont(boolean dynamic){
        if(dynamic)
            hoverFont = new Font(defaultFont.getName(), defaultFont.PLAIN, defaultFont.getSize()-2);
        else
            hoverFont = defaultFont;
    }

    public boolean getCheckable(){
        return checkable;
    }

    public void setCheckable(boolean checked){
        checkable = checked;
        if(!checked)
            checkBox.setVisible(false);
        else
            checkBox.setVisible(true);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Color backup = g.getColor();
        Rectangle bounds = getBounds();
        int width = bounds.width;
        int height = bounds.height;
        g.setColor(Color.black);
        //drawThickShadow(g, 0, 0, width-1, height-1, borderThickness);
        drawThickShadow(g, 0, 0, width, height, borderThickness);
        g.setColor(backup);
    }

    public void mousePressed(MouseEvent e){
        shiftState = e.getModifiers() & e.SHIFT_MASK;
        setFont(defaultFont);
        //setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        //setBorder(new MetalBorders.Flush3DBorder());
        repaint();
    }

    public void mouseClicked(MouseEvent e){
    }

    public void mouseReleased(MouseEvent e){
        //this.setBackground(defaultColor);
        setFont(hoverFont);
        //setBorder(new EtchedBorder(EtchedBorder.RAISED));
        repaint();
    }

    public void mouseEntered(MouseEvent e){
        setFont(hoverFont);
        borderThickness = hoverBorderThickness;
        //setBorder(null);
        //setBorder(new EtchedBorder(EtchedBorder.RAISED));
        repaint();
    }

    public void mouseExited(MouseEvent e){
        setFont(defaultFont);
        borderThickness = defaultBorderThickness;
        //setBorder(hoverBorder);
        //setBorder(null);
        repaint();
    }

    void drawThickRect(Graphics g, int x, int y, int width, int height,
                       int thickness) { 
        for(int i=0; i<thickness; i++)
            g.drawRect(x + i, y + i, width - 2*i, height - 2*i);
    }

    void drawThickShadow(Graphics g, int x, int y, int width, int height,
                         int thickness) { 
        int i=0;
        Color tempColor = g.getColor();
        g.setColor(Color.black);
        for(i=0; i<thickness; i++){
            g.drawLine(x + width - i, y, x + width - i, y + height - i);
            g.drawLine(x, y + height - i, x + width - i, y + height - i);
        }
        g.setColor(Color.white);
        for(i=0; i<thickness; i++){
            g.drawLine(x + i, y + i, x + width - i, y + i);
            g.drawLine(x + i, y + i, x + i, y + height - i);
        }
        g.setColor(tempColor);
    }

    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        //LOG.println(e.toString());
        int id = e.getID();
        switch(id) {
        case MouseEvent.MOUSE_PRESSED:
            mousePressed(e);
            break;
        case MouseEvent.MOUSE_RELEASED:
            mouseReleased(e);
            break;
        case MouseEvent.MOUSE_CLICKED:
            mouseClicked(e);
            break;
        case MouseEvent.MOUSE_EXITED:
            mouseExited(e);
            break;
        case MouseEvent.MOUSE_ENTERED:
            mouseEntered(e);
            break;
        }
    }

    protected void fireActionPerformed(ActionEvent e){
        ActionEvent newE = new ActionEvent(this, e.getID(), e.getActionCommand(), shiftState);
        super.fireActionPerformed(newE);
    }

    public void setShiftState(int state){
        shiftState = state;
    }
}
