/*
 * $Id$
 */

package org.ofbiz.core.crypto;


import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;


/**
 * <p><b>Title:</b> BlowFishCrypt.java
 * <p><b>Description:</b> Blowfish (Two-Way) Byte/String encryption.
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
public class BlowFishCrypt {

    private SecretKeySpec secretKeySpec = null;

    /*
     * Creates a new BlowFishCrypt object.
     *@param secretKeySpec A SecretKeySpec object.
     */
    public BlowFishCrypt(SecretKeySpec secretKeySpec) {
        this.secretKeySpec = secretKeySpec;
    }

    /*
     * Creates a new BlowFishCrypt object.
     *@param key An encoded secret key
     */
    public BlowFishCrypt(byte[] key) {
        try {
            secretKeySpec = new SecretKeySpec(key, "Blowfish");
        } catch (Exception e) {}
    }

    /*
     * Creates a new BlowFishCrypt object.
     *@param file A file object containing the secret key as a String object.
     */
    public BlowFishCrypt(File keyFile) {
        try {
            FileInputStream is = new FileInputStream(keyFile);
            ObjectInputStream os = new ObjectInputStream(is);
            String keyString = (String) os.readObject();

            is.close();

            byte[] keyBytes = keyString.getBytes();

            secretKeySpec = new SecretKeySpec(keyBytes, "Blowfish");
        } catch (Exception e) {}
    }

    /*
     * Encrypt the string with the secret key.
     *@param string The string to encrypt.
     */
    public byte[] encrypt(String string) {
        return encrypt(string.getBytes());
    }

    /*
     * Decrypt the string with the secret key.
     *@param string The string to decrypt.
     */
    public byte[] decrypt(String string) {
        return decrypt(string.getBytes());
    }

    /*
     * Encrypt the byte array with the secret key.
     *@param bytes The array of bytes to encrypt.
     */
    public byte[] encrypt(byte[] bytes) {
        byte[] resp = null;

        try {
            resp = crypt(bytes, Cipher.ENCRYPT_MODE);
        } catch (Exception e) {
            return null;
        }
        return resp;
    }

    /*
     * Decrypt the byte array with the secret key.
     *@param bytes The array of bytes to decrypt.
     */
    public byte[] decrypt(byte[] bytes) {
        byte[] resp = null;

        try {
            resp = crypt(bytes, Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            return null;
        }
        return resp;
    }

    private byte[] crypt(byte[] bytes, int mode) throws Exception {
        if (secretKeySpec == null)
            throw new Exception("SecretKey cannot be null.");
        Cipher cipher = Cipher.getInstance("Blowfish");

        cipher.init(mode, secretKeySpec);
        return cipher.doFinal(bytes);
    }
}
