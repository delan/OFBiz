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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;

/**
 * KeyStoreUtil - Utilities for getting KeyManagers and TrustManagers
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.0
 */
public class KeyStoreUtil {

    public static final String module = KeyStoreUtil.class.getName();

    public static String getKeyStoreFileName() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.keyStore", null);
    }

    public static String getKeyStorePassword() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.keyStore.password", null);
    }

    public static String getKeyStoreType() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.keyStore.type", "jks");
    }

    public static String getTrustStoreFileName() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.trustStore", null);
    }

    public static String getTrustStorePassword() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.trustStore.password", null);
    }

    public static String getTrustStoreType() {
        return UtilProperties.getPropertyValue("jsse.properties", "ofbiz.trustStore.type", "jks");
    }

    public static KeyStore getKeyStore() throws IOException, GeneralSecurityException {
        if (getKeyStoreFileName() != null && !keyStoreExists(getKeyStoreFileName())) {
            return null;
        }
        FileInputStream fis = new FileInputStream(getKeyStoreFileName());
        KeyStore ks = KeyStore.getInstance(getKeyStoreType());
        ks.load(fis, getKeyStorePassword().toCharArray());
        fis.close();
        return ks;
    }

    public static void saveKeyStore(KeyStore ks) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        ks.store(new FileOutputStream(getKeyStoreFileName()), getKeyStorePassword().toCharArray());
    }

    public static KeyStore getTrustStore() throws IOException, GeneralSecurityException {
        if (getTrustStoreFileName() != null && !keyStoreExists(getTrustStoreFileName())) {
            return null;
        }
        FileInputStream fis = new FileInputStream(getTrustStoreFileName());
        KeyStore ks = KeyStore.getInstance(getTrustStoreType());
        ks.load(fis, getTrustStorePassword().toCharArray());
        fis.close();
        return ks;
    }

    public static void saveTrustStore(KeyStore ks) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        ks.store(new FileOutputStream(getTrustStoreFileName()), getTrustStorePassword().toCharArray());
    }

    public static boolean keyStoreExists(String fileName) {
        File keyFile = new File(fileName);
        return keyFile.exists();
    }

    public static KeyStore createKeyStore(String fileName, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore ks = null;
        ks = KeyStore.getInstance("jks");
        ks.load(null, password.toCharArray());
        ks.store(new FileOutputStream(fileName), password.toCharArray());
        ks.load(new FileInputStream(fileName), password.toCharArray());
        return ks;
    }

    public static void renameKeyStoreEntry(String fromAlias, String toAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = getKeyStore();
        String pass = getKeyStorePassword();
        renameEntry(ks, pass, fromAlias, toAlias);
        saveKeyStore(ks);
    }

    private static void renameEntry(KeyStore ks, String pass, String fromAlias, String toAlias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        if (ks.isKeyEntry(fromAlias)) {
            Key fromKey = ks.getKey(fromAlias, pass.toCharArray());
            if (fromKey instanceof PrivateKey) {
                Certificate[] certs = ks.getCertificateChain(fromAlias);
                ks.deleteEntry(fromAlias);
                ks.setKeyEntry(toAlias, fromKey, pass.toCharArray(), certs);
            }
        } else if (ks.isCertificateEntry(fromAlias)) {
            Certificate cert = ks.getCertificate(fromAlias);
            ks.deleteEntry(fromAlias);
            ks.setCertificateEntry(toAlias, cert);
        }
    }

    public static void importPKCS8CertChain(KeyStore ks, String alias, byte[] keyBytes, String keyPass, byte[] certChain) throws InvalidKeySpecException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        // load the private key
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey pk = kf.generatePrivate(keysp);

        // load the cert chain
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bais = new ByteArrayInputStream(certChain);

        Collection certCol = cf.generateCertificates(bais);
        Certificate[] certs = new Certificate[certCol.toArray().length];
        if (certCol.size() == 1) {
            Debug.log("Single certificate; no chain", module);
            bais = new ByteArrayInputStream(certChain);
            Certificate cert = cf.generateCertificate(bais);
            certs[0] = cert;
        } else {
            Debug.log("Certificate chain length : " + certCol.size(), module);
            certs = (Certificate[]) certCol.toArray();
        }

        ks.setKeyEntry(alias, pk, keyPass.toCharArray(), certs);
    }

    // key pair generation methods
    public static KeyPair createDHKeyPair() throws Exception {
        AlgorithmParameterGenerator apGen = AlgorithmParameterGenerator.getInstance("DH");
        apGen.init(1024);

        AlgorithmParameters algParams = apGen.generateParameters();
        DHParameterSpec dhParamSpec = (DHParameterSpec) algParams.getParameterSpec(DHParameterSpec.class);

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
        keyGen.initialize(dhParamSpec);

        KeyPair keypair = keyGen.generateKeyPair();
        return keypair;
    }

    public static KeyPair getKeyPair(String alias, String password) throws Exception {
        KeyStore ks = getKeyStore();
        Key key = ks.getKey(alias, password.toCharArray());
        if (key instanceof PrivateKey) {
            Certificate cert = ks.getCertificate(alias);
            PublicKey publicKey = cert.getPublicKey();
            return new KeyPair(publicKey, (PrivateKey) key);
        } else {
            Debug.logError("Key is not an instance of PrivateKey", module);
        }
        return null;
    }

    public static void storeCertificate(String alias, Certificate cert) throws Exception {
        KeyStore ks = getKeyStore();
        ks.setCertificateEntry(alias, cert);
        ks.store(new FileOutputStream(getKeyStoreFileName()), getKeyStorePassword().toCharArray());
    }

    public static void storeKeyPair(KeyPair keyPair, String alias, String password) throws Exception {
        KeyStore ks = getKeyStore();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        // not sure what to do here. Do we need to create a cert to assoc with the private key?
        // cannot find methods for just setting the private/public key; missing something
        ks.store(new FileOutputStream(getKeyStoreFileName()), getKeyStorePassword().toCharArray());
    }

    public static String certToString(Certificate cert) throws CertificateEncodingException {
        byte[] certBuf = cert.getEncoded();
        StringBuffer buf = new StringBuffer();
        buf.append("-----BEGIN CERTIFICATE-----\n");
        buf.append(new sun.misc.BASE64Encoder().encode(certBuf));
        buf.append("\n-----END CERTIFICATE-----\n");
        return buf.toString();
    }

    public static Certificate stringToCert(String certString) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return null;
    }

    public static SecretKey generateSecretKey(PrivateKey ourKey, PublicKey theirKey) throws Exception {
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(ourKey);
        ka.doPhase(theirKey, true);
        return ka.generateSecret("TripleDES");
    }

    public static PublicKey readDHPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        return keyFactory.generatePublic(x509KeySpec);
    }

    public static PrivateKey readDHPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        return keyFactory.generatePrivate(x509KeySpec);
    }

}
