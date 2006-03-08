/*
 * $Id: $
 *
 * Copyright 2006-2006 The Apache Software Foundation
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
package org.ofbiz.order.order;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Order Events
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 */
public class OrderEvents {

    public static final String module = OrderEvents.class.getName();

    public static String downloadDigitalProduct(HttpServletRequest request, HttpServletResponse response) {
    	HttpSession session = request.getSession();
    	ServletContext application = session.getServletContext();
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	String dataResourceId = request.getParameter("dataResourceId");
    	
    	try {
			// has the userLogin.partyId ordered a product with DIGITAL_DOWNLOAD content associated for the given dataResourceId?
			List orderItemAndProductContentInfoList = delegator.findByAnd("OrderItemAndProductContentInfo", 
					UtilMisc.toMap("partyId", userLogin.get("partyId"), "dataResourceId", dataResourceId, "productContentTypeId", "DIGITAL_DOWNLOAD"));
			
			if (orderItemAndProductContentInfoList.size() == 0) {
				request.setAttribute("_ERROR_MESSAGE_", "No record of purchase for digital download found (dataResourceId=[" + dataResourceId + "]).");
				return "error";
			}
			
			GenericValue orderItemAndProductContentInfo = (GenericValue) orderItemAndProductContentInfoList.get(0);
			
	    	// TODO: check validity based on ProductContent fields: useCountLimit, useTime/useTimeUomId
	    	
			if (orderItemAndProductContentInfo.getString("mimeTypeId") != null) {
				response.setContentType(orderItemAndProductContentInfo.getString("mimeTypeId"));
			}
			OutputStream os = response.getOutputStream();
			DataResourceWorker.streamDataResource(os, delegator, dataResourceId, "", application.getInitParameter("webSiteId"), UtilHttp.getLocale(request), application.getRealPath("/"));
			os.flush();
		} catch (GenericEntityException e) {
			String errMsg = "Error downloading digital product content: " + e.toString();
			Debug.logError(e, errMsg, module);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		} catch (GeneralException e) {
			String errMsg = "Error downloading digital product content: " + e.toString();
			Debug.logError(e, errMsg, module);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		} catch (IOException e) {
			String errMsg = "Error downloading digital product content: " + e.toString();
			Debug.logError(e, errMsg, module);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
    	
        return "success";
    }
}
