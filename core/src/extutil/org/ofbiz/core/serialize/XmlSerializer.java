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

package org.ofbiz.core.serialize;


import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.lang.ref.WeakReference;

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
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Nov 15, 2001
 *@version    1.0
 */
public class XmlSerializer {
    private static WeakReference simpleDateFormatter;

    public static String serialize(Object object) throws SerializeException, FileNotFoundException, IOException {
        Document document = UtilXml.makeEmptyXmlDocument("ofbiz-ser");
        Element rootElement = document.getDocumentElement();

        rootElement.appendChild(serializeSingle(object, document));
        return UtilXml.writeXmlDocument(document);
    }

    public static Object deserialize(String content, GenericDelegator delegator)
        throws SerializeException, SAXException, ParserConfigurationException, IOException {
        // readXmlDocument with false second parameter to disable validation
        Document document = UtilXml.readXmlDocument(content, false);
        Element rootElement = document.getDocumentElement();
        // find the first element below the root element, that should be the object
        Node curChild = rootElement.getFirstChild();

        while (curChild != null && curChild.getNodeType() != Node.ELEMENT_NODE) {
            curChild = curChild.getNextSibling();
        }
        if (curChild == null) return null;
        Element element = (Element) curChild;

        return deserializeSingle(element, delegator);
    }

