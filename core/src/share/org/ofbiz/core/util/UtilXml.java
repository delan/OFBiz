/*
 * $Id$
 * $Log$
 * Revision 1.7  2001/12/18 14:47:40  jonesde
 * Added a couple of useful methods for creating XML
 *
 * Revision 1.6  2001/12/09 09:29:49  jonesde
 * Small changes to make it easy to turn off validation, validation now off for deserialization since it is not so easy to maintain there
 *
 * Revision 1.5  2001/11/24 06:32:17  jonesde
 * Added a few more useful routines
 *
 * Revision 1.4  2001/11/24 05:09:11  jonesde
 * Added some more useful utility methods, part of refactoring
 *
 * Revision 1.3  2001/11/21 07:28:59  jonesde
 * Improved error handling in UtilXml
 *
 * Revision 1.2  2001/11/16 14:10:50  jonesde
 * small refactoring, moved root element name set to UtilXml
 *
 * Revision 1.1  2001/11/15 14:55:29  jonesde
 * Refactored XML in/out and added stream and string methods
 *
 *
 */

package org.ofbiz.core.util;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

//needed for XML writing with Crimson
//import org.apache.crimson.tree.*;
//needed for XML writing with Xerces
import org.apache.xml.serialize.*;

/**
 * <p><b>Title:</b> UtilXml
 * <p><b>Description:</b>
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
public class UtilXml {
    public static String writeXmlDocument(Document document) throws java.io.IOException {
        if(document == null) {
            Debug.logWarning("[UtilXml.writeXmlDocument] Document was null, doing nothing");
            return null;
        }
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeXmlDocument(bos, document);
        String outString = bos.toString();
        if(bos != null) bos.close();
        return outString;
    }
    
    public static void writeXmlDocument(String filename, Document document) throws java.io.FileNotFoundException, java.io.IOException {
        if(document == null) {
            Debug.logWarning("[UtilXml.writeXmlDocument] Document was null, doing nothing");
            return;
        }
        if(filename == null) {
            Debug.logWarning("[UtilXml.writeXmlDocument] Filename was null, doing nothing");
            return;
        }
        
        File outFile = new File(filename);
        FileOutputStream fos = null;
        fos = new FileOutputStream(outFile);
        
        try { writeXmlDocument(fos, document); }
        finally { if(fos != null) fos.close(); }
    }
    
    public static void writeXmlDocument(OutputStream os, Document document) throws java.io.IOException {
        if(document == null) {
            Debug.logWarning("[UtilXml.writeXmlDocument] Document was null, doing nothing");
            return;
        }
        if(os == null) {
            Debug.logWarning("[UtilXml.writeXmlDocument] OutputStream was null, doing nothing");
            return;
        }
        
        //if(document instanceof XmlDocument) {
        //Crimson writer
        //XmlDocument xdoc = (XmlDocument) document;
        //xdoc.write(os);
        //}
        //else {
        //Xerces writer
        OutputFormat format = new OutputFormat(document);
        format.setIndent(2);
        XMLSerializer serializer = new XMLSerializer(os, format);
        serializer.asDOMSerializer();
        serializer.serialize(document.getDocumentElement());
        //}
    }
    
    public static Document readXmlDocument(String content) throws SAXException, ParserConfigurationException, java.io.IOException {
        return readXmlDocument(content, true);
    }
    
    public static Document readXmlDocument(String content, boolean validate) throws SAXException, ParserConfigurationException, java.io.IOException {
        if(content == null) {
            Debug.logWarning("[UtilXml.readXmlDocument] URL was null, doing nothing");
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
        return readXmlDocument(bis, validate, "Internal Content");
    }
    
    public static Document readXmlDocument(URL url) throws SAXException, ParserConfigurationException, java.io.IOException {
        return readXmlDocument(url, true);
    }
    
    public static Document readXmlDocument(URL url, boolean validate) throws SAXException, ParserConfigurationException, java.io.IOException {
        if(url == null) {
            Debug.logWarning("[UtilXml.readXmlDocument] URL was null, doing nothing");
            return null;
        }
        return readXmlDocument(url.openStream(), validate, url.toString());
    }
    
    public static Document readXmlDocument(InputStream is) throws SAXException, ParserConfigurationException, java.io.IOException {
        return readXmlDocument(is, true, null);
    }
    
    public static Document readXmlDocument(InputStream is, boolean validate, String docDescription) throws SAXException, ParserConfigurationException, java.io.IOException {
        if(is == null) {
            Debug.logWarning("[UtilXml.readXmlDocument] InputStream was null, doing nothing");
            return null;
        }
        
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validate);
        //factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        if (validate) {
            LocalResolver lr = new LocalResolver(new DefaultHandler());
            ErrorHandler eh = new LocalErrorHandler(docDescription, lr);
            builder.setEntityResolver(lr);
            builder.setErrorHandler(eh);
        }
        
        document = builder.parse(is);
        
        return document;
    }
    
    public static Document makeEmptyXmlDocument() {
        return makeEmptyXmlDocument(null);
    }
    
    public static Document makeEmptyXmlDocument(String rootElementName) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        //factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        }
        catch(Exception e) { Debug.logError(e); }
        
        if(rootElementName != null) {
            Element rootElement = document.createElement(rootElementName);
            document.appendChild(rootElement);
        }
        
        if(document == null) return null;
        return document;
    }
    
    /** Creates a child element with the given name and appends it to the element child node list. */
    public static Element addChildElement(Element element, String childElementName, Document document) {
        Element newElement = document.createElement(childElementName);
        element.appendChild(newElement);
        return newElement;
    }
    
    /** Creates a child element with the given name and appends it to the element child node list.
     *  Also creates a Text node with the given value and appends it to the new elements child node list.
     */
    public static Element addChildElementValue(Element element, String childElementName, String childElementValue, Document document) {
        Element newElement = addChildElement(element, childElementName, document);
        Text newText = document.createTextNode(childElementValue);
        newElement.appendChild(newText);
        return newElement;
    }
    
    /** Return a List of Element objects that have the given name and are
     * immediate children of the given element; if name is null, all child
     * elements will be included. */
    public static List childElementList(Element element, String childElementName) {
        if(element == null) return null;
        
        List elements = new LinkedList();
        Node node = element.getFirstChild();
        if(node != null) {
            do {
                if(node.getNodeType() == Node.ELEMENT_NODE && (childElementName == null || childElementName.equals(node.getNodeName()))) {
                    Element childElement = (Element)node;
                    elements.add(childElement);
                }
            } while((node = node.getNextSibling()) != null);
        }
        return elements;
    }
    
    /** Return the first child Element with the given name; if name is null
     * returns the first element. */
    public static Element firstChildElement(Element element, String childElementName) {
        if(element == null) return null;
        //get the first element with the given name
        Node node = element.getFirstChild();
        if(node != null) {
            do {
                if(node.getNodeType() == Node.ELEMENT_NODE && (childElementName == null || childElementName.equals(node.getNodeName()))) {
                    Element childElement = (Element)node;
                    return childElement;
                }
            } while((node = node.getNextSibling()) != null);
        }
        return null;
    }
    
    /** Return the text (node value) contained by the named child node */
    public static String childElementValue(Element element, String childElementName) {
        if(element == null) return null;
        //get the value of the first element with the given name
        Element childElement = firstChildElement(element, childElementName);
        return elementValue(childElement);
    }
    
    /** Return the text (node value) of the first node under this, works best if normalized */
    public static String elementValue(Element element) {
        if(element == null) return null;
        Node textNode = element.getFirstChild();
        if(textNode == null) return null;
        //should be of type text
        return textNode.getNodeValue();
    }
    
    public static String checkEmpty(String string) {
        if(string != null && string.length() > 0) return string;
        else return "";
    }
    public static String checkEmpty(String string1, String string2) {
        if(string1 != null && string1.length() > 0) return string1;
        else if(string2 != null && string2.length() > 0) return string2;
        else return "";
    }
    public static String checkEmpty(String string1, String string2, String string3) {
        if(string1 != null && string1.length() > 0) return string1;
        else if(string2 != null && string2.length() > 0) return string2;
        else if(string3 != null && string3.length() > 0) return string3;
        else return "";
    }
    
    /**
     * Local entity resolver to handle J2EE DTDs. With this a http connection
     * to sun is not needed during deployment.
     * Function boolean hadDTD() is here to avoid validation errors in
     * descriptors that do not have a DOCTYPE declaration.
     */
    private static class LocalResolver implements EntityResolver {
        private Map dtds = new HashMap();
        private boolean hasDTD = false;
        private EntityResolver defaultResolver;
        
        public LocalResolver(EntityResolver defaultResolver) {
            initDtds();
            this.defaultResolver = defaultResolver;
        }

        public void initDtds() {
            registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", "ejb-jar.dtd");
            registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN", "ejb-jar_2_0.dtd");
            registerDTD("-//Sun Microsystems, Inc.//DTD J2EE Application 1.2//EN", "application_1_2.dtd");
            registerDTD("-//Sun Microsystems, Inc.//DTD Connector 1.0//EN", "connector_1_0.dtd");
            registerDTD("-//OFBiz//DTD Data Files//EN", "datafiles.dtd");
            registerDTD("-//OFBiz//DTD Entity Group//EN", "entitygroup.dtd");
            registerDTD("-//OFBiz//DTD Entity Model//EN", "entitymodel.dtd");
            registerDTD("-//OFBiz//DTD Field Type Model//EN", "fieldtypemodel.dtd");
            registerDTD("-//OFBiz//DTD Services Config//EN", "services.dtd");
            registerDTD("-//OFBiz//DTD Request Config//EN", "site-conf.dtd");
        }
        
        /**
         * Registers available DTDs
         * @param String publicId    - Public ID of DTD
         * @param String dtdFileName - the file name of DTD
         */
        public void registerDTD(String publicId, String dtdFileName) {
            dtds.put(publicId, dtdFileName);
        }
        
        /**
         * Returns DTD inputSource. If DTD was found in the dtds Map and inputSource was created
         * flag hasDTD is set to true.
         * @param String publicId - Public ID of DTD
         * @param String systemId - System ID of DTD
         * @return InputSource of DTD
         */
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            hasDTD = false;
            String dtd = (String) dtds.get(publicId);
            
            Debug.logInfo("[UtilXml.LocalResolver.resolveEntity] resolving DTD with publicId [" + publicId + "] and the dtd file is [" + dtd + "]");
            if (dtd != null) {
                try {
                    InputStream dtdStream = getClass().getResourceAsStream(dtd);
                    InputSource inputSource = new InputSource(dtdStream);
                    hasDTD = true;
                    Debug.logInfo("[UtilXml.LocalResolver.resolveEntity] got DTD input source with publicId [" + publicId + "] and the dtd file is [" + dtd + "]");
                    return inputSource;
                } catch(Exception e) {
                    Debug.logWarning(e);
                }
            }
            Debug.logInfo("[UtilXml.LocalResolver.resolveEntity] local resolve failed for DTD with publicId [" + publicId + "] and the dtd file is [" + dtd + "], trying defaultResolver");
            return defaultResolver.resolveEntity(publicId, systemId);
        }
        
        /**
         * Returns the boolean value to inform id DTD was found in the XML file or not
         * @return boolean - true if DTD was found in XML
         */
        public boolean hasDTD() {
            return hasDTD;
        }
    }
    
    /** Local error handler for entity resolver to DocumentBuilder parser.
     * Error is printed to output just if DTD was detected in the XML file.
     */
    private static class LocalErrorHandler implements ErrorHandler {
        private String docDescription;
        private LocalResolver localResolver;
        public LocalErrorHandler(String docDescription, LocalResolver localResolver) {
            this.docDescription = docDescription;
            this.localResolver = localResolver;
        }
        
        public void error(SAXParseException exception) {
            if (localResolver.hasDTD()) {
                Debug.logError("XmlFileLoader: File "
                + docDescription
                + " process error. Line: "
                + String.valueOf(exception.getLineNumber())
                + ". Error message: "
                + exception.getMessage()
                );
            }
        }
        public void fatalError(SAXParseException exception) {
            if (localResolver.hasDTD()) {
                Debug.logError("XmlFileLoader: File "
                + docDescription
                + " process fatal error. Line: "
                + String.valueOf(exception.getLineNumber())
                + ". Error message: "
                + exception.getMessage()
                );
            }
        }
        public void warning(SAXParseException exception) {
            if (localResolver.hasDTD()) {
                Debug.logError("XmlFileLoader: File "
                + docDescription
                + " process warning. Line: "
                + String.valueOf(exception.getLineNumber())
                + ". Error message: "
                + exception.getMessage()
                );
            }
        }
    }
}
