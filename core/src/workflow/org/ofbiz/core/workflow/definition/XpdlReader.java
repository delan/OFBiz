/* $Id$ */

/*
 * $Log$
 * Revision 1.3  2001/11/28 16:18:16  jonesde
 * Added stuff for start activities and started transition restriction reading
 *
 * Revision 1.2  2001/11/27 15:44:09  jonesde
 * Not much done, mostly small changes as looked for needed entities
 *
 * Revision 1.1  2001/11/26 14:18:41  jonesde
 * Moved XpdlParser code to XpdlReader in order to restore previous XpdlParser, will be developed in parallel
 *
 *
 */

package org.ofbiz.core.workflow.definition;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.net.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

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

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * <p>Reads Process Definition objects from XPDL
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
 * @author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 * @created Sun Nov  4 06:04:33 MST 2001
 * @version 1.0
 */
public class XpdlReader {
    protected GenericDelegator delegator = null;
    protected List values = null;

    // LOG INITIALIZATION
    static {
        Log.init();
    }
    private static final Category cat = Category.getInstance(XpdlReader.class.getName());

    public XpdlReader(GenericDelegator delegator) {
        this.delegator = delegator;
    }

    /** Imports an XPDL file at the given location and imports it into the
     * datasource through the given delegator */
    public static void importXpdl(URL location, GenericDelegator delegator) throws DefinitionParserException {
        List values = readXpdl(location, delegator);
        try {
            delegator.storeAll(values);
        } catch (GenericEntityException e) {
            throw new DefinitionParserException("Could not store values", e);
        }
    }

    /** Gets an XML file from the specified location and reads it into
     * GenericValue objects from the given delegator and returns them in a
     * List; does not write to the database, just gets the entities. */
    public static List readXpdl(URL location, GenericDelegator delegator) throws DefinitionParserException {
        cat.info("Beginning XPDL File Parse: " + location.toString());

        XpdlReader reader = new XpdlReader(delegator);
        try {
            Document document = UtilXml.readXmlDocument(location);
            return reader.readAll(document);
        } catch (ParserConfigurationException e) {
            cat.fatal(e.getMessage(), e);
            throw new DefinitionParserException("Could not configure XML reader", e);
        }
        catch (SAXException e) {
            cat.error(e.getMessage(), e);
            throw new DefinitionParserException("Could not parse XML (invalid?)", e);
        }
        catch (IOException e) {
            cat.error(e.getMessage(), e);
            throw new DefinitionParserException("Could not load file", e);
        }
    }

    public List readAll(Document document) throws DefinitionParserException {
        values = new LinkedList();
        Element docElement;

        docElement = document.getDocumentElement();
        //read the package element, and everything under it
        // puts everything in the values list for returning, etc later
        readPackage(docElement);

        return(values);
    }

    // -------------------------------------------------------
    // Methods for individual entities
    // -------------------------------------------------------

