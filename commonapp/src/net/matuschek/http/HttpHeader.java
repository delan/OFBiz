package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

/**
 * This object represents a HTTP header. A header simply consist
 * of a name and a value separated by ":"
 *
 * @author Daniel Matuschek 
 * @version $Id$
*/
public class HttpHeader {

  /** the name (e.g. Content-Length, Set-Cookie, ...) */
  private String name="";

  /** the value (everything behind the first colon */
  private String value="";

  /**
   * initializes the HttpHeader from a given name/value pair
   */
  public HttpHeader(String name, String value) {
    this.name=name;
    this.value=value;
  }
  
  /**
   * initializes the HttpHeader from a line (request or response) 
   * @param line a HTTP header line in the format name: value
   */
  public HttpHeader(String httpLine) {
    int pos=0;
    pos=httpLine.indexOf(":");
    if (pos == -1) { return; }

    name=httpLine.substring(0,pos);
    value=httpLine.substring(pos+1).trim();
  }
  
  public String getName() { 
    return name; 
  }
  
  public void setName(String name) { 
    this.name = name; 
  }
  
  public String getValue() { 
    return value; 
  }
  
  public void setValue(String value) { 
    this.value = value; 
  }

  public String toString() {
    return toLine();
  }

  /**
   * Converts the object to a String
   * @return a name: value String
   */
  public String toLine() {
    return name+": "+value;
  }

  /**
   * Is this a Set-Cookie header ?
   * @return true if this header sets a cookie.
   */
  public boolean isSetCookie() {
    return name.equalsIgnoreCase("Set-Cookie");
  }
  
}
