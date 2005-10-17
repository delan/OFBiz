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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.*;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

/**
 * mt940 - routines for importing and exporting(later) files in the SWIFT MT940 format.
 *  using the document at: 
 *  http://www.abnamro.nl/nl/images/Generiek/Attachments/Bestanden/Zakelijk/Formatenboek_OfficeNet_direct_11_ENG.pdf&e=747
 *
 * @author     <a href="mailto:support@opentravelsystem.org">Hans Bakker</a> 
 * @version    $Rev: 0000 $
 */
public class mt940 {

	static boolean debug = false;	// to show error messages or not.....
	
	static String module = mt940.class.getName();
	// these variables are used by the getFile and getLine routines independant of the format 
	static String localFile = null;
	static int start;
	static int end;
	static int lineNumber;
	static int seqNr;
	static String bankSeqNr;	// only available in the header of every detail record
	// these structures and variables are filled by the MT940 dependant routine getPayment
	static String accountNr; 				// the accountnumber the file was downloaded from
	static String routingNr;					// the routing number of the bankaccountnumber the file was downloaded from
	//  These genericValues are used to update the Database  and are filled by the getPayment routine
	static boolean debet; 					// if it was a debet (true) or credit (false) transaction
	static String defaultCurrency = null;
	static Map payment;		
	static GenericValue party;    				
	static GenericValue partyGroup;    				
	static GenericValue paymentMethod;
	static GenericValue eftAccount;
	// statistics
	static int partiesCreated;
	static int accountsCreated;
	static int paymentsCreated;
	static int paymentAlreadyUploaded;
	
