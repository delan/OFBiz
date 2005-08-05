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
import java.net.*;
import org.w3c.dom.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.transaction.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


/**
 * Utilities to be used on the OpentravelSystem data
 */
public class otsUtils {

    public static final String module = otsUtils.class.getName();
    
    public static long numberWritten = 0;
    public static long numberUpdated = 0;
    public static long numberCreated = 0;

    public static GenericValue writeEntity(GenericDelegator delegator, PrintWriter writer, String entityName, Map keyValue)	{
        GenericValue entity = null;
        try {	entity = delegator.findByPrimaryKey(entityName, keyValue); } catch (GenericEntityException e) {	; }
        if (entity != null)	{
            entity.writeXmlText(writer, ""); 
            numberWritten++;
        }
        return entity;
    }
    
    public static List explode(GenericValue topEntityName, String child1EntityName, PrintWriter writer)	{
        return explode( topEntityName, child1EntityName,true, null,true,null,true,null,true,null,true, writer);
    }
    public static List explode(GenericValue topEntityName, String child1EntityName, String child2EntityName, PrintWriter writer)	{
        return explode( topEntityName, child1EntityName, true,child2EntityName,  true, null,true,null,true,null,true, writer);
    }
    public static List explode(GenericValue topEntityName, String child1EntityName, boolean xmlWrite1, String child2EntityName,boolean xmlWrite2, PrintWriter writer)	{
        return explode( topEntityName, child1EntityName, xmlWrite1,child2EntityName,  xmlWrite2, null,true, null,true, null, true,writer);
    }
    public static List explode(GenericValue topEntityName, String child1EntityName, String child2EntityName,String child3EntityName, PrintWriter writer)	{
        return explode( topEntityName, child1EntityName, true,child2EntityName,  true,child3EntityName, true, null,  true,null,  true, writer);
    }
    public static List explode(GenericValue topEntityName, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2, String child3EntityName, boolean xmlWrite3, PrintWriter writer)	{
        return explode( topEntityName, child1EntityName, xmlWrite1, child2EntityName, xmlWrite2,child3EntityName, xmlWrite3, null,  true,null,  true, writer);
    }
    public static List explode(GenericValue topEntityName, String child1EntityName, String child2EntityName,String child3EntityName,String child4EntityName, PrintWriter writer )	{
        return explode( topEntityName, child1EntityName, true,child2EntityName,  true,child3EntityName, true, child4EntityName, true, null, true, writer);
    }
    public static List explode(GenericValue topEntityName, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2, String child3EntityName, boolean xmlWrite3, String child4EntityName, boolean xmlWrite4, PrintWriter writer )	{
        return explode( topEntityName, child1EntityName, xmlWrite1, child2EntityName,  xmlWrite2, child3EntityName,  xmlWrite3,child4EntityName,  xmlWrite4, null, true, writer);
    }
    public static List explode(GenericValue topEntityName, String child1EntityName, String child2EntityName,String child3EntityName,String child4EntityName, String child5EntityName, PrintWriter writer )	{
        return explode( topEntityName, child1EntityName, true,child2EntityName,  true,child3EntityName, true, child4EntityName, true, child5EntityName, true, writer);
    }
   /**
    * This routine will export from the data base in xml format if the xmlWrite flag is true.
    * If false then no xml file will be generated but a list will be returned of the lowest level provided
    * Input is a path through the database of related entities.
    * @param topEntityName
    * @param child1EntityName
    * @param child2EntityName
    * @param child3EntityName
    * @param child4EntityName
    * @param child5EntityName
    * @param writer
    * @param xmlWrite
    * @return
    */
    private static List explode(GenericValue topEntityName, String child1EntityName, boolean xmlWrite1, String child2EntityName, boolean xmlWrite2,String child3EntityName, boolean xmlWrite3,String child4EntityName,  boolean xmlWrite4,String child5EntityName, boolean xmlWrite5, PrintWriter writer)	{
        List lastChildren = null;
        List children1 = null;
        try { children1 = topEntityName.getRelated(child1EntityName);
        } catch (GenericEntityException e) { Debug.logError(e, "Problems reading related entity: " + child1EntityName, module);}
        if (children1 != null && children1.size() > 0)	{
            Iterator psr1 = children1.iterator();
            while (psr1.hasNext())	{
                GenericValue child1 = (GenericValue) psr1.next();
                if (child2EntityName != null)	{
                    List children2 = null;
                    try { children2 = child1.getRelated(child2EntityName);
                    } catch (GenericEntityException e) { Debug.logError(e, "Problems reading related entity: " + child2EntityName, module);}
                    if (children2 != null && children2.size() > 0)	{
                        Iterator psr2 = children2.iterator();
                        while (psr2.hasNext())	{
                            GenericValue child2 = (GenericValue) psr2.next();
                            if (child3EntityName != null)	{
                                List children3 = null;
                                try { children3 = child2.getRelated(child3EntityName);
                                } catch (GenericEntityException e) { Debug.logError(e, "Problems reading related entity: " + child3EntityName, module);}
                                if (children3 != null && children3.size() > 0)	{
                                    Iterator psr3 = children3.iterator();
                                    while (psr3.hasNext())	{
                                        GenericValue child3 = (GenericValue) psr3.next();
                                        if (child4EntityName != null)	{
                                            List children4 = null;
                                            try { children4 = child3.getRelated(child4EntityName);
                                            } catch (GenericEntityException e) { Debug.logError(e, "Problems reading related entity: " + child4EntityName, module);}
                                            if (children4 != null && children4.size() > 0)	{
                                                Iterator psr4 = children4.iterator();
                                                while (psr4.hasNext())	{
                                                    GenericValue child4 = (GenericValue) psr4.next();
                                                    if (child5EntityName != null)	{
                                                        List children5 = null;
                                                        try { children5 = child4.getRelated(child5EntityName);
                                                        } catch (GenericEntityException e) { Debug.logError(e, "Problems reading related entity: " + child5EntityName, module);}
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
        return lastChildren;
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
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String productStoreId = (String) request.getSession().getAttribute("productStoreId");
        StringBuffer msgs = new StringBuffer();

        // single file
        if(productStoreId == null || productStoreId.length() == 0) {
            request.setAttribute("_ERROR_MESSAGE_", "No productStore defined...");
            return "error";
        }

        String fileName = new String("specialized/opentravelsystem/webapp/" + productStoreId + "/data/" + productStoreId + ".xml");
        String downloadLoc = new String(productStoreId + "/data/" + productStoreId + ".xml");
        String output = null;

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

        // store
        GenericValue productStore = writeEntity(delegator, writer, "ProductStore", UtilMisc.toMap("productStoreId",productStoreId));
        if (productStore == null)	{
            request.setAttribute("_ERROR_MESSAGE_", "ProductStore " + productStoreId + " not found");
        	return "error";		//productstore not found
        }

        
        // get the orders placed


        // retrieve the access users. When it is a group, retrieve its users
        explode(productStore, "ProductStoreRole",false, "Party",true, writer);				// party
        explode(productStore, "ProductStoreRole",false, "Party",false, "PartyRole",true, writer);	// partyrole
        explode(productStore, "ProductStoreRole",true, "Party",false, "UserLogin",true, writer); // productStoreRole and Userlogin
        explode(productStore, "ProductStoreRole",false, "Party",false, "UserLogin",false, "UserLoginSecurityGroup", true, writer); // login/securitygroup
        explode(productStore, "ProductStoreRole",false, "Party",false, "PartyGroup",true, writer);  // partygroup
        explode(productStore, "ProductStoreRole",false, "Party",false, "Person",true, writer);	// person
    
        //get the related website records id
        explode(productStore, "WebSite", writer);

        // get keyworde override
        explode(productStore, "ProductStoreKeywordOvrd", writer);
        // get the payment settings
        explode(productStore, "ProductStorePaymentSetting",writer);

        // get the email settings
        explode(productStore, "ProductStoreEmailSetting",writer);

        // get the ProductStoreShipmentMeth
        // explode(productStore, "ProductStoreShipmentMeth",writer);  // no direct link...

        // see if the primary category exists, if not create
        GenericValue productCategory = null;
        try {	productCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",productStoreId)); } catch (GenericEntityException e) {	; }
        if (productCategory != null)	{
            productCategory.writeXmlText(writer, ""); numberWritten++;
            }
        else	{ // not exist so create
            msgs.append("Primary Product category: " + productStoreId + " not found, creating.....\n");
            GenericValue newProductCategory = delegator.makeValue("ProductCategory",UtilMisc.toMap("productCategoryId",productStoreId,"description","Primary category for the ProductStore" + productStoreId,"productCategoryTypeId","CATALOG_CATEGORY"));
            try {	delegator.create(newProductCategory);
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_","Tried to add primary category: " + newProductCategory.getString("productCategoryId") + " but it failed: " +  e.getMessage() + "\n" );
                return "error";
            }
            newProductCategory.writeXmlText(writer, ""); numberWritten++;numberCreated++;
        }

        // see if the fixed Asset group exists, if not create
        {
            GenericValue fixedAsset = null;
            try {	fixedAsset = delegator.findByPrimaryKey("FixedAsset", UtilMisc.toMap("fixedAssetId",productStoreId)); } catch (GenericEntityException e) {	; }
            if (fixedAsset != null)	{
                fixedAsset.writeXmlText(writer, ""); numberWritten++;
            }
            else	{ // not exist so create
                msgs.append("FixedAsset group: " + productStoreId + " not found, creating.....\n");
                GenericValue newFixedAsset = delegator.makeValue("FixedAsset",UtilMisc.toMap("fixedAssetId",productStoreId,"fixedAssetName","Primary fixedAsset group for the ProductStore: " + productStoreId,"fixedAssetTypeId","OTHER_FIXED_ASSET"));
                try {	delegator.create(newFixedAsset);
                }	catch (GenericEntityException e) {
                    request.setAttribute("_ERROR_MESSAGE_","Tried to add fixed Asset group: \"" + newFixedAsset.getString("fixedAssetId") + "\" but it failed: " +  e.getMessage() + "\n" );
                    return "error";
                }
                newFixedAsset.writeXmlText(writer, ""); numberWritten++;numberCreated++;
            }
        }

        //get products with primary category set to the same name as the productStore
        List products = null;
        List expressions = UtilMisc.toList(new EntityExpr("primaryProductCategoryId", EntityOperator.EQUALS, productStoreId));
        try {	products = delegator.findByAnd("Product", expressions); } catch (GenericEntityException e) {	; }
        if (products == null || products.size() == 0)	{
            msgs.append("No products found for primary category: " + productStoreId + "\n");
        }
        else	{
            // get product information
            Iterator p = products.iterator();
            while (p.hasNext()){
                GenericValue product = (GenericValue) p.next();
                // save product info
                product.writeXmlText(writer, ""); numberWritten++;
                // get the simple tax
                explode(product, "SimpleSalesTaxLookup",writer);
                // get relation to fixedAsset and check if fixed asset is in the Productstore fixed asset group
                List fixedAssetProducts = null;
                try {	fixedAssetProducts = product.getRelated("FixedAssetProduct");
                } catch (GenericEntityException e) { Debug.logError(e, "Problems reading FixedAssetProduct", module); }
                if (fixedAssetProducts != null && fixedAssetProducts.size() > 0)	{
                    Iterator fap = fixedAssetProducts.iterator();
                    while (fap.hasNext())	{
                        GenericValue fixedAssetProduct = (GenericValue) fap.next();
                        // get the fixed asset itself
                        GenericValue fixedAsset = null;
                        try {	fixedAsset = fixedAssetProduct.getRelatedOne("FixedAsset");
                        } catch (GenericEntityException e) { Debug.logError(e, "Problems reading FixedAsset", module); }
                        if (fixedAsset != null && (fixedAsset.get("parentFixedAssetId") == null || fixedAsset.getString("parentFixedAssetId").compareTo(productStoreId) != 0))	{
                            // update with parentId
                            fixedAsset.set("parentFixedAssetId", productStoreId);
                            try { delegator.store(fixedAsset);
                            } catch (GenericEntityException e) { Debug.logError(e, "Error updating FixedAsset", module); }
                            finally	{
                                numberUpdated++;
                            }
                        }
                    }
                }
                // get the product prices
                explode(product, "ProductPrice",writer);
                // and seles tax
                explode(product, "SimpleSalesTaxLookup",writer);
                // try to get product content
                explode(product, "ProductContent", "Content", "DataResource", writer);
                // product reviews
                // explode(product, "ProductReview", "StatusItem", writer); // better not because it needs the partyId of the writer
            }
        }

        //get fixed Assets within the fixedAsset group set to the same name as the productStore
        List fixedAssets = null;
        expressions = UtilMisc.toList(new EntityExpr("parentFixedAssetId", EntityOperator.EQUALS, productStoreId));
        try {	fixedAssets = delegator.findByAnd("FixedAsset", expressions); } catch (GenericEntityException e) {	; }
        if (fixedAssets == null || fixedAssets.size() == 0)	{
            msgs.append("No fixed Assets found for fixed Asset group: " + productStoreId + "\n");
        }
        else {
            Iterator f = fixedAssets.iterator();
            while (f.hasNext()){
                GenericValue fixedAsset = (GenericValue) f.next();
                // check if techdataCalendar needs to be created.
/*                if (fixedAsset.get("calendarId") != null)	{
                     explode(fixedAsset,"TechDataCalendar", writer);  // tech data first
                     explode(fixedAsset,"TechDataCalendar", false, "TechDataCalendarExcDay",true, writer);
                }
*/             // fixed asset itself
                fixedAsset.writeXmlText(writer, ""); numberWritten++;

                // get relation to product
                List fixedAssetProducts = null;
                try {	fixedAssetProducts = fixedAsset.getRelated("FixedAssetProduct");
                } catch (GenericEntityException e) { ; }
                Iterator fap = fixedAssetProducts.iterator();
                while (fap.hasNext())	{
                    GenericValue fixedAssetProduct = (GenericValue) fap.next();
                    // write the fixed asset to product relation
                    fixedAssetProduct.writeXmlText(writer, "");numberWritten++;
                }
            }
        }

        //get categories with the primary category is set to name of the product store
        List productCategories = null;
        expressions = UtilMisc.toList(new EntityExpr("primaryParentCategoryId", EntityOperator.EQUALS, productStoreId));
        try {	productCategories = delegator.findByAnd("ProductCategory", expressions); } catch (GenericEntityException e) {	; }
        if (productCategories == null || productCategories.size() == 0)	{
            msgs.append("No categories found with primary parent category is: " + productStoreId + "\n");
        }
        else {
            Iterator pc = productCategories.iterator();
            while (pc.hasNext()){
                productCategory = (GenericValue) pc.next();
                productCategory.writeXmlText(writer, ""); numberWritten++;
            }
        }

        // get related catalogs for Hotel , should be "one" only
        List productStoreCatalogs  = null;
        try {	productStoreCatalogs = productStore.getRelated("ProductStoreCatalog");
        } catch (GenericEntityException e) { ; }
        if (productStoreCatalogs == null)
            msgs.append("No Catalogs not found!\n");
        else if (productStoreCatalogs.size() > 1)
            msgs.append("More than 1 Catalog!\n ");
        else	{
            Iterator pc = productStoreCatalogs.iterator();
            while (pc.hasNext())	{
                GenericValue productStoreCatalog = (GenericValue) pc.next();
                // get the catalog info
                GenericValue prodCatalog = null;
                try {	prodCatalog = (GenericValue) productStoreCatalog.getRelatedOne("ProdCatalog");
                } catch (GenericEntityException e) { ; }
                if (prodCatalog == null)	{
                    msgs.append("Cannot find catalog: " + productStoreCatalog.getString("catalogId") + "\n");	}
                else	{ // write the catalog info
                    prodCatalog.writeXmlText(writer, "");numberWritten++;
                    // write store to catalog relation (after catalog otherwise integrety problem)
                    productStoreCatalog.writeXmlText(writer, ""); numberWritten++;

                    // get the categories for a catalog
                    List prodCatalogCategories = null;
                    try {	prodCatalogCategories = prodCatalog.getRelated("ProdCatalogCategory");
                    } catch (GenericEntityException e) { ; }
                    if (prodCatalogCategories == null || prodCatalogCategories.size() == 0)	{
                        msgs.append("No Categories found for CatalogId: " + prodCatalog.getString("prodCatalogId") + "\n");}
                    else	{
                        Iterator pcc = prodCatalogCategories.iterator();
                        while (pcc.hasNext())	{
                            GenericValue prodCatalogCategory = (GenericValue) pcc.next();
                            // get the category info
                            productCategory = null;
                            try {	productCategory = (GenericValue) prodCatalogCategory.getRelatedOne("ProductCategory");
                            } catch (GenericEntityException e) { ; }
                            if (productCategory == null)	{
                                msgs.append("Cannot find Category: " + prodCatalogCategory.getString("categoryId") + "\n");
                            }
                            else	{
                                if (productCategory.get("primaryParentCategoryId") == null || productCategory.getString("primaryParentCategoryId").compareTo(productStoreId) != 0) {
                                    productCategory.set("primaryParentCategoryId",productStoreId);
                                    try {	delegator.store(productCategory);
                                    } catch (GenericEntityException e) {
                                        msgs.append("Tried to update primary category on Category: " + productCategory.getString("productCategoryId") + " but it failed: " + e.getMessage() + "\n");
                                    }
                                    finally {
                                        numberUpdated++;
                                        productCategory.writeXmlText(writer, ""); numberWritten++;
                                    }
                                }
                                // write the catalog to category relation (after category info)
                                prodCatalogCategory.writeXmlText(writer, "");numberWritten++;

                                // get the category to product relations for a category
                                List productCategoryMembers = null;
                                try {	productCategoryMembers = productCategory.getRelated("ProductCategoryMember");
                                } catch (GenericEntityException e) { ; }
                                if (productCategoryMembers != null && productCategoryMembers.size() > 0)	{
                                    Iterator pcc1 = productCategoryMembers.iterator();
                                    while (pcc1.hasNext())	{
                                        GenericValue productCategoryMember = (GenericValue) pcc1.next();
                                        // write the category to product relation
                                        productCategoryMember.writeXmlText(writer, "");numberWritten++;
                                        // get the product info to check if primary category is set....
                                        GenericValue product = null;
                                        try {	product = (GenericValue) productCategoryMember.getRelatedOne("Product");
                                        } catch (GenericEntityException e) { ; }
                                        if (product == null)
                                            msgs.append("Cannot find Product: " + productCategoryMember.getString("productId") + "\n");
                                        else	{
                                            // check if the primary category is set to the productStoreId
                                            if (product.get("primaryProductCategoryId") == null || product.getString("primaryProductCategoryId").compareTo(productStoreId) != 0)	{
                                                product.set("primaryProductCategoryId",productStoreId);
                                                try {	delegator.store(product);
                                                } catch (GenericEntityException e) {
                                                    msgs.append("Tried to update primary category on product: " + product.getString("productId") + " but it failed: " + e.getMessage() + "\n");
                                                }
                                                finally {
                                                    product.writeXmlText(writer,""); numberWritten++;
                                                    numberUpdated++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }




        writer.println("</entity-engine-xml>");
        writer.close();
        msgs.append("Unload ended normally, statistics:\n");
        msgs.append("--Entities written to output file: " + numberWritten + "\n");
        msgs.append("--Records created:  " + numberCreated + "\n");
        msgs.append("--Records Updated: " + numberUpdated + "\n");
        msgs.append("<a href=/"  + downloadLoc+ ">--Download your generated file here</a>");
        if (msgs.length() > 0)
            request.setAttribute("_EVENT_MESSAGE_", msgs.toString());
        numberWritten = 0;
        numberUpdated = 0;
        numberCreated = 0;
        return "success";
    }

}