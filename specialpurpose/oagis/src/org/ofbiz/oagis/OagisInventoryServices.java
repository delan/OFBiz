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
package org.ofbiz.oagis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.ParseException;



import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class OagisInventoryServices {
    
    public static final String module = OagisInventoryServices.class.getName();
    
    public static final Double doubleZero = new Double(0.0);
    public static final Double doubleOne = new Double(1.0);
    
    public static Map syncInventory(DispatchContext ctx, Map context) {
        InputStream in = (InputStream) context.get("inputStream");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Map receiveInventoryCtx = FastMap.newInstance();
        List errorMapList = FastList.newInstance();
        
        if (userLogin == null) {
            try {
                userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            } catch (GenericEntityException e){
                String errMsg = "Error Getting UserLogin: " + e.toString();
                Debug.logError(e, errMsg, module);
            }
        }

        Document doc = null;
        try {
            doc = UtilXml.readXmlDocument(in, true, "SyncInventory");
        } catch (SAXException e) {
            String errMsg = "Error parsing the SyncInventoryResponse: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "SAXException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        } catch (ParserConfigurationException e) {
            String errMsg = "Error parsing the SyncInventoryResponse: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "ParserConfigurationException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        } catch (IOException e) {
            String errMsg = "Error parsing the SyncInventoryResponse: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "IOException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        if (errorMapList.size() > 0) {
            return ServiceUtil.returnError("Unable to parse message: SyncInventory");
        }
        
        Element receiveInventoryElement = doc.getDocumentElement();
        receiveInventoryElement.normalize();
                        
        Element docCtrlAreaElement = UtilXml.firstChildElement(receiveInventoryElement, "N1:CNTROLAREA");
        Element docSenderElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:SENDER");
        Element docBsrElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:BSR");
            
        String bsrVerb = UtilXml.childElementValue(docBsrElement, "N2:VERB");
        String bsrNoun = UtilXml.childElementValue(docBsrElement, "N2:NOUN");
        String bsrRevision = UtilXml.childElementValue(docBsrElement, "N2:REVISION");
        
        String logicalId = UtilXml.childElementValue(docSenderElement, "N2:LOGICALID");
        String component = UtilXml.childElementValue(docSenderElement, "N2:COMPONENT");
        String task = UtilXml.childElementValue(docSenderElement, "N2:TASK");
        String referenceId = UtilXml.childElementValue(docSenderElement, "N2:REFERENCEID");
        String confirmation = UtilXml.childElementValue(docSenderElement, "N2:CONFIRMATION");
        String authId = UtilXml.childElementValue(docSenderElement, "N2:AUTHID");
            
        Element dataAreaElement = UtilXml.firstChildElement(receiveInventoryElement, "n:DATAAREA");
        Element dataAreaSyncInventoryElement = UtilXml.firstChildElement(dataAreaElement, "n:SYNC_INVENTORY");
        Element dataAreaInventoryElement = UtilXml.firstChildElement(dataAreaSyncInventoryElement, "n:INVENTORY");
            
        Element dataAreaQuantityElement = UtilXml.firstChildElement(dataAreaInventoryElement, "N1:QUANTITY");
            
        String itemQtyStr = UtilXml.childElementValue(dataAreaQuantityElement, "N2:VALUE");
        double itemQty = Double.parseDouble(itemQtyStr);
        String sign = UtilXml.childElementValue(dataAreaQuantityElement, "N2:SIGN");
        String uom = UtilXml.childElementValue(dataAreaQuantityElement, "N2:UOM");
        String productId = UtilXml.childElementValue(dataAreaQuantityElement, "N2:ITEM");
        String itemStatus = UtilXml.childElementValue(dataAreaQuantityElement, "N2:ITEMSTATUS");
        
        String datetimeReceived = UtilXml.childElementValue(dataAreaInventoryElement, "N1:DATETIMEANY");
        
        // In BOD the timestamp come in the format "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'Z"
        // Parse this into a valid Timestamp Object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'Z");
        Timestamp timestamp = null;
        try {        
            timestamp = new Timestamp(sdf.parse(datetimeReceived).getTime());
            receiveInventoryCtx.put("datetimeReceived", timestamp);
        } catch (ParseException e) {
            String errMsg = "Error parsing Date: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "ParseException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        
        String facilityId = UtilProperties.getPropertyValue("oagis.properties", "Oagis.Warehouse.SyncInventoryFacilityId");
        receiveInventoryCtx.put("facilityId", facilityId);
        
        receiveInventoryCtx.put("productId", productId);
        receiveInventoryCtx.put("statusId", itemStatus);
        receiveInventoryCtx.put("currencyUomId", uom);
        receiveInventoryCtx.put("userLogin", userLogin);   
        
        // check whether product is serialized or non-serialized
        try {
            GenericValue inventoryItem = EntityUtil.getFirst(delegator.findByAnd("InventoryItem", UtilMisc.toMap("productId", productId)));
            String inventoryItemTypeId = inventoryItem.getString("inventoryItemTypeId");
            if (inventoryItemTypeId.equals("SERIALIZED_INV_ITEM")) {
                receiveInventoryCtx.put("inventoryItemTypeId","SERIALIZED_INV_ITEM");
            } else {
                receiveInventoryCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error Getting Inventory Item: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericEntityException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        
/*      // get quantity on hand total using service getProductInventoryAvailable
        Map gpiaResult = FastMap.newInstance();
        try {
            gpiaResult = dispatcher.runSync("getProductInventoryAvailable", UtilMisc.toMap("productId", productId) );
            if (ServiceUtil.isError(gpiaResult)) {
                String errMsg = ServiceUtil.getErrorMessage(gpiaResult);
                errorMapList.add(UtilMisc.toMap("reasonCode", "GetProductInventoryAvailableServiceError", "description", errMsg));
            }
        } catch (GenericServiceException e) {
            String errMsg = "Error running service getProductInventoryAvailable: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericServiceException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
            
        double quantityOnHandTotal = ((Double) gpiaResult.get("quantityOnHandTotal")).doubleValue();
*/     
        // get quantity on hand total   
        double quantityOnHandTotal = 0.0;
        List inventoryItems = null;
        List exprs = FastList.newInstance();
        try {
            exprs.add(new EntityExpr("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, timestamp)); 
            exprs.add(new EntityExpr("productId", EntityOperator.EQUALS, productId));
            inventoryItems = delegator.findByCondition("InventoryItem", new EntityConditionList(exprs, EntityOperator.OR), null, UtilMisc.toList("inventoryItemId"));
            Iterator invItemIter = inventoryItems.iterator();
            while (invItemIter.hasNext()) {
                GenericValue inventoryItem = (GenericValue) invItemIter.next();
                quantityOnHandTotal = quantityOnHandTotal + Double.parseDouble(inventoryItem.getString("quantityOnHandTotal"));
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error Getting Inventory Item: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericEntityException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }

        // sign handling     
        double quantityAccepted ;
        double quantityRejected ;
            
        if ( sign.equals("+")) {
            quantityAccepted = itemQty - quantityOnHandTotal; // take difference to get mismatch quantity value
            quantityRejected = 0.0 ;
        } else {
            quantityRejected = itemQty;
            quantityAccepted = 0.0;
        }
        receiveInventoryCtx.put("quantityAccepted", new Double(quantityAccepted));
        receiveInventoryCtx.put("quantityRejected", new Double(quantityRejected));
        
        // if qoh from message and inventory matches, nothing is done and if not than create inventory  
        try {
            if (itemQty != quantityOnHandTotal) {   
                Map ripResult = dispatcher.runSync("receiveInventoryProduct", receiveInventoryCtx );
                if (ServiceUtil.isError(ripResult)) {
                    String errMsg = ServiceUtil.getErrorMessage(ripResult);
                    errorMapList.add(UtilMisc.toMap("reasonCode", "ReceiveInventoryServiceError", "description", errMsg));
                }
            } 
        } catch(GenericServiceException e) {
            String errMsg = "Error running service receiveInventoryProduct: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericServiceException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }

        // create oagis message info
        Map comiCtx= FastMap.newInstance();
        comiCtx.put("logicalId", logicalId);
        comiCtx.put("component", component);
        comiCtx.put("task", task);
        comiCtx.put("referenceId", referenceId);
        comiCtx.put("confirmation", confirmation);
        comiCtx.put("authId", authId);
        comiCtx.put("bsrVerb", bsrVerb);
        comiCtx.put("bsrNoun", bsrNoun);
        comiCtx.put("bsrRevision", bsrRevision);
        comiCtx.put("receivedDate", UtilDateTime.nowTimestamp());
        comiCtx.put("outgoingMessage", "N");
        comiCtx.put("userLogin", userLogin);
        try {
            Map comiResult = dispatcher.runSync("createOagisMessageInfo", comiCtx);
            if (ServiceUtil.isError(comiResult)) {
                String errMsg = ServiceUtil.getErrorMessage(comiResult);
                errorMapList.add(UtilMisc.toMap("reasonCode", "CreateOagisMessageServiceError", "description", errMsg));
            }
        } catch (GenericServiceException e) {
            String errMsg = "Error creating OagisMessageInfo for the Incoming Message: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "CreateOagisMessageInfoError", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        // prepare information to send mail
        Map sendMap = FastMap.newInstance();

        // get facility email address
        List facilityContactMechs = null;
        GenericValue contactMech = null;
        try {
            facilityContactMechs = delegator.findByAnd("FacilityContactMech", UtilMisc.toMap("facilityId", facilityId));    
        } catch (GenericEntityException e) {
            String errMsg = "Error Getting FacilityContactMech: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericEntityException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        Iterator fcmIter  = facilityContactMechs.iterator();
        while(fcmIter.hasNext()) {
            GenericValue facilityContactMech = (GenericValue) fcmIter.next();
            String contactMechId = facilityContactMech.getString("contactMechId");
            try {
                contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
            } catch (GenericEntityException e) {
                String errMsg = "Error Getting ContactMech: " + e.toString();
                errorMapList.add(UtilMisc.toMap("reasonCode", "GenericEntityException", "description", errMsg));
                Debug.logError(e, errMsg, module);
            }
            String contactMechTypeId = contactMech.getString("contactMechTypeId");
            if (contactMechTypeId.equals("EMAIL_ADDRESS")) {
                String emailString = contactMech.getString("infoString");
                sendMap.put("sendTo", emailString);
            }
        }
        
        GenericValue productStoreEmail = null;
        String productStoreId = UtilProperties.getPropertyValue("oagis.properties", "Oagis.Warehouse.SyncInventoryProductStoreId");
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", "PRDS_OAGIS_CONFIRM"));
        } catch (GenericEntityException e) {
            String errMsg = "Error Getting Entity ProductStoreEmailSetting: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericEntityException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        if (productStoreEmail != null) {
            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            sendMap.put("bodyScreenUri", bodyScreenLocation);
        } else {
            sendMap.put("bodyScreenUri", "component://oagis/widget/EmailOagisMessageScreens.xml#EmailOagisMessageNotification");
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        sendMap.put("subject", productStoreEmail.getString("subject"));
        sendMap.put("sendFrom", productStoreEmail.getString("fromAddress"));
        sendMap.put("sendCc", productStoreEmail.getString("ccAddress"));
        sendMap.put("sendBcc", productStoreEmail.getString("bccAddress"));
        sendMap.put("contentType", productStoreEmail.getString("contentType"));
        
        Map bodyParameters = UtilMisc.toMap("quantityOnHandTotal", String.valueOf(quantityOnHandTotal), "itemQtyFrmMessage", String.valueOf(itemQty), "locale", locale);
        sendMap.put("bodyParameters", bodyParameters);
        sendMap.put("userLogin", userLogin);
        
        // send the notification
        Map sendResp = null;
        try {
            sendResp = dispatcher.runSync("sendMailFromScreen", sendMap);
            if (ServiceUtil.isError(sendResp)){
                String errMsg = ServiceUtil.getErrorMessage(sendResp);
                errorMapList.add(UtilMisc.toMap("reasonCode", "SendMailServiceError", "description", errMsg));
            }
        } catch(GenericServiceException e) {
            String errMsg = "Error Running Service sendMailFromScreen: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericServiceException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }

        Map result = FastMap.newInstance();
        result.put("contentType", "text/plain");

        // check error list if there is any 
        if (errorMapList.size() > 0) {
            result.putAll(comiCtx);
            result.put("errorMapList", errorMapList);
            String errMsg = "Error Processing Received Messages";
            result.putAll(ServiceUtil.returnError(errMsg));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Action Performed Successfully"));
        return result;
    }
    
    public static Map receivePoAcknowledge(DispatchContext ctx, Map context) {
        InputStream in = (InputStream) context.get("inputStream");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        List errorMapList = FastList.newInstance();
        Map comiCtx = FastMap.newInstance();
        if (userLogin == null) {
            try {
                userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            } catch (GenericEntityException e) {
                String errMsg = "Error Getting UserLogin: " + e.toString();
                Debug.logError(e, errMsg, module);
            }
        }
        
        Document doc = null;
        try {
            doc = UtilXml.readXmlDocument(in, true, "ReceivePoAcknowledge");
        } catch (SAXException e) {
            String errMsg = "Error parsing the ReceivePoAcknowledge message: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "SAXException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        } catch (ParserConfigurationException e) {
            String errMsg = "Error parsing the ReceivePoAcknowledge message: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "ParserConfigurationException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        } catch (IOException e) {
            String errMsg = "Error parsing the ReceivePoAcknowledge message: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "IOException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        if (errorMapList.size() > 0) {
            return ServiceUtil.returnError("Unable to parse message: ReceivePoAcknowledge");
        }

        // parse the message 
        Element receivePoElement = doc.getDocumentElement();
        receivePoElement.normalize();
        Element docCtrlAreaElement = UtilXml.firstChildElement(receivePoElement, "N1:CNTROLAREA");
            
        Element docSenderElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:SENDER");
        Element docBsrElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:BSR");

        String bsrVerb = UtilXml.childElementValue(docBsrElement, "N2:VERB");
        String bsrNoun = UtilXml.childElementValue(docBsrElement, "N2:NOUN");
        String bsrRevision = UtilXml.childElementValue(docBsrElement, "N2:REVISION");
        
        String logicalId = UtilXml.childElementValue(docSenderElement, "N2:LOGICALID");
        String component = UtilXml.childElementValue(docSenderElement, "N2:COMPONENT");
        String task = UtilXml.childElementValue(docSenderElement, "N2:TASK");
        String referenceId = UtilXml.childElementValue(docSenderElement, "N2:REFERENCEID");
        String confirmation = UtilXml.childElementValue(docSenderElement, "N2:CONFIRMATION");
        String authId = UtilXml.childElementValue(docSenderElement, "N2:AUTHID");
            
        Element dataAreaElement = UtilXml.firstChildElement(receivePoElement, "n:DATAAREA");
        Element acknowledgeDeliveryElement = UtilXml.firstChildElement(dataAreaElement, "n:ACKNOWLEDGE_DELIVERY");

        String inventoryItemTypeId = null;
        String orderId = null;
        String facilityId = UtilProperties.getPropertyValue("oagis.properties", "Oagis.Warehouse.PoReceiptFacilityId");
        
        // get RECEIPTLN elements from message
        List acknowledgeElementList = UtilXml.childElementList(acknowledgeDeliveryElement, "n:RECEIPTLN");
        if (UtilValidate.isNotEmpty(acknowledgeElementList)) {
        	Iterator acknowledgeElementIter = acknowledgeElementList.iterator();
        	while (acknowledgeElementIter.hasNext()) {
                Map ripCtx = FastMap.newInstance();
                Element receiptLnElement = (Element) acknowledgeElementIter.next();
                Element qtyElement = UtilXml.firstChildElement(receiptLnElement, "N1:QUANTITY");
                
                String itemQtyStr = UtilXml.childElementValue(qtyElement, "N2:VALUE");
                double itemQty = Double.parseDouble(itemQtyStr);
                String sign = UtilXml.childElementValue(qtyElement, "N2:SIGN");
                
                String productId = UtilXml.childElementValue(receiptLnElement, "N2:ITEM");
                
                Element documentRefElement = UtilXml.firstChildElement(receiptLnElement, "N1:DOCUMNTREF");
                orderId = UtilXml.childElementValue(documentRefElement, "N2:DOCUMENTID");
                String orderItemSeqId = UtilXml.childElementValue(documentRefElement, "N2:LINENUM");
                
                // check reference to PO number, if exists
                GenericValue orderHeader = null;
                try {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                    if (orderHeader != null) {
                        ripCtx.put("orderId", orderId);
                        ripCtx.put("orderItemSeqId", orderItemSeqId);
                        comiCtx.put("orderId", orderId);
                    }
                }  catch (GenericEntityException e) {
                    String errMsg = "Error Getting OrderHeader: " + e.toString();
                    errorMapList.add(UtilMisc.toMap("reasonCode", "GenericEntityException", "description", errMsg));
                    Debug.logError(e, errMsg, module);
                }
                // get inventory item status
                String invItemStatus = UtilXml.childElementValue(receiptLnElement, "N2:DISPOSITN");
                if ( invItemStatus.equals("ReceivedTOAvailable") || invItemStatus.equals("NotAvailableTOAvailable")) {
                    ripCtx.put("statusId", "INV_AVAILABLE");    
                } else if ( invItemStatus.equals("ReceivedTONotAvailable") || invItemStatus.equals("AvailableTONotAvailable") ) {
                    ripCtx.put("statusId", "INV_ON_HOLD");
                }
                // get the serial number(s) 
                List serialNumsList = FastList.newInstance();
                List invDetailList = UtilXml.childElementList(receiptLnElement, "n:INVDETAIL");
                if (UtilValidate.isNotEmpty(invDetailList)) {
                    inventoryItemTypeId = "SERIALIZED_INV_ITEM";
                    ripCtx.put("inventoryItemTypeId", inventoryItemTypeId);
                    for (Iterator j = invDetailList.iterator(); j.hasNext();) {
                        Element invDetailElement = (Element) j.next();
                        String serialNumber = UtilXml.childElementValue(invDetailElement, "N2:SERIALNUM");
                        if (UtilValidate.isNotEmpty(serialNumber)) {
                            serialNumsList.add(serialNumber);
                        }
                    }

                    /* DEJ20070711 Commenting this out because it shouldn't happen, ie more likely the ITEM element will be filled 
                     * than INVDETAIL->SERIALNUM, and this isn't a reliable way to look it up (may be more than 1 record for a given 
                     * serialNumber for different products 
                    // this is a Serialized Inventory Item. If the productId from the message is not valid then lets read it from InventoryItem in Ofbiz database.
                    if (productId == null || "".equals(productId)) {
                        try {
                            GenericValue inventoryItem = EntityUtil.getFirst(delegator.findByAnd("InventoryItem", UtilMisc.toMap("serialNumber", serialNumber)));
                            if (inventoryItem !=null) {
                                productId = inventoryItem.getString("productId");
                            }
                        } catch (GenericEntityException e){
                            String errMsg = "Error Getting Entity InventoryItem";
                            Debug.logError(e, errMsg, module);
                        }
                    } */
                } else {
                    inventoryItemTypeId = "NON_SERIAL_INV_ITEM";
                    ripCtx.put("inventoryItemTypeId", inventoryItemTypeId);
                }
                ripCtx.put("productId", productId);
                ripCtx.put("facilityId",facilityId);
                ripCtx.put("userLogin", userLogin);

                // sign handling for items
                double quantityAccepted = 0.0;
                double quantityRejected = 0.0;
                if (sign.equals("+")) {
                    quantityAccepted = itemQty;
                    quantityRejected= 0.0;
                } else {
                    quantityRejected = itemQty;
                    quantityAccepted = 0.0;
                }
                if (quantityAccepted > 0) {
                	if (serialNumsList.size() > 0) {
                		if (serialNumsList.size() != quantityAccepted) {
                			// this is an error, do something about it, like add to the list to send back a Confirm BOD with error messages
                			String errMsg = "Error: the quantity [" + quantityAccepted + "] did not match the number of serial numbers passed [" + serialNumsList.size() + "].";
                			errorMapList.add(UtilMisc.toMap("reasonCode", "QuantitySerialMismatch", "description", errMsg));
                		}
                		
                    	Iterator serialNumIter = serialNumsList.iterator();
                    	while (serialNumIter.hasNext()) {
                    		String serialNum = (String) serialNumIter.next();
                    		
                            // clone the context as it may be changed in the call
                    		Map localRipCtx = FastMap.newInstance();
                            localRipCtx.putAll(ripCtx);
                            
                            localRipCtx.put("quantityAccepted", new Double(1.0));
                            // always set this to 0, if needed we'll handle the rejected quantity separately
                            localRipCtx.put("quantityRejected", new Double(0.0));

                            localRipCtx.put("serialNumber", serialNum);
                            
                            try {
                                Map ripResult = dispatcher.runSync("receiveInventoryProduct", localRipCtx);
                                if (ServiceUtil.isError(ripResult)) {
                                	String errMsg = ServiceUtil.getErrorMessage(ripResult);
                        			errorMapList.add(UtilMisc.toMap("reasonCode", "ReceiveInventoryServiceError", "description", errMsg));
                                }
                            } catch (GenericServiceException e) {
                                String errMsg = "Error running service receiveInventoryProduct: " + e.toString();
                    			errorMapList.add(UtilMisc.toMap("reasonCode", "GenericServiceException", "description", errMsg));
                                Debug.logError(e, errMsg, module);
                            }    
                    	}
                	} else {
                		// no serial numbers, just receive the quantity
                        
                		// clone the context as it may be changted in the call
                		Map localRipCtx = FastMap.newInstance();
                        localRipCtx.putAll(ripCtx);
                        
                        localRipCtx.put("quantityAccepted", new Double(quantityAccepted));
                        // always set this to 0, if needed we'll handle the rejected quantity separately
                        localRipCtx.put("quantityRejected", new Double(0.0));
                        
                        try {
                            Map ripResult = dispatcher.runSync("receiveInventoryProduct", localRipCtx);
                            if (ServiceUtil.isError(ripResult)) {
                            	String errMsg = ServiceUtil.getErrorMessage(ripResult);
                    			errorMapList.add(UtilMisc.toMap("reasonCode", "ReceiveInventoryServiceError", "description", errMsg));
                            }
                        } catch (GenericServiceException e) {
                            String errMsg = "Error running service receiveInventoryProduct: " + e.toString();
                			errorMapList.add(UtilMisc.toMap("reasonCode", "GenericServiceException", "description", errMsg));
                            Debug.logError(e, errMsg, module);
                        }    
                	}
                } else {
                    // TODOLATER: need to run service receiveInventoryProduct and updateInventoryItem when quantityRejected > 0
                	// NOTE DEJ20070711 this shouldn't happen for current needs, so save for later
                }
            }
        }         
        //prepare result Map for createOagisMessageinfo
        
        Timestamp timestamp = null;
        timestamp = UtilDateTime.nowTimestamp();
        comiCtx.put("logicalId", logicalId);
        comiCtx.put("authId", authId);
        comiCtx.put("referenceId", referenceId);
        comiCtx.put("receivedDate", timestamp);
        comiCtx.put("component", component);
        comiCtx.put("task", task);  
        comiCtx.put("outgoingMessage", "N");
        comiCtx.put("confirmation", confirmation);
        comiCtx.put("bsrVerb", bsrVerb);
        comiCtx.put("bsrNoun", bsrNoun);
        comiCtx.put("bsrRevision", bsrRevision);
        comiCtx.put("userLogin", userLogin);
        try {
            Map comiResult = dispatcher.runSync("createOagisMessageInfo", comiCtx);
            if (ServiceUtil.isError(comiResult)) {
                String errMsg = ServiceUtil.getErrorMessage(comiResult);
                errorMapList.add(UtilMisc.toMap("reasonCode", "CreateOagisMessageServiceError", "description", errMsg));
            }
        } catch (GenericServiceException e) {
            String errMsg = "Error creating OagisMessageInfo for the Incoming Message: " + e.toString();
            // TODO: reconsider sending this error back to other server, not much they can do about it, and it may not be a critical error causing the message to be rejected...
			errorMapList.add(UtilMisc.toMap("reasonCode", "CreateOagisMessageInfoError", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        Map result = FastMap.newInstance();
        result.put("contentType", "text/plain");
        
        if (errorMapList.size() > 0) {
            result.putAll(comiCtx);
            result.put("errorMapList", errorMapList);
            String errMsg = "Error Processing Received Messages";
            result.putAll(ServiceUtil.returnError(errMsg));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Action Performed Successfully"));
        return result;
    }
    
    public static Map receiveRmaAcknowledge(DispatchContext ctx, Map context) {
        InputStream in = (InputStream) context.get("inputStream");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        List errorMapList = FastList.newInstance();
        
        if (userLogin == null) {
            try {
                userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            } catch (GenericEntityException e) {
                String errMsg = "Error Getting UserLogin: " + e.toString();
                Debug.logError(e, errMsg, module);
            }
        }
        
        Document doc = null;
        try {
            doc = UtilXml.readXmlDocument(in, true, "ReceiveRmaAcknowledge");
        } catch (SAXException e) {
            String errMsg = "Error parsing the ReceiveRmaAcknowledgeResponse: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "SAXException", "description", errMsg));            
            Debug.logError(e, errMsg, module);
        } catch (ParserConfigurationException e) {
            String errMsg = "Error parsing the ReceiveRmaAcknowledgeResponse: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "ParserConfigurationException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        } catch (IOException e) {
            String errMsg = "Error parsing the ReceiveRmaAcknowledgeResponse: " + e.toString();
            errorMapList.add(UtilMisc.toMap("reasonCode", "IOException", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        if (errorMapList.size() > 0) {
            return ServiceUtil.returnError("Unable to parse message: ReceiveRmaAcknowledge");
        }
        // parse the message 
        Element receiveRmaElement = doc.getDocumentElement();
        receiveRmaElement.normalize();
        Element docCtrlAreaElement = UtilXml.firstChildElement(receiveRmaElement, "N1:CNTROLAREA");
        Element docSenderElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:SENDER");
        Element docBsrElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:BSR");
            
        String bsrVerb = UtilXml.childElementValue(docBsrElement, "N2:VERB");
        String bsrNoun = UtilXml.childElementValue(docBsrElement, "N2:NOUN");
        String bsrRevision = UtilXml.childElementValue(docBsrElement, "N2:REVISION");
            
        String logicalId = UtilXml.childElementValue(docSenderElement, "N2:LOGICALID");
        String component = UtilXml.childElementValue(docSenderElement, "N2:COMPONENT");
        String task = UtilXml.childElementValue(docSenderElement, "N2:TASK");
        String referenceId = UtilXml.childElementValue(docSenderElement, "N2:REFERENCEID");
        String confirmation = UtilXml.childElementValue(docSenderElement, "N2:CONFIRMATION");
        String authId = UtilXml.childElementValue(docSenderElement, "N2:AUTHID");
            
        Element dataAreaElement = UtilXml.firstChildElement(receiveRmaElement, "n:DATAAREA");
        Element acknowledgeDeliveryElement = UtilXml.firstChildElement(dataAreaElement, "n:ACKNOWLEDGE_DELIVERY");
        
        String inventoryItemTypeId = null;
        String returnId = null;
        String facilityId = UtilProperties.getPropertyValue("oagis.properties", "Oagis.Warehouse.PoReceiptFacilityId");
        String locationSeqId = UtilProperties.getPropertyValue("oagis.properties", "Oagis.Warehouse.ReturnReceiptLocationSeqId");
        
        // get RECEIPTLN elements from message
        List acknowledgeElementList = UtilXml.childElementList(acknowledgeDeliveryElement, "n:RECEIPTLN");
        if (UtilValidate.isNotEmpty(acknowledgeElementList)) {
            Iterator acknowledgeElementIter = acknowledgeElementList.iterator();
            while (acknowledgeElementIter.hasNext()) {
                Map ripCtx = FastMap.newInstance();
                Element receiptLnElement = (Element) acknowledgeElementIter.next();
                Element qtyElement = UtilXml.firstChildElement(receiptLnElement, "N1:QUANTITY");
                
                String itemQtyStr = UtilXml.childElementValue(qtyElement, "N2:VALUE");
                double itemQty = Double.parseDouble(itemQtyStr);
                String sign = UtilXml.childElementValue(qtyElement, "N2:SIGN");
                
                String productId = UtilXml.childElementValue(receiptLnElement, "N2:ITEM");
                
                Element documentRefElement = UtilXml.firstChildElement(receiptLnElement, "N1:DOCUMNTREF");
                returnId = UtilXml.childElementValue(documentRefElement, "N2:DOCUMENTID");
                ripCtx.put("returnId", returnId);
                
                String returnItemSeqId = UtilXml.childElementValue(documentRefElement, "N2:LINENUM");
                ripCtx.put("returnItemSeqId", returnItemSeqId);
                
                // get inventory item status
                String invItemStatus = UtilXml.childElementValue(receiptLnElement, "N2:DISPOSITN");
                if ( invItemStatus.equals("ReceivedTOAvailable") || invItemStatus.equals("NotAvailableTOAvailable")) {
                    ripCtx.put("statusId", "INV_AVAILABLE");    
                } else if ( invItemStatus.equals("ReceivedTONotAvailable") || invItemStatus.equals("AvailableTONotAvailable") ) {
                    ripCtx.put("statusId", "INV_ON_HOLD");
                }
                // get the serial number(s) 
                List serialNumsList = FastList.newInstance();
                List invDetailList = UtilXml.childElementList(receiptLnElement, "n:INVDETAIL");
                if (UtilValidate.isNotEmpty(invDetailList)) {
                    inventoryItemTypeId = "SERIALIZED_INV_ITEM";
                    ripCtx.put("inventoryItemTypeId", inventoryItemTypeId);
                    for (Iterator j = invDetailList.iterator(); j.hasNext();) {
                        Element invDetailElement = (Element) j.next();
                        String serialNumber = UtilXml.childElementValue(invDetailElement, "N2:SERIALNUM");
                        if (UtilValidate.isNotEmpty(serialNumber)) {
                            serialNumsList.add(serialNumber);
                        }
                    }

                    /* DEJ20070711 Commenting this out because it shouldn't happen, ie more likely the ITEM element will be filled 
                     * than INVDETAIL->SERIALNUM, and this isn't a reliable way to look it up (may be more than 1 record for a given 
                     * serialNumber for different products 
                    // this is a Serialized Inventory Item. If the productId from the message is not valid then lets read it from InventoryItem in Ofbiz database.
                    if (productId == null || "".equals(productId)) {
                        try {
                            GenericValue inventoryItem = EntityUtil.getFirst(delegator.findByAnd("InventoryItem", UtilMisc.toMap("serialNumber", serialNumber)));
                            if (inventoryItem !=null) {
                                productId = inventoryItem.getString("productId");
                            }
                        } catch (GenericEntityException e){
                            String errMsg = "Error Getting Entity InventoryItem";
                            Debug.logError(e, errMsg, module);
                        }
                    } */
                } else {
                    inventoryItemTypeId = "NON_SERIAL_INV_ITEM";
                    ripCtx.put("inventoryItemTypeId", inventoryItemTypeId);
                }
                ripCtx.put("productId", productId);
                ripCtx.put("facilityId",facilityId);
                ripCtx.put("locationSeqId", locationSeqId);
                ripCtx.put("userLogin", userLogin);

                // sign handling for items
                double quantityAccepted = 0.0;
                double quantityRejected = 0.0;
                if (sign.equals("+")) {
                    quantityAccepted = itemQty;
                    quantityRejected= 0.0;
                } else {
                    quantityRejected = itemQty;
                    quantityAccepted = 0.0;
                }
                if (quantityAccepted > 0) {
                    if (serialNumsList.size() > 0) {
                        if (serialNumsList.size() != quantityAccepted) {
                            // this is an error, do something about it, like add to the list to send back a Confirm BOD with error messages
                            String errMsg = "Error: the quantity [" + quantityAccepted + "] did not match the number of serial numbers passed [" + serialNumsList.size() + "].";
                            errorMapList.add(UtilMisc.toMap("reasonCode", "QuantitySerialMismatch", "description", errMsg));
                        }
                        
                        Iterator serialNumIter = serialNumsList.iterator();
                        while (serialNumIter.hasNext()) {
                            String serialNum = (String) serialNumIter.next();
                            
                            // clone the context as it may be changted in the call
                            Map localRipCtx = FastMap.newInstance();
                            localRipCtx.putAll(ripCtx);
                            
                            localRipCtx.put("quantityAccepted", new Double(1.0));
                            // always set this to 0, if needed we'll handle the rejected quantity separately
                            localRipCtx.put("quantityRejected", new Double(0.0));

                            localRipCtx.put("serialNumber", serialNum);
                            
                            try {
                                Map ripResult = dispatcher.runSync("receiveInventoryProduct", localRipCtx);
                                if (ServiceUtil.isError(ripResult)) {
                                    String errMsg = ServiceUtil.getErrorMessage(ripResult);
                                    errorMapList.add(UtilMisc.toMap("reasonCode", "ReceiveInventoryServiceError", "description", errMsg));
                                }
                            } catch (GenericServiceException e) {
                                String errMsg = "Error running service receiveInventoryProduct: " + e.toString();
                                errorMapList.add(UtilMisc.toMap("reasonCode", "GenericServiceException", "description", errMsg));
                                Debug.logError(e, errMsg, module);
                            }    
                        }
                    } else {
                        // no serial numbers, just receive the quantity
                        
                        // clone the context as it may be changted in the call
                        Map localRipCtx = FastMap.newInstance();
                        localRipCtx.putAll(ripCtx);
                        
                        localRipCtx.put("quantityAccepted", new Double(quantityAccepted));
                        // always set this to 0, if needed we'll handle the rejected quantity separately
                        localRipCtx.put("quantityRejected", new Double(0.0));
                        
                        try {
                            Map ripResult = dispatcher.runSync("receiveInventoryProduct", localRipCtx);
                            if (ServiceUtil.isError(ripResult)) {
                                String errMsg = ServiceUtil.getErrorMessage(ripResult);
                                errorMapList.add(UtilMisc.toMap("reasonCode", "ReceiveInventoryServiceError", "description", errMsg));
                            }
                        } catch (GenericServiceException e) {
                            String errMsg = "Error running service receiveInventoryProduct: " + e.toString();
                            errorMapList.add(UtilMisc.toMap("reasonCode", "GenericServiceException", "description", errMsg));
                            Debug.logError(e, errMsg, module);
                        }    
                    }
                } else {
                    // TODOLATER: need to run service receiveInventoryProduct and updateInventoryItem when quantityRejected > 0
                    // NOTE DEJ20070711 this shouldn't happen for current needs, so save for later
                }
            }
        }         
        //prepare result Map for createOagisMessageinfo
            
        Timestamp timestamp = null;
        timestamp = UtilDateTime.nowTimestamp();
        Map comiCtx = new HashMap(); 
        comiCtx.put("logicalId", logicalId);
        comiCtx.put("authId", authId);
        comiCtx.put("referenceId", referenceId);
        comiCtx.put("receivedDate", timestamp);
        comiCtx.put("component", component);
        comiCtx.put("task", task);  
        comiCtx.put("outgoingMessage", "N");
        comiCtx.put("confirmation", confirmation);
        comiCtx.put("bsrVerb", bsrVerb);
        comiCtx.put("bsrNoun", bsrNoun);
        comiCtx.put("bsrRevision", bsrRevision);
        comiCtx.put("userLogin", userLogin);
        try {
            Map comiResult = dispatcher.runSync("createOagisMessageInfo", comiCtx);
            if (ServiceUtil.isError(comiResult)) {
                String errMsg = ServiceUtil.getErrorMessage(comiResult);
                errorMapList.add(UtilMisc.toMap("reasonCode", "CreateOagisMessageServiceError", "description", errMsg));
            }
        } catch (GenericServiceException e) {
            String errMsg = "Error creating OagisMessageInfo for the Incoming Message: " + e.toString();
            // TODO: reconsider sending this error back to other server, not much they can do about it, and it may not be a critical error causing the message to be rejected...
            errorMapList.add(UtilMisc.toMap("reasonCode", "CreateOagisMessageInfoError", "description", errMsg));
            Debug.logError(e, errMsg, module);
        }
        
        Map result = FastMap.newInstance();
        result.put("contentType", "text/plain");
        if (errorMapList.size() > 0) {
            result.putAll(comiCtx);
            result.put("errorMapList", errorMapList);
            String errMsg = "Error Processing Received Messages";
            result.putAll(ServiceUtil.returnError(errMsg));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Action Performed Successfully"));
        return result;
    }
}
