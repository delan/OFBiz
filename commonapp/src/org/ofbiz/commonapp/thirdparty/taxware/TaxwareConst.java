/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project and repected authors.
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

package org.ofbiz.commonapp.thirdparty.taxware;

/**
 * TaxwareConst - Constants
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jun 5, 2002
 * @version    1.0
 */
public class TaxwareConst {

    public static int UTL_INPUT_DATA_SIZE = 2592;
    public static int INP_PARAM_SIZE = 60;

    public static int UTL_OUTPUT_DATA_SIZE = 1704;
    public static int OUT_PARAM_SIZE = 284;

    public static int OUT_FIELD_COUNT = 42;
    public static int IN_FIELD_COUNT = 171;

    public static int INP_POS_SYSTEM_INDICATOR = 0;
    public static int INP_SIZ_SYSTEM_INDICATOR = 1;

    public static int INP_POS_LANGUAGE_CODE = 1;
    public static int INP_SIZ_LANGUAGE_CODE = 3;

    public static int INP_POS_COMPANY_ID = 4;
    public static int INP_SIZ_COMPANY_ID = 20;

    public static int INP_POS_DIVISION_CODE = 24;
    public static int INP_SIZ_DIVISION_CODE = 20;

    public static int INP_POS_SF_COUNTRY_CODE = 44;
    public static int INP_SIZ_SF_COUNTRY_CODE = 3;

    public static int INP_POS_SF_STATE_PROVINCE = 50;
    public static int INP_SIZ_SF_STATE_PROVINCE = 26;

    public static int INP_POS_SF_COUNTY_NAME = 76;
    public static int INP_SIZ_SF_COUNTY_NAME = 26;

    public static int INP_POS_SF_COUNTY_CODE = 102;
    public static int INP_SIZ_SF_COUNTY_CODE = 3;

    public static int INP_POS_SF_CITY = 105;
    public static int INP_SIZ_SF_CITY = 26;

    public static int INP_POS_SF_POSTAL_CODE = 131;
    public static int INP_SIZ_SF_POSTAL_CODE = 9;

    public static int INP_POS_SF_ZIP_EXTENSION = 140;
    public static int INP_SIZ_SF_ZIP_EXTENSION = 4;

    public static int INP_POS_SF_GEO_CODE = 144;
    public static int INP_SIZ_SF_GEO_CODE = 2;

    public static int INP_POS_ST_COUNTRY_CODE = 146;
    public static int INP_SIZ_ST_COUNTRY_CODE = 3;

    public static int INP_POS_ST_STATE_PROVINCE = 152;
    public static int INP_SIZ_ST_STATE_PROVINCE = 26;

    public static int INP_POS_ST_COUNTY_NAME = 178;
    public static int INP_SIZ_ST_COUNTY_NAME = 26;

    public static int INP_POS_ST_COUNTY_CODE = 204;
    public static int INP_SIZ_ST_COUNTY_CODE = 3;

    public static int INP_POS_ST_CITY = 207;
    public static int INP_SIZ_ST_CITY = 26;

    public static int INP_POS_ST_POSTAL_CODE = 233;
    public static int INP_SIZ_ST_POSTAL_CODE = 9;

    public static int INP_POS_ST_ZIP_EXTENSION = 242;
    public static int INP_SIZ_ST_ZIP_EXTENSION = 4;

    public static int INP_POS_ST_GEO_CODE = 246;
    public static int INP_SIZ_ST_GEO_CODE = 2;

    public static int INP_POS_ST_SEC_STATE_PROVINCE = 248;
    public static int INP_SIZ_ST_SEC_STATE_PROVINCE = 26;

    public static int INP_POS_ST_SEC_COUNTY_CODE = 274;
    public static int INP_SIZ_ST_SEC_COUNTY_CODE = 3;

    public static int INP_POS_ST_SEC_COUNTY_NAME = 277;
    public static int INP_SIZ_ST_SEC_COUNTY_NAME = 26;

    public static int INP_POS_ST_SEC_CITY = 303;
    public static int INP_SIZ_ST_SEC_CITY = 26;

    public static int INP_POS_ST_SEC_POSTAL_CODE = 329;
    public static int INP_SIZ_ST_SEC_POSTAL_CODE = 9;

    public static int INP_POS_ST_SEC_ZIP_CODE_EXTENSION = 338;
    public static int INP_SIZ_ST_SEC_ZIP_CODE_EXTENSION = 4;

    public static int INP_POS_ST_SEC_GEO_CODE = 342;
    public static int INP_SIZ_ST_SEC_GEO_CODE = 2;

    public static int INP_POS_POO_COUNTRY_CODE = 344;
    public static int INP_SIZ_POO_COUNTRY_CODE = 3;

    public static int INP_POS_POO_STATE_PROVINCE = 350;
    public static int INP_SIZ_POO_STATE_PROVINCE = 26;

