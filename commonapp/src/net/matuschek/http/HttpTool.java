package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

import net.matuschek.http.connection.HttpConnection;
import net.matuschek.http.cookie.Cookie;
import net.matuschek.http.cookie.CookieManager;
import net.matuschek.http.cookie.CookieException;


import net.matuschek.util.ChunkedInputStream;
import net.matuschek.util.ByteBuffer;
import net.matuschek.util.Base64;
import net.matuschek.util.LimitedBandwidthStream;

import org.apache.log4j.Category;

import java.net.URL;
import java.net.InetAddress;
import java.net.Socket;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.BufferedInputStream;
import java.io.OutputStreamWriter;


/**
 * Class for retrieving documents from HTTP servers.
 *
 * <p>The main purpose of this class is to retrieve a document
 * from an HTTP server. </p>
 *
 * <p>For many purposes the Java URLInputStream is good for this,
 * but if you want to have full control over the HTTP headers 
 * (both request and response headers), HttpTool is the answer. </p>
 *
 * <p>Also it defines a callback interface to inform a client about
 * the state of the current download operation. </p>
 * 
 * <p>It is possible to abort a download after getting the 
 * HTTP response headers from the server (e.g. if a document of
 * this Content-Type is useless for your application or the document
 * is to big or whatever you like) </p>
 * 
 * <p>HttpTool is reusuable. You should initializes it once and use
 * it for every download operation.</p>
 * 
 * @author Daniel Matuschek 
 * @version $Id $*/
public class HttpTool {

  /** Carriage return */
  final static byte CR = 13;

  /** Line feed */
  final static byte LF = 10;

  /** used HTTP version */
  final static String HTTP_VERSION="HTTP/1.1";

  /* Status constants */ 

  /** HTTP connection will be established */
  public final static int STATUS_CONNECTING=0;
  /** HTTP connection was established, but no data where retrieved */
  public final static int STATUS_CONNECTED=1;
  /** data will be retrieved now */
  public final static int STATUS_RETRIEVING=2;
  /** download finished */
  public final static int STATUS_DONE=3;
  /** download could not be finished because a DownloadRule denied it */
  public final static int STATUS_DENIEDBYRULE=4;

  /** default HTTP port */
  private final static int DEFAULT_HTTPPORT = 80;

  /** default agent name */
  private final static String AGENTNAME = 
    "JoBo/@JOBO_VERSION@"
    +"(http://www.matuschek.net/jobo.html)";

  /** 
   * default update interval for calls of the callback interfaces 
   * (in bytes)
   */
  private final static int DEFAULT_UPDATEINTERVAL =1024;

  /** default socket timeout in seconds */
  private final static int DEFAULT_SOCKETTIMEOUT=60;

  /** HTTP AgentName header */
  private String agentName = AGENTNAME;
  
  /** HTTP Referer header */
  private String referer = null;

  /** HTTP From header */
  private String fromAddress = null;
  
  /** 
   * maximal used bandwidth in bytes per second 
   * 0 disables bandwidth limitations
   */
  private int bandwidth = 0;

  /** proxy address */
  private InetAddress proxyAddr = null;

  /** proxy port number */
  private int proxyPort = 0;

  /** textual description of the proxy (format host:port) */
  private String proxyDescr="";

  /** timeout for getting data in seconds */
  private int socketTimeout = DEFAULT_SOCKETTIMEOUT;

  /** HttpTool should accept and use cookies */
  private boolean cookiesEnabled = true;

  /** Log4J Category object for logging */
  private Category log = null;
  
  /** @link dependency */
  /*#HttpDoc lnkHttpDoc;*/


  /**
   * defines after how many bytes read from the web 
   * server the Callback interface will be called 
   * (default updates after one kilobyte)
   */
  private int updateInterval = DEFAULT_UPDATEINTERVAL;
  
  /**
   * callback interface that will be used after n bytes are
   * read from the web server to update the state of the current
   * retrieve operation to the application
   */
  private HttpToolCallback callback=null;

