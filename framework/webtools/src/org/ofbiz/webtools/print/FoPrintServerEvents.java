/*
 * $Id$
 *
 * Copyright 2001-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.webtools.print;

import java.util.Map;
import java.util.Locale;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.widget.screen.ScreenRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;

/**
 * FoPrintServerEvents
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      May 18, 2006
 */

public class FoPrintServerEvents {

    public static final String module = FoPrintServerEvents.class.getName();
    private static HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();

    public static String getXslFo(HttpServletRequest req, HttpServletResponse resp) {
        LocalDispatcher dispatcher = (LocalDispatcher) req.getAttribute("dispatcher");
        Map reqParams = UtilHttp.getParameterMap(req);
        reqParams.put("locale", UtilHttp.getLocale(req));

        String screenUri = (String) reqParams.remove("screenUri");
        if (screenUri != null && reqParams.size() > 0) {
            String base64String = null;
            try {
                byte[] bytes = FoPrintServerEvents.getXslFo(dispatcher.getDispatchContext(), screenUri, reqParams);
                base64String = new String(Base64.encodeBase64(bytes));
            } catch (GeneralException e) {
                Debug.logError(e, module);
                try {
                    resp.sendError(500);
                } catch (IOException e1) {
                    Debug.logError(e1, module);
                }
            }
            if (base64String != null) {
                try {
                    Writer out = resp.getWriter();
                    out.write(base64String);
                } catch (IOException e) {
                    try {
                        resp.sendError(500);
                    } catch (IOException e1) {
                        Debug.logError(e1, module);
                    }
                }
            }
        }

        return null;
    }

    public static byte[] getXslFo(DispatchContext dctx, String screen, Map parameters) throws GeneralException {
        // run as the system user
        GenericValue system = null;
        try {
            system = dctx.getDelegator().findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
        } catch (GenericEntityException e) {
            throw new GeneralException(e.getMessage(), e);
        }
        parameters.put("userLogin", system);
        if (!parameters.containsKey("locale")) {
            parameters.put("locale", Locale.getDefault());
        }

        // render and obtain the XSL-FO
        Writer writer = new StringWriter();
        try {
            ScreenRenderer screens = new ScreenRenderer(writer, null, htmlScreenRenderer);
            screens.populateContextForService(dctx, parameters);
            screens.render(screen);
        } catch (Throwable t) {
            throw new GeneralException("Problems rendering FOP XSL-FO", t);
        }
        return writer.toString().getBytes();
    }
}
