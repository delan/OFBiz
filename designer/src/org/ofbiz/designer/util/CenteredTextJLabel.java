package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.*;

public class CenteredTextJLabel extends JLabel {
	
	public CenteredTextJLabel(String sometxt) {
		setText(sometxt);
	}
	
	public void paintComponent(Graphics g){
		if(getText()==null) return;
		
		Graphics2D g2 = (Graphics2D)g;	
		java.awt.font.FontRenderContext fr = g2.getFontRenderContext();
		Font f = g.getFont();
		Rectangle r = f.getStringBounds(getText(), fr).getBounds();
		g.drawString(getText(),((int)(getWidth()/2))-((int)(r.getWidth()/2)),
								((int)(getHeight()/2))+((int)(r.getHeight()/4)));
		
	}
		
}
