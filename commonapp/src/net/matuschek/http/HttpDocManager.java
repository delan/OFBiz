package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

/**
 * An HttpDocManager does something with an HttpDoc. It is uses
 * by the WebRobot to store the retrieved documents.
 *
 * @author Daniel Matuschek
 * @version $Id$
 */

public interface HttpDocManager {

  void processDocument(HttpDoc doc) throws DocManagerException;

}
