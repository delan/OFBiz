
/**
 *	DataProxy.java
 * 
 *	This class is central to the DataCentric paradigm and has multi-faceted functionality.
 *	The DataCentric approach works as follows.
 * 
 *		1.	Specify the structure of classes via a dtd schema.
 *		
 *		2.	Invoke dxml to generate java classes to model the dtd.  For each dtd element
 *			X, dxml generates a similarly named class X and a corresponding interface 
 *			IX.
 *		
 *		3.	Invoke DCGenerator (perl) to generate the wrapper classes for each of the
 *			dxml generated classes.  Specifically, for each dxml class X (excluding 
 *			the generated interfaces and a few other utility classes) DCGenerator 
 *			generates two interfaces IXWrapper, IXSupportClass and one skeleton 
 *			class XSupportClass which implements the latter interface
 *		
 *		4.  Add functionality to XSupportClass as required.  XSupportClass has direct
 *			access to the original dxml class X through a common ancestor class
 *			AbstractDataSupportClass.  Modify IXSupportClass to reflect changes 
 *			if needed.
 *		
 *		5.	IXWrapper is implemented dynamically by DataProxy via the static method
 *			getProxyInstance, which takes as parameters the original dxml object X 
 *			and the xml document.  This method is typically invoked on the root 
 *			element of the xml document.  This method returns an object, which then 
 *			needs to be cast to IXWrapper.  All subsequent invocations on IXWrapper 
 *			return suitably "wrapped" IXWrapper objects and not the raw dxml object
 *			which is effectively hidden.  Also, when methods on the wrapper object 
 *			are passed objects that are themselves wrappers, these parameters get 
 *			unwrapped before being passed to the native dxml object underneath.  A
 *			typical usage scenario is as follows :
 * 
 *			IXml xml = Xml.openDocument(new File("DemoRole.xml"));
 *			
 *			// the one and only time "raw" data is used.
 *			IRoleDomain rawRoleDomain = (IRoleDomain)xml.getRoot();
 * 
 *			// for all subsequent use, use the wrapper
 *			IRoleDomainWrapper roleDomainWrapper = 
 *				(IRoleDomainWrapper) DataProxy.getProxyInstance(rawRoleDomain, xml);
 *		
 *			// all subsequent method invocations automatically return wrapper objects.
 *			IRoleWrapper roleWrapper = 
 *				(IRoleWrapper)roleDomainWrapper.getRoleAt( int arg0 );
 * 
 *		Except during the initialization stage, the dxml objects are hidden.  All 
 *		invocations to them go through the wrapper.  The wrapper objects provide
 *		various services above and beyond the functionality provided by the dxml
 *		classes.
 * 
 *		The dynamically generated wrapper objects inherits 3 different classes.
 *			-	The raw dxml class, 
 *			-	The XSupportClass which provides the user the
 *				flexibility of adding functionality which are 
 *				not datacentric, (ie defined in the dtd in step 1)
 *			-	The Registrar class, which allows the user to register and
 *				unregister themselves from the wrapper object.  Registering with the
 *				wrapper class enables the user to be notified about 2 key events.
 *				Anytime the data is changed, and when the data is about to be 
 *				destroyed.  This enables the user to centralize the synchronization
 *				routines.  For example, if the same data is being displayed in two 
 *				different GUI frames, then changing the information in one frame
 *				automatically notifies the other so it can then update its own display.
 */


package org.ofbiz.designer.pattern;

import java.lang.reflect.*;
import java.util.*;
import org.ofbiz.designer.util.*;

public class DataProxy implements InvocationHandler {
    static Hashtable proxies = new Hashtable();

    Object  dtdObj;
    IDataSupportClass dtdSupportObj;
    Registrar registrarObj;
    Method[] supportMethods, registerMethods;
    XmlWrapper xmlWrapper = null;

