/*
 * $Id$
 * $Log$
 *
 */

package org.ofbiz.core.serialize;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * <p><b>Title:</b> XmlSerializer
 * <p><b>Description:</b> Simple XML serialization/deserialization routines with embedded type information
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Nov 15, 2001
 *@version    1.0
 */
public class XmlSerializer {
  public static String serialize(Object object) throws SerializeException, FileNotFoundException, IOException {
    Document document = UtilXml.makeEmptyXmlDocument("ofbiz-ser");
    Element rootElement = document.getDocumentElement();
    rootElement.appendChild(serializeSingle(object, document));
    return UtilXml.writeXmlDocument(document);
  }
  
  public static Object deserialize(String content, GenericDelegator delegator) throws SerializeException, SAXException, ParserConfigurationException, IOException {
    Document document = UtilXml.readXmlDocument(content);
    Element rootElement = document.getDocumentElement();
    //find the first element below the root element, that should be the object
    Node curChild = rootElement.getFirstChild();
    while(curChild != null && curChild.getNodeType() != Node.ELEMENT_NODE) {
      curChild = curChild.getNextSibling();
    }
    if(curChild == null) return null;
    Element element = (Element)curChild;
    return deserializeSingle(element, delegator);
  }
  
  public static Element serializeSingle(Object object, Document document) throws SerializeException {
    if(object == null) return null;
    if(document == null) return null;
    
    Element element = null;
    
    if(object instanceof String) {
      String value = (String)object;
      element = document.createElement("std-String");
      element.setAttribute("value", value);
    }
    else if(object instanceof Integer) {
      Integer value = (Integer)object;
      element = document.createElement("std-Integer");
      element.setAttribute("value", value.toString());
    }
    else if(object instanceof Long) {
      Long value = (Long)object;
      element = document.createElement("std-Long");
      element.setAttribute("value", value.toString());
    }
    else if(object instanceof Float) {
      Float value = (Float)object;
      element = document.createElement("std-Float");
      element.setAttribute("value", value.toString());
    }
    else if(object instanceof Double) {
      Double value = (Double)object;
      element = document.createElement("std-Double");
      element.setAttribute("value", value.toString());
    }
    else if(object instanceof java.sql.Timestamp) {
      java.sql.Timestamp value = (java.sql.Timestamp)object;
      element = document.createElement("std-Timestamp");
      element.setAttribute("value", value.toString());
    }
    else if(object instanceof java.sql.Date) {
      java.sql.Date value = (java.sql.Date)object;
      element = document.createElement("std-Date");
      element.setAttribute("value", value.toString());
    }
    else if(object instanceof java.sql.Time) {
      java.sql.Time value = (java.sql.Time)object;
      element = document.createElement("std-Time");
      element.setAttribute("value", value.toString());
    }
    //for collections go try from most restricted to least to keep as much data as possible
    else if(object instanceof SortedSet) {
      SortedSet value = (SortedSet)object;
      element = document.createElement("col-SortedSet");
      Iterator iter = value.iterator();
      while(iter.hasNext()) {
        element.appendChild(serializeSingle(iter.next(), document));
      }
    }
    else if(object instanceof Set) {
      Set value = (Set)object;
      element = document.createElement("col-Set");
      Iterator iter = value.iterator();
      while(iter.hasNext()) {
        element.appendChild(serializeSingle(iter.next(), document));
      }
    }
    else if(object instanceof List) {
      List value = (List)object;
      element = document.createElement("col-List");
      Iterator iter = value.iterator();
      while(iter.hasNext()) {
        element.appendChild(serializeSingle(iter.next(), document));
      }
    }
    else if(object instanceof Collection) {
      Collection value = (Collection)object;
      element = document.createElement("col-Collection");
      Iterator iter = value.iterator();
      while(iter.hasNext()) {
        element.appendChild(serializeSingle(iter.next(), document));
      }
    }
    //Handle Maps
    else if(object instanceof Map) {
      Map value = (Map)object;
      element = document.createElement("col-Map");
      Iterator iter = value.entrySet().iterator();
      while(iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        Element key = document.createElement("col-Key");
        element.appendChild(key);
        key.appendChild(serializeSingle(entry.getKey(), document));
        Element mapValue = document.createElement("col-Value");
        element.appendChild(mapValue);
        mapValue.appendChild(serializeSingle(entry.getValue(), document));
      }
    }
    //Do GenericValue objects as a special case, use std XML import/export routines
    else if(object instanceof GenericEntity) {
      GenericEntity value = (GenericEntity)object;
      element = value.makeXmlElement(document);
    }
    else {
      //TODO: if nothing else, try looking up a class for the type in the properties file, the class should implement an interface or have a certain static method on it
      throw new SerializeException("Cannot serialize object of class " + object.getClass().getName());
    }
    return element;
  }

  public static Object deserializeSingle(Element element, GenericDelegator delegator) throws SerializeException {
    Object object = null;
    
    if("std-String".equals(element.getTagName())) {
    }
    else {
      throw new SerializeException("Cannot deserialize element named ");
    }
    
    return object;
  }
}
