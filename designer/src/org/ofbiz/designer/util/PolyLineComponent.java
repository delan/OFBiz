package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PolyLineComponent extends JComponent{
	protected PolyLineComponent(){
	}
	
	public PolyLineComponent(Vector points){
		if (points.size() < 2)
			throw new RuntimeException("NEED AT LEAST TWO POINTS FOR POLYLINE");
		
		Point previousPoint, currentPoint = (Point)points.elementAt(0);
		for (int i=1; i<points.size(); i++){
			previousPoint = currentPoint;
			currentPoint = (Point)points.elementAt(i);
			add(new LineComponent(currentPoint, previousPoint));
		}
		updateBounds();
	}
	
	protected void updateBounds() {
		Rectangle rect = getComponent(0).getBounds();
		for (int i=1; i<getComponentCount(); i++)
			rect.add(getComponent(i).getBounds());
		setBounds(rect);
		
		for (int i=0; i<getComponentCount(); i++){
			LineComponent comp = (LineComponent)getComponent(i);
			Rectangle bounds = comp.getBounds();
			bounds.translate(-getBounds().x, -getBounds().y);
			comp.setBounds(bounds);
		}
	}

	protected void updateBounds(LineComponent comp) {
		Rectangle rect = comp.getBounds();
		rect.translate(-getBounds().x, -getBounds().y);
		comp.setBounds(rect);
	}
	
	public boolean contains(int x, int y) {
		for (int i=0; i<getComponentCount(); i++)
			if (getComponent(i).contains(x, y))
				return true;
		return false;
	}
	
	public String toString(){
		String returnString = "" + getComponentCount() + " points ";
		LineComponent line = null;
		for (int i=0; i<getComponentCount(); i++){
			line = (LineComponent)getComponent(i);
			returnString += "" + line.getAbstractLine().getHeadPosition();
		}
		
		return returnString;
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 600, 500);
		Vector vec = new Vector();
		vec.addElement(new Point(0, 0));
		vec.addElement(new Point(100, 0));
		vec.addElement(new Point(100, 100));
		vec.addElement(new Point(200, 100));
		vec.addElement(new Point(200, 200));
		PolyArrowComponent line = new PolyArrowComponent(vec);
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.add(line);
		//frame.getContentPane().setLayout(null);
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		frame.getContentPane().repaint();
	}

	public void swapOrientation(){
		int count = getComponentCount();
		Rectangle bounds = getComponent(0).getBounds();
		int minx = bounds.x;
		int maxx = bounds.x + bounds.width;
		for (int i=1; i<count; i++){
			bounds = getComponent(i).getBounds();
			if (bounds.x < minx) minx = bounds.x;
			int temp = bounds.x + bounds.width;
			if (temp > maxx) maxx = temp;
		}
		
		for (int i=0; i<count; i++){
			if (getComponent(i) instanceof LineComponent){
				LineComponent arrow = ((LineComponent)getComponent(i));
				arrow.reverse();
			}
			bounds = getComponent(i).getBounds();
			bounds.x = maxx - bounds.x - bounds.width;
			getComponent(i).setBounds(bounds);
		}
	}
}
