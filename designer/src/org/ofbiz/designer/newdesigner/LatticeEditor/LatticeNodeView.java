package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.newdesigner.LatticeEditor.model.*;
import javax.swing.*;
import java.awt.event.*;
import org.ofbiz.designer.util.*;
import java.awt.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeNodeModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeModel;

import java.util.*;
import java.awt.dnd.*;
import java.io.*;
import java.awt.datatransfer.*;

public class LatticeNodeView extends ContainedDraggableJComponent implements IView {
	protected ILatticeNodeModel theModel;
	protected org.ofbiz.designer.util.CenteredTextJLabel theLabel;
	protected LatticeNodeDialog domainDialog;
	protected PopupMouseListener popListen;
	private DragSource dragSource;
	private DropTarget dropTarget;
	
	public LatticeNodeView(ILatticeNodeModel modelIn, Container containerIn) {
		super(containerIn);
		initDomain();
		setModel(modelIn);
	}
	
	public void setModel(IModel modelIn){
		if (theModel == modelIn)
			return;
		if (modelIn != null && !(modelIn instanceof IModelProxySupportClass))
			throw new RuntimeException("Model is not a Proxy");
		if (theModel != null)
			theModel.setGui(null);
		theModel = (ILatticeNodeModel)modelIn;
		theModel.setGui(this);
		synchronize();
	}
	
	public IModel getModel() {
		return theModel;
	}
	
	private void initDomain() {
		
		setSize(200,60);
		setBorder(new javax.swing.border.EtchedBorder());
		theLabel = new CenteredTextJLabel("NewLabel");
		setBackground(new Color(0,0,0));
		theLabel.setBounds(50,20,100,20);
		theLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		theLabel.setFont(new Font("Tahoma",Font.PLAIN,12));
		theLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		add(theLabel);
		
		String[] popItems = {"Delete"};
		
		addMouseListener(new PopupMouseListener(popItems,this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ILatticeModel modelParent = theModel.getParent();
				
				theModel.die();
			
			}
		}));
		
		
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) return;
				handleMousePressed();
			}
			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				((LatticeView)((ILatticeModel)theModel.getParent()).getGui()).doNodePropertyEdit((ILatticeNodeModel)LatticeNodeView.this.getModel());
			}
			
			public void mouseReleased(MouseEvent e) {
				LatticeView topMostFrame = (LatticeView) theModel.getParent().getGui();
				if(topMostFrame.getCurrTool() == null) {
					theModel.setLocation(LatticeNodeView.this.getLocation());
				}
			}
		});
		
		
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				LatticeView enView = (LatticeView)theModel.getParent().getGui();
				if(enView.getPossibleSource()!=null) {
					Point theLoc = getLocation();
					enView.setMouseX(e.getX()+theLoc.x);
					enView.setMouseY(e.getY()+theLoc.y);
					enView.repaint();
				}
			}
		});	
		
	}
	
	public void synchronize() {
		if(theModel==null) return;
		if(theModel.getParent()==null) {
			getParent().remove(this);
			return;
		}
		setBounds(theModel.getBounds());
		setBackground(theModel.getColor());
		theLabel.setText(theModel.getName());
		
		
		repaint();
	}
	
	public void handleMousePressed() {
		
		LatticeView topMostFrame = (LatticeView) theModel.getParent().getGui();
		
		if(topMostFrame.getCurrTool() == null) {
			setDragEnabled(true);
		}
		else {
			setDragEnabled(false);
				
			if(topMostFrame.getCurrTool() == topMostFrame.getArcTool()) {
				if(topMostFrame.getPossibleSource() == null) {
					topMostFrame.setPossibleSource(this);
				}
				else {
					ILatticeNodeModel src = (ILatticeNodeModel)((LatticeNodeView)topMostFrame.getPossibleSource()).getModel();
					ILatticeNodeModel castModel = (ILatticeNodeModel)getModel(); 
					if(this != src) {
						theModel.getParent().addLatticeLink(src,castModel);						
					}
					topMostFrame.unselectCurrTool();
				}
			}
			else checkForRestOfTools();
		}
	}
	
	protected void checkForRestOfTools() {
		//purposefully not implemented.
	}
				
}