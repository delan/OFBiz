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
 *
 */
package org.ofbiz.commonapp.thirdparty.clearcommerce;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.DispatchContext;
import org.ofbiz.core.service.ServiceUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilProperties;

import com.clearcommerce.ccxclientapi.CcApiDocument;
import com.clearcommerce.ccxclientapi.CcApiException;
import com.clearcommerce.ccxclientapi.CcApiMoney;
import com.clearcommerce.ccxclientapi.CcApiRecord;

/**
 * ClearCommerce Integration Services
 *
 * @author     <a href="mailto:joe.eckard@redrocketcorp.com">Joe Eckard</a>
 * @version    $Revision$
 * @since      2.2
 */
public class CCPaymentServices {

    public static final String module = CCPaymentServices.class.getName();

    public static final String TYPE_AUTH = "PreAuth";
    public static final String TYPE_CAPTURE = "PostAuth";
    
    public static Map ccAuthProcessor(DispatchContext dctx, Map context) {
        return ccProcessor(dctx, context, TYPE_AUTH);
    }
    
    public static Map ccCaptureProcessor(DispatchContext dctx, Map context) {
        return ccProcessor(dctx, context, TYPE_CAPTURE);
    }
    
    private static Map ccProcessor(DispatchContext dctx, Map context, String txType) {        
        Map result = new HashMap();
        
        // quick parameter check
        if (!txType.matches(TYPE_AUTH + "|" + TYPE_CAPTURE)) {
            return ServiceUtil.returnError("Invalid transaction type specified.");
        }
        
        try {           
            CcApiDocument requestDoc = new CcApiDocument();
            
            if (txType.equals(TYPE_AUTH)) {
                buildAuthRequest(requestDoc, context);
            } else if (txType.equals(TYPE_CAPTURE)) {
                buildCaptureRequest(requestDoc, context);
            }
            
            if (Debug.verboseOn()) {
                Writer requestWriter = new StringWriter();

                requestDoc.writeTo(requestWriter);
                Debug.logVerbose("---- ClearCommerce Request ----", module);
                Debug.logVerbose("\n\n" + requestWriter, module);
                Debug.logVerbose("---- End Request ----", module);
            }

            CcApiDocument responseDoc = requestDoc.process();
            
            if (Debug.verboseOn()) {
                Writer responseWriter = new StringWriter();

                responseDoc.writeTo(responseWriter);
                Debug.logVerbose("---- ClearCommerce Response ----", module);
                Debug.logVerbose("\n\n" + responseWriter, module);
                Debug.logVerbose("---- End Response ----", module);
            }
            
            CcApiRecord recEngineDoc = responseDoc.getFirstRecord("EngineDoc");

            if (recEngineDoc == null) {
                return ServiceUtil.returnError("ERROR: ClearCommerce response did not contain an EngineDoc record.");
            }

            // check for messages
            CcApiRecord recMessageList = recEngineDoc.getFirstRecord("MessageList");

            if (recMessageList != null) {    
                CcApiRecord recMessage = recMessageList.getFirstRecord("Message");
                List ccApiMessageList = new ArrayList();

                while (recMessage != null) {
                    StringBuffer ccApiMessage = new StringBuffer();

                    ccApiMessage.append("Audience=" + recMessage.getFieldString("Audience") + ", ");
                    ccApiMessage.append("ContextId=" + recMessage.getFieldString("ContextId") + ", ");
                    ccApiMessage.append("Component=" + recMessage.getFieldString("Component") + ", ");
                    ccApiMessage.append("Sev=" + recMessage.getFieldS32("Sev") + ", ");
                    ccApiMessage.append("Text=" + recMessage.getFieldString("Text"));
                    ccApiMessageList.add(ccApiMessage.toString());
                    recMessage = recMessageList.getNextRecord("Message");
                }

                if (!ccApiMessageList.isEmpty()) {
                    Debug.logWarning("ClearCommerce response message(s): " + ccApiMessageList, module);
                    Integer maxSeverity = recMessageList.getFieldS32("MaxSev");

                    if (maxSeverity.intValue() > 4) {
                        return ServiceUtil.returnError(ccApiMessageList);
                    }
                }
            }
            
            CcApiRecord recOrderFormDoc = recEngineDoc.getFirstRecord("OrderFormDoc");

            if (recOrderFormDoc == null) {
                return ServiceUtil.returnError("ERROR: ClearCommerce response did not contain an OrderFormDoc record.");
            }
            CcApiRecord recTransaction = recOrderFormDoc.getFirstRecord("Transaction");

            if (recTransaction == null) {
                return ServiceUtil.returnError("ERROR: ClearCommerce response did not contain a Transaction record.");
            }
            CcApiRecord recProcResponse = recTransaction.getFirstRecord("CardProcResp");

            if (recProcResponse == null) {
                return ServiceUtil.returnError("ERROR: ClearCommerce response did not contain a CardProcResp record.");
            }
            
            if (txType.equals(TYPE_AUTH)) {
                processAuthResponse(responseDoc, result);
            } else if (txType.equals(TYPE_CAPTURE)) {
                processCaptureResponse(responseDoc, result);
            }
            
        } catch (CcApiException ce) {
            ce.printStackTrace();
            return ServiceUtil.returnError("ERROR: ClearCommerce Problem (" + ce.getMessage() + ").");
        } catch (GenericEntityException gee) {
            gee.printStackTrace();
            return ServiceUtil.returnError("ERROR: Could not get order information (" + gee.getMessage() + ").");
        }
        
        return result;
    }
        
