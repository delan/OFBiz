/*
 * $Id$
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
package bsh;

/*
        This file is associated with the BeanShell Java Scripting language
        distribution (http://www.beanshell.org/).
 
        This file is hereby placed into the public domain...  You may copy,
        modify, and redistribute it without restriction.
 
 */

import java.io.*;
import java.util.*;

import com.ibm.bsf.*;
import com.ibm.bsf.util.*;
import bsh.*;
import bsh.util.*;

import org.ofbiz.core.util.*;

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
    protected static Map masterClassManagers = new HashMap();
    protected Interpreter interpreter;
    protected boolean installedApplyMethod;
    
    public static UtilCache parsedScripts = new UtilCache("webapp.BshBsfParsedCache", 0, 0, false);
    
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
        interpreter.DEBUG=debug;
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
            
            List parsedLineNodes = null;
            
            if (source != null && source.length() > 0) {
                parsedLineNodes = (List) parsedScripts.get(source);
                if (parsedLineNodes == null) {
                    synchronized (OfbizBshBsfEngine.class) {
                        parsedLineNodes = (List) parsedScripts.get(source);
                        if (parsedLineNodes == null) {
                            parsedLineNodes = bshParse(source, new StringReader((String) expr));
                            Debug.logInfo("Caching BSH script at: " + source);
                            parsedScripts.put(source, parsedLineNodes);
                        }
                    }
                }
            } else {
                parsedLineNodes = bshParse(source, new StringReader((String) expr));
            }
            
            return bshEval(source, parsedLineNodes, interpreter);
        } catch (InterpreterError e) {
            throw new BSFException("BeanShell interpreter internal error: "+ e + sourceInfo(source,lineNo,columnNo));
        } catch (TargetError e2) {
            throw new BSFException("The application script threw an exception: " + e2.getTarget() + " " + sourceInfo(source,lineNo,columnNo));
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
    
    public List bshParse(String sourceFileInfo, Reader in) throws ParseException {
        List parsedLineNodes = new LinkedList();
        Parser parser = new Parser(in);

        boolean eof = false;
        while(!eof) {
            try {
                eof = parser.Line();
                if (parser.jjtree.nodeArity() > 0) {
                    SimpleNode node = (SimpleNode) parser.jjtree.rootNode();
                    // nodes remember from where they were sourced
                    node.setSourceFile(sourceFileInfo);
                    parsedLineNodes.add(node);
                    if (Debug.verboseOn()) Debug.logVerbose("// " + node.getText());
                }
            } catch (ParseException e) {
                // add the source file info and throw again
                e.setErrorSourceFile(sourceFileInfo);
                Debug.logError(e);
                throw e;
            } finally {
                parser.jjtree.reset();
            }
        }
        
        return parsedLineNodes;
    }
    
    public Object bshEval(String sourceFileInfo, List parsedLineNodes, Interpreter parentInterpreter) throws EvalError {
        if (parsedLineNodes == null) {
            return null;
        }
        NameSpace nameSpace = parentInterpreter.getNameSpace();
        Object retVal = null;
        if (Debug.verboseOn()) Debug.logVerbose("eval: nameSpace = " + nameSpace);
        
        //Interpreter localInterpreter = new Interpreter(in, System.out, System.err, false, nameSpace, parentInterpreter, sourceFileInfo);
        
        CallStack callstack = new CallStack();
        callstack.push(nameSpace);
        
        Iterator lineNodeIter = parsedLineNodes.iterator();
        while(lineNodeIter.hasNext()) {
            SimpleNode node = (SimpleNode) lineNodeIter.next();
            try {
                retVal = node.eval(callstack, parentInterpreter);

                // sanity check during development
                if (callstack.depth() > 1) throw new InterpreterError("Callstack growing: " + callstack);

                if (retVal instanceof ReturnControl) {
                    retVal = ((ReturnControl)retVal).value;
                    break; // non-interactive, return control now
                }
            } catch (InterpreterError e) {
                Debug.logError(e);
                throw new EvalError("Sourced file: " + sourceFileInfo + " internal Error: " + e.getMessage(), node, callstack);
            } catch (TargetError e) {
                // failsafe, set the Line as the origin of the error.
                if (e.getNode() == null) e.setNode(node);
                Debug.logError(e, "Outermost exception: ");
                Debug.logError(e.getTarget(), "BSH Target Exception: ");
                Debug.logError(e.getCause(), "BSH Cause Exception: ");
                e.reThrow("Sourced file: " + sourceFileInfo);
            } catch (EvalError e) {
                // failsafe, set the Line as the origin of the error.
                if (e.getNode() == null) e.setNode(node);
                Debug.logError(e);
                e.reThrow("Sourced file: " + sourceFileInfo);
            } catch (Exception e) {
                Debug.logError(e);
                EvalError newError = new EvalError("Sourced file: " + sourceFileInfo + " unknown error: " + e.getMessage(), node, callstack);
                Debug.logError(newError);
                throw newError;
            } catch (TokenMgrError e) {
                Debug.logError(e);
                EvalError newError = new EvalError("Sourced file: " + sourceFileInfo + " Token Parsing Error: " + e.getMessage(), node, callstack);
                Debug.logError(newError);
                throw newError;
            } finally {
                // reinit the callstack
                if (callstack.depth() > 1) {
                    callstack.clear();
                    callstack.push(nameSpace);
                }
            }
        }
        return Primitive.unwrap(retVal);
    }
}
