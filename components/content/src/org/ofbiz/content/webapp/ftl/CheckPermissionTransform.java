/*
 * $Id: CheckPermissionTransform.java,v 1.2 2004/01/09 23:35:26 byersa Exp $
 * 
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *  
 */
package org.ofbiz.content.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentServicesComplex;
import org.ofbiz.content.content.ContentPermissionServices;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.service.ModelService;
import org.ofbiz.security.Security;

import freemarker.template.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;
import freemarker.template.TemplateModelException;

/**
 * CheckPermissionTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.2 $
 * @since 3.0
 */
public class CheckPermissionTransform implements TemplateTransformModel {

    public static final String module = CheckPermissionTransform.class.getName();

    public static final String [] saveKeyNames = {};
    public static final String [] removeKeyNames = {};

    /**
     * A wrapper for the FreeMarkerWorker version.
     */
    public static Object getWrappedObject(String varName, Environment env) {
        return FreeMarkerWorker.getWrappedObject(varName, env);
    }

    public static String getArg(Map args, String key, Environment env) {
        return FreeMarkerWorker.getArg(args, key, env);
    }

    public static String getArg(Map args, String key, Map ctx) {
        return FreeMarkerWorker.getArg(args, key, ctx);
    }


    public Writer getWriter(final Writer out, Map args) {
        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        final Map templateCtx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        //FreeMarkerWorker.convertContext(templateCtx);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        final HttpServletRequest request = (HttpServletRequest) FreeMarkerWorker.getWrappedObject("request", env);
        FreeMarkerWorker.getSiteParameters(request, templateCtx);
        //templateCtx.put("buf", buf);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(C)before save", templateCtx, 0),module);
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(C)after overrride", templateCtx, 0),module);
        final Map savedValues = FreeMarkerWorker.saveValues(templateCtx, saveKeyNames);
        if (Debug.verboseOn()) Debug.logVerbose("(C-0)savedValues: " + savedValues,module);

        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
                if (Debug.verboseOn()) Debug.logVerbose("in CheckPerm, buf:"+buf.toString(),module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                List trail = (List)templateCtx.get("globalNodeTrail");
                List passedGlobalNodeTrail = null;
                if (trail != null) 
                    passedGlobalNodeTrail = new ArrayList(trail);
                else
                    passedGlobalNodeTrail = new ArrayList();
                int sz = passedGlobalNodeTrail.size();

                GenericValue view = null;
                String contentId = (String)templateCtx.get("contentId");
                String subContentId = (String)templateCtx.get("subContentId");
                if (UtilValidate.isNotEmpty(subContentId) || UtilValidate.isNotEmpty(contentId) ) {
                    String thisContentId = contentId;
                    if (UtilValidate.isEmpty(thisContentId)) 
                        thisContentId = subContentId;
        
                    if (UtilValidate.isNotEmpty(thisContentId)) {
                    
                        try {
                            view = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", thisContentId));
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Error getting sub-content", module);
                            throw new RuntimeException(e.getMessage());
                        }
                        passedGlobalNodeTrail.add(FreeMarkerWorker.makeNode(view));
                    }
                } else {
                    if (sz > 0) {
                        view = (GenericValue)passedGlobalNodeTrail.get(sz - 1);
                    }
                }
                if (view == null) {
                    view = delegator.makeValue("Content", null);
                    view.put("ownerContentId", templateCtx.get("ownerContentId"));
                }
        
                GenericValue userLogin = null;
                Security security = null;
                if (request != null) {
                    userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
                    security = (Security) request.getAttribute("security");
                }
                if (userLogin == null) {
                    userLogin = (GenericValue)templateCtx.get("userLogin");
                }
             
                String statusId = (String)templateCtx.get("statusId");
                String targetPurpose = (String)templateCtx.get("purposeTypeId");
                List purposeList = new ArrayList();
                if (UtilValidate.isNotEmpty(targetPurpose))
                    purposeList.add(targetPurpose);
                String entityAction = (String)templateCtx.get("entityAction");
                String targetOperation = (String)templateCtx.get("targetOperation");
                if (UtilValidate.isEmpty(targetOperation)) {
                    if (UtilValidate.isNotEmpty(entityAction))
                        targetOperation = "CONTENT" + entityAction;
                }
                List targetOperationList = new ArrayList();
                if (UtilValidate.isNotEmpty(targetOperation)) 
                    targetOperationList.add(targetOperation);
                List roleList = new ArrayList();
        
                if (Debug.verboseOn()) Debug.logVerbose("in CheckPermission, view" + view,module);
                Map results = ContentPermissionServices.checkPermission(view, statusId, userLogin, purposeList, targetOperationList, roleList, delegator, security, entityAction); 
                if (Debug.verboseOn()) Debug.logVerbose("in CheckPermission, results" + results, module);

                boolean isError = ModelService.RESPOND_ERROR.equals(results.get(ModelService.RESPONSE_MESSAGE));
                if (isError) {
                    throw new IOException(ModelService.RESPONSE_MESSAGE);
                }

                String permissionStatus = (String) results.get("permissionStatus");

                if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
                if (Debug.verboseOn()) Debug.logVerbose("in CheckPermission, permissionStatus" + permissionStatus, module);
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }


            public void close() throws IOException {
                String wrappedFTL = buf.toString();
                if (Debug.verboseOn()) Debug.logVerbose("in CheckPerm, wrappedFTL:"+wrappedFTL,module);
                out.write(wrappedFTL);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(C)before remove", templateCtx, 0),module);
                    FreeMarkerWorker.removeValues(templateCtx, removeKeyNames);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(C)after remove", templateCtx, 0),module);
                    FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(C)after reload", templateCtx, 0),module);
            }
        };
    }
}
