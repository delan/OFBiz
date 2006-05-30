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
    public static void explode(String topEntityName, String topEntityId, String child1EntityName, String child2EntityName,String child3EntityName,String child4EntityName, String child5EntityName, PrintWriter writer )	{
        explode(topEntityName,  topEntityId, true,  child1EntityName, true,child2EntityName,  true,child3EntityName, true, child4EntityName, true, child5EntityName, true, writer);
    }
   /**
    * This routine will export from the data base in xml format if the xmlWrite flag is true.
    * If false then no xml file will be generated but a list will be returned of the lowest level provided
    * Input is a path through the database of related entities.
    * @param topEntityName
    * @param topEntityId
    * @param child1EntityName
    * @param child2EntityName
    * @param child3EntityName
    * @param child4EntityName
    * @param child5EntityName
    * @param writer
    * @param xmlWrite
    * @return
    */
    private static void explode(String topEntityName, String topEntityId, boolean xmlWrite0, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2,String child3EntityName, boolean xmlWrite3,String child4EntityName,  boolean xmlWrite4,String child5EntityName, boolean xmlWrite5, PrintWriter writer)	{
    	try {
    		Debug.logInfo("==processing topEntity:" + topEntityName,module);
    		List topEntityList = delegator.findByLike(topEntityName,UtilMisc.toMap(topEntityId,prefix.concat("%")),UtilMisc.toList(topEntityId));
    		if (topEntityList != null && topEntityList.size() > 0) {
    			Iterator t = topEntityList.iterator();
    			while(t.hasNext()) {
    				GenericValue topEntityListItem = (GenericValue) t.next();
    				if (xmlWrite0) { topEntityListItem.writeXmlText(writer,""); numberWritten++; }
    				if (child1EntityName != null)	{
    					List children1 = topEntityListItem.getRelated(child1EntityName);
    					if (children1 != null && children1.size() > 0)	{
    						Iterator psr1 = children1.iterator();
    						while (psr1.hasNext())	{
    							GenericValue child1 = (GenericValue) psr1.next();
    							if (child2EntityName != null)	{
    								List children2 =  child1.getRelated(child2EntityName);
    								if (children2 != null && children2.size() > 0)	{
    									Iterator psr2 = children2.iterator();
    									while (psr2.hasNext())	{
    										GenericValue child2 = (GenericValue) psr2.next();
    										if (child3EntityName != null)	{
    											List children3 = child2.getRelated(child3EntityName);
    											if (children3 != null && children3.size() > 0)	{
    												Iterator psr3 = children3.iterator();
    												while (psr3.hasNext())	{
    													GenericValue child3 = (GenericValue) psr3.next();
    													if (child4EntityName != null)	{
    														List children4 = child3.getRelated(child4EntityName);
    														if (children4 != null && children4.size() > 0)	{
    															Iterator psr4 = children4.iterator();
    															while (psr4.hasNext())	{
    																GenericValue child4 = (GenericValue) psr4.next();
    																if (child5EntityName != null)	{
    																	List children5 = child4.getRelated(child5EntityName);
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

        // security
        explode("SecurityGroup", "groupId", "SecurityGroupPermission", writer);
        
        // get all parties
        explode("Party", "partyId", "PartyGroup", writer);
        explode("Party", "partyId", false, "PartyRole",true, writer);
        explode("Party", "partyId", false, "UserLogin", true, "UserLoginSecurityGroup", true, writer);
        explode("Party", "partyId", false, "ToPartyRelationship", true, writer);

        // get all content, resource and electronic text
        explode("Content","contentId",false, "DataResource", true, "ElectronicText", true,  writer);
        explode("Content","contentId", writer);

        // invoices/payments/applications
        explode("PartyAcctgPreference", "partyId", writer);
        explode("CustomTimePeriod", "organizationPartyId", writer);
        explode("Invoice", "invoiceId", "InvoiceItem", writer);
        explode("Invoice", "invoiceId",false, "InvoiceStatus", true, writer);
        explode("Invoice", "invoiceId",false, "InvoiceRole", true, writer);
        explode("Payment", "paymentId", "PaymentApplication", writer);

        explode("GlAccountOrganization", "organizationPartyId", writer);
        explode("PaymentMethodTypeGlAccount", "organizationPartyId", writer);
        explode("GlAccountTypeDefault", "organizationPartyId", writer);
        explode("PaymentGlAccountTypeMap", "organizationPartyId", writer);
        explode("InvoiceItemTypeGlAccount", "organizationPartyId", writer);
        
        // orders (to be completed)
        explode("OrderHeader","orderId", "OrderItem", "orderItemPriceInfo", writer);
        explode("OrderItemBilling","orderId",  writer);
        
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