    //public static Object getProxyInstance(Object dtdobj, XmlWrapper xmlWrapper) throws Exception{
    public static Object getProxyInstance(Object dtdobj, XmlWrapper xmlWrapper) {
        try {
            if(proxies.containsKey(dtdobj))
                return proxies.get(dtdobj);
            String dtdClass = dtdobj.getClass().getName();
            String wrapperClassName = "I" + dtdClass +"Wrapper";
            IDataSupportClass dtdSupportObj = null;
            try {
                dtdSupportObj = (IDataSupportClass)Class.forName(dtdClass + "SupportClass").newInstance();
            } catch(ClassNotFoundException e) {
                //WARNING.println("Could not find support class " + e.getMessage());
                return dtdobj;
            }
            dtdSupportObj.setDtdObject(dtdobj);
            dtdSupportObj.setXml(xmlWrapper);
            if(dtdClass.indexOf(".") != -1) {
                int dotPosition = dtdClass.lastIndexOf(".");
                wrapperClassName = dtdClass.substring(0, dotPosition+1) + "I" + 
                                   dtdClass.substring(dotPosition+1, dtdClass.length()) +"Wrapper";
            }
            Registrar registrar = new Registrar();
            Object newproxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
                                                     new Class[] {Class.forName(wrapperClassName)}, 
                                                     new DataProxy(dtdobj, dtdSupportObj,registrar, xmlWrapper));

            registrar.setProxy(newproxy, wrapperClassName);
            proxies.put(dtdobj, newproxy);
            return newproxy;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public DataProxy(Object dtdobjIn, IDataSupportClass dtdSupportobjIn, Object registrarObjIn, XmlWrapper xmlWrapperIn) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        dtdSupportObj = dtdSupportobjIn;
        dtdObj = dtdobjIn;
        registrarObj = (Registrar)registrarObjIn;
        supportMethods = Class.forName(getInterface(dtdSupportObj.getClass().getName())).getMethods();
        registerMethods = registrarObj.getClass().getDeclaredMethods();
        xmlWrapper = xmlWrapperIn;
    }

