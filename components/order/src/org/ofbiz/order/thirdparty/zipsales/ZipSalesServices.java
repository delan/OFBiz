/*
 * $Id: ZipSalesServices.java,v 1.1 2003/12/03 19:43:06 ajzeneski Exp $
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
import org.ofbiz.datafile.DataFile;
import org.ofbiz.datafile.DataFileException;
import org.ofbiz.datafile.Record;
import org.ofbiz.datafile.RecordIterator;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.security.Security;

import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DecimalFormat;

/**
 * Zip-Sales Database Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class ZipSalesServices {

    public static final String module = ZipSalesServices.class.getName();
    public static final String dataFile = "org/ofbiz/order/thirdparty/zipsales/FlatTable.xml";
    public static final String flatTable = "FlatTable";

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
        String importFileLocation = (String) context.get("fileLocation");

        // do security check
        if (!security.hasPermission("SERVICE_INVOKE_ANY", userLogin)) {
            return ServiceUtil.returnError("You do not have permission to load tax tables");
        }

        // load the datafile
        DataFile df = null;
        try {
            df = DataFile.makeDataFile(UtilURL.fromResource(dataFile), flatTable);
        } catch (DataFileException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to read ZipSales DataFile");
        }

        // locate the file to be imported
        URL url = null;
        try {
            url = new URL(importFileLocation);
        } catch (MalformedURLException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Not a valid URL for fileLocation");
        }

        RecordIterator ri = null;
        try {
            ri = df.makeRecordIterator(url);
        } catch (DataFileException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problem getting the Record Iterator");
        }
        if (ri != null) {
            while (ri.hasNext()) {
                Record entry = null;
                try {
                    entry = ri.next();
                } catch (DataFileException e) {
                    Debug.logError(e, module);
                }
                GenericValue newValue = delegator.makeValue("ZipSalesTaxLookup", null);
                // PK fields
                newValue.set("zipCode", entry.getString("zipCode").trim());
                newValue.set("stateCode", entry.get("stateCode") != null ? entry.getString("stateCode").trim() : "_NA_");
                newValue.set("city", entry.get("city") != null ? entry.getString("city").trim() : "_NA_");
                newValue.set("county", entry.get("county") != null ? entry.getString("county").trim() : "_NA_");
                newValue.set("fromDate", parseDate(entry.getString("effectiveDate")));

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
                    delegator.create(newValue);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Error writing record(s) to the database");
                }

                // console log
                Debug.log(entry.get("zipCode") + "/" + newValue.get("stateCode") + "/" + newValue.get("city") + "/" + newValue.get("county") + "/" + newValue.get("fromDate"));
            }
        }

        return ServiceUtil.returnSuccess();
    }

    // tax calc service
    public static Map calcTax(DispatchContext dctx, Map context) {
        return ServiceUtil.returnError("This service is not implemented yet");
    }

    // formatting methods
    private static Timestamp parseDate(String dateString) {
        Timestamp ts = null;
        try {
            ts = new Timestamp(dateFormat.parse(dateString).getTime());
        } catch (ParseException e) {
            Debug.logError(e, module);
        }
        return ts;
    }

    private static String formatCurrency(double currency) {
        return curFormat.format(currency);
    }
}
