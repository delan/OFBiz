/*
 * $Id$
 * $Log$
 * Revision 1.3  2001/11/17 21:31:13  jonesde
 * Fixed a problem with deserializing maps - wasn't getting the child element of the key and value, was just trying to deserialize the key and value marker elements
 *
 * Revision 1.2  2001/11/17 05:52:42  jonesde
 * First pretty complete pass of the serialization/deser methods, cleaned up a bit too
 *
 * Revision 1.1  2001/11/16 15:54:56  jonesde
 * Initial checkin of XML serialization stuff
 *
 *
 */

package org.ofbiz.core.serialize;

import java.io.*;
import java.net.*;
import java.text.*;
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
    //readXmlDocument with false second parameter to disable validation
    Document document = UtilXml.readXmlDocument(content, false);
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
    if(document == null) return null;
    
    if(object == null) return document.createElement("null");
    
    // - Standard Objects -
    if(object instanceof String) return makeElement("std-String", object, document);
    else if(object instanceof Integer) return makeElement("std-Integer", object, document);
    else if(object instanceof Long) return makeElement("std-Long", object, document);
    else if(object instanceof Float) return makeElement("std-Float", object, document);
    else if(object instanceof Double) return makeElement("std-Double", object, document);
    else if(object instanceof Boolean) return makeElement("std-Boolean", object, document);
    else if(object instanceof java.util.Date) return makeElement("std-Date", object, document);
    // - SQL Objects -
    else if(object instanceof java.sql.Timestamp) return makeElement("sql-Timestamp", object, document);
    else if(object instanceof java.sql.Date) return makeElement("sql-Date", object, document);
    else if(object instanceof java.sql.Time) return makeElement("sql-Time", object, document);
    // - Collections -
    else if(object instanceof Collection) {
      String elementName = null;
      //these ARE order sensitive; for instance Stack extends Vector, so if Vector were first we would lose the stack part
      if(object instanceof ArrayList) elementName = "col-ArrayList";
      else if(object instanceof LinkedList) elementName = "col-LinkedList";
      else if(object instanceof Stack) elementName = "col-Stack";
      else if(object instanceof Vector) elementName = "col-Vector";
      else if(object instanceof TreeSet) elementName = "col-TreeSet";
      else if(object instanceof HashSet) elementName = "col-HashSet";
      if(elementName == null) return serializeCustom(object, document);
      
      Collection value = (Collection)object;
      Element element = document.createElement(elementName);
      Iterator iter = value.iterator();
      while(iter.hasNext()) {
        element.appendChild(serializeSingle(iter.next(), document));
      }
      return element;
    }
    // - Maps -
    else if(object instanceof Map) {
      String elementName = null;
      //these ARE order sensitive; for instance Properties extends Hashtable, so if Hashtable were first we would lose the Properties part
      if(object instanceof HashMap) elementName = "map-HashMap";
      else if(object instanceof Properties) elementName = "map-Properties";
      else if(object instanceof Hashtable) elementName = "map-Hashtable";
      else if(object instanceof WeakHashMap) elementName = "map-WeakHashMap";
      else if(object instanceof TreeMap) elementName = "map-TreeMap";
      if(elementName == null) return serializeCustom(object, document);

      Element element = document.createElement(elementName);
      Map value = (Map)object;
      Iterator iter = value.entrySet().iterator();
      while(iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        
        Element entryElement = document.createElement("map-Entry");
        element.appendChild(entryElement);
        
        Element key = document.createElement("map-Key");
        entryElement.appendChild(key);
        key.appendChild(serializeSingle(entry.getKey(), document));
        Element mapValue = document.createElement("map-Value");
        entryElement.appendChild(mapValue);
        mapValue.appendChild(serializeSingle(entry.getValue(), document));
      }
      return element;
    }
    //Do GenericEntity objects as a special case, use std XML import/export routines
    else if(object instanceof GenericPK) {
      GenericPK value = (GenericPK)object;
      return value.makeXmlElement(document, "eepk-");
    }
    else if(object instanceof GenericValue) {
      GenericValue value = (GenericValue)object;
      return value.makeXmlElement(document, "eeval-");
    }

    return serializeCustom(object, document);
  }

  public static Element serializeCustom(Object object, Document document) throws SerializeException {
    //TODO: if nothing else, try looking up a class for the type in the properties file, the class should implement an interface or have a certain static method on it
    throw new SerializeException("Cannot serialize object of class " + object.getClass().getName());
  }
  
  public static Element makeElement(String elementName, Object value, Document document) {
    if(value == null) return document.createElement("null");
    Element element = document.createElement(elementName);
    element.setAttribute("value", value.toString());
    return element;
  }
  
  public static Object deserializeSingle(Element element, GenericDelegator delegator) throws SerializeException {
    String tagName = element.getTagName();

    if(tagName.equals("null")) return null;

    // - Standard Objects -
    if(tagName.startsWith("std-")) {
      if("std-String".equals(tagName)) {
        return element.getAttribute("value");
      }
      else if("std-Integer".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return Integer.valueOf(valStr);
      }
      else if("std-Long".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return Long.valueOf(valStr);
      }
      else if("std-Float".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return Float.valueOf(valStr);
      }
      else if("std-Double".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return Double.valueOf(valStr);
      }
      else if("std-Boolean".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return Boolean.valueOf(valStr);
      }
      else if("std-Date".equals(tagName)) {
        String valStr = element.getAttribute("value");
        DateFormat formatter = DateFormat.getDateTimeInstance();
        java.util.Date value = null;
        try { value = formatter.parse(valStr); }
        catch(ParseException e) { throw new SerializeException("Could not parse date String: " + valStr, e); }
        return value;
      }
    }
    // - SQL Objects -
    else if(tagName.startsWith("sql-")) {
      if("sql-Timestamp".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return java.sql.Timestamp.valueOf(valStr);
      }
      else if("sql-Date".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return java.sql.Date.valueOf(valStr);
      }
      else if("sql-Time".equals(tagName)) {
        String valStr = element.getAttribute("value");
        return java.sql.Time.valueOf(valStr);
      }
    }
    // - Collections -
    else if(tagName.startsWith("col-")) {
      Collection value = null;
      if("col-ArrayList".equals(tagName)) value = new ArrayList();
      else if("col-LinkedList".equals(tagName)) value = new LinkedList();
      else if("col-Stack".equals(tagName)) value = new Stack();
      else if("col-Vector".equals(tagName)) value = new Vector();
      else if("col-TreeSet".equals(tagName)) value = new TreeSet();
      else if("col-HashSet".equals(tagName)) value = new HashSet();
      
      if(value == null) {
        return deserializeCustom(element);
      }
      else {
        Node curChild = element.getFirstChild();
        while(curChild != null) {
          if(curChild.getNodeType() == Node.ELEMENT_NODE) {
            value.add(deserializeSingle((Element)curChild, delegator));
          }
          curChild = curChild.getNextSibling();
        }
        return value;
      }
    }
    // - Maps -
    else if(tagName.startsWith("map-")) {
      Map value = null;
      if("map-HashMap".equals(tagName)) value = new HashMap();
      else if("map-Properties".equals(tagName)) value = new Properties();
      else if("map-Hashtable".equals(tagName)) value = new Hashtable();
      else if("map-WeakHashMap".equals(tagName)) value = new WeakHashMap();
      else if("map-TreeMap".equals(tagName)) value = new TreeMap();
      
      if(value == null) {
        return deserializeCustom(element);
      }
      else {
        Node curChild = element.getFirstChild();
        while(curChild != null) {
          if(curChild.getNodeType() == Node.ELEMENT_NODE) {
            Element curElement = (Element)curChild;
            if("map-Entry".equals(curElement.getTagName())) {
              NodeList tempList = curElement.getElementsByTagName("map-Key");
              if(tempList.getLength() != 1) throw new SerializeException("There were " + tempList.getLength() + " map-Key elements, expected 1");
              Element mapKeyElement = (Element)tempList.item(0);
              Element keyElement = null;
              Node tempNode = mapKeyElement.getFirstChild();
              while(tempNode != null) {
                if(tempNode.getNodeType() == Node.ELEMENT_NODE) {
                  keyElement = (Element)tempNode;
                  break;
                }
                tempNode = tempNode.getNextSibling();
              }
              if(keyElement == null) throw new SerializeException("Could not find an element under the map-Key");
              
              tempList = curElement.getElementsByTagName("map-Value");
              if(tempList.getLength() != 1) throw new SerializeException("There were " + tempList.getLength() + " map-Value elements, expected 1");
              Element mapValueElement = (Element)tempList.item(0);
              Element valueElement = null;
              tempNode = mapValueElement.getFirstChild();
              while(tempNode != null) {
                if(tempNode.getNodeType() == Node.ELEMENT_NODE) {
                  valueElement = (Element)tempNode;
                  break;
                }
                tempNode = tempNode.getNextSibling();
              }
              if(valueElement == null) throw new SerializeException("Could not find an element under the map-Value");
              
              value.put(deserializeSingle(keyElement, delegator), deserializeSingle(valueElement, delegator));
            }
          }
          curChild = curChild.getNextSibling();
        }
        return value;
      }
    }
    else if(tagName.startsWith("eepk-")) {
      return delegator.makePK(element);
    }
    else if(tagName.startsWith("eeval-")) {
      return delegator.makeValue(element);
    }

    return deserializeCustom(element);
  }

  public static Object deserializeCustom(Element element) throws SerializeException {
    throw new SerializeException("Cannot deserialize element named " + element.getTagName());
  }
}
