/*
 * $Id$
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
package org.ofbiz.core.util;

import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * Send HTTP GET/POST requests.
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class HttpClientJakarta {
    
    private int timeout = 30000;
    private boolean followRedirects = true;
    
    private String url = null;
    private Map parameters = null;
    private Map headers = null;
    
    private URL requestUrl = null;
    private HttpMethod httpMethod = null;
    //private URLConnection con = null;

    /** Creates an empty HttpClient object. */
    public HttpClientJakarta() {}

    /** Creates a new HttpClient object. */
    public HttpClientJakarta(URL url) {
        this.url = url.toExternalForm();
    }

    /** Creates a new HttpClient object. */
    public HttpClientJakarta(String url) {
        this.url = url;
    }

    /** Creates a new HttpClient object. */
    public HttpClientJakarta(String url, Map parameters) {
        this.url = url;
        this.parameters = parameters;      
    }

    /** Creates a new HttpClient object. */
    public HttpClientJakarta(URL url, Map parameters) {
        this.url = url.toExternalForm();
        this.parameters = parameters;
    }

    /** Creates a new HttpClient object. */
    public HttpClientJakarta(String url, Map parameters, Map headers) {
        this.url = url;
        this.parameters = parameters;
        this.headers = headers;
    }

    /** Creates a new HttpClient object. */
    public HttpClientJakarta(URL url, Map parameters, Map headers) {
        this.url = url.toExternalForm();
        this.parameters = parameters;
        this.headers = headers;
    }

    /** Sets the timeout for waiting for the connection (default 30sec) */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    /** Enables this request to follow redirect 3xx codes (default true) */
     public void followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }
    
    /** Set the URL for this request. */
    public void setUrl(URL url) {
        this.url = url.toExternalForm();
    }

    /** Set the URL for this request. */
    public void setUrl(String url) {
        this.url = url;
    }

    /** Set the parameters for this request. */
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    /** Set an individual parameter for this request. */
    public void setParameter(String name, String value) {
        if (parameters == null)
            parameters = new HashMap();
        parameters.put(name, value);
    }

    /** Set the headers for this request. */
    public void setHeaders(Map headers) {
        this.headers = headers;
    }

    /** Set an individual header for this request. */
    public void setHeader(String name, String value) {
        if (headers == null)
            headers = new HashMap();
        headers.put(name, value);
    }

    /** Return a Map of headers. */
    public Map getHeaders() {
        return headers;
    }

    /** Return a Map of parameters. */
    public Map getParameters() {
        return parameters;
    }

    /** Return a string representing the requested URL. */
    public String getUrl() {
        return url;
    }

    /** Invoke HTTP request GET. */
    public String get() throws HttpClientException {
        return sendHttpRequest("get");
    }

    /** Invoke HTTP request GET. */
    public InputStream getStream() throws HttpClientException {
        return sendHttpRequestStream("get");
    }

    /** Invoke HTTP request POST. */
    public String post() throws HttpClientException {
        return sendHttpRequest("post");
    }

    /** Invoke HTTP request POST. */
    public InputStream postStream() throws HttpClientException {
        return sendHttpRequestStream("post");
    }

    /** Returns the value of the specified named response header field. */
    public String getResponseHeader(String header) throws HttpClientException {
        if (httpMethod == null) {
            throw new HttpClientException("Connection not yet established");
        }
        return httpMethod.getResponseHeader(header).getValue();
    }

    /** Returns the key for the nth response header field. */
    public String getResponseHeaderFieldKey(int n) throws HttpClientException {
        if (httpMethod == null)
            throw new HttpClientException("Connection not yet established");
        return httpMethod.getResponseHeaders()[n].getName();
    }

    /** Returns the value for the nth response header field. It returns null of there are fewer then n fields. */
    public String getResponseHeaderField(int n) throws HttpClientException {
        if (httpMethod == null)
            throw new HttpClientException("Connection not yet established");
        return httpMethod.getResponseHeaders()[n].getValue();
    }

    /** Returns the content of the response. */
    public byte[] getResponseContent() throws java.io.IOException, HttpClientException {
        if (httpMethod == null) {
            throw new HttpClientException("Connection not yet established");
        }
        return httpMethod.getResponseBody();
    }

    /* * Returns the content-type of the response. * /
    public String getResponseContentType() throws HttpClientException {
        if (httpMethod == null)
            throw new HttpClientException("Connection not yet established");
        return httpMethod.getContentType();
    }*/

    /* * Returns the content length of the response * /
    public int getResponseContentLength() throws HttpClientException {
        if (httpMethod == null)
            throw new HttpClientException("Connection not yet established");
        return httpMethod.getContentLength();
    } */

    /* * Returns the content encoding of the response. * /
    public String getResponseContentEncoding() throws HttpClientException {
        if (con == null)
            throw new HttpClientException("Connection not yet established");
        return con.getContentEncoding();
    } */

    private String sendHttpRequest(String method) throws HttpClientException {
        try {
            this.createHttpMethod(method);
            return httpMethod.getResponseBodyAsString();
        } catch (HttpException ex) {
            throw new HttpClientException("Error getting URL content", ex);
        } catch (IOException ex) {
            throw new HttpClientException("Error getting URL content", ex);
        }
        
        /* OLD CODE
        InputStream in = sendHttpRequestStream(method);

        if (in == null) return null;

        StringBuffer buf = new StringBuffer();

        try {
            BufferedReader post = new BufferedReader(new InputStreamReader(in));
            String line = new String();

            while ((line = post.readLine()) != null) {
                buf.append(line);
                if (lineFeed)
                    buf.append("\n");
            }
        } catch (Exception e) {
            throw new HttpClientException("Error processing input stream", e);
        }
        return buf.toString();
        */
    }

    private InputStream sendHttpRequestStream(String method) throws HttpClientException {
        try {
            this.createHttpMethod(method);
            return httpMethod.getResponseBodyAsStream();
        } catch (HttpException ex) {
            throw new HttpClientException("Error getting URL content", ex);
        } catch (IOException ex) {
            throw new HttpClientException("Error getting URL content", ex);
        }


        /* OLD CODE:
        // Append the arguments to the query string if GET.
        if (method.equalsIgnoreCase("get") && arguments != null)
            url = url + "?" + arguments;
        */

        /* OLD CODE
        // Create the URL and open the connection.
        try {
            con = URLConnector.openConnection(requestUrl, timeout);
            if ((con instanceof HttpURLConnection))                 
                ((HttpURLConnection) con).setInstanceFollowRedirects(followRedirects);
            
            con.setDoOutput(true);
            con.setUseCaches(false);

            if (method.equalsIgnoreCase("post")) {
                con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                con.setDoInput(true);
            }

            if (headers != null && headers.size() > 0) {
                Set headerSet = headers.keySet();
                Iterator i = headerSet.iterator();

                while (i.hasNext()) {
                    String headerName = (String) i.next();
                    String headerValue = (String) headers.get(headerName);
                    con.setRequestProperty(headerName, headerValue);
                }
            }

            if (method.equalsIgnoreCase("post")) {
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(arguments);
                out.flush();
                out.close();
            }

            in = con.getInputStream();
        } catch (Exception e) {
            throw new HttpClientException("Error processing request", e);
        }

        return in;
        */
    }

    private void createHttpMethod(String method) throws HttpClientException, HttpException, IOException {
        // setup some SSL variables       
        String protocol = UtilProperties.getPropertyValue("jsse.properties", "java.protocol.handler.pkgs", "NONE");
        String proxyHost = UtilProperties.getPropertyValue("jsse.properties", "https.proxyHost", "NONE");
        String proxyPort = UtilProperties.getPropertyValue("jsse.properties", "https.proxyPort", "NONE");        
        String cypher = UtilProperties.getPropertyValue("jsse.properties", "https.cipherSuites", "NONE");
        if (protocol != null && !protocol.equals("NONE"))
            System.setProperty("java.protocol.handler.pkgs", protocol);
        if (proxyHost != null && !proxyHost.equals("NONE"))
            System.setProperty("https.proxyHost", proxyHost);
        if (proxyPort != null && !proxyPort.equals("NONE"))
            System.setProperty("https.proxyPort", proxyPort);
        if (cypher != null && !cypher.equals("NONE"))
            System.setProperty("https.cipherSuites", cypher);   
            
        String arguments = null;
        InputStream in = null;                     

        if (url == null) throw new HttpClientException("Cannot process a null URL.");

        if (parameters != null && parameters.size() > 0) arguments = encodeArgs(parameters);

        requestUrl = new URL(url);

        org.apache.commons.httpclient.HttpClient commonsClient = new org.apache.commons.httpclient.HttpClient();
        
        HttpMethod httpMethod = null;
        if ("get".equalsIgnoreCase(method)) {
            httpMethod = new GetMethod(requestUrl.getPath());
        } else if ("put".equalsIgnoreCase(method)) {
            httpMethod = new PutMethod(requestUrl.getPath());
        } else if ("post".equalsIgnoreCase(method)) {
            httpMethod = new PostMethod(requestUrl.getPath());
        } else if ("delete".equalsIgnoreCase(method)) {
            httpMethod = new DeleteMethod(requestUrl.getPath());
        } else if ("head".equalsIgnoreCase(method)) {
            httpMethod = new HeadMethod(requestUrl.getPath());
        }
        

        //con.setDoOutput(true);
        //con.setUseCaches(false);

        if (method.equalsIgnoreCase("post")) {
            httpMethod.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            //con.setDoInput(true);
        }

        if (headers != null && headers.size() > 0) {
            Set headerSet = headers.entrySet();
            Iterator i = headerSet.iterator();

            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String headerName = (String) i.next();
                String headerValue = (String) headers.get(headerName);
                httpMethod.setRequestHeader((String) entry.getKey(), (String) entry.getValue());
            }
        }
         
        //if (httpMethod instanceof PostMethod) {
        //    PostMethod postMethod = (PostMethod) httpMethod;
        //    postMethod.setRequestBody(arguments);
        //} else {
            httpMethod.setQueryString(arguments);
        //}

        // now why can't it find this one? commonsClient.setTimeout(timeout); // milliseconds
        commonsClient.startSession(requestUrl);
        httpMethod.setFollowRedirects(followRedirects);

        int resultCode = commonsClient.executeMethod(httpMethod);
    }

    private String encodeArgs(Map args) {
        StringBuffer buf = new StringBuffer();
        Set names = args.keySet();
        Iterator i = names.iterator();

        while (i.hasNext()) {
            String name = (String) i.next();
            String value = (String) args.get(name);

            buf.append(URLEncoder.encode(name));
            buf.append("=");
            buf.append(URLEncoder.encode(value));
            if (i.hasNext())
                buf.append("&");
        }
        return buf.toString();
    }
}
