/*
 * $Id: ModelMenuCondition.java 3103 2004-08-20 21:45:49Z jaz $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.widget.menu;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.operation.BaseCompare;
import org.ofbiz.security.Security;
import org.ofbiz.content.content.EntityPermissionChecker;
import org.w3c.dom.Element;

/**
 * Widget Library - Screen model condition class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev: 3103 $
 * @since      3.1
 */
public class ModelMenuCondition {
    public static final String module = ModelMenuCondition.class.getName();

    protected ModelMenu modelMenu;
    protected ScreenCondition rootCondition;

    public ModelMenuCondition(ModelMenu modelMenu, Element conditionElement) {
        this.modelMenu = modelMenu;
        Element firstChildElement = UtilXml.firstChildElement(conditionElement);
        this.rootCondition = readCondition(modelMenu, firstChildElement);
    }

    public boolean eval(Map context) {
        if (rootCondition == null) {
            return true;
        }
        return rootCondition.eval(context);
    }
    
    public static abstract class ScreenCondition {
        protected ModelMenu modelMenu;

        public ScreenCondition(ModelMenu modelMenu, Element conditionElement) {
            this.modelMenu = modelMenu;
        }
        
        public abstract boolean eval(Map context);
    }
    
    public static List readSubConditions(ModelMenu modelMenu, Element conditionElement) {
        List condList = new LinkedList();
        List subElementList = UtilXml.childElementList(conditionElement);
        Iterator subElementIter = subElementList.iterator();
        while (subElementIter.hasNext()) {
            Element subElement = (Element) subElementIter.next();
            condList.add(readCondition(modelMenu, subElement));
        }
        return condList;
    }
    
    public static ScreenCondition readCondition(ModelMenu modelMenu, Element conditionElement) {
        if (conditionElement == null) {
            return null;
        }
        if ("and".equals(conditionElement.getNodeName())) {
            return new And(modelMenu, conditionElement);
        } else if ("xor".equals(conditionElement.getNodeName())) {
            return new Xor(modelMenu, conditionElement);
        } else if ("or".equals(conditionElement.getNodeName())) {
            return new Or(modelMenu, conditionElement);
        } else if ("not".equals(conditionElement.getNodeName())) {
            return new Not(modelMenu, conditionElement);
        } else if ("if-has-permission".equals(conditionElement.getNodeName())) {
            return new IfHasPermission(modelMenu, conditionElement);
        } else if ("if-validate-method".equals(conditionElement.getNodeName())) {
            return new IfValidateMethod(modelMenu, conditionElement);
        } else if ("if-compare".equals(conditionElement.getNodeName())) {
            return new IfCompare(modelMenu, conditionElement);
        } else if ("if-compare-field".equals(conditionElement.getNodeName())) {
            return new IfCompareField(modelMenu, conditionElement);
        } else if ("if-regexp".equals(conditionElement.getNodeName())) {
            return new IfRegexp(modelMenu, conditionElement);
        } else if ("if-empty".equals(conditionElement.getNodeName())) {
            return new IfEmpty(modelMenu, conditionElement);
        } else if ("if-entity-permission".equals(conditionElement.getNodeName())) {
            return new IfEntityPermission(modelMenu, conditionElement);
        } else {
            throw new IllegalArgumentException("Condition element not supported with name: " + conditionElement.getNodeName());
        }
    }
    
    public static class And extends ScreenCondition {
        protected List subConditions;
        
        public And(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.subConditions = readSubConditions(modelMenu, condElement);
        }
        
