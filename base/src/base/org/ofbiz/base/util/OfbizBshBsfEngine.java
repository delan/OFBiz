/*
 * $Id: OfbizBshBsfEngine.java,v 1.4 2003/09/18 16:01:21 jonesde Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.base.util;

/*
        This file is associated with the BeanShell Java Scripting language
        distribution (http://www.beanshell.org/).
 
        This file is hereby placed into the public domain...  You may copy,
        modify, and redistribute it without restriction.
 
 */

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.InterpreterError;
import bsh.NameSpace;
import bsh.TargetError;

import com.ibm.bsf.BSFDeclaredBean;
import com.ibm.bsf.BSFException;
import com.ibm.bsf.BSFManager;
import com.ibm.bsf.util.BSFEngineImpl;

/**
 * This is the BeanShell adapter for IBM's Bean Scripting Famework.
 * It is an implementation of the BSFEngine class, allowing BSF aware
 * applications to use BeanShell as a scripting language.
 * <p>
 *
 * I believe this implementation is complete (with some hesitation about the
 * the usefullness of the compileXXX() style methods - provided by the base
 * utility class).
 *
 * @author Pat Niemeyer
 * @author David E. Jones
 */
public class OfbizBshBsfEngine extends BSFEngineImpl {
    
    public static final String module = OfbizBshBsfEngine.class.getName();
    
    protected static Map masterClassManagers = new HashMap();
    protected Interpreter interpreter;
    protected boolean installedApplyMethod;
    
    public static UtilCache parsedScripts = new UtilCache("script.BshBsfParsedCache", 0, 0, false);
    
    public void initialize(BSFManager mgr, String lang, Vector declaredBeans) throws BSFException {
        super.initialize(mgr, lang, declaredBeans);
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        //find the "master" BshClassManager for this classpath
        BshClassManager master = (BshClassManager) masterClassManagers.get(classLoader);
        if (master == null) {
            synchronized (OfbizBshBsfEngine.class) {
                master = (BshClassManager) masterClassManagers.get(classLoader);
                if (master == null) {
                    master = BshClassManager.createClassManager();
                    master.setClassLoader(classLoader);
                    masterClassManagers.put(classLoader, master);
                }
            }
        }
        
        if (master != null) {
            interpreter = new Interpreter(new StringReader(""), System.out, System.err, 
                    false, new NameSpace(master, "global"), null, null);
        } else {
            interpreter = new Interpreter();
            interpreter.setClassLoader(classLoader);
        }
        
        // declare the bsf manager for callbacks, etc.
        try {
            interpreter.set("bsf", mgr);
        } catch (EvalError e) {
            throw new BSFException("bsh internal error: "+e.toString());
        }
        
        for(int i=0; i<declaredBeans.size(); i++) {
            BSFDeclaredBean bean = (BSFDeclaredBean)declaredBeans.get(i);
            declareBean(bean);
        }
    }
    
    public void setDebug(boolean debug) {
        Interpreter.DEBUG=debug;
    }
    
    /**
     * Invoke method name on the specified bsh scripted object.
     * The object may be null to indicate the global namespace of the
     * interpreter.
     * @param object may be null for the global namespace.
     */
    public Object call(Object object, String name, Object[] args) throws BSFException {
        if (object == null) {
            try {
                object = interpreter.get("global");
            } catch (EvalError e) {
                throw new BSFException("bsh internal error: "+e.toString());
            }
        }
        
        if (object instanceof bsh.This) {
            try {
                return ((bsh.This)object).invokeMethod(name, args);
            } catch (InterpreterError e) {
                throw new BSFException("BeanShell interpreter internal error: "+e);
            } catch (TargetError e2) {
                throw new BSFException("The application script threw an exception: " + e2.getTarget());
            } catch (EvalError e3) {
                throw new BSFException("BeanShell script error: "+e3);
            }
        } else {
            throw new BSFException("Cannot invoke method: " + name +
            ". Object: "+object +" is not a BeanShell scripted object.");
        }
    }
    
    
    /**
     * A helper BeanShell method that implements the anonymous method apply
     * proposed by BSF.  Note that the script below could use the standard
     * bsh eval() method to set the variables and apply the text, however
     * then I'd have to escape quotes, etc.
     */
    final static String bsfApplyMethod =
    "_bsfApply(_bsfNames, _bsfArgs, _bsfText) {"
    +"for(i=0;i<_bsfNames.length;i++)"
    +"this.namespace.setVariable(_bsfNames[i], _bsfArgs[i]);"
    +"return this.interpreter.eval(_bsfText, this.namespace);"
    +"}";
    
