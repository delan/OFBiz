/*
 *  $Id$
 *
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.accounting.thirdparty.clearcommerce;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.HttpClient;
import org.ofbiz.base.util.HttpClientException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.accounting.payment.PaymentGatewayServices;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;


/**
 * ClearCommerce Payment Services (CCE 5.4)
 *
 * @author     <a href="mailto:joe.eckard@redrocketcorp.com">Joe Eckard</a>
 * @version    $Rev:$
 */
public class CCPaymentServices {

    public final static String module = CCPaymentServices.class.getName();


    public static Map ccAuth(DispatchContext dctx, Map context) {

        Document authRequestDoc = buildPrimaryTxRequest(context, "PreAuth", (Double) context.get("processAmount"),
                (String) context.get("orderId"));

        Document authResponseDoc = null;
        try {
            authResponseDoc = sendRequest(authRequestDoc, (String) context.get("paymentConfig"));
        } catch (ClearCommerceException cce) {
            return ServiceUtil.returnError(cce.getMessage());
        }

        List errorList = processMessageList(authResponseDoc);
        if (UtilValidate.isNotEmpty(errorList)) {
            return ServiceUtil.returnError(errorList);
        }

        return processAuthResponse(authResponseDoc);
    }

    public static Map ccCredit(DispatchContext dctx, Map context) {

        Document creditRequestDoc = buildPrimaryTxRequest(context, "Credit", (Double) context.get("creditAmount"),
                (String) context.get("referenceCode"));

        Document creditResponseDoc = null;
        try {
            creditResponseDoc = sendRequest(creditRequestDoc, (String) context.get("paymentConfig"));
        } catch (ClearCommerceException cce) {
            return ServiceUtil.returnError(cce.getMessage());
        }

        List errorList = processMessageList(creditResponseDoc);
        if (UtilValidate.isNotEmpty(errorList)) {
            return ServiceUtil.returnError(errorList);
        }

        return processCreditResponse(creditResponseDoc);
    }

    public static Map ccCapture(DispatchContext dctx, Map context) {

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found; cannot capture");
        }

        Document captureRequestDoc = buildSecondaryTxRequest(context, authTransaction.getString("referenceNum"),
                "PostAuth", (Double) context.get("processAmount"));

        Document captureResponseDoc = null;
        try {
            captureResponseDoc = sendRequest(captureRequestDoc, (String) context.get("paymentConfig"));
        } catch (ClearCommerceException cce) {
            return ServiceUtil.returnError(cce.getMessage());
        }

        List errorList = processMessageList(captureResponseDoc);
        if (UtilValidate.isNotEmpty(errorList)) {
            return ServiceUtil.returnError(errorList);
        }