    private static void buildAuthRequest(CcApiDocument doc, Map context) throws CcApiException, GenericEntityException {                
        String configString = (String) context.get("paymentConfig");

        if (configString == null) {
            configString = "payment.properties";
        }
        
        initDoc(doc, context);
            
        CcApiRecord recEngineDoc = doc.getFirstRecord("EngineDoc");
            
        // EngineDocList.EngineDoc.Instructions
        CcApiRecord recInstructions = recEngineDoc.addRecord("Instructions");

        recInstructions.setFieldString("Pipeline", "PaymentNoFraud");

        // EngineDocList.EngineDoc.OrderFormDoc
        CcApiRecord recOrderFormDoc = recEngineDoc.getFirstRecord("OrderFormDoc");
        String orderId = (String) context.get("orderId");

        recOrderFormDoc.setFieldString("Id", orderId);

        // EngineDocList.EngineDoc.OrderFormDoc.Consumer
        CcApiRecord recConsumer = recOrderFormDoc.addRecord("Consumer");
        GenericValue contactEmail = (GenericValue) context.get("contactEmail");

        recConsumer.setFieldString("Email", contactEmail.getString("infoString"));

        // EngineDocList.EngineDoc.OrderFormDoc.Consumer.PaymentMech
        CcApiRecord recPaymentMech = recConsumer.addRecord("PaymentMech");

        recPaymentMech.setFieldString("Type", "CreditCard");

        // EngineDocList.EngineDoc.OrderFormDoc.Consumer.PaymentMech.CreditCard
        CcApiRecord recCreditCard = recPaymentMech.addRecord("CreditCard");
        GenericValue creditCard = (GenericValue) context.get("creditCard");

        recCreditCard.setFieldString("Number", creditCard.getString("cardNumber"));
        String expDate = creditCard.getString("expireDate");

        recCreditCard.setFieldExpirationDate("Expires", expDate.substring(0, 3) + expDate.substring(5));     
        
        boolean enableCVM = UtilProperties.propertyValueEqualsIgnoreCase(configString, "payment.clearcommerce.enableCVM", "Y");
        String cvmCode = (String) context.get("cardSecurityCode");

        if (enableCVM && (cvmCode != null)) {
            if (cvmCode.length() < 4) {
                StringBuffer sb = new StringBuffer(cvmCode);

                while (sb.length() < 4) {
                    sb.append(" ");
                }
                cvmCode = sb.toString();
            }
            recCreditCard.setFieldString("Cvv2Val", cvmCode.substring(0, 4)); // this must be exactly 4 characters
            recCreditCard.setFieldString("Cvv2Indicator", "1");
        }
            
        // EngineDocList.EngineDoc.OrderFormDoc.Consumer.BillTo
        CcApiRecord recBillTo = recConsumer.addRecord("BillTo");

        // EngineDocList.EngineDoc.OrderFormDoc.Consumer.BillTo.Location
        CcApiRecord recLocation = recBillTo.addRecord("Location");

        // EngineDocList.EngineDoc.OrderFormDoc.Consumer.BillTo.Location.Address
        CcApiRecord recAddress = recLocation.addRecord("Address");
        GenericValue contactPerson = (GenericValue) context.get("contactPerson");
        String billToName = new String(contactPerson.getString("firstName") + " " + contactPerson.getString("lastName"));

        recAddress.setFieldString("Name", billToName);
        GenericValue billingAddress = (GenericValue) context.get("billingAddress");

        recAddress.setFieldString("Street1", billingAddress.getString("address1"));
        if (billingAddress.get("address2") != null) {
            recAddress.setFieldString("Street2", billingAddress.getString("address2"));
        }
        recAddress.setFieldString("City", billingAddress.getString("city"));
        if (billingAddress.get("stateProvinceGeoId") != null) {
            recAddress.setFieldString("StateProv", billingAddress.getString("stateProvinceGeoId"));
        }
        recAddress.setFieldString("PostalCode", billingAddress.getString("postalCode"));
        GenericValue geo = billingAddress.getRelatedOneCache("CountryGeo");

        recAddress.setFieldString("Country", geo.getString("geoSecCode"));
            
        // EngineDocList.EngineDoc.OrderFormDoc.Transaction
        CcApiRecord recTransaction = recOrderFormDoc.addRecord("Transaction");

        recTransaction.setFieldString("Type", TYPE_AUTH);

        // EngineDocList.EngineDoc.OrderFormDoc.Transaction.CurrentTotals
        CcApiRecord recCurrentTotals = recTransaction.addRecord("CurrentTotals");

        // Used in the following code to format for CcApiMoney
        NumberFormat nf = new DecimalFormat("#");
        
        // EngineDocList.EngineDoc.OrderFormDoc.Transaction.CurrentTotals.Totals
        CcApiRecord recTotals = recCurrentTotals.addRecord("Totals");
        Double processAmount = (Double) context.get("processAmount");
        CcApiMoney total = new CcApiMoney(nf.format(processAmount.doubleValue() * 100), "840");

        recTotals.setFieldMoney("Total", total);
            
        List orderItems = (List) context.get("orderItems");
        Iterator itemIterator = orderItems.iterator();
        CcApiRecord recOrderItemList = recOrderFormDoc.addRecord("OrderItemList");
            
        while (itemIterator.hasNext()) {
            GenericValue item = (GenericValue) itemIterator.next();
            GenericValue product = item.getRelatedOneCache("Product");
            CcApiRecord recOrderItem = recOrderItemList.addRecord("OrderItem");
            Integer orderItemSeqId = new Integer(item.getString("orderItemSeqId"));

            recOrderItem.setFieldS32("ItemNumber", orderItemSeqId.intValue());
            recOrderItem.setFieldString("Id", product.getString("productId"));
            Double qty = new Double(item.getString("quantity"));

            recOrderItem.setFieldS32("Qty", qty.intValue());
            recOrderItem.setFieldString("Desc", item.getString("itemDescription"));
            CcApiMoney unitPrice = new CcApiMoney(nf.format(item.getDouble("unitPrice").doubleValue() * 100), "840");

            recOrderItem.setFieldMoney("Price", unitPrice);    
        }
            
        return;
        
    }
    
