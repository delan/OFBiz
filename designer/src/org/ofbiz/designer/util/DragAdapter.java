package org.ofbiz.designer.util;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.*;
import java.io.*;

public abstract class DragAdapter implements DragSourceListener, DropTargetListener, DragGestureListener, Serializable{
	protected DragSource dragSource = null;
	protected Component comp = null;	
	protected DropTarget dropTarget = null;
	
	public DragAdapter(Component _comp){
		dragSource = new DragSource();
		comp = _comp;
		dropTarget = new DropTarget (comp, this);
		dragSource.createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_MOVE, this);
	}
	
	/*
	public DragAdapter(DragSource dragSourceIn){
		dragSource = dragSourceIn;
		if (dragSource == null) throw new RuntimeException("dragsource is Null");
	}
	*/
	
	public void dragOver(DragSourceDragEvent e){
	}
	
	public void dropActionChanged(DragSourceDragEvent e){
	}
	
	public void dragExit(DragSourceEvent e){
	}
	
	public void dragDropEnd(DragSourceDropEvent e){
	}
	
	public void dragEnter(DragSourceDragEvent e){
	}
	
	public void dragOver(DropTargetDragEvent e){
	}
	
	public void dropActionChanged(DropTargetDragEvent e){
	}
	
	public void dragExit(DropTargetEvent e){
	}
	
	public void drop(DropTargetDropEvent e){
		try {
			Transferable transferable = e.getTransferable();
			// we accept only Strings
			if (transferable.isDataFlavorSupported (DataFlavor.stringFlavor)){
				e.acceptDrop(DnDConstants.ACTION_MOVE);
				ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(((String)transferable.getTransferData (DataFlavor.stringFlavor)).getBytes()));
				readObjectOnDrop(is, e.getLocation());                    
				e.getDropTargetContext().dropComplete(true);
			}
			else
				e.rejectDrop();
		} catch (Exception exception) {
			exception.printStackTrace();
			e.rejectDrop();
		}
	}
	
	public void dragGestureRecognized(DragGestureEvent e) {
		try{
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			writeObjectOnDrag(os, e.getDragOrigin());                
			dragSource.startDrag(e, DragSource.DefaultMoveDrop, new StringSelection(new String(bs.toByteArray())), this);
		} catch (Exception ee){
			ee.printStackTrace();
		}
	}
	
	public void dragEnter(DropTargetDragEvent e){
	}
	
	public void readObjectOnDrop(ObjectInputStream is, Point dropLocation) throws Exception{
	}
	
	public void writeObjectOnDrag(ObjectOutputStream os, Point dragLocation) throws Exception{
	}
}
