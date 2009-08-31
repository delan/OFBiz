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
package org.ofbiz.ebay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProductsExportToEbay {

    private static final String resource = "EbayUiLabels";
    private static final String configFileName = "ebayExport.properties";
    private static final String module = ProductsExportToEbay.class.getName();

    public static Map exportToEbay(DispatchContext dctx, Map context) {
        Locale locale = (Locale) context.get("locale");
        Map result = null;
        try {
            String configFileName = "ebayExport.properties";

            // get the Developer Key
            String devID = UtilProperties.getPropertyValue(configFileName, "eBayExport.devID");

            // get the Application Key
            String appID = UtilProperties.getPropertyValue(configFileName, "eBayExport.appID");

            // get the Certifcate Key
            String certID = UtilProperties.getPropertyValue(configFileName, "eBayExport.certID");

            // get the Token
            String token = UtilProperties.getPropertyValue(configFileName, "eBayExport.token");

            // get the Compatibility Level
            String compatibilityLevel = UtilProperties.getPropertyValue(configFileName, "eBayExport.compatibilityLevel");

            // get the Site ID
            String siteID = UtilProperties.getPropertyValue(configFileName, "eBayExport.siteID");

            // get the xmlGatewayUri
            String xmlGatewayUri = UtilProperties.getPropertyValue(configFileName, "eBayExport.xmlGatewayUri");

            StringBuffer dataItemsXml = new StringBuffer();

            /*
            String itemId = "";
            if (!ServiceUtil.isFailure(buildAddTransactionConfirmationItemRequest(context, dataItemsXml, token,  itemId))) {
                Map result = postItem(xmlGatewayUri, dataItemsXml, devID, appID, certID, "AddTransactionConfirmationItem");
                Debug.logInfo(result.toString(), module);
            }
            */

            Map resultMap = buildDataItemsXml(dctx, context, dataItemsXml, token);
            if (!ServiceUtil.isFailure(resultMap)) {
                result = postItem(xmlGatewayUri, dataItemsXml, devID, appID, certID, "AddItem", compatibilityLevel, siteID);
                if (ServiceUtil.isFailure(result)) {
                    return ServiceUtil.returnFailure(ServiceUtil.getErrorMessage(result));
                }
            } else {
                return ServiceUtil.returnFailure(ServiceUtil.getErrorMessage(resultMap));
            }
        } catch (Exception e) {
            Debug.logError("Exception in exportToEbay " + e, module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.exceptionInExportToEbay", locale));
        }
        String successMessage = UtilProperties.getMessage(resource, "productsExportToEbay.productItemsSentToEbay", locale);
        if (result != null) {
            String responseString = (String)result.get("successMessage");
            if (UtilValidate.isNotEmpty(responseString)) {
                successMessage = responseString;
            }
        }
        return ServiceUtil.returnSuccess(successMessage);
    }

    private static void appendRequesterCredentials(Element elem, Document doc, String token) {
        Element requesterCredentialsElem = UtilXml.addChildElement(elem, "RequesterCredentials", doc);
        UtilXml.addChildElementValue(requesterCredentialsElem, "eBayAuthToken", token, doc);
    }

    private static String toString(InputStream inputStream) throws IOException {
        String string;
        StringBuilder outputBuilder = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (null != (string = reader.readLine())) {
                outputBuilder.append(string).append('\n');
            }
        }
        return outputBuilder.toString();
    }

    private static Map postItem(String postItemsUrl, StringBuffer dataItems, String devID, String appID, String certID,
                                String callName, String compatibilityLevel, String siteID) throws IOException {
        if (Debug.verboseOn()) {
            Debug.logVerbose("Request of " + callName + " To eBay:\n" + dataItems.toString(), module);
        }
        HttpURLConnection connection = (HttpURLConnection)(new URL(postItemsUrl)).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-EBAY-API-COMPATIBILITY-LEVEL", compatibilityLevel);
        connection.setRequestProperty("X-EBAY-API-DEV-NAME", devID);
        connection.setRequestProperty("X-EBAY-API-APP-NAME", appID);
        connection.setRequestProperty("X-EBAY-API-CERT-NAME", certID);
        connection.setRequestProperty("X-EBAY-API-CALL-NAME", callName);
        connection.setRequestProperty("X-EBAY-API-SITEID", siteID);
        connection.setRequestProperty("Content-Type", "text/xml");

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(dataItems.toString().getBytes());
        outputStream.close();
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        Map result = FastMap.newInstance();
        String response = null;

        if (responseCode == HttpURLConnection.HTTP_CREATED ||
            responseCode == HttpURLConnection.HTTP_OK) {
            inputStream = connection.getInputStream();
            response = toString(inputStream);
            result = ServiceUtil.returnSuccess(response);
        } else {
            inputStream = connection.getErrorStream();
            response = toString(inputStream);
            result = ServiceUtil.returnFailure(response);
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("Response of " + callName + " From eBay:\n" + response, module);
        }

        return result;
    }

    private static Map buildDataItemsXml(DispatchContext dctx, Map context, StringBuffer dataItemsXml, String token) {
        Locale locale = (Locale)context.get("locale");
        try {
            GenericDelegator delegator = dctx.getDelegator();
            String webSiteUrl = (String)context.get("webSiteUrl");
            List selectResult = (List)context.get("selectResult");

            StringUtil.SimpleEncoder encoder = StringUtil.getEncoder("xml");

            // Get the list of products to be exported to eBay
            List productsList  = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, selectResult), null, null, null, false);

            try {
                Document itemDocument = UtilXml.makeEmptyXmlDocument("AddItemRequest");
                Element itemRequestElem = itemDocument.getDocumentElement();
                itemRequestElem.setAttribute("xmlns", "urn:ebay:apis:eBLBaseComponents");

                appendRequesterCredentials(itemRequestElem, itemDocument, token);

                // Iterate the product list getting all the relevant data
                Iterator productsListItr = productsList.iterator();
                while (productsListItr.hasNext()) {
                    GenericValue prod = (GenericValue)productsListItr.next();
                    String title = encoder.encode(prod.getString("internalName"));
                    String qnt = (String)context.get("quantity");
                    if (UtilValidate.isEmpty(qnt)) {
                        qnt = "1";
                    }
                    String startPrice = (String)context.get("startPrice");
                    if (UtilValidate.isEmpty(startPrice)) {
                        GenericValue startPriceValue = EntityUtil.getFirst(EntityUtil.filterByDate(prod.getRelatedByAnd("ProductPrice", UtilMisc.toMap("productPricePurposeId", "EBAY", "productPriceTypeId", "MINIMUM_PRICE"))));
                        if (UtilValidate.isNotEmpty(startPriceValue)) {
                            startPrice = startPriceValue.getString("price");
                        } else {
                            return ServiceUtil.returnFailure("Unable to find a starting price for auction of product with id [" + prod.getString("productId") + "]");
                        }
                    }
                    Element itemElem = UtilXml.addChildElement(itemRequestElem, "Item", itemDocument);
                    UtilXml.addChildElementValue(itemElem, "Country", (String)context.get("country"), itemDocument);
                    UtilXml.addChildElementValue(itemElem, "Location", (String)context.get("location"), itemDocument);
                    UtilXml.addChildElementValue(itemElem, "Currency", "USD", itemDocument);
                    UtilXml.addChildElementValue(itemElem, "ApplicationData", prod.getString("productId"), itemDocument);
                    UtilXml.addChildElementValue(itemElem, "SKU", prod.getString("productId"), itemDocument);
                    UtilXml.addChildElementValue(itemElem, "Title", title, itemDocument);
                    UtilXml.addChildElementValue(itemElem, "ListingDuration", (String)context.get("listingDuration"), itemDocument);
                    UtilXml.addChildElementValue(itemElem, "Quantity", qnt, itemDocument);

                    ProductContentWrapper pcw = new ProductContentWrapper(dctx.getDispatcher(), prod, locale, "text/html");
                    StringUtil.StringWrapper ebayDescription = pcw.get("EBAY_DESCRIPTION");
                    if (UtilValidate.isNotEmpty(ebayDescription)) {
                        UtilXml.addChildElementCDATAValue(itemElem, "Description", ebayDescription.toString(), itemDocument);
                    } else {
                        UtilXml.addChildElementValue(itemElem, "Description", encoder.encode(prod.getString("productName")), itemDocument);
                    }
                    String smallImage = prod.getString("smallImageUrl");
                    String mediumImage = prod.getString("mediumImageUrl");
                    String largeImage = prod.getString("largeImageUrl");
                    String ebayImage = null;
                    if (UtilValidate.isNotEmpty(largeImage)) {
                        ebayImage = largeImage;
                    } else if (UtilValidate.isNotEmpty(mediumImage)) {
                        ebayImage = mediumImage;
                    } else if (UtilValidate.isNotEmpty(smallImage)) {
                        ebayImage = smallImage;
                    }
                    if (UtilValidate.isNotEmpty(ebayImage)) {
                        Element pictureDetails = UtilXml.addChildElement(itemElem, "PictureDetails", itemDocument);
                        UtilXml.addChildElementValue(pictureDetails, "PictureURL", webSiteUrl + ebayImage, itemDocument);
                    }
                    setPaymentMethodAccepted(itemDocument, itemElem, context);
                    setMiscDetails(itemDocument, itemElem, context);

                    String primaryCategoryId = "";
                    String categoryCode = (String)context.get("ebayCategory");
                    if (categoryCode != null) {
                        String[] params = categoryCode.split("_");

                        if (params == null || params.length != 3) {
                            ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.parametersNotCorrectInGetEbayCategories", locale));
                        } else {
                            primaryCategoryId = params[1];
                        }
                    } else {
                        GenericValue productCategoryValue = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryAndMember", UtilMisc.toMap("productCategoryTypeId", "EBAY_CATEGORY", "productId", prod.getString("productId")))));
                        if (UtilValidate.isNotEmpty(productCategoryValue)) {
                            primaryCategoryId = productCategoryValue.getString("categoryName");
                        }
                    }

                    Element primaryCatElem = UtilXml.addChildElement(itemElem, "PrimaryCategory", itemDocument);
                    UtilXml.addChildElementValue(primaryCatElem, "CategoryID", primaryCategoryId, itemDocument);

                    Element startPriceElem = UtilXml.addChildElementValue(itemElem, "StartPrice", startPrice, itemDocument);
                    startPriceElem.setAttribute("currencyID", "USD");
                }

                dataItemsXml.append(UtilXml.writeXmlDocument(itemDocument));
            } catch (Exception e) {
                Debug.logError("Exception during building data items to eBay: " + e.getMessage(), module);
                return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.exceptionDuringBuildingDataItemsToEbay", locale));
            }
        } catch (Exception e) {
            Debug.logError("Exception during building data items to eBay: " + e.getMessage(), module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.exceptionDuringBuildingDataItemsToEbay", locale));
        }
        return ServiceUtil.returnSuccess();
    }

    private static Map buildCategoriesXml(Map context, StringBuffer dataItemsXml, String token, String siteID, String categoryParent, String levelLimit) {
        Locale locale = (Locale)context.get("locale");
        try {
            Document itemRequest = UtilXml.makeEmptyXmlDocument("GetCategoriesRequest");
            Element itemRequestElem = itemRequest.getDocumentElement();
            itemRequestElem.setAttribute("xmlns", "urn:ebay:apis:eBLBaseComponents");

            appendRequesterCredentials(itemRequestElem, itemRequest, token);

            UtilXml.addChildElementValue(itemRequestElem, "DetailLevel", "ReturnAll", itemRequest);
            UtilXml.addChildElementValue(itemRequestElem, "CategorySiteID", siteID, itemRequest);

            if (UtilValidate.isNotEmpty(categoryParent)) {
                UtilXml.addChildElementValue(itemRequestElem, "CategoryParent", categoryParent, itemRequest);
            }

            if (UtilValidate.isEmpty(levelLimit)) {
                levelLimit = "1";
            }

            UtilXml.addChildElementValue(itemRequestElem, "LevelLimit", levelLimit, itemRequest);
            UtilXml.addChildElementValue(itemRequestElem, "ViewAllNodes", "true", itemRequest);

            dataItemsXml.append(UtilXml.writeXmlDocument(itemRequest));
        } catch (Exception e) {
            Debug.logError("Exception during building data items to eBay", module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.exceptionDuringBuildingGetCategoriesRequest", locale));
        }
        return ServiceUtil.returnSuccess();
    }

    private static Map buildSetTaxTableRequestXml(DispatchContext dctx, Map context, StringBuffer setTaxTableRequestXml, String token) {
        Locale locale = (Locale)context.get("locale");
        try {
            Document taxRequestDocument = UtilXml.makeEmptyXmlDocument("SetTaxTableRequest");
            Element taxRequestElem = taxRequestDocument.getDocumentElement();
            taxRequestElem.setAttribute("xmlns", "urn:ebay:apis:eBLBaseComponents");

            appendRequesterCredentials(taxRequestElem, taxRequestDocument, token);

            Element taxTableElem = UtilXml.addChildElement(taxRequestElem, "TaxTable", taxRequestDocument);
            Element taxJurisdictionElem = UtilXml.addChildElement(taxTableElem, "TaxJurisdiction", taxRequestDocument);

            UtilXml.addChildElementValue(taxJurisdictionElem, "JurisdictionID", "NY", taxRequestDocument);
            UtilXml.addChildElementValue(taxJurisdictionElem, "SalesTaxPercent", "4.25", taxRequestDocument);
            UtilXml.addChildElementValue(taxJurisdictionElem, "ShippingIncludedInTax", "false", taxRequestDocument);

            setTaxTableRequestXml.append(UtilXml.writeXmlDocument(taxRequestDocument));
        } catch (Exception e) {
            Debug.logError("Exception during building request set tax table to eBay", module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.exceptionDuringBuildingRequestSetTaxTableToEbay", locale));
        }
        return ServiceUtil.returnSuccess();
    }

    private static Map buildAddTransactionConfirmationItemRequest(Map context, StringBuffer dataItemsXml, String token, String itemId) {
        Locale locale = (Locale)context.get("locale");
        try {
            Document transDoc = UtilXml.makeEmptyXmlDocument("AddTransactionConfirmationItemRequest");
            Element transElem = transDoc.getDocumentElement();
            transElem.setAttribute("xmlns", "urn:ebay:apis:eBLBaseComponents");

            appendRequesterCredentials(transElem, transDoc, token);

            UtilXml.addChildElementValue(transElem, "ItemID", itemId, transDoc);
            UtilXml.addChildElementValue(transElem, "ListingDuration", "Days_1", transDoc);
            Element negotiatePriceElem = UtilXml.addChildElementValue(transElem, "NegotiatedPrice", "50.00", transDoc);
            negotiatePriceElem.setAttribute("currencyID", "USD");
            UtilXml.addChildElementValue(transElem, "RecipientRelationType", "1", transDoc);
            UtilXml.addChildElementValue(transElem, "RecipientUserID", "buyer_anytime", transDoc);

            dataItemsXml.append(UtilXml.writeXmlDocument(transDoc));
            Debug.logInfo(dataItemsXml.toString(), module);
        } catch (Exception e) {
            Debug.logError("Exception during building AddTransactionConfirmationItemRequest eBay", module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.exceptionDuringBuildingAddTransactionConfirmationItemRequestToEbay", locale));
        }
        return ServiceUtil.returnSuccess();
    }

    private static void setPaymentMethodAccepted(Document itemDocument, Element itemElem, Map context) {
        String payPal = (String)context.get("paymentPayPal");
        String payPalEmail = (String)context.get("payPalEmail");
        String visaMC = (String)context.get("paymentVisaMC");
        String amEx = (String)context.get("paymentAmEx");
        String discover = (String)context.get("paymentDiscover");
        String ccAccepted = (String)context.get("paymentCCAccepted");
        String cashInPerson = (String)context.get("paymentCashInPerson");
        String cashOnPickup = (String)context.get("paymentCashOnPickup");
        String cod = (String)context.get("paymentCOD");
        String codPrePayDelivery = (String)context.get("paymentCODPrePayDelivery");
        String mocc = (String)context.get("paymentMOCC");
        String moneyXferAccepted = (String)context.get("paymentMoneyXferAccepted");
        String personalCheck = (String)context.get("paymentPersonalCheck");

        // PayPal
        if (UtilValidate.isNotEmpty(payPal) && "on".equals(payPal)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "PayPal", itemDocument);
            // PayPal email
            if (UtilValidate.isNotEmpty(payPalEmail)) {
                UtilXml.addChildElementValue(itemElem, "PayPalEmailAddress", payPalEmail, itemDocument);
            }
        }
        // Visa/Master Card
        if (UtilValidate.isNotEmpty(visaMC) && "on".equals(visaMC)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "VisaMC", itemDocument);
        }
        // American Express
        if (UtilValidate.isNotEmpty(amEx) && "on".equals(amEx)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "AmEx", itemDocument);
        }
        // Discover
        if (UtilValidate.isNotEmpty(discover) && "on".equals(discover)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "Discover", itemDocument);
        }
        // Credit Card Accepted
        if (UtilValidate.isNotEmpty(ccAccepted) && "on".equals(ccAccepted)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "CCAccepted", itemDocument);
        }
        // Cash In Person
        if (UtilValidate.isNotEmpty(cashInPerson) && "on".equals(cashInPerson)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "CashInPerson", itemDocument);
        }
        // Cash on Pickup
        if (UtilValidate.isNotEmpty(cashOnPickup) && "on".equals(cashOnPickup)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "CashOnPickup", itemDocument);
        }
        // Cash on Delivery
        if (UtilValidate.isNotEmpty(cod) && "on".equals(cod)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "COD", itemDocument);
        }
        // Cash On Delivery After Paid
        if (UtilValidate.isNotEmpty(codPrePayDelivery) && "on".equals(codPrePayDelivery)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "CODPrePayDelivery", itemDocument);
        }
        // Money order/cashiers check
        if (UtilValidate.isNotEmpty(mocc) && "on".equals(mocc)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "MOCC", itemDocument);
        }
        // Direct transfer of money
        if (UtilValidate.isNotEmpty(moneyXferAccepted) && "on".equals(moneyXferAccepted)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "MoneyXferAccepted", itemDocument);
        }
        // Personal Check
        if (UtilValidate.isNotEmpty(personalCheck) && "on".equals(personalCheck)) {
            UtilXml.addChildElementValue(itemElem, "PaymentMethods", "PersonalCheck", itemDocument);
        }
    }

    private static void setMiscDetails(Document itemDocument, Element itemElem, Map context) throws Exception {
        String customXml = UtilProperties.getPropertyValue(configFileName, "eBayExport.customXml");
        if (UtilValidate.isNotEmpty(customXml)) {
            Document customXmlDoc = UtilXml.readXmlDocument(customXml);
            if (UtilValidate.isNotEmpty(customXmlDoc)) {
                Element customXmlElement = customXmlDoc.getDocumentElement();
                List<? extends Element> eBayElements = UtilXml.childElementList(customXmlElement);
                for (Element eBayElement: eBayElements) {
                    Node importedElement = itemElem.getOwnerDocument().importNode(eBayElement, true);
                    itemElem.appendChild(importedElement);
                }
            }
        }
    }
    
    public static Map getEbayCategories(DispatchContext dctx, Map context) {
        Locale locale = (Locale) context.get("locale");
        String categoryCode = (String)context.get("categoryCode");
        Map result = null;

        try {
            String configString = "ebayExport.properties";

            // get the Developer Key
            String devID = UtilProperties.getPropertyValue(configString, "eBayExport.devID");

            // get the Application Key
            String appID = UtilProperties.getPropertyValue(configString, "eBayExport.appID");

            // get the Certifcate Key
            String certID = UtilProperties.getPropertyValue(configString, "eBayExport.certID");

            // get the Token
            String token = UtilProperties.getPropertyValue(configString, "eBayExport.token");

            // get the Compatibility Level
            String compatibilityLevel = UtilProperties.getPropertyValue(configString, "eBayExport.compatibilityLevel");

            // get the Site ID
            String siteID = UtilProperties.getPropertyValue(configString, "eBayExport.siteID");

            // get the xmlGatewayUri
            String xmlGatewayUri = UtilProperties.getPropertyValue(configString, "eBayExport.xmlGatewayUri");

            String categoryParent = "";
            String levelLimit = "";

            if (categoryCode != null) {
                String[] params = categoryCode.split("_");

                if (params == null || params.length != 3) {
                    ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.parametersNotCorrectInGetEbayCategories", locale));
                } else {
                    categoryParent = params[1];
                    levelLimit = params[2];
                    Integer level = new Integer(levelLimit);
                    levelLimit = (level.intValue() + 1) + "";
                }
            }

            StringBuffer dataItemsXml = new StringBuffer();

            if (!ServiceUtil.isFailure(buildCategoriesXml(context, dataItemsXml, token, siteID, categoryParent, levelLimit))) {
                Map resultCat = postItem(xmlGatewayUri, dataItemsXml, devID, appID, certID, "GetCategories", compatibilityLevel, siteID);
                String successMessage = (String)resultCat.get("successMessage");
                if (successMessage != null) {
                    result = readEbayCategoriesResponse(successMessage, locale);
                } else {
                    ServiceUtil.returnFailure(ServiceUtil.getErrorMessage(resultCat));
                }
            }
        } catch (Exception e) {
            Debug.logError("Exception in GetEbayCategories " + e, module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, "productsExportToEbay.exceptionInGetEbayCategories", locale));
        }
        return result;
    }

    private static Map readEbayCategoriesResponse(String msg, Locale locale) {
        Map results = null;
        List categories = FastList.newInstance();
        try {
            Document docResponse = UtilXml.readXmlDocument(msg, true);
            Element elemResponse = docResponse.getDocumentElement();
            String ack = UtilXml.childElementValue(elemResponse, "Ack", "Failure");
            if (ack != null && "Failure".equals(ack)) {
                String errorMessage = "";
                List errorList = UtilXml.childElementList(elemResponse, "Errors");
                Iterator errorElemIter = errorList.iterator();
                while (errorElemIter.hasNext()) {
                    Element errorElement = (Element) errorElemIter.next();
                    errorMessage = UtilXml.childElementValue(errorElement, "ShortMessage", "");
                }
                return ServiceUtil.returnFailure(errorMessage);
            } else {
                // retrieve Category Array
                List categoryArray = UtilXml.childElementList(elemResponse, "CategoryArray");
                Iterator categoryArrayElemIter = categoryArray.iterator();
                while (categoryArrayElemIter.hasNext()) {
                    Element categoryArrayElement = (Element)categoryArrayElemIter.next();

                    // retrieve Category
                    List category = UtilXml.childElementList(categoryArrayElement, "Category");
                    Iterator categoryElemIter = category.iterator();
                    while (categoryElemIter.hasNext()) {
                        Map categ = FastMap.newInstance();
                        Element categoryElement = (Element)categoryElemIter.next();

                        String categoryCode = ("true".equalsIgnoreCase((UtilXml.childElementValue(categoryElement, "LeafCategory", "").trim())) ? "Y" : "N") + "_" +
                                              UtilXml.childElementValue(categoryElement, "CategoryID", "").trim() + "_" +
                                              UtilXml.childElementValue(categoryElement, "CategoryLevel", "").trim();
                        categ.put("CategoryCode", categoryCode);
                        categ.put("CategoryName", UtilXml.childElementValue(categoryElement, "CategoryName"));
                        categories.add(categ);
                    }
                }
                categories = UtilMisc.sortMaps(categories, UtilMisc.toList("CategoryName"));
                results = UtilMisc.toMap("categories", categories);
            }
        } catch (Exception e) {
            return ServiceUtil.returnFailure();
        }
        return results;
    }
}
