/*
 * $Id$
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
package org.ofbiz.base.util.string;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;

/**
 * Expands string values with in a Map context supporting the ${} syntax for
 * variable placeholders and the "." (dot) and "[]" (square-brace) syntax
 * elements for accessing Map entries and List elements in the context.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev:$
 * @since      2.2
 */
public class FlexibleStringExpander {
    
    public static final String module = FlexibleStringExpander.class.getName();
    
    protected String original;
    protected List stringElements = new LinkedList();
    
    public FlexibleStringExpander(String original) {
        this.original = original;
        
        ParseElementHandler handler = new PreParseHandler(stringElements);
        parseString(original, handler);
    }
    
    public boolean isEmpty() {
        if (this.original == null || this.original.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getOriginal() {
        return this.original;
    }

    /** 
     * This expands the pre-parsed String given the context passed in. Note that
     * pre-parsing can only parse the top-level place-holders and if there are 
     * nested expansions they will be done on the fly instead of pre-parsed because
     * they are dependent on the context which isn't known until expansion time.
     * 
     * @param context A context Map containing the variable values
     * @return The original String expanded by replacing varaible place holders.
     */    
    public String expandString(Map context) {
        return this.expandString(context, null);
    }
    
    /** 
     * This expands the pre-parsed String given the context passed in. Note that
     * pre-parsing can only parse the top-level place-holders and if there are 
     * nested expansions they will be done on the fly instead of pre-parsed because
     * they are dependent on the context which isn't known until expansion time.
     * 
     * @param context A context Map containing the variable values
     * @return The original String expanded by replacing varaible place holders.
     */    
    public String expandString(Map context, Locale locale) {
        StringBuffer expanded = new StringBuffer();
        
        Iterator stringElementIter = stringElements.iterator();
        while (stringElementIter.hasNext()) {
            StringElement element = (StringElement) stringElementIter.next();
            element.appendElement(expanded, context, locale);
        }
        
        //call back into this method with new String to take care of any/all nested expands
        return expandString(expanded.toString(), context);
    }
    
    /**
     * Does on-the-fly parsing and expansion of the original String using
     * varaible values from the passed context. Variables are denoted with
     * the "${}" syntax and the variable name inside the curly-braces can use
     * the "." (dot) syntax to access sub-Map entries and the "[]" square-brace
     * syntax to access List elements.
     * 
     * @param original The original String that will be expanded
     * @param context A context Map containing the variable values
     * @return The original String expanded by replacing varaible place holders.
     */
    public static String expandString(String original, Map context) {
        return expandString(original, context, null);
    }
    
    /**
     * Does on-the-fly parsing and expansion of the original String using
     * varaible values from the passed context. Variables are denoted with
     * the "${}" syntax and the variable name inside the curly-braces can use
     * the "." (dot) syntax to access sub-Map entries and the "[]" square-brace
     * syntax to access List elements.
     * 
     * @param original The original String that will be expanded
     * @param context A context Map containing the variable values
     * @return The original String expanded by replacing varaible place holders.
     */
    public static String expandString(String original, Map context, Locale locale) {
        // if null or less than 3 return original; 3 chars because that is the minimum necessary for a ${}
        if (original == null || original.length() < 3) {
            return original;
        }
        
        // start by checking to see if expansion is necessary for better performance
        // this is also necessary for the nested stuff since this will be the stopping point for nested expansions
        int start = original.indexOf("${");
        if (start == -1) {
            return original;
        } else {
            if (original.indexOf("}", start) == -1) {
                //no ending for the start, so we also have a stop condition
                Debug.logWarning("Found a ${ without a closing } (curly-brace) in the String: " + original, module);
                return original;  
            }
        }
        
        StringBuffer expanded = new StringBuffer();
        ParseElementHandler handler = new OnTheFlyHandler(expanded, context, locale);
        parseString(original, handler);
        
        //call back into this method with new String to take care of any/all nested expands
        return expandString(expanded.toString(), context);
    }
    
    public static void parseString(String original, ParseElementHandler handler) {
        if (original == null || original.length() == 0) {
            return;
        }
        
        int start = original.indexOf("${");
        if (start == -1) {
            handler.handleConstant(original, 0);
            return;
        }
        int currentInd = 0;
        int end = -1;
        while (start != -1) {
            end = original.indexOf("}", start);
            if (end == -1) {
                Debug.logWarning("Found a ${ without a closing } (curly-brace) in the String: " + original, module);
                break;
            } 
            
            // check to see if there is a nested ${}, ie something like ${foo.${bar}} or ${foo[$bar}]}
            // since we are only handling one at a time, and then recusively looking for nested ones, just look backward from the } for another ${ and if found and is not the same start spot, update the start spot
            int possibleNestedStart = original.lastIndexOf("${", end);
            if (start != possibleNestedStart) {
                // found a nested one, could print something here, but just do the simple thing...
                start = possibleNestedStart;
            }
            
            // append everything from the current index to the start of the var
            handler.handleConstant(original, currentInd, start);
            
            // get the environment value and append it
            handler.handleVariable(original, start+2, end);
            
            // reset the current index to after the var, and the start to the beginning of the next var
            currentInd = end + 1;
            start = original.indexOf("${", currentInd);
        }
        
        // append the rest of the original string, ie after the last variable
        if (currentInd < original.length()) {
            handler.handleConstant(original, currentInd);
        }
    }

    public static interface StringElement {
        public void appendElement(StringBuffer buffer, Map context, Locale locale);
    }
    
    public static class ConstantElement implements StringElement {
        protected String value;
        
        public ConstantElement(String value) {
            this.value = value;
        }
        
        public void appendElement(StringBuffer buffer, Map context, Locale locale) {
            buffer.append(this.value); 
        }
    }
    
    public static class VariableElement implements StringElement {
        protected FlexibleMapAccessor fma;
        
        public VariableElement(String valueName) {
            this.fma = new FlexibleMapAccessor(valueName);
        }
        
        public void appendElement(StringBuffer buffer, Map context, Locale locale) {
            Object retVal = fma.get(context, locale);
            if (retVal != null) {
                buffer.append(retVal.toString()); 
            } else {
                // otherwise do nothing
            }
        }
    }

    public static interface ParseElementHandler {
        public void handleConstant(String original, int start); 
        public void handleConstant(String original, int start, int end); 
        public void handleVariable(String original, int start, int end); 
    }
    
    public static class PreParseHandler implements ParseElementHandler {
        protected List stringElements;
        
        public PreParseHandler(List stringElements) {
            this.stringElements = stringElements;
        }
        
        public void handleConstant(String original, int start) {
            stringElements.add(new ConstantElement(original.substring(start))); 
        }
        
        public void handleConstant(String original, int start, int end) {
            stringElements.add(new ConstantElement(original.substring(start, end))); 
        }
        
        public void handleVariable(String original, int start, int end) {
            stringElements.add(new VariableElement(original.substring(start, end))); 
        }
    }
    
    public static class OnTheFlyHandler implements ParseElementHandler {
        protected StringBuffer targetBuffer;
        protected Map context;
        protected Locale locale;
        
        public OnTheFlyHandler(StringBuffer targetBuffer, Map context, Locale locale) {
            this.targetBuffer = targetBuffer;
            this.context = context;
            this.locale = locale;
        }
        
        public void handleConstant(String original, int start) { 
            targetBuffer.append(original.substring(start));
        }
        
        public void handleConstant(String original, int start, int end) {
            targetBuffer.append(original.substring(start, end));
        }
        
        public void handleVariable(String original, int start, int end) {
            //get the environment value and append it
            String envName = original.substring(start, end);
            FlexibleMapAccessor fma = new FlexibleMapAccessor(envName);
            Object envVal = fma.get(context, locale);
            if (envVal != null) {
                targetBuffer.append(envVal.toString());
            } else {
                Debug.logWarning("Could not find value in environment for the name [" + envName + "], inserting nothing.", module);
            }
        }
    }
}
