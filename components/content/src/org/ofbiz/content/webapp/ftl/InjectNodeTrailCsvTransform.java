/*
 * $Id: InjectNodeTrailCsvTransform.java,v 1.1 2004/01/13 06:16:30 byersa Exp $
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
 * InjectNodeTrailCsvTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.1 $
 * @since 3.0
 */
public class InjectNodeTrailCsvTransform implements TemplateTransformModel {

    public static final String module = InjectNodeTrailCsvTransform.class.getName();

    public static final String [] saveKeyNames = {"nodeTrailCsv","globalNodeTrail", "nodeTrail"};
    public static final String [] removeKeyNames = {"nodeTrailCsv"};

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
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(I)before save", templateCtx, 0),module);
        final Map savedValues = FreeMarkerWorker.saveValues(templateCtx, saveKeyNames);
        if (Debug.verboseOn()) Debug.logVerbose("(I-0)savedValues: " + savedValues,module);
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(I)after overrride", templateCtx, 0),module);

        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
                if (Debug.verboseOn()) Debug.logVerbose("in InjectNodeTrailCsv, buf:"+buf.toString(),module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                String passedCsv = (String)templateCtx.get("nodeTrailCsv");
                String csvTrail = null;
                List trail = (List)templateCtx.get("globalNodeTrail");
                if (UtilValidate.isNotEmpty(passedCsv)) {
                    csvTrail = passedCsv;
                    int lastComma = passedCsv.lastIndexOf(",");
                    String lastPassedContentId = null;
                    if (lastComma >= 0) { 
                        lastPassedContentId = passedCsv.substring(lastComma + 1);
                    } else {
                        lastPassedContentId = passedCsv;
                    }

                    if (UtilValidate.isNotEmpty(lastPassedContentId)) {
                        if (trail != null && trail.size() > 0) {
                            Map nd = (Map)trail.get(0);
                            String firstTrailContentId = (String)nd.get("contentId");
                            if (UtilValidate.isNotEmpty(firstTrailContentId)
                                && UtilValidate.isNotEmpty(lastPassedContentId)
                                && firstTrailContentId.equals(lastPassedContentId) ) {
                                csvTrail += "," + FreeMarkerWorker.nodeTrailToCsv(trail.subList(1, trail.size()));
                            } else {
                                csvTrail += "," + FreeMarkerWorker.nodeTrailToCsv(trail);
                            }
                        }
                    }
                } else {
                    csvTrail = FreeMarkerWorker.nodeTrailToCsv(trail);
                }
                if (Debug.infoOn()) Debug.logInfo("in InjectNodeTrailCsv, csvTrail:"+csvTrail,module);
                templateCtx.put("nodeTrailCsv", csvTrail);
                return TransformControl.EVALUATE_BODY;
            }


            public void close() throws IOException {
                String wrappedFTL = buf.toString();
                if (Debug.verboseOn()) Debug.logVerbose("in InjectNodeTrailCsv, wrappedFTL:"+wrappedFTL,module);
                out.write(wrappedFTL);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(I)before remove", templateCtx, 0),module);
                    FreeMarkerWorker.removeValues(templateCtx, removeKeyNames);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(I)after remove", templateCtx, 0),module);
                    FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(I)after reload", templateCtx, 0),module);
            }
        };
    }
}