	/* event to import bankstatement mt940 records */
	public static String importData(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin"); 
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale loc = (Locale)request.getSession().getServletContext().getAttribute("locale");
        if (loc == null) 
            loc = Locale.getDefault();
	       
		boolean debug = true;
		
		if (getFile(request).equals("error") || localFile == null || localFile.length() == 0) { // get the content of the uploaded file...
			request.setAttribute("_ERROR_MESSAGE_", "Uploaded file not found or an empty file......");
			return "error";
		}
		if (debug) Debug.logInfo("File loaded...", module);
		
		// find partyId for the bankccount number the file was generated from
		if (getFileHeader(request) == null) //  obtain data to retrieve partyId of the owner of the account
			return "error";
		String accountPartyId = null;
		if ((accountPartyId = getParty(delegator)) == null && (accountPartyId = createParty(delegator)) == null)	{ // retrieve party id...
			request.setAttribute("_ERROR_MESSAGE_", "No party/account could be found or being created with this Bankaccountnumber: " + accountNr + "(" + routingNr + ")");
			return "error";
		}
		if (debug) Debug.logInfo("Company Party found:" + accountPartyId, module);
		
		// find the related tax party to enable the correct payment type
		// find the taxGeoId of the company
		String taxAuthPartyId = null;
		try	{
			List partyTaxInfos = EntityUtil.filterByDate(delegator.findByAnd("PartyTaxInfo",UtilMisc.toMap("partyId",accountPartyId)));
			if (partyTaxInfos != null && partyTaxInfos.size() > 0)	{
				GenericValue partyTaxInfo = (GenericValue) partyTaxInfos.get(0);
					List taxAuthorities = delegator.findByAnd("TaxAuthority",UtilMisc.toMap("taxAuthGeoId",partyTaxInfo.getString("geoId")));
					if (taxAuthorities != null && taxAuthorities.size() > 0)	{
						taxAuthPartyId = ((GenericValue) taxAuthorities.get(0)).getString("taxAuthPartyId");
						if (debug)Debug.logInfo("taxAuthPartyId found:" + taxAuthPartyId.toString(),module);
					}
			}
		} catch (GenericEntityException e) {	
			request.setAttribute("_ERROR_MESSAGE_", "The company " + accountPartyId + " has no related taxauthority");
			return "error";
		}
		if (taxAuthPartyId == null)	{
			request.setAttribute("_ERROR_MESSAGE_", "The company " + accountPartyId + " has no related taxauthority");
			return "error";
		}
		
		if (debug) Debug.logInfo("Start processing payments...", module);
		while (getPayment(request) != null) {
			String otherParty = getParty(delegator);
			// check to see if the parties where found, when not create
			if (otherParty == null)	{ 
				if (createParty(delegator) == null) 	
					return "error"; // party creation error
				otherParty = party.getString("partyId");
			}
			if (debet  == false)	{
/*				if (otherParty.equals(taxAuthPartyId) || accountPartyId.equals(taxAuthPartyId))
					payment.put("paymentTypeId","TAX_PAYMENT");
				else */
					payment.put("paymentTypeId","CUSTOMER_PAYMENT");
				payment.put("partyIdFrom",otherParty);
				payment.put("partyIdTo", accountPartyId);    		
				}
			else	{ // credit
/*				if (otherParty.equals(taxAuthPartyId) || accountPartyId.equals(taxAuthPartyId))
					payment.put("paymentTypeId","TAX_PAYMENT");
				else */
					payment.put("paymentTypeId","VENDOR_PAYMENT");
				payment.put("partyIdTo",otherParty);
				payment.put("partyIdFrom", accountPartyId);    		
				}
			
			// set other fields in payment record
			payment.put("statusId","PMNT_NOT_PAID");  // it is always loaded with this status....needs tobe changed to send/received....	
			payment.put("paymentMethodId", paymentMethod.get("paymentMethodId"));
			payment.put("paymentMethodTypeId","EFT_ACCOUNT");
			// check if the payment was already uploaded.....
			if (debug) Debug.logInfo("Creating payment with reference number: " + payment.get("paymentRefNum"),module);
			if (checkPayment(delegator) == true)	{
				paymentAlreadyUploaded++;
				if (debug) Debug.logInfo("Payment already exists....",module);
			}
			else	{
				// finally create payment record.
				payment.put("userLogin",userLogin);
				payment.put("locale", loc);
                try {
                    dispatcher.runSync("createPayment", payment);
                } catch (GenericServiceException e1) {
                    Debug.logError(e1, "Error creating payment", module);
                    continue;
                }
                
				paymentsCreated++;
			}
		}
		
		request.setAttribute("_EVENT_MESSAGE_", "Upload ended...Payment records created: " + paymentsCreated + " Partygroups created: " + partiesCreated + " Accounts created: " + accountsCreated + " Payments already uploaded:" + paymentAlreadyUploaded);
		
		return "success";
	}
	/**
	 *  Check if the payment was already uploaded.......
	 * @param delegator
	 * @return "error" when not ok...
	 */
	private static boolean checkPayment(GenericDelegator delegator)	{
		List payments = null;
		try { payments = delegator.findByAnd("Payment", UtilMisc.toMap("paymentRefNum",payment.get("paymentRefNum"))); }
		catch (GenericEntityException e) {	Debug.logError("Find payment exception:" + e.getMessage(), module); }
		if (payments == null || payments.size() == 0)	
			return false;
		else
			return true;
	}
	/**
	 *  create party with payment method and eftAccount for an eftAccount that was not found.
	 *  create roles for customer or supplier...
	 * @param delegator
	 * @return "error" when not ok...
	 */
	private static String createParty(GenericDelegator delegator)	{
		if (accountNr.compareTo(eftAccount.getString("accountNumber")) != 0 || routingNr.compareTo(eftAccount.getString("routingNumber")) != 0)		{		// the company own account number should be added to the existing party " company"
			party.set("partyId",delegator.getNextSeqId("Party"));
			party.set("partyTypeId","PARTY_GROUP");
			try { party.create(); }
			catch (GenericEntityException e) {	Debug.logError("Create Party  exception:" + e.getMessage(), module); }
			partyGroup.set("partyId",party.getString("partyId"));
			try { partyGroup.create(); }
			catch (GenericEntityException e) {	Debug.logError("Create Person exception:" + e.getMessage(), module); }
			paymentMethod.set("partyId",party.getString("partyId"));
			partiesCreated++;
		}
		else	{  // party called "company" already exist create other (non party and person) records only
			paymentMethod = (GenericValue) delegator.makeValue("PaymentMethod",null);
			party = (GenericValue) delegator.makeValue("Party",null);
			paymentMethod.set("partyId","Company");
			eftAccount.set("nameOnAccount","Company");
			party.set("partyId","Company");
		}
		// create payment method
		paymentMethod.set("paymentMethodId",delegator.getNextSeqId("PaymentMethod"));
		paymentMethod.set("paymentMethodTypeId","EFT_ACCOUNT");
		paymentMethod.set("fromDate",  UtilDateTime.nowTimestamp());
		try { paymentMethod.create(); }
		catch (GenericEntityException e) {	Debug.logError("Create paymentMethod exception:" + e.getMessage(), module); }
		
		// create bank account
		eftAccount.set("paymentMethodId",paymentMethod.getString("paymentMethodId"));
		try { eftAccount.create(); }
		catch (GenericEntityException e) {	Debug.logError("Create EftAccount exception:" + e.getMessage(), module); }
		
		// create roles
		if (debet == false)	{	// customer
			GenericValue partyRole = (GenericValue) delegator.makeValue("PartyRole",
					UtilMisc.toMap("partyId",party.getString("partyId"),"roleTypeId","CUSTOMER"));
			try { partyRole.create(); }
			catch (GenericEntityException e) {	Debug.logError("Create PartyRole exception:" + e.getMessage(), module); }
			partyRole = (GenericValue) delegator.makeValue("PartyRole",
					UtilMisc.toMap("partyId",party.getString("partyId"),"roleTypeId","BILL_TO_CUSTOMER"));
			try { partyRole.create(); }
			catch (GenericEntityException e) {	Debug.logError("Create PartyRole exception:" + e.getMessage(), module); }
		}
		else	{	// vendor
			GenericValue partyRole = (GenericValue) delegator.makeValue("PartyRole",
					UtilMisc.toMap("partyId",party.getString("partyId"),"roleTypeId","VENDOR"));
			try { partyRole.create(); }
			catch (GenericEntityException e) {	Debug.logError("Create PartyRole exception:" + e.getMessage(), module); }
			partyRole = (GenericValue) delegator.makeValue("PartyRole",
					UtilMisc.toMap("partyId",party.getString("partyId"),"roleTypeId","BILL_FROM_VENDOR"));
			try { partyRole.create(); }
			catch (GenericEntityException e) {	Debug.logError("Create PartyRole exception:" + e.getMessage(), module); }
		}
		
		Debug.logInfo("Party Created:" + party.getString("partyId"), module);
		accountsCreated++;
		return party.getString("partyId");
	}
	/**
	 *    find parties by account number in the eftAccount table --> paymentMethod for the partyId
	 * @param delegator
	 * @param accountNumber in the eftAccount record
	 * @return partyId if found, when not null
	 */
	private static String getParty(GenericDelegator delegator)	{
		if (debug) Debug.logInfo("eftAccount searching: accountNumber:" + eftAccount.getString("accountNumber") + 
				" Routing Number:" + eftAccount.getString("routingNumber"), module);
		
		List eftAccounts = null; 
		
		if (UtilValidate.isNotEmpty(eftAccount.getString("routingNumber")))	{ // only use if routing number available
			try { 
				eftAccounts = delegator.findByAnd("EftAccount", 
						UtilMisc.toMap("accountNumber", eftAccount.getString("accountNumber"), 
								"routingNumber", eftAccount.getString("routingNumber"))); 
			}
			catch (GenericEntityException e) {	
				Debug.logError("Find account/routing Number exception:" + e.getMessage(), module); 
			}
		}
		
		if (UtilValidate.isEmpty(eftAccounts))	{
			if (debug) Debug.logInfo("Account: " + eftAccount.getString("accountNumber") + 
					" Routing number:" + eftAccount.getString("routingNumber") + 
					"  not specified or not found...try to find with account number only.", module);
			
			// try to find only with only the account number
			try { 
				eftAccounts = delegator.findByAnd("EftAccount", 
						UtilMisc.toMap("accountNumber", eftAccount.getString("accountNumber"))); 
			}
			catch (GenericEntityException e) {	
				Debug.logError("Find account number exception:" + e.getMessage(), module); 
			}
			if(UtilValidate.isEmpty(eftAccounts))	{
				if (debug) Debug.logInfo("Account: " + eftAccount.getString("accountNumber") + " not found....", module);
				return null;     		// account number not found.
			}
		}
		
		eftAccount = (GenericValue) eftAccounts.iterator().next(); // get first found record
		
		try { 
			paymentMethod = eftAccount.getRelatedOne("PaymentMethod"); 
		}
		catch (GenericEntityException e) {	
			Debug.logError("Find paymentmethod exception:" + e.getMessage(), module);
		}
		if (paymentMethod != null)	{
			if (debug) Debug.logInfo("Party found::" + paymentMethod.getString("partyId"), module);
			return paymentMethod.getString("partyId");
		}
		return null;
	}
	