    public static int INP_POS_POO_COUNTY_NAME = 376;
    public static int INP_SIZ_POO_COUNTY_NAME = 26;

    public static int INP_POS_POO_COUNTY_CODE = 402;
    public static int INP_SIZ_POO_COUNTY_CODE = 3;

    public static int INP_POS_POO_CITY = 405;
    public static int INP_SIZ_POO_CITY = 26;

    public static int INP_POS_POO_POSTAL_CODE = 431;
    public static int INP_SIZ_POO_POSTAL_CODE = 9;

    public static int INP_POS_POO_ZIP_EXTENSION = 440;
    public static int INP_SIZ_POO_ZIP_EXTENSION = 4;

    public static int INP_POS_POO_GEO_CODE = 444;
    public static int INP_SIZ_POO_GEO_CODE = 2;

    public static int INP_POS_POA_COUNTRY_CODE = 446;
    public static int INP_SIZ_POA_COUNTRY_CODE = 3;

    public static int INP_POS_POA_STATE_PROVINCE = 452;
    public static int INP_SIZ_POA_STATE_PROVINCE = 26;

    public static int INP_POS_POA_COUNTY_NAME = 478;
    public static int INP_SIZ_POA_COUNTY_NAME = 26;

    public static int INP_POS_POA_COUNTY_CODE = 504;
    public static int INP_SIZ_POA_COUNTY_CODE = 3;

    public static int INP_POS_POA_CITY = 507;
    public static int INP_SIZ_POA_CITY = 26;

    public static int INP_POS_POA_POSTAL_CODE = 533;
    public static int INP_SIZ_POA_POSTAL_CODE = 9;

    public static int INP_POS_POA_ZIP_EXTENSION = 542;
    public static int INP_SIZ_POA_ZIP_EXTENSION = 4;

    public static int INP_POS_POA_GEO_CODE = 546;
    public static int INP_SIZ_POA_GEO_CODE = 2;

    public static int INP_POS_BILL_TO_COUNTRY_CODE = 548;
    public static int INP_SIZ_BILL_TO_COUNTRY_CODE = 3;

    public static int INP_POS_BILL_TO_STATE_PROVINCE = 554;
    public static int INP_SIZ_BILL_TO_STATE_PROVINCE = 26;

    public static int INP_POS_BILL_TO_COUNTY_NAME = 580;
    public static int INP_SIZ_BILL_TO_COUNTY_NAME = 26;

    public static int INP_POS_BILL_TO_CITY = 609;
    public static int INP_SIZ_BILL_TO_CITY = 26;

    public static int INP_POS_BILL_TO_POSTAL_CODE = 635;
    public static int INP_SIZ_BILL_TO_POSTAL_CODE = 9;

    public static int INP_POS_BILL_TO_GEO_CODE = 648;
    public static int INP_SIZ_BILL_TO_GEO_CODE = 2;

    public static int INP_POS_POINT_OF_TITLE_PASSAGE = 650;
    public static int INP_SIZ_POINT_OF_TITLE_PASSAGE = 1;

    public static int INP_POS_TAXING_LOCATION = 651;
    public static int INP_SIZ_TAXING_LOCATION = 1;

    public static int INP_POS_CALCULATION_MODE = 652;
    public static int INP_SIZ_CALCULATION_MODE = 1;

    public static int INP_POS_TRANSACTION_TYPE = 653;
    public static int INP_SIZ_TRANSACTION_TYPE = 1;

    public static int INP_POS_WT_CODE = 654;
    public static int INP_SIZ_WT_CODE = 2;

    public static int INP_POS_TYPE_OF_TAX = 656;
    public static int INP_SIZ_TYPE_OF_TAX = 1;

    public static int INP_POS_INVOICE_DATE = 657;
    public static int INP_SIZ_INVOICE_DATE = 8;

    public static int INP_POS_DELIVERY_DATE = 665;
    public static int INP_SIZ_DELIVERY_DATE = 8;

    public static int INP_POS_MODE_OF_TRANSPORT = 673;
    public static int INP_SIZ_MODE_OF_TRANSPORT = 2;

    public static int INP_POS_COMMODITY_PRODUCT_CODE = 675;
    public static int INP_SIZ_COMMODITY_PRODUCT_CODE = 25;

    public static int INP_POS_CREDIT_INDICATOR = 700;
    public static int INP_SIZ_CREDIT_INDICATOR = 1;

    public static int INP_POS_REASON_CODE_COUNTRY = 701;
    public static int INP_SIZ_REASON_CODE_COUNTRY = 2;

    public static int INP_POS_REASON_CODE_STATE_PROVINCE = 703;
    public static int INP_SIZ_REASON_CODE_STATE_PROVINCE = 2;