    public Object invoke(Object obj, Method method, Object[] params) {
        try {
            if(methodsRequal(method, IDataSupportClass.class.getMethod("getDtdObject", null)))
                return method.invoke(dtdSupportObj, params);

            for(int i=0; i<registerMethods.length; i++)
                if(methodsRequal(method, registerMethods[i]))
                    return method.invoke(registrarObj, params);


            Object deleg = dtdObj;
            for(int i=0; i<supportMethods.length; i++) {
                if(methodsRequal(method, supportMethods[i])) {
                    deleg = dtdSupportObj;
                    break;
                }
            }

            // For DTD objects or support objects
            for(int j = 0; params != null && j < params.length; j++)
                params = (Object[])unwrapObject(params);

            Object returnObj;

            if(xmlWrapper.isIdMethod(deleg, method))
                returnObj = xmlWrapper.handleIdMethod(deleg, method, params);
            else if(deleg == dtdObj && method.getName().startsWith("set"))
                returnObj = xmlWrapper.handleSetMethod(deleg, method, params);
            else if(deleg == dtdObj && method.getName().startsWith("add"))
                returnObj = xmlWrapper.handleAddMethod(deleg, method, params);
            else if(deleg == dtdObj && method.getName().startsWith("remove"))
                returnObj = xmlWrapper.handleRemoveMethod(deleg, method, params);
            else
                returnObj = method.invoke(deleg, params);

            if(deleg == dtdObj && (method.getName().startsWith("set") || method.getName().startsWith("add") || 
                                   method.getName().startsWith("insert") || method.getName().startsWith("remove"))) {
                registrarObj.fire(); // notify data changes
            }

            return wrapObject(returnObj);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getInterface(String dtdClass) {
        if(dtdClass.indexOf(".") != -1) {
            int dotPosition = dtdClass.lastIndexOf(".");
            return dtdClass.substring(0, dotPosition+1) + "I" + dtdClass.substring(dotPosition+1, dtdClass.length());
        } else
            return "I" + dtdClass;
    }

    boolean methodsRequal(Method method1, Method method2) {
        if(!method1.getName().equals(method2.getName())) return false;
        else if(!method1.getReturnType().getName().equals(method2.getReturnType().getName())) return false;
        for(int i=0; i<method1.getParameterTypes().length; i++)
            if(!method1.getParameterTypes()[i].getName().equals(method2.getParameterTypes()[i].getName())) return false;
        return true;
    }

    Object wrapObject(Object returnObj) {
        try {
            if(returnObj == null) return null;
            else if(returnObj instanceof Integer || returnObj instanceof String || returnObj instanceof Boolean || returnObj instanceof XmlWrapper)
                return returnObj;
            else if(returnObj.getClass().isArray()) {
                if(Array.getLength(returnObj)==0)
                    return returnObj;// empty array
                String elementType = Array.get(returnObj, 0).getClass().getName();
                if(elementType.equals("java.lang.String") || elementType.equals("java.lang.Integer") || elementType.equals("java.lang.Boolean"))
                    return returnObj;
                Object returnArray = Array.newInstance(Class.forName(getInterface(elementType)), Array.getLength(returnObj));
                for(int i=0; i<Array.getLength(returnObj); i++)
                    Array.set(returnArray, i, DataProxy.getProxyInstance(Array.get(returnObj, i), xmlWrapper));
                return returnArray; 
            } else if(returnObj instanceof Vector) {
                Vector vec = (Vector)returnObj;

                if(vec.size() == 0)
                    return returnObj;// empty vector
                String elementType = vec.elementAt(0).getClass().getName();
                if(elementType.equals("java.lang.String") || elementType.equals("java.lang.Integer") || elementType.equals("java.lang.Boolean"))
                    return returnObj;
                Vector returnVec = new Vector();
                for(int i=0; i<vec.size(); i++)
                    returnVec.addElement(DataProxy.getProxyInstance(vec.elementAt(i), xmlWrapper));
                return returnVec; 
            } else if(returnObj instanceof Enumeration) {
                Enumeration enum = (Enumeration)returnObj;
                if(!enum.hasMoreElements())
                    return returnObj;//empty enumeration

                Object firstObject = enum.nextElement();
                String elementType = firstObject.getClass().getName();
                if(elementType.equals("java.lang.String") || elementType.equals("java.lang.Integer") || elementType.equals("java.lang.Boolean"))
                    return returnObj;

                Vector returnVec = new Vector();
                returnVec.addElement(DataProxy.getProxyInstance(firstObject, xmlWrapper));
                while(enum.hasMoreElements())
                    returnVec.addElement(DataProxy.getProxyInstance(enum.nextElement(), xmlWrapper));
                return returnVec.elements(); 
            } else return DataProxy.getProxyInstance(returnObj, xmlWrapper);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object unwrapObject(Object obj) {
        Object element = null;
        if(obj instanceof IDataSupportClass)
            return((IDataSupportClass)obj).getDtdObject();
        else if(obj.getClass().isArray()) {
            for(int i=0; i<Array.getLength(obj); i++) {
                if((element = Array.get(obj, i)) instanceof IDataSupportClass)
                    Array.set(obj, i, ((IDataSupportClass)element).getDtdObject());
            }
            return obj;
        } else if(obj instanceof Vector) {
            Vector vec = (Vector)obj;
            for(int i=0; i<vec.size(); i++) {
                if((element = vec.elementAt(i)) instanceof IDataSupportClass)
                    vec.set(i, ((IDataSupportClass)element).getDtdObject());
            }
            return obj;
        } else if(obj instanceof Enumeration) {
            Enumeration enum = (Enumeration)obj;
            Vector returnVec = new Vector();
            while(enum.hasMoreElements()) {
                if((element = enum.nextElement()) instanceof IDataSupportClass)
                    returnVec.addElement(((IDataSupportClass)element).getDtdObject());
                else
                    returnVec.addElement(element);
            }
            return returnVec.elements(); 
        }
        return obj;
    }
}
