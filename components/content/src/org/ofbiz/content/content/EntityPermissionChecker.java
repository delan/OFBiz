/*
 * $Id$
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.w3c.dom.Element;


/**
 * EntityPermissionChecker Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev$
 * @since      3.1
 * 
 * Services for granting operation permissions on Content entities in a data-driven manner.
 */
public class EntityPermissionChecker {

    public static final String module = EntityPermissionChecker.class.getName();

    protected FlexibleStringExpander entityIdExdr;
    protected FlexibleStringExpander entityNameExdr;
    protected boolean displayFailCond;
    protected List targetOperationList;
    protected ContentPermissionServices.PermissionConditionGetter permissionConditionGetter;
    protected ContentPermissionServices.RelatedRoleGetter relatedRoleGetter;
    protected ContentPermissionServices.AuxiliaryValueGetter auxiliaryValueGetter;
    
    public EntityPermissionChecker(Element element) {
        this.entityNameExdr = new FlexibleStringExpander(element.getAttribute("entity-name"));
        this.entityIdExdr = new FlexibleStringExpander(element.getAttribute("entity-id"));
        this.displayFailCond = "true".equals(element.getAttribute("display-fail-cond"));
        Element permissionConditionElement = UtilXml.firstChildElement(element, "permission-condition-getter");
        if (permissionConditionElement == null) {
            permissionConditionGetter = new ContentPermissionServices.StdPermissionConditionGetter();   
        } else {
            permissionConditionGetter = new ContentPermissionServices.StdPermissionConditionGetter(permissionConditionElement);   
        }
        Element auxiliaryValueElement = UtilXml.firstChildElement(element, "auxiliary-value-getter");
        if (auxiliaryValueElement == null) {
            auxiliaryValueGetter = new ContentPermissionServices.StdAuxiliaryValueGetter();   
        } else {
            auxiliaryValueGetter = new ContentPermissionServices.StdAuxiliaryValueGetter(auxiliaryValueElement);   
        }
        Element relatedRoleElement = UtilXml.firstChildElement(element, "related-role-getter");
        if (relatedRoleElement == null) {
            relatedRoleGetter = new ContentPermissionServices.StdRelatedRoleGetter();   
        } else {
            relatedRoleGetter = new ContentPermissionServices.StdRelatedRoleGetter(relatedRoleElement);   
        }
        String targetOperationString = new String(element.getAttribute("target-operation"));
        if (UtilValidate.isNotEmpty(targetOperationString)) {
            List operationsFromString = StringUtil.split(targetOperationString, "|");
            if (targetOperationList == null) {
                targetOperationList = new ArrayList();
            }
            targetOperationList.addAll(operationsFromString);
        }
        permissionConditionGetter.setOperationList(targetOperationList);

        return;
    }

    public boolean runPermissionCheck(Map context) {
    	
    	boolean passed = false;
    	String idString = entityIdExdr.expandString(context);
    	List entityIdList = null;
        if (UtilValidate.isNotEmpty(idString)) {
            entityIdList = StringUtil.split(idString, "|");
        } else {
        	entityIdList = new ArrayList();
        }
    	String entityName = entityNameExdr.expandString(context);
        HttpServletRequest request = (HttpServletRequest)context.get("request");
        GenericValue userLogin = null;
        String userLoginId = null; 
        String partyId = null; 
        GenericDelegator delegator = null;
        if (request != null) {
            HttpSession session = request.getSession();
            userLogin = (GenericValue)session.getAttribute("userLogin");
            if (userLogin != null) {
            	userLoginId = userLogin.getString("userLoginId");
            	partyId = userLogin.getString("partyId");
            }
           delegator = (GenericDelegator)request.getAttribute("delegator");
        }
        
        if (auxiliaryValueGetter != null) auxiliaryValueGetter.clearList();
        if (relatedRoleGetter != null) relatedRoleGetter.clearList();
    	try {
            permissionConditionGetter.init(delegator);
    		passed = ContentPermissionServices.checkPermissionMethod(delegator, partyId,  entityName, entityIdList, auxiliaryValueGetter, relatedRoleGetter, permissionConditionGetter);
                if (!passed && displayFailCond) {
                     String errMsg =  "Permission is denied. \nThese are the conditions of which one must be met:\n"
                     + permissionConditionGetter.dumpAsText();
                     List errorMessageList = (List)context.get("errorMessageList");
                     errorMessageList.add(errMsg);
                }
    	} catch(GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
    	}
        return passed;
    }
    
}
