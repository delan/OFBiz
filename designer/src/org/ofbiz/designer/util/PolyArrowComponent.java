package org.ofbiz.designer.util;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class PolyArrowComponent extends PolyLineComponent{
	public PolyArrowComponent(Vector points){
		if (points.size() < 2)
			throw new RuntimeException("NEED AT LEAST TWO POINTS FOR POLYLINE");
		
		Point previousPoint, currentPoint = (Point)points.elementAt(0);
		int i;
		for (i=1; i<points.size()-1; i++){
			previousPoint = currentPoint;
			currentPoint = (Point)points.elementAt(i);
			add(new LineComponent(currentPoint, previousPoint));
		}
		previousPoint = currentPoint;
		currentPoint = (Point)points.elementAt(i);
		add(new ArrowComponent(currentPoint, previousPoint));
		
		updateBounds();
	}

	/*
	public void swapOrientation(){
	int count = getComponentCount();
	ArrowComponent arrow = (ArrowComponent)getComponent(count-1);
	LineComponent newLine = new LineComponent(new Point(arrow.headX, arrow.headY), new Point(arrow.tailX, arrow.tailY));

	LineComponent line = (LineComponent)getComponent(0);
	ArrowComponent newArrow = new ArrowComponent(new Point(line.tailX, line.tailY), new Point(line.headX, line.headY));
	
	remove(arrow);
	remove(line);
	add(newLine);
	add(newArrow);
	updateBounds(newLine);
	updateBounds(newArrow);
	}1
	*/
	
	

	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 600, 500);
		Vector vec = new Vector();
		vec.addElement(new Point(0, 0));
		vec.addElement(new Point(50, 0));
		vec.addElement(new Point(50, 100));
		vec.addElement(new Point(200, 100));
		//vec.addElement(new Point(200, 200));
		PolyArrowComponent line = new PolyArrowComponent(vec);
		//PolyLineComponent line = new PolyLineComponent(vec);
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.add(line);
		//frame.getContentPane().setLayout(null);
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		frame.getContentPane().repaint();
		while (true){
			SafeThread.sleep(2000);
			line.swapOrientation();
			frame.getContentPane().repaint();
		}
	}
}
