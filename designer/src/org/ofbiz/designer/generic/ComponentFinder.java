
/**
 *	ComponentFinder.java
 * 
 *	This is a utility class which finds all names components within a java AWT Container.
 *	It returns these named components as a Hashtable, keyed by the component name.
 */


package org.ofbiz.designer.generic;

import java.awt.*;
import java.util.*;

public class ComponentFinder{
	public static Hashtable getAllNamedComponent(Container container){
		Component[] components = container.getComponents();
		Vector compVec = new Vector();
		for (int i=0; i<components.length; i++)
			compVec.addElement(components[i]);

		Hashtable returnTable = new Hashtable();
		while (true){
			if (compVec.size() == 0) return returnTable;
			Component comp = (Component)compVec.elementAt(0);
			if (comp.getName() != null)
				returnTable.put(comp.getName(), comp);
			if (comp instanceof Container){
				Component[] tempComponents = ((Container)comp).getComponents();
				for (int i=0; i<tempComponents.length; i++)
					compVec.addElement(tempComponents[i]);
			}
			compVec.removeElementAt(0);
		}
	}
}
