/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.view;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

import dori.jasper.engine.*;

/**
 * Handles JasperReports PDF view rendering
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    July 9, 2002
 *@version    1.0
 */
public class JasperReportsPdfViewHandler implements ViewHandler {

    protected ServletContext context;

    public void init(ServletContext context) throws ViewHandlerException {
        this.context = context;
    }

    public void render(String name, String page, String info, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        // some containers call filters on EVERY request, even forwarded ones,
        // so let it know that it came from the control servlet

        if (request == null) {
            throw new ViewHandlerException("The HttpServletRequest object was null, how did that happen?");
        }
        if (page == null || page.length() == 0) {
            throw new ViewHandlerException("View page was null or empty, but must be specified");
        }
        if (info == null || info.length() == 0) {
            throw new ViewHandlerException("View fnfo string was null or empty, but must be used to specify an Entity that is mapped to the Entity Engine datasource that the report will use.");
        }

        request.setAttribute(SiteDefs.FORWARDED_FROM_CONTROL_SERVLET, new Boolean(true));

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        if (delegator == null) {
            throw new ViewHandlerException("The delegator object was null, how did that happen?");
        }
        
        try {
            String datasourceName = delegator.getEntityHelperName(info);
            InputStream is = context.getResourceAsStream(page);
            Map parameters = UtilMisc.getParameterMap(request);
            
            JasperReport report = JasperCompileManager.compileReport(is);
            response.setContentType("application/pdf");
            
            PipedOutputStream fillToPrintOutputStream = new PipedOutputStream();
            PipedInputStream fillToPrintInputStream = new PipedInputStream(fillToPrintOutputStream);
            //JasperFillManager.fillReportToStream(report, fillToPrintOutputStream, parameters, ConnectionFactory.getConnection(datasourceName));
            //JasperPrintManager.printReportToPdfStream(fillToPrintInputStream, response.getOutputStream());

            JasperPrint jp = JasperManager.fillReport(report,parameters,ConnectionFactory.getConnection(datasourceName));
            if (jp.getPages().size() < 1) {
                throw new ViewHandlerException("Report is Empty (no results?)");
            }
            JasperManager.printReportToPdfStream(jp, response.getOutputStream());
        } catch (IOException ie) {
            throw new ViewHandlerException("IO Error in region", ie);
        } catch (java.sql.SQLException e) {
            throw new ViewHandlerException("Database error while running report", e);
        } catch (Exception e) {
            throw new ViewHandlerException("Error in report", e);
        //} catch (ServletException se) {
        //    throw new ViewHandlerException("Error in region", se.getRootCause());
        }
    }
}
