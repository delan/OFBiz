/*
 * $Id$
 *
 * Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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

import java.util.Vector;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.ImporterTopLevel;

import com.ibm.bsf.*;
import com.ibm.bsf.util.BSFEngineImpl;
import com.ibm.bsf.util.BSFFunctions;

/**
 * This is the interface to Netscape's Rhino (JavaScript) from the
 * Bean Scripting Framework.
 * <p>
 * The original version of this code was first written by Adam Peller
 * for use in LotusXSL. Sanjiva took his code and adapted it for BSF.
 *
 * <p>Also modified for optimized use in the OFBiz framework.
 *
 * @author   Adam Peller <peller@lotus.com>
 * @author   Sanjiva Weerawarana
 * @author   Matthew J. Duftler
 * @author   Norris Boyd
 * @author   David E. Jones <jonesde@ofbiz.org>
 */
public class OfbizJsBsfEngine extends BSFEngineImpl {
    
    public static final String module = OfbizJsBsfEngine.class.getName();
    
    /**
     * The global script object, where all embedded functions are defined,
     * as well as the standard ECMA "core" objects.
     */
    private Scriptable global;
    
    /**
     * initialize the engine. put the manager into the context -> manager
     * map hashtable too.
     */
    public void initialize(BSFManager mgr, String lang, Vector declaredBeans) throws BSFException {
        super.initialize(mgr, lang, declaredBeans);
        
        // Initialize context and global scope object
        try {
            Context cx = Context.enter();
            global = cx.initStandardObjects(new ImporterTopLevel(cx));
            Scriptable bsf = Context.toObject(new BSFFunctions(mgr, this), global);
            global.put("bsf", global, bsf);
            
            int size = declaredBeans.size();
            for (int i = 0; i < size; i++) {
                declareBean((BSFDeclaredBean) declaredBeans.elementAt(i));
            }
        } finally {
            Context.exit();
        }
    }
    
    /**
     * This is used by an application to evaluate a string containing
     * some expression.
     */
    public Object eval(String source, int lineNo, int columnNo, Object oscript) throws BSFException {
        if (Debug.verboseOn()) Debug.logVerbose("Running javascript script " + source + " through OFBiz BSH engine", module);
        String script = oscript.toString();
        Object retval = null;
        try {
            Context cx = Context.enter();
            
            // Use interpretive mode (-1) --generally faster for single executions of scripts.
            // Use optimized/compiled mode (9) --generally faster for repeated executions of scripts.
            cx.setOptimizationLevel(9);
            
            retval = cx.evaluateString(global, script, source, lineNo, null);
            if (retval instanceof NativeJavaObject)
                retval = ((NativeJavaObject)retval).unwrap();
        } catch (Throwable t) { // includes JavaScriptException, rethrows Errors
            handleError(t);
        } finally {
            Context.exit();
        }
        return retval;
    }
    
    /**
     * Return an object from an extension.
     * @param object Object on which to make the call (ignored).
     * @param method The name of the method to call.
     * @param args an array of arguments to be
     * passed to the extension, which may be either
     * Vectors of Nodes, or Strings.
     */
    public Object call(Object object, String method, Object[] args) throws BSFException {
        Object theReturnValue = null;
        
        try {
            Context cx = Context.enter();
            
            //REMIND: convert arg list Vectors here?
            
            Object fun = global.get(method, global);
            if (fun == Scriptable.NOT_FOUND) {
                throw new JavaScriptException("function " + method +
                " not found.");
            }
            
            theReturnValue = ScriptRuntime.call(cx, fun, global, args, null);
            if (theReturnValue instanceof Wrapper) {
                theReturnValue = ((Wrapper) theReturnValue).unwrap();
            }
        } catch (Throwable t) {
            handleError(t);
        } finally {
            Context.exit();
        }
        return theReturnValue;
    }
    
    public void declareBean(BSFDeclaredBean bean) throws BSFException {
        // Must wrap non-scriptable objects before presenting to Rhino
        Scriptable wrapped = Context.toObject(bean.bean, global);
        global.put(bean.name, global, wrapped);
    }
    
    public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
        global.delete(bean.name);
    }
    
    private void handleError(Throwable t) throws BSFException {
        if (t instanceof WrappedException) {
            t = (Throwable)((WrappedException)t).unwrap();
        }
        
        String message = null;
        Throwable target = t;
        
        if (t instanceof JavaScriptException) {
            message = t.getLocalizedMessage();
            
            // Is it an exception wrapped in a JavaScriptException?
            Object value = ((JavaScriptException)t).getValue();
            if (value instanceof Throwable) {
                // likely a wrapped exception from a LiveConnect call.
                // Display its stack trace as a diagnostic
                target = (Throwable)value;
            }
        } else if (t instanceof EvaluatorException
        || t instanceof SecurityException) {
            message = t.getLocalizedMessage();
        } else if (t instanceof RuntimeException) {
            message = "Internal Error: " + t.toString();
        } else if (t instanceof StackOverflowError) {
            message = "Stack Overflow";
        }
        
        if (message == null) {
            message = t.toString();
        }
        
        //REMIND: can we recover the line number here?  I think
        // Rhino does this by looking up the stack for bytecode
        // see Context.getSourcePositionFromStack()
        // but I don't think this would work in interpreted mode
        
        if (t instanceof Error && !(t instanceof StackOverflowError)) {
            // Re-throw Errors because we're supposed to let the JVM see it
            // Don't re-throw StackOverflows, because we know we've
            // corrected the situation by aborting the loop and
            // a long stacktrace would end up on the user's console
            throw (Error) t;
        } else {
            throw new BSFException(BSFException.REASON_OTHER_ERROR, "JavaScript Error: " + message, target);
        }
    }
}