  /**
   * DownloadRuleSet tells the HttpTool, if it should download
   *  the whole file after getting the headers
   */
  private DownloadRuleSet downloadRules = null;

  /**
   * The cookie manager will be used to store cookies 
   */
  private CookieManager cookieManager = null;


  /**
   * Initializes HttpTool with a new CookieManager (that will not contain
   * any cookie).
   * Enables logging
   */
  public HttpTool() {
    this.cookieManager = new CookieManager();
    log = Category.getInstance(getClass().getName());
  }


  /**
   * Sets the Referer: HTTP header 
   * @param referer value for the Referer header
   */
  public void setReferer(String referer) {
    this.referer = referer;
  }
  
  /**
   * Sets the User-Agent: HTTP header
   * @param name name of the user agent (may contain spaces)
   */
  public void setAgentName(String name) {
    this.agentName = name;
  }
  
  /**
   * Gets the current setting of the User-Agent HTTP header
   * @return the User-Agent name
   */
  public String getAgentName() {
    return agentName;
  }


  /**
   * Sets the DownloadRules for this object <br />
   * A download rule uses the HTTP return headers to decide if the
   * download should be finished. 
   * @param rule a DownloadRule
   */
  public void setDownloadRuleSet(DownloadRuleSet rules) {
    this.downloadRules=rules;
  }


  /**
   * Gets the DownloadRules for this object
   * @return a DownloadRuleSet
   */
  public DownloadRuleSet getDownloadRuleSet() {
    return this.downloadRules;
  }


  /**
   * Gets the timeout for getting data in seconds
   * @return the value of sockerTimeout
   * @see #setTimeout(int)
   */
  public int getTimeout() {
    return this.socketTimeout;
  }


  /**
   * Sets the timeout for getting data. If HttpTool can't read
   * data from a remote web server after this number of seconds
   * it will stop the download of the current file
   * @param timeout Timeout in seconds
   */
  public void setTimeout(int timeout) {
    this.socketTimeout = timeout;
  }


  /**
   * Enable/disable cookies
   * @param enable if true, HTTP cookies will be enabled, if false
   * HttpTool will not use cookies
   */
  public void setEnableCookies(boolean enable) {
    this.cookiesEnabled=enable;
  }

  /**
   * Get the status of the cookie engine
   * @return true, if HTTP cookies are enabled, false otherwise
   */
  public boolean getEnableCookies() {
    return this.cookiesEnabled;
  }


  /** 
   *  sets a proxy to use 
   *  @param proxyDescr the Proxy definition in the format host:port
   */
  public void setProxy(String proxyDescr) 
    throws HttpException
  {
    proxyAddr=null;
    proxyPort=0;
    String proxyHost = null;

    if ((proxyDescr != null) &&
	(! proxyDescr.equals(""))) {
      int pos = proxyDescr.indexOf(":");
      if (pos > 0) {
	try {
	  String port = proxyDescr.substring(pos+1);
	  proxyHost = proxyDescr.substring(0,pos);
	  proxyPort = Integer.parseInt(port);
	  proxyAddr = InetAddress.getByName(proxyHost);
	} catch (NumberFormatException e) {
	  throw new HttpException("Proxy definition incorrect, "+
				  "port not numeric: "+
				  proxyDescr);
	} catch (UnknownHostException e) {
	  throw new HttpException("Host not found: "+proxyHost);
	}
      } else {
	throw new HttpException("Proxy definition incorrect, "+
				"fomat must be host:port: "+
				proxyDescr);	
      }
    }
    this.proxyDescr=proxyDescr;
  }


  /**
   * Gets a textual representation of the current proxy settings
   * @return return the proxy settings in the format host:port
   */
  public String getProxy() {
    return proxyDescr;
  }


  /**
   * Sets the content From: HTTP header
   * @param fromAdress an email adress (e.g. some@where.com)
   */
  public void setFromAddress(String fromAddress) { 
    this.fromAddress=fromAddress; 
  }

  
  /**
   * Gets the current callback object
   * @return the defined HttpToolCallback object
   */
  public HttpToolCallback getCallback() { 
    return callback; 
  }


