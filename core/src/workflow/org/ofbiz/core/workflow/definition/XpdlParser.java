/* $Id$ */
/* $Source$ */

/*
 * $Log$
 * Revision 1.6  2001/11/11 22:33:34  rbb36
 * Everything except WorkflowProcess parses
 *
 * Revision 1.5  2001/11/11 06:13:32  rbb36
 * Now fully parses the first two entities
 *
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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
    public static final
        String ELEM_NAME_PRIORITY = "Priority";
    public static final
        String ELEM_NAME_LIMIT = "Limit";
    public static final
        String ELEM_NAME_WAITING_TIME = "WaitingTime";
    public static final
        String ELEM_NAME_WORKING_TIME = "WorkingTime";
    public static final
        String ELEM_NAME_DURATION = "Duration";
    public static final
        String ELEM_NAME_VALID_FROM = "ValidFrom";
    public static final
        String ELEM_NAME_VALID_TO = "ValidTo";
    public static final
        String ELEM_NAME_INITIAL_VALUE = "InitialValue";
    public static final
        String ELEM_NAME_LENGTH = "Length";
    public static final
        String ELEM_NAME_ACTUAL_PARAMETER = "ActualParameter";
    public static final
        String ELEM_NAME_PERFORMER = "Performer";
    public static final
        String ELEM_NAME_ICON = "Icon";
    public static final
        String ELEM_NAME_BLOCK_NAME = "BlockName";
    public static final
        String ELEM_NAME_COST = "Cost";
    
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
        ELEM_NAME_PRIORITY,
        ELEM_NAME_LIMIT,
        ELEM_NAME_WAITING_TIME,
        ELEM_NAME_WORKING_TIME,
        ELEM_NAME_DURATION,
        ELEM_NAME_VALID_FROM,
        ELEM_NAME_VALID_TO,
        ELEM_NAME_INITIAL_VALUE,
        ELEM_NAME_LENGTH,
        ELEM_NAME_ACTUAL_PARAMETER,
        ELEM_NAME_PERFORMER,
        ELEM_NAME_ICON,
        ELEM_NAME_BLOCK_NAME,
        ELEM_NAME_COST,
    };
    
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
    public static final
        String ELEM_NAME_RESPONSIBLES = "Responsibles";
    public static final
        String ELEM_NAME_FORMAL_PARAMETERS = "FormalParameters";
    public static final
        String ELEM_NAME_ACTIVITIES = "Activities";
    public static final
        String ELEM_NAME_TRANSITIONS = "Transitions";
    public static final
        String ELEM_NAME_TRANSITION_RESTRICTIONS = "TransitionRestrictions";
    public static final
        String ELEM_NAME_ACTUAL_PARAMETERS = "ActualParameters";
    public static final
        String ELEM_NAME_TRANSITION_REFS = "TransitionRefs";
    
    public static final String[] VECTOR_ELEM_NAMES = {
        ELEM_NAME_EXTERNAL_PACKAGES,
        ELEM_NAME_TYPE_DECLARATIONS,
        ELEM_NAME_PARTICIPANTS,
        ELEM_NAME_APPLICATIONS,
        ELEM_NAME_DATA_FIELDS,
        ELEM_NAME_WORKFLOW_PROCESSES,
        ELEM_NAME_EXTENDED_ATTRIBUTES,
        ELEM_NAME_RESPONSIBLES,
        ELEM_NAME_FORMAL_PARAMETERS,
        ELEM_NAME_ACTIVITIES,
        ELEM_NAME_TRANSITIONS,
        ELEM_NAME_TRANSITION_RESTRICTIONS,
        ELEM_NAME_ACTUAL_PARAMETERS,
        ELEM_NAME_TRANSITION_REFS,
    };
    
    public static Method TEXT_ELEM_METHOD = null;
    public static Method VECTOR_ELEM_METHOD = null;
    public static Method MAP_ELEM_METHOD = null;
    
    static {
        initParseTextMethod();
        initParseVectorMethod();
        initParseMapMethod();
    }
    
    public static final
        String ELEM_NAME_ = "";
    
    // -------------------------------------------------------------
    // CONSTRUCTOR AND INITIALIZATION METHODS
    // -------------------------------------------------------------
    
    protected static void initParseTextMethod() {
        final String methodName = "parseTextElement";
        final Class[] argClasses = new Class[]{ Element.class };
        try {
            TEXT_ELEM_METHOD =
                XpdlParser.class.getMethod( methodName, argClasses );
        } catch( Exception e ) {
            cat.fatal( "Can't find " + methodName + " method.", e );
        }
    }
    
    protected static void initParseVectorMethod() {
        final String methodName = "parseVectorElement";
        final Class[] argClasses = new Class[]{ Element.class };
        try {
            VECTOR_ELEM_METHOD =
                XpdlParser.class.getMethod( methodName, argClasses );
        } catch( Exception e ) {
            cat.fatal( "Can't find " + methodName + " method.", e );
        }
    }
    
    protected static void initParseMapMethod() {
        final String methodName = "parseMappedElement";
        final Class[] argClasses = new Class[]{ Element.class };
        try {
            MAP_ELEM_METHOD =
                XpdlParser.class.getMethod( methodName, argClasses );
        } catch( Exception e ) {
            cat.fatal( "Can't find " + methodName + " method.", e );
        }
    }
    
    // ---------------------------------------------------------
    // PUBLIC API
    // ---------------------------------------------------------
    
    public static Object parse( String fileName ) {
        cat.info( "Beginning File Parse: " + fileName );
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
    
    protected static Method getParseMethod( String elementName )
        throws NoSuchMethodException {
        if( isTextElement( elementName ) && TEXT_ELEM_METHOD != null ) {
            return( TEXT_ELEM_METHOD );
        } else if( isVectorElement( elementName ) && VECTOR_ELEM_METHOD != null ) {
            return( VECTOR_ELEM_METHOD );
        } else if( MAP_ELEM_METHOD != null ) {
            return( MAP_ELEM_METHOD );
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
    
    protected static boolean isVectorElement( String elementName ) {
        for( int i = 0; i < VECTOR_ELEM_NAMES.length; i++ ) {
            if( VECTOR_ELEM_NAMES[i].equals( elementName ) ) {
                return( true );
            }
        }
        return( false );
    }
    
    // -------------------------------------------------------
    // FACTORIES
    // -------------------------------------------------------
    
    /**
     * Returns null if the given node is not an element
     */
    public static Object parseElement( Node child ) {
        if( child.getNodeType() == Node.ELEMENT_NODE ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( nodeName, (Element)child );
            return( o );
        } else {
            return( null );
        }
    }
    
    /**
     * Dynamically invokes the parse method for the given
     * element type, returns an object of the appropriate
     * runtime type. Returns null if any exception occurs,
     * and writes the exception to Log4j.
     */
    public static Object parseElement( String elementName,
                                       Element element ) {
        try {
            final String targetClassName = 
                "org.ofbiz.core.workflow.definition." + elementName;
            final Class clazz = Class.forName( targetClassName );
            System.out.println( "Found: " + clazz.getName() );
        } catch( Exception e ) {
        }
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
        final String elementName = element.getNodeName();
        String string = null;
        Node child = element.getFirstChild();
        while( child != null && string == null ) {
            if( child.getNodeType() == Node.TEXT_NODE ) {
                string = child.getNodeValue();
            }
            child = child.getNextSibling();
        }
        cat.info( "parse " + elementName + " using generic text parser" );
        return( string );
    }
    
    public static Vector parseVectorElement( Element element ) {
        final String elementName = element.getNodeName();
        final Vector elements = new Vector();
        Node child = element.getFirstChild();
        while( child != null ) {
            final Object o = parseElement( child );
            if( o != null ) { elements.add( o ); }
            child = child.getNextSibling();
        }
        cat.info( "parse " + elementName + " using generic vector parser" );
        return( elements );
    }
    
    public static HashMap parseMappedElement( Element element ) {
        final String elementName = element.getNodeName();
        final HashMap map = new HashMap();
        final NamedNodeMap attributes = element.getAttributes();
        if( attributes != null ) {
            for( int i = 0; i < attributes.getLength(); i++ ) {
                final Attr attr = (Attr)attributes.item( i );
                if( attr != null ) {
                    map.put( attr.getName(), attr.getValue() );
                }
            }
        }
        final NodeList children = element.getChildNodes();
        for( int i = 0; i < children.getLength(); i++ ) {
            final Node child = children.item( i );
            if( child != null ) {
                final String name = child.getNodeName();
                final Object o = parseElement( child );
                if( o != null ) { map.put( name, o ); }
            }
        }
        cat.info( "parse " + elementName + " using generic map parser" );
        return( map );
    }
    
    /**
     * Parses an XPDL Package element into an instance of a
     * Package object.
     */
    /*
    public static Object parsePackage( Element packageElement ) {
        final HashMap pakkage = new HashMap();
        pakkage.put( "Id", packageElement.getAttribute( "Id" ) );
        Node child = packageElement.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { pakkage.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parsePackage has not been type implemented" );
        return( pakkage );
    }
    
    public static Object parsePackageHeader( Element packageHeaderElement ) {
        final HashMap packageHeader = new HashMap();
        Node child = packageHeaderElement.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { packageHeader.put( nodeName, o ); }
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
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { redefinableHeader.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseRedefinableHeader has not been type implemented" );
        return( redefinableHeader );
    }
    
    public static Object parseConformanceClass( Element element ) {
        final HashMap conformanceClass = new HashMap();
        conformanceClass.put( "GraphConformance",
                              element.getAttribute( "GraphConformance" ) );
        cat.debug( "parseConformanceClass has not been type implemented" );
        return( conformanceClass );
    }
    
    public static Object parseExternalPackages( Element element ) {
        final Vector externalPackages = new Vector();
        Node child = element.getFirstChild();
        while( child != null ) {
            final Object o = parseElement( child );
            if( o != null ) { externalPackages.add( o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseExternalPackages has not been type implemented" );
        return( externalPackages );
    }
    
    public static Object parseExternalPackage( Element element ) {
        final HashMap externalPackage = new HashMap();
        externalPackage.put( "href", element.getAttribute( "href" ) );
        Node child = element.getFirstChild();
        while( child != null ) { 
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { externalPackage.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseExternalPackage has not been type implemented" );
        return( externalPackage );
    }
    
    public static Object parseExtendedAttribute( Element element ) {
        final HashMap extendedAttribute = new HashMap();
        extendedAttribute.put( "Name", element.getAttribute( "Name" ) );
        extendedAttribute.put( "Value", element.getAttribute( "Value" ) );
        Node child = element.getFirstChild();
        cat.warn( "Since this element contains \"ANY\", this method could"
                  + " overwrite the second instance of an entity if"
                  + " a list is not wrappered by an array." );
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { extendedAttribute.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseExtendedAttribute has not been type implemented" );
        return( extendedAttribute );
    }
    
    public static Object parseTypeDeclaration( Element element ) {
        final HashMap typeDeclaration = new HashMap();
        typeDeclaration.put( "Id", element.getAttribute( "Id" ) );
        typeDeclaration.put( "Name", element.getAttribute( "Name" ) );
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { typeDeclaration.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseTypeDeclaration has not been type implemented" );
        return( typeDeclaration );
    }
    
    public static Object parseBasicType( Element element ) {
        final HashMap basicType = new HashMap();
        basicType.put( "Type", element.getAttribute( "Type" ) );
        cat.debug( "parseBasicType has not been type implemented" );
        return( basicType );
    }
    
    public static Object parseParticipant( Element element ) {
        final HashMap participant = new HashMap();
        participant.put( "Id", element.getAttribute( "Id" ) );
        participant.put( "Name", element.getAttribute( "Name" ) );
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { participant.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseParticipant has not been type implemented" );
        return( participant );
    }
    
    public static Object parseParticipantType( Element element ) {
        final HashMap participantType = new HashMap();
        participantType.put( "Type", element.getAttribute( "Type" ) );
        cat.debug( "parseParticipantType has not been type implemented" );
        return( participantType );
    }
    
    public static Object parseApplication( Element element ) {
        final HashMap application = new HashMap();
        application.put( "Id", element.getAttribute( "Id" ) );
        application.put( "Name", element.getAttribute( "Name" ) );
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { application.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseApplication has not been type implemented" );
        return( application );
    }
    
    public static Object parseFormalParameter( Element element ) {
        final HashMap formalParameter = new HashMap();
        formalParameter.put( "Id", element.getAttribute( "Id" ) );
        formalParameter.put( "Index", element.getAttribute( "Index" ) );
        formalParameter.put( "Mode", element.getAttribute( "Mode" ) );
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { formalParameter.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseFormalParameter has not been type implemented" );
        return( formalParameter );
    }
    
    public static Object parseDataType( Element element ) {
        final HashMap dataType = new HashMap();
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { dataType.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseDataType has not been type implemented" );
        return( dataType );
    }
    
    public static Object parseDataField( Element element ) {
        final HashMap dataField = new HashMap();
        dataField.put( "Id", element.getAttribute( "Id" ) );
        dataField.put( "Name", element.getAttribute( "Name" ) );
        dataField.put( "IsArray", element.getAttribute( "IsArray" ) );
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { dataField.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseDataField has not been type implemented" );
        return( dataField );
    }
    
    public static Object parseWorkflowProcess( Element element ) {
        final HashMap workflowProcess = new HashMap();
        workflowProcess.put( "Id", element.getAttribute( "Id" ) );
        workflowProcess.put( "Name", element.getAttribute( "Name" ) );
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { workflowProcess.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseWorkflowProcess has not been type implemented" );
        return( workflowProcess );
    }
    
    public static Object parseProcessHeader( Element element ) {
        final HashMap processHeader = new HashMap();
        processHeader.put( "DurationUnit", element.getAttribute( "DurationUnit" ) );
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { processHeader.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseProcessHeader has not been type implemented" );
        return( processHeader );
    }
    
    public static Object parseTimeEstimation( Element element ) {
        final HashMap timeEstimation = new HashMap();
        Node child = element.getFirstChild();
        while( child != null ) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement( child );
            if( o != null ) { timeEstimation.put( nodeName, o ); }
            child = child.getNextSibling();
        }
        cat.debug( "parseTimeEstimation has not been type implemented" );
        return( timeEstimation );
    }
    
    public static Object parseActivity( Element element ) {
        return( parseMappedElement( element ) );
    }
    */
    
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

