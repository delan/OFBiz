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
package org.ofbiz.entity.util;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.HashMap;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.transaction.TransactionManager;
import javax.transaction.Transaction;
import javax.transaction.SystemException;
import javax.transaction.InvalidTransactionException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilObject;
import org.ofbiz.entity.EntityCryptoException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.transaction.TransactionFactory;
import org.ofbiz.entity.transaction.GenericTransactionException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.2
 */
public class EntityCrypto {

    public static final String module = EntityCrypto.class.getName();
    protected static EntityCrypto crypto = null;

    protected GenericDelegator delegator = null;
    protected Map keyMap = null;

    protected EntityCrypto() { }
    public EntityCrypto(GenericDelegator delegator) {
        this.delegator = delegator;
        this.keyMap = new HashMap();
    }

    /** Encrypts a String into an encrypted hex encoded byte array */
    public String encrypt(String keyName, Object obj) throws EntityCryptoException {
        return StringUtil.toHexString(this.encrypt(keyName, UtilObject.getBytes(obj)));
    }

    public byte[] encrypt(String keyName, byte[] bytes) throws EntityCryptoException {
        Cipher cipher = this.getCipher(keyName, Cipher.ENCRYPT_MODE);
        byte[] encBytes = null;
        try {
            encBytes = cipher.doFinal(bytes);
        } catch (IllegalStateException e) {
            throw new EntityCryptoException(e);
        } catch (IllegalBlockSizeException e) {
            throw new EntityCryptoException(e);
        } catch (BadPaddingException e) {
            throw new EntityCryptoException(e);
        }
        return encBytes;
    }

    /** Decrypts a hex encoded byte array into a String */
    public Object decrypt(String keyName, String str) throws EntityCryptoException {
        return UtilObject.getObject(this.decrypt(keyName, StringUtil.fromHexString(str)));
    }

    public byte[] decrypt(String keyName, byte[] bytes) throws EntityCryptoException {
        Cipher cipher = this.getCipher(keyName, Cipher.DECRYPT_MODE);
        byte[] decBytes = null;
        try {
            decBytes = cipher.doFinal(bytes);
        } catch (IllegalStateException e) {
            throw new EntityCryptoException(e);
        } catch (IllegalBlockSizeException e) {
            throw new EntityCryptoException(e);
        } catch (BadPaddingException e) {
            throw new EntityCryptoException(e);
        }       
        return decBytes;
    }

    // return a cipher for a key - DESede/CBC/NoPadding IV = 0
    protected Cipher getCipher(String keyName, int mode) throws EntityCryptoException {
        SecretKey key = this.getKey(keyName);
        byte[] zeros = { 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec iv = new IvParameterSpec(zeros);

        // create the Cipher - DESede/CBC/NoPadding
        Cipher encCipher = null;
        try {
            encCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            Debug.logError(e, module);
            return null;
        } catch (NoSuchPaddingException e) {
            Debug.logError(e, module);
        }
        try {
            encCipher.init(mode, key, iv);
        } catch (InvalidKeyException e) {
            Debug.logError(e, "Invalid key", module);
        } catch (InvalidAlgorithmParameterException e) {
            Debug.logError(e, module);
        }
        return encCipher;
    }

    protected SecretKey generateKey() throws EntityCryptoException {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DESede");
        } catch (NoSuchAlgorithmException e) {
            throw new EntityCryptoException(e);
        }

        // generate the DES3 key
        return keyGen.generateKey();
    }

    protected SecretKey getKey(String keyName) throws EntityCryptoException {
        SecretKey key = (SecretKey) keyMap.get(keyName);
        if (key == null) {
            synchronized(this) {
                key = this.getKeyFromStore(keyName);
                keyMap.put(keyName, key);
            }
        }
        return key;
    }

    protected SecretKey getKeyFromStore(String keyName) throws EntityCryptoException {
        GenericValue keyValue = null;
        try {
            keyValue = delegator.findByPrimaryKey("EntityKeyStore", UtilMisc.toMap("keyName", keyName));
        } catch (GenericEntityException e) {
            throw new EntityCryptoException(e);
        }
        if (keyValue == null || keyValue.get("keyText") == null) {
            SecretKey key = this.generateKey();
            GenericValue newValue = delegator.makeValue("EntityKeyStore", null);
            newValue.set("keyText", StringUtil.toHexString(key.getEncoded()));
            newValue.set("keyName", keyName);

            TransactionManager tm = TransactionFactory.getTransactionManager();
            Transaction parentTransaction = null;
            boolean beganTrans = false;
            try {
                beganTrans = TransactionUtil.begin();
            } catch (GenericTransactionException e) {
                throw new EntityCryptoException(e);
            }

            if (!beganTrans) {
                try {
                    parentTransaction = tm.suspend();
                } catch (SystemException e) {
                    throw new EntityCryptoException(e);
                }

                // now start a new transaction
                try {
                    beganTrans = TransactionUtil.begin();
                } catch (GenericTransactionException e) {
                    throw new EntityCryptoException(e);
                }
            }

            try {
                delegator.create(newValue);
            } catch (GenericEntityException e) {
                throw new EntityCryptoException(e);
            }

            try {
                TransactionUtil.commit(beganTrans);
            } catch (GenericTransactionException e) {
                throw new EntityCryptoException(e);
            }


            // resume the parent transaction
            if (parentTransaction != null) {
                try {
                    tm.resume(parentTransaction);
                } catch (InvalidTransactionException e) {
                } catch (IllegalStateException e) {
                    throw new EntityCryptoException(e);
                } catch (SystemException e) {
                    throw new EntityCryptoException(e);
                }
            }

            return key;
        } else {
            byte[] keyBytes = StringUtil.fromHexString(keyValue.getString("keyText"));
            return this.getDesEdeKey(keyBytes);
        }
    }

    protected SecretKey getDesEdeKey(byte[] rawKey) throws EntityCryptoException {
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance("DESede");
        } catch (NoSuchAlgorithmException e) {
            throw new EntityCryptoException(e);
        }

        // load the raw key
        if (rawKey.length > 0) {
            DESedeKeySpec desedeSpec1 = null;
            try {
                desedeSpec1 = new DESedeKeySpec(rawKey);
            } catch (InvalidKeyException e) {
                throw new EntityCryptoException(e);
            }

            // create the SecretKey Object
            SecretKey key = null;
            try {
                key = skf.generateSecret(desedeSpec1);
            } catch (InvalidKeySpecException e) {
                throw new EntityCryptoException(e);
            }
            return key;
        } else {
            throw new EntityCryptoException("Not a valid DESede key!");
        }
    }
}