    protected void readPackage(Element packageElement) throws DefinitionParserException {
        if (packageElement == null)
            return;
        if (!"Package".equals(packageElement.getTagName()))
            throw new DefinitionParserException("Tried to make Package from element not named Package");

        GenericValue packageValue = delegator.makeValue("WorkflowPackage", null);
        values.add(packageValue);

        String packageId = packageElement.getAttribute("Id");
        packageValue.set("packageId", packageId);
        packageValue.set("packageName", packageElement.getAttribute("Name"));

        //PackageHeader
        Element packageHeaderElement = UtilXml.firstChildElement(packageElement, "PackageHeader");
        if (packageHeaderElement != null) {
            packageValue.set("specificationId", "XPDL");
            packageValue.set("specificationVersion", UtilXml.childElementValue(packageHeaderElement, "XPDLVersion"));
            packageValue.set("sourceVendorInfo", UtilXml.childElementValue(packageHeaderElement, "Vendor"));
            String createdStr = UtilXml.childElementValue(packageHeaderElement, "Created");
            if (createdStr != null)
                packageValue.set("creationDateTime", java.sql.Timestamp.valueOf(createdStr));
            packageValue.set("description", UtilXml.childElementValue(packageHeaderElement, "Description"));
            packageValue.set("documentationUrl", UtilXml.childElementValue(packageHeaderElement, "Documentation"));
            packageValue.set("priorityUomId", UtilXml.childElementValue(packageHeaderElement, "PriorityUnit"));
            packageValue.set("costUomId", UtilXml.childElementValue(packageHeaderElement, "CostUnit"));
        }

        //RedefinableHeader?
        Element redefinableHeaderElement = UtilXml.firstChildElement(packageElement, "RedefinableHeader");
        readRedefinableHeader(redefinableHeaderElement, packageValue);

        //ConformanceClass?
        Element conformanceClassElement = UtilXml.firstChildElement(packageElement, "ConformanceClass");
        if (conformanceClassElement != null) {
            packageValue.set("graphConformanceEnumId", "WGC_" + conformanceClassElement.getAttribute("GraphConformance"));
        }

        //ExternalPackages?
        Element externalPackagesElement = UtilXml.firstChildElement(packageElement, "ExternalPackages");
        List externalPackages = UtilXml.childElementList(externalPackagesElement, "ExternalPackage");
        readExternalPackages(externalPackages, packageId);

        //TypeDeclarations?
        Element typeDeclarationsElement = UtilXml.firstChildElement(packageElement, "TypeDeclarations");
        List typeDeclarations = UtilXml.childElementList(typeDeclarationsElement, "TypeDeclaration");
        readTypeDeclarations(typeDeclarations, packageId);

        //Participants?
        Element participantsElement = UtilXml.firstChildElement(packageElement, "Participants");
        List participants = UtilXml.childElementList(participantsElement, "Participant");
        readParticipants(participants, packageValue);

        //Applications?
        Element applicationsElement = UtilXml.firstChildElement(packageElement, "Applications");
        List applications = UtilXml.childElementList(applicationsElement, "Application");
        readApplications(applications, packageValue);

        //DataFields?
        Element dataFieldsElement = UtilXml.firstChildElement(packageElement, "DataFields");
        List dataFields = UtilXml.childElementList(dataFieldsElement, "DataField");
        readDataFields(dataFields, packageValue);

        //WorkflowProcesses?
        Element workflowProcessesElement = UtilXml.firstChildElement(packageElement, "WorkflowProcesses");
        List workflowProcesses = UtilXml.childElementList(workflowProcessesElement, "WorkflowProcess");
        readWorkflowProcesses(workflowProcesses, packageId);
    }

    protected void readRedefinableHeader(Element redefinableHeaderElement, GenericValue valueObject) throws DefinitionParserException {
        if (redefinableHeaderElement == null)
            return;

        valueObject.set("author", UtilXml.childElementValue(redefinableHeaderElement, "Author"));
        valueObject.set("objectVersion", UtilXml.childElementValue(redefinableHeaderElement, "Version"));
        valueObject.set("codepage", UtilXml.childElementValue(redefinableHeaderElement, "Codepage"));
        valueObject.set("countryGeoId", UtilXml.childElementValue(redefinableHeaderElement, "Countrykey"));

        valueObject.set("publicationStatusId", "WPS_" + redefinableHeaderElement.getAttribute("PublicationStatus"));

        //Responsibles?
        Element responsiblesElement = UtilXml.firstChildElement(redefinableHeaderElement, "Responsibles");
        List responsibles = UtilXml.childElementList(responsiblesElement, "Responsible");
        readResponsibles(responsibles, valueObject);
    }

    protected void readResponsibles(List responsibles, GenericValue valueObject) throws DefinitionParserException {
        if (responsibles == null || responsibles.size() == 0)
            return;

        Long nextSeqId = delegator.getNextSeqId("WorkflowParticipantList");
        if (nextSeqId == null)
            throw new DefinitionParserException("Could not get next sequence id from data source");
        String responsibleListId = nextSeqId.toString();
        valueObject.set("responsibleListId", responsibleListId);

        Iterator responsibleIter = responsibles.iterator();
        int responsibleIndex = 1;
        while (responsibleIter.hasNext()) {
            Element responsibleElement = (Element) responsibleIter.next();
            String responsibleId = UtilXml.elementValue(responsibleElement);
            GenericValue participantListValue = delegator.makeValue("WorkflowParticipantList", null);
            participantListValue.set("participantListId",responsibleListId);
            participantListValue.set("participantId", responsibleId);
            participantListValue.set("participantIndex", new Long(responsibleIndex));
            values.add(participantListValue);
            responsibleIndex++;
        }
    }

