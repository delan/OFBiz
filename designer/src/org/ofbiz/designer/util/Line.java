package org.ofbiz.designer.util;

import java.awt.*;

public class Line implements Cloneable {
	private float tailx, taily;
	private float headx, heady;

	private float length, slope, slopeAngle; 

	private static final float miniLength = 4f;

	public Line(Point a, Point b) {
		tailx = a.x;
		taily = a.y;
		headx = b.x;
		heady = b.y;
		setLength();
		setSlopeAngle();
	}

	public Line(int tailx, int taily, int headx, int heady){
		this.tailx = tailx;
		this.taily = taily;
		this.headx = headx;
		this.heady = heady;
		setLength();
		setSlopeAngle();
	}

	public void setSlopeAngle(){
		slope = (heady - taily)/(headx - tailx);
		if( slope == -0 ) // JDK 1.1.6 returns -0 for 0 divided by a negative
			slope = 0;
		float angle = (float)Math.atan(slope);
		/*
		Graphical positive X and positive Y is Quadrant I - No change
		Graphical negative X and positive Y is Quadrant II - add 180 degrees
		Graphical negative X and negative Y is Quadrant III - add 180 degrees
		Graphical positive X and negative Y is Quadrant IV - add 360 degrees
		only need to make angle in Quadrant IV positive
		*/
		if (headx < tailx )
			angle += Math.PI;
		else if (headx >= tailx && heady < taily )
			angle += 2*Math.PI;
		slopeAngle = angle;
	}

	public void setLength(){
		length = (float)Math.sqrt((headx - tailx)*(headx - tailx) + (heady - taily)*(heady - taily));
	}

	public float getLength(){
		return (float)Math.sqrt((headx - tailx)*(headx - tailx) + (heady - taily)*(heady - taily));
	}
	
	public Rectangle getBounds(){
		int x, y, width, height;
		
		if (headx > tailx) x = (int)tailx;
		else x = (int)headx;
		
		if (heady > taily) y = (int)taily;
		else y = (int)heady;

		width = (int)Math.abs(headx - tailx);
		height = (int)Math.abs(heady - taily);
		
		return new Rectangle(x, y, width, height);
	}

	public void translate(float x, float y){
		headx += x;
		tailx += x;
		heady += y;
		taily += y;
		setLength();
		setSlopeAngle();
	}
	
	public void setBounds(Rectangle bounds){
		float x1, x2, y1, y2;
		
		if (headx < tailx){
			headx = bounds.x;
			tailx = bounds.x + bounds.width;
			heady = bounds.y;
			taily = bounds.y + bounds.height;
		} else {
			tailx = bounds.x;
			headx = bounds.x + bounds.width;
			taily = bounds.y;
			heady = bounds.y + bounds.height;
		}
		setLength();
		setSlopeAngle();
	}

	public void expandFromTail(float multiplier){
		headx = tailx + multiplier*(headx - tailx);
		heady = taily + multiplier*(heady - taily);
		setLength();
	}

	public void expandFromHead(float multiplier){
		tailx = headx - multiplier*(headx - tailx);
		taily = heady - multiplier*(heady - taily);
		setLength();
	}

	public void setLengthFromHead(float lengthIn){
		length = lengthIn;
		float angle = slopeAngle;
		angle += Math.PI;
		tailx = headx + (float)(length*Math.cos(angle));
		taily = heady + (float)(length*Math.sin(angle));
	}

	public void setLengthFromTail(float lengthIn){
		length = lengthIn;
		float angle = slopeAngle;
		headx = tailx + (float)(length*Math.cos(angle));
		heady = taily + (float)(length*Math.sin(angle));
	}

	public void rotateAroundTail(float angle){
		//uble length = length();
		float currentAngle = slopeAngle;
		currentAngle += angle*Math.PI/180f;
		headx = tailx + (float)(length*Math.cos(currentAngle));
		heady = taily + (float)(length*Math.sin(currentAngle));
		setSlopeAngle();
	}

