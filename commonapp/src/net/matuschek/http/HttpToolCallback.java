package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

/**
 * Callback interface to inform a frontend about the state of the
 * current HttpTool download operation
 *
 * @author Daniel Matuschek
 * @version $Id $
 */
public interface HttpToolCallback {

  /** 
   * After initiating a download, this method will be called to
   * inform about the URL that will be retrieved
   *  @param URL url that will be retrieved now
   */
  void setHttpToolDocUrl(String url);
  
  /** 
   * After HttpTool got a Content-Length header
   * this method will be called to inform about the size of
   * the document to retrieve
   * @param size document size in 
   */
  void setHttpToolDocSize(int size);
  

  /** 
   * after a block of bytes was read (default after every 1024 bytes,
   * this method will be called 
   * @param size the number of bytes that where retrieved 
   */
  void setHttpToolDocCurrentSize(int size);


  /** 
   * informs about the current status of the HttpTool 
   * @param status an integer describing the current status
   * constants defined in HttpTool
   * @see HttpTool
   */
  void setHttpToolStatus(int status);
}
