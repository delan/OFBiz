package org.ofbiz.designer.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Arrow extends Line {
	private int clickRadius = 10;
	private Polygon arrowHead = null;
	private Color color;
	private int thickness;
	
	public boolean dotted = false;

	private static int temp = 0;
	private int instanceTemp = 0;
	public Arrow(int tailxIn, int tailyIn, int headxIn, int headyIn) {
		super(tailxIn, tailyIn, headxIn, headyIn);
		instanceTemp = temp++;
		color = Color.red;
		thickness = 1;
	}

	public void setArrowHead(){
		try{
			float arrowAngle = 20f + 10f*thickness;
			arrowHead = new Polygon();
			Line temp = (Line)clone();
			temp.setLengthFromHead(thickness + clickRadius);
			arrowHead.addPoint(temp.getTailX(), temp.getTailY());
			temp.rotateAroundHead(arrowAngle/2f);
			temp.setLengthFromHead(2*(clickRadius+thickness));
			arrowHead.addPoint(temp.getTailX(), temp.getTailY());
			arrowHead.addPoint(temp.getHeadX(), temp.getHeadY());
			temp.rotateAroundHead(360 - arrowAngle);
			arrowHead.addPoint(temp.getTailX(), temp.getTailY());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void setThickness(int n){
		thickness = n;
	}

	public int getThickness(){
		return thickness;
	}

	public void paint(Graphics g){
		//Color savedColor = g.getColor();
		//g.setColor(color);
		double length = getLength();
		if (!dotted){
			Polygon arrowGon = containingRect(thickness);
			g.fillPolygon(arrowGon);
		} else {
			Polygon[] arrowGon = containingRectDashed(10);
			LOG.println("got a dotted paint:" + arrowGon.length);
			for (int i=0; i<arrowGon.length; i++)
				g.fillPolygon(arrowGon[i]);
		}
		setArrowHead();
		g.fillPolygon(arrowHead);
		//g.setColor(savedColor);
	}

}

