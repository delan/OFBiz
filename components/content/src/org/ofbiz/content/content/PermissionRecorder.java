/*
 * $Id: PermissionRecorder.java,v 1.2 2004/03/24 16:04:17 byersa Exp $
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.content.content;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * PermissionRecorder Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.2 $
 * @since      2.2
 * 
 * Services for granting operation permissions on Content entities in a data-driven manner.
 */
public class PermissionRecorder {

    public static final int PRE_PURPOSE = 0;
    public static final int PRE_ROLE = 1;
    public static final int WITH_ROLES = 2;

    protected boolean isOn = false;
    protected GenericValue userLogin;
    protected List permCheckResults = new ArrayList();
    protected boolean entityPermCheckResult = false;
    protected String currentContentId = "";
    protected Map currentContentMap;
    protected String privilegeEnumId;
    protected int currentCheckMode;
    protected GenericValue [] contentPurposeOperations;
    protected String [] statusTargets;
    protected String [] targetOperations;

    public static final String module = PermissionRecorder.class.getName();

    public static final String [] opFields = { "contentPurposeTypeId", "contentOperationId", "roleTypeId", "statusId", "privilegeEnumId"};
    public static final String [] fieldTitles = { "Purpose", "Operation", "Role", "Status", "Privilege"};

    public PermissionRecorder() {
        isOn = UtilProperties.propertyValueEqualsIgnoreCase("content.properties", "permissionRecorderOn", "true");
    }

    public void setCheckMode(int val) {
        currentCheckMode = val;
    }

    public int getCheckMode() {
        return currentCheckMode;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean b) {
        isOn = b;
    }

    public void setUserLogin(GenericValue user) {
        userLogin = user;
    }

    public GenericValue getUserLogin() {
        return userLogin;
    }

    public boolean getEntityPermCheckResult() {
        return entityPermCheckResult;
    }

    public void setEntityPermCheckResult(boolean b) {
        entityPermCheckResult = b;
    }

    public GenericValue [] getContentPurposeOperations() {
       return contentPurposeOperations;
    }

    public void setContentPurposeOperations(List opList) {
       contentPurposeOperations = (GenericValue [])opList.toArray();
    }

    public void setPrivilegeEnumId(String id) {
        privilegeEnumId = id;
    }

    public String getPrivilegeEnumId() {
        return privilegeEnumId;
    }

    public String [] getStatusTargets() {
       return statusTargets;
    }

    public void setStatusTargets(List opList) {
       statusTargets = (String [])opList.toArray();
    }

    public String [] getTargetOperations() {
       return targetOperations;
    }

    public void setTargetOperations(List opList) {
       targetOperations = (String [])opList.toArray();
    }

    public void setCurrentContentId(String id) {
        if (!currentContentId.equals(id)) {
            currentContentMap = new HashMap();
            permCheckResults.add(currentContentMap);
            currentContentMap.put("contentId", id);            
            currentContentMap.put("checkResults", new ArrayList());            
        }
        currentContentId = id;
    }

    public String getCurrentContentId() {
        return currentContentId;
    }

    public void setRoles(List roles) {
        if (currentContentMap != null) {
            if (roles != null) 
                currentContentMap.put("roles", roles.toArray());
            else
                currentContentMap.put("roles", null);
        }
    }

    public void setPurposes(List purposes) {
        if (currentContentMap != null) {
            if (purposes != null) 
                currentContentMap.put("purposes", purposes.toArray());
            else
                currentContentMap.put("purposes", null);
        }
    }

    public void startMatchGroup(List targetOperations, List purposes, List roles, List targStatusList, String targPrivilegeEnumId, String contentId) {
        currentContentMap = new HashMap();
        permCheckResults.add(currentContentMap);
        String s = null;
        if (targetOperations != null) {
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, targetOperations:" + targetOperations, module);
            s = targetOperations.toString();
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, targetOperations(string):" + s, module);
            currentContentMap.put("contentOperationId", s);
        }
        if (purposes != null) {
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, purposes:" + purposes, module);
            s = purposes.toString();
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, purposes(string):" + s, module);
            currentContentMap.put("contentPurposeTypeId", s);
        }
        if (roles != null) {
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, roles:" + roles, module);
            s = roles.toString();
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, roles(string):" + s, module);
            currentContentMap.put("roleTypeId", s);
        }
        if (targStatusList != null) {
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, targStatusList:" + targStatusList, module);
            s = targStatusList.toString();
            //if (Debug.infoOn()) Debug.logInfo("startMatchGroup, targStatusList(string):" + s, module);
            currentContentMap.put("statusId", s);
        }
        currentContentMap.put("privilegeEnumId", privilegeEnumId);
        currentContentMap.put("contentId", contentId);
        currentContentMap.put("checkResultList", new ArrayList());
        currentContentMap.put("matches", null);
    }

