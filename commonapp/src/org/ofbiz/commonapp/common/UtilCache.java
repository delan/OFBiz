package org.ofbiz.commonapp.common;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * <p><b>Title:</b> Generic Object Cache
 * <p><b>Description:</b>
 * <p> Generalized caching utility. Provides a number of caching features:
 *   <ul>
 *    <li>Limited or unlimited element capacity
 *    <li>If limited, removes elements with the LRU (Least Recently Used) algorithm
 *    <li>Keeps track of when each element was loaded into the cache
 *    <li>Using the expireTime can report whether a given element has expired
 *    <li>Counts misses and hits
 *   </ul>
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     David Jones
 *@created    May 16, 2001
 *@version    1.0
 */
public class UtilCache
{
  /** A static Hashtable to keep track of all of the UtilCache instances.
   */  
  public static Hashtable utilCacheTable = new Hashtable();
  /** An index number appended to utilCacheTable names when there are conflicts.
   */  
  protected static int defaultIndex = 1;
  /** The name of the UtilCache instance, is also the key for the instance in utilCacheTable.
   */  
  protected String name;

  /** A list of the elements order by Least Recent Use
   */  
  public LinkedList keyLRUList = new LinkedList();
  /** A hashtable containing a value for each element.
   */  
  public Hashtable valueTable = new Hashtable();
  /** A count of the number of cache hits
   */  
  protected long hitCount = 0;
  /** A count of the number of cache misses
   */  
  protected long missCount = 0;
  /** The maximum number of elements in the cache.
   * If set to 0, there will be no limit on the number of elements in the cache.
   */  
  protected long maxSize = 0;
  
  /** A hashtable containing a Long integer representing the time that the corresponding element was first loaded
   */  
  public Hashtable expireTable = new Hashtable();
  /** Specifies the amount of time since initial loading before an element will be reported as expired.
   * If set to 0, elements will never expire.
   */  
  protected long expireTime = 0;

  /** Constructor which specifies the maxSize and expireTime.
   * @param maxSize The maxSize member is set to this value
   * @param expireTime The expireTime member is set to this value
   */  
  public UtilCache(long maxSize, long expireTime)
  {
    this.maxSize = maxSize;
    this.expireTime = expireTime;
    name = "specified"+(defaultIndex++);
    utilCacheTable.put(name,this);
  }
  
  /** This constructor takes a name for the cache, puts itself in the utilCacheTable.
   * It also uses the cacheName to lookup the initialization parameters from cache.properties.
   * @param cacheName The name of the cache.
   */  
  public UtilCache(String cacheName)
  {
    ResourceBundle res = ResourceBundle.getBundle("cache");
    if(res != null)
    {
      String value = null;
      Long longValue = null;
      try { value = res.getString(cacheName + ".maxSize"); } catch(Exception e) {}
      try { longValue = new Long(value); } catch(Exception e) {}
      if(longValue != null) maxSize = longValue.longValue();
      try { value = res.getString(cacheName + ".expireTime"); } catch(Exception e) {}
      try { longValue = new Long(value); } catch(Exception e) {}
      if(longValue != null) expireTime = longValue.longValue();
    }

    name = cacheName;
    if(utilCacheTable.containsKey(cacheName)) name = name + (defaultIndex++);
    utilCacheTable.put(name,this);
  }

  /** Default constructor, all members stay at default values as defined in cache.properties, or the defaults in this file if cache.properties is not found, or there are no 'default' entries in it.
   */  
  public UtilCache()
  {
    ResourceBundle res = ResourceBundle.getBundle("cache");
    if(res != null)
    {
      String value = null;
      Long longValue = null;
      try { value = res.getString("default.maxSize"); } catch(Exception e) {}
      try { longValue = new Long(value); } catch(Exception e) {}
      if(longValue != null) maxSize = longValue.longValue();
      try { value = res.getString("default.expireTime"); } catch(Exception e) {}
      try { longValue = new Long(value); } catch(Exception e) {}
      if(longValue != null) expireTime = longValue.longValue();
    }

    name = "default"+(defaultIndex++);
    utilCacheTable.put(name,this);
  }
  
  /** Puts or loads the passed element into the cache
   * @param key The key for the element, used to reference it in the hastables and LRU linked list
   * @param value The value of the element
   */  
  public synchronized void put(Object key, Object value) 
  {
    if(key == null) return;
    
    if(valueTable.containsKey(key))
    {
      keyLRUList.remove(key);
      keyLRUList.addFirst(key);
    }
    else
    {
      keyLRUList.addFirst(key);
    }
    valueTable.put(key, value);
    expireTable.put(key, new Long(new Date().getTime()));
    if(valueTable.size() > maxSize && maxSize != 0)
    {
      Object lastKey = keyLRUList.getLast();
      remove(lastKey);
    }
  }
  
  /** Gets an element from the cache according to the specified key.
   * If the requested element hasExpired, it is removed before it is looked up which causes the function to return null.
   * @param key The key for the element, used to reference it in the hastables and LRU linked list
   * @return The value of the element specified by the key
   */  
  public Object get(Object key) 
  {
    if(key == null)
    {
      missCount++;
      return null;
    }
    if(hasExpired(key))
    {
      //note that print.info in debug.properties cannot be checked through UtilProperties here, it would cause infinite recursion...
      //System.out.println("Element has expired with key " + key);
      remove(key);
    }
    Object value = valueTable.get(key);
    if(value == null) 
    {
      //System.out.println("Element not found with key " + key);
      missCount++;
      return null;
    }
    //System.out.println("Element found with key " + key);
    hitCount++;
    keyLRUList.remove(key);
    keyLRUList.addFirst(key);
    return value;
  }
  
