/* $Id$ */
/* $Source$ */

/*
 * $Log$
 * Revision 1.8  2001/11/24 06:26:04  jonesde
 * First pass at new direction for parser using UtilXml and creating entitiy value objects
 *
 * Revision 1.7  2001/11/11 23:30:20  rbb36
 * switched everything to using the magic parsers, including
 * the mapped entities. Wrote the first tiny piece of magic
 * class instantiaton, it identifies WorkflowProcess, Activity,
 * Join, Split, and Transition (though it doesn't know what
 * to do with 'em yet).
 *
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.net.*;

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
import org.ofbiz.core.entity.*;

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
 * @author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 * @created Sun Nov  4 06:04:33 MST 2001
 * @version 1.0
 */
public class XpdlParser {
    protected GenericDelegator delegator = null;
    protected List values = null;
    
    // LOG INITIALIZATION
    static{ Log.init(); }
    private static final Category cat = Category.getInstance(XpdlParser.class.getName());
    
    public XpdlParser(GenericDelegator delegator) {
        this.delegator = delegator;
    }
    
    // ---------------------------------------------------------
    // PUBLIC API
    // ---------------------------------------------------------

    /** Imports an XPDL file at the given location and imports it into the
     * datasource through the given delegator */
    public static void importXpdl(URL location, GenericDelegator delegator) throws DefinitionParserException {
        List values = parseXpdl(location, delegator);
        try { delegator.storeAll(values); }
        catch(GenericEntityException e) { throw new DefinitionParserException("Could not store values", e); }
    }
    
    /** Gets an XML file from the specified location and parses it into 
     * GenericValue objects from the given delegator and returns them in a 
     * List; does not write to the database, just gets the entities. */
    public static List parseXpdl(URL location, GenericDelegator delegator) throws DefinitionParserException {
        cat.info("Beginning XPDL File Parse: " + location.toString());
        
        XpdlParser parser = new XpdlParser(delegator);
        try {
            Document document = UtilXml.readXmlDocument(location);
            return parser.parseAll(document);
        }
        catch(ParserConfigurationException e) {
            cat.fatal(e.getMessage(), e);
            throw new DefinitionParserException("Could not configure XML parser", e);
        }
        catch(SAXException e) {
            cat.error(e.getMessage(), e);
            throw new DefinitionParserException("Could not parse XML (invalid?)", e);
        }
        catch(IOException e) {
            cat.error(e.getMessage(), e);
            throw new DefinitionParserException("Could not load file", e);
        }
    }
    
    public List parseAll(Document document) throws DefinitionParserException {
        values = new LinkedList();
        Element docElement;

        docElement = document.getDocumentElement();
        //parse the package element, and everything under it
        // puts everything in the values list for returning, etc later
        parsePackage(docElement);
        
        return(values);
    }
    
    // -------------------------------------------------------
    // Methods for individual entities
    // -------------------------------------------------------

