/*
 * $Id: URLConnector.java,v 1.2 2003/10/24 20:26:26 ajzeneski Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.base.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * URLConnector.java
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      2.0
 */
public class URLConnector {
    
    public static final String module = URLConnector.class.getName();

    private URLConnection connection = null;
    private URL url = null;
    private String clientCertAlias = null;
    private boolean timedOut = false;

    protected URLConnector() {}    
    protected URLConnector(URL url, String clientCertAlias) {
        this.clientCertAlias = clientCertAlias;
        this.url = url;
    }
    
    protected synchronized URLConnection openConnection(int timeout) throws IOException {       
        Thread t = new Thread(new URLConnectorThread());
        t.start();
              
        try {
            this.wait(timeout);
        } catch (InterruptedException e) {
            if (connection == null) {
                timedOut = true;
            } else {
                close(connection);
            }
            throw new IOException("Connection never established");
        }

        if (connection != null) {
            return connection;
        } else {
            timedOut = true;
            throw new IOException("Connection timed out");
        }
    }
    
    public static URLConnection openConnection(URL url) throws IOException {
        return openConnection(url, 30000);
    }
    
    public static URLConnection openConnection(URL url, int timeout) throws IOException {
        return openConnection(url, timeout, null);
    }
    
    public static URLConnection openConnection(URL url, String clientCertAlias) throws IOException {
        return openConnection(url, 30000, clientCertAlias);
    }
      
    public static URLConnection openConnection(URL url, int timeout, String clientCertAlias) throws IOException {
        URLConnector uc = new URLConnector(url, clientCertAlias);
        return uc.openConnection(timeout);
    }    

    // special thread to open the connection
    private class URLConnectorThread implements Runnable {
        public void run() {
            URLConnection con = null;
            try {
                con = url.openConnection();
                if ("HTTPS".equalsIgnoreCase(url.getProtocol())) {
                    HttpsURLConnection scon = (HttpsURLConnection) con;
                    try {
                        scon.setSSLSocketFactory(getSSLSocketFactory(clientCertAlias));
                    } catch (GeneralSecurityException gse) {
                        Debug.logError(gse, module);
                    }
                }
            } catch (IOException e) {
                Debug.logError(e, module);
            }

            synchronized (URLConnector.this) {
                if (timedOut && con != null) {
                    close(con);
                } else {
                    connection = con;
                    URLConnector.this.notify();
                }
            }
        }
    }
    
    // closes the HttpURLConnection does nothing to others
    private static void close(URLConnection con) {
        if (con instanceof HttpURLConnection) {
            ((HttpURLConnection) con).disconnect();
        }
    }
    
    // gets a aliased SSL socket factory
    private static SSLSocketFactory getSSLSocketFactory(String alias) throws IOException, GeneralSecurityException {
        KeyManager[] km = KeyStoreUtils.getKeyManagers(alias);
        TrustManager[] tm = KeyStoreUtils.getTrustManagers();
            
        // may want to have this in the properties file
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(km, tm, null);
        return (SSLSocketFactory) context.getSocketFactory();        
    }
}