  /** Removes an element from the cache according to the specified key
   * @param key The key for the element, used to reference it in the hastables and LRU linked list
   * @return The value of the removed element specified by the key
   */  
  public synchronized Object remove(Object key) 
  {
    if(key != null && valueTable.containsKey(key))
    {
      Object value = valueTable.get(key);
      valueTable.remove(key);
      expireTable.remove(key);
      keyLRUList.remove(key);
      return value;
    }
    else
    {
      missCount++;
      return null;
    }
  }
  
  /** Removes all element from the cache
   */  
  public synchronized void clear() 
  {
    //Enumeration e;
    //for (e = valueTable.keys(); e.hasMoreElements();) remove(e.nextElement());
    valueTable.clear();
    expireTable.clear();
    keyLRUList.clear();
    clearCounters();
  }
  
  /** Getter for the name of the UtilCache instance.
   * @return The name of the instance
   */  
  public String getName() { return name; }

  /** Returns the number of successful hits on the cache
   * @return The number of successful cache hits
   */  
  public long getHitCount() { return hitCount; }
  
  /** Returns the number of cache misses
   * @return The number of cache misses
   */  
  public long getMissCount() { return missCount; }
  
  /** Clears the hit and miss counters
   */  
  public void clearCounters() 
  {
    hitCount = 0;
    missCount = 0;
  }
  
  /** Sets the maximum number of elements in the cache.
   * If 0, there is no maximum.
   * @param maxSize The maximum number of elements in the cache
   */  
  public void setMaxSize(long maxSize) 
  {
    //if the new maxSize is less than the old maxSize, shrink the cache.
    if((maxSize < this.maxSize || this.maxSize == 0) && maxSize != 0)
    {
      while(valueTable.size() > maxSize)
      {
        Object lastKey = keyLRUList.getLast();
        remove(lastKey);
      }
    }
    this.maxSize = maxSize;
  }
  
  /** Returns the current maximum number of elements in the cache
   * @return The maximum number of elements in the cache
   */  
  public long getMaxSize() 
  {
    return maxSize;
  }
  
  /** Sets the expire time for the cache elements.
   * If 0, elements never expire.
   * @param expireTime The expire time for the cache elements
   */  
  public void setExpireTime(long expireTime) 
  {
    this.expireTime = expireTime;
  }
  
  /** return the current expire time for the cache elements
   * @return The expire time for the cache elements
   */  
  public long getExpireTime() 
  {
    return expireTime;
  }
  
  /** Returns the number of elements currently in the cache
   * @return The number of elements currently in the cache
   */  
  public long size() 
  {
    return valueTable.size();
  }

  /** Returns a boolean specifying whether or not an element with the specified key is in the cache.
   * If the requested element hasExpired, it is removed before it is looked up which causes the function to return false.
   * @param key The key for the element, used to reference it in the hastables and LRU linked list
   * @return True is the cache contains an element corresponding to the specified key, otherwise false
   */  
  public boolean containsKey(Object key)
  {
    if(hasExpired(key)) remove(key);    
    return valueTable.containsKey(key);
  }

  /** Returns a boolean specifying whether or not the element corresponding to the key has expired.
   * Only returns true if element is in cache and has expired. Error conditions return false.
   *
   * @param key The key for the element, used to reference it in the hastables and LRU linked list
   * @return True is the element corresponding to the specified key has expired, otherwise false
   */  
  public boolean hasExpired(Object key)
  {
    if(expireTime == 0) return false;
    if(key == null) return false;
    Long time = (Long)expireTable.get(key);
    if(time == null) return false;
    Long curTime = new Long(new Date().getTime());
    if(time.longValue() + expireTime < curTime.longValue()) return true;
    return false;
  }

// =============================== WEBEVENTS ============================
  /** An HTTP WebEvent handler the specified element from the specified cache
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return
   */  
  public static boolean removeElementEvent(HttpServletRequest request, HttpServletResponse response)
  {
    String name = request.getParameter("UTIL_CACHE_NAME");
    if(name==null) return true;
    String numString = request.getParameter("UTIL_CACHE_ELEMENT_NUMBER");
    if(numString==null) return true;
    int number;
    try { number = Integer.parseInt(numString); }
    catch(Exception e) { return true; }
    
    UtilCache utilCache = (UtilCache)utilCacheTable.get(name);
    if(utilCache != null)
    {
      Object key = utilCache.keyLRUList.get(number);
      if(key != null) utilCache.remove(key);
    }
    return true;
  }

  /** An HTTP WebEvent handler that clears the named cache
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return
   */  
  public static boolean clearEvent(HttpServletRequest request, HttpServletResponse response)
  {
    String name = request.getParameter("UTIL_CACHE_NAME");
    if(name==null) return true;
    UtilCache utilCache = (UtilCache)utilCacheTable.get(name);
    if(utilCache != null) utilCache.clear();
    return true;
  }

  /** An HTTP WebEvent handler that updates the named cache
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return
   */  
  public static boolean updateEvent(HttpServletRequest request, HttpServletResponse response)
  {
    String name = request.getParameter("UTIL_CACHE_NAME");
    if(name==null) return true;
    String maxSizeStr = request.getParameter("UTIL_CACHE_MAX_SIZE");
    String expireTimeStr = request.getParameter("UTIL_CACHE_EXPIRE_TIME");
    
    Long maxSize=null, expireTime=null;
    try { maxSize = Long.valueOf(maxSizeStr); } catch(Exception e) {}
    try { expireTime = Long.valueOf(expireTimeStr); } catch(Exception e) {}
    
    UtilCache utilCache = (UtilCache)utilCacheTable.get(name);
    if(utilCache != null)
    {
      if(maxSize!=null) utilCache.setMaxSize(maxSize.longValue());
      if(expireTime!=null) utilCache.setExpireTime(expireTime.longValue());
    }
    return true;
  }
}