  /**
   * Get the value of bandwidth.
   * @return value of bandwidth.
   */
  public int getBandwidth() {
    return bandwidth;
  }
  

  /**
   * Set the value of bandwidth.
   * @param bandwith  Value to assign to bandwidth.
   */
  public void setBandwidth(int bandwidth) {
    this.bandwidth = bandwidth;
  }
  

  /**
   * Sets a callback object
   *
   * If set this object will be used to inform about the current
   * status of the download. HttpTool will call methods of this
   * object while retrieving a document.
   *  
   * @param callback a callback object
   * @see HttpToolCallback
   */
  public void setCallback(HttpToolCallback callback) { 
    this.callback = callback; 
  }


  /**
   * Gets the current update interval
   * @return the update interval in bytes
   * @see #setUpdateInterval(int)
   */
  public int getUpdateInterval() { 
    return updateInterval; 
  }


  /**
   * Sets the callback update interval
   *
   * This setting is used if a callback object is defined. Then  after
   * reading this number of bytes, the method 
   * <code>setHttpToolDocCurrentSize</code> will be called.
   * You should not set this to a value smaller then 1000 unless your
   * bandwidth is very small, because it will slow down downloads.
   *
   * @param updateInterval update interval in bytes
   *
   * @see HttpToolCallbackInterface#setHttpToolDocCurrentSize(int)
   */
  public void setUpdateInterval(int updateInterval) { 
    if (updateInterval > 0) {
      this.updateInterval = updateInterval; 
    } else {
      throw new IllegalArgumentException("updateInterval must be > 0 (was "+
					 updateInterval+")");
    }
  }

  /**
   * Delete all cookies
   */
  public void clearCookies() {
    if (cookieManager != null) {
      cookieManager.clear();
    }
  }


