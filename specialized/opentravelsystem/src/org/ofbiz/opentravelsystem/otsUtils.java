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


/**
 * Utilities to be used on the OpentravelSystem data
 */
public class otsUtils {

    public static final String module = otsUtils.class.getName();
    
    public static long numberWritten = 0;
    public static String prefix = null;
    public static GenericDelegator delegator = null;

    // only top entity, no switches
    public static void explode(String topEntityName, String topEntityId, PrintWriter writer)	{
        explode(topEntityName,  topEntityId, true, null,true, null,true,null,true,null,true,null,true, writer);
    }
    // topentity, one child, no switches
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, PrintWriter writer)	{
        explode(topEntityName,  topEntityId, true, child1EntityName,true, null,true,null,true,null,true,null,true, writer);
    }
    // topentity, one child, switches
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, PrintWriter writer)	{
        explode(topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1, null,true,null,true,null,true,null,true, writer);
    }
    // topentity, two children, no switches
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, String child2EntityName, PrintWriter writer)	{
        explode(topEntityName,  topEntityId, true, child1EntityName, true,child2EntityName,  true, null,true,null,true,null,true, writer);
    }
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, String child2EntityName,boolean xmlWrite2, PrintWriter writer)	{
        explode(  topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1,child2EntityName,  xmlWrite2, null,true, null,true, null, true,writer);
    }
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, String child2EntityName,String child3EntityName, PrintWriter writer)	{
        explode(topEntityName,  topEntityId, true, child1EntityName, true,child2EntityName,  true,child3EntityName, true, null,  true,null,  true, writer);
    }
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2, String child3EntityName, boolean xmlWrite3, PrintWriter writer)	{
        explode(  topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1, child2EntityName, xmlWrite2,child3EntityName, xmlWrite3, null,  true,null,  true, writer);
    }
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, String child2EntityName,String child3EntityName,String child4EntityName, PrintWriter writer )	{
        explode(topEntityName,  topEntityId, true, child1EntityName, true,child2EntityName,  true,child3EntityName, true, child4EntityName, true, null, true, writer);
    }
    public static void explode(String topEntityName, String topEntityId,  boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2, String child3EntityName, boolean xmlWrite3, String child4EntityName, boolean xmlWrite4, PrintWriter writer )	{
        explode(  topEntityName,  topEntityId, xmlWrite0, child1EntityName, xmlWrite1, child2EntityName,  xmlWrite2, child3EntityName,  xmlWrite3,child4EntityName,  xmlWrite4, null, true, writer);
    }
    // all no entityId and no ignore
    public static void explode(String topEntityName, String topEntityId, 
            String child1EntityName,
            String child2EntityName,
            String child3EntityName,
            String child4EntityName, 
            String child5EntityName, PrintWriter writer )    {
        explode(topEntityName,  topEntityId, true,  
                child1EntityName, true,
                child2EntityName, true,
                child3EntityName, true, 
                child4EntityName, true, 
                child5EntityName, true, writer);
    }
    // all, no entity id
    public static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, 
            String child1EntityName, boolean xmlWrite1, 
            String child2EntityName, boolean xmlWrite2,
            String child3EntityName, boolean xmlWrite3,
            String child4EntityName, boolean xmlWrite4,
            String child5EntityName, boolean xmlWrite5, PrintWriter writer)    {
        explode(topEntityName,  topEntityId, xmlWrite0, 
                child1EntityName, null, xmlWrite1, 
                child2EntityName, null, xmlWrite2, 
                child3EntityName, null, xmlWrite3, 
                child4EntityName, null, xmlWrite4, 
                child5EntityName, null, xmlWrite5, writer);
    }
    // all, no ignore
    public static void explode(String topEntityName, String topEntityId, 
            String child1EntityName, String child1EntityId, 
            String child2EntityName, String child2EntityId,
            String child3EntityName, String child3EntityId,
            String child4EntityName, String child4EntityId, 
            String child5EntityName, String child5EntityId, PrintWriter writer)  {
        explode(topEntityName,  topEntityId, true, 
                child1EntityName, null, true, 
                child2EntityName, null, true, 
                child3EntityName, null, true, 
                child4EntityName, null, true, 
                child5EntityName, null, true, writer);
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
                null, null, false, writer);
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
            String child5EntityName, String child5EntityId, boolean xmlWrite5, PrintWriter writer)	{
    	try {
    		Debug.logInfo("==processing topEntity:" + topEntityName,module);
    		List topEntityList = delegator.findByLike(topEntityName,UtilMisc.toMap(topEntityId,prefix.concat("%")),UtilMisc.toList(topEntityId));
    		if (topEntityList != null && topEntityList.size() > 0) {
    			Iterator t = topEntityList.iterator();
    			while(t.hasNext()) {
    				GenericValue topEntityListItem = (GenericValue) t.next();
    				if (xmlWrite0) { topEntityListItem.writeXmlText(writer,""); numberWritten++; }
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
    																			if (xmlWrite5) { child5.writeXmlText(writer,""); numberWritten++;}
    																			//                                                               if (child5EntityName != null) { if (lastChildren != null) lastChildren.addAll(children5); else lastChildren = children5; }
    																		}
    																	}
    																}
    																if (xmlWrite4) { child4.writeXmlText(writer,""); numberWritten++;}
    																//                                                   if (child5EntityName == null) { if (lastChildren != null) lastChildren.addAll(children4); else lastChildren = children4; }
    															}
    														}
    													}
    													if (xmlWrite3) { child3.writeXmlText(writer,""); numberWritten++; }
    													//                                       if (child4EntityName == null) { if (lastChildren != null) lastChildren.addAll(children3); else lastChildren = children3; }
    												}
    											}
    										}
    										if (xmlWrite2) { child2.writeXmlText(writer,""); numberWritten++;}
    										//                          if (child3EntityName == null) { if (lastChildren != null) lastChildren.addAll(children2); else lastChildren = children2; }
    									}
    								}
    							}
    							if (xmlWrite1) { child1.writeXmlText(writer,""); numberWritten++; }
    							//               if (child2EntityName == null) { if (lastChildren != null) lastChildren.addAll(children1); else lastChildren = children1; }
    						}
    					}
    				}
    			}
    		}
    	} catch (GenericEntityException e) { Debug.logError(e, "Problems exploding" + topEntityName, module);}
    }
    
    public static GenericValue usedOn(GenericValue bottomEntityName, String parent1EntityName, PrintWriter writer)	{
        GenericValue lastParent = (GenericValue) null;
        GenericValue parent = (GenericValue) null;
        try { parent = bottomEntityName.getRelatedOne(parent1EntityName);
        } catch (GenericEntityException e) { Debug.logError(e, "Problems reading parent entity: " + parent1EntityName, module);}
        if (parent != null)	{
        	lastParent = parent;
        }
    return lastParent;    
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
        String productStoreId = (String) request.getParameter("downloadId");
        StringBuffer msgs = new StringBuffer();

        // single file
        if(productStoreId == null || productStoreId.length() == 0) {
            request.setAttribute("_ERROR_MESSAGE_", "No productStore defined...");
            return "error";
        }

        String fileName = new String("specialized/opentravelsystem/webapp/hotelbackend/html/" + productStoreId + ".xml");
        String downloadLoc = new String("backend/html/" + productStoreId + ".xml");

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
        PrintWriter writer = new PrintWriter(bw);
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<entity-engine-xml>");
        
        prefix = productStoreId;	// all main entities ID's are prefixed with this prefix

        // create party table first without the created/modified by userlogin fields
        try {
            List parties = delegator.findByLike("Party", UtilMisc.toMap("partyId", prefix.concat("%")));
            Iterator t = parties.iterator();
            while(t.hasNext()) {
                GenericValue party = (GenericValue) t.next();
                party.remove("createdByUserLogin");
                party.remove("lastModifiedByUserLogin");
                party.writeXmlText(writer,""); numberWritten++;
            }
        } catch (GenericEntityException e) { Debug.logError(e, "Problems parties file", module);}
        
        
        // get all parties
        explode("Party", "partyId",
                "UserLogin",null, 
                "UserLoginSecurityGroup", "groupId", 
                "SecurityGroupPermission", null, 
                "SecurityPermission", "permissionId", 
                writer);
        explode("Party", "partyId", false, "PartyGroup", true, writer);
        explode("Party", "partyId", false, "PartyRole",true, writer);
        
        // contact mechanisms
        explode("ContactMech", "contactMechId", "PartyContactMech", writer);
        explode("PostalAddress", "contactMechId", writer);
        explode("TelecomNumber", "contactMechId", writer);
        
        // payment methods
        explode("PaymentMethod", "paymentMethodId", writer);
        explode("EftAccount", "paymentMethodId", writer);
        explode("CreditCard", "paymentMethodId", writer);
        explode("GiftCard", "paymentMethodId", writer);

        // specifics for Anet
        if (prefix.equals("anet")) {
            
            try {
                List cmPartys = delegator.findByLike("PartyContactMech", UtilMisc.toMap("partyId", prefix.concat("%")));
                if (cmPartys != null && cmPartys.size() > 0) {
                    Iterator t = cmPartys.iterator();
                    while(t.hasNext()) {
                        GenericValue cmParty = (GenericValue) t.next();
                        GenericValue cm = (GenericValue) delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", cmParty.getString("contactMechId")));
                        if (cm != null) { cm.writeXmlText(writer,"");numberWritten++; }
                        cm = (GenericValue) delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", cmParty.getString("contactMechId")));
                        if (cm != null) { cm.writeXmlText(writer,"");numberWritten++; }
                        cm = (GenericValue) delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", cmParty.getString("contactMechId")));
                        if (cm != null) { cm.writeXmlText(writer,"");numberWritten++; }
                        cmParty.writeXmlText(writer,"");
                    }
                }
                
                List pms = delegator.findByLike("PaymentMethod", UtilMisc.toMap("partyId", prefix.concat("%")));
                if (pms != null && pms.size() > 0) {
                    Iterator t = pms.iterator();
                    while(t.hasNext()) {
                        GenericValue pm = (GenericValue) t.next();
                        String pmId = pm.getString("paymentMethodId");
                        pm.writeXmlText(writer,"");numberWritten++;
                        GenericValue cc = delegator.findByPrimaryKey("EftAccount",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) {
                            cc.writeXmlText(writer,"");numberWritten++;
                        }
                        cc = delegator.findByPrimaryKey("CreditCard",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) {
                            cc.writeXmlText(writer,"");numberWritten++;
                        }
                    }
                }
                
                GenericValue party = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId","BelastingDienst"));
                party.writeXmlText(writer,""); numberWritten++;
                party = delegator.findByPrimaryKey("TaxAuthority",UtilMisc.toMap("taxAuthPartyId","BelastingDienst","taxAuthGeoId", "NLD"));
                party.writeXmlText(writer,""); numberWritten++;
                party = delegator.findByPrimaryKey("PartyGroup",UtilMisc.toMap("partyId","BelastingDienst"));
                party.writeXmlText(writer,""); numberWritten++;
                List roles = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId", "BelastingDienst"));
                if (roles != null && roles.size() > 0) {
                    Iterator t = roles.iterator();
                    while(t.hasNext()) {
                        GenericValue role = (GenericValue) t.next();
                        role.writeXmlText(writer,""); numberWritten++;
                    }
                }
                party = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId","Stulemeijer"));
                party.writeXmlText(writer,""); numberWritten++;
                party = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId","Stulemeijer"));
                party.writeXmlText(writer,""); numberWritten++;
                roles = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId", "Stulemeijer"));
                if (roles != null && roles.size() > 0) {
                    Iterator t = roles.iterator();
                    while(t.hasNext()) {
                        GenericValue role = (GenericValue) t.next();
                        role.writeXmlText(writer,""); numberWritten++;
                    }
                }
                pms = delegator.findByAnd("PaymentMethod", UtilMisc.toMap("partyId","Stulemeijer"));
                if (pms != null && pms.size() > 0) {
                    Iterator t = pms.iterator();
                    while(t.hasNext()) {
                        GenericValue pm = (GenericValue) t.next();
                        String pmId = pm.getString("paymentMethodId");
                        pm.writeXmlText(writer,"");numberWritten++;
                        GenericValue cc = delegator.findByPrimaryKey("EftAccount",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) {
                            cc.writeXmlText(writer,"");numberWritten++;
                        }
                        cc = delegator.findByPrimaryKey("CreditCard",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) {
                            cc.writeXmlText(writer,"");numberWritten++;
                        }
                    }
                }
                party = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId","Sidin"));
                party.writeXmlText(writer,""); numberWritten++;
                party = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId","Sidin"));
                party.writeXmlText(writer,""); numberWritten++;
                roles = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId", "Sidin"));
                if (roles != null && roles.size() > 0) {
                    Iterator t = roles.iterator();
                    while(t.hasNext()) {
                        GenericValue role = (GenericValue) t.next();
                        role.writeXmlText(writer,""); numberWritten++;
                    }
                }
                pms = delegator.findByAnd("PaymentMethod", UtilMisc.toMap("partyId","Sidin"));
                if (pms != null && pms.size() > 0) {
                    Iterator t = pms.iterator();
                    while(t.hasNext()) {
                        GenericValue pm = (GenericValue) t.next();
                        String pmId = pm.getString("paymentMethodId");
                        pm.writeXmlText(writer,"");numberWritten++;
                        GenericValue cc = delegator.findByPrimaryKey("EftAccount",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) {
                            cc.writeXmlText(writer,"");numberWritten++;
                        }
                        cc = delegator.findByPrimaryKey("CreditCard",UtilMisc.toMap("paymentMethodId", pmId));
                        if (cc != null) {
                            cc.writeXmlText(writer,"");numberWritten++;
                        }
                    }
                }
                
            } catch (GenericEntityException e) { Debug.logError(e, "Problems reading extra parties for A-NeT", module);}
        }


        // tax tables
        explode("TaxAuthority","taxAuthPartyId",writer);
        explode("PartyTaxAuthInfo","partyId",writer);
        
        // security
        explode("SecurityGroup", "groupId", "SecurityGroupPermission", writer);
        explode("Party", "partyId", false, "ToPartyRelationship", true, writer);

        // store
        GenericValue productStore = null;
        try{ productStore =delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId",productStoreId));
		} catch (GenericEntityException e) { Debug.logError(e, "Problems reading ProductStore", module);}
        if (productStore == null)	{
            request.setAttribute("_ERROR_MESSAGE_", "ProductStore " + productStoreId + " not found");
        	return "error";		//productstore not found
        }
        //get the productstore, related website records id
        // get the payment settings
        explode("ProductStore", "productStoreId", "ProductStorePaymentSetting",writer);
        explode("WebSite", "webSiteId", writer);
        // get the email settings
        explode("ProductStore", "productStoreId", false, "ProductStoreEmailSetting",true,writer);
        // get the ProductStoreShipmentMeth
        // explode(productStore, "ProductStoreShipmentMeth",writer);  // no direct link...
        explode("ProductStoreShipmentMeth","productStoreId", writer); 
        // productStore, catalog and categories
        explode("ProdCatalog","prodCatalogId", writer); 
        explode("ProductStore", "productStoreId", false,"ProductStoreCatalog",true,writer); //productstore and catalog
        explode("ProductCategory","productCategoryId", "ProdCatalogCategory", writer); // category and relation to catalog
        explode("ProductCategoryRollup","productCategoryId",writer);  // category relations
        
        // get all products and prices en link to category and acciciations
        explode("Product","productId", "ProductPrice", writer);
        explode("Product","productId", false, "ProductCategoryMember", true, writer);
        explode("Product","productId", false, "AssocProductAssoc", true, writer);

        // product features
        explode("ProductFeatureCategory", "productFeatureCategoryId", "ProductFeature", "ProductFeatureAppl", writer);
        
        // get all fixed assets and link to product
        explode("FixedAsset","fixedAssetId", "FixedAssetProduct", writer);

        // get all content, resource and electronic text
        explode("Content","contentId",false, "DataResource", true, "ElectronicText", true,  writer);
        explode("Content","contentId","FromContentAssoc", writer);

        // invoices/payments/applications
        explode("PartyAcctgPreference", "partyId", writer);
        explode("CustomTimePeriod", "organizationPartyId", writer);
        explode("Invoice", "invoiceId", "InvoiceItem", writer);
        explode("Invoice", "invoiceId",false, "InvoiceStatus", true, writer);
        explode("Invoice", "invoiceId",false, "InvoiceRole", true, writer);
        explode("Payment", "paymentId", writer);
        explode("Payment", "paymentId", false, "PaymentApplication",true, writer);

        explode("GlAccountOrganization", "organizationPartyId", writer);
        explode("PaymentMethodTypeGlAccount", "organizationPartyId", writer);
        explode("GlAccountTypeDefault", "organizationPartyId", writer);
        explode("PaymentGlAccountTypeMap", "organizationPartyId", writer);
        explode("InvoiceItemTypeGlAccount", "organizationPartyId", writer);
        
        // orders (to be completed)
        explode("OrderHeader","orderId", "OrderItem", "orderItemPriceInfo", writer);
        explode("OrderItemBilling","orderId",  writer);
        
        // sequence value items: SequenceValueItems
        explode("SequenceValueItem","seqName",  writer);
        
        writer.println("</entity-engine-xml>");
        writer.close();
        msgs.append("Unload ended normally, statistics:\n");
        msgs.append("--Entities written to output file: " + numberWritten + "\n");
        msgs.append("<a href=/"  + downloadLoc+ ">--Download your generated file here</a>");
        if (msgs.length() > 0)
            request.setAttribute("_EVENT_MESSAGE_", msgs.toString());
        numberWritten = 0;
        return "success";
    }

    // boolean can not be defined in a minilanguage file
    public static Collection getContactMech(GenericValue party, String contactMechPurposeTypeId, String contactMechTypeId) {
    	return org.ofbiz.party.contact.ContactHelper.getContactMech(party, contactMechPurposeTypeId, contactMechTypeId, false); 
    }
}