    public void record(GenericValue purposeOp, boolean targetOpCond, boolean purposeCond, boolean statusCond, boolean privilegeCond, boolean roleCond) {

        Map map = new HashMap(purposeOp);
        map.put("contentOperationIdCond", new Boolean(targetOpCond));
        map.put("contentPurposeTypeIdCond", new Boolean(purposeCond));
        map.put("statusIdCond", new Boolean(statusCond));
        map.put("privilegeEnumIdCond", new Boolean(privilegeCond));
        map.put("roleTypeIdCond", new Boolean(roleCond));
        map.put("contentId", currentContentId);
        ((List)currentContentMap.get("checkResultList")).add(map);
    }

    public String toHtml() {
        StringBuffer sb = new StringBuffer();
        sb.append("<style type=\"text/css\">");
        sb.append(".pass {background-color:lime; font-family:Verdana,Arial,sans-serif; font-size:10px; }");
        sb.append(".fail {background-color:red; font-family:Verdana,Arial,sans-serif; font-size:10px; }");
        sb.append(".target {background-color:lightgrey; font-family:Verdana,Arial,sans-serif; font-size:10px; }");
        sb.append(".headr {background-color:white; font-weight:bold; font-family:Verdana,Arial,sans-serif; font-size:12px; }");
        sb.append("</style>");

        //if (Debug.infoOn()) Debug.logInfo("toHtml, style:" + sb.toString(), module);
        sb.append("<table border=\"1\" >");
        // Do header row
        sb.append("<tr>");

        sb.append("<td class=\"headr\">");
        sb.append("Content Id");
        sb.append("</td>");

        //if (Debug.infoOn()) Debug.logInfo("renderResultRowHtml, (1):" + sb.toString(), module);
        String str = null;
        String s = null;
        for (int i=0; i < fieldTitles.length; i++) {
            String opField = (String)fieldTitles[i];
            sb.append("<td class=\"headr\">");
            sb.append(opField);
            sb.append("</td>");
        }
        sb.append("<td class=\"headr\" >Pass/Fail</td>");
        sb.append("</tr>");

        Iterator iter = permCheckResults.iterator();
        while (iter.hasNext()) {
            Map cMap = (Map)iter.next();
            sb.append(renderCurrentContentMapHtml(cMap));
        }
        sb.append("</table>");
        return sb.toString();
    }

    public String renderCurrentContentMapHtml(Map cMap) {
        StringBuffer sb = new StringBuffer();
        List resultList = (List)cMap.get("checkResultList");
        Iterator iter = resultList.iterator();
        while (iter.hasNext()) {
            Map rMap = (Map)iter.next();
            sb.append(renderResultRowHtml(rMap, cMap));
        }
 
        return sb.toString();
    }

    //public static final String [] opFields = { "contentPurposeTypeId", "contentOperationId", "roleTypeId", "statusId", "privilegeEnumId"};

    public String renderResultRowHtml(Map rMap, Map currentContentResultMap ) {
        StringBuffer sb = new StringBuffer();

        // Do target row
        sb.append("<tr>");

        sb.append("<td class=\"target\">");
        sb.append((String)currentContentResultMap.get("contentId"));
        sb.append("</td>");

        //if (Debug.infoOn()) Debug.logInfo("renderResultRowHtml, (1):" + sb.toString(), module);
        String str = null;
        String s = null;
        for (int i=0; i < opFields.length; i++) {
            String opField = (String)opFields[i];
            sb.append("<td class=\"target\">");
            s = (String)currentContentResultMap.get(opField);
            if (s != null)
                str = s;
            else
                str = "&nbsp;";
            sb.append(str);
            sb.append("</td>");
        }
        sb.append("<td class=\"target\" >&nbsp;</td>");
        sb.append("</tr>");

        //if (Debug.infoOn()) Debug.logInfo("renderResultRowHtml, (2):" + sb.toString(), module);
        // Do UUT row
        sb.append("<tr>");

        sb.append("<td class=\"target\">");
        sb.append((String)currentContentResultMap.get("contentId"));
        sb.append("</td>");

        boolean isPass = true;
        for (int i=0; i < opFields.length; i++) {
            String opField = (String)opFields[i];
            Boolean bool = (Boolean)rMap.get(opField + "Cond");
            String cls = (bool.booleanValue()) ? "pass" : "fail";
            if (!bool.booleanValue())
                isPass = false;
            sb.append("<td class=\"" + cls + "\">");
        //if (Debug.infoOn()) Debug.logInfo("renderResultRowHtml, (2b):" + sb.toString(), module);
            s = (String)rMap.get(opField);
        //if (Debug.infoOn()) Debug.logInfo("renderResultRowHtml, (s):" + s,  module);
            sb.append(s);
            sb.append("</td>");
        }
        String passFailCls = (isPass) ? "pass" : "fail";
        sb.append("<td class=\"" + passFailCls +"\">" + passFailCls.toUpperCase() + "</td>");
        sb.append("</tr>");
        //if (Debug.infoOn()) Debug.logInfo("renderResultRowHtml, (3):" + sb.toString(), module);

        return sb.toString();
    }
}