	public void rotateAroundHead(float angle){
		float currentAngle = slopeAngle;
		currentAngle += Math.PI;
		currentAngle += angle*Math.PI/180f;
		tailx = headx + (float)(length*Math.cos(currentAngle));
		taily = heady + (float)(length*Math.sin(currentAngle));
		setSlopeAngle();
	}

	// using screen coordinate system, assuming observer is facing in direction of head
	public void translateLeft(float distance) {
		float tSlope = Math.abs(1/slope);  // perpendicular slope
		float deltaX = (float)Math.sqrt( (distance*distance)/(1 + (tSlope*tSlope)));
		if (heady > taily ) {
			tailx += deltaX;
			headx += deltaX;
		} else {
			tailx -= deltaX;
			headx -= deltaX;
		}
		if (headx < tailx ) {
			if (slope != 0 ) {
				taily += deltaX*tSlope;
				heady += deltaX*tSlope;
			} else {
				taily += distance;
				heady += distance;
			}
		} else {
			if (slope != 0 ) {
				taily -= deltaX*tSlope;
				heady -= deltaX*tSlope;
			} else {
				taily -= distance;
				heady -= distance;
			}
		}
	}

	// using screen coordinate system, assuming observer is facing in direction of head
	public void translateRight(float distance) {
		translateLeft(-distance);
	}

	public void translateForward(float distance) {
		float tempLength = getLength();
		setLengthFromTail(tempLength + distance);
		setLengthFromHead(tempLength);
	}

	public void translateBack(float distance) {
		translateForward(-distance);
	}

	public Polygon containingRect(float radius){
		Polygon rect = new Polygon();
		if (Math.abs(taily-heady) < 0.00001d){
			rect.addPoint((int)tailx, (int)(taily-radius));
			rect.addPoint((int)headx, (int)(taily-radius));
			rect.addPoint((int)headx, (int)(taily+radius));
			rect.addPoint((int)tailx, (int)(taily+radius));
			return rect;
		}
		if (Math.abs(tailx-headx) < 0.00001d){
			rect.addPoint((int)(tailx-radius), (int)taily);
			rect.addPoint((int)(tailx+radius), (int)taily);
			rect.addPoint((int)(tailx+radius), (int)heady);
			rect.addPoint((int)(tailx-radius), (int)heady);
			return rect;
		}
		Line line = new Line((int)tailx, (int)taily, (int)headx, (int)heady);
		line.rotateAroundHead(90);
		line.setLengthFromHead(radius);
		rect.addPoint((int)line.tailx, (int)line.taily);
		line.expandFromTail(2);
		rect.addPoint((int)line.headx, (int)line.heady);

		//line = new Line((int)headx, (int)heady, (int)tailx, (int)taily);
		line = new Line((int)tailx, (int)taily, (int)headx, (int)heady);
		line.rotateAroundTail(90);
		line.setLengthFromTail(radius);
		rect.addPoint((int)line.headx, (int)line.heady);
		line.expandFromHead(2);
		rect.addPoint((int)line.tailx, (int)line.taily);
		return rect;
	}

	// the following returns a "dashed" rectangle or in other words, the containing
	// rectangle as a series of slices.  This is used primarily by the fail arc, to show
	// itself as a dashed line (or very thin rectangle)

	public Polygon[] containingRectDashed(float radius){
		int num = (int)(length/(2*miniLength));
		if (num < 2) num = 2;
		Polygon[] polygonArray = new Polygon[num];

		Line temp1 = null;
		Line temp2 = null;

		temp1 = (Line)clone();
		temp2 = (Line)clone();

		temp1.translateLeft(radius);
		temp2.translateRight(radius);

		temp1.setLengthFromHead(miniLength + getLength());
		temp2.setLengthFromHead(miniLength + getLength());
		temp1.setLengthFromTail(miniLength);
		temp2.setLengthFromTail(miniLength);

		float dx = (float)(2*miniLength*Math.cos(temp1.slopeAngle));
		float dy = (float)(2*miniLength*Math.sin(temp1.slopeAngle));

		for (int i=0; i<num; i++){
			temp1.tailx += dx;
			temp1.headx += dx;
			temp2.tailx += dx;
			temp2.headx += dx;

			temp1.taily += dy;
			temp1.heady += dy;
			temp2.taily += dy;
			temp2.heady += dy;

			polygonArray[i] = new Polygon();
			polygonArray[i].addPoint(temp1.getHeadX(), temp1.getHeadY());
			polygonArray[i].addPoint(temp1.getTailX(), temp1.getTailY());
			polygonArray[i].addPoint(temp2.getTailX(), temp2.getTailY());
			polygonArray[i].addPoint(temp2.getHeadX(), temp2.getHeadY());
		}
		return polygonArray;
	}

