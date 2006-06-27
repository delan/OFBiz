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

import java.util.*;

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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import org.apache.commons.fileupload.FileItem;

/**
 
 *
 * Product/Category import  program which will read files in the following format:
 * 
 * Category(added to browsecategory),subcat1,subcat2,subcat3,productId,description,c1,c2,c3,c4,extrainfo
 * ProductId, description, largeimage filename, smallimagefilename, category,  promo, price
 * 
 * it will also create the categories. The catalog should however already exist and should be the same name as the organizationPartyId.
 *
 *Please note that the override services from the opentravelsystem should be installed which will prefix product, productcategory with
 * the invoice prefix etc...
 *
 * @author     <a href="mailto:support@opentravelsystem.org">Hans Bakker</a> 
 * @version    $Rev$
 */
public class importDataBw {
	
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
		Map results = null;
		int categoryNbr = 0;
		int productNbr = 0;

		if (getFile(request).equals("error") || localFile == null || localFile.length() == 0) { // get the content of the uploaded file...
			request.setAttribute("_ERROR_MESSAGE_", "imPortProduct: Uploaded file not found or an empty file......");
			return "error";
		}
		if (debug) Debug.logInfo("File loaded...", module);
		
		GenericValue catalog = null;
		GenericValue partyAcctgPreference = null;
		try {
			partyAcctgPreference = delegator.findByPrimaryKey("PartyAcctgPreference", UtilMisc.toMap("partyId",organizationPartyId));
			catalog = delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId",organizationPartyId));
		} catch(GenericEntityException e2) {
			request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
			return "error";
		}
        String prefix = partyAcctgPreference.getString("invoiceIdPrefix");
		
		if (catalog == null) {
			request.setAttribute("_ERROR_MESSAGE_", "Catalog not found with name: " + organizationPartyId);
			return "error";
		}
		
		if (partyAcctgPreference == null) {
			request.setAttribute("_ERROR_MESSAGE_", "Party logged on is not a organizationParty: " + organizationPartyId);
			return "error";
		}
		// create basis categories
        String browseCategoryId = null;
	    // browseroot
        try {
            // check if browse category exist
            List catList = delegator.findByAnd("ProdCatalogCategory",UtilMisc.toMap("prodCatalogId",prefix,"prodCatalogCategoryTypeId","PCCT_BROWSE_ROOT"));
            GenericValue prodCatalogCategory = null;
            if (catList != null && catList.size() > 0) {
                prodCatalogCategory = (GenericValue) catList.get(0);
                browseCategoryId = prodCatalogCategory.getString("productCategoryId");
            }
            else {
                // create browse category
                results = dispatcher.runSync("createProductCategory",UtilMisc.toMap(
                        "userLogin",userLogin,
                        "productCategoryTypeId","CATALOG_CATEGORY",
                        "description","contain the categories to browse"	));
                browseCategoryId = (String) results.get("productCategoryId");
                Debug.logInfo("======category created : " + browseCategoryId,module);
                categoryNbr++;
                
                // connect to catalog
                results = dispatcher.runSync("addProductCategoryToProdCatalog",UtilMisc.toMap(
                        "userLogin",userLogin,
                        "prodCatalogId",organizationPartyId,
                        "productCategoryId",browseCategoryId,
                        "prodCatalogCategoryTypeId","PCCT_BROWSE_ROOT",
                        "fromDate",nowTimestamp));
            }
        } 
        catch (GenericServiceException e1) {
            request.setAttribute("_ERROR_MESSAGE_", "Error creating/linking base Categories");
            return "error";
        }
        catch (GenericEntityException e1) {
            request.setAttribute("_ERROR_MESSAGE_", "Error creating/linking base Categories");
            return "error";
        }

