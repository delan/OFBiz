package org.ofbiz.designer.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.font.FontRenderContext;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;


class AlertDialog2  extends JDialog {
	private int frameMargin = 30;
	private int dialogHeight = 0;
	private int dialogWidth = 250;
	private int verticalMargin = 10;
	private int horizontalMargin = 10;
	private int lineHeight = 0;
	private int lineSpacing = 5;
	private int buttonHeight = 20;
	private int buttonWidth = 60;
	private String alertMessage = null;
	private Font alertFont = null;
	private Container cp = null;
	private boolean movedFlag = false;

	private AlertDialog2(Frame owner, String title, boolean modal){
		super(owner, title, modal);
		cp = getContentPane();
		cp.setLayout(null);
	}

	public static void showAlertDialog2(Frame owner, String alert){
		AlertDialog2 alertD = null;
		if( owner == null) {
			owner = new Frame();
			alertD = new AlertDialog2(owner, "  Alert !", true);
			alertD.alertFont = new Font("Arial", Font.PLAIN, 12);
		} else {
			alertD = new AlertDialog2(owner, owner.getName() + " - Alert !", true);
			alertD.alertFont = owner.getGraphics().getFont();
		}
		alertD.alertMessage = alert;
		alertD.buildAlertDialog2();
		alertD.setVisible(true);
	}

