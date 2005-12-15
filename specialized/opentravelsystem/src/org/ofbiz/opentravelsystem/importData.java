/*
 * $Id: mt940.java 0000 2005-05-11 09:08:15Z hansbak $
 *
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;

import org.apache.commons.fileupload.FileItem;

/**
 
 *
 * Product import  program which will read files in the following format:
 * 
 * ProductId, description, largeimage filename, smallimagefilename, category,  promo, price
 * 
 * it will also create the categories. The catalog should however already exist and should be the same name as the organizationPartyId.
 *
 * @author     <a href="mailto:support@opentravelsystem.org">Hans Bakker</a> 
 * @version    $Rev: 0000 $
 */
public class importData {
	
	static boolean debug = true;	// to show error messages or not.....
	
	static String module = importData.class.getName();
	// these variables are used by the getFile and getLine routines independant of the format
	static GenericDelegator delegator = null;
	static LocalDispatcher dispatcher = null;
	static String localFile = null;
	static int lineNumber;
	static int start = 0;
	static int end = 0;
	static String fileLine = null;
	static java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	
	public static String importProduct(HttpServletRequest request, HttpServletResponse response) {
		delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale loc = (Locale)request.getSession().getServletContext().getAttribute("locale");
		if (loc == null) loc = Locale.getDefault();
		String organizationPartyId = userLogin.getString("partyId");
		
		if (getFile(request).equals("error") || localFile == null || localFile.length() == 0) { // get the content of the uploaded file...
			request.setAttribute("_ERROR_MESSAGE_", "imPortProduct: Uploaded file not found or an empty file......");
			return "error";
		}
		if (debug) Debug.logInfo("File loaded...", module);
		
		List toBeStored = new LinkedList(); // file to keep all the values which need updating
		
		GenericValue catalog = null;
		GenericValue partyAcctgPreference = null;
		try {
			partyAcctgPreference = delegator.findByPrimaryKey("PartyAcctgPreference", UtilMisc.toMap("partyId",organizationPartyId));
			catalog = delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId",organizationPartyId));
		} catch(GenericEntityException e2) {
			request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
			return "error";
		}
		
		if (catalog == null) {
			request.setAttribute("_ERROR_MESSAGE_", "Catalog not found with name: " + organizationPartyId);
			return "error";
		}
		
		if (partyAcctgPreference == null) {
			request.setAttribute("_ERROR_MESSAGE_", "Party logged on is not a organizationParty: " + organizationPartyId);
			return "error";
		}
		// create basis categories
		// promotion
		toBeStored.add(delegator.makeValue("ProductCategory",UtilMisc.toMap(
				"productCategoryId",organizationPartyId + "_PROMO",
				"productCategoryTypeId","CATALOG_CATEGORY",
				"primaryParentCategoryId",organizationPartyId,
				"description","Promotion Items")));
		
		// connect to catalog
		toBeStored.add(delegator.makeValue("ProdCatalogCategory",UtilMisc.toMap(
				"prodCatalogId",organizationPartyId,
				"productCategoryId",organizationPartyId + "_PROMO",
				"prodCatalogCategoryTypeId","PCCT_PROMOTIONS",
				"fromDate",nowTimestamp)));
		
		// browseroot
		toBeStored.add(delegator.makeValue("ProductCategory",UtilMisc.toMap(
				"primaryParentCategoryId",organizationPartyId,
				"productCategoryId",organizationPartyId + "_ROOT",
				"productCategoryTypeId","CATALOG_CATEGORY",
				"description","contain the categories to browse"	)));
		
		// connect to catalog
		toBeStored.add(delegator.makeValue("ProdCatalogCategory",UtilMisc.toMap(
				"prodCatalogId",organizationPartyId,
				"productCategoryId",organizationPartyId + "_ROOT",
				"prodCatalogCategoryTypeId","PCCT_BROWSE_ROOT",
				"fromDate",nowTimestamp)));

		List categories = new ArrayList(); // remember added categories, do not add links over and over
		while ((fileLine = getLine()) != null && lineNumber < 100) {
			if (debug) Debug.logInfo("Line read: " +fileLine, module);
			
			// prepare structures for updating
			GenericValue product =delegator.makeValue("Product",UtilMisc.toMap(
					"productTypeId","FINISHED_GOOD",		
					"includeInPromotions","Y",						// allow promotions
					"primaryProductCategoryId", organizationPartyId));
			GenericValue productPrice =delegator.makeValue("ProductPrice",UtilMisc.toMap(
					"productPricePurposeId","PURCHASE",
					"productPriceTypeId","DEFAULT_PRICE",
					"currencyUomId",partyAcctgPreference.getString("baseCurrencyUomId"),
					"productStoreGroupId","_NA_",
					"fromDate",nowTimestamp));
			GenericValue productCategory =delegator.makeValue("ProductCategory",UtilMisc.toMap(
					"primaryParentCategoryId",organizationPartyId,
					"productCategoryTypeId","CATALOG_CATEGORY")); // category itself
			GenericValue productCategoryMember = delegator.makeValue("ProductCategoryMember",UtilMisc.toMap( // connect product to category
					"fromDate",nowTimestamp));
			GenericValue productCategoryMemberPromo = delegator.makeValue("ProductCategoryMember",UtilMisc.toMap( // connect product to promo category
					"fromDate",nowTimestamp,
					"productCategoryId",organizationPartyId + "_PROMO"));
			GenericValue prodCatalogCategory =delegator.makeValue("ProdCatalogCategory",UtilMisc.toMap(  // category to productCatalog
					"prodCatalogId",organizationPartyId,
					"prodCatalogCategoryTypeId","PCCT_QUICK_ADD",  // dummy entry
					"fromDate",nowTimestamp));
			GenericValue productCategoryRollup =delegator.makeValue("ProductCategoryRollup",UtilMisc.toMap( // category to parent for browsing
					"fromDate",nowTimestamp,
					"parentProductCategoryId",organizationPartyId + "_ROOT"));
			
			int infoItemNr = 0;
			boolean promo = false;
			String prefix = partyAcctgPreference.getString("invoiceIdPrefix");
			String infoItem = null;
			// parse line from file
			while ((infoItem=getToken()) != null && infoItemNr != 7 ) {
				if (debug) Debug.logInfo("Token read: " + infoItem, module);
				switch(++infoItemNr) {
				case 1: // product number
					product.put("productId", prefix.concat("-").concat(infoItem));
					productPrice.put("productId", prefix.concat("-").concat(infoItem));
					productCategoryMemberPromo.put("productId",prefix.concat("-").concat(infoItem)); // connect to promocategory if required
					productCategoryMember.put("productId",prefix.concat("-").concat(infoItem)); // connect to browse category
					break;
				case 2: //description
					product.put("productName", infoItem);
					product.put("description", infoItem);
					break;
				case 3: // large image
					product.put("largeImageUrl", "/".concat(organizationPartyId).concat("/html/").concat(infoItem));
					break;
				case 4: //small image
					product.put("smallImageUrl","/".concat(organizationPartyId).concat("/html/").concat(infoItem));
					break;
				case 5: //price
						productPrice.put("price", new Double(Double.parseDouble(infoItem)));
					break;
				case 6: //category
					productCategory.put("productCategoryId", prefix.concat("-").concat(infoItem));
					productCategory.put("description",infoItem);
					prodCatalogCategory.put("productCategoryId", prefix.concat("-").concat(infoItem));
					productCategoryRollup.put("productCategoryId", prefix.concat("-").concat(infoItem));
					productCategoryMember.put("productCategoryId", prefix.concat("-").concat(infoItem));
					break;
				case 7: //promo (y/n)
					if (infoItem.equals("N") || infoItem.equals("n")|| infoItem.equals(" ")) {
						promo = false;
					}
					else  {
						promo=true;
					}
					break;
				}
			}


			// check is some already exists, do not create link again
			GenericValue prExist = null;
			GenericValue catExist = null;
			try {
				prExist = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId",product.getString("productId")));
				catExist = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",productCategory.getString("productCategoryId")));
			} catch(GenericEntityException e2) {
				request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
				return "error";
			}
			
			// update/create
			toBeStored.add(productCategory); 				// category
			toBeStored.add(product); 							// product
			toBeStored.add(productPrice); 						// product price
			
			// create links only if items did not exist
			if(prExist == null) {
				toBeStored.add(productCategoryMember); 	// product link to category
			}
			if (catExist == null && categories.indexOf(productCategory.getString("productCategoryId")) == -1) {
				categories.add(productCategory.getString("productCategoryId"));  // add to the list so not recreated
				toBeStored.add(productCategoryRollup); 		// category to browseroot category
			}
			if (promo) 
				toBeStored.add(productCategoryMemberPromo); 	// product link to promotion category
		}
		
		try {	// create entities for this record
            delegator.storeAll(toBeStored);
		} catch(GenericEntityException e2) {
			request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Import successfull: " + toBeStored.size() + " records imported/updated.");
		return (String) "success";
	}		
	
	
	/**
	 * get the next token from the line 
	 * @return the content of the token or null when end of line
	 */
	private static String getToken() {
		if(fileLine.length() == 0) return null;
		int start = 0;int end = 0;
		String token = null;
		if (fileLine.charAt(0) == '"') 	{
			start = 1;
			// find next quote
			end = fileLine.substring(start).indexOf("\",")+1;
			if (end > 0)	{ // found?
				token = fileLine.substring(start,end);
				fileLine = fileLine.substring(end + 2);
			}
			else { // no...
				end = fileLine.length() - 1;
				token = fileLine.substring(start,end);
				fileLine = "";
			}
		}
		else	{ // not quoted field find next comma...
			end = fileLine.indexOf(",");
			if (end > 0)	{ // found?
				token = fileLine.substring(start,end);
				fileLine = fileLine.substring(end + 1);
			}
			else	{ // no...
				end = fileLine.length();
				token = fileLine.substring(start,end);
				fileLine = "";
			}
		}
		if (debug) Debug.logInfo("===start/end/length:" + start + "/" + end + "/" + fileLine.length() + "  token:>>" + token + "<<  rest on line:>>" +fileLine + "<<", module);
		return token;
	}
	
	/**
	 * get a line from the input file 
	 * Assuming the file has been opened by using getFile.
	 * @return the content of the line or null when no lines left
	 */
	private static String getLine() {
		String fLine = null;
		// find end of line	
		while (end < localFile.length() && localFile.charAt(end) != 0x0d && localFile.charAt(end) != 0x0a) { 
//			if (end < localFile.length()-2) Debug.logInfo("char: [" +Integer.toHexString(localFile.charAt(end+1)) +  Integer.toHexString(localFile.charAt(end+2))+ "]", module);
			end++;
		}
		if (end >= localFile.length())
			return null;
		if (debug) Debug.logInfo("Start: " + start + "    End: " + end + "  Filelength: " + localFile.length(), module);
		fLine = localFile.substring(start, end);
		lineNumber++;
		if (localFile.charAt(end) == 0x0d && (localFile.charAt(end+1) == 0x0a || localFile.charAt(end+1) == 0x25 ))	{
			start = end + 2; // skip 0x0d and 0x0a / 0x25
			end++;  // skip the next 0x0a
		}
		else if (localFile.charAt(end) == 0x0a) { // for a windows formatted file only 0x0a?
			start = end + 1;
		}
		end++; //look for next line
		if (debug) Debug.logInfo("Line: " + lineNumber + "  -->" + fLine, module);
		return fLine;
	}
	
	/**
	 * return content of file in static variable localFile
	 * @return 'error' is not successfull....
	 */
	private static String getFile(HttpServletRequest request)	{
		try {
			start = end = lineNumber = 0;
			
			DiskFileUpload dfu = new DiskFileUpload();
			java.util.List lst = null;
			try {
				lst = dfu.parseRequest(request);
			} catch (FileUploadException e4) {
				request.setAttribute("_ERROR_MESSAGE_", e4.getMessage());
				Debug.logError("[UploadContentAndImage.uploadContentAndImage] " + e4.getMessage(), module);
				return "error";
			}
			if (lst.size() == 0) {
				String errMsg = "Data Exchange: no files uploaded";                                                        
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				Debug.logWarning("[ImportProduct] No files uploaded", module);
				return "error";
			}
			FileItem fi = null;
			for (int i = 0; i < lst.size(); i++) {
				fi = (FileItem) lst.get(i);
				String fieldName = fi.getFieldName();
				Debug.logInfo("DataExchange fieldName: " + fieldName, module);
				Debug.logInfo("DataExchange in isInMem: " + fi.isInMemory(), module);
				Debug.logInfo("DataExchange in getstring: " + fi.getString(), module);
				Debug.logInfo("DataExchange in getSize: " + fi.getSize(), module);
				Debug.logInfo("DataExchange in get: " + fi.get(), module);
				Debug.logInfo("DataExchange in getContentType: " + fi.getContentType(), module);
				Debug.logInfo("DataExchange in isFormField: " + fi.isFormField(), module);
				
				if (fi.getFieldName().equals("localFile")) {  // uploaded file found....
					localFile = fi.getString();
					return "OK";
				}
			}
			
		} catch( Exception e) {
			Debug.logError(e, "[DataExchange] " , module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			try {
				TransactionUtil.rollback();
			} catch(GenericTransactionException e2) {
				request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
				return "error";
			}
			return "error";
		}
		return "error";
	}
}