    public static int INP_POS_REASON_CODE_COUNTY = 705;
    public static int INP_SIZ_REASON_CODE_COUNTY = 2;

    public static int INP_POS_REASON_CODE_CITY = 707;
    public static int INP_SIZ_REASON_CODE_CITY = 2;

    public static int INP_POS_REG_NUM_COUNTRY = 709;
    public static int INP_SIZ_REG_NUM_COUNTRY = 25;

    public static int INP_POS_REG_NUM_STATE = 734;
    public static int INP_SIZ_REG_NUM_STATE = 25;

    public static int INP_POS_REG_NUM_COUNTY = 759;
    public static int INP_SIZ_REG_NUM_COUNTY = 25;

    public static int INP_POS_REG_NUM_CITY = 784;
    public static int INP_SIZ_REG_NUM_CITY = 25;

    public static int INP_POS_EXEMPT_IND_ALL = 809;
    public static int INP_SIZ_EXEMPT_IND_ALL = 1;

    public static int INP_POS_EXEMPT_IND_COUNTRY = 810;
    public static int INP_SIZ_EXEMPT_IND_COUNTRY = 1;

    public static int INP_POS_EXEMPT_IND_PROVINCE = 812;
    public static int INP_SIZ_EXEMPT_IND_PROVINCE = 1;

    public static int INP_POS_EXEMPT_IND_COUNTY = 813;
    public static int INP_SIZ_EXEMPT_IND_COUNTY = 1;

    public static int INP_POS_EXEMPT_IND_CITY = 814;
    public static int INP_SIZ_EXEMPT_IND_CITY = 1;

    public static int INP_POS_EXEMPT_IND_SEC_STATE = 815;
    public static int INP_SIZ_EXEMPT_IND_SEC_STATE = 1;

    public static int INP_POS_EXEMPT_IND_SEC_COUNTY = 816;
    public static int INP_SIZ_EXEMPT_IND_SEC_COUNTY = 1;

    public static int INP_POS_EXEMPT_IND_SEC_CITY = 817;
    public static int INP_SIZ_EXEMPT_IND_SEC_CITY = 1;

    public static int INP_POS_INVOICE_NUMBER = 819;
    public static int INP_SIZ_INVOICE_NUMBER = 20;

    public static int INP_POS_PRIMARY_CURRENCY_CODE = 839;
    public static int INP_SIZ_PRIMARY_CURRENCY_CODE = 3;

    public static int INP_POS_AUDIT_FILE_INDICATOR = 845;
    public static int INP_SIZ_AUDIT_FILE_INDICATOR = 1;

    public static int INP_POS_EXEMPTION_USE_FLAG = 846;
    public static int INP_SIZ_EXEMPTION_USE_FLAG = 1;

    public static int INP_POS_EXEMPTION_CRITERION_FLAG = 847;
    public static int INP_SIZ_EXEMPTION_CRITERION_FLAG = 1;

    public static int INP_POS_EXEMPTION_PROCESS_FLAG = 848;
    public static int INP_SIZ_EXEMPTION_PROCESS_FLAG = 1;

    public static int INP_POS_CUSTOMER_NUMBER = 849;
    public static int INP_SIZ_CUSTOMER_NUMBER = 20;

    public static int INP_POS_CONTRACT_JOB_NUMBER = 869;
    public static int INP_SIZ_CONTRACT_JOB_NUMBER = 10;

    public static int INP_POS_BUSINESS_LOCATION_CODE = 889;
    public static int INP_SIZ_BUSINESS_LOCATION_CODE = 13;

    public static int INP_POS_ITEM_DESCRIPTION = 917;
    public static int INP_SIZ_ITEM_DESCRIPTION = 100;

    public static int INP_POS_END_INVOICE_PROCESSING = 1017;
    public static int INP_SIZ_END_INVOICE_PROCESSING = 1;

    public static int INP_POS_INVOICE_SUMMARY_INDICATOR = 1018;
    public static int INP_SIZ_INVOICE_SUMMARY_INDICATOR = 1;

    public static int INP_POS_RECOVERABLE_INDICATOR = 1019;
    public static int INP_SIZ_RECOVERABLE_INDICATOR = 1;

    public static int INP_POS_ACCOUNTING_REFERENCE = 1021;
    public static int INP_SIZ_ACCOUNTING_REFERENCE = 15;

    public static int INP_POS_ORIGINAL_DOC_NUMBER = 1036;
    public static int INP_SIZ_ORIGINAL_DOC_NUMBER = 20;

    public static int INP_POS_DOCUMENT_TYPE = 1056;
    public static int INP_SIZ_DOCUMENT_TYPE = 2;

    public static int INP_POS_AGENT_INDICATOR = 1058;
    public static int INP_SIZ_AGENT_INDICATOR = 1;

    public static int INP_POS_AGENT_BRANCH_ID = 1059;
    public static int INP_SIZ_AGENT_BRANCH_ID = 3;