    protected void parsePackage(Element packageElement) throws DefinitionParserException {
        if(!"Package".equals(packageElement.getTagName()))  throw new DefinitionParserException("Tried to make Package from element not named Package");
        
        GenericValue packageValue = delegator.makeValue("WorkflowPackage", null);
        String packageId = packageElement.getAttribute("Id");
        packageValue.set("packageId", packageId);
        packageValue.set("packageName", packageElement.getAttribute("Name"));
        
        //PackageHeader
        Element packageHeaderElement = UtilXml.firstChildElement(packageElement, "PackageHeader");
        packageValue.set("specificationId", "XPDL");
        packageValue.set("specificationVersion", UtilXml.childElementValue(packageHeaderElement, "XPDLVersion"));
        packageValue.set("sourceVendorInfo", UtilXml.childElementValue(packageHeaderElement, "Vendor"));
        String createdStr = UtilXml.childElementValue(packageHeaderElement, "Created");
        packageValue.set("creationDateTime", java.sql.Timestamp.valueOf(createdStr));
        packageValue.set("description", UtilXml.childElementValue(packageHeaderElement, "Description"));
        packageValue.set("documentationUrl", UtilXml.childElementValue(packageHeaderElement, "Documentation"));
        packageValue.set("priorityUomId", UtilXml.childElementValue(packageHeaderElement, "PriorityUnit"));
        packageValue.set("costUomId", UtilXml.childElementValue(packageHeaderElement, "CostUnit"));
        
        //RedefinableHeader
        Element redefinableHeaderElement = UtilXml.firstChildElement(packageElement, "RedefinableHeader");
        packageValue.set("author", UtilXml.childElementValue(redefinableHeaderElement, "Author"));
        packageValue.set("packageVersion", UtilXml.childElementValue(redefinableHeaderElement, "Version"));
        packageValue.set("codepage", UtilXml.childElementValue(redefinableHeaderElement, "Codepage"));
        packageValue.set("countryGeoId", UtilXml.childElementValue(redefinableHeaderElement, "Countrykey"));
        
        //ConformanceClass
        Element conformanceClass = UtilXml.firstChildElement(packageElement, "ConformanceClass");
        packageValue.set("graphConformanceEnumId", "WGC_" + conformanceClass.getAttribute("GraphConformance"));
        
        //done with basic packageValue, add to list
        values.add(packageValue);

        //Responsibles
        Element responsiblesElement = UtilXml.firstChildElement(redefinableHeaderElement, "Responsibles");
        List responsibles = UtilXml.childElementList(responsiblesElement, "Responsible");
        parseResponsibles(responsibles, packageValue);
        
        //ExternalPackages
        Element externalPackagesElement = UtilXml.firstChildElement(packageElement, "ExternalPackages");
        List externalPackages = UtilXml.childElementList(externalPackagesElement, "ExternalPackage");
        parseExternalPackages(externalPackages, packageId);
        
        //TypeDeclarations
        Element typeDeclarationsElement = UtilXml.firstChildElement(packageElement, "TypeDeclarations");
        List typeDeclarations = UtilXml.childElementList(typeDeclarationsElement, "TypeDeclaration");
        parseTypeDeclarations(typeDeclarations, packageId);
        
        //Participants
        Element participantsElement = UtilXml.firstChildElement(packageElement, "Participants");
        List participants = UtilXml.childElementList(participantsElement, "Participant");
        parseParticipants(participants, packageValue);
        
        //Applications
        Element applicationsElement = UtilXml.firstChildElement(packageElement, "Applications");
        List applications = UtilXml.childElementList(applicationsElement, "Application");
        parseApplications(applications, packageValue);
        
        //DataFields
        Element dataFieldsElement = UtilXml.firstChildElement(packageElement, "DataFields");
        List dataFields = UtilXml.childElementList(dataFieldsElement, "DataField");
        parseDataFields(dataFields, packageValue);
        
        //WorkflowProcesses
        Element workflowProcessesElement = UtilXml.firstChildElement(packageElement, "WorkflowProcesses");
        List workflowProcesses = UtilXml.childElementList(workflowProcessesElement, "WorkflowProcess");
        parseWorkflowProcesses(workflowProcesses, packageId);
    }
    
    protected void parseResponsibles(List responsibles, GenericValue packageValue) throws DefinitionParserException {
        if(responsibles == null || responsibles.size() == 0) return;

        Long nextSeqId = delegator.getNextSeqId("WorkflowParticipantList");
        if(nextSeqId == null) throw new DefinitionParserException("Could not get next sequence id from data source");
        String responsibleListId = nextSeqId.toString();
        packageValue.set("responsibleListId", responsibleListId);

        Iterator responsibleIter = responsibles.iterator();
        int responsibleIndex = 1;
        while(responsibleIter.hasNext()) {
            Element responsibleElement = (Element)responsibleIter.next();
            String responsibleId = UtilXml.elementValue(responsibleElement);
            GenericValue participantListValue = delegator.makeValue("WorkflowParticipantList", null);
            participantListValue.set("participantListId",responsibleListId);
            participantListValue.set("participantId", responsibleId);
            participantListValue.set("participantIndex", new Long(responsibleIndex));
            values.add(participantListValue);
            responsibleIndex++;
        }
    }
    
    protected void parseExternalPackages(List externalPackages, String packageId) {
        if(externalPackages == null || externalPackages.size() == 0) return;
        Iterator externalPackageIter = externalPackages.iterator();
        while(externalPackageIter.hasNext()) {
            Element externalPackageElement = (Element)externalPackageIter.next();
            GenericValue externalPackageValue = delegator.makeValue("WorkflowPackageExternal", null);
            externalPackageValue.set("packageId", packageId);
            externalPackageValue.set("externalPackageId", externalPackageElement.getAttribute("href"));
            values.add(externalPackageValue);
        }
    }
    
