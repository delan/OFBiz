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

import org.ofbiz.core.util.*;

/**
 * Generic Entity - Relation model class
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 31, 2001
 *@version    1.0
 */

public class ModelRelation {

    /** the title, gives a name/description to the relation */
    public String title = "";
    /** the type: either "one" or "many" */
    public String type = "";
    /** the name of the related EJB/entity */
    public String relEntityName = "";
    /** keyMaps defining how to lookup the relatedTable using columns from this table */
    public Vector keyMaps = new Vector();
    /** the main entity of this relation */
    public ModelEntity mainEntity = null;

    /** Default Constructor */
    public ModelRelation() {
    }
    
    /** XML Constructor */
    public ModelRelation(ModelEntity entity, Element relationElement) {
        this.mainEntity = entity;

        this.type = UtilXml.checkEmpty(relationElement.getAttribute("type"));
        this.title = UtilXml.checkEmpty(relationElement.getAttribute("title"));
        this.relEntityName = UtilXml.checkEmpty(relationElement.getAttribute("rel-entity-name"));

        NodeList keyMapList = relationElement.getElementsByTagName("key-map");
        for (int i = 0; i < keyMapList.getLength(); i++) {
            Element keyMapElement = (Element) keyMapList.item(i);
            if (keyMapElement.getParentNode() == relationElement) {
                ModelKeyMap keyMap = new ModelKeyMap(keyMapElement);
                if (keyMap != null) {
                    this.keyMaps.add(keyMap);
                }
            }
        }
    }

    /** Find a KeyMap with the specified fieldName */
    public ModelKeyMap findKeyMap(String fieldName) {
        for (int i = 0; i < keyMaps.size(); i++) {
            ModelKeyMap keyMap = (ModelKeyMap) keyMaps.elementAt(i);
            if (keyMap.fieldName.equals(fieldName)) return keyMap;
        }
        return null;
    }

    /** Find a KeyMap with the specified relFieldName */
    public ModelKeyMap findKeyMapByRelated(String relFieldName) {
        for (int i = 0; i < keyMaps.size(); i++) {
            ModelKeyMap keyMap = (ModelKeyMap) keyMaps.elementAt(i);
            if (keyMap.relFieldName.equals(relFieldName)) return keyMap;
        }
        return null;
    }

    public String keyMapString(String separator, String afterLast) {
        String returnString = "";
        if (keyMaps.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < keyMaps.size() - 1; i++) {
            returnString = returnString + ((ModelKeyMap) keyMaps.elementAt(i)).fieldName + separator;
        }
        returnString = returnString + ((ModelKeyMap) keyMaps.elementAt(i)).fieldName + afterLast;
        return returnString;
    }

    public String keyMapUpperString(String separator, String afterLast) {
        String returnString = "";
        if (keyMaps.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < keyMaps.size() - 1; i++) {
            returnString = returnString + ModelUtil.upperFirstChar(((ModelKeyMap) keyMaps.elementAt(i)).fieldName) + separator;
        }
        returnString = returnString + ModelUtil.upperFirstChar(((ModelKeyMap) keyMaps.elementAt(i)).fieldName) + afterLast;
        return returnString;
    }

    public String keyMapRelatedUpperString(String separator, String afterLast) {
        String returnString = "";
        if (keyMaps.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < keyMaps.size() - 1; i++) {
            returnString = returnString + ModelUtil.upperFirstChar(((ModelKeyMap) keyMaps.elementAt(i)).relFieldName) + separator;
        }
        returnString = returnString + ModelUtil.upperFirstChar(((ModelKeyMap) keyMaps.elementAt(i)).relFieldName) + afterLast;
        return returnString;
    }
/*
  public String keyMapColumnString(String separator, String afterLast) {
    String returnString = "";
    if(keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < keyMaps.size() - 1; i++) {
      returnString = returnString + ((ModelKeyMap)keyMaps.elementAt(i)).colName + separator;
    }
    returnString = returnString + ((ModelKeyMap)keyMaps.elementAt(i)).colName + afterLast;
    return returnString;
  }
*/
/*
  public String keyMapRelatedColumnString(String separator, String afterLast) {
    String returnString = "";
    if(keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < keyMaps.size() - 1; i++) {
      returnString = returnString + ((ModelKeyMap)keyMaps.elementAt(i)).relColName + separator;
    }
    returnString = returnString + ((ModelKeyMap)keyMaps.elementAt(i)).relColName + afterLast;
    return returnString;
  }
*/
}
