/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity.model;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ofbiz.core.util.*;

/**
 * Generic Entity - KeyMap model class
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 31, 2001
 *@version    1.0
 */
public class ModelKeyMap {

    /** name of the field in this entity */
    protected String fieldName = "";
    /** name of the field in related entity */
    protected String relFieldName = "";

    /** Default Constructor */
    public ModelKeyMap() {
    }

    /** XML Constructor */
    public ModelKeyMap(Element keyMapElement) {
        this.fieldName = UtilXml.checkEmpty(keyMapElement.getAttribute("field-name"));
        //if no relFieldName is specified, use the fieldName; this is convenient for when they are named the same, which is often the case
        this.relFieldName = UtilXml.checkEmpty(keyMapElement.getAttribute("rel-field-name"), this.fieldName);
    }

    /** name of the field in this entity */
    public String getFieldName() { return this.fieldName; }
    /** name of the field in related entity */
    public String getRelFieldName() { return this.relFieldName; }
}
