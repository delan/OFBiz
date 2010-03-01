/*
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
 */
package org.ofbiz.ebaystore;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.taglib.ServiceTag;

public class EbayEvents {

    public static final String module = ServiceTag.class.getName();

    public static String sendLeaveFeedback(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map requestParams = UtilHttp.getParameterMap(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        int feedbackSize = Integer.parseInt((String)requestParams.get("feedbackSize"));
        String productStoreId = (String)requestParams.get("productStoreId");
        for (int i = 1; i <= feedbackSize; i++) {
            String commentType = (String)requestParams.get("commentType"+i);
            String commentText = (String)requestParams.get("commentText"+i);
            if (!commentType.equals("none") && commentText != null) {
                String itemId = (String)requestParams.get("itemId"+i);
                String transactionId = (String)requestParams.get("transactionId"+i);
                String targetUser = (String)requestParams.get("targetUser"+i);
                String commentingUser = (String)requestParams.get("commentingUser"+i);
                String role = (String)requestParams.get("role"+i);
                String ratingItem = (String)requestParams.get("ratingItem"+i);
                String ratingComm = (String)requestParams.get("ratingComm"+i);
                String ratingShip = (String)requestParams.get("ratingShip"+i);
                String ratingShipHand = (String)requestParams.get("ratingShipHand"+i);
                String AqItemAsDescribedId = (String)requestParams.get("AqItemAsDescribedId"+i);

                Map leavefeedback =  FastMap.newInstance();
                leavefeedback.put("productStoreId", productStoreId);
                leavefeedback.put("userLogin", userLogin);
                leavefeedback.put("itemId", itemId);
                leavefeedback.put("transactionId", transactionId);
                leavefeedback.put("targetUser", targetUser);
                leavefeedback.put("commentingUser", commentingUser);
                leavefeedback.put("role", role);
                leavefeedback.put("commentText", commentText);
                leavefeedback.put("commentType", commentType);
                leavefeedback.put("ratingItem", ratingItem);
                leavefeedback.put("ratingComm", ratingComm);
                leavefeedback.put("ratingShip", ratingShip);
                leavefeedback.put("ratingShipHand", ratingShipHand);
                leavefeedback.put("AqItemAsDescribedId", AqItemAsDescribedId);
                // Call service
                try {
                    Map result = dispatcher.runSync("leaveFeedback", leavefeedback);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    return "error";
                }
            }
        }
        return "success";
    }
}