/* $Id$ */
/* $Source$ */

/*
 * $Log$
 * Revision 1.3  2001/11/11 00:15:42  rbb36
 * Added log4j, began implementation
 *
 * Revision 1.2  2001/11/09 03:27:42  rbb36
 * This works if run from core/src/workflow
 *
 * Revision 1.1  2001/11/04 13:14:44  rbb36
 * Initial Blank Checkin
 *
 */

package org.ofbiz.core.workflow.definition;

// SUN IMPORTS
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

// FOREIGN IMPORTS
import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// OPEN FOR BUSINESS IMPORTS
import org.ofbiz.core.util.*;

/** 
 * <p>Parses Process Definition objects to/from XPDL
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author <a href='mailto:ofb@traxel.com'>Robert Bushman</a>
 * @created Sun Nov  4 06:04:33 MST 2001
 * @version 0.0
 */
public class XpdlParser {
    
    // -------------------------------------------------------
    // LOG INITIALIZATION
    // -------------------------------------------------------
    
    static{ Log.init(); }
    private static final Category cat =
        Category.getInstance( XpdlParser.class.getName() );
    
    // -------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------
    
    public static final
        String ELEM_NAME_PACKAGE = "Package";
    public static final
        String ELEM_NAME_PACKAGE_HEADER = "PackageHeader";
    public static final
        String ELEM_NAME_REDEFINABLE_HEADER = "RedefinableHeader";
    public static final
        String ELEM_NAME_CONFORMANCE_CLASS = "ConformanceClass";
    public static final
        String ELEM_NAME_EXTERNAL_PACKAGES = "ExternalPackages";
    public static final
        String ELEM_NAME_TYPE_DECLARATIONS = "TypeDeclarations";
    public static final
        String ELEM_NAME_PARTICIPANTS = "Participants";
    public static final
        String ELEM_NAME_APPLICATIONS = "Applications";
    public static final
        String ELEM_NAME_DATA_FIELDS = "DataFields";
    public static final
        String ELEM_NAME_WORKFLOW_PROCESSES = "WorkflowProcesses";
    public static final
        String ELEM_NAME_EXTENDED_ATTRIBUTES = "ExtendedAttributes";
    
    public static final String[] LEGAL_ELEM_NAMES = {
        ELEM_NAME_PACKAGE,
        ELEM_NAME_PACKAGE_HEADER,
        ELEM_NAME_REDEFINABLE_HEADER,
        ELEM_NAME_CONFORMANCE_CLASS,
        ELEM_NAME_EXTERNAL_PACKAGES,
        ELEM_NAME_TYPE_DECLARATIONS,
        ELEM_NAME_PARTICIPANTS,
        ELEM_NAME_APPLICATIONS,
        ELEM_NAME_DATA_FIELDS,
        ELEM_NAME_WORKFLOW_PROCESSES,
        ELEM_NAME_EXTENDED_ATTRIBUTES,
    };
    
    // ---------------------------------------------------------
    // PUBLIC API
    // ---------------------------------------------------------
    
    public static Object parse( String fileName ) {
        final File file = new File( fileName );
        final DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        
        DocumentBuilder builder;
        Document document;
        Element docElement;
        Object pakkage = null;
        
        factory.setValidating( true );
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse( file );
            docElement = document.getDocumentElement();
            pakkage = (Object)parseElement( ELEM_NAME_PACKAGE,
                                            docElement );
        } catch( ParserConfigurationException e ) {
            cat.fatal( e.getMessage(), e );
        } catch( SAXException e ) {
            cat.error( e.getMessage(), e );
        } catch( IOException e ) {
            cat.error( e.getMessage(), e );
        }
        
        return( pakkage );
    }
    
    // ---------------------------------------------------------
    // RUNTIME, TEST, AND SAMPLE METHODS
    // ---------------------------------------------------------
    
    public static void main( String[] args ) throws Exception {
        final String sampleFileName = "../../docs/examples/sample.xpdl";
        final Object o = parse( sampleFileName );
    }
    
    // -------------------------------------------------------
    // INTERNAL API
    // -------------------------------------------------------
    
    protected static Method getParseMethod( String elementName )
        throws NoSuchMethodException {
        final String methodName = "parse" + elementName;
        final Class[] argClasses = new Class[]{ Element.class };
        return( XpdlParser.class.getMethod( methodName, argClasses ) );
    }
    
    // -------------------------------------------------------
    // FACTORIES
    // -------------------------------------------------------
    
    /**
     * Dynamically invokes the parse method for the given
     * element type, returns an object of the appropriate
     * runtime type. Returns null if any exception occurs,
     * and writes the exception to Log4j.
     */
    public static Object parseElement( String elementName,
                                       Element element ) {
        try {
            final Object[] args = new Object[]{ element };
            final Method method = getParseMethod( elementName );
            return( method.invoke( null, args ) );
        } catch( NoSuchMethodException e ) {
            cat.error
                ( "Attempt to Parse Unknown Element: " + elementName, e );
            return( null );
        } catch( Exception e ) {
            cat.error
                ( "Unanticipated Exception Parsing: " + elementName, e );
            return( null );
        }
    }
    
    /**
     * Parses an XPDL Package element into an instance of a
     * Package object.
     */
    public static Object parsePackage( Element packageElement ) {
        final Hashtable pakkage = new Hashtable();
        pakkage.put( "Package.Id", packageElement.getAttribute( "Id" ) );
        Node child = packageElement.getFirstChild();
        while( child != null ) {
            if( child.getNodeType() == Node.ELEMENT_NODE ) {
                final String nodeName = child.getNodeName();
                final Object o = parseElement( nodeName, (Element)child );
                if( o != null ) {
                    pakkage.put( "Package." + nodeName, o );
                }
            }
            child = child.getNextSibling();
        }
        cat.fatal( "parsePackage( Element ) has not been implemented",
                   new Exception() );
        System.out.println( pakkage.get( "Package.Id" ) );
        return( pakkage );
    }
    
    /** Parses an XPDL Join element into an instance of a Join object. */
    public static Join parseJoin( Element joinElement ) {
        final String ATTR_KEY_TYPE = "Type";
        final String ATTR_VALUE_XOR = "XOR";
        final String ATTR_VALUE_AND = "AND";
        
        final String typeAttr = joinElement.getAttribute( ATTR_KEY_TYPE );
        int type = Join.AND;
        
        if( typeAttr.equals( ATTR_VALUE_XOR ) ) { type = Join.XOR; }
        
        return( new Join( type ) );
    }
}