	public String toString(){
		String temp = new String();
		temp += tailx + ", ";
		temp += taily + ", ";
		temp += headx + ", ";
		temp += heady + ", ";
		return temp;
	}

	public void paint(Graphics g){
		g.drawLine((int)tailx, (int)taily, (int)headx, (int)heady);

	}

	public Point getTailPosition(){
		return new Point(getTailX(), getTailY());
	}
	
	public Point getHeadPosition(){
		return new Point(getHeadX(), getHeadY());
	}
	
	public int getTailX(){
		return (int) tailx;
	}

	public int getTailY(){
		return (int) taily;
	}

	public int getHeadX(){
		return (int) headx;
	}

	public int getHeadY(){
		return (int) heady;
	}

	public boolean equals(Line line){
		if (line == null) return false;
		if (tailx == line.tailx && headx == line.headx && taily == line.taily && heady == line.heady) return true;
		else return false;
	}

	public boolean equals(Object lineObj){
		return (equals((Line)lineObj));
	}
	
	public static void main(String[] args){
		Line tempLine;
		long startTime;
		long finishTime;

		startTime = System.currentTimeMillis();
		tempLine = new Line(334, 230, 2234, 1455);
		for (int i=0; i<1000; i++)
			tempLine.translateRight(i);

		tempLine = new Line(2234, 1455, 334, 230);
		for (int i=0; i<1000; i++)
			tempLine.translateRight(i);

		tempLine = new Line(2234, 334, 230, 1455);
		for (int i=0; i<1000; i++)
			tempLine.translateRight(i);

		tempLine = new Line(230, 1455, 2234, 334);
		for (int i=0; i<1000; i++)
			tempLine.translateRight(i);

		finishTime = System.currentTimeMillis();

		System.err.println("time taken is " + (finishTime - startTime));

		startTime = System.currentTimeMillis();
		tempLine = new Line(334, 230, 2234, 1455);
		for (int i=0; i<1000; i++)
			tempLine.translateLeft(i);

		tempLine = new Line(2234, 1455, 334, 230);
		for (int i=0; i<1000; i++)
			tempLine.translateLeft(i);

		tempLine = new Line(2234, 334, 230, 1455);
		for (int i=0; i<1000; i++)
			tempLine.translateLeft(i);

		tempLine = new Line(230, 1455, 2234, 334);
		for (int i=0; i<1000; i++)
			tempLine.translateLeft(i);

		finishTime = System.currentTimeMillis();

		System.err.println("time taken is " + (finishTime - startTime));
	}
	
	public Object clone() {
		try{
			return super.clone();
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public float getSlopeAngle() {
		return slopeAngle*(float)(180f/Math.PI);
	}
	
	public void switchDirection(){
		float temp = tailx;
		tailx = headx;
		headx = temp;
		temp = taily;
		taily = heady;
		heady = temp;
		setLength();
		setSlopeAngle();
	}
	
	public Point getHead(){
		return new Point((int)headx, (int)heady);
	}
	
	public Point getTail(){
		return new Point((int)tailx, (int)taily);
	}
	
	public void setHead(Point p){
		headx = p.x;
		heady = p.y;
		setLength();
		setSlopeAngle();
	}
	
	public void setTail(Point p){
		tailx = p.x;
		taily = p.y;
		setLength();
		setSlopeAngle();
	}
	
}