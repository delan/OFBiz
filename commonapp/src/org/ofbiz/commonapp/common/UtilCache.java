package org.ofbiz.commonapp.common;

import java.util.*;

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
  /** A list of the elements order by Least Recent Use
   */  
  protected LinkedList keyLRUList = new LinkedList();
  /** A hashtable containing a value for each element.
   */  
  protected Hashtable valueTable = new Hashtable();
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
  protected Hashtable expireTable = new Hashtable();
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
  }
  
  /** Constructor which specifies the maxSize.
   * @param maxSize The maxSize member is set to this value
   */  
  public UtilCache(long maxSize)
  {
    this.maxSize = maxSize;
  }
  
  /** Default constructor, all members stay at default values.
   */  
  public UtilCache()
  {
  }
  
  /** Puts or loads the passed element into the cache
   * @param key The key for the element, used to reference it in the hastables and LRU linked list
   * @param value The value of the element
   */  
  public synchronized void put(Object key, Object value) 
  {
    if(key == null) return;
    
    valueTable.put(key, value);
    expireTable.put(key, new Long(new Date().getTime()));
    if(valueTable.containsKey(key))
    {
      keyLRUList.remove(key);
      keyLRUList.addFirst(key);
    }
    else
    {
      keyLRUList.addFirst(key);
      if(valueTable.size() > maxSize && maxSize >= 0)
      {
        Object lastKey = keyLRUList.getLast();
        valueTable.remove(lastKey);
        keyLRUList.remove(lastKey);
      }
    }
  }
  
  /** Gets an element from the cache according to the specified key
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
    Object value = valueTable.get(key);
    if(value == null) 
    {
      missCount++;
      return null;
    }
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
    keyLRUList.clear();
    clearCounters();
  }
  
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
  public long geExpireTime() 
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

  /** Returns a boolean specifying whether or not an element with the specified key is in the cache
   * @param key The key for the element, used to reference it in the hastables and LRU linked list
   * @return True is the cache contains an element corresponding to the specified key, otherwise false
   */  
  public boolean containsKey(Object key)
  {
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
    if(time.longValue() + expireTime > curTime.longValue()) return true;
    return false;
  }
}
