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

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ShipmentServices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class UpsServices {

    public static Map upsShipmentConfirm(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String shipmentId = (String) context.get("shipmentId");
        String shipmentRouteSegmentId = (String) context.get("shipmentRouteSegmentId");

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

            List shipmentPackageRouteSegs = shipmentRouteSegment.getRelated("ShipmentPackageRouteSeg");
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
            UtilXml.addChildElementValue(shipperElement, "Name", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperElement, "AttentionName", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperElement, "PhoneNumber", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperElement, "ShipperNumber", "", shipmentConfirmRequestDoc);

            Element shipperAddressElement = UtilXml.addChildElement(shipperElement, "Address", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "AddressLine1", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "AddressLine2", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "AddressLine3", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "City", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "StateProvinceCode", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "PostalCode", "", shipmentConfirmRequestDoc);
            UtilXml.addChildElementValue(shipperAddressElement, "CountryCode", "", shipmentConfirmRequestDoc);
            // How to determine this? Add to data model... UtilXml.addChildElement(shipperAddressElement, "ResidentialAddress", shipmentConfirmRequestDoc);
            // TODO: fill Shipper element

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
            // TODO: fill in BillShipper AccountNumber element
            UtilXml.addChildElementValue(billShipperElement, "AccountNumber", "", shipmentConfirmRequestDoc);

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
                GenericValue weightUom = shipmentPackage.getRelatedOne("WeightUom");
                if (weightUom != null) {
                    UtilXml.addChildElementValue(packageWeightUnitOfMeasurementElement, "Code", weightUom.getString("abbreviation"), shipmentConfirmRequestDoc);
                } else {
                    // might as well default to LBS
                    UtilXml.addChildElementValue(packageWeightUnitOfMeasurementElement, "Code", "LBS", shipmentConfirmRequestDoc);
                }
                Element referenceNumberElement = UtilXml.addChildElement(packageElement, "ReferenceNumber", shipmentConfirmRequestDoc);
                UtilXml.addChildElementValue(referenceNumberElement, "Code", "MK", shipmentConfirmRequestDoc);
                UtilXml.addChildElementValue(referenceNumberElement, "Value", shipmentPackage.getString("shipmentPackageSeqId"), shipmentConfirmRequestDoc);

                if (carrierShipmentBoxType != null && carrierShipmentBoxType.get("oversizeCode") != null) {
                    UtilXml.addChildElementValue(packageElement, "OversizePackage", carrierShipmentBoxType.getString("oversizeCode"), shipmentConfirmRequestDoc);
                }
            }
            
            // TODO: create AccessRequest XML doc
            // TODO: connect to UPS server, send AccessRequest to auth
            // TODO: send ShipmentConfirmRequest doc
            // TODO: get ShipmentConfirmResponse doc back
            // TODO: process ShipmentConfirmResponse, update data as needed 

        } catch (GenericEntityException e) {
            Debug.logError(e);
            ServiceUtil.returnError("Error reading or writing Shipment data for UPS Shipment Confirm: " + e.toString());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    public static Map upsShipmentAccept(DispatchContext dctx, Map context) {
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
            ServiceUtil.returnError("Error reading or writing Shipment data for UPS Shipment Accept: " + e.toString());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}

/*
 * UPS Code Reference

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


 */

 
 