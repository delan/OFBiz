/*
 * $Id$
 */

package org.ofbiz.core.crypto;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;

import org.ofbiz.core.crypto.BlowFishCrypt;

/**
 * <p><b>Title:</b> GenerateKey.java
 * <p><b>Description:</b> Blowfish secret key generator.
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
public class GenerateKey {

    /**
     * Generates a secret key file (ofbkey) in the current directory.
     */
    public static void main(String args[]) throws Exception {
        System.out.println();
        System.out.println("The Open For Business Project Secret Key Generator.");
        System.out.println();
        System.out.println("(c) 2001 The Open For Business Project - And respected authors.");
        System.out.println("Permission is hereby granted, free of charge, to any person obtaining a ");
        System.out.println("copy of this software and associated documentation files (the \"Software\"");
        System.out.println("to deal in the Software without restriction, including without limitation");
        System.out.println("the rights to use, copy, modify, merge, publish, distribute, sublicense,");
        System.out.println("and/or sell copies of the Software, and to permit persons to whom the ");
        System.out.println("Software is furnished to do so, subject to the following conditions:");
        System.out.println();
        System.out.println("The above copyright notice and this permission notice shall be included");
        System.out.println("in all copies or substantial portions of the Software.");
        System.out.println();
        System.out.println("THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS");
        System.out.println("OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF");
        System.out.println("MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.");
        System.out.println("IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY");
        System.out.println("CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT");
        System.out.println("OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR");
        System.out.println("THE USE OR OTHER DEALINGS IN THE SOFTWARE.");
        System.out.println("@author Andy Zeneski (jaz@zsolv.com)");
        System.out.println();
        System.out.print("Generating key...");
        KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
        keyGen.init(448);
        SecretKey secretKey = keyGen.generateKey();
        System.out.println("Done.");
        System.out.print("Saving key file...");
        byte[] keyBytes = secretKey.getEncoded();
        String keyString = new String(keyBytes);

        FileOutputStream fos = new FileOutputStream("ofbkey");
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(keyString);
        fos.close();
        System.out.println("Done.");
        System.out.println();

        String testString = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstufwxyz";
        System.out.println("Testing key...");
        System.out.println(testString);
        File file = new File("ofbkey");
        BlowFishCrypt c = new BlowFishCrypt(file);
        byte[] encryptedBytes = c.encrypt(testString);
        String encryptedMessage = new String(encryptedBytes);
        System.out.println(encryptedMessage);
        byte[] decryptedBytes = c.decrypt(encryptedMessage);
        String decryptedMessage = new String(decryptedBytes);
        System.out.println(decryptedMessage);
        System.out.println("Strings match: " + (decryptedMessage.equals(testString) ? "Yes" : "No"));
        System.out.println();
        System.out.println("Finshed.");
        System.out.println();

    }

}
