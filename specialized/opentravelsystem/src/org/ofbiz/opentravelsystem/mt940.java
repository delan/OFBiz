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

/*
TODO: check only parties and payments can be uploaded either where the groupId of the login is either the 'from' party or the 'to' party 
TODO: if the party eft account number exist, check if the party relationship exists, when not add....

*/
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;

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
import org.ofbiz.minilang.method.entityops.TransactionBegin;
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
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.transaction.GenericTransactionException;


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
	static GenericDelegator delegator = null;
	static LocalDispatcher dispatcher = null;
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
	static Map payment = null;		
	static Map partyInfo = null;    				
	// statistics
	static int partiesCreated;
	static int paymentsCreated;
	static int paymentAlreadyUploaded;

	static String accountPartyId = null; // the owner of the imported data file
	static boolean partyOnly = false;
	static GenericValue userLogin = null;
	static Map results = null;
	
	/* event to import bankstatement mt940 records creating parties only*/
	public static String importDataPartyOnly(HttpServletRequest request, HttpServletResponse response) {
		partyOnly = true;
		return importDataProcess(request,response);
	}
	
	/* event to import bankstatement mt940 records */
	public static String importData(HttpServletRequest request, HttpServletResponse response) {
		partyOnly = false;
		return importDataProcess(request,response);
	}
	
	public static String importDataProcess(HttpServletRequest request, HttpServletResponse response) {
		delegator = (GenericDelegator) request.getAttribute("delegator");
        userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale loc = (Locale)request.getSession().getServletContext().getAttribute("locale");
        if (loc == null) 
            loc = Locale.getDefault();
		// conversion of old file insert year in the reference number because banksequence number start new every year
        try {
   	    int count = 0;   
		GenericValue paymentRecord = delegator.findByPrimaryKey("Payment",UtilMisc.toMap("paymentId","anet10025"));
		if (paymentRecord == null)
			paymentRecord = delegator.findByPrimaryKey("Payment",UtilMisc.toMap("paymentId","anet11128"));
		String refnum = paymentRecord.getString("paymentRefNum");
		if (!refnum.substring(21,22).equals("-")) { // check if conversion is done or not
	   		TransactionUtil.begin();
			Iterator iPayments = delegator.findListIteratorByCondition("Payment", null,null,null);
			while ((paymentRecord = (GenericValue) iPayments.next()) != null) {
				refnum = paymentRecord.getString("paymentRefNum");
				if (refnum != null && refnum.length() > 20) {
					String year = paymentRecord.get("effectiveDate").toString().substring(2,4);
					paymentRecord.put("paymentRefNum", refnum.substring(0,19) + year + "-" + refnum.substring(19));
					if (debug) Debug.logInfo("Updating refnum old:" + refnum + " new:" + paymentRecord.getString("paymentRefNum"), module);
					paymentRecord.store();
					count++;
					if (count == 100) {
						TransactionUtil.commit();
						count = 0;
						TransactionUtil.begin();
					}
				}
			}
			TransactionUtil.commit();
			request.setAttribute("_EVENT_MESSAGE_", "File converted...however re-enter you upload request.....");
			return "success";
		}}
		catch (GenericEntityException e) {	Debug.logError("Conversion problems:" + e.getMessage(), module); return "error"; }

		if (getFile(request).equals("error") || localFile == null || localFile.length() == 0) { // get the content of the uploaded file...
			request.setAttribute("_ERROR_MESSAGE_", "Uploaded file not found or an empty file......");
			return "error";
		}
		if (debug) Debug.logInfo("File loaded...", module);
		
		// find partyId for the bankccount number the file was generated from
		if (getFileHeader(request) == null) //  obtain data to retrieve partyId of the owner of the account
			return "error";
		if ((accountPartyId = getParty()) == null)	{ // retrieve party id...
			request.setAttribute("_ERROR_MESSAGE_", "No party/account could be found with this Bankaccountnumber: " + accountNr + "(" + routingNr + "). Please correct....");
			return "error";
		}
		if (debug) Debug.logInfo("Company Party found:" + accountPartyId, module);
		
		// find the related tax party to enable the correct payment type
		// find the taxGeoId of the company
		String taxAuthPartyId = null; // the taxauthority
		try	{
			List partyTaxInfos = EntityUtil.filterByDate(delegator.findByAnd("PartyTaxAuthInfo",UtilMisc.toMap("partyId",accountPartyId)));
			if (partyTaxInfos != null && partyTaxInfos.size() > 0)	{
				taxAuthPartyId = ((GenericValue) partyTaxInfos.get(0)).getString("taxAuthPartyId");
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
			// first check if the payment already exists
			if (checkPayment() == true)	{
				paymentAlreadyUploaded++;
				if (debug) Debug.logInfo("Payment already exists...so not create again...",module);
				continue;
			}
			
			String otherParty = getParty();
			// check to see if the parties where found, when not create
			if (otherParty == null)	{ 
				if (createParty() == null) 	
					return "error"; // party creation error
				otherParty = (String) partyInfo.get("partyId");
			}
			if (debet  == false)	{
				if (otherParty.equals(taxAuthPartyId) || accountPartyId.equals(taxAuthPartyId))
					payment.put("paymentTypeId","SALES_TAX_PAYMENT");
				else 
					payment.put("paymentTypeId","CUSTOMER_PAYMENT");
				payment.put("partyIdFrom",otherParty);
				payment.put("partyIdTo", accountPartyId);    		
				}
			else	{ // credit
			if (otherParty.equals(taxAuthPartyId) || accountPartyId.equals(taxAuthPartyId))
					payment.put("paymentTypeId","SALES_TAX_PAYMENT");
						else 
					payment.put("paymentTypeId","VENDOR_PAYMENT");
				payment.put("partyIdTo",otherParty);
				payment.put("partyIdFrom", accountPartyId);    		
				}
			
			// set other fields in payment record
			payment.put("statusId","PMNT_NOT_PAID");  // it is always loaded with this status....needs tobe changed to send/received....	
			payment.put("paymentMethodTypeId","EFT_ACCOUNT");
			// check if the payment was already uploaded.....
			if (!partyOnly)	{	// input parameter.....
				// finally create payment record.
				payment.put("userLogin",userLogin);
				payment.put("locale", loc);
				try {
					results = dispatcher.runSync("createPayment", payment);
				} catch (GenericServiceException e1) {
					Debug.logError(e1, "Error creating payment", module);
					continue;
				}
				if (debug) Debug.logInfo("Payment [" + results.get("paymentId") + "] created with reference number: " + payment.get("paymentRefNum"),module);
				paymentsCreated++;
			}
		}
		String mess = "Upload ended... " + partiesCreated + " partyGroups created, ";
		if (!partyOnly) mess = mess.concat(paymentsCreated + " payment records created, " + paymentAlreadyUploaded + " payments already uploaded....");
		request.setAttribute("_EVENT_MESSAGE_", mess);
		
		return "success";
	}
	/**
	 *  Check if the payment was already uploaded.......
	 * @return "error" when not ok...
	 */
	private static boolean checkPayment()	{
		List payments = null;
		try { payments = delegator.findByAnd("Payment", UtilMisc.toMap("paymentRefNum",payment.get("paymentRefNum"))); }
		catch (GenericEntityException e) {	Debug.logError("Find payment exception:" + e.getMessage(), module); }
		if (payments == null || payments.size() == 0)	
			return false;
		else	{
			GenericValue paym = (GenericValue) payments.get(0);
			if (debug) Debug.logInfo("Payment [" + paym.getString("paymentId") + "] with reference number: " + payment.get("paymentRefNum") + " already exists",module);
			return true;
		}
	}

	/**
	 *  create party with payment method and eftAccount for an eftAccount that was not found.
	 *  create roles for customer or supplier...
	 * @return "error" when not ok...
	 */
	private static String createParty()	{
		// create party
		partyInfo.put("userLogin", userLogin);
		partyInfo.put("partyRelationshipTypeId", "SUPPLIER_REL");
		try {
			results = dispatcher.runSync("otsAddParty", partyInfo);
			partyInfo.put("partyId",results.get("partyId"));

		} catch (GenericServiceException e1) {
			Debug.logError(e1, "Error creating party relationship", module);
		}
		
		Debug.logInfo("Party Created:" + partyInfo.get("partyId"), module);
		partiesCreated++;
		return (String) partyInfo.get("partyId");
	}
	/**
	 *    find parties by account number in the eftAccount table --> paymentMethod for the partyId
	 * @param accountNumber in the eftAccount record
	 * @return partyId if found, when not null
	 */
	private static String getParty()	{
		if (debug) Debug.logInfo("eftAccount searching: accountNumber:" + partyInfo.get("accountNumber"), module);
		
		List eftAccounts = null; 
		
		// try to find the account number
		try { 
			eftAccounts = delegator.findByAnd("EftAccount", 
					UtilMisc.toMap("accountNumber", partyInfo.get("accountNumber"))); 
		}
		catch (GenericEntityException e) {	
			Debug.logError("Find account number exception:" + e.getMessage(), module); 
		}
		
		
		if(UtilValidate.isEmpty(eftAccounts))	{
			if (debug) Debug.logInfo("Account: " + partyInfo.get("accountNumber") + " not found....", module);
			return null;     		// account number not found.
		}
		
		Iterator it = eftAccounts.iterator();
		List paymentMethods = null;
		
		try { 
			while (it.hasNext() && (paymentMethods == null || paymentMethods.size() == 0))	{
				GenericValue eAccount = (GenericValue) it.next();
				paymentMethods = EntityUtil.filterByDate(eAccount.getRelated("PaymentMethod"));
			}
		}
		catch (GenericEntityException e) {	
			Debug.logError("Find paymentmethod exception:" + e.getMessage(), module);
		}
		
		if (paymentMethods != null && paymentMethods.size() > 0)	{
			GenericValue paymentMethod = (GenericValue) paymentMethods.get(0);
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
			HttpSession session = request.getSession();
			partiesCreated =  paymentsCreated = paymentAlreadyUploaded = 0;
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
		partyInfo = new HashMap();
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
					payment.put("paymentRefNum", routingNr.concat("-").concat(accountNr).concat("-").concat(tagData.substring(0,2)).concat("-").concat(bankSeqNr).concat("-").concat(String.valueOf(seqNr++))); //create payment number
					if (debug) Debug.logInfo("Line: " + lineNumber + "  Payment reference: " + payment.get("paymentRefNum"), module);
					payment.put("effectiveDate",UtilDateTime.toTimestamp(tagData.substring(2,4),tagData.substring(4,6), "20" + tagData.substring(0,2), "00","00","00"));
					if (tagData.charAt(10) == 'D')	debet = true; else debet = false;
					int x=11; while( x < 19 && tagData.charAt(x) != ',' && tagData.charAt(x) != '.')  x++;	// find end of amount string
					if (x < 19)		{
						String amount = new String(tagData.substring(11,x));
						if (tagData.charAt(x+2) == 'N')	amount = amount.concat(".").concat(tagData.substring(x+1,x+2));
						if (tagData.charAt(x+3) == 'N')	amount = amount.concat(".").concat(tagData.substring(x+1,x+3));
						payment.put("amount",new Double(amount));
					}
					else payment.put("amount",new Double("00.00"));
					if (debug) Debug.logInfo("Line: " + lineNumber + " Value of 'x':" + x + " Char at 'x+3':" + tagData.charAt(x+3) + "  Payment amount: " + payment.get("amount"), module);
					break;
				case 86:  // more information. For ABN-AMRO account info other party.
					payment.put("comments","");
					if (tagData.substring(0,4).compareTo("GIRO") == 0)	{ // GIRO number
						x = 4; while (tagData.charAt(x) == ' ')  x++;	// find first nonblank character
						int y = x; while (y< tagData.length() && tagData.charAt(y) != ' ')  y++;	// find end of account number
						partyInfo.put("accountNumber","G".concat(tagData.substring(x,y)));
						if (tagData.length() > y && tagData.charAt(y) == ' ')	// if blank name follows the account number
							partyInfo.put("groupName", tagData.substring(y));
						else // when not, name on next line
							partyInfo.put("groupName", getLine());
					}
					else if (tagData.charAt(0) == ' ') {	// normal bank account number start with blank
						partyInfo.put("accountNumber",tagData.substring(1,3) + tagData.substring(4,6) + tagData.substring(7,9) + tagData.substring(10,13));
						if (tagData.length() > 13 && tagData.charAt(13) == ' ')	// if blank name follows the account number
							partyInfo.put("groupName", tagData.substring(14));
						else // when not, name on next line
							partyInfo.put("groupName", getLine());
					}
					else if (tagData.substring(0,2).compareTo("EM") == 0)	{  // international payment
						partyInfo.put("accountNumber", tagData.substring(2,15));
						payment.put("comments",tagData.substring(15));
						if((fileLine = getLine()) != null) {
							payment.put("comments",payment.get("comments") + fileLine);
							if((fileLine = getLine()) != null) {
								payment.put("comments",payment.get("comments") + fileLine);
							}
						}
						if (fileLine != null) {
							if((fileLine=getLine()) != null) 
								partyInfo.put("groupName", fileLine);
							else
								partyInfo.put("groupName", "International Payment");
						}
						else
							partyInfo.put("groupName", "International Payment");
					}
					else if (tagData.substring(0,3).compareTo("BEA") == 0)	{  // paying with pincode
						partyInfo.put("accountNumber", "BEA_Payment");
						payment.put("comments",tagData.substring(18));
						if((fileLine = getLine()) == null) return null;
						x = 0; while (fileLine.charAt(x) != ',')  x++;	// find first comma 
						partyInfo.put("groupName", "BEA Payments");
						payment.put("comments",payment.get("comments") + " - " + fileLine); 
					}
					else	{ // bank charges
						payment.put("comments",tagData);
						partyInfo.put("accountNumber", "ABNAMRO");
						partyInfo.put("groupName", "ABN AMRO Bank");
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
		GenericValue eftAccount = (GenericValue) delegator.makeValue("EftAccount",null);
		String fileLine = null; // get first line of transaction
		lineNumber = end = start = seqNr =  0;
		while (lineNumber < 7 && (fileLine = getLine()) != null) {
			
			switch(lineNumber)	{
			case 1: 
				eftAccount.put("routingNumber",  fileLine);
				routingNr = fileLine;
				break;
			case 2: 
				if (fileLine.compareTo("940") != 0)	{
					request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (line 2 does not contain 940)");
					return null;
				}
				break;
			case 3: 
				if (!fileLine.equals((String)eftAccount.get("routingNumber")))	{
					Debug.logError("MT940 upload error, line 3 (" + routingNr + ") differs from line 1 (" + fileLine + ")" , module);
					request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (line 3 differs from line 1)");
					return null;
				}
			default:
				if (fileLine.indexOf(":25:") == 0)	{	
					eftAccount.put("accountNumber", fileLine.substring(4));
					accountNr = fileLine.substring(4);
					lineNumber = end = start = 0;
					partyInfo = new HashMap();
					partyInfo.put("accountNumber",accountNr);
					return "ok";
				}
			}
		}
		Debug.logError("MT940 format error, no :25: tag record......" , module);
		request.setAttribute("_ERROR_MESSAGE_", "File does not seem to have the MT940 file format (no :25: tag record found)");
		return null;
	}
}