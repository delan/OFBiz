/* $Id$ */
/* $Source$ */

/*
 * $Log$
 * Revision 1.4  2001/11/11 02:09:32  rbb36
 * Further down the parsing trail
 *
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
    
    public static final
        String ELEM_NAME_XPDL_VERSION = "XPDLVersion";
    public static final
        String ELEM_NAME_VENDOR = "Vendor";
    public static final
        String ELEM_NAME_CREATED = "Created";
    public static final
        String ELEM_NAME_DESCRIPTION = "Description";
    public static final
        String ELEM_NAME_DOCUMENTATION = "Documentation";
    public static final
        String ELEM_NAME_PRIORITY_UNIT = "PriorityUnit";
    public static final
        String ELEM_NAME_COST_UNIT = "CostUnit";
    public static final
        String ELEM_NAME_AUTHOR = "Author";
    public static final
        String ELEM_NAME_VERSION = "Version";
    public static final
        String ELEM_NAME_CODEPAGE = "Codepage";
    public static final
        String ELEM_NAME_COUNTRYKEY = "Countrykey";
    public static final
        String ELEM_NAME_RESPONSIBLE = "Responsible";
    
    public static final String[] TEXT_ELEM_NAMES = {
        ELEM_NAME_XPDL_VERSION,
        ELEM_NAME_VENDOR,
        ELEM_NAME_CREATED,
        ELEM_NAME_DESCRIPTION,
        ELEM_NAME_DOCUMENTATION,
        ELEM_NAME_PRIORITY_UNIT,
        ELEM_NAME_COST_UNIT,
        ELEM_NAME_AUTHOR,
        ELEM_NAME_VERSION,
        ELEM_NAME_CODEPAGE,
        ELEM_NAME_COUNTRYKEY,
        ELEM_NAME_RESPONSIBLE,
    };
    
    public static final
        String ELEM_NAME_ = "";
    
    public static final
        Method TEXT_ELEM_METHOD = getParseTextElementMethod();
    
    // ---------------------------------------------------------
    // PUBLIC API
    // ---------------------------------------------------------
    
    public static Object parse( String fileName ) {
        cat.info( "Beggining File Parse: " + fileName );
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
        HashMap pakkage = (HashMap)o;
        System.out.println( pakkage.get( "Id" ) );
    }
    
    // -------------------------------------------------------
    // INTERNAL API
    // -------------------------------------------------------
    
    protected static Method getParseTextElementMethod() {
        final String methodName = "parseTextElement";
        final Class[] argClasses = new Class[]{ Element.class };
        try {
            return( XpdlParser.class.getMethod( methodName, argClasses ) );
        } catch( Exception e ) {
            cat.fatal( "Can't Find parseTextElementMethod."
                       + " Null Pointer Coming Soon.", e );
        }
        return( null );
    }
    
    protected static Method getParseMethod( String elementName )
        throws NoSuchMethodException {
        if( isTextElement( elementName ) && TEXT_ELEM_METHOD != null ) {
            return( TEXT_ELEM_METHOD );
        }
        final String methodName = "parse" + elementName;
        final Class[] argClasses = new Class[]{ Element.class };
        return( XpdlParser.class.getMethod( methodName, argClasses ) );
    }
    
    protected static boolean isTextElement( String elementName ) {
        for( int i = 0; i < TEXT_ELEM_NAMES.length; i++ ) {
            if( TEXT_ELEM_NAMES[i].equals( elementName ) ) {
                return( true );
            }
        }
        return( false );
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
    
    public static String parseTextElement( Element element ) {
        String string = null;
        Node child = element.getFirstChild();
        while( child != null && string == null ) {
            if( child.getNodeType() == Node.TEXT_NODE ) {
                string = child.getNodeValue();
            }
            child = child.getNextSibling();
        }
        return( string );
    }
    
    /**
     * Parses an XPDL Package element into an instance of a
     * Package object.
     */
    public static Object parsePackage( Element packageElement ) {
        final HashMap pakkage = new HashMap();
        pakkage.put( "Id", packageElement.getAttribute( "Id" ) );
        Node child = packageElement.getFirstChild();
        while( child != null ) {
            if( child.getNodeType() == Node.ELEMENT_NODE ) {
                final String nodeName = child.getNodeName();
                final Object o = parseElement( nodeName, (Element)child );
                if( o != null ) {
                    pakkage.put( nodeName, o );
                }
            }
            child = child.getNextSibling();
        }
        cat.debug( "parsePackage has not been type implemented" );
        return( pakkage );
    }
    
    public static Object parsePackageHeader( Element packageHeaderElement ) {
        final HashMap packageHeader = new HashMap();
        Node child = packageHeaderElement.getFirstChild();
        while( child != null ) {
            if( child.getNodeType() == Node.ELEMENT_NODE ) {
                final String nodeName = child.getNodeName();
                final Object o = parseElement( nodeName, (Element)child );
                if( o != null ) {
                    packageHeader.put( nodeName, o );
                }
            }
            child = child.getNextSibling();
        }
        cat.debug( "parsePackageHeader has not been type implemented" );
        return( packageHeader );
    }
    
    public static Object parseRedefinableHeader( Element element ) {
        final HashMap redefinableHeader = new HashMap();
        final String elementName = "RedefinableHeader";
        final String pubStatusName = "PublicationStatus";
        final String pubStatusValue = element.getAttribute( pubStatusName );
        redefinableHeader.put( pubStatusName, pubStatusValue );
        Node child = element.getFirstChild();
        while( child != null ) {
            if( child.getNodeType() == Node.ELEMENT_NODE ) {
                final String nodeName = child.getNodeName();
                final Object o = parseElement( nodeName, (Element)child );
                if( o != null ) {
                    redefinableHeader.put( nodeName, o );
                }
            }
            child = child.getNextSibling();
        }
        cat.debug( "parseRedefinableHeader has not been type implemented" );
        return( redefinableHeader );
    }
    
    public static Object parseResponsibles( Element element ) {
        final Vector responsibles = new Vector();
        Node child = element.getFirstChild();
        while( child != null ) {
            if( child.getNodeType() == Node.ELEMENT_NODE ) {
                final String nodeName = child.getNodeName();
                final Object o = parseElement( nodeName, (Element)child );
                if( o != null ) {
                    responsibles.add( o );
                }
            }
            child = child.getNextSibling();
        }
        cat.debug( "parseResponsibles has not been type implemented" );
        return( responsibles );
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