    private static void buildCaptureRequest(CcApiDocument doc, Map context) throws CcApiException, GenericEntityException {        
        initDoc(doc, context);
        
        CcApiRecord recEngineDoc = doc.getFirstRecord("EngineDoc");

        // EngineDocList.EngineDoc.Instructions
        CcApiRecord recInstructions = recEngineDoc.addRecord("Instructions");

        recInstructions.setFieldString("Pipeline", "PaymentNoFraud");

        // EngineDocList.EngineDoc.OrderFormDoc
        CcApiRecord recOrderFormDoc = recEngineDoc.getFirstRecord("OrderFormDoc");
        GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
        String orderId = paymentPref.getString("orderId");

        recOrderFormDoc.setFieldString("Id", orderId);
            
        // EngineDocList.EngineDoc.OrderFormDoc.Transaction
        CcApiRecord recTransaction = recOrderFormDoc.addRecord("Transaction");

        recTransaction.setFieldString("Type", TYPE_CAPTURE);

        // EngineDocList.EngineDoc.OrderFormDoc.Transaction.CurrentTotals
        CcApiRecord recCurrentTotals = recTransaction.addRecord("CurrentTotals");
            
        // EngineDocList.EngineDoc.OrderFormDoc.Transaction.CurrentTotals.Totals
        CcApiRecord recTotals = recCurrentTotals.addRecord("Totals");
        Double recCaptureAmount = (Double) context.get("captureAmount");
        NumberFormat nf = new DecimalFormat("#");
        CcApiMoney total = new CcApiMoney(nf.format(recCaptureAmount.doubleValue() * 100), "840");

        recTotals.setFieldMoney("Total", total);
        
        return;
    }