		while ((fileLine = getLine()) != null) { // && lineNumber < 2) {
            String sub1CategoryId = null;
            String sub2CategoryId = null;
            String sub3CategoryId = null;
            String productId = null;
			if (debug) Debug.logInfo("Line read: " +fileLine, module);
			
			// prepare structures for updating
			Map product = UtilMisc.toMap(
					"userLogin",userLogin,
					"productTypeId","FINISHED_GOOD",		
					"includeInPromotions","Y");						// allow promotions
			Map productPrice =UtilMisc.toMap(
					"userLogin",userLogin,
					"productPricePurposeId","PURCHASE",
					"productPriceTypeId","DEFAULT_PRICE",
					"currencyUomId",partyAcctgPreference.getString("baseCurrencyUomId"),
					"productStoreGroupId","_NA_",
					"fromDate",nowTimestamp);
            Map sub3Category = UtilMisc.toMap(
                    "userLogin",userLogin,
                    "productCategoryTypeId","CATALOG_CATEGORY"); // category itself
            Map sub3CategoryMember = UtilMisc.toMap( // connect product to category
                    "userLogin",userLogin,
                    "fromDate",nowTimestamp);
            Map sub2Category = UtilMisc.toMap(
                    "userLogin",userLogin,
                    "productCategoryTypeId","CATALOG_CATEGORY"); // category itself
            Map productCategoryRollup3 =UtilMisc.toMap( // category to parent for browsing
                    "userLogin",userLogin,
                    "fromDate",nowTimestamp,
                    "parentProductCategoryId",sub2CategoryId);
            Map sub1Category = UtilMisc.toMap(
                    "userLogin",userLogin,
                    "productCategoryTypeId","CATALOG_CATEGORY"); // category itself
            Map productCategoryRollup2 =UtilMisc.toMap( // category to parent for browsing
                    "userLogin",userLogin,
                    "fromDate",nowTimestamp,
                    "parentProductCategoryId",sub1CategoryId);
            Map productCategoryRollup1 =UtilMisc.toMap( // category to browsecategory
                    "userLogin",userLogin,
                    "fromDate",nowTimestamp,
                    "parentProductCategoryId",browseCategoryId);
			
			int infoItemNr = 0;
			String infoItem = null;
			// parse line from file
			while ((infoItem=getToken()) != null && infoItemNr != 12 ) {
				if (debug) Debug.logInfo("Token read: " + infoItem, module);
				switch(++infoItemNr) {
                case 1: // category with connection to browse category
                    sub1Category.put("productCategoryId", infoItem); // is prefixed by the service
                    sub1Category.put("description",infoItem);
                    sub1Category.put("categoryName",infoItem);
                    sub1Category.put("categoryImageUrl","/".concat(prefix).concat("/html/images/").concat(infoItem).concat(".jpg"));

                    sub1CategoryId = prefix.concat(infoItem); // used for roll up (not prefixed by service)
                    productCategoryRollup1.put("productCategoryId", prefix.concat(infoItem));
                    sub1Category.put("categoryImageUrl", "/".concat(organizationPartyId).concat("/html/images/categories/").concat(infoItem).concat(".jpg"));
                    break;
                case 2: // category with connection sub1 category
                    sub2Category.put("productCategoryId", infoItem);
                    sub2Category.put("description",infoItem);
                    sub2Category.put("categoryName",infoItem);
                    sub2CategoryId = prefix.concat(infoItem);
                    sub2Category.put("categoryImageUrl","/".concat(prefix).concat("/html/images/").concat(infoItem).concat(".jpg"));
                    productCategoryRollup2.put("productCategoryId", prefix.concat(infoItem));
                    productCategoryRollup2.put("parentProductCategoryId", sub1CategoryId);
                    sub2Category.put("categoryImageUrl", "/".concat(organizationPartyId).concat("/html/images/categories/").concat(infoItem).concat(".jpg"));                    
                    break;
                case 3: // category with connection to sub2 category
                    sub3Category.put("productCategoryId", infoItem);
                    sub3Category.put("description",infoItem);
                    sub3Category.put("categoryName",infoItem);
                    sub3CategoryId = prefix.concat(infoItem);
                    sub3Category.put("categoryImageUrl","/".concat(prefix).concat("/html/images/").concat(infoItem).concat(".jpg"));
                    productCategoryRollup3.put("productCategoryId", prefix.concat(infoItem));
                    productCategoryRollup3.put("parentProductCategoryId", sub2CategoryId);
                    sub3CategoryMember.put("productCategoryId", prefix.concat(infoItem));
                    sub3Category.put("categoryImageUrl", "/".concat(organizationPartyId).concat("/html/images/categories/category/").concat(infoItem).concat(".jpg"));
                    sub3Category.put("linkOneImageUrl", "/".concat(organizationPartyId).concat("/html/images/categories/categoryThumbs/").concat(infoItem).concat(".jpg"));
                    break;
                case 4: // product number
					product.put("productId", infoItem); // prefixed by service
                    productId = prefix.concat(infoItem);
                    product.put("internalName", infoItem);
                    product.put("productName", infoItem);
                    productPrice.put("productId", prefix.concat(infoItem));  // not prefixed by service
					sub3CategoryMember.put("productId",prefix.concat(infoItem)); // connect to category, not prefixed by service
                    product.put("largeImageUrl", "/".concat(organizationPartyId).concat("/html/images/products/").concat(infoItem).concat(".jpg"));
                    product.put("smallImageUrl","/".concat(organizationPartyId).concat("/html/images/products/").concat(infoItem).concat(".jpg"));
                    product.put("largeImageUrl", "/".concat(prefix).concat("/html/images/").concat(infoItem).concat(".jpg"));
                    product.put("smallImageUrl","/".concat(prefix).concat("/html/images/").concat(infoItem).concat(".jpg"));
					break;
				case 5: //description					
					product.put("description", infoItem);
					break;
                case 6: // coordinate 1
                    sub3CategoryMember.put("comments",infoItem);
                    break;
                case 7: // coordinate 2
                    String comments = (String) sub3CategoryMember.get("comments");
                    sub3CategoryMember.put("comments",comments.concat(",").concat(infoItem));
                    break;
                case 8: // coordinate 3sub3CategoryMember.put("productId",prefix.conca
                    comments = (String) sub3CategoryMember.get("comments");
                    sub3CategoryMember.put("comments",comments.concat(",").concat(infoItem));
                    break;
                case 9: // coordinate 4
                    comments = (String) sub3CategoryMember.get("comments");
                    sub3CategoryMember.put("comments",comments.concat(",").concat(infoItem));
                    break;
                case 10: //comments
                    if (infoItem.length() > 0)
                        product.put("comments", infoItem);
                    break;
                case 11: //price
                    if (infoItem.length() > 0) {
                        productPrice.put("price", new Double(Double.parseDouble(infoItem)));
                    	productPrice.put("productPriceTypeId", "DEFAULT_PRICE");
                    	productPrice.put("productPricePurposeId", "PURCHASE");
                    	productPrice.put("currencyUomId", "EUR");
                    }
                    else {
                        productPrice.put("price", null);
                    }
					break;
				}
			}

			if (product.get("productId") == null) { // skip line with empty product number
				continue;
			}

			// check is some already exists, do not create link again
			GenericValue prExist = null;
            GenericValue cat3Exist = null; //category which contains product members
            List cat3MemExists = new LinkedList();
            GenericValue cat3MemExist = null;
            GenericValue cat2Exist = null; 
            GenericValue cat1Exist = null; 
			try {
				prExist = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId",productId));
                if (sub3Category.get("productCategoryId") != null) { // filledin, in input file ?
                    cat3Exist = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",sub3CategoryId));
                    cat3MemExists = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId",sub3CategoryId,"productId",productId));
                    if(cat3MemExists != null && cat3MemExists.size() > 0) cat3MemExist = (GenericValue) cat3MemExists.get(0);
                }
                if (sub2Category.get("productCategoryId") != null)
                    cat2Exist = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",sub2CategoryId));
                if (sub1Category.get("productCategoryId") != null)
                    cat1Exist = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",sub1CategoryId));
			} catch(GenericEntityException e2) {
				request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
				return "error";
			}
			
			try { 
                // create categories only if did not exist
                if (cat1Exist == null && sub1Category.get("productCategoryId") != null) { 
                    Debug.logInfo("======creating category : " + sub1Category.get("productCategoryId"),module);
                    results = dispatcher.runSync("createProductCategory",sub1Category);                                 // add category
                    categoryNbr++;
                    results = dispatcher.runSync("addProductCategoryToCategory",productCategoryRollup1);            // link to higherlevel category
                }
                if (cat2Exist == null && sub2Category.get("productCategoryId") != null) { 
                    Debug.logInfo("======creating category : " + sub2Category.get("productCategoryId"),module);
                    results = dispatcher.runSync("createProductCategory",sub2Category);                                 // add category
                    categoryNbr++;
                    results = dispatcher.runSync("addProductCategoryToCategory",productCategoryRollup2);       // link to higherlevel category
                }
                if (cat3Exist == null && sub3Category.get("productCategoryId") != null) { 
                    Debug.logInfo("======creating category : " + sub3Category.get("productCategoryId"),module);
                    results = dispatcher.runSync("createProductCategory",sub3Category);                                 // add category
                    categoryNbr++;
                    results = dispatcher.runSync("addProductCategoryToCategory",productCategoryRollup3); // link to higherlevel category
                    
                    if(prExist != null) { // new category but existing product
                        results = dispatcher.runSync("addProductToCategory",sub3CategoryMember);
                    }
                    
                }
                // update comments when category already exists
                if (cat3MemExist != null)  {
                        String exComments = (String) cat3MemExist.get("comments"); 
                        String newComments = (String) sub3CategoryMember.get("comments"); 
                        sub3CategoryMember.put("comments", exComments.concat(",").concat(newComments)); 
                        results = dispatcher.runSync("updateProductToCategory",sub3CategoryMember);               
                }
                // create product
                if(prExist == null) {
					results = dispatcher.runSync("createProduct",product); 																// create product
					productNbr++;
					if (productPrice.get("price") != null)									// if price is supplied
						results = dispatcher.runSync("createProductPrice",productPrice);												// create product price
                    if (sub3Category.get("productCategoryId") != null)      // category is supplied 
                        results = dispatcher.runSync("addProductToCategory",sub3CategoryMember);                
				}
				
			} catch (GenericServiceException e1) {
				request.setAttribute("_ERROR_MESSAGE_", "Error creating records see log for details...");
				return "error";
			}
		}
		
		request.setAttribute("_EVENT_MESSAGE_", "Import successfull, " + categoryNbr +" categories and " + productNbr + " products added." );
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
			if (fileLine.charAt(0) == ',') { //empty
				token = "";
				fileLine = fileLine.substring(end + 1);
			}
			else if (end > 0)	{ // found?
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




