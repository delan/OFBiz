package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

/**
 * Exception that will be thrown on HTTP errors
 *
 * @author Daniel Matuschek 
 * @version $Id$
 */
public class HttpException extends Exception {
  
  public HttpException(String msg) {
    super(msg);
  }
}