    public static int INP_POS_COUNTRY_OF_ORIGIN = 1062;
    public static int INP_SIZ_COUNTRY_OF_ORIGIN = 3;

    public static int INP_POS_REGION_OF_ORIGIN = 1065;
    public static int INP_SIZ_REGION_OF_ORIGIN = 2;

    public static int INP_POS_DELIVERY_TERMS = 1067;
    public static int INP_SIZ_DELIVERY_TERMS = 5;

    public static int INP_POS_PORT_OF_LOADING_UNLOADING = 1072;
    public static int INP_SIZ_PORT_OF_LOADING_UNLOADING = 5;

    public static int INP_POS_NATURE_OF_TRANSACTION_CODE = 1077;
    public static int INP_SIZ_NATURE_OF_TRANSACTION_CODE = 2;

    public static int INP_POS_DISTANCE_SALE_INDICATOR = 1079;
    public static int INP_SIZ_DISTANCE_SALE_INDICATOR = 1;

    public static int INP_POS_STATISTICAL_PROCEDURE = 1087;
    public static int INP_SIZ_STATISTICAL_PROCEDURE = 6;

    public static int INP_POS_SUPPLEMENTARY_UNITS = 1093;
    public static int INP_SIZ_SUPPLEMENTARY_UNITS = 11;

    public static int INP_POS_CORRECTIONS_CODE = 1104;
    public static int INP_SIZ_CORRECTIONS_CODE = 1;

    public static int INP_POS_REVERSE_CHARGE_INDICATOR = 1105;
    public static int INP_SIZ_REVERSE_CHARGE_INDICATOR = 1;

    public static int INP_POS_NEW_MEANS_OF_TRANSPORTATION_INDICATOR = 1106;
    public static int INP_SIZ_NEW_MEANS_OF_TRANSPORTATION_INDICATOR = 1;

    public static int INP_POS_BOE_CODE = 1107;
    public static int INP_SIZ_BOE_CODE = 2;

    public static int INP_POS_AFFILIATION = 1109;
    public static int INP_SIZ_AFFILIATION = 1;

    public static int INP_POS_CSA = 1110;
    public static int INP_SIZ_CSA = 1;

    public static int INP_POS_CUSTOMER_NAME = 1113;
    public static int INP_SIZ_CUSTOMER_NAME = 20;

    public static int INP_POS_TAXSEL_PARM = 1133;
    public static int INP_SIZ_TAXSEL_PARM = 1;

    public static int INP_POS_PART_NUMBER = 1134;
    public static int INP_SIZ_PART_NUMBER = 20;

    public static int INP_POS_FISCAL_DATE = 1154;
    public static int INP_SIZ_FISCAL_DATE = 8;

    public static int INP_POS_MISC_INFO = 1162;
    public static int INP_SIZ_MISC_INFO = 50;

    public static int INP_POS_SERVICE_INDICATOR = 1257;
    public static int INP_SIZ_SERVICE_INDICATOR = 1;

    public static int INP_POS_PRODUCT_CODE_CONVERSION = 1262;
    public static int INP_SIZ_PRODUCT_CODE_CONVERSION = 1;

    public static int INP_POS_STATE_TAX_TYPE = 1266;
    public static int INP_SIZ_STATE_TAX_TYPE = 1;

    public static int INP_POS_COUNTY_TAX_TYPE = 1267;
    public static int INP_SIZ_COUNTY_TAX_TYPE = 1;

    public static int INP_POS_CITY_TAX_TYPE = 1268;
    public static int INP_SIZ_CITY_TAX_TYPE = 1;

    public static int INP_POS_SECONDARY_STATE_TAX_TYPE = 1269;
    public static int INP_SIZ_SECONDARY_STATE_TAX_TYPE = 1;

    public static int INP_POS_SECONDARY_COUNTY_TAX_TYPE = 1270;
    public static int INP_SIZ_SECONDARY_COUNTY_TAX_TYPE = 1;

    public static int INP_POS_SECONDARY_CITY_TAX_TYPE = 1271;
    public static int INP_SIZ_SECONDARY_CITY_TAX_TYPE = 1;

    public static int INP_POS_NO_TAX_INDICATOR_ALL = 1273;
    public static int INP_SIZ_NO_TAX_INDICATOR_ALL = 1;

    public static int INP_POS_STATE_PROVINCE_NO_TAX_INDICATOR = 1276;
    public static int INP_SIZ_STATE_PROVINCE_NO_TAX_INDICATOR = 1;

    public static int INP_POS_COUNTY_NO_TAX_INDICATOR = 1277;
    public static int INP_SIZ_COUNTY_NO_TAX_INDICATOR = 1;

    public static int INP_POS_CITY_NO_TAX_INDICATOR = 1278;
    public static int INP_SIZ_CITY_NO_TAX_INDICATOR = 1;

