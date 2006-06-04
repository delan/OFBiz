/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.opentravelsystem;

import java.util.*;
import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;



/**
 * Utilities to be used on the OpentravelSystem data
 */
public class otsUtils {

    public static final String module = otsUtils.class.getName();
    
    public static long numberWritten = 0;
    public static long doubleRecords = 0;
    public static String prefix = null;
    public static GenericDelegator delegator = null;
    private static PrintWriter writer = null;
    private static String output = null;
    private static Stack stack = new Stack();
    
    // write an entity to the xml file
    public static void action(GenericValue entity)    {
        if (output.equals("xml")) {
            entity.writeXmlText( writer, "");
            numberWritten++;
        }
        if (output.equals("delete")) {
            stack.push(entity);
        }
        
    }
    
    // only top entity, no switches
    public static void explode(String topEntityName, String topEntityId)	{
        explode(topEntityName,  topEntityId, true, null,true, null,true,null,true,null,true,null,true );
    }
    // topentity, one child, no switches
    public static void explode(String topEntityName, String topEntityId, String child1EntityName )	{
        explode(topEntityName,  topEntityId, true, child1EntityName,true, null,true,null,true,null,true,null,true );
    }
    // topentity, one child, switches
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, String child1EntityName, boolean xmlWrite1 )	{
        explode(topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1, null,true,null,true,null,true,null,true );
    }
    // topentity, two children, no switches
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, String child2EntityName )	{
        explode(topEntityName,  topEntityId, true, child1EntityName, true,child2EntityName,  true, null,true,null,true,null,true );
    }
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, String child2EntityName,boolean xmlWrite2 )	{
        explode(  topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1,child2EntityName,  xmlWrite2, null,true, null,true, null, true);
    }
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, String child2EntityName,String child3EntityName )	{
        explode(topEntityName,  topEntityId, true, child1EntityName, true,child2EntityName,  true,child3EntityName, true, null,  true,null,  true );
    }
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2, String child3EntityName, boolean xmlWrite3 )	{
        explode(  topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1, child2EntityName, xmlWrite2,child3EntityName, xmlWrite3, null,  true,null,  true );
    }
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, String child2EntityName,String child3EntityName,String child4EntityName  )	{
        explode(topEntityName,  topEntityId, true, child1EntityName, true,child2EntityName,  true,child3EntityName, true, child4EntityName, true, null, true );
    }
    public static void explode(String topEntityName, String topEntityId,  boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2, String child3EntityName, boolean xmlWrite3, String child4EntityName, boolean xmlWrite4  )	{
        explode(  topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1, child2EntityName,  xmlWrite2, child3EntityName,  xmlWrite3,child4EntityName,  xmlWrite4, null, true );
    }
    // all no entityId and no ignore
    public static void explode(String topEntityName, String topEntityId, 
            String child1EntityName,
            String child2EntityName,
            String child3EntityName,
            String child4EntityName, 
            String child5EntityName  )    {
        explode(topEntityName,  topEntityId, true,  
                child1EntityName, true,
                child2EntityName, true,
                child3EntityName, true, 
                child4EntityName, true, 
                child5EntityName, true );
    }
    // all, no entity id
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, 
            String child1EntityName, boolean xmlWrite1, 
            String child2EntityName, boolean xmlWrite2,
            String child3EntityName, boolean xmlWrite3,
            String child4EntityName, boolean xmlWrite4,
            String child5EntityName, boolean xmlWrite5 )    {
        explode(topEntityName,  topEntityId, xmlWrite0, 
                child1EntityName, null, xmlWrite1, 
                child2EntityName, null, xmlWrite2, 
                child3EntityName, null, xmlWrite3, 
                child4EntityName, null, xmlWrite4, 
                child5EntityName, null, xmlWrite5 );
    }
    // all, no ignore
    public static void explode(String topEntityName, String topEntityId, 
            String child1EntityName, String child1EntityId, 
            String child2EntityName, String child2EntityId,
            String child3EntityName, String child3EntityId,
            String child4EntityName, String child4EntityId, 
            String child5EntityName, String child5EntityId )  {
        explode(topEntityName,  topEntityId, true, 
                child1EntityName, null, true, 
                child2EntityName, null, true, 
                child3EntityName, null, true, 
                child4EntityName, null, true, 
                child5EntityName, null, true );
    }
    // 4, no ignore
    public static void explode(String topEntityName, String topEntityId, 
            String child1EntityName, String child1EntityId, 
            String child2EntityName, String child2EntityId,
            String child3EntityName, String child3EntityId,
            String child4EntityName, String child4EntityId, 
            PrintWriter writer)  {
        explode(topEntityName,  topEntityId, true, 
                child1EntityName, child1EntityId, true, 
                child2EntityName, child2EntityId, true, 
                child3EntityName, child3EntityId, true, 
                child4EntityName, child4EntityId, true, 
                null, null, false );
    }
    
   /**
    * This routine will export from the data base in xml format if the xmlWrite flag is true.
    * If false then no xml file will be generated but a list will be returned of the lowest level provided
    * Input is a path through the database of related entities.
    * @param topEntityName
    * @param topEntityId
    * @param xmlWrite0
    * @param child1EntityName
    * @param child1EntityId
    * @param xmlWrite1
    * @param child2EntityName
    * @param child2EntityId
    * @param xmlWrite2
    * @param child3EntityName
    * @param child3EntityId
    * @param xmlWrite3
    * @param child4EntityName
    * @param child4EntityId
    * @param xmlWrite4
    * @param child5EntityName
    * @param child5EntityId
    * @param xmlWrite5
    * @param writer
    * @param xmlWrite
    * @return
    */
    private static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, 
            String child1EntityName, String child1EntityId, boolean xmlWrite1, 
            String child2EntityName, String child2EntityId, boolean xmlWrite2,
            String child3EntityName, String child3EntityId, boolean xmlWrite3,
            String child4EntityName, String child4EntityId, boolean xmlWrite4,
            String child5EntityName, String child5EntityId, boolean xmlWrite5 )	{
    	try {
    		Debug.logInfo("==processing topEntity:" + topEntityName,module);
    		List topEntityList = delegator.findByLike(topEntityName,UtilMisc.toMap(topEntityId,prefix.concat("%")),UtilMisc.toList(topEntityId));
    		if (topEntityList != null && topEntityList.size() > 0) {
    			Iterator t = topEntityList.iterator();
    			while(t.hasNext()) {
    				GenericValue topEntityListItem = (GenericValue) t.next();
    				if (xmlWrite0) { action(topEntityListItem); }
    				if (child1EntityName != null)	{
                        List children1 = null;
                        if (child1EntityId != null)
                            children1 = topEntityListItem.getRelatedByAnd(child1EntityName, UtilMisc.toMap(child1EntityId,prefix.concat("%")));
                        else
                            children1 = topEntityListItem.getRelated(child1EntityName);
    					if (children1 != null && children1.size() > 0)	{
    						Iterator psr1 = children1.iterator();
    						while (psr1.hasNext())	{
    							GenericValue child1 = (GenericValue) psr1.next();
    							if (child2EntityName != null)	{
                                    List children2 = null;
                                    if (child2EntityId != null)
                                        children2 = child1.getRelatedByAnd(child2EntityName, UtilMisc.toMap(child2EntityId,prefix.concat("%")));
                                    else
                                        children2 = child1.getRelated(child2EntityName);
    								if (children2 != null && children2.size() > 0)	{
    									Iterator psr2 = children2.iterator();
    									while (psr2.hasNext())	{
    										GenericValue child2 = (GenericValue) psr2.next();
    										if (child3EntityName != null)	{
                                                List children3 = null;
                                                if (child3EntityId != null)
                                                    children3 = child2.getRelatedByAnd(child3EntityName, UtilMisc.toMap(child3EntityId,prefix.concat("%")));
                                                else
                                                    children3 = child2.getRelated(child3EntityName);
    											if (children3 != null && children3.size() > 0)	{
    												Iterator psr3 = children3.iterator();
    												while (psr3.hasNext())	{
    													GenericValue child3 = (GenericValue) psr3.next();
    													if (child4EntityName != null)	{
                                                            List children4 = null;
                                                            if (child4EntityId != null)
                                                                children4 = child3.getRelatedByAnd(child4EntityName, UtilMisc.toMap(child4EntityId,prefix.concat("%")));
                                                            else
                                                                children4 = child3.getRelated(child4EntityName);
    														if (children4 != null && children4.size() > 0)	{
    															Iterator psr4 = children4.iterator();
    															while (psr4.hasNext())	{
    																GenericValue child4 = (GenericValue) psr4.next();
    																if (child5EntityName != null)	{
                                                                        List children5 = null;
                                                                        if (child5EntityId != null)
                                                                            children5 = child4.getRelatedByAnd(child5EntityName, UtilMisc.toMap(child5EntityId,prefix.concat("%")));
                                                                        else
                                                                            children5 = child4.getRelated(child5EntityName);
    																	if (children5 != null && children5.size() > 0)	{
    																		Iterator psr5 = children5.iterator();
    																		while (psr5.hasNext())	{
    																			GenericValue child5 = (GenericValue) psr5.next();
    																			if (xmlWrite5)  action(child5);
    																			//                                                               if (child5EntityName != null) { if (lastChildren != null) lastChildren.addAll(children5); else lastChildren = children5; }
    																		}
    																	}
    																}
    																if (xmlWrite4) action(child4);
    																//                                                   if (child5EntityName == null) { if (lastChildren != null) lastChildren.addAll(children4); else lastChildren = children4; }
    															}
    														}
    													}
                                                        if (xmlWrite3) action(child3);
    													//                                       if (child4EntityName == null) { if (lastChildren != null) lastChildren.addAll(children3); else lastChildren = children3; }
    												}
    											}
    										}
                                            if (xmlWrite2) action(child2);
    										//                          if (child3EntityName == null) { if (lastChildren != null) lastChildren.addAll(children2); else lastChildren = children2; }
    									}
    								}
    							}
                                if (xmlWrite1) action(child1);
    							//               if (child2EntityName == null) { if (lastChildren != null) lastChildren.addAll(children1); else lastChildren = children1; }
    						}
    					}
    				}
    			}
    		}
    	} catch (GenericEntityException e) { Debug.logError(e, "Problems exploding" + topEntityName, module);}
    }
    
    /**
     * Unload Opentravelsystem data from the database (experimental program)
     *
     * @param productStoreId : the store name
     * @return a string containing the result.
     *
     * @author     <a href="mailto:info@opentravelsystem.org">Hans Bakker</a>
     * @version    $Rev: 1$
     * @since      3.0
     *
     * This program exports all data related to the specified productStoreName.
     * It unloads the productstore information with the roles 
     * It unloads the related Catalog and Categories
     * It unloads all products which have a primary category which is the same as the productStoreName and the relations to the category
     * It unloads all fixed Assets whith a parent fixed asset with an Id which is the same as the productStoreName and the relations to the products.
     * It also u
     */
    public static String unLoad(HttpServletRequest request, HttpServletResponse response) {
        delegator = (GenericDelegator) request.getAttribute("delegator");
        prefix = (String) request.getParameter("downloadId");
        output = (String) request.getParameter("action");
        StringBuffer msgs = new StringBuffer();
        
        String downloadLoc = null;
        if (output.equals("xml")) {
            String fileName = new String("specialized/opentravelsystem/webapp/hotelbackend/html/" + prefix + ".xml");
            downloadLoc = new String("backend/html/" + prefix + ".xml");
            
            FileOutputStream fos = null;
            try { fos = new FileOutputStream(fileName);
            } catch (FileNotFoundException e) {
                request.setAttribute("_ERROR_MESSAGE_", "File " + fileName + " could not be created");
                return "error";
            }
            OutputStreamWriter osw = null;
            try { osw = new OutputStreamWriter(fos, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Encoding not supported");
                return "error";
            }
            
            BufferedWriter bw = new BufferedWriter(osw);
            writer = new PrintWriter(bw);
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<entity-engine-xml>");

            // create party table first without the created/modified by userlogin fields
            try {
                List parties = delegator.findByLike("Party", UtilMisc.toMap("partyId", prefix.concat("%")));
                Iterator t = parties.iterator();
                while(t.hasNext()) {
                    GenericValue party = (GenericValue) t.next();
                    party.remove("createdByUserLogin");
                    party.remove("lastModifiedByUserLogin");
                    action(party);
                }
            } catch (GenericEntityException e) { Debug.logError(e, "Problems parties file", module);}
            doubleRecords = numberWritten; // these records should be counted
        }

        // security
        explode("SecurityGroup", "groupId" );
        explode("SecurityGroupPermission", "groupId" );
        
        //parties
        explode("Party", "partyId",  "UserLogin" ); 
        explode("Party", "partyId", false,  "UserLogin", false, "UserLoginSecurityGroup", true ); 
        explode("Party", "partyId", false, "PartyGroup", true );
        explode("Party", "partyId", false, "Person", true );
        explode("Party", "partyId", false, "PartyRole",true );
        
        // contact mechanisms
        explode("ContactMech", "contactMechId", "PartyContactMech" );
        explode("PostalAddress", "contactMechId" );
        explode("TelecomNumber", "contactMechId" );
        
        // payment methods
        explode("PaymentMethod", "paymentMethodId" );
        explode("EftAccount", "paymentMethodId" );
        explode("CreditCard", "paymentMethodId" );
        explode("GiftCard", "paymentMethodId" );

        /* specifics for Anet
        if (prefix.equals("anet") && output.equals("xml")) {
            
            try {
                List cmPartys = delegator.findByLike("PartyContactMech", UtilMisc.toMap("partyId", prefix.concat("%")));
                if (cmPartys != null && cmPartys.size() > 0) {
                    Iterator t = cmPartys.iterator();
                    while(t.hasNext()) {
                        GenericValue cmParty = (GenericValue) t.next();
                        GenericValue cm = (GenericValue) delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", cmParty.getString("contactMechId")));
                        if (cm != null)  action(cm);
                        cm = (GenericValue) delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", cmParty.getString("contactMechId")));
                        if (cm != null)  action(cm);
                        cm = (GenericValue) delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", cmParty.getString("contactMechId")));
                        if (cm != null)  action(cm);
                        action(cmParty);
                    }
                }
                
                List pms = delegator.findByLike("PaymentMethod", UtilMisc.toMap("partyId", prefix.concat("%")));
                if (pms != null && pms.size() > 0) {
                    Iterator t = pms.iterator();
                    while(t.hasNext()) {
                        GenericValue pm = (GenericValue) t.next();
                        String pmId = pm.getString("paymentMethodId");
                        action(pm);
                        GenericValue cc = delegator.findByPrimaryKey("EftAccount",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null)  action(cc);
                        cc = delegator.findByPrimaryKey("CreditCard",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null)  action(cc);
                    }
                }
                
                GenericValue party = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId","BelastingDienst"));
                action(party);
                party = delegator.findByPrimaryKey("TaxAuthority",UtilMisc.toMap("taxAuthPartyId","BelastingDienst","taxAuthGeoId", "NLD"));
                action(party);
                party = delegator.findByPrimaryKey("PartyGroup",UtilMisc.toMap("partyId","BelastingDienst"));
                action(party);
                List roles = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId", "BelastingDienst"));
                if (roles != null && roles.size() > 0) {
                    Iterator t = roles.iterator();
                    while(t.hasNext()) {
                        GenericValue role = (GenericValue) t.next();
                        action(role);
                    }
                }
                party = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId","Stulemeijer"));
                action(party);
                party = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId","Stulemeijer"));
                action(party);
                roles = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId", "Stulemeijer"));
                if (roles != null && roles.size() > 0) {
                    Iterator t = roles.iterator();
                    while(t.hasNext()) {
                        GenericValue role = (GenericValue) t.next();
                        action(role);
                    }
                }
                pms = delegator.findByAnd("PaymentMethod", UtilMisc.toMap("partyId","Stulemeijer"));
                if (pms != null && pms.size() > 0) {
                    Iterator t = pms.iterator();
                    while(t.hasNext()) {
                        GenericValue pm = (GenericValue) t.next();
                        String pmId = pm.getString("paymentMethodId");
                        action(pm);
                        GenericValue cc = delegator.findByPrimaryKey("EftAccount",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) action(cc);
                        cc = delegator.findByPrimaryKey("CreditCard",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) action(cc);
                    }
                }
                party = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId","Sidin"));
                action(party);
                party = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId","Sidin"));
                action(party);
                roles = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId", "Sidin"));
                if (roles != null && roles.size() > 0) {
                    Iterator t = roles.iterator();
                    while(t.hasNext()) {
                        GenericValue role = (GenericValue) t.next();
                        action(role);
                    }
                }
                pms = delegator.findByAnd("PaymentMethod", UtilMisc.toMap("partyId","Sidin"));
                if (pms != null && pms.size() > 0) {
                    Iterator t = pms.iterator();
                    while(t.hasNext()) {
                        GenericValue pm = (GenericValue) t.next();
                        String pmId = pm.getString("paymentMethodId");
                        action(pm);
                        GenericValue cc = delegator.findByPrimaryKey("EftAccount",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null)  action(cc);
                        cc = delegator.findByPrimaryKey("CreditCard",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null)  action(cc);
                    }
                }
                
            } catch (GenericEntityException e) { Debug.logError(e, "Problems reading extra parties for A-NeT", module);}
        }
        */

        // tax tables
        explode("TaxAuthority","taxAuthPartyId");
        explode("PartyTaxAuthInfo","partyId");
        
        // relationship
        explode("Party", "partyId", false, "ToPartyRelationship", true );

        // store
        GenericValue productStore = null;
        try{ productStore =delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", prefix));
		} catch (GenericEntityException e) { Debug.logError(e, "Problems reading ProductStore", module);}
        if (productStore == null)	{
            request.setAttribute("_ERROR_MESSAGE_", "ProductStore " + prefix + " not found");
        	return "error";		//productstore not found
        }
        //get the productstore, related website records id
        // get the payment settings
        explode("ProductStore", "productStoreId", "ProductStorePaymentSetting");
        explode("ProductStore", "productStoreId", false, "ProductStoreEmailSetting",true);
        explode("WebSite", "webSiteId" );
        // get the ProductStoreShipmentMeth
        // explode(productStore, "ProductStoreShipmentMeth");  // no direct link...
        explode("ProductStoreShipmentMeth","productStoreId" ); 
        // productStore, catalog and categories
        explode("ProdCatalog","prodCatalogId" ); 
        explode("ProductStore", "productStoreId", false,"ProductStoreCatalog",true); //productstore/catalog relation
        explode("ProductCategory","productCategoryId", "ProdCatalogCategory" ); // category and relation to catalog
        explode("ProductCategoryRollup","productCategoryId");  // category relations
        
        // get all products and prices en link to category and acciciations
        explode("Product","productId", "ProductPrice" );
        explode("Product","productId", false, "ProductCategoryMember", true );
        explode("Product","productId", false, "AssocProductAssoc", true );

        // product features
        explode("ProductFeatureCategory", "productFeatureCategoryId", "ProductFeature", "ProductFeatureAppl" );
        
        // get all fixed assets and link to product
        explode("FixedAsset","fixedAssetId", "FixedAssetProduct" );

        // get all content, resource and electronic text
        explode("DataResource",  "dataResourceId");
        explode("ElectronicText", "dataResourceId");
        explode("ImageDataResource", "dataResourceId");
        explode("Content","contentId" );
        explode("Content","contentId", false,"FromContentAssoc", true );

        //communication
        explode("CommunicationEvent","communicationEventId" );
        explode("CommunicationEvent","communicationEventId", false, "CommEventContentAssoc", true );
        explode("ContactList","contactListId", "ContactListParty" );

        // invoices/payments/applications
        explode("PartyAcctgPreference", "partyId" );
        explode("CustomTimePeriod", "organizationPartyId" );
        explode("Invoice", "invoiceId", "InvoiceItem" );
        explode("Invoice", "invoiceId",false, "InvoiceStatus", true );
        explode("Invoice", "invoiceId",false, "InvoiceRole", true );
        explode("Payment", "paymentId" );
        explode("Payment", "paymentId", false, "PaymentApplication",true );

        explode("GlAccountOrganization", "organizationPartyId" );
        explode("PaymentMethodTypeGlAccount", "organizationPartyId" );
        explode("GlAccountTypeDefault", "organizationPartyId" );
        explode("PaymentGlAccountTypeMap", "organizationPartyId" );
        explode("InvoiceItemTypeGlAccount", "organizationPartyId" );
        
        // orders (to be completed)
        explode("OrderHeader","orderId", "OrderItem" );
        explode("OrderHeader","orderId", false, "OrderItem", false, "orderItemPriceInfo", true );
        explode("OrderItemBilling","orderId");
        
        // sequence value items: SequenceValueItems
        explode("SequenceValueItem","seqName");

        if (output.equals("xml")) {
            writer.println("</entity-engine-xml>");
            writer.close();
            msgs.append("Unload ended normally, statistics:\n");
            msgs.append("--Entities written to output file: " + numberWritten + " double records: " + doubleRecords + "\n");
            msgs.append("<a href=/"  + downloadLoc + ">--Download your generated file here</a>");
            if (msgs.length() > 0)
                request.setAttribute("_EVENT_MESSAGE_", msgs.toString());
        }

        if ( output.equals("delete")) {
            // delete records created in the server hit table
           
            GenericValue current = null;
            try{
                // keep history (not tested)
/*             explode("Party", "partyId", false,  "UserLogin", false, "ServerHit", true ); 
                explode("Party", "partyId", false,  "UserLogin", false, "UserLoginSession", true ); 
                explode("Visitor", "partyId"); 
                explode("UserLoginHistory", "partyId"); 
*/                
                // delete history
                EntityExpr exprParty = new EntityExpr("partyId", EntityOperator.LIKE, prefix.concat("%"));
                EntityExpr exprContent = new EntityExpr("contentId", EntityOperator.LIKE, prefix.concat("%"));
                // delete all serverHits related to the visits to be deleted
                List visits = delegator.findByCondition("Visit", exprParty,null,null);
                if (visits != null && visits.size() > 0) {
                    Iterator h = (Iterator) visits.iterator();
                    while(h.hasNext()) {
                        GenericValue visit = (GenericValue) h.next();
                        List serverHits = visit.getRelated("ServerHit");
                        if (serverHits != null && serverHits.size() > 0) {
                            Iterator s = serverHits.iterator();
                            while (s.hasNext()) {
                                ((GenericValue) s.next()).remove();
                            }
                        }
                        visit.remove(); // delete visit
                    }
                }
                
                delegator.removeByCondition( "Visitor", exprParty );
//                delegator.removeByCondition( "UserLoginSession", exprUserLogin );
                delegator.removeByCondition( "UserLoginHistory", exprParty );
                delegator.removeByCondition( "ServerHitBin", exprContent );
                
                while(!stack.empty()) {
                    current =  (GenericValue) stack.pop();
                    delegator.removeValue(current);
                    numberWritten++;
                }
            } catch (GenericEntityException e) { Debug.logError(e, "Problems deleting:" + current, module);}
            msgs.append("Deletion ended normally, statistics:\n");
            msgs.append("--Entities deleted: " + numberWritten + "\n");
            if (msgs.length() > 0)
                request.setAttribute("_EVENT_MESSAGE_", msgs.toString());
        }
        
        numberWritten = 0;
        return "success";
    }

    // boolean can not be defined in a minilanguage file
    public static Collection getContactMech(GenericValue party, String contactMechPurposeTypeId, String contactMechTypeId) {
    	return org.ofbiz.party.contact.ContactHelper.getContactMech(party, contactMechPurposeTypeId, contactMechTypeId, false); 
    }
}