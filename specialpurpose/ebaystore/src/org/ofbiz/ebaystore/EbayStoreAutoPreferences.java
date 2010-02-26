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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.GetSellingManagerSoldListingsCall;
import com.ebay.sdk.call.GetUserCall;
import com.ebay.sdk.call.LeaveFeedbackCall;
import com.ebay.soap.eBLBaseComponents.AutomatedLeaveFeedbackEventCodeType;
import com.ebay.soap.eBLBaseComponents.CommentTypeCodeType;
import com.ebay.soap.eBLBaseComponents.FeedbackDetailType;
import com.ebay.soap.eBLBaseComponents.SellingManagerOrderStatusType;
import com.ebay.soap.eBLBaseComponents.SellingManagerPaidStatusCodeType;
import com.ebay.soap.eBLBaseComponents.SellingManagerShippedStatusCodeType;
import com.ebay.soap.eBLBaseComponents.SellingManagerSoldListingsSortTypeCodeType;
import com.ebay.soap.eBLBaseComponents.SellingManagerSoldOrderType;
import com.ebay.soap.eBLBaseComponents.SellingManagerSoldTransactionType;

public class EbayStoreAutoPreferences {
	public static String module = EbayStoreAutoPreferences.class.getName();

	public EbayStoreAutoPreferences(){

	}
	/*  It may take several minutes to process your automated feedback.  to connect to ebay site*/
	public static Map<String, Object> autoPrefLeaveFeedbackOption(DispatchContext dctx, Map<String, ? extends Object> context) throws ApiException, SdkException, Exception{

		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");

		if (UtilValidate.isEmpty(context.get("productStoreId"))){
			return ServiceUtil.returnFailure("Required productStoreId for get api context to connect with ebay site.");
		}

		String productStoreId = (String) context.get("productStoreId");
		String isAutoPositiveFeedback = "N";
		String feedbackEventCode = null;
		GenericValue ebayProductStorePref = null;
		List<String> list = FastList.newInstance();

		try {
			ApiContext apiContext = EbayStoreHelper.getApiContext(productStoreId, locale, delegator);
			ebayProductStorePref = delegator.findByPrimaryKey("EbayProductStorePref", UtilMisc.toMap("productStoreId", productStoreId,"autoPrefEnumId","EBAY_AUTO_PIT_FB"));
			if (UtilValidate.isNotEmpty(ebayProductStorePref)) {
				isAutoPositiveFeedback = ebayProductStorePref.getString("enabled");
				// if isAutoPositiveFeedback is N that means not start this job run service
				if ("Y".equals(isAutoPositiveFeedback)) {
					feedbackEventCode = ebayProductStorePref.getString("condition1");
					String storeComments = ebayProductStorePref.getString("condition2");
					String comment = null;
					if (UtilValidate.isNotEmpty(storeComments)){
						if (storeComments.indexOf("\\[,\\]") != -1) {
							String[] strs = storeComments.split("\\[,\\]");
							for (String str :strs) {
								list.add(str);
							}
						}
					}
					// start getting sold item list from ebay follow your site
					GetSellingManagerSoldListingsCall sellingManagerSoldListings = new GetSellingManagerSoldListingsCall(apiContext);

					List<SellingManagerSoldOrderType> items = FastList.newInstance();
					SellingManagerSoldOrderType[] sellingManagerSoldOrders = sellingManagerSoldListings.getSellingManagerSoldListings();
					if (UtilValidate.isNotEmpty(sellingManagerSoldOrders)) {
						for(SellingManagerSoldOrderType solditem :sellingManagerSoldOrders){
							SellingManagerOrderStatusType orderStatus = solditem.getOrderStatus();
							if (orderStatus != null && !orderStatus.isFeedbackSent()) {
								SellingManagerPaidStatusCodeType  paidStatus = orderStatus.getPaidStatus();
								CommentTypeCodeType commentType  = orderStatus.getFeedbackReceived();
								//Buyer has paid for this item. 
								if ("PAYMENT_RECEIVED".equals(feedbackEventCode) && SellingManagerPaidStatusCodeType.PAID.equals(paidStatus)) {
									items.add(solditem);
								}
								//Buyer has paid for this item and left me positive feedback.
								if ("POSITIVE_FEEDBACK_RECEIVED".equals(feedbackEventCode) && CommentTypeCodeType.POSITIVE.equals(commentType) && SellingManagerPaidStatusCodeType.PAID.equals(paidStatus)) {
									items.add(solditem);
								}
							}
						}
						GetUserCall getUserCall = new GetUserCall(apiContext);
						String commentingUser = getUserCall.getUser().getUserID();
						for(SellingManagerSoldOrderType item :items){
							// start leave feedbacks
							SellingManagerSoldTransactionType[] soldTrans = item.getSellingManagerSoldTransaction();
							if (UtilValidate.isNotEmpty(soldTrans)) {
								for(SellingManagerSoldTransactionType soldTran : soldTrans){
									LeaveFeedbackCall leaveFeedbackCall = new LeaveFeedbackCall(apiContext);
									FeedbackDetailType detail = new FeedbackDetailType();
									// ramdom comments
									if (list.size()>0) {
										Collections.shuffle(list, new Random());
										comment = list.get(0);
									}
									detail.setCommentText(comment);
									detail.setCommentingUser(commentingUser);
									//detail.setCommentingUserScore(value);
									detail.setCommentType(CommentTypeCodeType.POSITIVE);
									detail.setItemID(soldTran.getItemID());
									detail.setItemPrice(soldTran.getItemPrice());
									detail.setItemTitle(soldTran.getItemTitle());
									leaveFeedbackCall.setFeedbackDetail(detail);
									leaveFeedbackCall.setTargetUser(item.getBuyerID());
									leaveFeedbackCall.setTransactionID(String.valueOf(soldTran.getTransactionID()));
									leaveFeedbackCall.leaveFeedback();
									Debug.logInfo("Auto leave feedback with site ".concat(apiContext.getSite().value()).concat("itemId ".concat(soldTran.getItemID())).concat(" comment is ".concat(comment)), module);
								}
							}
						}
					}
				}
			} 
		}catch (Exception e) {
			return ServiceUtil.returnFailure("Problems to connect with ebay site message:"+e);
		}

		return ServiceUtil.returnSuccess();
	}

