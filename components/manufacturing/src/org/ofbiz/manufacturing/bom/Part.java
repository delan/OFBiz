/*
 * Part.java
 *
 * Created on 19 settembre 2003, 12.50
 */

package org.ofbiz.manufacturing.bom;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author  jacopo
 */
public class Part {

    private String partId;
    private ArrayList components;
    
    /** Creates a new instance of Part */
    public Part() {
        partId = "";
        components = new ArrayList();
    }

    public Part(String partId) {
        this();
        this.partId = partId;
    }

    /** Getter for property components.
     * @return Value of property components.
     *
     */
    public Iterator getComponents() {
        return components.iterator();
    }
    
    /** Setter for property components.
     * @param components New value of property components.
     *
     */
    public void addComponent(Part component) {
        components.add(component);
    }
    
}
