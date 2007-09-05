/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.webapp.event;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

/**
 * DojoJSONServiceEventHandler - JSON Object Wrapper around the ServiceEventHandler
 * 
 * This handles two issues with the Dojo version of JSON. Actually, it may only applies when a form is used to upload a file.
 * Dojo expects the json response to be wrapped in html like this:
 * <html><head></head><body><textarea style="width: 100%%; height: 100px;">{name: value, name2: value2}</textarea></body></html>
 * The other issue is that the JSONObject balks at the "byte" type of uploaded file data.
 * I solved that by making the parameters returned by the service (which also contains the input parameters)
 * valid for the output parameters defined by the service.
 */
public class DojoJSONServiceEventHandler implements EventHandler {

    public static final String module = DojoJSONServiceEventHandler.class.getName();
    protected EventHandler service;

    public void init(ServletContext context) throws EventHandlerException {
        this.service = new ServiceEventHandler();
        this.service.init(context);
    }

    public String invoke(String eventPath, String eventMethod, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        // call into the service handler for parameters parsing and invocation
        String respCode = service.invoke(eventPath, eventMethod, request, response);

        // pull out the service response from the request attribute
        Map attrMap = getAttributesAsMap(request);

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            throw new EventHandlerException("The local service dispatcher is null");
        }
        DispatchContext dctx = dispatcher.getDispatchContext();
        if (dctx == null) {
            throw new EventHandlerException("Dispatch context cannot be found");
        }

        // get the details for the service(s) to call
        //String mode = SYNC;
        String serviceName = null;
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        // nake sure we have a defined service to call
        serviceName = eventMethod;
        if (serviceName == null) {
            throw new EventHandlerException("Service name (eventMethod) cannot be null");
        }
        ModelService model = null;

        try {
            model = dctx.getModelService(serviceName);
        } catch (GenericServiceException e) {
            throw new EventHandlerException("Problems getting the service model", e);
        }

        if (model == null) {
            throw new EventHandlerException("Problems getting the service model");
        }
        List errorMessages = new LinkedList();
        Map serviceContext = new HashMap();
        serviceContext = model.makeValid(attrMap, ModelService.OUT_PARAM, true, errorMessages, timeZone, locale);

        // create a JSON Object for return
        JSONObject json = JSONObject.fromObject(serviceContext);
        String jsonStr = json.toString();
        if (jsonStr == null) {
            throw new EventHandlerException("JSON Object was empty; fatal error!");
        }
 
        String htmlJsonStr = "<html><head></head><body><textarea style=\"width: 100%%; height: 100px;\">" + jsonStr + "</textarea></body></html>";
        // set the X-JSON content type
        response.setContentType("text/html");
        response.setContentLength(htmlJsonStr.length());

        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(htmlJsonStr);
            out.flush();
        } catch (IOException e) {
            throw new EventHandlerException("Unable to get response writer", e);
        }

        return respCode;
    }

    private Map getAttributesAsMap(HttpServletRequest request) {
        Map attrMap = FastMap.newInstance();
        Enumeration en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            Object val = request.getAttribute(name);
            if (val instanceof java.sql.Timestamp) {
                val = val.toString();
            }
            if (val instanceof String || val instanceof Number || val instanceof Map || val instanceof List) {
                if (Debug.verboseOn()) Debug.logVerbose("Adding attribute to JSON output: " + name, module);
                attrMap.put(name, val);
            }
        }

        return attrMap;
    }
}