	/**
	 * return content of file in static variable localFile
	 * @return 'error' is not successfull....
	 */
	private static String getFile(HttpServletRequest request)	{
		try {
			Locale locale = UtilHttp.getLocale(request);
			LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
			partiesCreated =  accountsCreated = paymentsCreated = paymentAlreadyUploaded = 0;
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
				Debug.logWarning("[DataExchange.mt940] No files uploaded", module);
				return "error";
			}
			FileItem fi = null;
			for (int i = 0; i < lst.size(); i++) {
				fi = (FileItem) lst.get(i);
				String fieldName = fi.getFieldName();
				 //     Debug.logInfo("DataExchange fieldName: " + fieldName, module);
				 //     Debug.logInfo("DataExchange in isInMem: " + fi.isInMemory(), module);
 				 //     Debug.logInfo("DataExchange in getstring: " + fi.getString(), module);
				 //     Debug.logInfo("DataExchange in getSize: " + fi.getSize(), module);
				 //     Debug.logInfo("DataExchange in get: " + fi.get(), module);
				 //     Debug.logInfo("DataExchange in getContentType: " + fi.getContentType(), module);
				 //     Debug.logInfo("DataExchange in isFormField: " + fi.isFormField(), module);
				
				if (fi.getFieldName().compareTo("localFile") == 0)  // uploaded file found....
					localFile = fi.getString();
				return "OK";
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
		else if (localFile.charAt(end) == 0x0a) // for a windows formatted file only 0x0a?
			start = end + 1;
		end++; //look for next line
		if (debug) Debug.logInfo("Line: " + lineNumber + "  -->" + fLine, module);
		return fLine;
	}
	/**
	 *  All format MT940 dependent processing is done in this routine.
	 *  The found information is stored directly into the OFBiz structures.
	 *  It uses the format independant routine getLine to obtain a line from the uploded file.
	 * @param request 
	 * @return null if no payment transactions left.
	 */
	private static String getPayment(HttpServletRequest request) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		// structurees to create the records in the database (filled by the getPayment routine)
		payment = new HashMap();
		payment.put("paymentMethodTypeId","EFT_ACCOUNT");  // all payment are bank transfers....
		party = (GenericValue) delegator.makeValue("Party",null);
		partyGroup = (GenericValue) delegator.makeValue("PartyGroup",null);
		paymentMethod = (GenericValue) delegator.makeValue("PaymentMethod",null);
		eftAccount = (GenericValue) delegator.makeValue("EftAccount",null);
		String fileLine = getLine(); // get first line of transaction
		while (fileLine != null) {
			//   		Debug.logInfo("Line: " + lineNumber + "  -->" + fileLine, module);
			
			switch(lineNumber)	{
			case 1:
				routingNr =  fileLine; 
				break;
			case 2: 
				if (fileLine.compareTo("940") != 0)	{
					request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (line 2 does not contain 940)");
					return null;
				}
				break;
			case 3: 
				if (fileLine.compareTo(routingNr) != 0)	{
					// Debug.logError("MT940 upload error, line 3 (" + routingNr + ") differs from line 1 (" + fileLine + ")" , module);
					request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (line 3 differs from line 1)");
					return null;
				}
				break;
			}
			
			if (fileLine.charAt(0) == ':') 	{
				int tagNumber =   Integer.parseInt(fileLine.substring(1,3));
				String tagData = fileLine.substring(4);
				if (fileLine.charAt(4) == ':')  // 3 character tag;
					tagData = fileLine.substring(5);
				
				switch ( tagNumber)	{
				case 20: 
					break; // bank name of account file 
				case 25: 
					break; // account number of transaction file
				case 28: 
					bankSeqNr = tagData; // account sequence number
				break;
				case 60:	// balance and valuta
					String curr = new String(tagData.substring(7,10));
					// try to find in the currency table
					GenericValue uom = null;
					try	{
						uom = delegator.findByPrimaryKey("Uom",UtilMisc.toMap("uomId",curr));
					}
					catch (GenericEntityException e) {
						request.setAttribute("_ERROR_MESSAGE_", "Problem reading the currency table...");
						return "error";
					}
					if (uom == null)	{
						request.setAttribute("_ERROR_MESSAGE_","Could not find currency code " + curr + " in the currency table");
						return "error";
					}
					payment.put("currencyUomId", curr );
					defaultCurrency = curr;
					break;
				case 61:  //content of the transaction
					payment.put("paymentRefNum", routingNr.concat("-").concat(accountNr).concat("-").concat(bankSeqNr).concat("-").concat(String.valueOf(seqNr++))); //create payment number
					if (debug) Debug.logInfo("Line: " + lineNumber + "  Payment reference: " + payment.get("paymentRefNum"), module);
					payment.put("effectiveDate",UtilDateTime.toTimestamp(tagData.substring(2,4),tagData.substring(4,6), "20" + tagData.substring(0,2), "00","00","00"));
					if (tagData.charAt(10) == 'D')	debet = true; else debet = false;
					int x=11; while( x < 19 && tagData.charAt(x) != ',' && tagData.charAt(x) != '.')  x++;	// find end of amount string
					if (x < 19)		{
						payment.put("amount",new Double(tagData.substring(11,x).concat(".").
								concat( tagData.charAt(x+1) == 'N'? "0": tagData.substring(x+1,x+2)).
								concat( tagData.charAt(x+2) == 'N'? "0": tagData.substring(x+2,x+3))));
					}
					else payment.put("amount",new Double("00.00"));
					break;
				case 86:  // more information. For ABN-AMRO account info other party.
					payment.put("comments","");
					if (tagData.substring(0,4).compareTo("GIRO") == 0)	{ // GIRO number
						x = 4; while (tagData.charAt(x) == ' ')  x++;	// find first nonblank character
						int y = x; while (y< tagData.length() && tagData.charAt(y) != ' ')  y++;	// find end of account number
						eftAccount.set("accountNumber","G".concat(tagData.substring(x,y)));
						if (tagData.length() > y && tagData.charAt(y) == ' ')	// if blank name follows the account number
							eftAccount.set("nameOnAccount", tagData.substring(y));
						else // when not, name on next line
							eftAccount.set("nameOnAccount", getLine());
						partyGroup.set("groupName", eftAccount.getString("nameOnAccount"));
					}
					else if (tagData.charAt(0) == ' ') {	// normal bank account number start with blank
						eftAccount.set("accountNumber",tagData.substring(1,3) + tagData.substring(4,6) + tagData.substring(7,9) + tagData.substring(10,13));
						if (tagData.length() > 13 && tagData.charAt(13) == ' ')	// if blank name follows the account number
							eftAccount.set("nameOnAccount", tagData.substring(14));
						else // when not, name on next line
							eftAccount.set("nameOnAccount", getLine());
						partyGroup.set("groupName", eftAccount.getString("nameOnAccount"));
					}
					else if (tagData.substring(0,2).compareTo("EM") == 0)	{  // international payment
						eftAccount.set("accountNumber", tagData.substring(2,15));
						payment.put("comments",tagData.substring(15));
						payment.put("comments",payment.get("comments") + getLine() + getLine());
						eftAccount.set("nameOnAccount", getLine());
						partyGroup.set("groupName", eftAccount.getString("nameOnAccount"));
					}
					else if (tagData.substring(0,3).compareTo("BEA") == 0)	{  // paying with pincode
						eftAccount.set("accountNumber", "BEA" + tagData.substring(9,15));
						payment.put("comments",tagData.substring(18));
						if((fileLine = getLine()) == null) return null;
						x = 0; while (fileLine.charAt(x) != ',')  x++;	// find first comma 
						eftAccount.set("nameOnAccount", fileLine.substring(0,x));
						partyGroup.set("groupName", eftAccount.getString("nameOnAccount"));
						payment.put("comments",payment.get("comments") + " " + fileLine.substring(x+1));
					}
					else	{ // bank charges
						payment.put("comments",tagData);
						eftAccount.set("accountNumber", "ABNAMRO");
						eftAccount.set("nameOnAccount", "ABN AMRO Bank");
						partyGroup.set("groupName", eftAccount.getString("nameOnAccount"));
					}
					// read lines until the next 'tag' line and add to payment comments
					while (localFile.charAt(start) != ':') // start is pointing at the first character of next  line
						payment.put("comments",payment.get("comments") + " " + getLine());
					if (debug) Debug.logInfo("Payment Comments:"  + payment.get("comments"), module);
					return "ok";
				}
			}
			if (payment.get("currencyUomId") == null)	{
				payment.put("currencyUomId", defaultCurrency);
			}
				
			if (fileLine.charAt(0) == '-') 	{ // end of transaction
				bankSeqNr = null;
				seqNr = 0;
			}

			fileLine = getLine(); // get next line
		}
		return null;
	}
	private static String getFileHeader(HttpServletRequest request) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		eftAccount = (GenericValue) delegator.makeValue("EftAccount",null);
		String fileLine = null; // get first line of transaction
		lineNumber = end = start = seqNr =  0;
		while (lineNumber < 7 && (fileLine = getLine()) != null) {
			
			switch(lineNumber)	{
			case 1: 
				eftAccount.set("routingNumber",  fileLine);
				routingNr = fileLine;
				break;
			case 2: 
				if (fileLine.compareTo("940") != 0)	{
					request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (line 2 does not contain 940)");
					return null;
				}
				break;
			case 3: 
				if (fileLine.compareTo(eftAccount.getString("routingNumber")) != 0)	{
					Debug.logError("MT940 upload error, line 3 (" + routingNr + ") differs from line 1 (" + fileLine + ")" , module);
					request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (line 3 differs from line 1)");
					return null;
				}
			default:
				if (fileLine.indexOf(":25:") == 0)	{	
					eftAccount.set("accountNumber", fileLine.substring(4));
					accountNr = fileLine.substring(4);
					lineNumber = end = start = 0;
					return "ok";
				}
			}
		}
		Debug.logError("MT940 format error, no :25: tag record......" , module);
		request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (no :25: tag record found)");
		return null;
	}
}