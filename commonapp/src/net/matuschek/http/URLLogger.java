package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

import java.io.Writer;
import java.io.IOException;

/**
 * Simple document manager that logs the URL of the document to
 * a given Writer
 *
 * @author Daniel Matuschek 
 * @version $Revision$
 */
public class URLLogger implements HttpDocManager {
  
  /** Writer to write to */
  private Writer wr;

  public URLLogger(Writer wr) {
    this.wr = wr;
  }

  public void processDocument(HttpDoc doc) 
    throws DocManagerException 
  {
    try {
      wr.write(doc.getURL().toString());
      wr.write("\n");
    } catch (IOException e) {
      throw new DocManagerException("IOError: "+e.getMessage());
    }
  }
}
