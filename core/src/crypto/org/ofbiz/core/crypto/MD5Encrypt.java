/*
 * $Id$
 */

package org.ofbiz.core.crypto;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * <p><b>Title:</b> MD5Encrypt.java
 * <p><b>Description:</b> MD5(One-Way) String Encryption.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version    1.0
 * @created    August 23, 2001
 */
public class MD5Encrypt {

    private String encryptedString = null;

    /**
     * Creates a new Encrypted String object.
     *@param encryptedString The encrypted string to use.
     */
    public MD5Encrypt(String encryptedString) {
        this.encryptedString = encryptedString;
    }

    /**
     * Sets the encrypted string.
     *@param encryptedString The encrypted string to use.
     */
    public void setEncryptedString(String encryptedString) {
        this.encryptedString = encryptedString;
    }

    /**
     *@return The defined encrypted string.
     */
    public String getEncryptedString() {
        return encryptedString;
    }

    /**
     * Encrypts the string.
     *@param string The plain text string to encrypt.
     *@return The encrypted string.
     */
    public String encrypt(String string) {
        encryptedString = encryptString(string);
        return encryptedString;
    }

    /**
     * Compares the supplied plain text string to the encrypted string.
     *@param string The plain text string.
     */
    public boolean compareEncrypted(String string) {
        if (encryptedString == null || !encryptedString.equals(encryptString(string)))
            return false;
        else
            return true;
    }

    /**
     * Compares the supplied plain text string to supplied the encrypted string.
     *@param crypt The encrypted string.
     *@param string The plain text string.
     *@return True if the two encrypted strings match.
     */
    public boolean compareEncrypted(String crypt, String string) {
        if (crypt == null || !crypt.equals(encryptString(string)))
            return false;
        else
            return true;
    }

    private String encryptString(String string) {

        byte[] val = string.getBytes();
        MessageDigest algorithm = null;

        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        algorithm.reset();
        algorithm.update(val);
        byte[] digest = algorithm.digest();
        String crypt = new String(digest);

        return crypt;
    }
}
