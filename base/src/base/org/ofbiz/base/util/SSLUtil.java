/*
 * $Id$
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.*;

/**
 * KeyStoreUtil - Utilities for setting up SSL connections with specific client certificates
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.0
 */
public class SSLUtil {

    public static final String module = SSLUtil.class.getName();
    private static boolean loadedProps = false;

    public static KeyManager[] getKeyManagers() throws IOException, GeneralSecurityException {
        // get the default TrustManagerFactory
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory factory = KeyManagerFactory.getInstance(alg);

        // set up the KeyStore to use
        KeyStore ks = KeyStoreUtil.getKeyStore();

        // initialise the TrustManagerFactory with this KeyStore
        factory.init(ks, KeyStoreUtil.getKeyStorePassword().toCharArray());

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
        KeyStore ks = KeyStoreUtil.getTrustStore();

        // initialise the TrustManagerFactory with this KeyStore
        factory.init(ks);

        // get the TrustManagers
        TrustManager[] trustManagers = factory.getTrustManagers();
        return trustManagers;
    }

    public static SSLSocketFactory getSSLSocketFactory(String alias) throws IOException, GeneralSecurityException {
        KeyManager[] km = getKeyManagers(alias);
        TrustManager[] tm = getTrustManagers();

        // may want to have this in the properties file
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(km, tm, null);
        return context.getSocketFactory();
    }

    public static synchronized void loadJsseProperties() {
        if (!loadedProps) {
            String protocol = UtilProperties.getPropertyValue("jsse.properties", "java.protocol.handler.pkgs", "NONE");
            String proxyHost = UtilProperties.getPropertyValue("jsse.properties", "https.proxyHost", "NONE");
            String proxyPort = UtilProperties.getPropertyValue("jsse.properties", "https.proxyPort", "NONE");
            String cypher = UtilProperties.getPropertyValue("jsse.properties", "https.cipherSuites", "NONE");
            if (protocol != null && !protocol.equals("NONE")) {
                System.setProperty("java.protocol.handler.pkgs", protocol);
            }
            if (proxyHost != null && !proxyHost.equals("NONE")) {
                System.setProperty("https.proxyHost", proxyHost);
            }
            if (proxyPort != null && !proxyPort.equals("NONE")) {
                System.setProperty("https.proxyPort", proxyPort);
            }
            if (cypher != null && !cypher.equals("NONE")) {
                System.setProperty("https.cipherSuites", cypher);
            }
            loadedProps = true;
        }
    }
}
