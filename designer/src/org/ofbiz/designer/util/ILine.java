package org.ofbiz.designer.util;

import java.awt.*;

public interface ILine{
	public Point getHead();
	public Point getTail();
	public void setHead(Point p);
	public void setTail(Point p);
	public void switchDirection();
	public void rotateAroundHead(float angle);
	public void rotateAroundTail(float angle);
	public void setLengthFromHead(float length);
	public void setLengthFromTail(float length);
	public void translateForward(float distance);
	public void translateLeft(float distance);
	public void translate(float x, float y);
	public Rectangle getBounds();
	public void setBounds(Rectangle bounds);
	public Polygon containingRect(float radius);
}