    protected void parseTypeDeclarations(List typeDeclarations, String packageId) throws DefinitionParserException {
        if(typeDeclarations == null || typeDeclarations.size() == 0) return;
    }
    
    protected void parseParticipants(List participants, GenericValue packageValue) throws DefinitionParserException {
        if(participants == null || participants.size() == 0) return;
    }
    
    protected void parseApplications(List applications, GenericValue packageValue) throws DefinitionParserException {
        if(applications == null || applications.size() == 0) return;
    }
    
    protected void parseDataFields(List dataFields, GenericValue packageValue) throws DefinitionParserException {
        if(dataFields == null || dataFields.size() == 0) return;
    }
    
    protected void parseWorkflowProcesses(List workflowProcesses, String packageId) {
        if(workflowProcesses == null || workflowProcesses.size() == 0) return;
        Iterator workflowProcessIter = workflowProcesses.iterator();
        while(workflowProcessIter.hasNext()) {
            Element workflowProcessElement = (Element)workflowProcessIter.next();
            parseWorkflowProcess(workflowProcessElement, packageId);
        }
    }
    
    protected void parseWorkflowProcess(Element workflowProcessElement, String packageId) {
        GenericValue workflowProcessValue = delegator.makeValue("WorkflowProcess", null);
        workflowProcessValue.set("packageId", packageId);
        workflowProcessValue.set("processId", workflowProcessElement.getAttribute("Id"));
        workflowProcessValue.set("objectName", workflowProcessElement.getAttribute("Name"));
        values.add(workflowProcessValue);
    }
    