  /**
   * Retrieves a document from the given URL. 
   * If Cookies are enabled it will use the CookieManager to set Cookies 
   * it got from former retrieveDocument operations.
   *
   * @param u the URL to retrieve (only http:// supported yet)
   * @param method HttpConstants.GET for a GET request, HttpConstants.POST
   * for a POST request
   * @param parameters additional parameters. Will be added to the URL if
   * this is a GET request, posted if it is a POST request
   * @return a HttpDoc if a document was retrieved, null otherwise
   *
   * @see HttpConstants
   */
  public HttpDoc retrieveDocument(URL u, int method, String parameters) 
    throws HttpException 
  {
    String host = null;
    InetAddress addr = null;
    String path = null;
    String requestPath = null;
    String protocol = null;
    String userinfo = null;
    boolean chunkedEncoding = false;
    ChunkedInputStream chunkStream=null;

    // Content-Length
    int docSize = -1;
      
    int port = 0;
    HttpDoc doc = new HttpDoc();
    int i = 0;

    // set document URL
    doc.setURL(u);

    // document buffer
    ByteBuffer buff = new ByteBuffer();

    // the connection to the HTTP server
    HttpConnection httpConn = null;

    InputStream is = null;
    BufferedWriter bwrite = null;

    // get host
    host = u.getHost();
    if (host == null) {
      throw new HttpException("no host part in URL found");
    }

    // get address
    try {
      addr = InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      addr = null;
    }
    if (addr == null) {
      throw new HttpException("host part (" + host + ") does not resolve");
    }

    // get path    
    path = u.getFile();
    if (path.equals("")) {
      path = "/";
    }

    // if using the proxy, request path is the whole URL, otherwise only
    // the path part of the URL
    if (useProxy()) {
      requestPath="http://"+host+path;
    } else {
      requestPath=path;
    }

    // get user info
    userinfo = u.getUserInfo();
    if ((userinfo != null) && userinfo.equals("")) {
      userinfo = null;
    }

    // get protocol and port
    port = u.getPort();
    protocol = u.getProtocol().toLowerCase();
    if (protocol.equals("http")) {
      if (port == -1) {
	port = DEFAULT_HTTPPORT;
      }
    } else {
      throw new HttpException("protocol " + protocol + " not supported");
    }

    if (callback != null) {
      callback.setHttpToolDocUrl(u.toString());
      callback.setHttpToolStatus(STATUS_CONNECTING);
    }

    // okay, we got all needed information, try to connect to the host
    try {
      // connect and initialize streams
      // timeout is stored in seconds in HttpTool, but
      // HttpConnection uses milliseconds
      if (useProxy()) {
	httpConn = HttpConnection.createConnection(proxyAddr, 
						   proxyPort,
						   socketTimeout*1000);
      } else {
	httpConn = HttpConnection.createConnection(addr, 
						   port,
						   socketTimeout*1000);
      }

      is = new LimitedBandwidthStream(
	     new BufferedInputStream(httpConn.getInputStream(), 256),
	     bandwidth);
      bwrite = new BufferedWriter(
	         new OutputStreamWriter(httpConn.getOutputStream()));

      if (callback != null) {
	callback.setHttpToolStatus(STATUS_CONNECTED);
      }


      // write HTTP request
      // get or post ?
      if (method == HttpConstants.GET) {
	bwrite.write("GET ");
	bwrite.write(requestPath);
	if ((parameters != null) 
	    && (! parameters.equals(""))) {
	  bwrite.write("?");
	  bwrite.write(parameters);
	}

      } else if (method == HttpConstants.POST) {
	bwrite.write("POST " + requestPath);
      } else {
	throw new HttpException("HTTP method " + method + " not supported");
      }

      // last part of request line
      bwrite.write(" ");
      bwrite.write(HTTP_VERSION);
      bwrite.write("\r\n");

      // Write other headers
      bwrite.write("Host: " + host + "\r\n");
      bwrite.write("User-Agent: " + agentName + "\r\n");
      bwrite.write("From: "+fromAddress+"\r\n");
      bwrite.write("Accept: */*\r\n");
      bwrite.write("Connection: close\r\n");

      if (referer != null) {
	bwrite.write("Referer: " + referer + "\r\n");
      }

      // if we have username and password, lets write an Authorization 
      // header
      if (userinfo != null) {
	bwrite.write("Authorization: Basic ");
	bwrite.write(Base64.encode(userinfo));
	bwrite.write("\r\n");
      }
      

      // for a POST request we also need a content-length header
      if (method == HttpConstants.POST) {
	bwrite.write("Content-Type: application/x-www-form-urlencoded\r\n");
	bwrite.write("Content-Length: "+parameters.length()+"\r\n");
      }

      // if cookies are enabled, write a Cookie: header
      if (cookiesEnabled) {
	String cookieString = cookieManager.cookiesForURL(u);
	if (cookieString != null) {
	  bwrite.write("Cookie: ");
	  bwrite.write(cookieString);
	  bwrite.write("\r\n");
	  log.debug("Cookie request header: "+cookieString);
	}
      }

      // finished headers
      bwrite.write("\r\n");
      // if this is a POST request, we have to add the POST parameters
      if (method == HttpConstants.POST) {
	bwrite.write(parameters);
      }
      bwrite.flush();
      
      if (callback != null) {
	callback.setHttpToolStatus(STATUS_RETRIEVING);
      }

      // read the first line (HTTP return code)
      while ((i = is.read()) != 10) {
	if (i == -1) {
	  throw new HttpException("Could not get HTTP return code "+
				  "(buffer content is "+buff.toString()+")");
	}
	buff.append((byte)i);
      }

      String httpCode = lineString(buff.getContent());
      buff.clean();
      doc.setHttpCode(httpCode);


      // read the HTTP headers
      boolean finishedHeaders = false;
      while (!finishedHeaders) {
	i = is.read();
	if (i == -1) {
	  throw new HttpException("Could read HTTP headers");
	}
	if (i >= 32) {
	  buff.append((byte)i);
	}
	// HTTP header processing
	if (i == LF) {
	  String line = lineString(buff.getContent());
	  
	  buff.clean();
	  // empty line means "end of headers"
	  if (line.trim().equals("")) {
	    finishedHeaders = true;
	  } else {
	    HttpHeader head = new HttpHeader(line);
	    doc.addHeader(head);

	    if (cookiesEnabled
		&& head.isSetCookie()) {
	      try {
		Cookie cookie = new Cookie(head.toLine(),u);
		cookieManager.add(cookie);
		log.debug("Got a cookie "+cookie);
	      } catch (CookieException e) {
		log.info("Could not interpret cookie: "+e.getMessage());
	      }
	    }

	    // Content chunked ?
	    if (head.getName().equalsIgnoreCase("Transfer-Encoding")
		&& head.getValue().equalsIgnoreCase("chunked")) {
	      chunkedEncoding = true;
	    }

	  }
	}
      }
      buff.clean();

      // if there is a DownloadRule, ask if we should download
      // the data 
      if (downloadRules != null) {
	// if it is not allowed to download this URL, close socket
	// and return a null document
	if (! downloadRules.downloadAllowed(doc.getHttpHeader())) {
	  // Close connection
	  httpConn.close();

	  if (callback != null) {
	    callback.setHttpToolStatus(STATUS_DENIEDBYRULE);
	  }
	  return null;
	}
      }

      
      // if we got encoding "chunked", use the ChunkedInputStream
      if (chunkedEncoding) {
	chunkStream = new ChunkedInputStream(is);
      }
      

      // did we got an Content-Length header ?
      HttpHeader contentLength = doc.getHeader("Content-Length");
      if (contentLength != null) {       
	
	try { 
	  docSize = Integer.parseInt(contentLength.getValue());
	} catch (NumberFormatException e) {
	  log.error("Got a malformed Content-Length header from the server");
	  docSize = -1;
	}

	// send information to callback
	if (callback != null) {
	  callback.setHttpToolDocSize(docSize);
	}

	// initialize the byte buffer with the given document size
	// there is no need to increase the buffer size dynamically
	if (docSize > 0) {
	  buff.setSize(docSize);
	}
      }

      // read data
      boolean finished = false;
      int count=0;

      while (! finished) {
	
	if (chunkedEncoding) {
	  i = chunkStream.read();
	} else {
	  i = is.read();
	}
	
	if (i == -1) {
	  // this should only happen on HTTP/1.0 responses
	  // without a Content-Length header
	  finished = true;
	} else {
	  buff.append((byte)i);
	  count++;
	}


	// finished ?
	// there are other tests then wait until read gives us a -1:

	// if there was a Content-Length header stop after reading the
	// given number of bytes
	if (count == docSize) {
	  finished = true;
	}
	
	// if it is a chunked stream we should use the isDone method
	// to look if we reached the end
	if (chunkedEncoding) {
	  if (chunkStream.isDone()) {
	    finished=true;
	  }
	}
	

	// should we call the callback interface ?
	if (callback != null) {
	  if (((buff.length() % updateInterval) == 0)
	      || finished) {
	    callback.setHttpToolDocCurrentSize(buff.length());
	  }
	}

	
      }
      
      doc.setContent(buff.getContent());

      // close everything
      //      bwrite.close();
      //      is.close();
      httpConn.close();
      
      if (callback != null) {
	callback.setHttpToolStatus(STATUS_DONE);
      }

    } catch (IOException e) {
      throw new HttpException(e.getMessage());
    }

    return doc;
  }



  /**
   * should I use a proxy ?
   * @return true if a proxy was configured, false otherwise
   */
  protected boolean useProxy() {
    return (proxyAddr != null);
  }

  /**
   * convert an array of bytes to a String. if the last byte is an CR
   * it will be ignored
   */
  protected String lineString(byte[] b) {
    if (b.length == 0) {
      return "";
    }

    if (b[b.length-1] != CR) {
      return new String(b);
    } else {
      return new String(b,0,b.length-1);
    }
  }

}
