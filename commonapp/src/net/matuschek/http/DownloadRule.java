package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

/**
 * This class implements a rule what documents should be
 * downloaded based on file size and mime type
 * 
 * @author Daniel Matuschek
 * @version $Id$
 */
public class DownloadRule  {

  private final static int MINDEFAULT = 0;
  private final static int MAXDEFAULT = Integer.MAX_VALUE;

  /** basic mime type **/
  private String mimeBaseType=null;
  private String mimeSubType=null;
  private int minSize=MINDEFAULT;
  private int maxSize=MAXDEFAULT;
  
  /** allow or deny download ? **/
  private boolean allow;
  

  /**
     empty constructor that does nothing
  **/
  public DownloadRule() {
  }

  
  /**
     Get the value of minSize.
     @return Value of minSize.
  **/
  public int getMinSize () { 
    return minSize;
  }
  
  /**
     Set the value of minSize.
     @param v  Value to assign to minSize.
  **/
  public void setMinSize(int  minSize ) {
    if (minSize >= MINDEFAULT) {
      this.minSize = minSize;
    };
  }
  
  /**
     Get the value of maxSize.
     @return Value of maxSize.
  **/
  public int getMaxSize () { 
    return maxSize;
  }
  
  /**
     Set the value of maxSize.
     @param v  Value to assign to maxSize.
  **/
  public void setMaxSize(int  maxSize ) {
    if (maxSize >= MINDEFAULT) {
      this.maxSize = maxSize;
    }
  }
  
  /**
     Get the value of allow.
     @return Value of allow.
  **/
  public boolean getAllow () { 
    return allow;
  }
  
  /**
     Set the value of allow.
     @param v  Value to assign to allow.
  **/
  public void setAllow(boolean  allow ) {
    this.allow = allow;
  }
  
  /**
     Get the value of mimeBaseType.
     @return Value of mimeBaseType.
  **/
  public String getMimeBaseType () { 
    return mimeBaseType;
  }
  
  /**
     Set the value of mimeBaseType.
     @param v  Value to assign to mimeBaseType.
  **/
  public void setMimeBaseType(String mimeBaseType) {
    this.mimeBaseType = mimeBaseType;
  }
  
  /**
     Get the value of mimeSubType.
     @return Value of mimeSubType.
  **/
  public String getMimeSubType () { 
    return mimeSubType;
  }
  
  /**
     Set the value of mimeSubType.
     @param v  Value to assign to mimeSubType.
  **/
  public void setMimeSubType(String  mimeSubType ) {
    this.mimeSubType = mimeSubType;
  }
  
  /**
     Get the value of mimeType.
     @return Value of mimeType.
  **/
  public String getMimeType () { 
        return mimeBaseType+"/"+mimeSubType;
  }
  
  /**
     Set the value of mimeType.
     @param v  Value to assign to mimeType.
  **/
  public void setMimeType(String mimeType) 
    throws IllegalArgumentException
  {
    int pos = mimeType.indexOf("/");
    if (pos < 0) {
      throw new IllegalArgumentException("mime type must be in the format "+
					 " basetype/subtype");
    }

    this.mimeBaseType = mimeType.substring(0,pos);
    this.mimeSubType = mimeType.substring(pos+1);
  }


  public boolean matches(String mimeBaseType,
			 String mimeSubType,
			 int size) {
    if (simpleStringMatch(mimeBaseType,this.mimeBaseType) &&
	simpleStringMatch(mimeSubType,mimeSubType)) {
      if (size >= 0) {
	if ((size>=this.minSize) && (size<= this.maxSize)) {
	  return true;
	}
      } else {
	// if sizes are default (0, MAXINT), this rule belongs
	// to ALL documents of this type (it matches), otherwise it depends
	// on size and doesn't match !
	if ((this.minSize == MINDEFAULT) && 
	    (this.maxSize == MAXDEFAULT)) {
	  return true;
	} else {
	  return false;
	}
      }
    }
    return false;
  }
    
  /**
     matches the given string to the rule string
     @param value a string to test
     @param rule rule used (can be a value or "* that will
     match anything
     @return true is value is equal to rule or rule is "*"
   **/
  protected boolean simpleStringMatch(String value, String rule) {
    if (rule.equals("*")) {
      return true;
    }
    if (value.equalsIgnoreCase(rule)) {
      return true;
    }
    return false;
  }
  

  /**
     Convert rule to a String
     @return a String representation of this rule. Format may change
     without notice (only useful for debugging or logging)
   **/
  public String toString() {
    String s=null;
    if (allow) {
      s="allow";
    } else {
      s="deny";
    }
    return mimeBaseType+"/"+mimeSubType+" >"+minSize+" <"+maxSize+" "+s;
  }
  
} // DownloadRule