	public static String autoPrefLeaveFeedbackOptions(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		Map paramMap = UtilHttp.getCombinedMap(request);

		if (UtilValidate.isEmpty(paramMap.get("productStoreId"))){
			request.setAttribute("_ERROR_MESSAGE_","Required productStoreId for get api context to connect with ebay site.");
			return "error";
		}

		String productStoreId = (String) paramMap.get("productStoreId");
		String isAutoPositiveFeedback = "N";
		String condition = null;
		if (UtilValidate.isNotEmpty(paramMap.get("isAutoPositiveFeedback"))) isAutoPositiveFeedback = (String) paramMap.get("isAutoPositiveFeedback");
		String feedbackEventCode = (String) paramMap.get("feedbackEventCode");
		ApiContext apiContext = EbayStoreHelper.getApiContext(productStoreId, locale, delegator);

		try {
			GenericValue ebayProductStorePref = null;
			String comments = null;
			String autoPrefJobId = null;

			if ("Y".equals(isAutoPositiveFeedback)) {
				if ("PAYMENT_RECEIVED".equals(feedbackEventCode)) {
					condition = AutomatedLeaveFeedbackEventCodeType.PAYMENT_RECEIVED.toString();
				} else if ("POSITIVE_FEEDBACK_RECEIVED".equals(feedbackEventCode)) {
					condition = AutomatedLeaveFeedbackEventCodeType.POSITIVE_FEEDBACK_RECEIVED.toString();
				}
				// allow only 10 comment can be store / set new comments to condition2 separate by [,]
			}
			for(int i=1;i<=5;i++){
				String comment = (String)paramMap.get("comment_".concat(String.valueOf(i)));
				if (comment!=null && comment.length()>0) {
					if (comments==null) comments = comment;
					else comments = comments.concat("[").concat(",").concat(("]").concat(comment));
				}
			}
			if (UtilValidate.isEmpty(comments)){
				request.setAttribute("_ERROR_MESSAGE_","Required least one at comment for your store feedback send with ebay site.");
				return "error";
			}

			Map context  = UtilMisc.toMap("userLogin", userLogin,"serviceName","autoPrefLeaveFeedbackOption");
			ebayProductStorePref = delegator.findByPrimaryKey("EbayProductStorePref", UtilMisc.toMap("productStoreId", productStoreId,"autoPrefEnumId","EBAY_AUTO_PIT_FB"));
			context.put("productStoreId", productStoreId);
			context.put("autoPrefEnumId", "EBAY_AUTO_PIT_FB");
			if (UtilValidate.isNotEmpty(ebayProductStorePref) && UtilValidate.isNotEmpty(ebayProductStorePref.getString("autoPrefJobId"))) autoPrefJobId = ebayProductStorePref.getString("autoPrefJobId");
			context.put("autoPrefJobId", autoPrefJobId);
			context.put("enabled", isAutoPositiveFeedback);
			context.put("condition1", condition);
			context.put("condition2", comments);
			context.put("condition3", null);
			if (UtilValidate.isEmpty(ebayProductStorePref)) {
				dispatcher.runSync("createEbayProductStorePref", context);
			} else {
				dispatcher.runSync("updateEbayProductStorePref", context);
			}
			request.setAttribute("_EVENT_MESSAGE_","Setting Automated Positive Feedback for Buyers Success with site "+apiContext.getSite().value());

		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		} catch (GenericServiceException e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}
	
	/* start automatically service send a Feedback Reminder email if feedback has not been received. and check how many days after shipping you want this email sent? */
	public static Map<String, Object> autoSendFeedbackReminderEmail(DispatchContext dctx, Map<String, ? extends Object> context) throws ApiException, SdkException, Exception{
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");

		if (UtilValidate.isEmpty(context.get("productStoreId"))){
			return ServiceUtil.returnFailure("Required productStoreId for get api context to connect with ebay site.");
		}

		String productStoreId = (String) context.get("productStoreId");
		String isAutoFeedbackReminder = "N";
		int afterDays = 0;
		String isAlsoSendCopyToSeller = "N";
		GenericValue ebayProductStorePref = null;
		List<String> list = FastList.newInstance();
		String dateTimeFormat = UtilDateTime.DATE_TIME_FORMAT;
		SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
		
		try {
			ApiContext apiContext = EbayStoreHelper.getApiContext(productStoreId, locale, delegator);
			ebayProductStorePref = delegator.findByPrimaryKey("EbayProductStorePref", UtilMisc.toMap("productStoreId", productStoreId,"autoPrefEnumId","EBAY_AUTO_FB_RMD"));
			if (UtilValidate.isNotEmpty(ebayProductStorePref)) {
				isAutoFeedbackReminder = ebayProductStorePref.getString("enabled");
				// if isAutoPositiveFeedback is N that means not start this job run service
				if ("Y".equals(isAutoFeedbackReminder)) {
					afterDays = Integer.parseInt(ebayProductStorePref.getString("condition1"));
					isAlsoSendCopyToSeller = ebayProductStorePref.getString("condition2");

					// start getting sold item list from ebay follow your site
					GetSellingManagerSoldListingsCall sellingManagerSoldListings = new GetSellingManagerSoldListingsCall(apiContext);
					List<SellingManagerSoldOrderType> items = FastList.newInstance();
					SellingManagerSoldOrderType[] sellingManagerSoldOrders = sellingManagerSoldListings.getSellingManagerSoldListings();
					if (UtilValidate.isNotEmpty(sellingManagerSoldOrders)) {
						for(SellingManagerSoldOrderType solditem :sellingManagerSoldOrders){
							SellingManagerOrderStatusType orderStatus = solditem.getOrderStatus();
							if (orderStatus != null && !orderStatus.isFeedbackSent()) {
								SellingManagerPaidStatusCodeType  paidStatus = orderStatus.getPaidStatus();
								CommentTypeCodeType commentType  = orderStatus.getFeedbackReceived();
								SellingManagerShippedStatusCodeType  shippedStatus = orderStatus.getShippedStatus();
								
								//Buyer has paid for this item.  && Seller shipped items but feedback has not been received from buyer more than days condition 
								if (SellingManagerPaidStatusCodeType.PAID.equals(paidStatus) && SellingManagerShippedStatusCodeType.SHIPPED.equals(shippedStatus)) {
									Calendar right_now =  Calendar.getInstance();
									Calendar shippedTime = orderStatus.getShippedTime();
									Calendar afterShippedTime = orderStatus.getShippedTime();
									afterShippedTime.add(afterShippedTime.DAY_OF_MONTH, afterDays);
									Debug.logInfo("Verify date for send reminder feedback eamil by auto service: buyer "+solditem.getBuyerID()+" seller shippedTime " +
											""+formatter.format(shippedTime)+" codition days "+afterDays+" after shippedTime :"+formatter.format(afterShippedTime)+" now date"+formatter.format(right_now), module);
									// if now date is after shipped time follow after days condition would be send reminder email to buyer
									if (right_now.after(afterShippedTime)) items.add(solditem);
								}
							}
						}
						
						// call service send email (get template follow productStoreId)
						GetUserCall getUserCall = new GetUserCall(apiContext);
						String sellerUser = getUserCall.getUser().getUserID();
						for(SellingManagerSoldOrderType item :items){
							// start leave feedbacks
							SellingManagerSoldTransactionType[] soldTrans = item.getSellingManagerSoldTransaction();
							if (UtilValidate.isNotEmpty(soldTrans)) {
								for(SellingManagerSoldTransactionType soldTran : soldTrans){
									// call send 
								}
							}
						}
					}
				}
			} 
		}catch (Exception e) {
			return ServiceUtil.returnFailure("Problems to connect with ebay site message:"+e);
		}
		
		return ServiceUtil.returnSuccess();
	}
}
