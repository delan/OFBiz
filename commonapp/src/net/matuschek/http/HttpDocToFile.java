package net.matuschek.http;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

import java.net.URL;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * DocumentManager that will store document contents in a file.
 *
 * @author Daniel Matuschek 
 * @version $Revision$
 */
public class HttpDocToFile implements HttpDocManager 
{
  /**
   * directory where the files will be created
   */
  private String baseDir;


  /**
   * the object will not store files smaller then this size !
   */
  private int minFileSize;
  

  /**
   * defines if special characters in the URL should be replaced
   * by "normal" characters
   * @see #setReplaceAllSpecials(boolean)
   */
  private boolean replaceAllSpecials = false;

  
  /**
   * creates a new HttpDocToFile object that will store the
   * documents in the given directory
   */
  public HttpDocToFile(String baseDir) {
    this.baseDir = baseDir;
  }
  

  /**
   * store document (that means write it to disk)
   * @param doc the document to store
   * @exception DocManagerException if the document can't be stored
   * (some IO error occured)
   */
  public void processDocument(HttpDoc doc) 
    throws DocManagerException
  {
    if (doc == null) {
      return;
    }
    String filename = Url2Filename(doc.getURL());
    if (doc.getContent().length >= minFileSize) {
      try {
	createDirs(filename);
	BufferedOutputStream os = 
	  new BufferedOutputStream(new FileOutputStream(filename));
	os.write(doc.getContent());
	os.flush();
	os.close();
      } catch (IOException e) {
	throw new DocManagerException(e.getMessage());
      }
    }
  }
  

  /**
   * gets the value of baseDir
   * @return the value of baseDir
   */
  public String getBaseDir() {
    return baseDir;
  }
  

  /**
   * sets the value of basedir
   * @param baseDir the new value of baseDir
   */
  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }
  

  /**
   * converts an URL to a filename http://host/path will 
   * be converted to basedir/host/path
   * @param URL a URL to convert, must not be null
   * @return a pathname
   */
  protected String Url2Filename(URL u) {
    StringBuffer sb = new StringBuffer();

    sb.append(baseDir);
    sb.append(File.separatorChar);
    sb.append(u.getHost());
    sb.append(u.getFile());

    // is there a query part ?
    // that is something after the file name seperated by ?
    String query = u.getQuery();
    if ((query != null) &&
	(!query.equals(""))) {
      sb.append(File.separatorChar);
      sb.append(query);
    }

    // filename that ends with /
    // are directories, we will name the file "index.html"
    if (sb.charAt(sb.length()-1) == '/') {
      sb.append("index.html");
    } 

    // postprocess filename (replace special characters)
    for (int i=0; i<sb.length(); i++) {
      char c=sb.charAt(i);
      char newc=(char)0;

      // replace / by operating system file name separator
      if (c == '/') {
	newc = File.separatorChar;
      }
      
      // replace special characters from CGIs
      if (replaceAllSpecials) {
	if ((c == '?')
	    || (c == '=')
	    || (c == '&')) {
	  newc = '-';
	}
      }

      if ((newc != (char)0) 
	  && (newc != c)) {
	sb.setCharAt(i,newc);
      }
    }

    return sb.toString();
  }
  

  /** 
   * creates all directories that are needed to place the 
   * file filename if they don't exists 
   * @param filename the full path name of a file
   */
  protected void createDirs(String filename) throws IOException {
    int pos = -1;
    // look for the last directory separator in the filename
    for (int i = filename.length() - 1; i >= 0; i--) {
      if (filename.charAt(i) == File.separatorChar) {
	pos = i;
	i = -1;
      }
    }
    File dir = new File(filename.substring(0, pos));
    dir.mkdirs();
  }
  

  /**
   * gets the value of minFileSize. Files smaller then this size
   * (in Bytes) will not be saved to disk !
   * @return the value of minFileSize 
   */
  public int getMinFileSize() {
    return minFileSize;
  }

  
  /**
   * sets the value of minFileSize
   * @param minFileSize the new value of minFileSize
   * @see #getMinFileSize()
   */
  public void setMinFileSize(int minFileSize) {
    this.minFileSize = minFileSize;
  }


  /**
   * Get the value of replaceAllSpecials.
   *
   * if replaceAllSpecials is true, all sepcial characters in the URL
   * will be replaced by "-". This is useful for operating system that
   * can't handle files with special characters in the filename (e.g.
   * Windows)
   *
   * @return value of replaceAllSpecials.
   */
  public boolean isReplaceAllSpecials() {
    return replaceAllSpecials;
  }
  

  /**
   * Set the value of replaceAllSpecials.
   *
   * if replaceAllSpecials is true, all sepcial characters in the URL
   * will be replaced by "-". This is useful for operating system that
   * can't handle files with special characters in the filename (e.g.
   * Windows)
   *
   * @param v  Value to assign to replaceAllSpecials.
   */
  public void setReplaceAllSpecials(boolean  v) {
    this.replaceAllSpecials = v;
  } 

}


