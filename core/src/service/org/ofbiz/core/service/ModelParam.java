/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.core.service;

/**
 * Generic Service Model Parameter
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ModelParam {

    /** Parameter name */
    public String name;

    /** Paramater type */
    public String type;

    /** Parameter mode (IN/OUT/INOUT) */
    public String mode;
    
    /** The form label */
    public String formLabel;
    
    /** The entity name */
    public String entityName;
    
    /** The entity field name */
    public String fieldName;

    /** Is this Parameter required or optional? Default to false, or required. */
    public boolean optional = false;
    
    /** Is this parameter to be displayed via the form tool? */
    public boolean formDisplay = true;
    
    /** Is this Parameter set internally? */
    public boolean internal = false;
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(name + "::");
        buf.append(type + "::");
        buf.append(mode + "::");
        buf.append(formLabel + "::");
        buf.append(entityName + "::");
        buf.append(fieldName + "::");
        buf.append(optional + "::");
        buf.append(formDisplay + "::");
        buf.append(internal);
        return buf.toString();
    }
    
    public boolean equals(ModelParam model) {
        if (model.name.equals(this.name))
            return true;
        return false;
    }
}

