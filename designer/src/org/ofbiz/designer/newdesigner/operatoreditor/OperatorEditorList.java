package org.ofbiz.designer.newdesigner.operatoreditor;

import javax.swing.*;
import java.awt.dnd.*;
import java.util.*;
import java.awt.datatransfer.*;
import org.ofbiz.designer.util.*;
import java.awt.*;
import java.io.*;

class OperatorEditorList extends JList {
    String  listType;

    public OperatorEditorList(Vector vec, String listTypeIn) {
        super(vec);
        if(!listTypeIn.equals(OperatorEditor.OPERATOR) && !listTypeIn.equals(OperatorEditor.TASK))
            throw new RuntimeException("Inappropriate list type " + listTypeIn);
        listType = listTypeIn;
        setBackground(Color.lightGray);

        //DND stuff
        OperatorEditorListDragAdapter myDragAdapter = new OperatorEditorListDragAdapter(this);
    }
    
    public OperatorEditorList(String listTypeIn) {
        if(!listTypeIn.equals(OperatorEditor.OPERATOR) && !listTypeIn.equals(OperatorEditor.TASK))
            throw new RuntimeException("Inappropriate list type " + listTypeIn);
        listType = listTypeIn;
        setBackground(Color.lightGray);

        //DND stuff
        OperatorEditorListDragAdapter myDragAdapter = new OperatorEditorListDragAdapter(this);
    }
}

class OperatorEditorListDragAdapter extends DragAdapter {
    OperatorEditorListDragAdapter(OperatorEditorList _comp) {
        super(_comp);
    }

    public void writeObjectOnDrag(ObjectOutputStream os, Point dragLocation) throws Exception{
        OperatorEditorList myComp = (OperatorEditorList)comp;

        os.writeObject(myComp.listType);
        os.writeObject((String)myComp.getSelectedValue());
    }
}

