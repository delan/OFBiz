package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class SimpleTextArea extends JFrame
{
	public JTextArea textArea = new JTextArea();
	
	public SimpleTextArea(String message){
		Container cp = getContentPane();		
		cp.setLayout(new BorderLayout());
		Dimension dim = new Dimension(100, 100);
		textArea.setText(message);
		textArea.setLineWrap(true);
		cp.add(textArea, BorderLayout.CENTER);
		setSize(dim);
	}
	
	public SimpleTextArea(){
		Container cp = getContentPane();		
		cp.setLayout(new BorderLayout());
		Dimension dim = new Dimension(100, 100);
		textArea.setLineWrap(true);
		cp.add(textArea, BorderLayout.CENTER);
		setSize(dim);
	}
	
	public static void main(String[] args){
		SimpleTextArea temp = new SimpleTextArea("one day a fox was very hungry, it wanted to eat grapes, the grapes were very high, so he could not reach them, so he said the grapes are sour");
		temp.setSize(400, 300);
		temp.setVisible(true);
	}
}