    public static int INP_POS_SEC_COUNTY_NO_TAX_INDICATOR = 1279;
    public static int INP_SIZ_SEC_COUNTY_NO_TAX_INDICATOR = 1;

    public static int INP_POS_SEC_CITY_NO_TAX_INDICATOR = 1280;
    public static int INP_SIZ_SEC_CITY_NO_TAX_INDICATOR = 1;

    public static int INP_POS_SEC_STATE_PROVINCE_NO_TAX_INDICATOR = 1281;
    public static int INP_SIZ_SEC_STATE_PROVINCE_NO_TAX_INDICATOR = 1;

    public static int INP_POS_SELLER_REGISTRATION_NUMBER = 1294;
    public static int INP_SIZ_SELLER_REGISTRATION_NUMBER = 25;

    public static int INP_POS_BUYER_REGISTRATION_NUMBER = 1319;
    public static int INP_SIZ_BUYER_REGISTRATION_NUMBER = 25;

    public static int INP_POS_REPORT_EXCLUSION_INDICATOR = 1344;
    public static int INP_SIZ_REPORT_EXCLUSION_INDICATOR = 1;

    public static int INP_POS_OVERRIDE_TAX_AMOUNT_COUNTRY = 2044;
    public static int INP_SIZ_OVERRIDE_TAX_AMOUNT_COUNTRY = 14;

    public static int INP_POS_OVERRIDE_TAX_AMOUNT_STATE_PROVINCE = 2072;
    public static int INP_SIZ_OVERRIDE_TAX_AMOUNT_STATE_PROVINCE = 14;

    public static int INP_POS_OVERRIDE_TAX_AMOUNT_COUNTY = 2086;
    public static int INP_SIZ_OVERRIDE_TAX_AMOUNT_COUNTY = 14;

    public static int INP_POS_OVERRIDE_TAX_AMOUNT_CITY = 2100;
    public static int INP_SIZ_OVERRIDE_TAX_AMOUNT_CITY = 14;

    public static int INP_POS_OVERRIDE_TAX_AMOUNT_SEC_STATE = 2128;
    public static int INP_SIZ_OVERRIDE_TAX_AMOUNT_SEC_STATE = 14;

    public static int INP_POS_OVERRIDE_TAX_AMOUNT_SEC_COUNTY = 2142;
    public static int INP_SIZ_OVERRIDE_TAX_AMOUNT_SEC_COUNTY = 14;

    public static int INP_POS_OVERRIDE_TAX_AMOUNT_SEC_CITY = 2156;
    public static int INP_SIZ_OVERRIDE_TAX_AMOUNT_SEC_CITY = 14;

    public static int INP_POS_OVERRIDE_RATE_COUNTRY = 2170;
    public static int INP_SIZ_OVERRIDE_RATE_COUNTRY = 7;

    public static int INP_POS_OVERRIDE_RATE_STATE_PROVINCE = 2184;
    public static int INP_SIZ_OVERRIDE_RATE_STATE_PROVINCE = 7;

    public static int INP_POS_OVERRIDE_RATE_COUNTY = 2191;
    public static int INP_SIZ_OVERRIDE_RATE_COUNTY = 7;

    public static int INP_POS_OVERRIDE_RATE_CITY = 2198;
    public static int INP_SIZ_OVERRIDE_RATE_CITY = 7;

    public static int INP_POS_OVERRIDE_RATE_SEC_STATE = 2212;
    public static int INP_SIZ_OVERRIDE_RATE_SEC_STATE = 7;

    public static int INP_POS_OVERRIDE_RATE_SEC_COUNTY = 2219;
    public static int INP_SIZ_OVERRIDE_RATE_SEC_COUNTY = 7;

    public static int INP_POS_OVERRIDE_RATE_SEC_CITY = 2226;
    public static int INP_SIZ_OVERRIDE_RATE_SEC_CITY = 7;

    public static int INP_POS_NUMBER_OF_ITEMS = 2233;
    public static int INP_SIZ_NUMBER_OF_ITEMS = 7;

    public static int INP_POS_INVOICE_LINE_NUMBER = 2240;
    public static int INP_SIZ_INVOICE_LINE_NUMBER = 5;

    public static int INP_POS_LINE_ITEM_AMOUNT = 2245;
    public static int INP_SIZ_LINE_ITEM_AMOUNT = 14;

    public static int INP_POS_TAX_AMOUNT = 2259;
    public static int INP_SIZ_TAX_AMOUNT = 14;

    public static int INP_POS_DISCOUNT_AMOUNT = 2273;
    public static int INP_SIZ_DISCOUNT_AMOUNT = 14;

    public static int INP_POS_FREIGHT_AMOUNT = 2287;
    public static int INP_SIZ_FREIGHT_AMOUNT = 14;

