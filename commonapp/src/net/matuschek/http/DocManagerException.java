package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
 *********************************************/

/**
 * Exception that will be thrown by HttpDocumentManagers
 * 
 * @author Daniel Matuschek
 * @version $Revision$
 */
public class DocManagerException extends Exception {

  /**
   * creates a DocManagerException with the given error message
   */
  public DocManagerException(String msg) {
    super(msg);
  }
  
} // DocManagerException