        public boolean eval(Map context) {
            // return false for the first one in the list that is false, basic and algo
            Iterator subConditionIter = this.subConditions.iterator();
            while (subConditionIter.hasNext()) {
                ScreenCondition subCondition = (ScreenCondition) subConditionIter.next();
                if (!subCondition.eval(context)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static class Xor extends ScreenCondition {
        protected List subConditions;
        
        public Xor(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.subConditions = readSubConditions(modelMenu, condElement);
        }
        
        public boolean eval(Map context) {
            // if more than one is true stop immediately and return false; if all are false return false; if only one is true return true
            boolean foundOneTrue = false;
            Iterator subConditionIter = this.subConditions.iterator();
            while (subConditionIter.hasNext()) {
                ScreenCondition subCondition = (ScreenCondition) subConditionIter.next();
                if (subCondition.eval(context)) {
                    if (foundOneTrue) {
                        // now found two true, so return false
                        return false;
                    } else {
                        foundOneTrue = true;
                    }
                }
            }
            return foundOneTrue;
        }
    }
    
    public static class Or extends ScreenCondition {
        protected List subConditions;
        
        public Or(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.subConditions = readSubConditions(modelMenu, condElement);
        }
        
        public boolean eval(Map context) {
            // return true for the first one in the list that is true, basic or algo
            Iterator subConditionIter = this.subConditions.iterator();
            while (subConditionIter.hasNext()) {
                ScreenCondition subCondition = (ScreenCondition) subConditionIter.next();
                if (subCondition.eval(context)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public static class Not extends ScreenCondition {
        protected ScreenCondition subCondition;
        
        public Not(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            Element firstChildElement = UtilXml.firstChildElement(condElement);
            this.subCondition = readCondition(modelMenu, firstChildElement);
        }
        
        public boolean eval(Map context) {
            return !this.subCondition.eval(context);
        }
    }
    
    public static class IfHasPermission extends ScreenCondition {
        protected FlexibleStringExpander permissionExdr;
        protected FlexibleStringExpander actionExdr;
        
        public IfHasPermission(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.permissionExdr = new FlexibleStringExpander(condElement.getAttribute("permission"));
            this.actionExdr = new FlexibleStringExpander(condElement.getAttribute("action"));
        }
        
        public boolean eval(Map context) {
            // if no user is logged in, treat as if the user does not have permission
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            if (userLogin != null) {
                String permission = permissionExdr.expandString(context);
                String action = actionExdr.expandString(context);
                
                Security security = (Security) context.get("security");
                if (action != null && action.length() > 0) {
                    // run hasEntityPermission
                    if (security.hasEntityPermission(permission, action, userLogin)) {
                        return true;
                    }
                } else {
                    // run hasPermission
                    if (security.hasPermission(permission, userLogin)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class IfValidateMethod extends ScreenCondition {
        protected FlexibleMapAccessor fieldAcsr;
        protected FlexibleStringExpander methodExdr;
        protected FlexibleStringExpander classExdr;
        
        public IfValidateMethod(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.fieldAcsr = new FlexibleMapAccessor(condElement.getAttribute("field-name"));
            this.methodExdr = new FlexibleStringExpander(condElement.getAttribute("method"));
            this.classExdr = new FlexibleStringExpander(condElement.getAttribute("class"));
        }
        
        public boolean eval(Map context) {
            String methodName = this.methodExdr.expandString(context);
            String className = this.classExdr.expandString(context);
            
            Object fieldVal = this.fieldAcsr.get(context);
            String fieldString = null;
            if (fieldVal != null) {
                try {
                    fieldString = (String) ObjectType.simpleTypeConvert(fieldVal, "String", null, null);
                } catch (GeneralException e) {
                    Debug.logError(e, "Could not convert object to String, using empty String", module);
                }
            }

            // always use an empty string by default
            if (fieldString == null) fieldString = "";

            Class[] paramTypes = new Class[] {String.class};
            Object[] params = new Object[] {fieldString};

            Class valClass;
            try {
                valClass = ObjectType.loadClass(className);
            } catch (ClassNotFoundException cnfe) {
                Debug.logError("Could not find validation class: " + className, module);
                return false;
            }

            Method valMethod;
            try {
                valMethod = valClass.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException cnfe) {
                Debug.logError("Could not find validation method: " + methodName + " of class " + className, module);
                return false;
            }

            Boolean resultBool = Boolean.FALSE;
            try {
                resultBool = (Boolean) valMethod.invoke(null, params);
            } catch (Exception e) {
                Debug.logError(e, "Error in IfValidationMethod " + methodName + " of class " + className + ", defaulting to false ", module);
            }

            return resultBool.booleanValue();
        }
    }
    
    public static class IfCompare extends ScreenCondition {
        protected FlexibleMapAccessor fieldAcsr;
        protected FlexibleStringExpander valueExdr;

        protected String operator;
        protected String type;
        protected FlexibleStringExpander formatExdr;
        
        public IfCompare(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.fieldAcsr = new FlexibleMapAccessor(condElement.getAttribute("field-name"));
            this.valueExdr = new FlexibleStringExpander(condElement.getAttribute("value"));
            
            this.operator = condElement.getAttribute("operator");
            this.type = condElement.getAttribute("type");

            this.formatExdr = new FlexibleStringExpander(condElement.getAttribute("format"));
        }
        
        public boolean eval(Map context) {
            String value = this.valueExdr.expandString(context);
            String format = this.formatExdr.expandString(context);
            
            Object fieldVal = this.fieldAcsr.get(context);
            
            // always use an empty string by default
            if (fieldVal == null) {
                fieldVal = "";
            }

            List messages = new LinkedList();
            Boolean resultBool = BaseCompare.doRealCompare(fieldVal, value, operator, type, format, messages, null, null);
            if (messages.size() > 0) {
                messages.add(0, "Error with comparison in if-compare between field [" + fieldAcsr.toString() + "] with value [" + fieldVal + "] and value [" + value + "] with operator [" + operator + "] and type [" + type + "]: ");

                StringBuffer fullString = new StringBuffer();
                Iterator miter = messages.iterator();
                while (miter.hasNext()) {
                    fullString.append((String) miter.next());
                }
                Debug.logWarning(fullString.toString(), module);

                throw new IllegalArgumentException(fullString.toString());
            }
            
            return resultBool.booleanValue();
        }
    }
    
    public static class IfCompareField extends ScreenCondition {
        protected FlexibleMapAccessor fieldAcsr;
        protected FlexibleMapAccessor toFieldAcsr;

        protected String operator;
        protected String type;
        protected FlexibleStringExpander formatExdr;
        
        public IfCompareField(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.fieldAcsr = new FlexibleMapAccessor(condElement.getAttribute("field-name"));
            this.toFieldAcsr = new FlexibleMapAccessor(condElement.getAttribute("to-field-name"));
            
            this.operator = condElement.getAttribute("operator");
            this.type = condElement.getAttribute("type");

            this.formatExdr = new FlexibleStringExpander(condElement.getAttribute("format"));
        }
        
        public boolean eval(Map context) {
            String format = this.formatExdr.expandString(context);
            
            Object fieldVal = this.fieldAcsr.get(context);
            Object toFieldVal = this.toFieldAcsr.get(context);
            
            // always use an empty string by default
            if (fieldVal == null) {
                fieldVal = "";
            }

            List messages = new LinkedList();
            Boolean resultBool = BaseCompare.doRealCompare(fieldVal, toFieldVal, operator, type, format, messages, null, null);
            if (messages.size() > 0) {
                messages.add(0, "Error with comparison in if-compare-field between field [" + fieldAcsr.toString() + "] with value [" + fieldVal + "] and to-field [" + toFieldVal.toString() + "] with value [" + toFieldVal + "] with operator [" + operator + "] and type [" + type + "]: ");

                StringBuffer fullString = new StringBuffer();
                Iterator miter = messages.iterator();
                while (miter.hasNext()) {
                    fullString.append((String) miter.next());
                }
                Debug.logWarning(fullString.toString(), module);

                throw new IllegalArgumentException(fullString.toString());
            }
            
            return resultBool.booleanValue();
        }
    }
    
    public static class IfRegexp extends ScreenCondition {
        static PatternMatcher matcher = new Perl5Matcher();
        static PatternCompiler compiler = new Perl5Compiler();

        protected FlexibleMapAccessor fieldAcsr;
        protected FlexibleStringExpander exprExdr;
        
        public IfRegexp(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.fieldAcsr = new FlexibleMapAccessor(condElement.getAttribute("field-name"));
            this.exprExdr = new FlexibleStringExpander(condElement.getAttribute("expr"));
        }
        
        public boolean eval(Map context) {
            Object fieldVal = this.fieldAcsr.get(context);
            String expr = this.exprExdr.expandString(context);
            Pattern pattern = null;
            try {
                pattern = compiler.compile(expr);
            } catch (MalformedPatternException e) {
                String errMsg = "Error in evaluation in if-regexp in screen: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }

            String fieldString = null;
            try {
                fieldString = (String) ObjectType.simpleTypeConvert(fieldVal, "String", null, null);
            } catch (GeneralException e) {
                Debug.logError(e, "Could not convert object to String, using empty String", module);
            }
            // always use an empty string by default
            if (fieldString == null) fieldString = "";
    
            return matcher.matches(fieldString, pattern);
        }
    }
    
    public static class IfEmpty extends ScreenCondition {
        protected FlexibleMapAccessor fieldAcsr;
        
        public IfEmpty(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.fieldAcsr = new FlexibleMapAccessor(condElement.getAttribute("field-name"));
        }
        
        public boolean eval(Map context) {
            Object fieldVal = this.fieldAcsr.get(context);
            return ObjectType.isEmpty(fieldVal);
        }
    }
    public static class IfEntityPermission extends ScreenCondition {
        protected EntityPermissionChecker permissionChecker;
        
        public IfEntityPermission(ModelMenu modelMenu, Element condElement) {
            super (modelMenu, condElement);
            this.permissionChecker = new EntityPermissionChecker(condElement);
        }
        
        public boolean eval(Map context) {
        	
        	boolean passed = permissionChecker.runPermissionCheck(context);
            return passed;
        }
    }
}

