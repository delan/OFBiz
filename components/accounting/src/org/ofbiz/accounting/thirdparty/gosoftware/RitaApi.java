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
import java.io.DataInputStream;
import java.net.Socket;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.Debug;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      Sep 28, 2004
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
    protected Document document = null;
    protected Element req = null;
    protected String host = null;
    protected int port = 0;
    protected int mode = 0;

    public RitaApi(Document document) {
        this.document = document;
        Element rootElement = this.document.getDocumentElement();
        if (reqElement.equals(rootElement.getNodeName())) {
            this.req = rootElement;
        } else {
            this.req = UtilXml.firstChildElement(rootElement, reqElement);
        }
        this.mode = MODE_OUT;
    }

    public RitaApi(boolean isFile) {
        // initialize the document
        String initialElement = rootElement;
        if (!isFile) {
            initialElement = reqElement;
        }

        this.document = UtilXml.makeEmptyXmlDocument(initialElement);
        Element root = this.document.getDocumentElement();
        if (isFile) {
            root.setAttribute("xmlns", xschema);
            this.req = UtilXml.addChildElement(root, reqElement, document);
        } else {
            this.req = root;
        }
        this.mode = MODE_IN;
    }

    public RitaApi(String host, int port) {
        this(false);
        this.host = host;
        this.port = port;
    }

    public RitaApi() {
        this(true);
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
        UtilXml.addChildElementValue(req, name, objString, document);
    }

    public String get(String name) {
        if (!checkOut(name)) {
            throw new IllegalArgumentException("Field [" + name + "] is not a valid OUT parameter");
        }

        return UtilXml.childElementValue(req, name);
    }

    public String toString() {
        try {
            return UtilXml.writeXmlDocument(document);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw new IllegalStateException("Unable to write document as String");
        }
    }

    public Document getDocument() {
        return this.document;
    }

    public RitaApi send() throws IOException, GeneralException {
        if (host == null || port == 0) {
            throw new GeneralException("TCP transaction not supported without valid host/port configuration");
        }

        byte readBuffer[] = new byte[2250];
        if (mode == MODE_IN) {
            Socket sock = new Socket(host, port);
            PrintStream ps = new PrintStream(sock.getOutputStream());
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            ps.print(this.toString());
            ps.flush();

            StringBuffer buf = new StringBuffer();
            int size;
            while ((size = dis.read(readBuffer)) > -1) {
                buf.append(new String(readBuffer, 0, size));
            }
            Document outDoc = null;
            try {
                outDoc = UtilXml.readXmlDocument(buf.toString(), false);
            } catch (ParserConfigurationException e) {
                throw new GeneralException(e);
            } catch (SAXException e) {
                throw new GeneralException(e);
            }

            RitaApi out = new RitaApi(outDoc);
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
