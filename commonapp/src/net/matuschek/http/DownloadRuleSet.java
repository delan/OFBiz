package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 * This download selector decides if a file should be downloaded
 * based on the mime type and the size of the content (content-length
 * header)
 *  
 * @author Daniel Matuschek daniel@matuschek.net
 * @version $Id$
 */
public class DownloadRuleSet {


  /**********************************************************************/
  /* instance variables                                                 */
  /**********************************************************************/

  /** a vector containing all rules **/
  protected Vector rules;

  /** default behavior, if no rule matches (allow or deny)
      true = allow, false = deny **/ 
  protected boolean defaultBehavior=true;

  
  /**********************************************************************/
  /* methods                                                            */
  /**********************************************************************/

  /** 
   * initializes the DownloadRuleSet with an empty rule set 
   * if no other rules will be added, it will allow all downloads
   */
  public DownloadRuleSet() {
    rules=new Vector();
    defaultBehavior=true;
  }


  /**
   * initializes the DownloadRuleSet with a rule set
   * read from a file
   * @see #loadRuleFile(String)
   */
  public DownloadRuleSet(String filename) 
    throws IOException
  {
    this();
    loadRuleFile(filename);
  }


  /**
   *  adds a set of rules that are defined in a rule file
   * a rule file consists of lines in the format<br />
   * allow|deny mimetype/subtype <xxxx >yyyyy
   */
  public void loadRuleFile(String filename) 
    throws IOException
  {
    InputStream is = new FileInputStream(filename);
    BufferedReader reader = 
      new BufferedReader(new InputStreamReader(is));

    String line = "";
    int lineno=0;

    while (line != null) {
      line=reader.readLine();
      lineno++;

      if ((line != null) &&
	  (! line.trim().equals("")) &&
	  (! line.startsWith("#"))) {
	StringTokenizer st = new StringTokenizer(line);
	// we need at least 2 tokens
	if (st.countTokens() < 2) {
	  throw new IOException("line "+lineno+" has less then 2 fields");
	}

	String allowStr = st.nextToken();
	boolean allow = true;
	String mime = st.nextToken();

	// allow or deny ?
	if (allowStr.equalsIgnoreCase("allow")) {
	  allow=true;
	} else if (allowStr.equalsIgnoreCase("deny")) {
	  allow=false;
	} else {
	  throw new IOException("first token in line "+lineno+
				" has to be allow or deny");
	}
	  
	
	DownloadRule r = new DownloadRule();
	r.setAllow(allow);
	try {
	  r.setMimeType(mime);
	} catch (IllegalArgumentException e) {
	  throw new IOException(e.getMessage());
	}
	

	// parse < and > rules
	while (st.hasMoreTokens()) {
	  boolean isMin=true;

	  String descr=st.nextToken();
	  
	  if (descr.startsWith("<")) {
	    // it is a maximum value
	    isMin=false;
	  } else if (descr.startsWith(">")) {
	    isMin=true;
	  } else {
	    throw new IOException("can't understand "+descr+
				  " in line "+lineno);
	  }

	  int size=0;
	  try {
	    size = Integer.parseInt(descr.substring(1));
	  } catch (NumberFormatException e) {
	    throw new IOException("no numerical value "+descr+
				  " in line "+lineno);
	  }

	  if (isMin) {
	    r.setMinSize(size);
	  } else {
	    r.setMaxSize(size);
	  }
	}

	rules.add(r);
      }
    }
  }
    


  /**
   * sets the default behavior
   * @param allow allow or deny download if no matching rule was 
   * found
   */
  public void setDefault(boolean allow) {
    this.defaultBehavior=allow;
  }

  /**
   *  gets the default behaviour
   */
  public boolean getDefault() {
    return this.defaultBehavior;
  }

  /**
   * Get the value of downloadRules.
   * @return Value of downloadRules as a Vector fo DownloadRule objects.
   */
  public Vector getDownloadRules () { 
    return this.rules;
  }
  
  /**
   * Set the value of downloadRules.
   * @param v  Value to assign to downloadRules. Must be a vector
   * of DownloadRule objects
   */
  public void setDownloadRules(Vector downloadRules) {
    this.rules = downloadRules;
  }
  

  /** 
   * adds a download rule for the given mimetype/subtype
   * @param mimeType basic mime type (the part before /)
   * @param mimeSubtype mime sub type (part after /)
   * @param minSize minimal size (in bytes)
   * @param maxSize maximal size (in bytes)
   * @param allow allow or deny this download ?
   * wildchar "*" can be used as mimeType and mimeSubtype that means
   * "all". there will be no pattern matching, that means "*" matches
   * all types, but "t*" doesn't match all types that start with t
   */
  public void addRule(String mimeBaseType, String mimeSubtype, 
		      int minSize, int maxSize, boolean allow) {
    DownloadRule newrule = new DownloadRule();
    newrule.setMimeBaseType(mimeBaseType);
    newrule.setMimeSubType(mimeSubtype);
    newrule.setMinSize(minSize);
    newrule.setMaxSize(maxSize);
    newrule.setAllow(allow);
    rules.add(newrule);
  }


  /**
   * finds the first matching rule
   * @param mimetype mimeType ("type/subtype")
   * @return a rule or null if no rule was found
   */
  private DownloadRule findRule(String mimeType, int size) {
    // is it a valie mime string
    if (mimeType.indexOf("/")<0) {
      return null;
    }

    String basetype = null;
    String subtype = null;
    StringTokenizer st = new StringTokenizer(mimeType,"/");
    basetype = st.nextToken();
    subtype = st.nextToken();

    for (int i=0; i<rules.size(); i++) {
      DownloadRule rule = (DownloadRule)rules.elementAt(i);
      if (rule.matches(basetype,subtype,size)) {
	return rule;
      }
    }

    return null;
  }



  /**
   * gets the value of a httpHeader from a vector of httpHeaders
   * @param httpHeaders a Vector of HttpHeader objects
   * @param name name of the header (e.g. content-length) not case-sensitive
   * @return the value of this header or null if this header doesn't
   * exists
   */
  protected String getHeaderValue(Vector httpHeaders, String name) {
    for (int i=0; i<httpHeaders.size(); i++) {
      HttpHeader h = (HttpHeader)httpHeaders.elementAt(i);
      if (h.getName().equalsIgnoreCase(name)) {
	return h.getValue();
      }
    }
    return null;
  }
  

  public boolean downloadAllowed(Vector httpHeaders) {
    String mimeType = getHeaderValue(httpHeaders,"Content-Type");
    String sizeStr = getHeaderValue(httpHeaders,"Content-Length");

    // mimeType must exists in any HTTP response !!!
    if (mimeType == null) {
      return false;
    }

    // size MAY exist, if not use -1 for unknown
    int size=-1;
    try { 
      size = Integer.parseInt(sizeStr);
    } catch (NumberFormatException e) {}

    DownloadRule r = findRule(mimeType,size);

    if (r == null) {
      return defaultBehavior;
    } else {
      //      System.err.println(size+" "+r);
      return r.getAllow();
    }
  }

  /**
   * converts the object to a String represenation. the format may
   * change without notice. Use it only for debugging and logging.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("DownloadRule default=");
    if (defaultBehavior) {
      sb.append("true");
    } else {
      sb.append("false");
    }
    sb.append("\n");

    for (int i=0; i<rules.size(); i++) {
      sb.append(" ");
      sb.append(((DownloadRule)rules.elementAt(i)).toString());
      sb.append("\n");
    }
    return sb.toString();
  }

  
} // DownloadRuleSet