    protected void readExternalPackages(List externalPackages, String packageId) {
        if (externalPackages == null || externalPackages.size() == 0)
            return;
        Iterator externalPackageIter = externalPackages.iterator();
        while (externalPackageIter.hasNext()) {
            Element externalPackageElement = (Element) externalPackageIter.next();
            GenericValue externalPackageValue = delegator.makeValue("WorkflowPackageExternal", null);
            externalPackageValue.set("packageId", packageId);
            externalPackageValue.set("externalPackageId", externalPackageElement.getAttribute("href"));
            values.add(externalPackageValue);
        }
    }

    protected void readTypeDeclarations(List typeDeclarations, String packageId) throws DefinitionParserException {
        if (typeDeclarations == null || typeDeclarations.size() == 0)
            return;
    }

    protected void readParticipants(List participants, GenericValue valueObject) throws DefinitionParserException {
        if (participants == null || participants.size() == 0)
            return;
    }

    protected void readApplications(List applications, GenericValue valueObject) throws DefinitionParserException {
        if (applications == null || applications.size() == 0)
            return;
    }

    protected void readDataFields(List dataFields, GenericValue valueObject) throws DefinitionParserException {
        if (dataFields == null || dataFields.size() == 0)
            return;
    }

    protected void readFormalParameters(List formalParameters, GenericValue valueObject) throws DefinitionParserException {
        if (formalParameters == null || formalParameters.size() == 0)
            return;
    }

    protected void readWorkflowProcesses(List workflowProcesses, String packageId) throws DefinitionParserException {
        if (workflowProcesses == null || workflowProcesses.size() == 0)
            return;
        Iterator workflowProcessIter = workflowProcesses.iterator();
        while (workflowProcessIter.hasNext()) {
            Element workflowProcessElement = (Element) workflowProcessIter.next();
            readWorkflowProcess(workflowProcessElement, packageId);
        }
    }

    protected void readWorkflowProcess(Element workflowProcessElement, String packageId) throws DefinitionParserException {
        GenericValue workflowProcessValue = delegator.makeValue("WorkflowProcess", null);
        values.add(workflowProcessValue);

        String processId = workflowProcessElement.getAttribute("Id");
        workflowProcessValue.set("packageId", packageId);
        workflowProcessValue.set("processId", processId);
        workflowProcessValue.set("objectName", workflowProcessElement.getAttribute("Name"));

        //ProcessHeader
        Element processHeaderElement = UtilXml.firstChildElement(workflowProcessElement, "ProcessHeader");
        if (processHeaderElement != null) {
            //TODO: add prefix to duration Unit or map it to make it a real uomId
            workflowProcessValue.set("durationUomId", processHeaderElement.getAttribute("DurationUnit"));
            String createdStr = UtilXml.childElementValue(processHeaderElement, "Created");
            if (createdStr != null)
                workflowProcessValue.set("creationDateTime", java.sql.Timestamp.valueOf(createdStr));
            workflowProcessValue.set("description", UtilXml.childElementValue(processHeaderElement, "Description"));

            String priorityStr = UtilXml.childElementValue(processHeaderElement, "Priority");
            if (priorityStr != null)
                workflowProcessValue.set("objectPriority", Long.valueOf(priorityStr));
            String limitStr = UtilXml.childElementValue(processHeaderElement, "Limit");
            if (limitStr != null)
                workflowProcessValue.set("timeLimit", Double.valueOf(limitStr));

            String validFromStr = UtilXml.childElementValue(processHeaderElement, "ValidFrom");
            if (validFromStr != null)
                workflowProcessValue.set("validFromDate", java.sql.Timestamp.valueOf(validFromStr));
            String validToStr = UtilXml.childElementValue(processHeaderElement, "ValidTo");
            if (validToStr != null)
                workflowProcessValue.set("validToDate", java.sql.Timestamp.valueOf(validToStr));

            //TimeEstimation?
            Element timeEstimationElement = UtilXml.firstChildElement(processHeaderElement, "TimeEstimation");
            if (timeEstimationElement != null) {
                String waitingTimeStr = UtilXml.childElementValue(timeEstimationElement, "WaitingTime");
                if (waitingTimeStr != null)
                    workflowProcessValue.set("waitingTime", Double.valueOf(waitingTimeStr));
                String workingTimeStr = UtilXml.childElementValue(timeEstimationElement, "WorkingTime");
                if (workingTimeStr != null)
                    workflowProcessValue.set("waitingTime", Double.valueOf(workingTimeStr));
                String durationStr = UtilXml.childElementValue(timeEstimationElement, "Duration");
                if (durationStr != null)
                    workflowProcessValue.set("duration", Double.valueOf(durationStr));
            }
        }

        //RedefinableHeader?
        Element redefinableHeaderElement = UtilXml.firstChildElement(workflowProcessElement, "RedefinableHeader");
        readRedefinableHeader(redefinableHeaderElement, workflowProcessValue);

        //FormalParameters?
        Element formalParametersElement = UtilXml.firstChildElement(workflowProcessElement, "FormalParameters");
        List formalParameters = UtilXml.childElementList(formalParametersElement, "FormalParameter");
        readFormalParameters(formalParameters, workflowProcessValue);

        //(%Type;)*

        //DataFields?
        Element dataFieldsElement = UtilXml.firstChildElement(workflowProcessElement, "DataFields");
        List dataFields = UtilXml.childElementList(dataFieldsElement, "DataField");
        readDataFields(dataFields, workflowProcessValue);

        //Participants?
        Element participantsElement = UtilXml.firstChildElement(workflowProcessElement, "Participants");
        List participants = UtilXml.childElementList(participantsElement, "Participant");
        readParticipants(participants, workflowProcessValue);

        //Applications?
        Element applicationsElement = UtilXml.firstChildElement(workflowProcessElement, "Applications");
        List applications = UtilXml.childElementList(applicationsElement, "Application");
        readApplications(applications, workflowProcessValue);

        //Activities
        Element activitiesElement = UtilXml.firstChildElement(workflowProcessElement, "Activities");
        List activities = UtilXml.childElementList(activitiesElement, "Activity");
        readActivities(activities, packageId, processId, workflowProcessValue);

        //Transitions
        Element transitionsElement = UtilXml.firstChildElement(workflowProcessElement, "Transitions");
        List transitions = UtilXml.childElementList(transitionsElement, "Transition");
        readTransitions(transitions, packageId, processId);
    }

