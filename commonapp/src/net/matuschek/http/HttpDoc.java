package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
 *********************************************/

import java.util.Vector;
import java.util.StringTokenizer;
import java.net.URL;

import net.matuschek.http.HttpHeader;

/**
 * A HTTP document. It consists of the contents and HTTP headers.
 *
 * @author Daniel Matuschek 
 * @version $Id$
 */
public class HttpDoc {

  /** The content */
  private byte[] content;

  /**
   * The HTTP header lines
   *
   * @link aggregation
   * @associates <{HttpHeader}>
   */
  private Vector httpHeader;

  private int httpReturnCode=0;
  private URL url;

  private final static int HTTP_REDIRECTSTART=300;
  private final static int HTTP_REDIRECTEND=399;



  /**
   * Default constructor, initializes a new HttpDoc with 
   * empty headers and no content
   */
  public HttpDoc() {
    httpHeader = new Vector();
  }
  

  /**
   * Gets the content of the document
   *
   * @return an array of bytes containing the document content. This
   * may represent text or binary data
   */
  public byte[] getContent() {
    return content; 
  }
  

  /**
   * Set the content of the document
   * 
   * @param content
   */
  public void setContent(byte[] content) { 
    this.content = content; 
  }


  
  public void setHttpCode(String httpCode) { 
    StringTokenizer st = new StringTokenizer(httpCode," ");
    // an HTTP answer must have at least 2 fields
    if (st.countTokens() < 2) {
      return;
    }
    
    st.nextToken();
    String codeStr = st.nextToken();
    
    try {
      httpReturnCode = Integer.parseInt(codeStr);
    } catch (NumberFormatException e) {
      // something is wrong !!!
    }
  }


  /**
   * Add another HTTP header
   *
   * @param header an HttpHeader object to add to the lis
   * of headers
   */
  public void addHeader(HttpHeader header) {
    httpHeader.add(header);
  }

  
  /**
   * Get all HTTP header lines
   *
   * @return a Vector of HttpHeader objects
   */
  public Vector getHttpHeader() {
    return httpHeader;
  }

  /**
   * Get the content of the Location header. This header will
   * be used for REDIRECTs.
   * 
   * @return the value of the HTTP Location header.
   */
  public String getLocation() {
    HttpHeader location= getHeader("location");
    if (location == null) {
      return "";
    } else {
      return location.getValue();
    }
  }

  /**
   * Was it a redirect ?
   *
   * @return true if this document is a HTTP REDIRECT
   */
  public boolean isRedirect() {
    if ((httpReturnCode >= HTTP_REDIRECTSTART) && 
	(httpReturnCode <= HTTP_REDIRECTEND)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Was it a "normal" document ?
   */
  public boolean isOk() {
    return (httpReturnCode == HttpConstants.HTTP_OK);
  }


  /**
   * Was it not found ?
   */
  public boolean isNotFound() {
    return (httpReturnCode == HttpConstants.HTTP_NOTFOUND);
  }

  /**
   * did we get "Authorization required"
   */
  public boolean isUnauthorized() {
    return (httpReturnCode == HttpConstants.HTTP_UNAUTHORIZED);
  }

  /**
   * the the content of an HTTP header line
   */
  public HttpHeader getHeader(String name) {
    for (int i=0; i<httpHeader.size(); i++) {
      HttpHeader h = (HttpHeader)httpHeader.elementAt(i);
      if (name.equalsIgnoreCase(h.getName())) {
	return h;
      }
    }    
    return null;
  }


  /**
   * Get all the HTTP headers. This function is useful if you
   * don't know what headers exists and you want to have ALL 
   * headers
   * 
   * @return a Vector containing HttpHeader objects
   */
  public Vector getHttpHeaders() {
    return httpHeader;
  }
  

  /**
   * is the content-type text/html ?
   * 
   * @return true if the HTTP Content-Type header has the
   * value text/html
   */
  public boolean isHTML() {
    HttpHeader ct = getHeader("content-type");
    if (ct==null) { 
      return false;
    } else {
      if (ct.getValue().equalsIgnoreCase("text/html")) {
	return true;
      }
    }
    return false;
  }


  /**
   * Convert this object to a String.
   *
   * @return a String representation of this HttpDoc. Format
   * may change, therefore this should be used only for
   * logging or debugging
   */
  public String toString() {
    StringBuffer res = new StringBuffer();
    
    res.append(url.toString()+"\n\n");

    for (int i=0; i<httpHeader.size(); i++) {
      HttpHeader h = (HttpHeader)httpHeader.elementAt(i);
      res.append(h.toString());
      res.append("\n");
    }
    res.append("\n");

    res.append(new String(content));
    
    return res.toString();
  }

  /**
   * Get the full URL where this document was retrieved from
   *
   * @return an URL object containing the location where this
   * document was retrieved from 
   */
  public URL getURL() { 
    return url; 
  }


  /**
   * Set the location where this  document was retrieved from
   *
   * @param url the original location of this document
   */
  public void setURL(URL url) { 
    this.url = url; 
  }

}
