/*
 * $Id: Base64.java,v 1.1 2003/08/15 20:23:20 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.base.util;

/**
 * Base64 implements Base64 encoding and Base 64 decoding.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     UPS XPCI Sample Code
 *@version    $Revision: 1.1 $
 *@since      2.2
 */

public class Base64 {

    private static byte[] Base64EncMap, Base64DecMap;
    static {
        // rfc-2045: Base64 Alphabet
        byte[] map =
            {
                (byte) 'A',
                (byte) 'B',
                (byte) 'C',
                (byte) 'D',
                (byte) 'E',
                (byte) 'F',
                (byte) 'G',
                (byte) 'H',
                (byte) 'I',
                (byte) 'J',
                (byte) 'K',
                (byte) 'L',
                (byte) 'M',
                (byte) 'N',
                (byte) 'O',
                (byte) 'P',
                (byte) 'Q',
                (byte) 'R',
                (byte) 'S',
                (byte) 'T',
                (byte) 'U',
                (byte) 'V',
                (byte) 'W',
                (byte) 'X',
                (byte) 'Y',
                (byte) 'Z',
                (byte) 'a',
                (byte) 'b',
                (byte) 'c',
                (byte) 'd',
                (byte) 'e',
                (byte) 'f',
                (byte) 'g',
                (byte) 'h',
                (byte) 'i',
                (byte) 'j',
                (byte) 'k',
                (byte) 'l',
                (byte) 'm',
                (byte) 'n',
                (byte) 'o',
                (byte) 'p',
                (byte) 'q',
                (byte) 'r',
                (byte) 's',
                (byte) 't',
                (byte) 'u',
                (byte) 'v',
                (byte) 'w',
                (byte) 'x',
                (byte) 'y',
                (byte) 'z',
                (byte) '0',
                (byte) '1',
                (byte) '2',
                (byte) '3',
                (byte) '4',
                (byte) '5',
                (byte) '6',
                (byte) '7',
                (byte) '8',
                (byte) '9',
                (byte) '+',
                (byte) '/' };
        Base64EncMap = map;
        Base64DecMap = new byte[128];
        for (int idx = 0; idx < Base64EncMap.length; idx++) {
            Base64DecMap[Base64EncMap[idx]] = (byte) idx;
        }
    }
    
    /**
     * This method decodes the given byte[] using the base64-encoding
     * specified in RFC-2045 (Section 6.8).
     *
     * @param  data the base64-encoded data.
     * @return the decoded data.
     */
    public final static byte[] base64Decode(byte[] data) {
        if (data == null) {
            return null;
        }

        int tail = data.length;
        while (data[tail - 1] == '=') {
            tail--;
        }

        byte dest[] = new byte[tail - data.length / 4];

        // ascii printable to 0-63 conversion
        for (int idx = 0; idx < data.length; idx++) {
            data[idx] = Base64DecMap[data[idx]];
        }

        // 4-byte to 3-byte conversion
        int sidx, didx;
        for (sidx = 0, didx = 0; didx < dest.length - 2; sidx += 4, didx += 3) {
            dest[didx] = (byte) (((data[sidx] << 2) & 255) | ((data[sidx + 1] >>> 4) & 003));
            dest[didx + 1] = (byte) (((data[sidx + 1] << 4) & 255) | ((data[sidx + 2] >>> 2) & 017));
            dest[didx + 2] = (byte) (((data[sidx + 2] << 6) & 255) | (data[sidx + 3] & 077));
        }
        if (didx < dest.length) {
            dest[didx] = (byte) (((data[sidx] << 2) & 255) | ((data[sidx + 1] >>> 4) & 003));
        }
        if (++didx < dest.length) {
            dest[didx] = (byte) (((data[sidx + 1] << 4) & 255) | ((data[sidx + 2] >>> 2) & 017));
        }

        return dest;
    }
    
    /**
     * This method decodes the given string using the base64-encoding
     * specified in RFC-2045 (Section 6.8).
     *
     * @param  str the base64-encoded string.
     * @return the decoded str.
     */
    public final static String base64Decode(String str) {
        if (str == null) {
            return null;
        }
        
        byte data[] = new byte[str.length()];
        data = str.getBytes();
        return new String(base64Decode(data));
    }
    
    /**
     * This method encodes the given byte[] using the base64-encoding
     * specified in RFC-2045 (Section 6.8).
     *
     * @param  data the data
     * @return the base64-encoded data
     */
    public final static byte[] base64Encode(byte[] data) {
        if (data == null) {
            return null;
        }

        int sidx, didx;
        byte dest[] = new byte[((data.length + 2) / 3) * 4];

        // 3-byte to 4-byte conversion + 0-63 to ascii printable conversion
        for (sidx = 0, didx = 0; sidx < data.length - 2; sidx += 3) {
            dest[didx++] = Base64EncMap[(data[sidx] >>> 2) & 077];
            dest[didx++] = Base64EncMap[(data[sidx + 1] >>> 4) & 017 | (data[sidx] << 4) & 077];
            dest[didx++] = Base64EncMap[(data[sidx + 2] >>> 6) & 003 | (data[sidx + 1] << 2) & 077];
            dest[didx++] = Base64EncMap[data[sidx + 2] & 077];
        }
        if (sidx < data.length) {
            dest[didx++] = Base64EncMap[(data[sidx] >>> 2) & 077];
            if (sidx < data.length - 1) {
                dest[didx++] = Base64EncMap[(data[sidx + 1] >>> 4) & 017 | (data[sidx] << 4) & 077];
                dest[didx++] = Base64EncMap[(data[sidx + 1] << 2) & 077];
            } else
                dest[didx++] = Base64EncMap[(data[sidx] << 4) & 077];
        }

        // add padding
        for (; didx < dest.length; didx++) {
            dest[didx] = (byte) '=';
        }

        return dest;
    }
    
    /**
     * This method encodes the given string using the base64-encoding
     * specified in RFC-2045 (Section 6.8).
     *
     * @param  str the string
     * @return the base64-encoded str
     */
    public final static String base64Encode(String str) {
        if (str == null) {
            return null;
        }
        byte data[] = new byte[str.length()];

        data = str.getBytes();
        return new String(base64Encode(data));
    }
}