        return processCaptureResponse(captureResponseDoc);
    }

    public static Map ccRelease(DispatchContext dctx, Map context) {

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found; cannot release");
        }

        Document releaseRequestDoc = buildSecondaryTxRequest(context, authTransaction.getString("referenceNum"), "Void", null);

        Document releaseResponseDoc = null;
        try {
            releaseResponseDoc = sendRequest(releaseRequestDoc, (String) context.get("paymentConfig"));
        } catch (ClearCommerceException cce) {
            return ServiceUtil.returnError(cce.getMessage());
        }

        List errorList = processMessageList(releaseResponseDoc);
        if (UtilValidate.isNotEmpty(errorList)) {
            return ServiceUtil.returnError(errorList);
        }

        return processReleaseResponse(releaseResponseDoc);
    }

    public static Map ccRefund(DispatchContext dctx, Map context) {

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found; cannot refund");
        }

        // Although refunds are applied to captured transactions, using the auth reference number is ok here
        // Related auth and capture transactions will always have the same reference number
        Document refundRequestDoc = buildSecondaryTxRequest(context, authTransaction.getString("referenceNum"),
                "Credit", (Double) context.get("refundAmount"));

        Document refundResponseDoc = null;
        try {
            refundResponseDoc = sendRequest(refundRequestDoc, (String) context.get("paymentConfig"));
        } catch (ClearCommerceException cce) {
            return ServiceUtil.returnError(cce.getMessage());
        }

        List errorList = processMessageList(refundResponseDoc);
        if (UtilValidate.isNotEmpty(errorList)) {
            return ServiceUtil.returnError(errorList);
        }

        return processRefundResponse(refundResponseDoc);
    }

    public static Map ccReAuth(DispatchContext dctx, Map context) {
        // TODO: implement as soon as an interface is defined - JFE 2004.03.25
        return ServiceUtil.returnError("ClearCommerce ReAuth Service not implemented");
    }

    private static Map processAuthResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("authResult", Boolean.valueOf(true));
            result.put("authCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String authAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("processAmount", new Double(Double.parseDouble(authAmountStr) / 100));
        } else {
            result.put("authResult", Boolean.valueOf(false));
            result.put("processAmount", Double.valueOf("0.00"));
        }

        result.put("authRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("authFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("authMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        // AVS
        String avsCode = UtilXml.childElementValue(procResponseElement, "AvsDisplay");
        if (UtilValidate.isNotEmpty(avsCode)) {
            result.put("avsCode", avsCode);
        }

        // Fraud score
        Element fraudInfoElement = UtilXml.firstChildElement(orderFormElement, "FraudInfo");
        if (fraudInfoElement != null) {
            result.put("scoreCode", UtilXml.childElementValue(fraudInfoElement, "TotalScore"));
        }

        return result;
    }

    private static Map processCreditResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("creditResult", Boolean.valueOf(true));
            result.put("creditCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String creditAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("creditAmount", new Double(Double.parseDouble(creditAmountStr) / 100));
        } else {
            result.put("creditResult", Boolean.valueOf(false));
            result.put("creditAmount", Double.valueOf("0.00"));
        }

        result.put("creditRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("creditFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("creditMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        return result;
    }

    private static Map processCaptureResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("captureResult", Boolean.valueOf(true));
            result.put("captureCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String captureAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("captureAmount", new Double(Double.parseDouble(captureAmountStr) / 100));
        } else {
            result.put("captureResult", Boolean.valueOf(false));
            result.put("captureAmount", Double.valueOf("0.00"));
        }

        result.put("captureRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("captureFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("captureMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        return result;
    }

    private static Map processReleaseResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("releaseResult", Boolean.valueOf(true));
            result.put("releaseCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String releaseAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("releaseAmount", new Double(Double.parseDouble(releaseAmountStr) / 100));
        } else {
            result.put("releaseResult", Boolean.valueOf(false));
            result.put("releaseAmount", Double.valueOf("0.00"));
        }

        result.put("releaseRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("releaseFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("releaseMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        return result;
    }

    private static Map processRefundResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("refundResult", Boolean.valueOf(true));
            result.put("refundCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String refundAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("refundAmount", new Double(Double.parseDouble(refundAmountStr) / 100));
        } else {
            result.put("refundResult", Boolean.valueOf(false));
            result.put("refundAmount", Double.valueOf("0.00"));
        }

        result.put("refundRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("refundFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("refundMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        return result;
    }

    private static List processMessageList(Document responseDocument) {

        List errorList = new ArrayList();

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element messageListElement = UtilXml.firstChildElement(engineDocElement, "MessageList");
        List messageList = UtilXml.childElementList(messageListElement, "Message");

        for (Iterator i = messageList.iterator(); i.hasNext();) {
            Element messageElement = (Element) i.next();

            int severity = Integer.parseInt(UtilXml.childElementValue(messageElement, "Sev"));

            String message = "[" + UtilXml.childElementValue(messageElement, "Audience") + "] " +
                    UtilXml.childElementValue(messageElement, "Text") + " (" + severity + ")";

            /* Severity values
             *
             * 0 - Success
             * 1 - Debug
             * 2 - Informational
             * 3 - Notice
             * 4 - Warning
             * 5 - Error
             * 6 - Critical
             * 7 - Alert
             * 8 - Emergency
             */

            if (severity > 4) {
                Debug.logError(message, module);
                errorList.add(message);
            } else if (severity > 2) {
                Debug.logWarning(message, module);
            } else {
                Debug.logInfo(message, module);
            }
        }

        return errorList;
    }

    private static Document buildPrimaryTxRequest(Map context, String type, Double amount, String refNum) {

        String paymentConfig = (String) context.get("paymentConfig");
        if (UtilValidate.isEmpty(paymentConfig)) {
            paymentConfig = "payment.properties";
        }

        Document requestDocument = createRequestDocument(paymentConfig);

        Element engineDocElement = UtilXml.firstChildElement(requestDocument.getDocumentElement(), "EngineDoc");
        Element orderFormDocElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");

        // add the reference number as a comment
        UtilXml.addChildElementValue(orderFormDocElement, "Comments", refNum, requestDocument);

        Element consumerElement = UtilXml.addChildElement(orderFormDocElement, "Consumer", requestDocument);

        // email address
        GenericValue contactEmail = (GenericValue) context.get("contactEmail");
        if (contactEmail != null) {
            UtilXml.addChildElementValue(consumerElement, "Email", contactEmail.getString("infoString"), requestDocument);
        }

        // payment mech
        GenericValue creditCard = (GenericValue) context.get("creditCard");

        boolean enableCVM = UtilProperties.propertyValueEqualsIgnoreCase(paymentConfig, "payment.clearcommerce.enableCVM", "Y");
        String cardSecurityCode = enableCVM ? (String) context.get("cardSecurityCode") : null;

        // Default to locale code 840 (United States)
        String localCode = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.localeCode", "840");

        appendPaymentMechNode(consumerElement, creditCard, cardSecurityCode, localCode);

        // billing address
        GenericValue billingAddress = (GenericValue) context.get("billingAddress");
        if (billingAddress != null) {
            Element billToElement = UtilXml.addChildElement(consumerElement, "BillTo", requestDocument);
            Element billToLocationElement = UtilXml.addChildElement(billToElement, "Location", requestDocument);
            appendAddressNode(billToLocationElement, billingAddress);
        }

        // shipping address
        GenericValue shippingAddress = (GenericValue) context.get("shippingAddress");
        if (shippingAddress != null) {
            Element shipToElement = UtilXml.addChildElement(consumerElement, "ShipTo", requestDocument);
            Element shipToLocationElement = UtilXml.addChildElement(shipToElement, "Location", requestDocument);
            appendAddressNode(shipToLocationElement, shippingAddress);
        }

        // Default to currency code 840 (USD)
        String currencyCode = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.currencyCode", "840");

        // transaction
        appendTransactionNode(orderFormDocElement, type, amount, currencyCode);

        // TODO: determine if adding OrderItemList is worthwhile - JFE 2004.02.14

        return requestDocument;
    }

    private static Document buildSecondaryTxRequest(Map context, String id, String type, Double amount) {

        String paymentConfig = (String) context.get("paymentConfig");
        if (UtilValidate.isEmpty(paymentConfig)) {
            paymentConfig = "payment.properties";
        }

        Document requestDocument = createRequestDocument(paymentConfig);

        Element engineDocElement = UtilXml.firstChildElement(requestDocument.getDocumentElement(), "EngineDoc");
        Element orderFormDocElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        UtilXml.addChildElementValue(orderFormDocElement, "Id", id, requestDocument);

        // Default to currency code 840 (USD)
        String currencyCode = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.currencyCode", "840");

        appendTransactionNode(orderFormDocElement, type, amount, currencyCode);

        return requestDocument;
    }

    private static void appendPaymentMechNode(Element element, GenericValue creditCard, String cardSecurityCode, String localeCode) {

        Document document = element.getOwnerDocument();

        Element paymentMechElement = UtilXml.addChildElement(element, "PaymentMech", document);
        Element creditCardElement = UtilXml.addChildElement(paymentMechElement, "CreditCard", document);

        UtilXml.addChildElementValue(creditCardElement, "Number", creditCard.getString("cardNumber"), document);

        String expDate = creditCard.getString("expireDate");
        Element expiresElement = UtilXml.addChildElementValue(creditCardElement, "Expires",
                expDate.substring(0, 3) + expDate.substring(5), document);
        expiresElement.setAttribute("DataType", "ExpirationDate");
        expiresElement.setAttribute("Locale", localeCode);

        if (UtilValidate.isNotEmpty(cardSecurityCode)) {
            // Cvv2Val must be exactly 4 characters
            if (cardSecurityCode.length() < 4) {
                while (cardSecurityCode.length() < 4) {
                    cardSecurityCode = cardSecurityCode + " ";
                }
            } else if (cardSecurityCode.length() > 4) {
                cardSecurityCode = cardSecurityCode.substring(0, 4);
            }
            UtilXml.addChildElementValue(creditCardElement, "Cvv2Val", cardSecurityCode, document);
            UtilXml.addChildElementValue(creditCardElement, "Cvv2Indicator", "1", document);
        }
    }

    private static void appendAddressNode(Element element, GenericValue address) {

        Document document = element.getOwnerDocument();

        Element addressElement = UtilXml.addChildElement(element, "Address", document);

        UtilXml.addChildElementValue(addressElement, "Name", address.getString("toName"), document);
        UtilXml.addChildElementValue(addressElement, "Street1", address.getString("address1"), document);
        UtilXml.addChildElementValue(addressElement, "Street2", address.getString("address2"), document);
        UtilXml.addChildElementValue(addressElement, "City", address.getString("city"), document);
        UtilXml.addChildElementValue(addressElement, "StateProv", address.getString("stateProvinceGeoId"), document);
        UtilXml.addChildElementValue(addressElement, "PostalCode", address.getString("postalCode"), document);

        String countryGeoId = address.getString("countryGeoId");
        if (UtilValidate.isNotEmpty(countryGeoId)) {
            try {
                GenericValue countryGeo = address.getRelatedOneCache("CountryGeo");
                UtilXml.addChildElementValue(addressElement, "Country", countryGeo.getString("geoSecCode"), document);
            } catch (GenericEntityException gee) {
                Debug.log(gee, "Error finding related Geo for countryGeoId: " + countryGeoId, module);
            }
        }
    }

    private static void appendTransactionNode(Element element, String type, Double amount, String currencyCode) {

        Document document = element.getOwnerDocument();

        Element transactionElement = UtilXml.addChildElement(element, "Transaction", document);
        UtilXml.addChildElementValue(transactionElement, "Type", type, document);

        // Some transactions will not have an amount (release, reAuth)
        if (amount != null) {
            Element currentTotalsElement = UtilXml.addChildElement(transactionElement, "CurrentTotals", document);
            Element totalsElement = UtilXml.addChildElement(currentTotalsElement, "Totals", document);

            // DecimalFormat("#") is used here in case the total is something like 9.9999999...
            // in that case, we want to send 999, not 999.9999999...
            String totalString = new DecimalFormat("#").format(amount.doubleValue() * 100);

            Element totalElement = UtilXml.addChildElementValue(totalsElement, "Total", totalString, document);
            totalElement.setAttribute("DataType", "Money");
            totalElement.setAttribute("Currency", currencyCode);
        }
    }

    private static Document createRequestDocument(String paymentConfig) {

        // EngineDocList
        Document requestDocument = UtilXml.makeEmptyXmlDocument("EngineDocList");
        Element engineDocListElement = requestDocument.getDocumentElement();
        UtilXml.addChildElementValue(engineDocListElement, "DocVersion", "1.0", requestDocument);

        // EngineDocList.EngineDoc
        Element engineDocElement = UtilXml.addChildElement(engineDocListElement, "EngineDoc", requestDocument);
        UtilXml.addChildElementValue(engineDocElement, "ContentType", "OrderFormDoc", requestDocument);

        String sourceId = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.sourceId");
        if (UtilValidate.isNotEmpty(sourceId)) {
            UtilXml.addChildElementValue(engineDocElement, "SourceId", sourceId, requestDocument);
        }

        String groupId = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.groupId");
        if (UtilValidate.isNotEmpty(groupId)) {
            UtilXml.addChildElementValue(engineDocElement, "GroupId", groupId, requestDocument);
        }

        // EngineDocList.EngineDoc.User
        Element userElement = UtilXml.addChildElement(engineDocElement, "User", requestDocument);
        UtilXml.addChildElementValue(userElement, "Name",
                UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.username", ""), requestDocument);
        UtilXml.addChildElementValue(userElement, "Password",
                UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.password", ""), requestDocument);
        UtilXml.addChildElementValue(userElement, "Alias",
                UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.alias", ""), requestDocument);

        String effectiveAlias = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.effectiveAlias");
        if (UtilValidate.isNotEmpty(effectiveAlias)) {
            UtilXml.addChildElementValue(userElement, "EffectiveAlias", effectiveAlias, requestDocument);
        }

        // EngineDocList.EngineDoc.Instructions
        Element instructionsElement = UtilXml.addChildElement(engineDocElement, "Instructions", requestDocument);

        String pipeline = "PaymentNoFraud";
        if (UtilProperties.propertyValueEqualsIgnoreCase(paymentConfig, "payment.clearcommerce.enableFraudShield", "Y")) {
            pipeline = "Payment";
        }
        UtilXml.addChildElementValue(instructionsElement, "Pipeline", pipeline, requestDocument);

        // EngineDocList.EngineDoc.OrderFormDoc
        Element orderFormDocElement = UtilXml.addChildElement(engineDocElement, "OrderFormDoc", requestDocument);

        // default to "P" for Production Mode
        String mode = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.processMode", "P");
        UtilXml.addChildElementValue(orderFormDocElement, "Mode", mode, requestDocument);

        return requestDocument;
    }

    private static Document sendRequest(Document requestDocument, String paymentConfig) throws ClearCommerceException {

        if (UtilValidate.isEmpty(paymentConfig)) {
            paymentConfig = "payment.properties";
        }

        String serverURL = UtilProperties.getPropertyValue(paymentConfig, "payment.clearcommerce.serverURL");
        if (UtilValidate.isEmpty(serverURL)) {
            throw new ClearCommerceException("Missing server URL; check your ClearCommerce configuration");
        }
        if (Debug.verboseOn()) {
            Debug.logVerbose("ClearCommerce server URL: " + serverURL, module);
        }

        OutputStream os = new ByteArrayOutputStream();

        OutputFormat format = new OutputFormat();
        format.setOmitDocumentType(true);
        format.setOmitXMLDeclaration(true);
        format.setIndenting(false);

        XMLSerializer serializer = new XMLSerializer(os, format);
        try {
            serializer.asDOMSerializer();
            serializer.serialize(requestDocument.getDocumentElement());
        } catch (IOException ioe) {
            throw new ClearCommerceException("Error serializing requestDocument: " + ioe.getMessage());
        }

        String xmlString = os.toString();

        if (Debug.verboseOn()) {
            Debug.logVerbose("ClearCommerce XML request string: " + xmlString, module);
        }

        HttpClient http = new HttpClient(serverURL);
        http.setParameter("CLRCMRC_XML", xmlString);

        String response = null;
        try {
            response = http.post();
        } catch (HttpClientException hce) {
            Debug.log(hce, module);
            throw new ClearCommerceException("ClearCommerce connection problem", hce);
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("ClearCommerce response: " + response, module);
        }

        Document responseDocument = null;
        try {
            responseDocument = UtilXml.readXmlDocument(response, false);
        } catch (SAXException se) {
            throw new ClearCommerceException("Error reading response Document from a String: " + se.getMessage());
        } catch (ParserConfigurationException pce) {
            throw new ClearCommerceException("Error reading response Document from a String: " + pce.getMessage());
        } catch (IOException ioe) {
            throw new ClearCommerceException("Error reading response Document from a String: " + ioe.getMessage());
        }

        return responseDocument;
    }

}

class ClearCommerceException extends GeneralException {

    ClearCommerceException() {
        super();
    }


    ClearCommerceException(String msg) {
        super(msg);
    }


    ClearCommerceException(Throwable t) {
        super(t);
    }


    ClearCommerceException(String msg, Throwable t) {
        super(msg, t);
    }
}

