/*
 * $Id: ZipSalesServices.java,v 1.12 2004/02/12 05:07:17 ajzeneski Exp $
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

import java.util.*;
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
 * @version    $Revision: 1.12 $
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
                    delegator.createOrStore(newValue);
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
                    newValue.set("shipCond", entry.get("shipCond") != null ? entry.getString("shipCond").trim() : null);

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
        String stateProvince = shippingAddress.getString("stateProvinceGeoId");
        String postalCode = shippingAddress.getString("postalCode");
        String city = shippingAddress.getString("city");

        // setup the return lists.
        List orderAdjustments = new ArrayList();
        List itemAdjustments = new ArrayList();

        // check for a valid state/province geo
        String validStates = UtilProperties.getPropertyValue("zipsales.properties", "zipsales.valid.states");
        if (validStates != null && validStates.length() > 0) {
            List stateSplit = StringUtil.split(validStates, "|");
            if (!stateSplit.contains(stateProvince)) {
                Map result = ServiceUtil.returnSuccess();
                result.put("orderAdjustments", orderAdjustments);
                result.put("itemAdjustments", itemAdjustments);
                return result;
            }
        }

        // loop through and get per item tax rates
        for (int i = 0; i < itemProductList.size(); i++) {
            GenericValue product = (GenericValue) itemProductList.get(i);
            Double itemAmount = (Double) itemAmountList.get(i);
            Double shippingAmount = (Double) itemShippingList.get(i);
            itemAdjustments.add(getItemTaxList(delegator, product, postalCode, city, itemAmount.doubleValue(), shippingAmount.doubleValue(), false));
        }
        if (orderShippingAmount.doubleValue() > 0) {
            List taxList = getItemTaxList(delegator, null, postalCode, city, 0.00, orderShippingAmount.doubleValue(), false);
            orderAdjustments.addAll(taxList);
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("orderAdjustments", orderAdjustments);
        result.put("itemAdjustments", itemAdjustments);
        return result;
    }

    private static List getItemTaxList(GenericDelegator delegator, GenericValue item, String zipCode, String city, double itemAmount, double shippingAmount, boolean isUseTax) {
        List adjustments = new ArrayList();

        // check the item for tax status
        if (item != null && item.get("taxable") != null && "N".equals(item.getString("taxable"))) {
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
        } else {
            // now filter the county default since we don't track counties
            if (taxLookup != null && taxLookup.size() > 1) {
                taxLookup = EntityUtil.filterByAnd(taxLookup, UtilMisc.toMap("countyDefault", "Y"));
            }
        }

        // now filter by date
        if (taxLookup != null && taxLookup.size() > 1) {
            taxLookup = EntityUtil.filterByDate(taxLookup);
        }

        // get the first one
        GenericValue taxEntry = null;
        if (taxLookup != null && taxLookup.size() > 0) {
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

        // get state code
        String stateCode = taxEntry.getString("stateCode");

        // check if shipping is exempt
        boolean taxShipping = true;

        // look up the rules
        List ruleLookup = null;
        try {
            ruleLookup = delegator.findByAnd("ZipSalesRuleLookup", UtilMisc.toMap("stateCode", stateCode), UtilMisc.toList("-fromDate"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        // filter out city
        if (ruleLookup != null && ruleLookup.size() > 1) {
            ruleLookup = EntityUtil.filterByAnd(ruleLookup, UtilMisc.toMap("city", city.toUpperCase()));
        }

        // no county captured; so filter by date
        if (ruleLookup != null && ruleLookup.size() > 1) {
            ruleLookup = EntityUtil.filterByDate(ruleLookup);
        }

        if (ruleLookup != null) {
            Iterator ruleIterator = ruleLookup.iterator();
            while (ruleIterator.hasNext()) {
                if (!taxShipping) {
                    // if we found an rule which passes no need to contine (all rules are ||)
                    break;
                }
                GenericValue rule = (GenericValue) ruleIterator.next();
                String idCode = rule.getString("idCode");
                String taxable = rule.getString("taxable");
                String condition = rule.getString("shipCond");
                if ("T".equals(taxable))  {
                    // this record is taxable
                    continue;
                } else {
                    // except if conditions are met
                    boolean qualify = false;
                    char[] conditions = condition.toCharArray();
                    for (int i = 0; i < conditions.length; i++) {
                        switch (conditions[i]) {
                            case 'A' :
                                // SHIPPING CHARGE SEPARATELY STATED ON INVOICE
                                qualify = true; // OFBiz does this by default
                                break;
                            case 'B' :
                                // SHIPPING CHARGE SEPARATED ON INVOICE FROM HANDLING OR SIMILAR CHARGES
                                qualify = false; // we do not support this currently
                                break;
                            case 'C' :
                                // ITEM NOT SOLD FOR GUARANTEED SHIPPED PRICE
                                qualify = false; // we don't support this currently
                                break;
                            case 'D' :
                                // SHIPPING CHARGE IS COST ONLY
                                qualify = false; // we assume a handling charge is included
                                break;
                            case 'E' :
                                // SHIPPED DIRECTLY TO PURCHASER
                                qualify = true; // this is true, unless gifts do not count?
                                break;
                            case 'F' :
                                // SHIPPED VIA COMMON CARRIER
                                qualify = true; // best guess default
                                break;
                            case 'G' :
                                // SHIPPED VIA CONTRACT CARRIER
                                qualify = false; // best guess default
                                break;
                            case 'H' :
                                // SHIPPED VIA VENDOR EQUIPMENT
                                qualify = false; // best guess default
                                break;
                            case 'I' :
                                // SHIPPED F.O.B. ORIGIN
                                qualify = false; // no clue
                                break;
                            case 'J' :
                                // SHIPPED F.O.B. DESTINATION
                                qualify = false; // no clue
                                break;
                            case 'K' :
                                // F.O.B. IS PURCHASERS OPTION
                                qualify = false; // no clue
                                break;
                            case 'L' :
                                // SHIPPING ORIGINATES OR TERMINATES IN DIFFERENT STATES
                                qualify = false; // not determined at order time, no way to know
                                break;
                            case 'M' :
                                // PROOF OF VENDOR ACTING AS SHIPPING AGENT FOR PURCHASER
                                qualify = false; // no clue
                                break;
                            case 'N' :
                                // SHIPPED FROM VENDOR LOCATION
                                qualify = true; // sure why not
                                break;
                            case 'O' :
                                // SHIPPING IS BY PURCHASER OPTION
                                qualify = false; // most online stores require shipping
                                break;
                            case 'P' :
                                // CREDIT ALLOWED FOR SHIPPING CHARGE PAID BY PURCHASER TO CARRIER
                                qualify = false; // best guess default
                                break;
                            default: break;
                        }
                    }

                    if (qualify) {
                        if (isUseTax) {
                            if (idCode.indexOf('U') > 0) {
                                taxShipping = false;
                            }
                        } else {
                            if (idCode.indexOf('S') > 0) {
                                taxShipping = false;
                            }
                        }
                    }
                }
            }
        }

        double taxableAmount = itemAmount;
        if (taxShipping) {
            //Debug.log("Taxing shipping", module);
            taxableAmount += shippingAmount;
        } else {
            Debug.log("Shipping is not taxable", module);
        }

        // calc tax amount
        double taxRate = comboTaxRate.doubleValue();
        double taxCalc = taxableAmount * taxRate;

        // format the number
        Double taxAmount = new Double(formatCurrency(taxCalc));
        adjustments.add(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("amount", taxAmount, "orderAdjustmentTypeId", "SALES_TAX", "comments", new Double(taxRate).toString(), "description", "Sales Tax (" + stateCode + ")")));

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
