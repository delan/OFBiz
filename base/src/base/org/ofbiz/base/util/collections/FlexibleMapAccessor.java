/*
 * $Id: FlexibleMapAccessor.java,v 1.2 2004/07/15 02:11:58 jonesde Exp $
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.base.util.collections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;

/**
 * Used to flexibly access Map values, supporting the "." (dot) syntax for
 * accessing sub-map values and the "[]" (square bracket) syntax for accessing
 * list elements. See individual Map operations for more information.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.2 $
 * @since      2.1
 */
public class FlexibleMapAccessor {

    protected String original;
    protected String extName;
    protected boolean isListReference = false;
    protected boolean isAddAtIndex = false;
    protected boolean isAddAtEnd = false;
    protected int listIndex = -1;
    protected SubMapAccessor subMapAccessor = null;

    public FlexibleMapAccessor(String name) {
        this.original = name;
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex != -1) {
            this.extName = name.substring(dotIndex+1);
            String subName = name.substring(0, dotIndex);
            this.subMapAccessor = new SubMapAccessor(subName);
        } else {
            this.extName = name;
        }
        int openBrace = this.extName.indexOf('[');
        int closeBrace = (openBrace == -1 ? -1 : this.extName.indexOf(']', openBrace));
        if (openBrace != -1 && closeBrace != -1) {
            String liStr = this.extName.substring(openBrace+1, closeBrace);
            //if brackets are empty, append to list
            if (liStr.length() == 0) {
                this.isAddAtEnd = true;
            } else {
                if (liStr.charAt(0) == '+') {
                    liStr = liStr.substring(1);
                    this.listIndex = Integer.parseInt(liStr);
                    this.isAddAtIndex = true;
                } else {
                    this.listIndex = Integer.parseInt(liStr);
                }
            }
            this.extName = this.extName.substring(0, openBrace);
            this.isListReference = true;
        }
    }
    
    public String getOriginalName() {
        return this.original;
    }
    
    public boolean isEmpty() {
        if (this.original == null || this.original.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /** Given the name based information in this accessor, get the value from the passed in Map. 
     *  Supports LocalizedMaps by getting a String or Locale object from the base Map with the key "locale", or by explicit locale parameter.
     * @param base
     * @return
     */
    public Object get(Map base) {
        return get(base, null);
    }
    
    /** Given the name based information in this accessor, get the value from the passed in Map. 
     *  Supports LocalizedMaps by getting a String or Locale object from the base Map with the key "locale", or by explicit locale parameter.
     *  Note that the localization functionality is only used when the lowest level sub-map implements the LocalizedMap interface
     * @param base Map to get value from
     * @param locale Optional locale parameter, if null will see if the base Map contains a "locale" key
     * @return
     */
    public Object get(Map base, Locale locale) {
        if (base == null) {
            return null;
        }
        
        // so we can keep the passed context
        Map newBase = new HashMap(base);
        
        if (this.subMapAccessor != null) {
            newBase = this.subMapAccessor.getSubMap(base);
        }
        
        Object ret = null;
        if (this.isListReference) {
            List lst = (List) newBase.get(this.extName);
            ret = lst.get(this.listIndex);
        } else {
            ret = getByLocale(this.extName, base, newBase, locale);
        }
        
        // in case the name has a dot like system env values
        if (ret == null) {
            ret = getByLocale(this.original, base, base, locale);
        }        
        
        return ret;
    }
    
    protected Object getByLocale(String name, Map base, Map sub, Locale locale) {
        if (sub instanceof LocalizedMap) {
            LocalizedMap locMap = (LocalizedMap) sub;
            if (locale != null) {
                return locMap.get(name, locale);
            } else if (base.containsKey("locale")) {
                return locMap.get(name, UtilMisc.ensureLocale(base.get("locale")));
            } else {
                return locMap.get(name, Locale.getDefault());
            }
        } else {
            return sub.get(name);
        }
    }
    
    /** Given the name based information in this accessor, put the value in the passed in Map. 
     * If the brackets for a list are empty the value will be appended to the list,
     * otherwise the value will be set in the position of the number in the brackets.
     * If a "+" (plus sign) is included inside the square brackets before the index 
     * number the value will inserted/added at that point instead of set at the point.
     * @param base
     * @param value
     */
    public void put(Map base, Object value) {
        if (base == null) {
            throw new IllegalArgumentException("Cannot put a value in a null base Map");
        }
        if (this.subMapAccessor != null) {
            Map subBase = this.subMapAccessor.getSubMap(base);
            if (subBase == null) {
                return;
            }
            base = subBase;
        }
        if (this.isListReference) {
            List lst = (List) base.get(this.extName);
            //if brackets are empty, append to list
            if (this.isAddAtEnd) {
                lst.add(value);
            } else {
                if (this.isAddAtIndex) {
                    lst.add(this.listIndex, value);
                } else {
                    lst.set(this.listIndex, value);
                }
            }
        } else {
            base.put(this.extName, value);
        }
    }
    
    /** Given the name based information in this accessor, remove the value from the passed in Map. * @param base
     * @param base the Map to remove from
     * @return the object removed
     */
    public Object remove(Map base) {
        if (this.subMapAccessor != null) {
            base = this.subMapAccessor.getSubMap(base);
        }
        if (this.isListReference) {
            List lst = (List) base.get(this.extName);
            return lst.remove(this.listIndex);
        } else {
            return base.remove(this.extName);
        }
    }
    
    public String toString() {
        return this.original;
    }
    
    public boolean equals(Object that) {
        FlexibleMapAccessor thatAcsr = (FlexibleMapAccessor) that;
        if (this.original == null) {
            if (thatAcsr.original == null) {
                return true;
            } else {
                return false;
            }
        } else {
            return this.original.equals(thatAcsr.original);
        }
    }
    
    public int hashCode() {
        return this.original.hashCode();
    }
    
    public class SubMapAccessor {
        protected String extName;
        protected boolean isListReference = false;
        protected int listIndex = -1;
        protected SubMapAccessor subMapAccessor = null;

        public SubMapAccessor(String name) {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                this.extName = name.substring(dotIndex+1);
                String subName = name.substring(0, dotIndex);
                this.subMapAccessor = new SubMapAccessor(subName);
            } else {
                this.extName = name;
            }
            int openBrace = this.extName.indexOf('[');
            int closeBrace = (openBrace == -1 ? -1 : this.extName.indexOf(']', openBrace));
            if (openBrace != -1 && closeBrace != -1) {
                String liStr = this.extName.substring(openBrace+1, closeBrace);
                this.listIndex = Integer.parseInt(liStr);
                this.extName = this.extName.substring(0, openBrace);
                this.isListReference = true;
            }
        }
        
        public Map getSubMap(Map base) {
            if (base == null) return null;
            if (this.subMapAccessor != null) {
                base = this.subMapAccessor.getSubMap(base);
            }
            if (this.isListReference) {
                List lst = (List) base.get(this.extName);
                if (lst == null) {
                    lst = new LinkedList();
                    base.put(this.extName, lst);
                }
                
                Map extMap = null;
                if (lst.size() > this.listIndex) {
                    extMap = (Map) lst.get(this.listIndex);
                }
                if (extMap == null) {
                    extMap = new HashMap();
                    lst.add(this.listIndex, extMap);
                }
                
                return extMap;
            } else {
                Map extMap = (Map) base.get(this.extName);
                if (extMap == null) {
                    extMap = new HashMap();
                    base.put(this.extName, extMap);
                }
                return extMap;
            }
        }
    }

    /*    
        protected Map getNamedMap(String name, Map base) {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                String subName = name.substring(0, dotIndex);
                String extName = name.substring(dotIndex+1);
                Map subMap = getNamedMap(subName, base);
                return (Map) this.getMemberCheckList(extName, subMap);
            } else {
                return (Map) this.getMemberCheckList(name, base);
            }
        }
    
        protected Object getMemberCheckList(String name, Map base) {
            int openBrace = name.indexOf('[');
            int closeBrace = name.indexOf(']', openBrace);
            if (openBrace != -1 && closeBrace != -1) {
                String liStr = name.substring(openBrace+1, closeBrace);
                int listIndex = Integer.parseInt(liStr);
                String baseName = name.substring(0, openBrace);
                List lst = (List) base.get(baseName);
                return lst.get(listIndex);
            } else {
                return base.get(name);
            }
        }

        protected void putMemberCheckList(String name, Map base, Object value) {
            int openBrace = name.indexOf('[');
            int closeBrace = name.indexOf(']', openBrace);
            if (openBrace != -1 && closeBrace != -1) {
                String baseName = name.substring(0, openBrace);
                List lst = (List) base.get(baseName);

                String liStr = name.substring(openBrace+1, closeBrace);
                //if brackets are empty, append to list
                if (liStr.length() == 0) {
                    lst.add(value);
                } else {
                    if (liStr.charAt(0) == '+') {
                        liStr = liStr.substring(1);
                        int listIndex = Integer.parseInt(liStr);
                        lst.add(listIndex, value);
                    } else {
                        int listIndex = Integer.parseInt(liStr);
                        lst.set(listIndex, value);
                    }
                }
            } else {
                base.put(name, value);
            }
        }

        protected Object removeMemberCheckList(String name, Map base) {
            int openBrace = name.indexOf('[');
            int closeBrace = name.indexOf(']', openBrace);
            if (openBrace != -1 && closeBrace != -1) {
                String liStr = name.substring(openBrace+1, closeBrace);
                int listIndex = Integer.parseInt(liStr);
                String baseName = name.substring(0, openBrace);
                List lst = (List) base.get(baseName);
                return lst.remove(listIndex);
            } else {
                return base.remove(name);
            }
        }

        protected Object getMapMember(String name, Map base) {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                String subName = name.substring(0, dotIndex);
                String extName = name.substring(dotIndex+1);
                Map subMap = getNamedMap(subName, base);
                return this.getMemberCheckList(extName, subMap);
            } else {
                return this.getMemberCheckList(name, base);
            }
        }
    
        protected void putMapMember(String name, Map base, Object value) {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                String subName = name.substring(0, dotIndex);
                String extName = name.substring(dotIndex+1);
                Map subMap = getNamedMap(subName, base);
                this.putMemberCheckList(extName, subMap, value);
            } else {
                this.putMemberCheckList(name, base, value);
            }
        }
    
        protected Object removeMapMember(String name, Map base) {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                String subName = name.substring(0, dotIndex);
                String extName = name.substring(dotIndex+1);
                Map subMap = getNamedMap(subName, base);
                return this.removeMemberCheckList(extName, subMap);
            } else {
                return this.removeMemberCheckList(name, base);
            }
        }
     */    
}