    private static void processAuthResponse(CcApiDocument response, Map result) throws CcApiException {        
        CcApiRecord recEngineDoc = response.getFirstRecord("EngineDoc");
        CcApiRecord recOrderFormDoc = recEngineDoc.getFirstRecord("OrderFormDoc");
        CcApiRecord recTransaction = recOrderFormDoc.getFirstRecord("Transaction");
        CcApiRecord recProcResponse = recTransaction.getFirstRecord("CardProcResp");
        
        Integer errCode = recProcResponse.getFieldS32("CcErrCode");
 
        if (errCode.intValue() == 1) {
            result.put("authCode", recTransaction.getFieldString("AuthCode"));
            result.put("authResult", new Boolean(true));
            CcApiRecord recCurrentTotals = recTransaction.getFirstRecord("CurrentTotals");
            CcApiRecord recTotals = recCurrentTotals.getFirstRecord("Totals");
            CcApiMoney authAmount = recTotals.getFieldMoney("Total"); 

            result.put("processAmount", new Double(authAmount.getAmount()));
        } else {
            result.put("authResult", new Boolean(false));
            result.put("processAmount", new Double(0.00));
        }
        result.put("authRefNum", recTransaction.getFieldString("Id"));
        result.put("authFlag", recProcResponse.getFieldString("Status"));
        result.put("authMessage", recProcResponse.getFieldString("CcReturnMsg"));
        String avsDisplay = recProcResponse.getFieldString("AvsDisplay");

        if (avsDisplay != null) {
            result.put("avsCode", avsDisplay);
        }
    }
    
    private static void processCaptureResponse(CcApiDocument response, Map result) throws CcApiException {                
        CcApiRecord recEngineDoc = response.getFirstRecord("EngineDoc");
        CcApiRecord recOrderFormDoc = recEngineDoc.getFirstRecord("OrderFormDoc");
        CcApiRecord recTransaction = recOrderFormDoc.getFirstRecord("Transaction");
        CcApiRecord recProcResponse = recTransaction.getFirstRecord("CardProcResp");
        
        Integer errCode = recProcResponse.getFieldS32("CcErrCode");
        
        if (errCode.intValue() == 1) {
            CcApiRecord recCurrentTotals = recTransaction.getFirstRecord("CurrentTotals");
            CcApiRecord recTotals = recCurrentTotals.getFirstRecord("Totals");
            CcApiMoney captureAmount = recTotals.getFieldMoney("Total");

            result.put("captureAmount", new Double(captureAmount.getAmount()));
            result.put("captureResult", new Boolean(true));
        } else {
            result.put("captureAmount", new Double(0.00));
            result.put("captureResult", new Boolean(false));
        }
        result.put("captureRefNum", recTransaction.getFieldString("Id"));
    }
    
    private static void initDoc(CcApiDocument doc, Map context) throws CcApiException {            
        String configString = (String) context.get("paymentConfig");

        if (configString == null) {
            configString = "payment.properties";
        }

        // Some default values  
        String sourceId = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.sourceId", "mySourceId");
        String groupId = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.groupId", "myGroup");
        String userName = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.username", "myUsername");
        String userPassword = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.password", "myPassword");
        String alias = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.alias", "myAlias");
        String effectiveAlias = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.effectiveAlias", "");
        String processMode = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.processMode", "Y");
        String hostAddress = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.hostAddress", "test5x.clearcommerce.com");
        String hostPort = UtilProperties.getPropertyValue(configString, "payment.clearcommerce.hostPort", "12000");
        	    
        // Connection params
        doc.setHost(hostAddress);
        doc.setPort(Short.parseShort(hostPort));
        doc.useCRYPTO();
 
        // EngineDocList
        doc.setFieldString("DocVersion", "1.0");
			
        // EngineDocList.EngineDoc
        CcApiRecord engineDoc = doc.addRecord("EngineDoc");

        engineDoc.setFieldString("DocumentId", "1");
        engineDoc.setFieldString("ContentType", "OrderFormDoc");
        engineDoc.setFieldString("SourceId", sourceId);
            
        // EngineDocList.EngineDoc.User
        CcApiRecord user = engineDoc.addRecord("User");

        user.setFieldString("Name", userName);
        user.setFieldString("Password", userPassword);
        user.setFieldString("Alias", alias);
        if (!effectiveAlias.equals("")) {
            user.setFieldString("EffectiveAlias", effectiveAlias);
        }
			
        // EngineDocList.EngineDoc.OrderFormDoc
        CcApiRecord orderFormDoc = engineDoc.addRecord("OrderFormDoc");

        orderFormDoc.setFieldString("Mode", processMode);
        orderFormDoc.setFieldString("GroupId", groupId);
        
        return;
    }

}