    protected void readActivities(List activities, String packageId, String processId, GenericValue workflowProcessValue) throws DefinitionParserException {
        if (activities == null || activities.size() == 0)
            return;
        Iterator activitiesIter = activities.iterator();
        
        //do the first one differently because it will be the defaultStart activity
        if (activitiesIter.hasNext()) {
            Element activityElement = (Element) activitiesIter.next();
            String activityId = activityElement.getAttribute("Id");
            workflowProcessValue.set("defaultStartActivityId", activityId);
            readActivity(activityElement, packageId, processId);
        }
        
        while (activitiesIter.hasNext()) {
            Element activityElement = (Element) activitiesIter.next();
            readActivity(activityElement, packageId, processId);
        }
    }

    protected void readActivity(Element activityElement, String packageId, String processId) throws DefinitionParserException {
        if (activityElement == null)
            return;

        GenericValue activityValue = delegator.makeValue("WorkflowActivity", null);
        values.add(activityValue);

        String activityId = activityElement.getAttribute("Id");
        activityValue.set("packageId", packageId);
        activityValue.set("processId", processId);
        activityValue.set("activityId", activityId);
        activityValue.set("objectName", activityElement.getAttribute("Name"));

        activityValue.set("description", UtilXml.childElementValue(activityElement, "Description"));
        String limitStr = UtilXml.childElementValue(activityElement, "Limit");
        if (limitStr != null)
            activityValue.set("timeLimit", Double.valueOf(limitStr));

        //(Route | Implementation)
        Element routeElement = UtilXml.firstChildElement(activityElement, "Route");
        Element implementationElement = UtilXml.firstChildElement(activityElement, "Implementation");


        //Performer?
        activityValue.set("performerParticipantId", UtilXml.childElementValue(activityElement, "Performer"));

        //StartMode?
        Element startModeElement = UtilXml.firstChildElement(activityElement, "StartMode");
        if (startModeElement != null) {
            if (UtilXml.firstChildElement(startModeElement, "Automatic") != null)
                activityValue.set("startModeEnumId", "WAM_AUTOMATIC");
            else if (UtilXml.firstChildElement(startModeElement, "Manual") != null)
                activityValue.set("startModeEnumId", "WAM_MANUAL");
            else
                throw new DefinitionParserException("Could not find Mode under StartMode");
        }

        //FinishMode?
        Element finishModeElement = UtilXml.firstChildElement(activityElement, "FinishMode");
        if (finishModeElement != null) {
            if (UtilXml.firstChildElement(finishModeElement, "Automatic") != null)
                activityValue.set("finishModeEnumId", "WAM_AUTOMATIC");
            else if (UtilXml.firstChildElement(finishModeElement, "Manual") != null)
                activityValue.set("finishModeEnumId", "WAM_MANUAL");
            else
                throw new DefinitionParserException("Could not find Mode under FinishMode");
        }

        String priorityStr = UtilXml.childElementValue(activityElement, "Priority");
        if (priorityStr != null)
            activityValue.set("objectPriority", Long.valueOf(priorityStr));


        //SimulationInformation?
        Element simulationInformationElement = UtilXml.firstChildElement(activityElement, "SimulationInformation");
        if (simulationInformationElement != null) {
            if (simulationInformationElement.getAttribute("Instantiation") != null)
                activityValue.set("instantiationLimitEnumId", "WFI_" + simulationInformationElement.getAttribute("Instantiation"));
            String costStr = UtilXml.childElementValue(simulationInformationElement, "Cost");
            if (costStr != null)
                activityValue.set("cost", Double.valueOf(costStr));

            //TimeEstimation
            Element timeEstimationElement = UtilXml.firstChildElement(simulationInformationElement, "TimeEstimation");
            if (timeEstimationElement != null) {
                String waitingTimeStr = UtilXml.childElementValue(timeEstimationElement, "WaitingTime");
                if (waitingTimeStr != null)
                    activityValue.set("waitingTime", Double.valueOf(waitingTimeStr));
                String workingTimeStr = UtilXml.childElementValue(timeEstimationElement, "WorkingTime");
                if (workingTimeStr != null)
                    activityValue.set("waitingTime", Double.valueOf(workingTimeStr));
                String durationStr = UtilXml.childElementValue(timeEstimationElement, "Duration");
                if (durationStr != null)
                    activityValue.set("duration", Double.valueOf(durationStr));
            }
        }


        activityValue.set("iconUrl", UtilXml.childElementValue(activityElement, "Icon"));
        activityValue.set("documentationUrl", UtilXml.childElementValue(activityElement, "Documentation"));

        //TransitionRestrictions?
        Element transitionRestrictionsElement = UtilXml.firstChildElement(activityElement, "TransitionRestrictions");
        List transitionRestrictions = UtilXml.childElementList(transitionRestrictionsElement, "TransitionRestriction");
        readTransitionRestrictions(transitionRestrictions, activityValue);
        
        //for not set the canStart to always be true
        activityValue.set("canStart", "Y");
    }