    /**
     * This is an implementation of the BSF apply() method.
     * It exectutes the funcBody text in an "anonymous" method call with
     * arguments.
     */
    public Object apply(String source, int lineNo, int columnNo, Object funcBody, Vector namesVec, Vector argsVec) throws BSFException {
        if (namesVec.size() != argsVec.size()) throw new BSFException("number of params/names mismatch");
        if (!(funcBody instanceof String)) throw new BSFException("apply: function body must be a string");
        
        String [] names = new String [ namesVec.size() ];
        namesVec.copyInto(names);
        Object [] args = new String [ argsVec.size() ];
        argsVec.copyInto(args);
        
        try {
            if (!installedApplyMethod) {
                interpreter.eval(bsfApplyMethod);
                installedApplyMethod = true;
            }
            
            bsh.This global = (bsh.This)interpreter.get("global");
            return global.invokeMethod("_bsfApply", new Object [] { names, args, (String)funcBody });
            
        } catch (InterpreterError e) {
            throw new BSFException("BeanShell interpreter internal error: " + e + sourceInfo(source,lineNo,columnNo));
        } catch (TargetError e2) {
            throw new BSFException("The application script threw an exception: " + e2.getTarget() + sourceInfo(source,lineNo,columnNo));
        } catch (EvalError e3) {
            throw new BSFException("BeanShell script error: " + e3 + sourceInfo(source,lineNo,columnNo));
        }
    }
    
    public Object eval(String source, int lineNo, int columnNo, Object expr) throws BSFException {
        if (!(expr instanceof String)) throw new BSFException("BeanShell expression must be a string");
        
        try {
            //return interpreter.eval(((String) expr));
            
            Interpreter.ParsedScript script = null;
            
            if (source != null && source.length() > 0) {
                script = (Interpreter.ParsedScript) parsedScripts.get(source);
                if (script == null) {
                    synchronized (OfbizBshBsfEngine.class) {
                        script = (Interpreter.ParsedScript) parsedScripts.get(source);
                        if (script == null) {
                            script = interpreter.parseScript(source, new StringReader((String) expr));
                            Debug.logVerbose("Caching BSH script at: " + source, module);
                            parsedScripts.put(source, script);
                        }
                    }
                }
            } else {
                script = interpreter.parseScript(source, new StringReader((String) expr));
            }
            
            return interpreter.evalParsedScript(script);
        } catch (InterpreterError e) {
            throw new BSFException("BeanShell interpreter internal error: "+ e + sourceInfo(source,lineNo,columnNo));
        } catch (TargetError e2) {
            Debug.logError(e2, "Error thrown in BeanShell script called through BSF at: " + sourceInfo(source,lineNo,columnNo), module);
            //Debug.logError(e2.getTarget(), module);
            throw new BSFException("The application script threw an exception: " + e2 + " " + sourceInfo(source,lineNo,columnNo));
        } catch (EvalError e3) {
            throw new BSFException("BeanShell script error: " + e3 + sourceInfo(source,lineNo,columnNo));
        }
    }
    
    
    public void exec(String source, int lineNo, int columnNo, Object script) throws BSFException {
        eval(source, lineNo, columnNo, script);
    }
    
    
/*
        public void compileApply (String source, int lineNo, int columnNo,
                Object funcBody, Vector paramNames, Vector arguments, CodeBuffer cb)
                throws BSFException;
 
        public void compileExpr (String source, int lineNo, int columnNo,
                Object expr, CodeBuffer cb) throws BSFException;
 
        public void compileScript (String source, int	lineNo,	int columnNo,
                Object script, CodeBuffer cb) throws BSFException;
 */
    
    public void declareBean(BSFDeclaredBean bean) throws BSFException {
        try {
            interpreter.set(bean.name, bean.bean);
        } catch (EvalError e) {
            throw new BSFException("error declaring bean: " + bean.name + " : " + e.toString());
        }
    }
    
    public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
        try {
            interpreter.unset(bean.name);
        } catch (EvalError e) {
            throw new BSFException("bsh internal error: " + e.toString());
        }
    }
    
    public void terminate() { }
    
    private String sourceInfo(String source, int lineNo, int columnNo) {
        return "BSF info: " + source + " at line: " + lineNo +" column: " + columnNo;
    }
}
