/*
 * $Id: HashEncrypt.java,v 1.2 2004/04/07 07:07:37 jonesde Exp $
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.securityext.login;


import java.security.MessageDigest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;


/**
 * Utility class for doing SHA One-Way Hash Encryption
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    10 Mar 2002
 *@version    1.0
 */
public class HashEncrypt {
    
    public static final String module = HashEncrypt.class.getName();
    public static final String resource = "SecurityextUiLabels";
    
    private static char hexChars[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
        };

    public static String getHash(String str) {
        String hashType = UtilProperties.getPropertyValue("security.properties", "password.encrypt.hash.type");

        if (hashType == null || hashType.length() == 0) {
            Debug.logWarning("Password encrypt hash type is not specified in security.properties, use SHA", module);
            hashType = "SHA";
        }

        return getDigestHash(str, hashType);
    }

    public static String getDigestHash(String str, String hashType) {
        if (str == null) return null;
        try {
            MessageDigest messagedigest = MessageDigest.getInstance(hashType);
            int i = str.length();
            byte strBytes[] = str.getBytes();

            messagedigest.update(strBytes);
            byte digestBytes[] = messagedigest.digest();
            int k = 0;
            char digestChars[] = new char[digestBytes.length * 2];

            for (int l = 0; l < digestBytes.length; l++) {
                int i1 = digestBytes[l];

                if (i1 < 0)
                    i1 = 127 + i1 * -1;
                encodeInt(i1, k, digestChars);
                k += 2;
            }

            return new String(digestChars, 0, digestChars.length);
        } catch (Exception e) {
            Debug.logError(e, "Error while computing hash of type " + hashType, module);
        }
        return str;
    }

    public static String getDigestHash(String str, String code, String hashType) {
        if (str == null) return null;
        try {
            byte codeBytes[] = null;

            if (code == null) codeBytes = str.getBytes();
            else codeBytes = str.getBytes(code);
            MessageDigest messagedigest = MessageDigest.getInstance(hashType);

            messagedigest.update(codeBytes);
            byte digestBytes[] = messagedigest.digest();
            int i = 0;
            char digestChars[] = new char[digestBytes.length * 2];

            for (int j = 0; j < digestBytes.length; j++) {
                int k = digestBytes[j];

                if (k < 0) {
                    k = 127 + k * -1;
                }
                encodeInt(k, i, digestChars);
                i += 2;
            }

            return new String(digestChars, 0, digestChars.length);
        } catch (Exception e) {
            Debug.logError(e, "Error while computing hash of type " + hashType, module);
        }
        return str;
    }

    private static char[] encodeInt(int i, int j, char digestChars[]) {
        if (i < 16) {
            digestChars[j] = '0';
        }
        j++;
        do {
            digestChars[j--] = hexChars[i & 0xf];
            i >>>= 4;
        } while (i != 0);
        return digestChars;
    }
}
