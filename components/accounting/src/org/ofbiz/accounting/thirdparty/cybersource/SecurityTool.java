/*
 * $Id: SecurityTool.java,v 1.1 2003/10/28 06:21:37 ajzeneski Exp $
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.accounting.thirdparty.cybersource;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;

import com.cybersource.security.SecurityApi;
import com.cybersource.security.identity.Identity;
import com.cybersource.security.identity.IdentityInfo;
import com.cybersource.security.exception.SecuritySystemException;

/**
 * CyberSource Security Tool - Service Wrapper To Manage Encryption Keys
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class SecurityTool {

    public static final String module = SecurityTool.class.getName();

    // these will be moved to the properties file when complete
    public static final String merchantId = "merchantId";
    public static final String resellerId = "resellerId";
    public static final String password = "password";

    public static final String csHost = "security.ic3.com";
    public static final int csPort = 2112;

    public static final String keyStoreFile = "csstore";

    public static Map getCertificate(DispatchContext dctx, Map context) {
        String serialNumber = (String) context.get("serialNumber");
        IdentityInfo idInfo = null;
        try {
            idInfo = SecurityApi.listCertificate(resellerId, merchantId, password, csHost, csPort, serialNumber);
        } catch (SecuritySystemException e) {
            Debug.logError(e, "ERROR: Unable to get Certificate from CyberSource", module);
            return ServiceUtil.returnError("Unable to get Certificate from CyberSource");
        }

        Map certificateInfo = new HashMap();
        if (idInfo != null) {
            certificateInfo = makeIdentityInfoMap(idInfo);
        } else {
            return ServiceUtil.returnError("Empty Certificate returned from CyberSource");
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("certificate", certificateInfo);
        return result;
    }

    public static Map getCertificateList(DispatchContext dctx, Map context) {
        IdentityInfo[] idInfos = null;
        try {
            idInfos = SecurityApi.listCertificates(resellerId, merchantId, password, csHost, csPort);
        } catch (SecuritySystemException e) {
            Debug.logError(e, "ERROR: Unable to get Certificate List from CyberSource", module);
            return ServiceUtil.returnError("Unable to get Certificate List from CyberSource");
        }

        List certList = new ArrayList();
        if (idInfos != null) {
            for (int i = 0; i < idInfos.length; i++) {
                certList.add(makeIdentityInfoMap(idInfos[i]));
            }
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("certificateList", certList);
        return result;
    }

    public static Map deleteCertificate(DispatchContext dctx, Map context) {
        String serialNumber = (String) context.get("serialNumber");
        try {
            SecurityApi.deleteCertificate(resellerId, merchantId, password, csHost, csPort, serialNumber);
        } catch (SecuritySystemException e) {
            Debug.logError(e, "ERROR: Unable to delete Certificate from CyberSource", module);
            return ServiceUtil.returnError("Unable to delete Certificate from CyberSource");
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map generateCertificate(DispatchContext dctx, Map context) {
        Identity[] identity = null;
        try {
            identity = SecurityApi.generateCertificate(resellerId, merchantId, password, csHost, csPort, keyStoreFile);
        } catch (SecuritySystemException e) {
            Debug.logError(e, "ERROR: Unable to generate Certificate from CyberSource", module);
            return ServiceUtil.returnError("Unable to generate Certificate from CyberSource");
        } catch (IOException e) {
            Debug.logError(e, "ERROR: Unable to write the CS KeyStore", module);
            return ServiceUtil.returnError("Unable to write the CS KeyStore");
        }
        return ServiceUtil.returnSuccess();
    }

    private static Map makeIdentityInfoMap(IdentityInfo idInfo) {
        Map certificateInfo = new HashMap();
        if (idInfo != null) {
            certificateInfo.put("serialNumber", idInfo.getSerialNumber());
            certificateInfo.put("expirationDate", idInfo.getExpirationDate());
            certificateInfo.put("string", idInfo.toString());
        }
        return certificateInfo;
    }

    private static Map makeIdentityMap(Identity id) {
        Map identity = new HashMap();
        if (id != null) {
            identity.put("certHex", StringUtil.toHexString(id.getCertificate()));
            identity.put("privateKeyHex", StringUtil.toHexString(id.getPrivateKey()));
            identity.put("publicKeyHex", StringUtil.toHexString(id.getPublicKey()));
            identity.put("issuerName", id.getIssuerName());
            identity.put("merchantId", id.getName());
            identity.put("expireDate", id.getExpirationDate());
            identity.put("serialNumber", id.getSerialNumber());
            identity.put("string", id.toString());
        }
        return identity;
    }
}
