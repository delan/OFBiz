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

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * Generic Entity - Field model class
 *
 *@author     David E. Jones
 *@created    May 15, 2001
 *@version    1.0
 */

public class ModelField {

    /** The name of the Field */
    protected String name = "";
    /** The type of the Field */
    protected String type = "";
    /** The col-name of the Field */
    protected String colName = "";
    /** boolean which specifies whether or not the Field is a Primary Key */
    protected boolean isPk = false;
    /** validators to be called when an update is done */
    protected Vector validators = new Vector();

    /** Default Constructor */
    public ModelField() {
    }

    /** XML Constructor */
    public ModelField(Element fieldElement) {
        this.type = UtilXml.checkEmpty(fieldElement.getAttribute("type"));
        this.name = UtilXml.checkEmpty(fieldElement.getAttribute("name"));
        this.colName = UtilXml.checkEmpty(fieldElement.getAttribute("col-name"), ModelUtil.javaNameToDbName(UtilXml.checkEmpty(this.name)));
        this.isPk = false; //is set elsewhere

        NodeList validateList = fieldElement.getElementsByTagName("validate");
        for (int i = 0; i < validateList.getLength(); i++) {
            Element element = (Element) validateList.item(i);
            this.validators.add(UtilXml.checkEmpty(element.getAttribute("name")));
        }
    }
    
    /** DB Names Constructor */
    public ModelField(GenericDAO.ColumnCheckInfo ccInfo, ModelFieldTypeReader modelFieldTypeReader) {
        this.colName = ccInfo.columnName.toUpperCase();
        this.name = ModelUtil.dbNameToVarName(this.colName);

        //figure out the type according to the typeName, columnSize and decimalDigits
        this.type = ModelUtil.induceFieldType(ccInfo.typeName, ccInfo.columnSize, ccInfo.decimalDigits, modelFieldTypeReader);

        //how do we find out if it is a primary key? for now, if not nullable, assume it is a pk
        //this is a bad assumption, but since this output must be edited by hand later anyway, oh well
        if ("NO".equals(ccInfo.isNullable))
            this.isPk = true;
        else
            this.isPk = false;
    }
    
    /** The name of the Field */
    public String getName() { return this.name; }
    /** The type of the Field */
    public String getType() { return this.type; }
    /** The col-name of the Field */
    public String getColName() { return this.colName; }
    /** boolean which specifies whether or not the Field is a Primary Key */
    public boolean getIsPk() { return this.isPk; }
    /** validators to be called when an update is done */
    public Vector getValidators() { return this.validators; }
}
