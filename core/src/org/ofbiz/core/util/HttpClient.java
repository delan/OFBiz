/* 
 * $Id$
 * $Log$
 * Revision 1.1  2001/07/23 18:38:14  azeneski
 * Added in new HttpClient. Makes behind the scenes HTTP request (GET/POST)
 * and returns the output as a string.
 *
 */

package org.ofbiz.core.util;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * <p><b>Title:</b> HttpClient.java
 * <p><b>Description:</b> Send HTTP GET/POST requests.
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on June 29, 2001, 6:18 PM
 */
public class HttpClient {
        
    private String url = null;
    private HashMap parameters = null;
    private HashMap headers = null;
    
    /** Creates an empty HttpClient object. */
    public HttpClient() {        
    }
    
    /** Creates a new HttpClient object. */
    public HttpClient(URL url) {
        this.url = url.toExternalForm();
    }
    
    /** Creates a new HttpClient object. */
    public HttpClient(String url) {
        this.url = url;
    }
    
    /** Creates a new HttpClient object. */
    public HttpClient(String url, HashMap parameters ) {
        this.url = url;
        this.parameters = parameters;
    }
    
    /** Creates a new HttpClient object. */
    public HttpClient(URL url, HashMap parameters ) {
        this.url = url.toExternalForm();
        this.parameters = parameters;
    }
    
    /** Creates a new HttpClient object. */
    public HttpClient(String url, HashMap parameters, HashMap headers ) {
        this.url = url;
        this.parameters = parameters;
        this.headers = headers;
    }
    
    /** Creates a new HttpClient object. */
    public HttpClient(URL url, HashMap parameters, HashMap headers) {
        this.url = url.toExternalForm();
        this.parameters = parameters;
        this.headers = headers;
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
    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }
    
    /** Set an individual parameter for this request. */
    public void setParameter(String name, String value) {
        if ( parameters == null ) 
            parameters = new HashMap();
        parameters.put(name,value);
    }
    
    /** Set the headers for this request. */
    public void setHeaders(HashMap headers) {
        this.headers = headers;
    }
    
    /** Set an individual header for this request. */
    public void setHeader(String name, String value) {
        if ( headers == null )
            headers = new HashMap();
        headers.put(name,value);
    }
    
    /** Return a HashMap of headers. */
    public HashMap getHeaders() {
        return headers;
    }
    
    /** Return a HashMap of parameters. */
    public HashMap getParameters() {
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
    
    /** Invoke HTTP request POST. */
    public String post() throws HttpClientException {
        return sendHttpRequest("post");
    }
    
    private String sendHttpRequest(String method) throws HttpClientException {
        URL requestUrl;
        URLConnection con;
        String arguments = null;
        StringBuffer buf = new StringBuffer();
        
        if ( url == null ) 
            throw new HttpClientException("Cannot process a null URL.");
        
        if ( parameters != null && parameters.size() > 0 )
            arguments = encodeArgs(parameters);
        
        // Append the arguments to the query string if GET.
        if ( method.equalsIgnoreCase("get") && arguments != null )
            url = url + "?" + arguments;
                        
        // Create the URL and open the connection.
        try {
            requestUrl = new URL(url);
            con = requestUrl.openConnection();
            con.setDoOutput(true);
            con.setUseCaches(false);
            
            if ( method.equalsIgnoreCase("post") ) {
                con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                con.setDoInput(true);
            }
            
            if ( headers != null && headers.size() > 0 ) {
                Set headerSet = headers.keySet();
                Iterator i = headerSet.iterator();
                while ( i.hasNext() ) {
                    String headerName = (String) i.next();
                    String headerValue = (String) headers.get(headerName);
                    con.setRequestProperty(headerName,headerValue);
                }
            }
                
            if ( method.equalsIgnoreCase("post") ) {
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(arguments);
                out.flush();
                out.close();
            }
            
            InputStream in = con.getInputStream();
            BufferedReader post = new BufferedReader(new InputStreamReader(in));
            String line = new String();
            while ((line = post.readLine()) != null)
            {
                buf.append(line);
                buf.append("\n");
            }
        }
        catch ( Exception e ) {
            throw new HttpClientException(e.getMessage());
        }
        
        return buf.toString();
    }
    
    private String encodeArgs( HashMap args ) {
        StringBuffer buf = new StringBuffer();
        Set names = args.keySet();
        Iterator i = names.iterator();
        while ( i.hasNext() ) {
            String name = (String) i.next();
            String value = (String) args.get(name);
            buf.append(URLEncoder.encode(name));
            buf.append("=");
            buf.append(URLEncoder.encode(value));
            if ( i.hasNext() )
                buf.append("&");
        }
        return buf.toString();
    }            
}