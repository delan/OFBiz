/*
 * $Id$
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 */
package org.ofbiz.content.widget;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.taglib.ContentUrlTag;

public class WidgetWorker {

    public static final String module = WidgetWorker.class.getName();

    public WidgetWorker () {}

    public static void buildHyperlinkUrl(StringBuffer buffer, String requestName, String targetType, HttpServletRequest request, HttpServletResponse response, Map context) {

        if ("intra-app".equals(targetType)) {
            appendOfbizUrl(buffer, "/" + requestName, request, response);
        } else if ("inter-app".equals(targetType)) {
            String fullTarget = requestName;
            buffer.append(fullTarget);
            String externalLoginKey = (String) request.getAttribute("externalLoginKey");
            if (UtilValidate.isNotEmpty(externalLoginKey)) {
                if (fullTarget.indexOf('?') == -1) {
                    buffer.append('?');
                } else {
                    buffer.append('&');
                }
                buffer.append("externalLoginKey=");
                buffer.append(externalLoginKey);
            }
        } else if ("content".equals(targetType)) {
            appendContentUrl(buffer, requestName, request);
        } else if ("plain".equals(targetType)) {
            buffer.append(requestName);
        } else {
            buffer.append(requestName);
        }

    
        return;
    }

    public static void appendOfbizUrl(StringBuffer buffer, String location, HttpServletRequest request, HttpServletResponse response) {
        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
        // make and append the link
        buffer.append(rh.makeLink(request, response, location));
    }

    public static void appendContentUrl(StringBuffer buffer, String location, HttpServletRequest request) {
        ContentUrlTag.appendContentPrefix(request, buffer);
        buffer.append(location);
    }

    public static void makeHyperlinkString(StringBuffer buffer, String linkStyle, String targetType, String target, String description, HttpServletRequest request, HttpServletResponse response, Map context) {
        buffer.append("<a");

        if (UtilValidate.isNotEmpty(linkStyle)) {
            buffer.append(" class=\"");
            buffer.append(linkStyle);
            buffer.append("\"");
        }

        buffer.append(" href=\"");

        WidgetWorker.buildHyperlinkUrl(buffer, target, targetType, request, response, context);

        buffer.append("\"");

        buffer.append('>');

        buffer.append(description);
        buffer.append("</a>");
    }
}