	public static void showAlertDialog2(Frame owner, String alert, int widthIn){
		AlertDialog2 alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog2(owner, "  Alert !", true);
			alertD.alertFont = new Font("Arial", Font.PLAIN, 12);
		} else {
			alertD = new AlertDialog2(owner, owner.getName() + " - Alert !", true);
			alertD.alertFont = owner.getGraphics().getFont();
		}
		alertD.alertMessage = alert;
		alertD.dialogWidth = widthIn;
		alertD.buildAlertDialog2();
		alertD.setVisible(true);
	}

	public static void showAlertDialog2(Frame owner, String alert, String title){
		AlertDialog2 alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog2(owner, "  Alert !", true);
			alertD.alertFont = new Font("Arial", Font.PLAIN, 12);
		} else {
			alertD = new AlertDialog2(owner, title + " - Alert !", true);
			alertD.alertFont = owner.getGraphics().getFont();
		}
		alertD.alertMessage = alert;
		alertD.buildAlertDialog2();
		alertD.setVisible(true);
	}

	public static void showAlertDialog2(Frame owner, String alert, String title, int widthIn){
		AlertDialog2 alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog2(owner, "  Alert !", true);
			alertD.alertFont = new Font("Arial", Font.PLAIN, 12);
		} else {
			alertD = new AlertDialog2(owner, title + " - Alert !", true);
			alertD.alertFont = owner.getGraphics().getFont();
		}
		alertD.alertMessage = alert;
		alertD.dialogWidth = widthIn;
		alertD.buildAlertDialog2();
		alertD.setVisible(true);
	}

	public static AlertDialog2 getAlertDialog2(Frame owner, String alert) {
		AlertDialog2 alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog2(owner, "  Alert !", true);
			alertD.alertFont = new Font("Arial", Font.PLAIN, 12);
		} else {
			alertD = new AlertDialog2(owner, " - Alert !", true);
			alertD.alertFont = owner.getGraphics().getFont();
		}
		alertD.alertMessage = alert;
		alertD.buildAlertDialog2();
		return alertD;
	}

	private void buildAlertDialog2() {
		setResizable(true);
		cp.removeAll();
		FontRenderContext frc = new FontRenderContext(alertFont.getTransform(), false, false);

		int messageWidth = dialogWidth - (2*horizontalMargin);
		Vector lines = new Vector();
		char[] alertArray = alertMessage.toCharArray();
		int lineNumber = 0;
		int currentPos = 0;
		int lastPos = 0;
		for (int i=0; i < alertArray.length; i++) {
			lastPos = i;
			for (currentPos=i; currentPos < alertArray.length; currentPos++) {
				int stringWidth = 0;
				if  (alertArray[currentPos] == ' ') {
					stringWidth = (int)alertFont.getStringBounds(alertArray, i, currentPos-i, frc).getWidth();
					if (stringWidth > messageWidth) {
						if (lastPos != i){
							currentPos = lastPos;
						}
						lines.addElement(new String(alertArray, i, (currentPos - i)));
						break;
					}
					lastPos = currentPos;
				} else if (currentPos == (alertArray.length-1)) {
					stringWidth = (int)alertFont.getStringBounds(alertArray, i, currentPos-i+1, frc).getWidth();
					if (stringWidth > messageWidth) {
						if (lastPos != i){
							currentPos = lastPos;
						}
						lines.addElement(new String(alertArray, i, (currentPos - i)));
						break;
					} else {
						lines.addElement(new String(alertArray, i, (currentPos-i+1)));
					}
					lastPos = currentPos;
				} else if ((alertArray[currentPos] == '\\') && (alertArray[currentPos+1] == 'n')) {
					stringWidth = (int)alertFont.getStringBounds(alertArray, i, currentPos-i, frc).getWidth();
					if (stringWidth > messageWidth) {
						if (lastPos != i){
							currentPos = lastPos;
						}
						lines.addElement(new String(alertArray, i, (currentPos - i)));
						currentPos += 1;
						break;
					}
					lines.addElement(new String(alertArray, i, (currentPos - i)));
					currentPos += 1;
					break;
				}
			}
			i = currentPos;
		}
		if (lines.size() != 0) {
			lineHeight = (int)alertFont.getStringBounds((String)lines.elementAt(0), frc).getHeight();
			dialogHeight = (lineHeight*lines.size()) + (lineSpacing*(lines.size()-1))
						   + (3*verticalMargin) + buttonHeight;
		} else {
			dialogHeight = (3*verticalMargin) + buttonHeight;
		}

		//  Build Gui for dialog box
		int yPos = verticalMargin;
		JLabel[] labels = new JLabel[lines.size()];
		for( int i=0; i<labels.length; i++) {
			labels[i] = new JLabel((String)lines.elementAt(i));
			labels[i].setBounds(horizontalMargin, yPos-(lineSpacing/2),
								dialogWidth-horizontalMargin, lineHeight+lineSpacing);
			labels[i].setVisible(true);
			yPos += lineHeight+lineSpacing;
			cp.add(labels[i]);
		}

		yPos += verticalMargin;
		WFButton okButton = new WFButton("Ok");
		okButton.setBounds( (dialogWidth-buttonWidth)/2, yPos,
							buttonWidth, buttonHeight);
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
			setVisible(false);
			}
			});
		cp.add(okButton);

		// the super.setBounds in the next line relies on fact that all size
		//  change methods in the JDialog inheritance tree call setBounds
		if (movedFlag == true ) {
			super.setBounds((int)getLocation().getX(), (int)getLocation().getY(), dialogWidth+10,
							dialogHeight+frameMargin);
		} else {
			Dimension screenDim = getToolkit().getScreenSize();
			super.setBounds((screenDim.width-dialogWidth)/2,
							(screenDim.height-dialogHeight)/2, dialogWidth,
							dialogHeight+frameMargin);
		}
		setResizable(false);
		validate();
	}

	public void setWidth(int widthIn) {
		dialogWidth = widthIn;
		buildAlertDialog2();
		// next line relies on fact that all size change methods in the JDialog
		//  inheritance tree call setBounds
		super.setBounds((int)getLocation().getX(), (int)getLocation().getY(), dialogWidth+10,
						dialogHeight+frameMargin);
	}

	public void setSize(Dimension d) {
		dialogWidth = d.width;
		buildAlertDialog2();
		//    if (d.height > dialogHeight )
		//      dialogHeight = d.height;
		// next line relies on fact that all size change methods in the JDialog
		//  inheritance tree call setBounds
		super.setBounds((int)getLocation().getX(), (int)getLocation().getY(), dialogWidth+10,
						dialogHeight+frameMargin);
	}

	public void setSize(int widthIn, int heightIn) {
		dialogWidth = widthIn;
		buildAlertDialog2();
		//    if (heightIn > dialogHeight )
		//      dialogHeight = heightIn;
		// next line relies on fact that all size change methods in the JDialog
		//  inheritance tree call setBounds
		super.setBounds((int)getLocation().getX(), (int)getLocation().getY(), dialogWidth+10,
						dialogHeight+frameMargin);
	}

	public void setBounds(int xIn, int yIn, int widthIn, int heightIn) {
		movedFlag = true;
		dialogWidth = widthIn;
		buildAlertDialog2();
		super.setBounds(xIn, yIn, dialogWidth, dialogHeight+frameMargin);
	}

	public void setTitle(String title) {
		super.setTitle(title + " - Alert !");
	}

	public static void main(String[] args){
		AlertDialog2 ad = AlertDialog2.getAlertDialog2(null, args[0]);
		ad.setTitle("hi");
		ad.setBounds(10, 10, 200, 1);
		ad.setVisible(true);
		System.exit(0);
	}
}                                     

