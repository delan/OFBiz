
/**
 *	DocSaver.java
 * 
 *	This is a utility class to ensure automatic saving of xml documents on window-close events.  The
 *	data from the xml comprises the model for the gui represented inside the frame, and when changes are
 *	made to the gui components and the frame is subsequently closed, DocSaver ensures that the changes 
 *	are written back to the xml document automatically.
 * 
 */


package org.ofbiz.designer.generic;

import java.awt.event.*;
import java.awt.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;
import java.util.*;

public class DocSaver extends WindowAdapter {
    XmlWrapper xml;
    private static Hashtable docSavers = new Hashtable();
    private static Hashtable xmlFrames = new Hashtable();

    public DocSaver(XmlWrapper xmlIn) {
        xml = xmlIn;
    }

    public static void add(XmlWrapper xml, WFFrame frame) {
        DocSaver ds = new DocSaver(xml);
        frame.addWindowListener(ds);
        docSavers.put(frame, ds);

        if(xmlFrames.get(xml) == null)
            xmlFrames.put(xml, new HashSet());

        ((HashSet)xmlFrames.get(xml)).add(frame);
    }

    public static void remove(WFFrame frame) {
        DocSaver ds = (DocSaver)docSavers.get(frame);
        frame.removeWindowListener(ds);
        docSavers.remove(frame);

        ((HashSet)xmlFrames.get(ds.xml)).remove(frame);
    }

    public void windowClosing(WindowEvent e) {
        WFFrame frame = (WFFrame)e.getSource();
        ((HashSet)xmlFrames.get(xml)).remove(frame);
        try {
            if(((HashSet)xmlFrames.get(xml)).size() == 0) {
                Rectangle rect = frame.getBounds();
                Point topRight = new Point(rect.x+rect.width, rect.y);
                String save = SaveDialog.getResult(topRight, "Save", "Discard");
                if(save != null && save.equals("Save"))
                    xml.saveDocument();
            }
            docSavers.remove(e.getSource());
        } catch(Exception ee) {
            ee.printStackTrace();
        }
    }
}