    public static Element serializeSingle(Object object, Document document) throws SerializeException {
        if (document == null) return null;

        if (object == null) return document.createElement("null");

        // - Standard Objects -
        if (object instanceof String) {
            return makeElement("std-String", object, document);
        } else if (object instanceof Integer) {
            return makeElement("std-Integer", object, document);
        } else if (object instanceof Long) {
            return makeElement("std-Long", object, document);
        } else if (object instanceof Float) {
            return makeElement("std-Float", object, document);
        } else if (object instanceof Double) {
            return makeElement("std-Double", object, document);
        } else if (object instanceof Boolean) {
            return makeElement("std-Boolean", object, document);
            // - SQL Objects -
        } else if (object instanceof java.sql.Timestamp) {
            return makeElement("sql-Timestamp", object, document);
        } else if (object instanceof java.sql.Date) {
            return makeElement("sql-Date", object, document);
        } else if (object instanceof java.sql.Time) {
            return makeElement("sql-Time", object, document);
        } else if (object instanceof java.util.Date) {
            // NOTE: make sure this is AFTER the java.sql date/time objects since they inherit from java.util.Date
            DateFormat formatter = getDateFormat();
            String stringValue = null;

            synchronized (formatter) {
                stringValue = formatter.format((java.util.Date) object);
            }
            return makeElement("std-Date", stringValue, document);
            // return makeElement("std-Date", object, document);
        } else if (object instanceof Collection) {
            // - Collections -
            String elementName = null;

            // these ARE order sensitive; for instance Stack extends Vector, so if Vector were first we would lose the stack part
            if (object instanceof ArrayList) {
                elementName = "col-ArrayList";
            } else if (object instanceof LinkedList) {
                elementName = "col-LinkedList";
            } else if (object instanceof Stack) {
                elementName = "col-Stack";
            } else if (object instanceof Vector) {
                elementName = "col-Vector";
            } else if (object instanceof TreeSet) {
                elementName = "col-TreeSet";
            } else if (object instanceof HashSet) {
                elementName = "col-HashSet";
            } else {
                // no specific type found, do general Collection, will deserialize as LinkedList
                elementName = "col-Collection";
            }

            // if (elementName == null) return serializeCustom(object, document);

            Collection value = (Collection) object;
            Element element = document.createElement(elementName);
            Iterator iter = value.iterator();

            while (iter.hasNext()) {
                element.appendChild(serializeSingle(iter.next(), document));
            }
            return element;
        } else if (object instanceof GenericPK) {
            // Do GenericEntity objects as a special case, use std XML import/export routines
            GenericPK value = (GenericPK) object;

            return value.makeXmlElement(document, "eepk-");
        } else if (object instanceof GenericValue) {
            GenericValue value = (GenericValue) object;

            return value.makeXmlElement(document, "eeval-");
        } else if (object instanceof Map) {
            // - Maps -
            String elementName = null;

            // these ARE order sensitive; for instance Properties extends Hashtable, so if Hashtable were first we would lose the Properties part
            if (object instanceof HashMap) {
                elementName = "map-HashMap";
            } else if (object instanceof Properties) {
                elementName = "map-Properties";
            } else if (object instanceof Hashtable) {
                elementName = "map-Hashtable";
            } else if (object instanceof WeakHashMap) {
                elementName = "map-WeakHashMap";
            } else if (object instanceof TreeMap) {
                elementName = "map-TreeMap";
            } else {
                // serialize as a simple Map implementation if nothing else applies, these will deserialize as a HashMap
                elementName = "map-Map";
            }

            Element element = document.createElement(elementName);
            Map value = (Map) object;
            Iterator iter = value.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

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

        return serializeCustom(object, document);
    }

    public static Element serializeCustom(Object object, Document document) throws SerializeException {
        // TODO: if nothing else, try looking up a class for the type in the properties file, the class should implement an interface or have a certain static method on it
        throw new SerializeException("Cannot serialize object of class " + object.getClass().getName());
    }

    public static Element makeElement(String elementName, Object value, Document document) {
        if (value == null) return document.createElement("null");
        Element element = document.createElement(elementName);

        element.setAttribute("value", value.toString());
        return element;
    }

    public static Object deserializeSingle(Element element, GenericDelegator delegator) throws SerializeException {
        String tagName = element.getTagName();

        if (tagName.equals("null")) return null;

        if (tagName.startsWith("std-")) {
            // - Standard Objects -
            if ("std-String".equals(tagName)) {
                return element.getAttribute("value");
            } else if ("std-Integer".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return Integer.valueOf(valStr);
            } else if ("std-Long".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return Long.valueOf(valStr);
            } else if ("std-Float".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return Float.valueOf(valStr);
            } else if ("std-Double".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return Double.valueOf(valStr);
            } else if ("std-Boolean".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return Boolean.valueOf(valStr);
            } else if ("std-Date".equals(tagName)) {
                String valStr = element.getAttribute("value");
                DateFormat formatter = getDateFormat();
                java.util.Date value = null;

                try {
                    synchronized (formatter) {
                        value = formatter.parse(valStr);
                    }
                } catch (ParseException e) {
                    throw new SerializeException("Could not parse date String: " + valStr, e);
                }
                return value;
            }
        } else if (tagName.startsWith("sql-")) {
            // - SQL Objects -
            if ("sql-Timestamp".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return java.sql.Timestamp.valueOf(valStr);
            } else if ("sql-Date".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return java.sql.Date.valueOf(valStr);
            } else if ("sql-Time".equals(tagName)) {
                String valStr = element.getAttribute("value");

                return java.sql.Time.valueOf(valStr);
            }
        } else if (tagName.startsWith("col-")) {
            // - Collections -
            Collection value = null;

            if ("col-ArrayList".equals(tagName)) {
                value = new ArrayList();
            } else if ("col-LinkedList".equals(tagName)) {
                value = new LinkedList();
            } else if ("col-Stack".equals(tagName)) {
                value = new Stack();
            } else if ("col-Vector".equals(tagName)) {
                value = new Vector();
            } else if ("col-TreeSet".equals(tagName)) {
                value = new TreeSet();
            } else if ("col-HashSet".equals(tagName)) {
                value = new HashSet();
            } else if ("col-Collection".equals(tagName)) {
                value = new LinkedList();
            }

            if (value == null) {
                return deserializeCustom(element);
            } else {
                Node curChild = element.getFirstChild();

                while (curChild != null) {
                    if (curChild.getNodeType() == Node.ELEMENT_NODE) {
                        value.add(deserializeSingle((Element) curChild, delegator));
                    }
                    curChild = curChild.getNextSibling();
                }
                return value;
            }
        } else if (tagName.startsWith("map-")) {
            // - Maps -
            Map value = null;

            if ("map-HashMap".equals(tagName)) {
                value = new HashMap();
            } else if ("map-Properties".equals(tagName)) {
                value = new Properties();
            } else if ("map-Hashtable".equals(tagName)) {
                value = new Hashtable();
            } else if ("map-WeakHashMap".equals(tagName)) {
                value = new WeakHashMap();
            } else if ("map-TreeMap".equals(tagName)) {
                value = new TreeMap();
            } else if ("map-Map".equals(tagName)) {
                value = new HashMap();
            }

            if (value == null) {
                return deserializeCustom(element);
            } else {
                Node curChild = element.getFirstChild();

                while (curChild != null) {
                    if (curChild.getNodeType() == Node.ELEMENT_NODE) {
                        Element curElement = (Element) curChild;

                        if ("map-Entry".equals(curElement.getTagName())) {
                            Element mapKeyElement = UtilXml.firstChildElement(curElement, "map-Key");
                            Element keyElement = null;
                            Node tempNode = mapKeyElement.getFirstChild();

                            while (tempNode != null) {
                                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                                    keyElement = (Element) tempNode;
                                    break;
                                }
                                tempNode = tempNode.getNextSibling();
                            }
                            if (keyElement == null) throw new SerializeException("Could not find an element under the map-Key");

                            Element mapValueElement = UtilXml.firstChildElement(curElement, "map-Value");
                            Element valueElement = null;

                            tempNode = mapValueElement.getFirstChild();
                            while (tempNode != null) {
                                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                                    valueElement = (Element) tempNode;
                                    break;
                                }
                                tempNode = tempNode.getNextSibling();
                            }
                            if (valueElement == null) throw new SerializeException("Could not find an element under the map-Value");

                            value.put(deserializeSingle(keyElement, delegator), deserializeSingle(valueElement, delegator));
                        }
                    }
                    curChild = curChild.getNextSibling();
                }
                return value;
            }
        } else if (tagName.startsWith("eepk-")) {
            return delegator.makePK(element);
        } else if (tagName.startsWith("eeval-")) {
            return delegator.makeValue(element);
        }

        return deserializeCustom(element);
    }

    public static Object deserializeCustom(Element element) throws SerializeException {
        throw new SerializeException("Cannot deserialize element named " + element.getTagName());
    }

    /**
     * Returns the DateFormat used to serialize and deserialize <code>java.util.Date</code> objects.
     * This format is NOT used to format any of the java.sql subtypes of java.util.Date.
     * A <code>WeakReference</code> is used to maintain a reference to the DateFormat object
     * so that it can be created and garbage collected as needed.
     *
     * @return the DateFormat used to serialize and deserialize <code>java.util.Date</code> objects.
     */
    private static DateFormat getDateFormat() {
        DateFormat formatter = null;

        if (simpleDateFormatter != null) {
            formatter = (DateFormat) simpleDateFormatter.get();
        }
        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            simpleDateFormatter = new WeakReference(formatter);
        }
        return formatter;
    }
}