    protected void readTransitions(List transitions, String packageId, String processId) throws DefinitionParserException {
        if (transitions == null || transitions.size() == 0)
            return;
        Iterator transitionsIter = transitions.iterator();
        while (transitionsIter.hasNext()) {
            Element transitionElement = (Element) transitionsIter.next();
            readTransition(transitionElement, packageId, processId);
        }
    }

    protected void readTransition(Element transitionElement, String packageId, String processId) throws DefinitionParserException {
        GenericValue transitionValue = delegator.makeValue("WorkflowTransition", null);
        values.add(transitionValue);

        String transitionId = transitionElement.getAttribute("Id");
        transitionValue.set("packageId", packageId);
        transitionValue.set("processId", processId);
        transitionValue.set("transitionId", transitionId);
        transitionValue.set("fromActivityId", transitionElement.getAttribute("From"));
        transitionValue.set("toActivityId", transitionElement.getAttribute("To"));

        if (transitionElement.getAttribute("Loop") != null)
            transitionValue.set("loopTypeEnumId", "WTL_" + transitionElement.getAttribute("Loop"));
        else
            transitionValue.set("loopTypeEnumId", "WTL_NOLOOP");

        transitionValue.set("transitionName", transitionElement.getAttribute("Name"));

        //Condition?
        Element conditionElement = UtilXml.firstChildElement(transitionElement, "Condition");
        if (conditionElement != null) {
            if (conditionElement.getAttribute("Type") != null)
                transitionValue.set("loopTypeEnumId", "WTC_" + conditionElement.getAttribute("Type"));
            else
                transitionValue.set("loopTypeEnumId", "WTC_CONDITION");

            //a Condition will have either a list of XPression elements, or plain PCDATA
            List xPressions = UtilXml.childElementList(conditionElement, "XPression");
            if (xPressions != null && xPressions.size() > 0) {
                throw new DefinitionParserException("XPression elements under Condition not yet supported, just use text inside Condition with the expression");
            } else {
                transitionValue.set("conditionExpr", UtilXml.elementValue(conditionElement));
            }
        }

        //Description?
        transitionValue.set("description", UtilXml.childElementValue(transitionElement, "Description"));
    }

