/* $Id$ */
/* $Source$ */

/*
 * $Log$
 * Revision 1.9  2001/11/24 10:20:55  jonesde
 * Basic structure in place, now just need to fill in the gaps, lots of gaps
 *
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
    static {
        Log.init();
    }
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
        try {
            delegator.storeAll(values);
        } catch (GenericEntityException e) {
            throw new DefinitionParserException("Could not store values", e);
        }
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
        } catch (ParserConfigurationException e) {
            cat.fatal(e.getMessage(), e);
            throw new DefinitionParserException("Could not configure XML parser", e);
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
        parseRedefinableHeader(redefinableHeaderElement, packageValue);

        //ConformanceClass?
        Element conformanceClassElement = UtilXml.firstChildElement(packageElement, "ConformanceClass");
        if (conformanceClassElement != null) {
            packageValue.set("graphConformanceEnumId", "WGC_" + conformanceClassElement.getAttribute("GraphConformance"));
        }

        //ExternalPackages?
        Element externalPackagesElement = UtilXml.firstChildElement(packageElement, "ExternalPackages");
        List externalPackages = UtilXml.childElementList(externalPackagesElement, "ExternalPackage");
        parseExternalPackages(externalPackages, packageId);

        //TypeDeclarations?
        Element typeDeclarationsElement = UtilXml.firstChildElement(packageElement, "TypeDeclarations");
        List typeDeclarations = UtilXml.childElementList(typeDeclarationsElement, "TypeDeclaration");
        parseTypeDeclarations(typeDeclarations, packageId);

        //Participants?
        Element participantsElement = UtilXml.firstChildElement(packageElement, "Participants");
        List participants = UtilXml.childElementList(participantsElement, "Participant");
        parseParticipants(participants, packageValue);

        //Applications?
        Element applicationsElement = UtilXml.firstChildElement(packageElement, "Applications");
        List applications = UtilXml.childElementList(applicationsElement, "Application");
        parseApplications(applications, packageValue);

        //DataFields?
        Element dataFieldsElement = UtilXml.firstChildElement(packageElement, "DataFields");
        List dataFields = UtilXml.childElementList(dataFieldsElement, "DataField");
        parseDataFields(dataFields, packageValue);

        //WorkflowProcesses?
        Element workflowProcessesElement = UtilXml.firstChildElement(packageElement, "WorkflowProcesses");
        List workflowProcesses = UtilXml.childElementList(workflowProcessesElement, "WorkflowProcess");
        parseWorkflowProcesses(workflowProcesses, packageId);
    }

    protected void parseRedefinableHeader(Element redefinableHeaderElement, GenericValue valueObject) throws DefinitionParserException {
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
        parseResponsibles(responsibles, valueObject);
    }

    protected void parseResponsibles(List responsibles, GenericValue valueObject) throws DefinitionParserException {
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

    protected void parseExternalPackages(List externalPackages, String packageId) {
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

    protected void parseTypeDeclarations(List typeDeclarations, String packageId) throws DefinitionParserException {
        if (typeDeclarations == null || typeDeclarations.size() == 0)
            return;
    }

    protected void parseParticipants(List participants, GenericValue valueObject) throws DefinitionParserException {
        if (participants == null || participants.size() == 0)
            return;
    }

    protected void parseApplications(List applications, GenericValue valueObject) throws DefinitionParserException {
        if (applications == null || applications.size() == 0)
            return;
    }

    protected void parseDataFields(List dataFields, GenericValue valueObject) throws DefinitionParserException {
        if (dataFields == null || dataFields.size() == 0)
            return;
    }

    protected void parseFormalParameters(List formalParameters, GenericValue valueObject) throws DefinitionParserException {
        if (formalParameters == null || formalParameters.size() == 0)
            return;
    }

    protected void parseWorkflowProcesses(List workflowProcesses, String packageId) throws DefinitionParserException {
        if (workflowProcesses == null || workflowProcesses.size() == 0)
            return;
        Iterator workflowProcessIter = workflowProcesses.iterator();
        while (workflowProcessIter.hasNext()) {
            Element workflowProcessElement = (Element) workflowProcessIter.next();
            parseWorkflowProcess(workflowProcessElement, packageId);
        }
    }

    protected void parseWorkflowProcess(Element workflowProcessElement, String packageId) throws DefinitionParserException {
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
        parseRedefinableHeader(redefinableHeaderElement, workflowProcessValue);

        //FormalParameters?
        Element formalParametersElement = UtilXml.firstChildElement(workflowProcessElement, "FormalParameters");
        List formalParameters = UtilXml.childElementList(formalParametersElement, "FormalParameter");
        parseFormalParameters(formalParameters, workflowProcessValue);

        //(%Type;)*

        //DataFields?
        Element dataFieldsElement = UtilXml.firstChildElement(workflowProcessElement, "DataFields");
        List dataFields = UtilXml.childElementList(dataFieldsElement, "DataField");
        parseDataFields(dataFields, workflowProcessValue);

        //Participants?
        Element participantsElement = UtilXml.firstChildElement(workflowProcessElement, "Participants");
        List participants = UtilXml.childElementList(participantsElement, "Participant");
        parseParticipants(participants, workflowProcessValue);

        //Applications?
        Element applicationsElement = UtilXml.firstChildElement(workflowProcessElement, "Applications");
        List applications = UtilXml.childElementList(applicationsElement, "Application");
        parseApplications(applications, workflowProcessValue);

        //Activities
        Element activitiesElement = UtilXml.firstChildElement(workflowProcessElement, "Activities");
        List activities = UtilXml.childElementList(activitiesElement, "Activity");
        parseActivities(activities, packageId, processId);

        //Transitions
        Element transitionsElement = UtilXml.firstChildElement(workflowProcessElement, "Transitions");
        List transitions = UtilXml.childElementList(transitionsElement, "Transition");
        parseTransitions(transitions, packageId, processId);
    }

    protected void parseActivities(List activities, String packageId, String processId) throws DefinitionParserException {
        if (activities == null || activities.size() == 0)
            return;
        Iterator activitiesIter = activities.iterator();
        while (activitiesIter.hasNext()) {
            Element activityElement = (Element) activitiesIter.next();
            parseActivity(activityElement, packageId, processId);
        }
    }

    protected void parseActivity(Element activityElement, String packageId, String processId) throws DefinitionParserException {
        if (activityElement == null)
            return;

        GenericValue activityValue = delegator.makeValue("WorkflowActivity", null);
        values.add(activityValue);

        activityValue.set("packageId", packageId);
        activityValue.set("processId", processId);
        activityValue.set("activityId", activityElement.getAttribute("Id"));
        activityValue.set("objectName", activityElement.getAttribute("Name"));

        activityValue.set("description", UtilXml.childElementValue(activityElement, "Description"));
        String limitStr = UtilXml.childElementValue(activityElement, "Limit");
        if (limitStr != null)
            activityValue.set("timeLimit", Double.valueOf(limitStr));

        //(Route | Implementation)


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


    }

    protected void parseTransitions(List transitions, String packageId, String processId) throws DefinitionParserException {
        if (transitions == null || transitions.size() == 0)
            return;
        Iterator transitionsIter = transitions.iterator();
        while (transitionsIter.hasNext()) {
            Element transitionElement = (Element) transitionsIter.next();
            parseTransition(transitionElement, packageId, processId);
        }
    }

    protected void parseTransition(Element transitionElement, String packageId, String processId) throws DefinitionParserException {
        GenericValue transitionValue = delegator.makeValue("WorkflowTransition", null);
        values.add(transitionValue);

        transitionValue.set("packageId", packageId);
        transitionValue.set("processId", processId);
        transitionValue.set("transitionId", transitionElement.getAttribute("Id"));
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

    // ---------------------------------------------------------
    // RUNTIME, TEST, AND SAMPLE METHODS
    // ---------------------------------------------------------

    public static void main(String[] args) throws Exception {
        String sampleFileName = "../../docs/examples/sample.xpdl";
        if (args.length > 0)
            sampleFileName = args[0];
        List values = parseXpdl(UtilURL.fromFilename(sampleFileName), GenericDelegator.getGenericDelegator("default"));
        Iterator viter = values.iterator();
        while (viter.hasNext())
            System.out.println(viter.next().toString());
    }
}