    public static int INP_POS_INSURANCE_AMOUNT_LOCAL = 2301;
    public static int INP_SIZ_INSURANCE_AMOUNT_LOCAL = 14;

    public static int INP_POS_EXEMPT_AMOUNT_COUNTRY = 2315;
    public static int INP_SIZ_EXEMPT_AMOUNT_COUNTRY = 14;

    public static int INP_POS_EXEMPT_AMOUNT_STATE_PROVINCE = 2343;
    public static int INP_SIZ_EXEMPT_AMOUNT_STATE_PROVINCE = 14;

    public static int INP_POS_EXEMPT_AMOUNT_COUNTY = 2357;
    public static int INP_SIZ_EXEMPT_AMOUNT_COUNTY = 14;

    public static int INP_POS_EXEMPT_AMOUNT_CITY = 2371;
    public static int INP_SIZ_EXEMPT_AMOUNT_CITY = 14;

    public static int INP_POS_EXEMPT_AMOUNT_SEC_STATE = 2399;
    public static int INP_SIZ_EXEMPT_AMOUNT_SEC_STATE = 14;

    public static int INP_POS_EXEMPT_AMOUNT_SEC_COUNTY = 2413;
    public static int INP_SIZ_EXEMPT_AMOUNT_SEC_COUNTY = 14;

    public static int INP_POS_EXEMPT_AMOUNT_SEC_CITY = 2427;
    public static int INP_SIZ_EXEMPT_AMOUNT_SEC_CITY = 14;

    public static int INP_POS_INSURANCE_AMOUNT_FOREIGN = 2441;
    public static int INP_SIZ_INSURANCE_AMOUNT_FOREIGN = 14;

    public static int INP_POS_SHIPPING_AMOUNT_FOREIGN = 2455;
    public static int INP_SIZ_SHIPPING_AMOUNT_FOREIGN = 14;

    public static int INP_POS_BASIS_PERCENT = 2497;
    public static int INP_SIZ_BASIS_PERCENT = 7;

    public static int INP_POS_NET_MASS_VOLUME = 2504;
    public static int INP_SIZ_NET_MASS_VOLUME = 15;

    public static int INP_POS_RECOVERABLE_PERCENTAGE = 2564;
    public static int INP_SIZ_RECOVERABLE_PERCENTAGE = 7;

    /*
     * Define the constants for "General Output Record Layout".
     * The first OUT_PARAM_SIZE characters returned from
     * the API are some general information.
     */

    public static int OUT_POS_RECORD_COUNT = 0;
    public static int OUT_SIZ_RECORD_COUNT = 6;

    public static int OUT_POS_GENERAL = 1;
    public static int OUT_SIZ_GENERAL = 4;

    public static int OUT_POS_FEDERAL = 5;
    public static int OUT_SIZ_FEDERAL = 4;

    public static int OUT_POS_TERRITORY = 9;
    public static int OUT_SIZ_TERRITORY = 4;

    public static int OUT_POS_STATE = 13;
    public static int OUT_SIZ_STATE = 4;

    public static int OUT_POS_COUNTY = 17;
    public static int OUT_SIZ_COUNTY = 4;

    public static int OUT_POS_CITY = 21;
    public static int OUT_SIZ_CITY = 4;

    public static int OUT_POS_SECSTATE = 25;
    public static int OUT_SIZ_SECSTATE = 4;

    public static int OUT_POS_SECCOUNTY = 29;
    public static int OUT_SIZ_SECCOUNTY = 4;

    public static int OUT_POS_SECCITY = 33;
    public static int OUT_SIZ_SECCITY = 4;

    /*
     * Define the constants for the Output Buffer. When the
     * Output buffer is returned from the API, it actual starts
     * at position OUT_PARAM_SIZE + 1 of the returned buffer.
     * We take that into account after returning.
     */

    public static int OUT_POS_SYSTEM_INDICATOR = 0;
    public static int OUT_SIZ_SYSTEM_INDICATOR = 1;

    public static int OUT_POS_UTL_GEN_COMPLETION_CODE_DESCRIPTION = 73;
    public static int OUT_SIZ_UTL_GEN_COMPLETION_CODE_DESCRIPTION = 200;

    public static int OUT_POS_UTL_GEN_COMPLETION_CODE = 1;
    public static int OUT_SIZ_UTL_GEN_COMPLETION_CODE = 4;

    public static int OUT_POS_UTL_FED_COMPLETION_CODE = 5;
    public static int OUT_SIZ_UTL_FED_COMPLETION_CODE = 4;

    public static int OUT_POS_SYSTEM_DATE = 273;
    public static int OUT_SIZ_SYSTEM_DATE = 8;

    public static int OUT_POS_TRANSACTION_NUMBER = 281;
    public static int OUT_SIZ_TRANSACTION_NUMBER = 10;