    /**
     * Parses an XPDL Package element into an instance of a
     * Package object.
     */
    /*
    public static Object parsePackage(Element packageElement) {
        final HashMap pakkage = new HashMap();
        pakkage.put("Id", packageElement.getAttribute("Id"));
        Node child = packageElement.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { pakkage.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parsePackage has not been type implemented");
        return(pakkage);
    }

    public static Object parsePackageHeader(Element packageHeaderElement) {
        final HashMap packageHeader = new HashMap();
        Node child = packageHeaderElement.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { packageHeader.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parsePackageHeader has not been type implemented");
        return(packageHeader);
    }
    
    public static Object parseRedefinableHeader(Element element) {
        final HashMap redefinableHeader = new HashMap();
        final String elementName = "RedefinableHeader";
        final String pubStatusName = "PublicationStatus";
        final String pubStatusValue = element.getAttribute(pubStatusName);
        redefinableHeader.put(pubStatusName, pubStatusValue);
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { redefinableHeader.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseRedefinableHeader has not been type implemented");
        return(redefinableHeader);
    }
    
    public static Object parseConformanceClass(Element element) {
        final HashMap conformanceClass = new HashMap();
        conformanceClass.put("GraphConformance",
        element.getAttribute("GraphConformance"));
        cat.debug("parseConformanceClass has not been type implemented");
        return(conformanceClass);
    }
    
    public static Object parseExternalPackages(Element element) {
        final Vector externalPackages = new Vector();
        Node child = element.getFirstChild();
        while(child != null) {
            final Object o = parseElement(child);
            if(o != null) { externalPackages.add(o); }
            child = child.getNextSibling();
        }
        cat.debug("parseExternalPackages has not been type implemented");
        return(externalPackages);
    }
    
    public static Object parseExternalPackage(Element element) {
        final HashMap externalPackage = new HashMap();
        externalPackage.put("href", element.getAttribute("href"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { externalPackage.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseExternalPackage has not been type implemented");
        return(externalPackage);
    }
    
    public static Object parseExtendedAttribute(Element element) {
        final HashMap extendedAttribute = new HashMap();
        extendedAttribute.put("Name", element.getAttribute("Name"));
        extendedAttribute.put("Value", element.getAttribute("Value"));
        Node child = element.getFirstChild();
        cat.warn("Since this element contains \"ANY\", this method could"
        + " overwrite the second instance of an entity if"
        + " a list is not wrappered by an array.");
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { extendedAttribute.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseExtendedAttribute has not been type implemented");
        return(extendedAttribute);
    }
    
    public static Object parseTypeDeclaration(Element element) {
        final HashMap typeDeclaration = new HashMap();
        typeDeclaration.put("Id", element.getAttribute("Id"));
        typeDeclaration.put("Name", element.getAttribute("Name"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { typeDeclaration.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseTypeDeclaration has not been type implemented");
        return(typeDeclaration);
    }
    
    public static Object parseBasicType(Element element) {
        final HashMap basicType = new HashMap();
        basicType.put("Type", element.getAttribute("Type"));
        cat.debug("parseBasicType has not been type implemented");
        return(basicType);
    }
    
    public static Object parseParticipant(Element element) {
        final HashMap participant = new HashMap();
        participant.put("Id", element.getAttribute("Id"));
        participant.put("Name", element.getAttribute("Name"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { participant.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseParticipant has not been type implemented");
        return(participant);
    }
    
    public static Object parseParticipantType(Element element) {
        final HashMap participantType = new HashMap();
        participantType.put("Type", element.getAttribute("Type"));
        cat.debug("parseParticipantType has not been type implemented");
        return(participantType);
    }
    
    public static Object parseApplication(Element element) {
        final HashMap application = new HashMap();
        application.put("Id", element.getAttribute("Id"));
        application.put("Name", element.getAttribute("Name"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { application.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseApplication has not been type implemented");
        return(application);
    }
    
    public static Object parseFormalParameter(Element element) {
        final HashMap formalParameter = new HashMap();
        formalParameter.put("Id", element.getAttribute("Id"));
        formalParameter.put("Index", element.getAttribute("Index"));
        formalParameter.put("Mode", element.getAttribute("Mode"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { formalParameter.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseFormalParameter has not been type implemented");
        return(formalParameter);
    }
    
    public static Object parseDataType(Element element) {
        final HashMap dataType = new HashMap();
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { dataType.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseDataType has not been type implemented");
        return(dataType);
    }
    
    public static Object parseDataField(Element element) {
        final HashMap dataField = new HashMap();
        dataField.put("Id", element.getAttribute("Id"));
        dataField.put("Name", element.getAttribute("Name"));
        dataField.put("IsArray", element.getAttribute("IsArray"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { dataField.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseDataField has not been type implemented");
        return(dataField);
    }
    
    public static Object parseWorkflowProcess(Element element) {
        final HashMap workflowProcess = new HashMap();
        workflowProcess.put("Id", element.getAttribute("Id"));
        workflowProcess.put("Name", element.getAttribute("Name"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { workflowProcess.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseWorkflowProcess has not been type implemented");
        return(workflowProcess);
    }
    
    public static Object parseProcessHeader(Element element) {
        final HashMap processHeader = new HashMap();
        processHeader.put("DurationUnit", element.getAttribute("DurationUnit"));
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { processHeader.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseProcessHeader has not been type implemented");
        return(processHeader);
    }
    
    public static Object parseTimeEstimation(Element element) {
        final HashMap timeEstimation = new HashMap();
        Node child = element.getFirstChild();
        while(child != null) {
            final String nodeName = child.getNodeName();
            final Object o = parseElement(child);
            if(o != null) { timeEstimation.put(nodeName, o); }
            child = child.getNextSibling();
        }
        cat.debug("parseTimeEstimation has not been type implemented");
        return(timeEstimation);
    }
    
    public static Object parseActivity(Element element) {
        return(parseMappedElement(element));
    }
    
    /** Parses an XPDL Join element into an instance of a Join object. * /
    public static Join parseJoin(Element joinElement) {
        final String ATTR_KEY_TYPE = "Type";
        final String ATTR_VALUE_XOR = "XOR";
        final String ATTR_VALUE_AND = "AND";
        
        final String typeAttr = joinElement.getAttribute(ATTR_KEY_TYPE);
        int type = Join.AND;
        
        if(typeAttr.equals(ATTR_VALUE_XOR)) { type = Join.XOR; }
        
        return(new Join(type));
    }
    */
    
    // ---------------------------------------------------------
    // RUNTIME, TEST, AND SAMPLE METHODS
    // ---------------------------------------------------------
    
    public static void main(String[] args) throws Exception {
        String sampleFileName = "../../docs/examples/sample.xpdl";
        if(args.length > 0) sampleFileName = args[0];
        List values = parseXpdl(UtilURL.fromFilename(sampleFileName), GenericDelegator.getGenericDelegator("default"));
        Iterator viter = values.iterator();
        while(viter.hasNext()) System.out.println(viter.next().toString());
    }
}