    protected void readTransitionRestrictions(List transitionRestrictions, GenericValue activityValue) throws DefinitionParserException {
        if (transitionRestrictions == null || transitionRestrictions.size() == 0)
            return;
        Iterator transitionRestrictionsIter = transitionRestrictions.iterator();
        if (transitionRestrictionsIter.hasNext()) {
            Element transitionRestrictionElement = (Element) transitionRestrictionsIter.next();
            readTransitionRestriction(transitionRestrictionElement, activityValue);
        }
        if (transitionRestrictionsIter.hasNext()) {
            throw new DefinitionParserException("Multiple TransitionRestriction elements found, this is not currently supported. Please remove extras.");
        }
    }

    protected void readTransitionRestriction(Element transitionRestrictionElement, GenericValue activityValue) throws DefinitionParserException {
        String packageId = activityValue.getString("packageId");
        String processId = activityValue.getString("processId");
        String activityId = activityValue.getString("activityId");
        
        //InlineBlock?
        Element inlineBlockElement = UtilXml.firstChildElement(transitionRestrictionElement, "InlineBlock");
        if (inlineBlockElement != null) {
            activityValue.set("isInlineBlock", "Y");
            
        }
/*
      <field name="blockName" type="name"></field>
      <field name="blockDocumentationUrl" type="url"></field>
      <field name="blockIconUrl" type="url"></field>
      <field name="blockDescription" type="description"></field>
      <field name="blockBeginActivityId" type="id-long"></field>
      <field name="blockEndActivityId" type="id-long"></field>
*/        
        //Join?
        Element joinElement = UtilXml.firstChildElement(transitionRestrictionElement, "Join");
        if (joinElement != null) {
            String joinType = joinElement.getAttribute("Type");
            if(joinType != null && joinType.length() > 0) {
                activityValue.set("joinTypeEnumId", "WJT_" + joinType);
            }
        }
        
        //Split?
        Element splitElement = UtilXml.firstChildElement(transitionRestrictionElement, "Split");
        if (splitElement != null) {
            String splitType = splitElement.getAttribute("Type");
            if(splitType != null && splitType.length() > 0) {
                activityValue.set("splitTypeEnumId", "WST_" + splitType);
            }
            
            //TransitionRefs
            Element transitionRefsElement = UtilXml.firstChildElement(transitionRestrictionElement, "TransitionRefs");
            List transitionRefs = UtilXml.childElementList(transitionRefsElement, "TransitionRef");
            readTransitionRefs(transitionRefs, packageId, processId, activityId);
        }
    }
    
    protected void readTransitionRefs(List transitionRefs, String packageId, String processId, String activityId) throws DefinitionParserException {
        if (transitionRefs == null || transitionRefs.size() == 0)
            return;
        Iterator transitionRefsIter = transitionRefs.iterator();
        while (transitionRefsIter.hasNext()) {
            Element transitionRefElement = (Element) transitionRefsIter.next();
            GenericValue transitionRefValue = delegator.makeValue("WorkflowTransitionRef", null);
            values.add(transitionRefValue);
    
            transitionRefValue.set("packageId", packageId);
            transitionRefValue.set("processId", processId);
            transitionRefValue.set("activityId", activityId);
            transitionRefValue.set("transitionId", transitionRefElement.getAttribute("Id"));
        }
    }

    // ---------------------------------------------------------
    // RUNTIME, TEST, AND SAMPLE METHODS
    // ---------------------------------------------------------

    public static void main(String[] args) throws Exception {
        String sampleFileName = "../../docs/examples/sample.xpdl";
        if (args.length > 0)
            sampleFileName = args[0];
        List values = readXpdl(UtilURL.fromFilename(sampleFileName), GenericDelegator.getGenericDelegator("default"));
        Iterator viter = values.iterator();
        while (viter.hasNext())
            System.out.println(viter.next().toString());
    }
}