    public static int OUT_POS_LINE_ITEM_ID = 291;
    public static int OUT_SIZ_LINE_ITEM_ID = 5;

    public static int OUT_POS_TAX_JUR_LOC = 298;
    public static int OUT_SIZ_TAX_JUR_LOC = 1;

    public static int OUT_POS_JUR_STATE = 305;
    public static int OUT_SIZ_JUR_STATE = 26;

    public static int OUT_POS_JUR_COUNTY_CODE = 331;
    public static int OUT_SIZ_JUR_COUNTY_CODE = 3;

    public static int OUT_POS_JUR_COUNTY = 569;
    public static int OUT_SIZ_JUR_COUNTY = 26;

    public static int OUT_POS_JUR_CITY = 334;
    public static int OUT_SIZ_JUR_CITY = 26;

    public static int OUT_POS_JUR_POSTAL_CODE = 360;
    public static int OUT_SIZ_JUR_POSTAL_CODE = 9;

    public static int OUT_POS_JUR_GEO_CODE = 373;
    public static int OUT_SIZ_JUR_GEO_CODE = 2;

    public static int OUT_POS_JUR_SEC_STATE = 375;
    public static int OUT_SIZ_JUR_SEC_STATE = 26;

    public static int OUT_POS_JUR_SEC_COUNTY_CODE = 401;
    public static int OUT_SIZ_JUR_SEC_COUNTY_CODE = 3;

    public static int OUT_POS_JUR_SEC_COUNTY = 595;
    public static int OUT_SIZ_JUR_SEC_COUNTY = 26;

    public static int OUT_POS_JUR_SEC_CITY = 404;
    public static int OUT_SIZ_JUR_SEC_CITY = 26;

    public static int OUT_POS_JUR_SEC_POSTAL_CODE = 430;
    public static int OUT_SIZ_JUR_SEC_POSTAL_CODE = 9;

    public static int OUT_POS_JUR_SEC_GEOCODE = 443;
    public static int OUT_SIZ_JUR_SEC_GEOCODE = 2;

    public static int OUT_POS_TAX_TYPE_STATE = 446;
    public static int OUT_SIZ_TAX_TYPE_STATE = 1;

    public static int OUT_POS_TAX_TYPE_COUNTY = 447;
    public static int OUT_SIZ_TAX_TYPE_COUNTY = 1;

    public static int OUT_POS_TAX_TYPE_CITY = 448;
    public static int OUT_SIZ_TAX_TYPE_CITY = 1;

    public static int OUT_POS_TAX_TYPE_SEC_STATE = 449;
    public static int OUT_SIZ_TAX_TYPE_SEC_STATE = 1;

    public static int OUT_POS_TAX_TYPE_SEC_COUNTY = 450;
    public static int OUT_SIZ_TAX_TYPE_SEC_COUNTY = 1;

    public static int OUT_POS_TAX_TYPE_SEC_CITY = 451;
    public static int OUT_SIZ_TAX_TYPE_SEC_CITY = 1;

    // begin of step record;

    public static int OUT_POS_STEP_STATUS_COUNTRY = 561;
    public static int OUT_SIZ_STEP_STATUS_COUNTRY = 1;

    public static int OUT_POS_STEP_COMMENT_COUNTRY = 562;
    public static int OUT_SIZ_STEP_COMMENT_COUNTRY = 1;

    public static int OUT_POS_STEP_STATUS_STATE = 563;
    public static int OUT_SIZ_STEP_STATUS_STATE = 1;

    public static int OUT_POS_STEP_COMMENT_STATE = 564;
    public static int OUT_SIZ_STEP_COMMENT_STATE = 1;

    public static int OUT_POS_STEP_STATUS_CITY = 565;
    public static int OUT_SIZ_STEP_STATUS_CITY = 1;

    public static int OUT_POS_STEP_COMMENT_CITY = 566;
    public static int OUT_SIZ_STEP_COMMENT_CITY = 1;

    // end of step record;

    public static int OUT_POS_LINE_AMOUNT = 1319;
    public static int OUT_SIZ_LINE_AMOUNT = 14;
    ;
    public static int OUT_POS_TAX_AMT_COUNTRY = 1333;
    public static int OUT_SIZ_TAX_AMT_COUNTRY = 14;

    public static int OUT_POS_TAX_AMT_STATE = 1361;
    public static int OUT_SIZ_TAX_AMT_STATE = 14;

    public static int OUT_POS_TAX_AMT_COUNTY = 1375;
    public static int OUT_SIZ_TAX_AMT_COUNTY = 14;

    public static int OUT_POS_TAX_AMT_CITY = 1389;
    public static int OUT_SIZ_TAX_AMT_CITY = 14;

    public static int OUT_POS_TAX_AMT_SEC_STATE = 1403;
    public static int OUT_SIZ_TAX_AMT_SEC_STATE = 14;

