/*
 * $Id$
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.commonapp.thirdparty.ups;

import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * UPS ShipmentServices
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.2
 */
public class UpsServices {
    
    public final static String module = UpsServices.class.getName();
    
    public static Map unitsUpsToOfbiz = new HashMap();
    public static Map unitsOfbizToUps = new HashMap();
    static {
        unitsUpsToOfbiz.put("LBS", "WT_lb");
        unitsUpsToOfbiz.put("KGS", "WT_kg");
        
        Iterator unitsUpsToOfbizIter = unitsUpsToOfbiz.entrySet().iterator();
        while (unitsUpsToOfbizIter.hasNext()) {
            Map.Entry entry = (Map.Entry) unitsUpsToOfbizIter.next();
            unitsOfbizToUps.put(entry.getValue(), entry.getKey());
        }
    }


    public static Map upsShipmentConfirm(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String shipmentId = (String) context.get("shipmentId");
        String shipmentRouteSegmentId = (String) context.get("shipmentRouteSegmentId");

        String shipmentConfirmResponseString = null;
        
        try {
            GenericValue shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));
            if (shipment == null) {
                return ServiceUtil.returnError("Shipment not found with ID " + shipmentId);
            }
            GenericValue shipmentRouteSegment = delegator.findByPrimaryKey("ShipmentRouteSegment", UtilMisc.toMap("shipmentId", shipmentId, "shipmentRouteSegmentId", shipmentRouteSegmentId));
            if (shipmentRouteSegment == null) {
                return ServiceUtil.returnError("ShipmentRouteSegment not found with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            
            if (!"UPS".equals(shipmentRouteSegment.getString("carrierPartyId"))) {
                return ServiceUtil.returnError("ERROR: The Carrier for ShipmentRouteSegment " + shipmentRouteSegmentId + " of Shipment " + shipmentId + ", is not UPS.");
            }
            
            // Get Origin Info
            GenericValue originPostalAddress = shipmentRouteSegment.getRelatedOne("OriginPostalAddress");
            if (originPostalAddress == null) {
                return ServiceUtil.returnError("OriginPostalAddress not found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            GenericValue originTelecomNumber = shipmentRouteSegment.getRelatedOne("OriginTelecomNumber");
            if (originTelecomNumber == null) {
                return ServiceUtil.returnError("OriginTelecomNumber not found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            String originPhoneNumber = originTelecomNumber.getString("areaCode") + originTelecomNumber.getString("contactNumber");
            // don't put on country code if not specified or is the US country code (UPS wants it this way)
            if (UtilValidate.isNotEmpty(originTelecomNumber.getString("countryCode")) && !"001".equals(originTelecomNumber.getString("countryCode"))) {
                originPhoneNumber = originTelecomNumber.getString("countryCode") + originPhoneNumber;
            }
            originPhoneNumber = StringUtil.replaceString(originPhoneNumber, "-", "");
            originPhoneNumber = StringUtil.replaceString(originPhoneNumber, " ", "");
            // lookup the two letter country code (in the geoCode field)
            GenericValue originCountryGeo = originPostalAddress.getRelatedOne("CountryGeo");
            if (originCountryGeo == null) {
                return ServiceUtil.returnError("OriginCountryGeo not found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }

            // Get Dest Info
            GenericValue destPostalAddress = shipmentRouteSegment.getRelatedOne("DestPostalAddress");
            if (destPostalAddress == null) {
                return ServiceUtil.returnError("DestPostalAddress not found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            GenericValue destTelecomNumber = shipmentRouteSegment.getRelatedOne("DestTelecomNumber");
            if (destTelecomNumber == null) {
                return ServiceUtil.returnError("DestTelecomNumber not found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            String destPhoneNumber = destTelecomNumber.getString("areaCode") + destTelecomNumber.getString("contactNumber");
            // don't put on country code if not specified or is the US country code (UPS wants it this way)
            if (UtilValidate.isNotEmpty(destTelecomNumber.getString("countryCode")) && !"001".equals(destTelecomNumber.getString("countryCode"))) {
                destPhoneNumber = destTelecomNumber.getString("countryCode") + destPhoneNumber;
            }
            destPhoneNumber = StringUtil.replaceString(destPhoneNumber, "-", "");
            destPhoneNumber = StringUtil.replaceString(destPhoneNumber, " ", "");
            // lookup the two letter country code (in the geoCode field)
            GenericValue destCountryGeo = destPostalAddress.getRelatedOne("CountryGeo");
            if (destCountryGeo == null) {
                return ServiceUtil.returnError("DestCountryGeo not found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            
            Map findCarrierShipmentMethodMap = UtilMisc.toMap("partyId", shipmentRouteSegment.get("carrierPartyId"), "roleTypeId", "CARRIER", "shipmentMethodTypeId", shipmentRouteSegment.get("shipmentMethodTypeId")); 
            GenericValue carrierShipmentMethod = delegator.findByPrimaryKey("CarrierShipmentMethod", findCarrierShipmentMethodMap);
            if (carrierShipmentMethod == null) {
                return ServiceUtil.returnError("CarrierShipmentMethod not found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId + "; partyId is " + shipmentRouteSegment.get("carrierPartyId") + " and shipmentMethodTypeId is " + shipmentRouteSegment.get("shipmentMethodTypeId"));
            }

            List shipmentPackageRouteSegs = shipmentRouteSegment.getRelated("ShipmentPackageRouteSeg", null, UtilMisc.toList("+shipmentPackageSeqId"));
            if (shipmentPackageRouteSegs == null || shipmentPackageRouteSegs.size() == 0) {
                return ServiceUtil.returnError("No ShipmentPackageRouteSegs found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            
            List itemIssuances = shipment.getRelated("ItemIssuance");
            Set orderIdSet = new TreeSet();
            Iterator itemIssuanceIter = itemIssuances.iterator();
            while (itemIssuanceIter.hasNext()) {
                GenericValue itemIssuance = (GenericValue) itemIssuanceIter.next();
                orderIdSet.add(itemIssuance.get("orderId"));
            }
            String ordersDescription = "";
            if (orderIdSet.size() > 1) {
                StringBuffer odBuf = new StringBuffer("Orders ");
                Iterator orderIdIter = orderIdSet.iterator();
                while (orderIdIter.hasNext()) {
                    String orderId = (String) orderIdIter.next();
                    odBuf.append(orderId);
                    if (orderIdIter.hasNext()) {
                        odBuf.append(", ");
                    }
                }
                ordersDescription = odBuf.toString();
            } else if (orderIdSet.size() > 0) {
                ordersDescription = "Order " + (String) orderIdSet.iterator().next();
            }
            
            // Okay, start putting the XML together...
            Document shipmentConfirmRequestDoc = UtilXml.makeEmptyXmlDocument("ShipmentConfirmRequest");
            Element shipmentConfirmRequestElement = shipmentConfirmRequestDoc.getDocumentElement();
            shipmentConfirmRequestElement.setAttribute("xml:lang", "en-US");

            // Top Level Element: Request
            Element requestElement = UtilXml.addChildElement(shipmentConfirmRequestElement, "Request", shipmentConfirmRequestDoc);

            Element transactionReferenceElement = UtilXml.addChildElement(requestElement, "TransactionReference", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(transactionReferenceElement, "CustomerContext", "Ship Confirm / nonvalidate", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(transactionReferenceElement, "XpciVersion", "1.0001", shipmentConfirmRequestDoc);

            UtilXml.addChildElementValue(requestElement, "RequestAction", "ShipConfirm", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(requestElement, "RequestOption", "nonvalidate", shipmentConfirmRequestDoc);

            // Top Level Element: LabelSpecification
            Element labelSpecificationElement = UtilXml.addChildElement(shipmentConfirmRequestElement, "LabelSpecification", shipmentConfirmRequestDoc);
            
            Element labelPrintMethodElement = UtilXml.addChildElement(labelSpecificationElement, "LabelPrintMethod", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(labelPrintMethodElement, "Code", "GIF", shipmentConfirmRequestDoc);

            UtilXml.addChildElementValue(labelSpecificationElement, "HTTPUserAgent", "Mozilla/5.0", shipmentConfirmRequestDoc);

            Element labelImageFormatElement = UtilXml.addChildElement(labelSpecificationElement, "LabelImageFormat", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(labelImageFormatElement, "Code", "GIF", shipmentConfirmRequestDoc);
            
            // Top Level Element: Shipment
            Element shipmentElement = UtilXml.addChildElement(shipmentConfirmRequestElement, "Shipment", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipmentElement, "Description", "Goods for Shipment " + shipment.get("shipmentId") + " from " + ordersDescription, shipmentConfirmRequestDoc);
            
            // Child of Shipment: Shipper
            Element shipperElement = UtilXml.addChildElement(shipmentElement, "Shipper", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperElement, "Name", originPostalAddress.getString("toName"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperElement, "AttentionName", originPostalAddress.getString("attnName"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperElement, "PhoneNumber", originPhoneNumber, shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperElement, "ShipperNumber", UtilProperties.getPropertyValue("shipment", "shipment.ups.shipper.number"), shipmentConfirmRequestDoc);

            Element shipperAddressElement = UtilXml.addChildElement(shipperElement, "Address", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "AddressLine1", originPostalAddress.getString("address1"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "AddressLine2", originPostalAddress.getString("address2"), shipmentConfirmRequestDoc);
            //UtilXml.addChildElementValue(shipperAddressElement, "AddressLine3", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "City", originPostalAddress.getString("city"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "StateProvinceCode", originPostalAddress.getString("stateProvinceGeoId"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "PostalCode", originPostalAddress.getString("postalCode"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "CountryCode", originCountryGeo.getString("geoCode"), shipmentConfirmRequestDoc);
            // How to determine this? Add to data model...? UtilXml.addChildElement(shipperAddressElement, "ResidentialAddress", shipmentConfirmRequestDoc);


            // Child of Shipment: ShipTo
            Element shipToElement = UtilXml.addChildElement(shipmentElement, "ShipTo", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToElement, "CompanyName", destPostalAddress.getString("toName"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToElement, "AttentionName", destPostalAddress.getString("attnName"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToElement, "PhoneNumber", destPhoneNumber, shipmentConfirmRequestDoc);
            Element shipToAddressElement = UtilXml.addChildElement(shipToElement, "Address", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToAddressElement, "AddressLine1", destPostalAddress.getString("address1"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToAddressElement, "AddressLine2", destPostalAddress.getString("address2"), shipmentConfirmRequestDoc);
            //UtilXml.addChildElementValue(shipToAddressElement, "AddressLine3", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToAddressElement, "City", destPostalAddress.getString("city"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToAddressElement, "StateProvinceCode", destPostalAddress.getString("stateProvinceGeoId"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToAddressElement, "PostalCode", destPostalAddress.getString("postalCode"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipToAddressElement, "CountryCode", destCountryGeo.getString("geoCode"), shipmentConfirmRequestDoc);

            // Child of Shipment: ShipFrom
            Element shipFromElement = UtilXml.addChildElement(shipmentElement, "ShipFrom", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromElement, "CompanyName", originPostalAddress.getString("toName"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromElement, "AttentionName", originPostalAddress.getString("attnName"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromElement, "PhoneNumber", originPhoneNumber, shipmentConfirmRequestDoc);
            Element shipFromAddressElement = UtilXml.addChildElement(shipFromElement, "Address", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromAddressElement, "AddressLine1", originPostalAddress.getString("address1"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromAddressElement, "AddressLine2", originPostalAddress.getString("address2"), shipmentConfirmRequestDoc);
            //UtilXml.addChildElementValue(shipFromAddressElement, "AddressLine3", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromAddressElement, "City", originPostalAddress.getString("city"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromAddressElement, "StateProvinceCode", originPostalAddress.getString("stateProvinceGeoId"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromAddressElement, "PostalCode", originPostalAddress.getString("postalCode"), shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipFromAddressElement, "CountryCode", originCountryGeo.getString("geoCode"), shipmentConfirmRequestDoc);

            // Child of Shipment: PaymentInformation
            Element paymentInformationElement = UtilXml.addChildElement(shipmentElement, "PaymentInformation", shipmentConfirmRequestDoc);
            Element prepaidElement = UtilXml.addChildElement(paymentInformationElement, "Prepaid", shipmentConfirmRequestDoc);
            Element billShipperElement = UtilXml.addChildElement(prepaidElement, "BillShipper", shipmentConfirmRequestDoc);
            // fill in BillShipper AccountNumber element from properties file
            UtilXml.addChildElementValue(billShipperElement, "AccountNumber", UtilProperties.getPropertyValue("shipment", "shipment.ups.bill.shipper.account.number"), shipmentConfirmRequestDoc);

            // Child of Shipment: Service
            Element serviceElement = UtilXml.addChildElement(shipmentElement, "Service", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(serviceElement, "Code", carrierShipmentMethod.getString("carrierServiceCode"), shipmentConfirmRequestDoc);

            // Child of Shipment: Package
            Iterator shipmentPackageRouteSegIter = shipmentPackageRouteSegs.iterator();
            while (shipmentPackageRouteSegIter.hasNext()) {
                GenericValue shipmentPackageRouteSeg = (GenericValue) shipmentPackageRouteSegIter.next();
                GenericValue shipmentPackage = shipmentPackageRouteSeg.getRelatedOne("ShipmentPackage");
                GenericValue shipmentBoxType = shipmentPackage.getRelatedOne("ShipmentBoxType");
                List carrierShipmentBoxTypes = shipmentPackage.getRelated("CarrierShipmentBoxType", UtilMisc.toMap("partyId", "UPS"), null);
                GenericValue carrierShipmentBoxType = null;
                if (carrierShipmentBoxTypes.size() > 0) {
                    carrierShipmentBoxType = (GenericValue) carrierShipmentBoxTypes.get(0); 
                }
                 
                Element packageElement = UtilXml.addChildElement(shipmentElement, "Package", shipmentConfirmRequestDoc);
                Element packagingTypeElement = UtilXml.addChildElement(packageElement, "PackagingType", shipmentConfirmRequestDoc);
                if (carrierShipmentBoxType != null && carrierShipmentBoxType.get("packagingTypeCode") != null) {
                    UtilXml.addChildElementValue(packagingTypeElement, "Code", carrierShipmentBoxType.getString("packagingTypeCode"), shipmentConfirmRequestDoc);
                } else {
                    // default to "02", plain old Package
                    UtilXml.addChildElementValue(packagingTypeElement, "Code", "02", shipmentConfirmRequestDoc);
                }
                if (shipmentBoxType != null) {
                    Element dimensionsElement = UtilXml.addChildElement(packageElement, "Dimensions", shipmentConfirmRequestDoc);
                    Element unitOfMeasurementElement = UtilXml.addChildElement(dimensionsElement, "UnitOfMeasurement", shipmentConfirmRequestDoc);
                    GenericValue dimensionUom = shipmentBoxType.getRelatedOne("DimensionUom");
                    if (dimensionUom != null) {
                        UtilXml.addChildElementValue(unitOfMeasurementElement, "Code", dimensionUom.getString("abbreviation"), shipmentConfirmRequestDoc);
                    } else {
                        // I guess we'll default to inches...
                        UtilXml.addChildElementValue(unitOfMeasurementElement, "Code", "IN", shipmentConfirmRequestDoc);
                    }
                    UtilXml.addChildElementValue(dimensionsElement, "Length", shipmentBoxType.get("boxLength").toString(), shipmentConfirmRequestDoc);
                    UtilXml.addChildElementValue(dimensionsElement, "Width", shipmentBoxType.get("boxWidth").toString(), shipmentConfirmRequestDoc);
                    UtilXml.addChildElementValue(dimensionsElement, "Height", shipmentBoxType.get("boxHeight").toString(), shipmentConfirmRequestDoc);
                }
                
                Element packageWeightElement = UtilXml.addChildElement(packageElement, "PackageWeight", shipmentConfirmRequestDoc);
                Element packageWeightUnitOfMeasurementElement = UtilXml.addChildElement(packageElement, "UnitOfMeasurement", shipmentConfirmRequestDoc);
                String weightUomUps = (String) unitsOfbizToUps.get(shipmentPackage.get("weightUomId"));
                if (weightUomUps != null) {
                    UtilXml.addChildElementValue(packageWeightUnitOfMeasurementElement, "Code", weightUomUps, shipmentConfirmRequestDoc);
                } else {
                    // might as well default to LBS
                    UtilXml.addChildElementValue(packageWeightUnitOfMeasurementElement, "Code", "LBS", shipmentConfirmRequestDoc);
                }
                UtilXml.addChildElementValue(packageWeightElement, "Weight", shipmentPackage.getString("weight"), shipmentConfirmRequestDoc);
                
                Element referenceNumberElement = UtilXml.addChildElement(packageElement, "ReferenceNumber", shipmentConfirmRequestDoc);
                UtilXml.addChildElementValue(referenceNumberElement, "Code", "MK", shipmentConfirmRequestDoc);
                UtilXml.addChildElementValue(referenceNumberElement, "Value", shipmentPackage.getString("shipmentPackageSeqId"), shipmentConfirmRequestDoc);

                if (carrierShipmentBoxType != null && carrierShipmentBoxType.get("oversizeCode") != null) {
                    UtilXml.addChildElementValue(packageElement, "OversizePackage", carrierShipmentBoxType.getString("oversizeCode"), shipmentConfirmRequestDoc);
                }
            }

            String shipmentConfirmRequestString = null;
            try {
                shipmentConfirmRequestString = UtilXml.writeXmlDocument(shipmentConfirmRequestDoc);
            } catch (IOException e) {
                String ioeErrMsg = "Error writing the ShipmentConfirmRequest XML Document to a String: " + e.toString();
                Debug.logError(e, ioeErrMsg);
                return ServiceUtil.returnError(ioeErrMsg);
            }
            
            // create AccessRequest XML doc
            Document accessRequestDocument = createAccessRequestDocument();
            String accessRequestString = null;
            try {
                accessRequestString = UtilXml.writeXmlDocument(accessRequestDocument);
            } catch (IOException e) {
                String ioeErrMsg = "Error writing the AccessRequest XML Document to a String: " + e.toString();
                Debug.logError(e, ioeErrMsg);
                return ServiceUtil.returnError(ioeErrMsg);
            }
            
            // connect to UPS server, send AccessRequest to auth
            // send ShipmentConfirmRequest String
            // get ShipmentConfirmResponse String back
            StringBuffer xmlString = new StringBuffer();
            // TODO: note that we may have to append <?xml version="1.0"?> before each string
            xmlString.append(accessRequestString);
            xmlString.append(shipmentConfirmRequestString);
            try {
                shipmentConfirmResponseString = sendUpsRequest("ShipConfirm", xmlString.toString());
            } catch (UpsConnectException e) {
                String uceErrMsg = "Error sending UPS request for UPS Service ShipConfirm: " + e.toString();
                Debug.logError(e, uceErrMsg);
                return ServiceUtil.returnError(uceErrMsg);
            }

            Document shipmentConfirmResponseDocument = null;
            try {
                shipmentConfirmResponseDocument = UtilXml.readXmlDocument(shipmentConfirmResponseString, false);
            } catch (SAXException e2) {
                String excErrMsg = "Error parsing the ShipmentConfirmResponse: " + e2.toString();
                Debug.logError(e2, excErrMsg);
                return ServiceUtil.returnError(excErrMsg);
            } catch (ParserConfigurationException e2) {
                String excErrMsg = "Error parsing the ShipmentConfirmResponse: " + e2.toString();
                Debug.logError(e2, excErrMsg);
                return ServiceUtil.returnError(excErrMsg);
            } catch (IOException e2) {
                String excErrMsg = "Error parsing the ShipmentConfirmResponse: " + e2.toString();
                Debug.logError(e2, excErrMsg);
                return ServiceUtil.returnError(excErrMsg);
            }

            // process ShipmentConfirmResponse, update data as needed
            Element shipmentConfirmResponseElement = shipmentConfirmResponseDocument.getDocumentElement();
            
            // handle Response element info
            Element responseElement = UtilXml.firstChildElement(shipmentConfirmResponseElement, "Response");
            Element responseTransactionReferenceElement = UtilXml.firstChildElement(responseElement, "TransactionReference");
            String responseTransactionReferenceCustomerContext = UtilXml.childElementValue(responseTransactionReferenceElement, "CustomerContext");
            String responseTransactionReferenceXpciVersion = UtilXml.childElementValue(responseTransactionReferenceElement, "XpciVersion");

            String responseStatusCode = UtilXml.childElementValue(responseElement, "ResponseStatusCode");
            String responseStatusDescription = UtilXml.childElementValue(responseElement, "ResponseStatusDescription");
            List errorList = new LinkedList();
            UpsServices.handleErrors(responseElement, errorList);

            if ("1".equals(responseStatusCode)) {
                // handle ShipmentCharges element info
                Element shipmentChargesElement = UtilXml.firstChildElement(shipmentConfirmResponseElement, "ShipmentCharges");

                Element transportationChargesElement = UtilXml.firstChildElement(shipmentChargesElement, "TransportationCharges");
                String transportationCurrencyCode = UtilXml.childElementValue(transportationChargesElement, "CurrencyCode");
                String transportationMonetaryValue = UtilXml.childElementValue(transportationChargesElement, "MonetaryValue");
            
                Element serviceOptionsChargesElement = UtilXml.firstChildElement(shipmentChargesElement, "ServiceOptionsCharges");
                String serviceOptionsCurrencyCode = UtilXml.childElementValue(serviceOptionsChargesElement, "CurrencyCode");
                String serviceOptionsMonetaryValue = UtilXml.childElementValue(serviceOptionsChargesElement, "MonetaryValue");

                Element totalChargesElement = UtilXml.firstChildElement(shipmentChargesElement, "TotalCharges");
                String totalCurrencyCode = UtilXml.childElementValue(totalChargesElement, "CurrencyCode");
                String totalMonetaryValue = UtilXml.childElementValue(totalChargesElement, "MonetaryValue");
            
                if (UtilValidate.isNotEmpty(totalCurrencyCode)) {
                    if (UtilValidate.isEmpty(shipmentRouteSegment.getString("currencyUomId"))) {
                        shipmentRouteSegment.set("currencyUomId", totalCurrencyCode);
                    } else if(!totalCurrencyCode.equals(shipmentRouteSegment.getString("currencyUomId"))) {
                        errorList.add("The Currency Unit of Measure returned [" + totalCurrencyCode + "] is not the same as the original [" + shipmentRouteSegment.getString("currencyUomId") + "], setting to the new one.");
                        shipmentRouteSegment.set("currencyUomId", totalCurrencyCode);
                    }
                }
            
                try {
                    shipmentRouteSegment.set("actualTransportCost", Double.valueOf(transportationMonetaryValue));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the transportationMonetaryValue [" + transportationMonetaryValue + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
                try {
                    shipmentRouteSegment.set("actualServiceCost", Double.valueOf(serviceOptionsMonetaryValue));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the serviceOptionsMonetaryValue [" + serviceOptionsMonetaryValue + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
                try {
                    shipmentRouteSegment.set("actualCost", Double.valueOf(totalMonetaryValue));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the totalMonetaryValue [" + totalMonetaryValue + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
            
                // handle BillingWeight element info
                Element billingWeightElement = UtilXml.firstChildElement(shipmentConfirmResponseElement, "BillingWeight");
                Element billingWeightUnitOfMeasurementElement = UtilXml.firstChildElement(billingWeightElement, "UnitOfMeasurement");
                String billingWeightUnitOfMeasurement = UtilXml.childElementValue(billingWeightUnitOfMeasurementElement, "Code");
                String billingWeight = UtilXml.childElementValue(billingWeightElement, "Weight");
                try {
                    shipmentRouteSegment.set("billingWeight", Double.valueOf(billingWeight));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the billingWeight [" + billingWeight + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
                shipmentRouteSegment.set("billingWeightUomId", unitsUpsToOfbiz.get(billingWeightUnitOfMeasurement));

                // store the ShipmentIdentificationNumber and ShipmentDigest
                String shipmentIdentificationNumber = UtilXml.childElementValue(shipmentConfirmResponseElement, "ShipmentIdentificationNumber");
                String shipmentDigest = UtilXml.childElementValue(shipmentConfirmResponseElement, "ShipmentDigest");
                shipmentRouteSegment.set("trackingIdNumber", shipmentIdentificationNumber);
                shipmentRouteSegment.set("trackingDigest", shipmentDigest);
            
                // write/store all modified value objects
                shipmentRouteSegment.store();
                
                // -=-=-=- Okay, now done with that, just return any extra info...
            
                StringBuffer successString = new StringBuffer("The UPS ShipmentConfirm succeeded");
                if (errorList.size() > 0) {
                    // this shouldn't happen much, but handle it anyway
                    successString.append(", but the following occurred: ");
                    Iterator errorListIter = errorList.iterator();
                    while (errorListIter.hasNext()) {
                        String errorMsg = (String) errorListIter.next();
                        successString.append(errorMsg);
                        if (errorListIter.hasNext()) {
                            successString.append(", ");
                        }
                    }
                }
                return ServiceUtil.returnSuccess(successString.toString());
            } else {
                errorList.add(0, "The UPS ShipmentConfirm failed");
                return ServiceUtil.returnError(errorList);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e);
            if (shipmentConfirmResponseString != null) {
                Debug.logError("Got XML ShipmentConfirmRespose: " + shipmentConfirmResponseString);
                return ServiceUtil.returnError(UtilMisc.toList(
                            "Error reading or writing Shipment data for UPS Shipment Confirm: " + e.toString(),
                            "A ShipmentConfirmRespose was received: " + shipmentConfirmResponseString));
            } else {
                return ServiceUtil.returnError("Error reading or writing Shipment data for UPS Shipment Confirm: " + e.toString());
            }
        }
    }
    
    public static Map upsShipmentAccept(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String shipmentId = (String) context.get("shipmentId");
        String shipmentRouteSegmentId = (String) context.get("shipmentRouteSegmentId");

        String shipmentAccessResponseString = null;

        try {
            GenericValue shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));
            GenericValue shipmentRouteSegment = delegator.findByPrimaryKey("ShipmentRouteSegment", UtilMisc.toMap("shipmentId", shipmentId, "shipmentRouteSegmentId", shipmentRouteSegmentId));

            if (!"UPS".equals(shipmentRouteSegment.getString("carrierPartyId"))) {
                return ServiceUtil.returnError("ERROR: The Carrier for ShipmentRouteSegment " + shipmentRouteSegmentId + " of Shipment " + shipmentId + ", is not UPS.");
            }
            
            if (UtilValidate.isEmpty(shipmentRouteSegment.getString("trackingDigest"))) {
                return ServiceUtil.returnError("ERROR: The trackingDigest was not set for this Route Segment, meaning that a UPS shipment confirm has not been done.");
            }

            List shipmentPackageRouteSegs = shipmentRouteSegment.getRelated("ShipmentPackageRouteSeg", null, UtilMisc.toList("+shipmentPackageSeqId"));
            if (shipmentPackageRouteSegs == null || shipmentPackageRouteSegs.size() == 0) {
                return ServiceUtil.returnError("No ShipmentPackageRouteSegs found for ShipmentRouteSegment with shipmentId " + shipmentId + " and shipmentRouteSegmentId " + shipmentRouteSegmentId);
            }
            
            Document shipmentAcceptRequestDoc = UtilXml.makeEmptyXmlDocument("ShipmentAcceptRequest");
            Element shipmentAcceptRequestElement = shipmentAcceptRequestDoc.getDocumentElement();
            shipmentAcceptRequestElement.setAttribute("xml:lang", "en-US");

            // Top Level Element: Request
            Element requestElement = UtilXml.addChildElement(shipmentAcceptRequestElement, "Request", shipmentAcceptRequestDoc);

            Element transactionReferenceElement = UtilXml.addChildElement(requestElement, "TransactionReference", shipmentAcceptRequestDoc);
            UtilXml.addChildElementValue(transactionReferenceElement, "CustomerContext", "ShipAccept / 01", shipmentAcceptRequestDoc);
            UtilXml.addChildElementValue(transactionReferenceElement, "XpciVersion", "1.0001", shipmentAcceptRequestDoc);

            UtilXml.addChildElementValue(requestElement, "RequestAction", "ShipAccept", shipmentAcceptRequestDoc);
            UtilXml.addChildElementValue(requestElement, "RequestOption", "01", shipmentAcceptRequestDoc);

            UtilXml.addChildElementValue(shipmentAcceptRequestElement, "ShipmentDigest", shipmentRouteSegment.getString("trackingDigest"), shipmentAcceptRequestDoc);
            
            
            String shipmentAcceptRequestString = null;
            try {
                shipmentAcceptRequestString = UtilXml.writeXmlDocument(shipmentAcceptRequestDoc);
            } catch (IOException e) {
                String ioeErrMsg = "Error writing the ShipmentAcceptRequest XML Document to a String: " + e.toString();
                Debug.logError(e, ioeErrMsg);
                return ServiceUtil.returnError(ioeErrMsg);
            }
            
            // create AccessRequest XML doc
            Document accessRequestDocument = createAccessRequestDocument();
            String accessRequestString = null;
            try {
                accessRequestString = UtilXml.writeXmlDocument(accessRequestDocument);
            } catch (IOException e) {
                String ioeErrMsg = "Error writing the AccessRequest XML Document to a String: " + e.toString();
                Debug.logError(e, ioeErrMsg);
                return ServiceUtil.returnError(ioeErrMsg);
            }
            
            // connect to UPS server, send AccessRequest to auth
            // send ShipmentConfirmRequest String
            // get ShipmentConfirmResponse String back
            StringBuffer xmlString = new StringBuffer();
            // TODO: note that we may have to append <?xml version="1.0"?> before each string
            xmlString.append(accessRequestString);
            xmlString.append(shipmentAcceptRequestString);
            try {
                shipmentAccessResponseString = sendUpsRequest("ShipAccept", xmlString.toString());
            } catch (UpsConnectException e) {
                String uceErrMsg = "Error sending UPS request for UPS Service ShipAccept: " + e.toString();
                Debug.logError(e, uceErrMsg);
                return ServiceUtil.returnError(uceErrMsg);
            }

            Document shipmentAccessResponseDocument = null;
            try {
                shipmentAccessResponseDocument = UtilXml.readXmlDocument(shipmentAccessResponseString, false);
            } catch (SAXException e2) {
                String excErrMsg = "Error parsing the ShipmentAccessResponse: " + e2.toString();
                Debug.logError(e2, excErrMsg);
                return ServiceUtil.returnError(excErrMsg);
            } catch (ParserConfigurationException e2) {
                String excErrMsg = "Error parsing the ShipmentAccessResponse: " + e2.toString();
                Debug.logError(e2, excErrMsg);
                return ServiceUtil.returnError(excErrMsg);
            } catch (IOException e2) {
                String excErrMsg = "Error parsing the ShipmentAccessResponse: " + e2.toString();
                Debug.logError(e2, excErrMsg);
                return ServiceUtil.returnError(excErrMsg);
            }

            // process ShipmentAccessResponse, update data as needed
            Element shipmentAccessResponseElement = shipmentAccessResponseDocument.getDocumentElement();
            
            // handle Response element info
            Element responseElement = UtilXml.firstChildElement(shipmentAccessResponseElement, "Response");
            Element responseTransactionReferenceElement = UtilXml.firstChildElement(responseElement, "TransactionReference");
            String responseTransactionReferenceCustomerContext = UtilXml.childElementValue(responseTransactionReferenceElement, "CustomerContext");
            String responseTransactionReferenceXpciVersion = UtilXml.childElementValue(responseTransactionReferenceElement, "XpciVersion");

            String responseStatusCode = UtilXml.childElementValue(responseElement, "ResponseStatusCode");
            String responseStatusDescription = UtilXml.childElementValue(responseElement, "ResponseStatusDescription");
            List errorList = new LinkedList();
            UpsServices.handleErrors(responseElement, errorList);

            if ("1".equals(responseStatusCode)) {
                Element shipmentResultsElement = UtilXml.firstChildElement(shipmentAccessResponseElement, "ShipmentResults");

                // This information is returned in both the ShipmentConfirmResponse and 
                //the ShipmentAcceptResponse. So, we'll go ahead and store it here again
                //and warn of changes or something...
                

                // handle ShipmentCharges element info
                Element shipmentChargesElement = UtilXml.firstChildElement(shipmentResultsElement, "ShipmentCharges");

                Element transportationChargesElement = UtilXml.firstChildElement(shipmentChargesElement, "TransportationCharges");
                String transportationCurrencyCode = UtilXml.childElementValue(transportationChargesElement, "CurrencyCode");
                String transportationMonetaryValue = UtilXml.childElementValue(transportationChargesElement, "MonetaryValue");
            
                Element serviceOptionsChargesElement = UtilXml.firstChildElement(shipmentChargesElement, "ServiceOptionsCharges");
                String serviceOptionsCurrencyCode = UtilXml.childElementValue(serviceOptionsChargesElement, "CurrencyCode");
                String serviceOptionsMonetaryValue = UtilXml.childElementValue(serviceOptionsChargesElement, "MonetaryValue");

                Element totalChargesElement = UtilXml.firstChildElement(shipmentChargesElement, "TotalCharges");
                String totalCurrencyCode = UtilXml.childElementValue(totalChargesElement, "CurrencyCode");
                String totalMonetaryValue = UtilXml.childElementValue(totalChargesElement, "MonetaryValue");
            
                if (UtilValidate.isNotEmpty(totalCurrencyCode)) {
                    if (UtilValidate.isEmpty(shipmentRouteSegment.getString("currencyUomId"))) {
                        shipmentRouteSegment.set("currencyUomId", totalCurrencyCode);
                    } else if(!totalCurrencyCode.equals(shipmentRouteSegment.getString("currencyUomId"))) {
                        errorList.add("The Currency Unit of Measure returned [" + totalCurrencyCode + "] is not the same as the original [" + shipmentRouteSegment.getString("currencyUomId") + "], setting to the new one.");
                        shipmentRouteSegment.set("currencyUomId", totalCurrencyCode);
                    }
                }
            
                try {
                    shipmentRouteSegment.set("actualTransportCost", Double.valueOf(transportationMonetaryValue));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the transportationMonetaryValue [" + transportationMonetaryValue + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
                try {
                    shipmentRouteSegment.set("actualServiceCost", Double.valueOf(serviceOptionsMonetaryValue));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the serviceOptionsMonetaryValue [" + serviceOptionsMonetaryValue + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
                try {
                    shipmentRouteSegment.set("actualCost", Double.valueOf(totalMonetaryValue));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the totalMonetaryValue [" + totalMonetaryValue + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
            
                // handle BillingWeight element info
                Element billingWeightElement = UtilXml.firstChildElement(shipmentResultsElement, "BillingWeight");
                Element billingWeightUnitOfMeasurementElement = UtilXml.firstChildElement(billingWeightElement, "UnitOfMeasurement");
                String billingWeightUnitOfMeasurement = UtilXml.childElementValue(billingWeightUnitOfMeasurementElement, "Code");
                String billingWeight = UtilXml.childElementValue(billingWeightElement, "Weight");
                try {
                    shipmentRouteSegment.set("billingWeight", Double.valueOf(billingWeight));
                } catch (NumberFormatException e) {
                    String excErrMsg = "Error parsing the billingWeight [" + billingWeight + "]: " + e.toString();
                    Debug.logError(e, excErrMsg);
                    errorList.add(excErrMsg);
                }
                shipmentRouteSegment.set("billingWeightUomId", unitsUpsToOfbiz.get(billingWeightUnitOfMeasurement));

                // store the ShipmentIdentificationNumber and ShipmentDigest
                String shipmentIdentificationNumber = UtilXml.childElementValue(shipmentResultsElement, "ShipmentIdentificationNumber");
                // should compare to trackingIdNumber, should always be the same right?
                shipmentRouteSegment.set("trackingIdNumber", shipmentIdentificationNumber);

                // write/store modified value object
                shipmentRouteSegment.store();
                
                // now process the PackageResults elements
                List packageResultsElements = UtilXml.childElementList(shipmentResultsElement, "PackageResults");
                Iterator packageResultsElementIter = packageResultsElements.iterator();
                Iterator shipmentPackageRouteSegIter = shipmentPackageRouteSegs.iterator();
                while (packageResultsElementIter.hasNext()) {
                    Element packageResultsElement = (Element) packageResultsElementIter.next();
                    
                    String trackingNumber = UtilXml.childElementValue(packageResultsElement, "TrackingNumber");

                    Element packageServiceOptionsChargesElement = UtilXml.firstChildElement(packageResultsElement, "ServiceOptionsCharges");
                    String packageServiceOptionsCurrencyCode = UtilXml.childElementValue(packageServiceOptionsChargesElement, "CurrencyCode");
                    String packageServiceOptionsMonetaryValue = UtilXml.childElementValue(packageServiceOptionsChargesElement, "MonetaryValue");

                    Element packageLabelImageElement = UtilXml.firstChildElement(packageResultsElement, "LabelImage");
                    Element packageLabelImageFormatElement = UtilXml.firstChildElement(packageResultsElement, "LabelImageFormat");
                    // will be EPL or GIF, should always be GIF since that is what we requested
                    String packageLabelImageFormatCode = UtilXml.childElementValue(packageLabelImageFormatElement, "Code");
                    String packageLabelGraphicImageString = UtilXml.childElementValue(packageLabelImageElement, "GraphicImage");

                    if (!shipmentPackageRouteSegIter.hasNext()) {
                        errorList.add("Error: More PackageResults were returned than there are Packages on this Shipment; the TrackingNumber is [" + trackingNumber + "], the ServiceOptionsCharges were " + packageServiceOptionsMonetaryValue + packageServiceOptionsCurrencyCode);
                        // NOTE: if this happens much we should just create a new package to store all of the info...
                        continue;
                    }
                    
                    //NOTE: I guess they come back in the same order we sent them, so we'll get the packages in order and off we go...
                    GenericValue shipmentPackageRouteSeg = (GenericValue) shipmentPackageRouteSegIter.next();
                    shipmentPackageRouteSeg.set("trackingCode", trackingNumber);
                    shipmentPackageRouteSeg.set("boxNumber", "");
                    shipmentPackageRouteSeg.set("currencyUomId", packageServiceOptionsCurrencyCode);
                    try {
                        shipmentRouteSegment.set("packageServiceCost", Double.valueOf(packageServiceOptionsMonetaryValue));
                    } catch (NumberFormatException e) {
                        String excErrMsg = "Error parsing the packageServiceOptionsMonetaryValue [" + packageServiceOptionsMonetaryValue + "] for Package [" + shipmentPackageRouteSeg.getString("shipmentPackageSeqId") + "]: " + e.toString();
                        Debug.logError(e, excErrMsg);
                        errorList.add(excErrMsg);
                    }
                    
                    byte[] labelImageBytes = Base64.base64Decode(packageLabelGraphicImageString.getBytes());
                    shipmentPackageRouteSeg.setBytes("labelImage", labelImageBytes);
                    
                    shipmentPackageRouteSeg.store();
                }

                if (shipmentPackageRouteSegIter.hasNext()) {
                    errorList.add("Error: There are more Packages on this Shipment than there were PackageResults returned from UPS");
                    while (shipmentPackageRouteSegIter.hasNext()) {
                        GenericValue shipmentPackageRouteSeg = (GenericValue) shipmentPackageRouteSegIter.next();
                        errorList.add("Error: No PackageResults were returned for the Package [" + shipmentPackageRouteSeg.getString("shipmentPackageSeqId") + "]");
                    }
                }
                
                // -=-=-=- Okay, now done with that, just return any extra info...
                StringBuffer successString = new StringBuffer("The UPS ShipmentAccept succeeded");
                if (errorList.size() > 0) {
                    // this shouldn't happen much, but handle it anyway
                    successString.append(", but the following occurred: ");
                    Iterator errorListIter = errorList.iterator();
                    while (errorListIter.hasNext()) {
                        String errorMsg = (String) errorListIter.next();
                        successString.append(errorMsg);
                        if (errorListIter.hasNext()) {
                            successString.append(", ");
                        }
                    }
                }
                return ServiceUtil.returnSuccess(successString.toString());
            } else {
                errorList.add(0, "The UPS ShipmentConfirm failed");
                return ServiceUtil.returnError(errorList);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e);
            return ServiceUtil.returnError("Error reading or writing Shipment data for UPS Shipment Accept: " + e.toString());
        }
    }
    
    public static Map upsVoidShipment(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String shipmentId = (String) context.get("shipmentId");
        String shipmentRouteSegmentId = (String) context.get("shipmentRouteSegmentId");

        try {
            GenericValue shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));
            GenericValue shipmentRouteSegment = delegator.findByPrimaryKey("ShipmentRouteSegment", UtilMisc.toMap("shipmentId", shipmentId, "shipmentRouteSegmentId", shipmentRouteSegmentId));

            if (!"UPS".equals(shipmentRouteSegment.getString("carrierPartyId"))) {
                return ServiceUtil.returnError("ERROR: The Carrier for ShipmentRouteSegment " + shipmentRouteSegmentId + " of Shipment " + shipmentId + ", is not UPS.");
            }
            
        } catch (GenericEntityException e) {
            Debug.logError(e);
            return ServiceUtil.returnError("Error reading or writing Shipment data for UPS Void Shipment: " + e.toString());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    public static Map upsTrackShipment(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String shipmentId = (String) context.get("shipmentId");
        String shipmentRouteSegmentId = (String) context.get("shipmentRouteSegmentId");

        try {
            GenericValue shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));
            GenericValue shipmentRouteSegment = delegator.findByPrimaryKey("ShipmentRouteSegment", UtilMisc.toMap("shipmentId", shipmentId, "shipmentRouteSegmentId", shipmentRouteSegmentId));

            if (!"UPS".equals(shipmentRouteSegment.getString("carrierPartyId"))) {
                return ServiceUtil.returnError("ERROR: The Carrier for ShipmentRouteSegment " + shipmentRouteSegmentId + " of Shipment " + shipmentId + ", is not UPS.");
            }
            
        } catch (GenericEntityException e) {
            Debug.logError(e);
            return ServiceUtil.returnError("Error reading or writing Shipment data for UPS Track Shipment: " + e.toString());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    public static Document createAccessRequestDocument() {
        Document accessRequestDocument = UtilXml.makeEmptyXmlDocument("AccessRequest");
        Element accessRequestElement = accessRequestDocument.getDocumentElement();
        UtilXml.addChildElementValue(accessRequestElement, "AccessLicenseNumber", UtilProperties.getPropertyValue("shipment", "shipment.ups.access.license.number"), accessRequestDocument);
        UtilXml.addChildElementValue(accessRequestElement, "UserId", UtilProperties.getPropertyValue("shipment", "shipment.ups.access.user.id"), accessRequestDocument);
        UtilXml.addChildElementValue(accessRequestElement, "Password", UtilProperties.getPropertyValue("shipment", "shipment.ups.access.password"), accessRequestDocument);
        return accessRequestDocument;
    }
    
    public static void handleErrors(Element responseElement, List errorList) {
        List errorElements = UtilXml.childElementList(responseElement, "Error");
        Iterator errorElementIter = errorElements.iterator();
        while (errorElementIter.hasNext()) {
            StringBuffer errorMessageBuf = new StringBuffer();
            Element errorElement = (Element) errorElementIter.next();
            
            String errorSeverity = UtilXml.childElementValue(errorElement, "ErrorSeverity");
            String errorCode = UtilXml.childElementValue(errorElement, "ErrorCode");
            String errorDescription = UtilXml.childElementValue(errorElement, "ErrorDescription");
            String minimumRetrySeconds = UtilXml.childElementValue(errorElement, "MinimumRetrySeconds");
            
            errorMessageBuf.append("An error occurred [code:");
            errorMessageBuf.append(errorCode);
            errorMessageBuf.append("] with severity ");
            errorMessageBuf.append(errorSeverity);
            errorMessageBuf.append(": ");
            errorMessageBuf.append(errorDescription);
            if (UtilValidate.isNotEmpty(minimumRetrySeconds)) {
                errorMessageBuf.append("; you should wait ");
                errorMessageBuf.append(minimumRetrySeconds);
                errorMessageBuf.append(" seconds before retrying. ");
            } else {
                errorMessageBuf.append(". ");
            }
            
            List errorLocationElements = UtilXml.childElementList(errorElement, "ErrorLocation");
            Iterator errorLocationElementIter = errorLocationElements.iterator();
            while (errorLocationElementIter.hasNext()) {
                Element errorLocationElement = (Element) errorLocationElementIter.next();
                String errorLocationElementName = UtilXml.childElementValue(errorLocationElement, "ErrorLocationElementName");
                String errorLocationAttributeName = UtilXml.childElementValue(errorLocationElement, "ErrorLocationAttributeName");
                
                errorMessageBuf.append("The error was at Element [");
                errorMessageBuf.append(errorLocationElementName);
                errorMessageBuf.append("]");

                if (UtilValidate.isNotEmpty(errorLocationAttributeName)) {
                    errorMessageBuf.append(" in the attribute [");
                    errorMessageBuf.append(errorLocationAttributeName);
                    errorMessageBuf.append("]");
                }
                
                List errorDigestElements = UtilXml.childElementList(errorLocationElement, "ErrorDigest");
                Iterator errorDigestElementIter = errorDigestElements.iterator();
                while (errorDigestElementIter.hasNext()) {
                    Element errorDigestElement = (Element) errorDigestElementIter.next();
                    errorMessageBuf.append(" full text: [");
                    errorMessageBuf.append(UtilXml.elementValue(errorDigestElement));
                    errorMessageBuf.append("]");
                }
            }
            
            errorList.add(errorMessageBuf.toString());
        }
    }
    
    /**
     * Opens a URL to UPS and makes a request.
     * @param upsService Name of the UPS service to invoke
     * @param xmlString XML message to send
     * @return XML string response from UPS
     * @throws UpsConnectException
     */
    public static String sendUpsRequest(String upsService, String xmlString) throws UpsConnectException {
        String conStr = UtilProperties.getPropertyValue("shipment.properties", "shipment.ups.connect.url");                
        if (conStr == null) {
            throw new UpsConnectException("Incomplete connection URL; check your UPS configuration");
        }
        
        // need a ups service to call
        if (upsService == null) {
            throw new UpsConnectException("UPS service name cannot be null");
        }
        
        // xmlString should contain the auth document at the beginning
        // all documents require an <?xml version="1.0"?> header
        if (xmlString == null) {
            throw new UpsConnectException("XML message cannot be null");
        }
        
        // prepare the connect string
        conStr = conStr.trim();
        if (!conStr.endsWith("/")) {
            conStr = conStr + "/";
        }
        conStr = conStr + upsService;
               
        if (Debug.verboseOn()) Debug.logVerbose("UPS Connect URL : " + conStr, module); 
        if (Debug.verboseOn()) Debug.logVerbose("UPS XML String : " + xmlString, module);
        
        HttpClient http = new HttpClient(conStr);
        String response = null;
        try {            
            response = http.post(xmlString);
        } catch (HttpClientException e) {            
            Debug.logError(e, "Problem connecting with UPS server", module);
            throw new UpsConnectException("URL Connection problem", e);
        }
        
        if (response == null) {
            throw new UpsConnectException("Received a null response");
        }
        if (Debug.verboseOn()) Debug.logVerbose("UPS Response : " + response, module);
        
        return response;
    }    
}

class UpsConnectException extends GeneralException {
    UpsConnectException() {
        super();
    }
    
    UpsConnectException(String msg) {
        super(msg);
    }
    
    UpsConnectException(Throwable t) {
        super(t);
    }
    
    UpsConnectException(String msg, Throwable t) {
        super(msg, t);
    }
}


/*
 * UPS Code Reference

UPS Service IDs
ShipConfirm
ShipAccept
Void
Track

Package Type Code
00 Unknown
01 UPS Letter
02 Package
03 UPS Tube
04 UPS Pak
21 UPS Express Box
24 UPS 25KG Box
25 UPS 10KG Box

Pickup Types
01 Daily Pickup
03 Customer Counter
06 One Time Pickup
07 On Call Air Pickup
19 Letter Center
20 Air Service Center

UPS Service Codes
US Origin
01 UPS Next Day Air
02 UPS 2nd Day Air
03 UPS Ground
07 UPS Worldwide Express
08 UPS Worldwide Expedited
11 UPS Standard
12 UPS 3-Day Select
13 UPS Next Day Air Saver
14 UPS Next Day Air Early AM
54 UPS Worldwide Express Plus
59 UPS 2nd Day Air AM
64 N/A
65 UPS Express Saver

Reference Number Codes
AJ Acct. Rec. Customer Acct.
AT Appropriation Number
BM Bill of Lading Number
9V COD Number
ON Dealer Order Number
DP Department Number
EI Employer's ID Number
3Q FDA Product Code
TJ Federal Taxpayer ID Number
IK Invoice Number
MK Manifest Key Number
MJ Model Number
PM Part Number
PC Production Code
PO Purchase Order No.
RQ Purchase Request No.
RZ Return Authorization No.
SA Salesperson No.
SE Serial No.
SY Social Security No.
ST Store No.
TN Transaction Ref. No. 

Error Codes
First note that in the ref guide there are about 21 pages of error codes
Here are some overalls:
1 Success (no error)
01xxxx XML Error
02xxxx Architecture Error
15xxxx Tracking Specific Error

 */


/*
 * Sample XML documents:
 *  
<?xml version="1.0"?>
<AccessRequest xml:lang="en-US">
   <AccessLicenseNumber>TEST262223144CAT</AccessLicenseNumber>
   <UserId>REG111111</UserId>
   <Password>REG111111</Password>
</AccessRequest>

=======================================
Shipment Confirm Request/Response
=======================================

<?xml version="1.0"?>
<ShipmentConfirmRequest xml:lang="en-US">
    <Request>
        <TransactionReference>
            <CustomerContext>Ship Confirm / nonvalidate</CustomerContext>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <RequestAction>ShipConfirm</RequestAction>
        <RequestOption>nonvalidate</RequestOption>
    </Request>
    <LabelSpecification>
        <LabelPrintMethod>
            <Code>GIF</Code>
        </LabelPrintMethod>
        <HTTPUserAgent>Mozilla/5.0</HTTPUserAgent>
        <LabelImageFormat>
            <Code>GIF</Code>
        </LabelImageFormat>
    </LabelSpecification>
    <Shipment>
        <Description>DescriptionofGoodsTest</Description>
        <Shipper>
            <Name>ShipperName</Name>
            <AttentionName>ShipperName</AttentionName>
            <PhoneNumber>2226267227</PhoneNumber>
            <ShipperNumber>12345E</ShipperNumber>
            <Address>
                <AddressLine1>123 ShipperStreet</AddressLine1>
                <AddressLine2>123 ShipperStreet</AddressLine2>
                <AddressLine3>123 ShipperStreet</AddressLine3>
                <City>ShipperCity</City>
                <StateProvinceCode>foo</StateProvinceCode>
                <PostalCode>03570</PostalCode>
                <CountryCode>DE</CountryCode>
            </Address>
        </Shipper>
        <ShipTo>
            <CompanyName>ShipToCompanyName</CompanyName>
            <AttentionName>ShipToAttnName</AttentionName>
            <PhoneNumber>3336367336</PhoneNumber>
            <Address>
                <AddressLine1>123 ShipToStreet</AddressLine1>
                <PostalCode>DT09</PostalCode>
                <City>Trent</City>
                <CountryCode>GB</CountryCode>
            </Address>
        </ShipTo>
        <ShipFrom>
            <CompanyName>ShipFromCompanyName</CompanyName>
            <AttentionName>ShipFromAttnName</AttentionName>
            <PhoneNumber>7525565064</PhoneNumber>
            <Address>
                <AddressLine1>123 ShipFromStreet</AddressLine1>
                <City>Berlin</City>
                <PostalCode>03570</PostalCode>
                <CountryCode>DE</CountryCode>
            </Address>
        </ShipFrom>
        <PaymentInformation>
            <Prepaid>
                <BillShipper>
                    <AccountNumber>12345E</AccountNumber>
                </BillShipper>
            </Prepaid>
        </PaymentInformation>
        <Service>
            <Code>07</Code>
        </Service>
        <Package>
            <PackagingType>
                <Code>02</Code>
            </PackagingType>
            <Dimensions>
                <UnitOfMeasurement>
                    <Code>CM</Code>
                </UnitOfMeasurement>
                <Length>60</Length>
                <Width>7</Width>
                <Height>5</Height>
            </Dimensions>
            <PackageWeight>
                <UnitOfMeasurement>
                    <Code>KGS</Code>
                </UnitOfMeasurement>
                <Weight>3.0</Weight>
            </PackageWeight>
            <ReferenceNumber>
                <Code>MK</Code>
                <Value>00001</Value>
            </ReferenceNumber>
        </Package>
    </Shipment>
</ShipmentConfirmRequest>

=======================================

<?xml version="1.0"?>
<ShipmentConfirmResponse>
    <Response>
        <TransactionReference>
            <CustomerContext>ShipConfirmUS</CustomerContext>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <ResponseStatusCode>1</ResponseStatusCode>
        <ResponseStatusDescription>Success</ResponseStatusDescription>
    </Response>
    <ShipmentCharges>
        <TransportationCharges>
            <CurrencyCode>USD</CurrencyCode>
            <MonetaryValue>31.38</MonetaryValue>
        </TransportationCharges>
        <ServiceOptionsCharges>
            <CurrencyCode>USD</CurrencyCode>
            <MonetaryValue>7.75</MonetaryValue>
        </ServiceOptionsCharges>
        <TotalCharges>
            <CurrencyCode>USD</CurrencyCode>
            <MonetaryValue>39.13</MonetaryValue>
        </TotalCharges>
    </ShipmentCharges>
    <BillingWeight>
        <UnitOfMeasurement>
            <Code>LBS</Code>
        </UnitOfMeasurement>
        <Weight>4.0</Weight>
    </BillingWeight>
    <ShipmentIdentificationNumber>1Z12345E1512345676</ShipmentIdentificationNumber>
    <ShipmentDigest>INSERT SHIPPING DIGEST HERE</ShipmentDigest>
</ShipmentConfirmResponse>

=======================================
Shipment Accept Request/Response
=======================================

<?xml version="1.0"?>
<ShipmentAcceptRequest>
    <Request>
        <TransactionReference>
            <CustomerContext>TR01</CustomerContext>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <RequestAction>ShipAccept</RequestAction>
        <RequestOption>01</RequestOption>
    </Request>
    <ShipmentDigest>INSERT SHIPPING DIGEST HERE</ShipmentDigest>
</ShipmentAcceptRequest>

=======================================

<?xml version="1.0"?>
<ShipmentAcceptResponse>
    <Response>
        <TransactionReference>
            <CustomerContext>TR01</CustomerContext>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <ResponseStatusCode>1</ResponseStatusCode>
        <ResponseStatusDescription>Success</ResponseStatusDescription>
    </Response>
    <ShipmentResults>
        <ShipmentCharges>
            <TransportationCharges>
                <CurrencyCode>USD</CurrencyCode>
                <MonetaryValue>31.38</MonetaryValue>
            </TransportationCharges>
            <ServiceOptionsCharges>
                <CurrencyCode>USD</CurrencyCode>
                <MonetaryValue>7.75</MonetaryValue>
            </ServiceOptionsCharges>
            <TotalCharges>
                <CurrencyCode>USD</CurrencyCode>
                <MonetaryValue>39.13</MonetaryValue>
            </TotalCharges>
        </ShipmentCharges>
        <BillingWeight>
            <UnitOfMeasurement>
                <Code>LBS</Code>
            </UnitOfMeasurement>
            <Weight>4.0</Weight>
        </BillingWeight>
        <ShipmentIdentificationNumber>1Z12345E1512345676</ShipmentIdentificationNumber>
        <PackageResults>
            <TrackingNumber>1Z12345E1512345676</TrackingNumber>
            <ServiceOptionsCharges>
                <CurrencyCode>USD</CurrencyCode>
                <MonetaryValue>0.00</MonetaryValue>
            </ServiceOptionsCharges>
            <LabelImage>
                <LabelImageFormat>
                    <Code>epl</Code>
                </LabelImageFormat>
                <GraphicImage>INSERT GRAPHIC IMAGE HERE</GraphicImage>
            </LabelImage>
        </PackageResults>
        <PackageResults>
            <TrackingNumber>1Z12345E1512345686</TrackingNumber>
            <ServiceOptionsCharges>
                <CurrencyCode>USD</CurrencyCode>
                <MonetaryValue>7.75</MonetaryValue>
            </ServiceOptionsCharges>
            <LabelImage>
                <LabelImageFormat>
                    <Code>epl</Code>
                </LabelImageFormat>
                <GraphicImage>INSERT GRAPHIC IMAGE HERE</GraphicImage>
            </LabelImage>
        </PackageResults>
    </ShipmentResults>
</ShipmentAcceptResponse>

=======================================
Void Shipment Request/Response
=======================================

<VoidShipmentRequest>
    <Request>
        <TransactionReference>
            <CustomerContext>Void</CustomerContext>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <RequestAction>Void</RequestAction>
        <RequestOption>1</RequestOption>
    </Request>
    <ShipmentIdentificationNumber>1Z12345E1512345676</ShipmentIdentificationNumber>
</VoidShipmentRequest>

=======================================

<?xml version="1.0"?>
<VoidShipmentResponse>
    <Response>
        <TransactionReference>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <ResponseStatusCode>1</ResponseStatusCode>
        <ResponseStatusDescription>Success</ResponseStatusDescription>
    </Response>
    <Status>
        <StatusType>
            <Code>1</Code>
            <Description>Success</Description>
        </StatusType>
        <StatusCode>
            <Code>1</Code>
            <Description>Success</Description>
        </StatusCode>
    </Status>
</VoidShipmentResponse>

=======================================
Void Shipment Request/Response
=======================================

<?xml version="1.0"?>
<TrackRequest xml:lang="en-US">
    <Request>
        <TransactionReference>
            <CustomerContext>sample</CustomerContext>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <RequestAction>Track</RequestAction>
    </Request>
    <TrackingNumber>1Z12345E1512345676</TrackingNumber>
</TrackRequest>

=======================================

<?xml version="1.0" encoding="UTF-8"?>
<TrackResponse>
    <Response>
        <TransactionReference>
            <CustomerContext>sample</CustomerContext>
            <XpciVersion>1.0001</XpciVersion>
        </TransactionReference>
        <ResponseStatusCode>1</ResponseStatusCode>
        <ResponseStatusDescription>Success</ResponseStatusDescription>
    </Response>
    <Shipment>
        <Shipper>
            <ShipperNumber>12345E</ShipperNumber>
        </Shipper>
        <Service>
            <Code>15</Code>
            <Description>NDA EAM/EXP EAM</Description>
        </Service>
        <ShipmentIdentificationNumber>1Z12345E1512345676</ShipmentIdentificationNumber>
        <Package>
            <TrackingNumber>1Z12345E1512345676</TrackingNumber>
            <Activity>
                <ActivityLocation>
                    <Address>
                        <City>CLAKVILLE</City>
                        <StateProvinceCode>AK</StateProvinceCode>
                        <PostalCode>99901</PostalCode>
                        <CountryCode>US</CountryCode>
                    </Address>
                    <Code>MG</Code>
                    <Description>MC MAN</Description>
                </ActivityLocation>
                <Status>
                    <StatusType>
                        <Code>D</Code>
                        <Description>DELIVERED</Description>
                    </StatusType>
                    <StatusCode>
                        <Code>FS</Code>
                    </StatusCode>
                </Status>
                <Date>20020930</Date>
                <Time>130900</Time>
            </Activity>
            <PackageWeight>
                <UnitOfMeasurement>
                    <Code>LBS</Code>
                </UnitOfMeasurement>
                <Weight>0.00</Weight>
            </PackageWeight>
        </Package>
    </Shipment>
</TrackResponse>

 */

 
 