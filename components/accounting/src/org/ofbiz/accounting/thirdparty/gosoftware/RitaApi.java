/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.accounting.thirdparty.gosoftware;

import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LinkedMap;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class RitaApi {

    public static final String module = RitaApi.class.getName();
    public static final String xschema = "x-schema:..\\dtd\\stnd.xdr";
    public static final String rootElement = "XML_FILE";
    public static final String reqElement = "XML_REQUEST";

    // request fields
    public static final String FUNCTION_TYPE = "FUNCTION_TYPE";
    public static final String PAYMENT_TYPE = "PAYMENT_TYPE";
    public static final String USER_ID = "USER_ID";
    public static final String USER_PW = "USER_PW";
    public static final String COMMAND = "COMMAND";
    public static final String CLIENT_ID = "CLIENT_ID";

    public static final String ACCT_NUM = "ACCT_NUM";
    public static final String EXP_MONTH = "EXP_MONTH";
    public static final String EXP_YEAR = "EXP_YEAR";
    public static final String TRANS_AMOUNT = "TRANS_AMOUNT";

    public static final String CARDHOLDER = "CARDHOLDER";
    public static final String TRACK_DATA = "TRACK_DATA";
    public static final String INVOICE = "INVOICE";
    public static final String PRESENT_FLAG = "PRESENT_FLAG";
    public static final String CUSTOMER_STREET = "CUSTOMER_STREET";
    public static final String CUSTOMER_ZIP = "CUSTOMER_ZIP";
    public static final String CVV2 = "CVV2";
    public static final String TAX_AMOUNT = "TAX_AMOUNT";
    public static final String PURCHASE_ID = "PURCHASE_ID";
    public static final String FORCE_FLAG = "FORCE_FLAG";
    public static final String ORIG_SEQ_NUM = "ORIG_SEQ_NUM";

    // response fields
    public static final String TERMINATION_STATUS = "TERMINATION_STATUS";
    public static final String INTRN_SEQ_NUM = "INTRN_SEQ_NUM";
    public static final String RESULT = "RESULT";
    public static final String RESULT_CODE = "RESULT_CODE";
    public static final String RESPONSE_TEXT = "RESPONSE_TEXT";

    public static final String AUTH_CODE = "AUTH_CODE";
    public static final String AVS_CODE = "AVS_CODE";
    public static final String CVV2_CODE = "CVV2_CODE";
    public static final String REFERENCE = "REFERENCE";
    public static final String TRANS_DATE = "TRANS_DATE";
    public static final String TRANS_TIME = "TRANS_TIME";
    public static final String ORIG_TRANS_AMOUNT = "ORIG_TRANS_AMOUNT";

    // IN/OUT validation array
    protected static final String[] validOut = { TERMINATION_STATUS, INTRN_SEQ_NUM, RESULT, RESULT_CODE, RESPONSE_TEXT,
                                                 AUTH_CODE, AVS_CODE, CVV2_CODE, REFERENCE, TRANS_DATE, TRANS_TIME,
                                                 ORIG_TRANS_AMOUNT };

    protected static final String[] validIn = { FUNCTION_TYPE, PAYMENT_TYPE, USER_ID, USER_PW, COMMAND, CLIENT_ID,
                                                ACCT_NUM, EXP_MONTH, EXP_YEAR, TRANS_AMOUNT, CARDHOLDER, TRACK_DATA,
                                                INVOICE, PRESENT_FLAG, CUSTOMER_STREET, CUSTOMER_ZIP, CVV2, TAX_AMOUNT,
                                                PURCHASE_ID, FORCE_FLAG, ORIG_TRANS_AMOUNT };

    // mode definition
    protected static final int MODE_OUT = 20;
    protected static final int MODE_IN = 10;

    // instance variables
    protected LinkedMap document = null;
    protected String host = null;
    protected int port = 0;
    protected int mode = 0;

    public RitaApi(Map document) {
        this.document = new LinkedMap(document);
        this.mode = MODE_OUT;
    }

    public RitaApi() {
        this.document = new LinkedMap();
        this.mode = MODE_IN;
    }

    public RitaApi(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

    public void set(String name, Object value) {
        if (!checkIn(name)) {
            throw new IllegalArgumentException("Field [" + name + "] is not a valid IN parameter");
        }

        String objString = null;
        try {
            objString = (String) ObjectType.simpleTypeConvert(value, "java.lang.String", null, null);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            throw new IllegalArgumentException("Unable to convert value to String");
        } catch (ClassCastException e) {
            Debug.logError(e, module);
            throw new IllegalArgumentException("Unable to convert value to String");
        }
        if (objString == null && value != null) {
            throw new IllegalArgumentException("Unable to convert value to String");
        } else if (objString == null) {
            objString = "";
        }

        // append to the XML document
        document.put(name, objString);
    }

    public String get(String name) {
        if (!checkOut(name)) {
            throw new IllegalArgumentException("Field [" + name + "] is not a valid OUT parameter");
        }

        return (String) document.get(name);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        MapIterator i = document.mapIterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            String value = (String) i.getValue();
            buf.append(name);
            buf.append(" ");
            buf.append(value);
            buf.append("\r\n");
        }
        buf.append(".\r\n");
        return buf.toString();
    }

    public Map getDocument() {
        return this.document;
    }

    public RitaApi send() throws IOException, GeneralException {
        if (host == null || port == 0) {
            throw new GeneralException("TCP transaction not supported without valid host/port configuration");
        }

        Debug.log("Sending - \n" + this.toString(), module);
        if (mode == MODE_IN) {
            Socket sock = new Socket(host, port);

            // get the streams
            BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintStream ps = new PrintStream(sock.getOutputStream());

            // send the request
            ps.print(this.toString());
            ps.flush();

            // the output map
            LinkedMap docMap = new LinkedMap();
            String line;

            // read the response
            while ((line = br.readLine()) != null) {
                Debug.log(line, module);
                if (!line.trim().equals(".")) {
                    String[] lineSplit = line.trim().split(" ");
                    if (lineSplit != null && lineSplit.length == 2) {
                        docMap.put(lineSplit[0], lineSplit[1]);
                    } else {
                        Debug.logWarning("Line split error - " + line, module);
                    }
                } else {
                    break;
                }
            }
            Debug.log("Reading finished.", module);

            // send session finished signal
            ps.print("..\r\n");
            ps.flush();

            // close the streams
            ps.close();
            br.close();

            RitaApi out = new RitaApi(docMap);
            return out;
        } else {
            throw new IllegalStateException("Cannot send output object");
        }
    }

    private boolean checkIn(String name) {
        for (int i = 0; i < validOut.length; i++) {
            if (name.equals(validOut[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean checkOut(String name) {
        for (int i = 0; i < validIn.length; i++) {
            if (name.equals(validIn[i])) {
                return false;
            }
        }
        return true;
    }
}
