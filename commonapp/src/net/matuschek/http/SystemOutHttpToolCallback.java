package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

/**
 * Simple HttpTool callback that prints information on stdout
 *
 * @author Daniel Matuschek <daniel@matuschek.net>
 * @version $Id$
 */
public class SystemOutHttpToolCallback 
  implements HttpToolCallback {

  int size=0;

  public void setHttpToolDocUrl(String url) {
    System.out.println("URL: "+url);
  }

  public void setHttpToolDocSize(int size) {
    this.size=size;
  }

  public void setHttpToolDocCurrentSize(int size) {
    System.out.print(size+" of "+this.size+"\r");
  }

  public void setHttpToolStatus(int status) {
  }
}