public class AlertDialog extends JDialog{
	private String alertMessage = null;
	private Container cp = null;
	private int dialogHeight = 100;
	private int dialogWidth = 250;
	
	private JTextArea textArea = null;
	private WFButton okButton = null;
	private WFButton cancelButton = null;
	
	
	private AlertDialog(Frame owner, String title, boolean modal){
		super(owner, title, modal);
		cp = getContentPane();
		cp.setLayout(null);
	}
	
	public static void showAlertDialog(Frame owner, String alert){
		AlertDialog alertD = null;
		if( owner == null) {
			owner = new Frame();
			alertD = new AlertDialog(owner, "  Alert !", true);
		} else {
			alertD = new AlertDialog(owner, owner.getName() + " - Alert !", true);
		}
		alertD.alertMessage = alert;
		alertD.buildAlertDialog();
		alertD.setVisible(true);
		System.err.println("done");
	}

	public static void showAlertDialog(Frame owner, String alert, int widthIn){
		AlertDialog alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog(owner, "  Alert !", true);
		} else {
			alertD = new AlertDialog(owner, owner.getName() + " - Alert !", true);
		}
		alertD.alertMessage = alert;
		alertD.dialogWidth = widthIn;
		alertD.buildAlertDialog();
		alertD.setVisible(true);
	}

	public static void showAlertDialog(Frame owner, String alert, String title){
		AlertDialog alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog(owner, "  Alert !", true);
		} else {
			alertD = new AlertDialog(owner, title + " - Alert !", true);
		}
		alertD.alertMessage = alert;
		alertD.buildAlertDialog();
		alertD.setVisible(true);
	}

	public static void showAlertDialog(Frame owner, String alert, String title, int widthIn){
		System.err.println(4);
		AlertDialog alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog(owner, "  Alert !", true);
		} else {
			alertD = new AlertDialog(owner, title + " - Alert !", true);
		}
		alertD.alertMessage = alert;
		alertD.dialogWidth = widthIn;
		alertD.buildAlertDialog();
		alertD.setVisible(true);
	}
	
	public static AlertDialog getAlertDialog(Frame owner, String alert) {
		AlertDialog alertD = null;
		if (owner == null) {
			owner = new Frame();
			alertD = new AlertDialog(owner, "  Alert !", true);
		} else {
			alertD = new AlertDialog(owner, " - Alert !", true);
		}
		alertD.alertMessage = alert;
		alertD.buildAlertDialog();
		return alertD;
	}
	
	void buildAlertDialog(){
		cp.removeAll();
		System.err.println("setting bounds " + dialogWidth + " " + dialogHeight);
		setBounds(0, 0, dialogWidth, dialogHeight);
		cp.setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setBackground(getBackground());
		textArea.setBorder(null);
		textArea.setText(alertMessage);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		System.err.println("border is " + textArea.getBorder());
		
		okButton = new WFButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});
		cp.add(textArea, BorderLayout.CENTER);
		cp.add(okButton, BorderLayout.SOUTH);
		System.err.println("setting visible true");
		cp.setVisible(true);
		//new Throwable().printStackTrace();
	}
	
	public static void main(String[] args){
		System.err.println(80+870+16+556+69+28+1926+380);
		AlertDialog ad = AlertDialog.getAlertDialog(null, args[0]);
		ad.setTitle("hi");
		ad.setBounds(10, 10, 200, 100);
		ad.setVisible(true);
		System.exit(0);
	}
}