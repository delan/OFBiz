/*
 * $Id: otsUtils.java 3103 2004-08-20 21:45:49Z jaz $
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

	public static long explode(GenericValue topEntityName, String childEntityName, PrintWriter writer)	{
		return explode( topEntityName, childEntityName, null, writer);
	}
	
	public static long explode(GenericValue topEntityName, String child1EntityName, String child2EntityName, PrintWriter writer)	{
		List children1 = null;
		long numberWritten = 0;
		try { children1 = topEntityName.getRelated(child1EntityName); 
		} catch (GenericEntityException e) { Debug.logError(e, "Problems reading entity: " + child1EntityName, module);}
		if (children1 != null && children1.size() > 0)	{
			Iterator psr1 = children1.iterator();
			while (psr1.hasNext())	{
				GenericValue child1 = (GenericValue) psr1.next();
				child1.writeXmlText(writer,""); numberWritten++;
				if (child2EntityName != null)	{
					List children2 = null;
					try { children2 = child1.getRelated(child2EntityName); 
					} catch (GenericEntityException e) { Debug.logError(e, "Problems reading entity: " + child2EntityName, module);}
					if (children2 != null && children2.size() > 0)	{
						Iterator psr2 = children2.iterator();
						while (psr2.hasNext())	{
							GenericValue child2 = (GenericValue) psr2.next();
							child2.writeXmlText(writer,""); numberWritten++;
						}
					}
				}
			}
		}
		return numberWritten;
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
	 * It unloads the productstore information with the roles and Unloads the Party group who has the Admin role and all users under it.
	 * It unloads the related Catalog and Categories
	 * It unloads all products which have a primary category which is the same as the productStoreName and the relations to the category
	 * It unloads all fixed Assets whith a parent fixed asset with an Id which is the same as the productStoreName and the relations to the products.
	 * It 
	 */	
	public static String unLoad(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String productStoreId = request.getParameter("productStoreId");
		StringBuffer msgs = new StringBuffer();                       
		long numberWritten = 0;
		long numberUpdated = 0;
		long numberCreated = 0;
		
		String fileName = new String("exportFile.xml");
		String output = null;
		
		// single file
		if(fileName == null || fileName.length() == 0) {
			request.setAttribute("_ERROR_MESSAGE_", "No fileName supplied");
			return "error";
		}
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
		GenericValue productStore = null;
		try {	productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId",productStoreId)); } catch (GenericEntityException e) {	; }
		if (productStore != null)	{
			productStore.writeXmlText(writer, ""); numberWritten++; 
		}
		else	{
			request.setAttribute("_ERROR_MESSAGE_","Product Store: " + productStoreId + " not found");
			return "error";
		}
		
		// retrieve the access users. When it is a group, retrieve its users
		List productStoreRoles = null;
		try { productStoreRoles = productStore.getRelated("ProductStoreRole"); } catch (GenericEntityException e) {;}
		if (productStoreRoles != null && productStoreRoles.size() > 0)	{
			Iterator psr = productStoreRoles.iterator();
			while (psr.hasNext())	{
				GenericValue productStoreRole = (GenericValue) psr.next();
				// write store to party group relation
				productStoreRole.writeXmlText(writer,""); numberWritten++;
				GenericValue partyGroup = null;
				try { partyGroup = productStoreRole.getRelatedOne("PartyGroup");	} catch (GenericEntityException e) {;}
				if (partyGroup != null) { 
					partyGroup.writeXmlText(writer,""); numberWritten++;
					// get loginId's
					numberWritten += explode(partyGroup,"UserLogin", "UserLoginSecurityGroup", writer);
					// find related parties in the party file
					List parties = null;
					try { parties = partyGroup.getRelated("Party"); } catch (GenericEntityException e) {;}
					if (parties != null && parties.size() > 0)	{
						Iterator p = parties.iterator();
						while (p.hasNext())	{
							GenericValue party = (GenericValue) p.next();
							party.writeXmlText(writer,""); numberWritten++;
							numberWritten += explode(party,"UserLogin",writer);
						}
					}
				}
			}
		}
		//get the related website records id
		numberWritten += explode(productStore, "WebSite", writer);
		// get keyworde override
		numberWritten += explode(productStore, "ProductStoreKeywordOvrd", writer);
		// get the payment settings
		numberWritten += explode(productStore, "ProductStorePaymentSetting",writer);
		// get the email settings
		numberWritten += explode(productStore, "ProductStoreEmailSetting",writer);
		// get the ProductStoreShipmentMeth
		List ships = null;
		List exp = UtilMisc.toList(new EntityExpr("productStoreId", EntityOperator.EQUALS, productStoreId));
		try {	ships = delegator.findByAnd("ProductStoreShipmentMeth", exp); } catch (GenericEntityException e) {	; }
		if (ships != null && ships.size() > 0)	{
			Iterator s = ships.iterator();
			while (s.hasNext())	{
			GenericValue ship = (GenericValue) s.next(); 
			ship.writeXmlText(writer, ""); numberWritten++;
			}
		}
		
		// get the simple tax
		List taxes = null;
		List exp1 = UtilMisc.toList(new EntityExpr("productStoreId", EntityOperator.EQUALS, productStoreId));
		try {	taxes = delegator.findByAnd("SimpleSalesTaxLookup", exp1); } catch (GenericEntityException e) {	; }
		if (taxes != null && taxes.size() > 0)	{
			Iterator s = taxes.iterator();
			while (s.hasNext())	{
			GenericValue tax = (GenericValue) s.next(); 
			tax.writeXmlText(writer, ""); numberWritten++;
			}
		}
		
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
			Iterator p = products.iterator();
			while (p.hasNext()){
				GenericValue product = (GenericValue) p.next();
				product.writeXmlText(writer, ""); numberWritten++;
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
				numberWritten += explode(product, "ProductPrice",writer);
				// and seles tax
				numberWritten += explode(product, "SimpleSalesTaxLookup",writer);
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
		
		// get related catalogs for Hotel is should be "one" only
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
		msgs.append("Statistics: Entities written to output file " +  fileName + ": " + numberWritten + "\n");
		msgs.append("            Records created:  " + numberCreated + "\n");
		msgs.append("            Records Updated: " + numberUpdated + "\n");
		if (msgs.length() > 0)
			request.setAttribute("_EVENT_MESSAGE_", msgs.toString());
		return "success";
	}
	
}