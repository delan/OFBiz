/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.commonapp.thirdparty.paymentech;

import java.text.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.accounting.payment.*;

// SDK imports
import com.paymentech.gw.sdk.wrapper.*;
import com.paymentech.gw.sdk.request.*;
import com.paymentech.gw.sdk.response.*;
import com.paymentech.eis.tools.Debug;
import com.paymentech.gw.tp.*;
import com.paymentech.gw.sdk.request.AVS;

/**
 * PaymentechGateway.java
 *
 * @author     cnelson
 * @version    $Revision$
 * @since      2.0
 */
public class PaymentechGateway extends AbstractPaymentGateway {
    private String merchantId;
    private String terminalId;
    private String binId;

    /** Creates a new instance of PaymentechGateway */
    public PaymentechGateway() {
        terminalId = UtilProperties.getPropertyValue("payment.properties", "paymentech.terminalID");
        merchantId = UtilProperties.getPropertyValue("payment.properties", "paymentech.merchantID");
        binId = UtilProperties.getPropertyValue("payment.properties", "paymentech.bin");
    }

    public boolean authorize(EFTPaymentInfo payment) throws PaymentGatewayException {
        throw new PaymentGatewayException("EFT payment unsupported");
    }

    public boolean authorize(CreditCardPaymentInfo payment) throws PaymentGatewayException {
        Merchant aMerchant = Merchant.getInstance(merchantId);
        Terminal aTerminal = Terminal.getInstance("Terminal", terminalId);
        BIN bin = BIN.getInstance(binId);

        AuthRequest ar = new AuthRequest(
                aMerchant,
                aTerminal,
                bin,
                buildOrder(payment),
                buildPayInfo(payment),
                buildAVS(payment), // AVS is optional
                null, // Ship ref is optional
                "This is a basic auth request"
            );
        ResponseTransaction data = sendRequest(ar.getRequest());

        return saveData(payment.getPaymentPreference(), data, PAYMENT_AUTHORIZED);

    }

    protected AVS buildAVS(org.ofbiz.commonapp.accounting.payment.PaymentInfo payment) {
        AVS avs = AVS.getInstance();

        avs.setAddress1(payment.getAddress().getString("address1"));
        avs.setState(payment.getAddress().getString("stateProvinceGeoId"));
        avs.setZip(payment.getAddress().getString("postalCode"));
        return avs;
    }

    protected com.paymentech.gw.sdk.request.PaymentInfo buildPayInfo(CreditCardPaymentInfo payment) {
        GenericValue creditCard = payment.getCreditCard();
        // just get month and year
        List expDateList = StringUtil.split(creditCard.getString("expireDate"), "/");
        String year = expDateList.get(1).toString();

        if (year.length() == 4) {
            year = year.substring(2);
        }
        String expDate = expDateList.get(0).toString() + year;
        com.paymentech.gw.sdk.request.PaymentInfo payinfo =
            com.paymentech.gw.sdk.request.PaymentInfo.getInstance(creditCard.getString("cardNumber"),
                expDate);

        return payinfo;
    }

    protected Order buildOrder(CreditCardPaymentInfo payment) {
        DecimalFormat numberFormat = new DecimalFormat("###0");
        String orderId = payment.getOrderHeader().getString("orderId");
        // Need to set price * 100 to eliminate decimals
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < 16 - orderId.length(); i++) {
            buf.append('0');
        }
        buf.append(orderId);
        Order order = Order.getInstance(buf.toString(), numberFormat.format(payment.getAmount() * 100));

        return order;
    }

    protected boolean saveData(GenericValue paymentPreference, ResponseTransaction data, String successStatus)
        throws PaymentGatewayException {
        boolean approved = false;

        if (data.getValue(ResponseTransaction.OUTCOME).equals(ResponseTransaction.OUTCOME_APPROVED)) {
            paymentPreference.setString("statusId", successStatus);
            paymentPreference.setString("authCode", data.getValue(ResponseTransaction.AUTH_CODE));
            paymentPreference.setString("authMessage", data.getValue(ResponseTransaction.MISC_MESSAGE));
            paymentPreference.setString("authRefNum", data.getValue(ResponseTransaction.REF_CODE));
            approved = true;
        } else if (data.getValue(ResponseTransaction.OUTCOME).equals(ResponseTransaction.OUTCOME_DECLINED)) {
            paymentPreference.setString("statusId", PaymentGateway.PAYMENT_DECLINED);
            paymentPreference.setString("authCode", data.getValue(ResponseTransaction.DECLINE_CODE));
            paymentPreference.setString("authMessage", data.getValue(ResponseTransaction.DECLINE_TEXT));
        } else if (data.getValue(ResponseTransaction.OUTCOME).equals(ResponseTransaction.OUTCOME_ERROR)) {
            paymentPreference.setString("statusId", PaymentGateway.PAYMENT_ERROR);
            paymentPreference.setString("authCode", data.getValue(ResponseTransaction.ERROR_CODE));
            paymentPreference.setString("authMessage", data.getValue(ResponseTransaction.ERROR_TEXT));
        }
        try {
            paymentPreference.store();
        } catch (GenericEntityException ge) {
            throw new PaymentGatewayException(ge);
        }
        return approved;
    }

    /**
     * Send the Request to the gateway and process the response.
     */
    protected ResponseTransaction sendRequest(Request req) throws PaymentGatewayException {
        Response gatewayResponse = null;
        ResponseTransaction transData = null;
        ITransactionProcessorPool newTranProcessorPool = null;

        try { // create a transaction processor pool
            newTranProcessorPool = TransactionProcessorPoolSingleton.getInstance();
            // Acquire a transaction processor from the pool
            ITransactionProcessor processor = newTranProcessorPool.acquire();

            Debug.ASSERT("processor != null", processor != null);
            // Process this trans request and get a response.
            gatewayResponse = (Response) processor.process(req);
            processor = null;
        } catch (Throwable e) {
            throw new PaymentGatewayException(e);
        } finally {
            newTranProcessorPool.release();
        }

        if (gatewayResponse != null) {
            try {  // Create a paymentech transaction
                transData = new PaymentechResponseTransaction();
                // Create a translator for the response
                ResponseTranslator translator
                    = new ResponseTranslator(gatewayResponse);

                // Use the translator to translate codes and messages from
                // response XML document to the ResponseTransaction.
                translator.populateResponseTransaction(transData);
            } catch (Exception e) {
                throw new PaymentGatewayException(e);
            }
        }
        return transData;
    }

    public boolean capture(CreditCardPaymentInfo payment) throws PaymentGatewayException {
        Merchant aMerchant = Merchant.getInstance("700000000302");
        Terminal aTerminal = Terminal.getInstance("Terminal", "0001");
        BIN bin = BIN.getInstance("000002");

        String txRefNo = payment.getPaymentPreference().getString("authRefNum");

        CaptureRequest cr = new CaptureRequest(
                aMerchant,
                aTerminal,
                bin,
                buildOrder(payment),
                buildPayInfo(payment),
                txRefNo,
                "This is a basic capture request"
            );

        ResponseTransaction data = sendRequest(cr.getRequest());

        return saveData(payment.getPaymentPreference(), data, PAYMENT_CAPTURED);
    }

    public boolean capture(EFTPaymentInfo payment) throws PaymentGatewayException {
        throw new PaymentGatewayException("Unsupported Operation");
    }

}
