/*
 * $Id: KeyStoreUtils.java,v 1.1 2003/10/24 20:26:26 ajzeneski Exp $
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * KeyStoreUtils - Utilities for getting KeyManagers and TrustManagers
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class KeyStoreUtils {
      
    public static KeyManager[] getKeyManagers() throws IOException, GeneralSecurityException {
        // get the default TrustManagerFactory
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory factory = KeyManagerFactory.getInstance(alg);
    
        // set up the KeyStore to use
        KeyStore ks = getKeyStore();

        // initialise the TrustManagerFactory with this KeyStore
        factory.init(ks, getKeyStorePassword().toCharArray());

        // get the KeyManagers
        KeyManager[] keyManagers = factory.getKeyManagers();
        return keyManagers;
    }
    
    public static KeyManager[] getKeyManagers(String alias) throws IOException, GeneralSecurityException {
        KeyManager[] keyManagers = getKeyManagers();
        
        // if an alias has been specified, wrap recognised KeyManagers in an AliasKeyManager
        if (alias != null) {
            for (int i = 0; i < keyManagers.length; i++) {
                // we can only work with instances of X509KeyManager
                if (keyManagers[i] instanceof X509KeyManager) {
                    keyManagers[i] = new AliasKeyManager((X509KeyManager)keyManagers[i], alias);
                }
            }
        }
        return keyManagers;
    }
    
    public static TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException {
        // get the default TrustManagerFactory
        String alg = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(alg);
    
        // set up the TrustStore to use
        KeyStore ks = getTrustStore();
    
        // initialise the TrustManagerFactory with this KeyStore
        factory.init(ks);

        // get the TrustManagers
        TrustManager[] trustManagers = factory.getTrustManagers();
        return trustManagers;
    }
    
    public static String getKeyStoreFileName() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.keyStore", null);
    }
    
    public static String getKeyStorePassword() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.keyStore.password", null);
    }
    
    public static String getTrustStoreFileName() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.trustStore", null);
    }
    
    public static String getTrustStorePassword() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.trustStore.password", null);
    }
          
    public static KeyStore getKeyStore() throws IOException, GeneralSecurityException {
        if (getKeyStoreFileName() != null && !keyStoreExists(getKeyStoreFileName())) {
            return null;
        }
        FileInputStream fis = new FileInputStream(getKeyStoreFileName());
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(fis, getKeyStorePassword().toCharArray());
        fis.close();
        return ks;
    } 
    
    
    public static KeyStore getTrustStore() throws IOException, GeneralSecurityException {
        if (getTrustStoreFileName() != null && !keyStoreExists(getTrustStoreFileName())) {
            return null;
        }
        FileInputStream fis = new FileInputStream(getTrustStoreFileName());
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(fis, getTrustStorePassword().toCharArray());
        fis.close();
        return ks;
    }
    
    public static boolean keyStoreExists(String fileName) {
        File keyFile = new File(fileName);
        return keyFile.exists();
    }
    
    protected static class AliasKeyManager implements X509KeyManager {
    
        X509KeyManager keyManager = null;
        String alias = null;

        public AliasKeyManager(X509KeyManager keyManager, String alias) {
            this.keyManager = keyManager;
            this.alias = alias;
        }

        // this is where the customization comes in
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) { 
          for (int i = 0; i < keyType.length; i++) {
              String[] aliases = keyManager.getClientAliases(keyType[i], issuers);
              if (aliases != null && aliases.length > 0) {
                  for (int x = 0; x < aliases.length; x++) {
                      if (alias.equals(aliases[i])) {
                          return alias;
                      }
                  }
              }
          }
          return null;
        }

        // these just pass through the keyManager
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
          return keyManager.chooseServerAlias(keyType, issuers, socket);
        }

        public X509Certificate[] getCertificateChain(String alias) {
          return keyManager.getCertificateChain(alias);
        }

        public String[] getClientAliases(String keyType, Principal[] issuers) {
          return keyManager.getClientAliases(keyType, issuers);
        }

        public PrivateKey getPrivateKey(String alias) {
          return keyManager.getPrivateKey(alias);
        }

        public String[] getServerAliases(String keyType, Principal[] issuers) {
          return keyManager.getServerAliases(keyType, issuers);
        }
    }
    
}
