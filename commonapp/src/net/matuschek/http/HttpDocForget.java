package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

/**
 * Simple document manager that does nothing. For debugging purposes.
 *
 * @author Daniel Matuschek 
 * @version $Revision$
 */
public class HttpDocForget implements HttpDocManager {

  public void processDocument(HttpDoc doc) {
    System.out.println("forgot document "+doc.getURL().toString());
  }
}
