package org.ofbiz.designer.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrintFrame extends JFrame implements Printable, ActionListener {
	
	JComponent comp;
	// ATTENTION !@!  VERRRY VERRY IMPORTANT !!!!!
	// compIn should be a Printable
	public PrintFrame(JComponent compIn, int frameState) {
		if (!(compIn instanceof Printable))
			throw new RuntimeException("PrintFrame component in is not a Printable");
		comp = compIn;

		initialize();
		
		setState(frameState);
	}

	void initialize() {

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(comp, BorderLayout.CENTER);

		JButton printButton = new JButton("Print");
		printButton.setActionCommand("Print");
		printButton.addActionListener(this);
		JButton prevButton = new JButton("<- Prev");
		prevButton.setActionCommand("Prev");
		prevButton.addActionListener(this);
		JButton nextButton = new JButton("Next ->");
		nextButton.setActionCommand("Next");
		nextButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(printButton);
		buttonPanel.add(prevButton);
		buttonPanel.add(nextButton);
		cp.add(buttonPanel, BorderLayout.SOUTH);
		
		Paper p = new Paper();
// next line is wrong because it does not take into account extra size for frame correctly
		setSize((int)p.getImageableWidth(), (int)p.getImageableHeight()+50);
//System.err.println(p.getImageableWidth() +"/"+p.getImageableHeight());
//System.err.println("comp height is " + comp.getHeight());

	}
	
	public int print( Graphics g, PageFormat pf, int pi ) {
		int returnvalue;
		try{
			returnvalue = ((Printable)comp).print(g, pf, pi);
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
		return returnvalue;
	}
	
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
System.err.println("Command was: " + command);
		if( command.equals("Print") ) {
			PrinterJob printJob = PrinterJob.getPrinterJob();
			setState(JFrame.ICONIFIED);
			//PrintFrame printFrame = new PrintFrame(new PrintTaskPane(node), JFrame.ICONIFIED);
			printJob.setPrintable(this);
			//printFrame.setVisible(true);
//			if(printJob.printDialog()){
				try {
					printJob.print();
					dispose();
				} catch (PrinterException pe) {
					dispose();
					pe.printStackTrace();
				}
//			}
		} else if( command.equals("Prev") ) {
			((PrintableWF)comp).prevPage();
		} else if( command.equals("Next") ) {
			((PrintableWF)comp).nextPage();
		}
		
	}
}