    public static int OUT_POS_TAX_AMT_SEC_COUNTY = 1417;
    public static int OUT_SIZ_TAX_AMT_SEC_COUNTY = 14;

    public static int OUT_POS_TAX_AMT_SEC_CITY = 1431;
    public static int OUT_SIZ_TAX_AMT_SEC_CITY = 14;

    public static int OUT_POS_TAX_RATE_COUNTRY = 1585;
    public static int OUT_SIZ_TAX_RATE_COUNTRY = 7;

    public static int OUT_POS_TAX_RATE_STATE = 1599;
    public static int OUT_SIZ_TAX_RATE_STATE = 7;

    public static int OUT_POS_TAX_RATE_COUNTY = 1606;
    public static int OUT_SIZ_TAX_RATE_COUNTY = 7;

    public static int OUT_POS_TAX_RATE_CITY = 1613;
    public static int OUT_SIZ_TAX_RATE_CITY = 7;

    public static int OUT_POS_TAX_RATE_SEC_STATE = 1620;
    public static int OUT_SIZ_TAX_RATE_SEC_STATE = 7;

    public static int OUT_POS_TAX_RATE_SEC_COUNTY = 1627;
    public static int OUT_SIZ_TAX_RATE_SEC_COUNTY = 7;

    public static int OUT_POS_TAX_RATE_SEC_CITY = 1634;
    public static int OUT_SIZ_TAX_RATE_SEC_CITY = 7;

    public static int OUT_SYSTEM_INDICATOR = 0;
    public static int OUT_SEVERITY_CODE = 1;
    public static int OUT_RETURN_DESCRIPTION = 2;
    public static int OUT_UTL_GEN_COMPLETION_CODE_DESCRIPTION = 3;
    public static int OUT_SYSTEM_DATE = 4;
    public static int OUT_TRANSACTION_NUMBER = 5;
    public static int OUT_LINE_ITEM_ID = 6;
    public static int OUT_TAX_JUR_LOC = 7;
    public static int OUT_JUR_STATE = 8;
    public static int OUT_JUR_COUNTY_CODE = 9;
    public static int OUT_JUR_COUNTY = 10;
    public static int OUT_JUR_CITY = 11;
    public static int OUT_JUR_POSTAL_CODE = 12;
    public static int OUT_JUR_GEO_CODE = 13;
    public static int OUT_JUR_SEC_STATE = 14;
    public static int OUT_JUR_SEC_COUNTY_CODE = 15;
    public static int OUT_JUR_SEC_COUNTY = 16;
    public static int OUT_JUR_SEC_CITY = 17;
    public static int OUT_JUR_SEC_POSTAL_CODE = 18;
    public static int OUT_JUR_SEC_GEOCODE = 19;
    public static int OUT_TAX_TYPE_STATE = 20;
    public static int OUT_TAX_TYPE_COUNTY = 21;
    public static int OUT_TAX_TYPE_CITY = 22;
    public static int OUT_TAX_TYPE_SEC_STATE = 23;
    public static int OUT_TAX_TYPE_SEC_COUNTY = 24;
    public static int OUT_TAX_TYPE_SEC_CITY = 25;
    public static int OUT_LINE_AMOUNT = 26;
    public static int OUT_TAX_AMT_TOTAL = 27;
    public static int OUT_TAX_AMT_COUNTRY = 28;
    public static int OUT_TAX_AMT_STATE = 29;
    public static int OUT_TAX_AMT_COUNTY = 30;
    public static int OUT_TAX_AMT_CITY = 31;
    public static int OUT_TAX_AMT_SEC_STATE = 32;
    public static int OUT_TAX_AMT_SEC_COUNTY = 33;
    public static int OUT_TAX_AMT_SEC_CITY = 34;
    public static int OUT_TAX_RATE_TOTAL = 35;
    public static int OUT_TAX_RATE_COUNTRY = 36;
    public static int OUT_TAX_RATE_STATE = 37;
    public static int OUT_TAX_RATE_COUNTY = 38;
    public static int OUT_TAX_RATE_CITY = 39;
    public static int OUT_TAX_RATE_SEC_STATE = 40;
    public static int OUT_TAX_RATE_SEC_COUNTY = 41;
    public static int OUT_TAX_RATE_SEC_CITY = 42;

    public static int OUT_UTL_GEN_COMPLETION_CODE = 43;
    public static int OUT_UTL_FED_COMPLETION_CODE = 44;

    public static int OUT_STEP_STATUS_COUNTRY = 45;
    public static int OUT_STEP_COMMENT_COUNTRY = 46;
    public static int OUT_STEP_STATUS_STATE = 47;
    public static int OUT_STEP_COMMENT_STATE = 48;
    public static int OUT_STEP_STATUS_CITY = 49;
    public static int OUT_STEP_COMMENT_CITY = 50;

}
