package net.matuschek.http.connection;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

import java.io.IOException;
import java.net.InetAddress;

/**
 * This class implements an connection pool for HTTP TCP connections.
 * Actually, it doesn't pool, but this will be implemented in the next
 * time.
 * 
 * @author Daniel Matuschek 
 * @version $Id$
 */
public abstract class HttpConnectionPool {

  /** default timeout in milliseconds (60 seconds) */
  private static int DEFAULT_TIMEOUT = 60000;
  
  /** the HttpConnections */
  HttpConnection connections[];

  /** TCP socket timeout (for connect and read/write) */
  int connectionTimeout = DEFAULT_TIMEOUT;

  /**
   * Creates a new HTTP connection pool
   *
   * @param maxConnections maximal number of open connections
   */
  public HttpConnectionPool(int maxConnections) {
    connections = new HttpConnection[maxConnections];
  }


  /**
   * Sets the timeout for the HTTP connections
   * @param connectionTimeout timeout in milliseconds
   */
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  
  /**
   * Gets the timeout for the HTTP connections
   * @return timeout in milliseconds
   */
  public int getConnectionTimeout() {
    return this.connectionTimeout;
  }
  

  /**
   * Gets a connection to the given server and port. Opens a new
   * connection or uses a connection from the pool if there is one
   * for this address/port combination
   *
   * @param address the IP address to connect to
   * @param port the port to connect to (usually 80 for HTTP)
   * @return a HttpConnection object 
   * @exception IOException if the TCP socket connection could
   * not be established or all slots are used
   */
  public abstract HttpConnection 
    getConnection(InetAddress addr, int port)
    throws IOException ;


  /**
   * Gives back the given HttpConnection to the pool
   */
  public abstract void giveback(HttpConnection conn);
}
