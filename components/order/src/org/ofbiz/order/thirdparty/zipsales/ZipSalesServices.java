/*
 * $Id: ZipSalesServices.java,v 1.3 2003/12/04 00:52:17 ajzeneski Exp $
 *
 *  Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order.thirdparty.zipsales;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.datafile.DataFile;
import org.ofbiz.datafile.DataFileException;
import org.ofbiz.datafile.Record;
import org.ofbiz.datafile.RecordIterator;
import org.ofbiz.base.util.*;
import org.ofbiz.security.Security;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.io.File;

/**
 * Zip-Sales Database Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.0
 */
public class ZipSalesServices {

    public static final String module = ZipSalesServices.class.getName();
    public static final String dataFile = "org/ofbiz/order/thirdparty/zipsales/ZipSalesTaxTables.xml";
    public static final String flatTable = "FlatTaxTable";
    public static final String ruleTable = "FreightRuleTable";

    // number formatting
    private static String curFmtStr = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
    private static DecimalFormat curFormat = new DecimalFormat(curFmtStr);

    // date formatting
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    // import table service
    public static Map importFlatTable(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String taxFileLocation = (String) context.get("taxFileLocation");
        String ruleFileLocation = (String) context.get("ruleFileLocation");

        // do security check
        if (!security.hasPermission("SERVICE_INVOKE_ANY", userLogin)) {
            return ServiceUtil.returnError("You do not have permission to load tax tables");
        }

        // get a now stamp (we'll use 2000-01-01)
        Timestamp now = parseDate("20000101", null);

        // load the data file
        DataFile tdf = null;
        try {
            tdf = DataFile.makeDataFile(UtilURL.fromResource(dataFile), flatTable);
        } catch (DataFileException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to read ZipSales DataFile");
        }

        // locate the file to be imported
        URL tUrl = UtilURL.fromResource(taxFileLocation);
        if (tUrl == null) {
            return ServiceUtil.returnError("Unable to locate tax file at location : " + taxFileLocation);
        }

        RecordIterator tri = null;
        try {
            tri = tdf.makeRecordIterator(tUrl);
        } catch (DataFileException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problem getting the Record Iterator");
        }
        if (tri != null) {
            while (tri.hasNext()) {
                Record entry = null;
                try {
                    entry = tri.next();
                } catch (DataFileException e) {
                    Debug.logError(e, module);
                }
                GenericValue newValue = delegator.makeValue("ZipSalesTaxLookup", null);
                // PK fields
                newValue.set("zipCode", entry.getString("zipCode").trim());
                newValue.set("stateCode", entry.get("stateCode") != null ? entry.getString("stateCode").trim() : "_NA_");
                newValue.set("city", entry.get("city") != null ? entry.getString("city").trim() : "_NA_");
                newValue.set("county", entry.get("county") != null ? entry.getString("county").trim() : "_NA_");
                newValue.set("fromDate", parseDate(entry.getString("effectiveDate"), now));

                // non-PK fields
                newValue.set("countyFips", entry.get("countyFips"));
                newValue.set("countyDefault", entry.get("countyDefault"));
                newValue.set("generalDefault", entry.get("generalDefault"));
                newValue.set("insideCity", entry.get("insideCity"));
                newValue.set("geoCode", entry.get("geoCode"));
                newValue.set("stateSalesTax", entry.get("stateSalesTax"));
                newValue.set("citySalesTax", entry.get("citySalesTax"));
                newValue.set("cityLocalSalesTax", entry.get("cityLocalSalesTax"));
                newValue.set("countySalesTax", entry.get("countySalesTax"));
                newValue.set("countyLocalSalesTax", entry.get("countyLocalSalesTax"));
                newValue.set("comboSalesTax", entry.get("comboSalesTax"));
                newValue.set("stateUseTax", entry.get("stateUseTax"));
                newValue.set("cityUseTax", entry.get("cityUseTax"));
                newValue.set("cityLocalUseTax", entry.get("cityLocalUseTax"));
                newValue.set("countyUseTax", entry.get("countyUseTax"));
                newValue.set("countyLocalUseTax", entry.get("countyLocalUseTax"));
                newValue.set("comboUseTax", entry.get("comboUseTax"));

                try {
                    // using storeAll as an easy way to create/update
                    delegator.storeAll(UtilMisc.toList(newValue));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Error writing record(s) to the database");
                }

                // console log
                Debug.log(newValue.get("zipCode") + "/" + newValue.get("stateCode") + "/" + newValue.get("city") + "/" + newValue.get("county") + "/" + newValue.get("fromDate"));
            }
        }

        // load the data file
        DataFile rdf = null;
        try {
            rdf = DataFile.makeDataFile(UtilURL.fromResource(dataFile), ruleTable);
        } catch (DataFileException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to read ZipSales DataFile");
        }

        // locate the file to be imported
        URL rUrl = UtilURL.fromResource(ruleFileLocation);
        if (rUrl == null) {
            return ServiceUtil.returnError("Unable to locate rule file from location : " + ruleFileLocation);
        }

        RecordIterator rri = null;
        try {
            rri = rdf.makeRecordIterator(rUrl);
        } catch (DataFileException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problem getting the Record Iterator");
        }
        if (rri != null) {
            while (rri.hasNext()) {
                Record entry = null;
                try {
                    entry = rri.next();
                } catch (DataFileException e) {
                    Debug.logError(e, module);
                }
                if (entry.get("stateCode") != null && entry.getString("stateCode").length() > 0) {
                    GenericValue newValue = delegator.makeValue("ZipSalesRuleLookup", null);
                    // PK fields
                    newValue.set("stateCode", entry.get("stateCode") != null ? entry.getString("stateCode").trim() : "_NA_");
                    newValue.set("city", entry.get("city") != null ? entry.getString("city").trim() : "_NA_");
                    newValue.set("county", entry.get("county") != null ? entry.getString("county").trim() : "_NA_");
                    newValue.set("fromDate", parseDate(entry.getString("effectiveDate"), now));

                    // non-PK fields
                    newValue.set("idCode", entry.get("idCode") != null ? entry.getString("idCode").trim() : null);
                    newValue.set("taxable", entry.get("taxable") != null ? entry.getString("taxable").trim() : null);
                    newValue.set("condition", entry.get("condition") != null ? entry.getString("condition").trim() : null);

                    try {
                        // using storeAll as an easy way to create/update
                        delegator.storeAll(UtilMisc.toList(newValue));
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError("Error writing record(s) to the database");
                    }

                    // console log
                    Debug.log(newValue.get("stateCode") + "/" + newValue.get("city") + "/" + newValue.get("county") + "/" + newValue.get("fromDate"));
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    // tax calc service
    public static Map flatTaxCalc(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        List itemProductList = (List) context.get("itemProductList");
        List itemAmountList = (List) context.get("itemAmountList");
        List itemShippingList = (List) context.get("itemShippingList");
        Double orderShippingAmount = (Double) context.get("orderShippingAmount");
        GenericValue shippingAddress = (GenericValue) context.get("shippingAddress");

        // flatTaxCalc only uses the Zip + City from the address
        String postalCode = shippingAddress.getString("postalCode");
        String city = shippingAddress.getString("city");

        // setup the return lists.
        List orderAdjustments = new ArrayList();
        List itemAdjustments = new ArrayList();

        // loop through and get per item tax rates
        for (int i = 0; i < itemProductList.size(); i++) {
            GenericValue product = (GenericValue) itemProductList.get(i);
            Double itemAmount = (Double) itemAmountList.get(i);
            itemAdjustments.add(getItemTaxList(delegator, product, postalCode, city, itemAmount.doubleValue(), false));
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("orderAdjustments", orderAdjustments);
        result.put("itemAdjustments", itemAdjustments);
        return result;
    }

    private static List getItemTaxList(GenericDelegator delegator, GenericValue item, String zipCode, String city, double itemAmount, boolean isUseTax) {
        List adjustments = new ArrayList();

        // check the item for tax status
        if (item.get("taxable") != null && "N".equals(item.getString("taxable"))) {
            // item not taxable
            return adjustments;
        }

        // lookup the records
        List zipLookup = null;
        try {
            zipLookup = delegator.findByAnd("ZipSalesTaxLookup", UtilMisc.toMap("zipCode", zipCode), UtilMisc.toList("-fromDate"));
        } catch (GenericEntityException e) {
            Debug.logWarning("ZipSalesFlatTaxCalc: No ZipCode Entry Found.", module);
            return adjustments;
        }

        // the filtered list
        List taxLookup = null;

        // first filter by city
        if (zipLookup != null && zipLookup.size() > 0) {
            taxLookup = EntityUtil.filterByAnd(zipLookup, UtilMisc.toMap("city", city.toUpperCase()));
        }

        // no city get the main default
        if (taxLookup == null) {
            taxLookup = EntityUtil.filterByAnd(zipLookup, UtilMisc.toMap("generalDefault", "Y"));
        }

        // now filter the county default since we don't track counties
        if (taxLookup != null && taxLookup.size() > 1) {
            taxLookup = EntityUtil.filterByAnd(taxLookup, UtilMisc.toMap("countyDefault", "Y"));
        }

        // now filter by date
        if (taxLookup != null && taxLookup.size() > 1) {
            taxLookup = EntityUtil.filterByDate(taxLookup);
        }

        // get the first one
        GenericValue taxEntry = null;
        if (taxLookup != null) {
            taxEntry = (GenericValue) taxLookup.iterator().next();
        }

        if (taxEntry == null) {
            Debug.logWarning("No tax entry found for : " + zipCode + " / " + city + " - " + itemAmount, module);
            return adjustments;
        }

        String fieldName = "comboSalesTax";
        if (isUseTax) {
            fieldName = "comboUseTax";
        }

        Double comboTaxRate = taxEntry.getDouble(fieldName);
        if (comboTaxRate == null) {
            Debug.logWarning("No Combo Tax Rate In Field " + fieldName + " @ " + zipCode + " / " + city + " - " + itemAmount, module);
            return adjustments;
        }

        // calc tax amount
        double taxRate = comboTaxRate.doubleValue();
        double taxCalc = itemAmount * taxRate;

        // get state code for comment
        String stateCode = taxEntry.getString("stateCode");

        // format the number
        Double taxAmount = new Double(formatCurrency(taxCalc));
        adjustments.add(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("amount", taxAmount, "orderAdjustmentTypeId", "SALES_TAX", "comments", "Sales Tax (" + stateCode + ")")));

        return adjustments;
    }

    // formatting methods
    private static Timestamp parseDate(String dateString, Timestamp useWhenNull) {
        Timestamp ts = null;
        if (dateString != null) {
            try {
                ts = new Timestamp(dateFormat.parse(dateString).getTime());
            } catch (ParseException e) {
                Debug.logError(e, module);
            }
        }

        if (ts != null) {
            return ts;
        } else {
            return useWhenNull;
        }
    }

    private static String formatCurrency(double currency) {
        return curFormat.format(currency);
    }
}
