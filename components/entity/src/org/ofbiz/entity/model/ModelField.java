/*
 * $Id: ModelField.java,v 1.6 2004/07/07 05:20:06 doogie Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entity.model;

import java.util.*;
import org.w3c.dom.*;

import org.ofbiz.entity.jdbc.*;
import org.ofbiz.base.util.*;

/**
 * Generic Entity - Field model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class ModelField extends ModelChild {

    /** The name of the Field */
    protected String name = "";

    /** The type of the Field */
    protected String type = "";

    /** The col-name of the Field */
    protected String colName = "";

    /** boolean which specifies whether or not the Field is a Primary Key */
    protected boolean isPk = false;

    protected boolean isAutoCreatedInternal = false;
    
    /** validators to be called when an update is done */
    protected List validators = new ArrayList();

    /** Default Constructor */
    public ModelField() {}

    /** Fields Constructor */
    public ModelField(String name, String type, String colName, boolean isPk) {
        this.name = name;
        this.type = type;
        this.setColName(colName);
        this.isPk = isPk;
    }

    /** XML Constructor */
    public ModelField(Element fieldElement) {
        this.type = UtilXml.checkEmpty(fieldElement.getAttribute("type"));
        this.name = UtilXml.checkEmpty(fieldElement.getAttribute("name"));
        this.setColName(UtilXml.checkEmpty(fieldElement.getAttribute("col-name")));
        this.isPk = false; // is set elsewhere

        NodeList validateList = fieldElement.getElementsByTagName("validate");

        for (int i = 0; i < validateList.getLength(); i++) {
            Element element = (Element) validateList.item(i);

            this.validators.add(UtilXml.checkEmpty(element.getAttribute("name")));
        }
    }

    /** DB Names Constructor */
    public ModelField(DatabaseUtil.ColumnCheckInfo ccInfo, ModelFieldTypeReader modelFieldTypeReader) {
        this.colName = ccInfo.columnName;
        this.name = ModelUtil.dbNameToVarName(this.colName);

        // figure out the type according to the typeName, columnSize and decimalDigits
        this.type = ModelUtil.induceFieldType(ccInfo.typeName, ccInfo.columnSize, ccInfo.decimalDigits, modelFieldTypeReader);

        // how do we find out if it is a primary key? for now, if not nullable, assume it is a pk
        // this is a bad assumption, but since this output must be edited by hand later anyway, oh well
        if ("NO".equals(ccInfo.isNullable)) {
            this.isPk = true;
        } else {
            this.isPk = false;
        }
    }

    /** The name of the Field */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** The type of the Field */
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /** The col-name of the Field */
    public String getColName() {
        return this.colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
        if (this.colName == null || this.colName.length() == 0) {
            this.colName = ModelUtil.javaNameToDbName(UtilXml.checkEmpty(this.name));
        }
    }

    /** boolean which specifies whether or not the Field is a Primary Key */
    public boolean getIsPk() {
        return this.isPk;
    }

    public void setIsPk(boolean isPk) {
        this.isPk = isPk;
    }

    public boolean getIsAutoCreatedInternal() {
        return this.isAutoCreatedInternal;
    }

    public void setIsAutoCreatedInternal(boolean isAutoCreatedInternal) {
        this.isAutoCreatedInternal = isAutoCreatedInternal;
    }
    
    /** validators to be called when an update is done */
    public String getValidator(int index) {
        return (String) this.validators.get(index);
    }

    public int getValidatorsSize() {
        return this.validators.size();
    }

    public void addValidator(String validator) {
        this.validators.add(validator);
    }

    public String removeValidator(int index) {
        return (String) this.validators.remove(index);
    }

    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) return false;
        ModelField other = (ModelField) obj;
        return other.getName().equals(getName()) && other.getModelEntity() == getModelEntity();
    }

    public int hashCode() {
        return getModelEntity().hashCode() ^ getName().hashCode();
    }

    public String toString() {
        return getModelEntity() + "@" + getName();
    }
}
