package org.ofbiz.designer.pattern;



import org.ofbiz.wrappers.xml.*;

import java.util.*;

import java.lang.reflect.*;

import java.io.*;

import org.ofbiz.designer.generic.IDRefHelper;

import org.ofbiz.designer.util.*;

import org.ofbiz.designer.networkdesign.NetworkDesign;



import java.net.*;



public class XmlWrapper {

    public static final String XMLDIR = System.getProperty("WF_XMLDIR");
    public static final String DTDDIR = System.getProperty("WF_DTDDIR");

    static final String IDREF = "IDREF";

    static final String IDREFS = "IDREFS";



    private Hashtable reverseLookup = new Hashtable();

    IXml xml;



    public XmlWrapper(IXml xmlIn) {

        xml = xmlIn;

        System.out.println("IXML = " + xmlIn);

        initializeReverseLookupTable();        
    }

    public String getRootElementName() {
        
        return xml.getRootElementName();
        
    }

    static Hashtable openXmls = new Hashtable();





    public static XmlWrapper openDocument(File file) {

        try{

            if(openXmls.containsKey(file.toURL()))

                return(XmlWrapper)openXmls.get(file.toURL());

	    fixDtd(file.toURL());

            IXml xml = Xml.openDocument(file);

            XmlWrapper returnValue =  new XmlWrapper(xml);

            //openXmls.put(file.toURL(), returnValue);
            openXmls.put(file, returnValue);

            return returnValue;

        } catch(IOException e) {

            e.printStackTrace();

            return null;

        }

    }



    public static XmlWrapper openDocument(URL url) {

        if(openXmls.containsKey(url))

            return(XmlWrapper)openXmls.get(url);



        try {

	    fixDtd(url);

            IXml xml = Xml.openDocument(url);

            System.out.println("XML open " + xml);

            XmlWrapper returnValue =  new XmlWrapper(xml);

            openXmls.put(url, returnValue);

            return returnValue;

        } catch(IOException e) {

            e.printStackTrace();

            return null;

        }

    }



    private static void fixDtd(URL url){

	try{

	    String fileName = url.getFile();

	    if (fileName == null)

		throw new RuntimeException("URL is NOT a file");

	    BufferedReader br = new BufferedReader(new FileReader(fileName));

	    String result = "";

	    while (true) {

		String line = br.readLine();

		if (line == null) 

		    break;

		if (line.trim().startsWith("<!DOCTYPE")) {

		    int lastSlash = Math.max(line.lastIndexOf("/"), line.lastIndexOf("\\"));

		    int lastQuote = line.lastIndexOf("\"");

		    String dtdName = line.substring(lastSlash+1, lastQuote);

		    result += "<!DOCTYPE NetworkDesign SYSTEM \"file:///" + DTDDIR + "/" + dtdName + "\">\n";

		} else

		    result += line + "\n";

	    }

	    br.close();



	    PrintWriter pr = new PrintWriter(new FileOutputStream(fileName));

	    pr.print(result);

	    pr.close();

	} catch (Exception e){

	    e.printStackTrace();

	}

    }



    public IIDRefBinding getIDRefBinding() {

        return xml.getIDRefBinding();

    }



    public void saveDocument() {

        try {

            Object key = getKey();

            if(key instanceof URL) {

                URL url = (URL)key;

                String fileName = url.getFile();

                xml.saveDocument(new File(fileName));

            } else

                xml.saveDocument();

        } catch(Exception e) {

            e.printStackTrace();

        }

    }



    public Object getKey() {

        if(!openXmls.containsValue(this)) throw new RuntimeException("Xml not found");

        Enumeration keys = openXmls.keys();

        while(keys.hasMoreElements()) {

            Object key = keys.nextElement();

            if(openXmls.get(key) == this)

                return key;

        }

        return null;

    }



    public void saveDocument(File file) {

        try {

            xml.saveDocument(file);

        } catch(Exception e) {

            e.printStackTrace();

        }

    }



    public static XmlWrapper newDocument(File file, String str) {

        try {

            return new XmlWrapper(Xml.newDocument(file, str));

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    public Object getIdRef(String idref) {

        try {

            Object rawObject = getIdRefRaw(idref);

            if(rawObject == null) return null;

            else return DataProxy.getProxyInstance(rawObject, this);

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    public static XmlWrapper getContainingXml(String idAttribute) {

        Enumeration keys = openXmls.keys();

        while(keys.hasMoreElements()) {

            XmlWrapper xml = (XmlWrapper)openXmls.get(keys.nextElement());

            if(xml.getIdRef(idAttribute) != null)

                return xml;

        }

        return null;

    }



    public static Object getHref(String href) {

        href = fixURL(href);

        try {

            if(href.indexOf("#") == -1)

                return openDocument(new URL(href)).getRoot();



            String key = href.substring(0, href.indexOf("#"));

            String idref = href.substring(href.indexOf("#")+1, href.length());

            return openDocument(new URL(key)).getIdRef(idref);

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    public static Object getHrefRaw(String href) {

        href = fixURL(href);

        try {

            if(href.indexOf("#") == -1)

                return openDocument(new URL(href)).getRoot();



            String key = href.substring(0, href.indexOf("#"));

            String idref = href.substring(href.indexOf("#")+1, href.length());

            return openDocument(new URL(key)).getIdRefRaw(idref);

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    public Object getIdRefRaw(String idref) {

        return xml.getIDRefBinding().getIdRef(idref);

    }



    public void setIdRef(String idref, Object obj) {

        if(DataProxy.proxies.containsValue(obj)) {

            Enumeration keys = DataProxy.proxies.keys();

            while(keys.hasMoreElements()) {

                Object key = keys.nextElement();

                if(DataProxy.proxies.get(key) == obj) {

                    xml.getIDRefBinding().setIdRef(idref, key);

                    return;

                }

            }

        }

        xml.getIDRefBinding().setIdRef(idref, obj);

    }



    public void removeIdRef(String idref) {

        xml.getIDRefBinding().removeIdRef(idref);

    }



    private static final int METHODROOT = 0;

    private static final int IDREFTYPE = 1;

    private static final int PREFIXTYPE = 2;



    boolean isIdMethod(Object obj, Method method) {

        if(method.getName().equals("setIdAttribute") && method.getReturnType().getName().equals("void") &&

           method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == java.lang.String.class)

            return true;

        else if(getIdrefMethodInfo(method) != null)

            return true;

        else return false;

    }



    private Method getMethod(Object deleg, String methodName) {

        Method methods[] = deleg.getClass().getMethods();

        for(int i=0; i<methods.length; i++)

            if(methods[i].getName().equals(methodName))

                return methods[i];

        return null;



    }



    public Object handleSetMethod(Object deleg, Method method, Object[] params) {

        try {

            Object tempParams[] = null;

            Object newObject = params[0];

            String methodName = method.getName();

            String getMethodName = "get" + methodName.substring("set".length(), methodName.length());

            if(getMethodName.endsWith("At") && params.length == 2 && params[1].getClass() == java.lang.Integer.class) {

                tempParams = new Object[1];

                tempParams[0] = params[1];

            }



            Method getMethod = getMethod(deleg, getMethodName);

            if(getMethod == null) throw new RuntimeException("couldn't find get method for " + getMethodName);



            Object removeObject = getMethod.invoke(deleg, tempParams);

            if(removeObject != null) removeIDsFromTree(removeObject);

            if(newObject != null) addIDsToTree(newObject);



            Object returnObj = method.invoke(deleg, params);

            initializeReverseLookupTable();

            return returnObj;

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    public Object handleAddMethod(Object deleg, Method method, Object[] params) {

        try {

            Object newObject = params[0];

            if(newObject != null) addIDsToTree(newObject);

            Object returnObj = method.invoke(deleg, params);

            initializeReverseLookupTable();

            return returnObj;

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    public Object handleRemoveMethod(Object deleg, Method method, Object[] params) {

        try {

            Object removeObject = null;

            String methodName = method.getName();

            String getMethodName = "get" + methodName.substring("remove".length(), methodName.length());

            if(getMethodName.endsWith("At") && params.length == 1 && params[0].getClass() == java.lang.Integer.class) {

                Method getMethod = getMethod(deleg, getMethodName);

                if(getMethod == null) throw new RuntimeException("couldn't find get method for " + getMethodName);

                Object[] tempParams = new Object[1];

                tempParams[0] = params[0];

                removeObject = getMethod.invoke(deleg, tempParams);

            } else if(params != null && params.length == 1)

                removeObject = params[0];

            else if(params != null || !methodName.endsWith("Attribute"))

                throw new RuntimeException("Cannot handle remove method " + methodName);



            if(removeObject != null) removeIDsFromTree(removeObject);

            Object returnObj = method.invoke(deleg, params);

            initializeReverseLookupTable();

            if(removeObject != null) ((IRegistrar)DataProxy.proxies.get(removeObject)).fireDataGone();

            return returnObj;                 

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    Object handleIdMethod(Object deleg, Method method, Object[] params) {

        String[] methodInfo = null;

        try {

            if(method.getName().equals("setIdAttribute") && method.getReturnType().getName().equals("void") &&

               method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == java.lang.String.class) {



                String newValue = (String)params[0];

                String oldValue = (String)deleg.getClass().getMethod("getIdAttribute", null).invoke(deleg, null);

                if(!newValue.equals(oldValue) && getIdRefRaw(newValue) != null)

                    throw new RuntimeException("Attempt to set duplicate IDAttribute");

                Object targetObject = getIdRefRaw(oldValue);

                HashSet set = (HashSet)reverseLookup.get(targetObject);

                if(set != null) {

                    Iterator it = set.iterator();

                    while(it.hasNext()) {

                        Object[] triple = (Object[])it.next();

                        Object idrefContainer = triple[0];

                        String idrefVariable = (String)triple[1];

                        String idrefType = (String)triple[2];



                        try {

                            if(idrefType.equals(IDREFS)) {

                                String existingValue = (String)idrefContainer.getClass().getMethod("get" + idrefVariable + "Attribute", null).invoke(idrefContainer, null);

                                String tempNewValue = IDRefHelper.removeIDRef(existingValue, oldValue);

                                Object[] tempParams = {tempNewValue};

                                Class[] paramClasses = {java.lang.String.class};

                                idrefContainer.getClass().getMethod("set" + idrefVariable + "Attribute", paramClasses).invoke(idrefContainer, tempParams);

                                Object proxyObject = DataProxy.proxies.get(idrefContainer);

                                if(proxyObject != null)

                                    ((IRegistrar)proxyObject).fire();

                            } else idrefContainer.getClass().getMethod("remove" + idrefVariable + "Attribute", null).invoke(idrefContainer, null);

                        } catch(Exception e) {

                            e.printStackTrace();

                        }

                    }

                    reverseLookup.remove(targetObject);

                }

                Object returnObj = method.invoke(deleg, params);

                removeIdRef(oldValue);

                setIdRef(((String)params[0]), deleg);

                return returnObj;

            } else if((methodInfo = getIdrefMethodInfo(method)) != null) {

                String newValue = null;

                if(params != null && params.length != 0)

                    newValue = (String)params[0];

                String oldValue = (String)deleg.getClass().getMethod("get" + methodInfo[0] + "Attribute", null).invoke(deleg, null);



                if(methodInfo[PREFIXTYPE].equals("set") && methodInfo[IDREFTYPE].equals(IDREF)) {

                    if(getIdRef(newValue) == null) throw new RuntimeException("Attempting to set illegal idref " + newValue);

                    removeReverseMapping(deleg, oldValue, methodInfo[METHODROOT]);

                    setReverseMapping(deleg, newValue, methodInfo[METHODROOT], IDREF);

                } else if(methodInfo[PREFIXTYPE].equals("set") && methodInfo[IDREFTYPE].equals(IDREFS)) {

                    String[] oldList = IDRefHelper.getReferenceArray(oldValue);

                    String[] newList = IDRefHelper.getReferenceArray(newValue);



                    Object[] removeList = ArrayHelper.subtract(oldList, newList);

                    Object[] addList = ArrayHelper.subtract(newList, oldList);

                    if(removeList == null) removeList = new Object[0];

                    if(addList == null) addList = new Object[0];





                    for(int i=0; i<addList.length; i++)

                        if(getIdRef((String)addList[i]) == null) throw new RuntimeException("Attempting to set illegal idref " + addList[i]);



                    for(int i=0; i<removeList.length; i++)

                        removeReverseMapping(deleg, (String)removeList[i], methodInfo[METHODROOT]);



                    for(int i=0; i<addList.length; i++)

                        setReverseMapping(deleg, (String)addList[i], methodInfo[METHODROOT], IDREFS);



                } else if(methodInfo[PREFIXTYPE].equals("remove") && methodInfo[IDREFTYPE].equals(IDREF)) {

                    removeReverseMapping(deleg, oldValue, methodInfo[METHODROOT]);

                } else if(methodInfo[PREFIXTYPE].equals("remove") && methodInfo[IDREFTYPE].equals(IDREFS)) {

                    String[] oldList = org.ofbiz.designer.generic.IDRefHelper.getReferenceArray(oldValue);

                    for(int i=0; i<oldList.length; i++)

                        removeReverseMapping(deleg, oldList[i], methodInfo[METHODROOT]);

                } else throw new RuntimeException("Bad method prefix type in call to XmlWrapper::synchronizeReverseMapping()");



                return method.invoke(deleg, params);

            } else throw new RuntimeException("Incorrect invocation of XmlWrapper::handleIdMethod");

        } catch(Exception e) {

            e.printStackTrace();

            throw new RuntimeException(e.getMessage());

        }

    }



    //add a new idref value to hashtable

    public void setReverseMapping(Object obj, String value, String methodRoot, String idreftype) {

        Object targetObject = xml.getIDRefBinding().getIdRef(value);

        HashSet set = (HashSet)reverseLookup.get(targetObject);

        if(set == null) set = new HashSet();

        Object[] triple = {obj, methodRoot, idreftype};

        set.add(triple);

        reverseLookup.put(targetObject, set);

    }



    public void removeReverseMapping(Object obj, String value, String methodRoot) {

        if(value == null)

            return;



        Object targetObject = xml.getIDRefBinding().getIdRef(value);

        HashSet set = (HashSet)reverseLookup.get(targetObject);

        if(set == null) throw new RuntimeException("XmlWrapper::reverseLookup table does not contain target object");

        Iterator it = set.iterator();

        while(it.hasNext()) {

            Object[] pair = (Object[])it.next();

            if(pair[0] == obj && ((String)pair[1]).equals(methodRoot)) {

                System.err.println("removing idref from reverseLookup");

                set.remove(pair);

                return;

            }

        }

        throw new RuntimeException("XmlWrapper::reverseLookup table does not contain idref object");

    }



    static String[] getIdrefMethodInfo(Method method) {

        if(!method.getName().startsWith("set") && !method.getName().startsWith("remove")) return null;

        if(!method.getName().endsWith("Attribute")) return null;



        Method[] methods = method.getDeclaringClass().getMethods();

        for(int j=0; j<methods.length; j++) {

            if(!methods[j].getName().startsWith("get") ||

               !methods[j].getName().endsWith("Reference") ||

               methods[j].getParameterTypes().length != 1 ||

               methods[j].getParameterTypes()[0] != org.ofbiz.wrappers.xml.IIDRefBinding.class)

                continue;



            String idreftype = null;

            if(methods[j].getReturnType() == java.util.Vector.class)

                idreftype = XmlWrapper.IDREFS;

            else if(methods[j].getReturnType() == java.lang.Object.class)

                idreftype = XmlWrapper.IDREF;

            else continue;



            String methodName = methods[j].getName();

            String methodRoot = methodName.substring("get".length(), methodName.length()-"Reference".length());



            if(method.getName().endsWith(methodRoot + "Attribute")) {

                String setOrRemove = method.getName().startsWith("set")?"set":"remove";

                String[] methodInfo = {methodRoot, idreftype, setOrRemove};

                return methodInfo;

            } else return null;

        }

        return null;

    }



    public Object getRoot() {



        Object obj = xml.getRoot();

        return DataProxy.getProxyInstance(obj, this);

        /* olli: No exception is thrown from getInstance

        try {

            return DataProxy.getProxyInstance(obj, this);

        } catch(Exception e) {

            e.printStackTrace();

            return null;

        }

        */

    }



    void initializeReverseLookupTable() {

        Object root = xml.getRoot();

        initializeReverseLookupTable(root);

        //initializeReverseLookupTable( new NetworkDesign());

    }



    void initializeReverseLookupTable(Object root) {

        try {

            Vector objectList = new Vector();

            objectList.addElement(root);



            while(objectList.size() > 0) {

                Object obj = objectList.elementAt(0);

                if(obj == null || obj instanceof String) {

                    objectList.removeElementAt(0);

                    continue;

                }



                Field[] fields = obj.getClass().getDeclaredFields();

                //System.out.println("Scanning " + obj);

                for(int i=0; i<fields.length; i++) {

                    //System.out.println("Scanning field " + fields[i]);

                    if(fields[i].getType() == String.class) {

                        continue;

                    } else if(fields[i].getType() == java.util.Hashtable.class  && fields[i].getName().equals("_Attributes")) {

                        Vector idrefsMethodRoots = new Vector();

                        Method[] methods = obj.getClass().getMethods();

                        for(int j=0; j<methods.length; j++) {

                            if(!methods[j].getName().startsWith("get") ||

                               !methods[j].getName().endsWith("Reference") ||

                               methods[j].getParameterTypes().length != 1 ||

                               methods[j].getParameterTypes()[0] != org.ofbiz.wrappers.xml.IIDRefBinding.class)

                                continue;







                            // the method can return a Vector or Object, depending on whether dtd attribute is IDREFS or IDREF

                            boolean vectorReturnType = false;

                            if(methods[j].getReturnType() == java.util.Vector.class)

                                vectorReturnType = true;



                            String methodName = methods[j].getName();

                            String methodRoot = methodName.substring("get".length(), methodName.length()-"Reference".length());



                            Method getIdrefsORIdrefAttribute = null;

                            try {

                                getIdrefsORIdrefAttribute = obj.getClass().getMethod("get" + methodRoot + "Attribute", null);

                            } catch(NoSuchMethodException nsme) {

                                throw new RuntimeException("method get" + methodRoot + "Attributes does not exist");

                            }





                            String idrefs = (String)getIdrefsORIdrefAttribute.invoke(obj, null);

                            if(idrefs == null) continue;



                            if(vectorReturnType) {

                                String[] references = IDRefHelper.getReferenceArray(idrefs);

                                if(references == null || idrefs.trim().length() == 0) {

                                    obj.getClass().getMethod("remove" + methodRoot + "Attribute", null).invoke(obj, null);

                                    ((IRegistrar)DataProxy.proxies.get(obj)).fire();

                                    continue;

                                }



                                for(int k=0; k<references.length; k++) {

                                    Object target = xml.getIDRefBinding().getIdRef(references[k]);

                                    if(target == null) {

                                        Object[] params = {IDRefHelper.removeIDRef(idrefs, references[k])};

                                        if(params[0] != null) {

                                            Class[] paramClasses = {java.lang.String.class};

                                            Method setIdrefsAttribute = obj.getClass().getMethod("set" + methodRoot + "Attribute", paramClasses);

                                            setIdrefsAttribute.invoke(obj, params);

                                        } else {

                                            Method removeIdrefsAttribute = obj.getClass().getMethod("remove" + methodRoot + "Attribute", null);

                                            removeIdrefsAttribute.invoke(obj, null);

                                        }

                                        try {

                                            if(DataProxy.proxies.get(obj) != null)

                                                ((IRegistrar)DataProxy.proxies.get(obj)).fire();

                                        } catch(Exception e) {

                                            e.printStackTrace();

                                        }

                                    } else

                                        add2reverseLookup(target, obj, methodRoot, IDREFS);

                                }

                            } else {

                                Object target = xml.getIDRefBinding().getIdRef(idrefs);

                                if(target == null) {

                                    Method removeIdrefAttribute = obj.getClass().getMethod("remove" + methodRoot + "Attribute", null);

                                    removeIdrefAttribute.invoke(obj, null);

                                    try {

                                        ((IRegistrar)DataProxy.proxies.get(obj)).fire();

                                    } catch(NullPointerException e) {

                                    }

                                } else

                                    add2reverseLookup(target, obj, methodRoot, IDREF);

                            }

                        }

                    } else if(fields[i].getType() == java.util.Vector.class) {

                        Vector vec = (Vector)fields[i].get(obj);

                        for(int j=0; j<vec.size(); j++)

                            objectList.addElement(vec.elementAt(j));



                    } else {

                        try {

                            objectList.addElement(fields[i].get(obj));

                        } catch(Exception e) {

                            System.err.println("fields[i].getType():" + fields[i].getType() + "\nobj.getClass().getName():" +

                                               obj.getClass().getName() + "\nobj:" +  obj + "\nfields[i].getName():" + fields[i].getName());

                            throw e;

                        }

                    }



                }

                objectList.removeElementAt(0);

            }

        } catch(Exception e) {

            System.err.println("exception !!");

            e.printStackTrace();

        }

    }



    static HashSet ignorableClasses = new HashSet();

    static{

        ignorableClasses.add(String.class);

        ignorableClasses.add(Integer.class);

        ignorableClasses.add(Class.class);

        //ignorableClasses.add(org.ofbiz.wrappers.xml.core.String.class);

    }



    void removeIDsFromTree(Object root) {

        processIdsInTree(root, "Remove");

    }



    void addIDsToTree(Object root) {

        processIdsInTree(root, "Add");

    }



    private void processIdsInTree(Object root, String mode) {

        if(!"Add".equals(mode) && !"Remove".equals(mode))

            throw new RuntimeException("Invalid mode !");



        try {

            Vector objectList = new Vector();

            objectList.addElement(root);



            while(objectList.size() > 0) {

                Object obj = objectList.elementAt(0);

                if(obj == null);

                else if(!ignorableClasses.contains(obj.getClass())) {

                    Field[] fields = obj.getClass().getFields();

                    for(int i=0; i<fields.length; i++) {

                        if(fields[i].getType() == java.util.Hashtable.class  && fields[i].getName().equals("_Attributes")) {

                            try {

                                Method method = obj.getClass().getMethod("getIdAttribute", null);

                                if(method == null) continue;

                                String id = (String)method.invoke(obj, null);

                                if(mode.equals("Remove")) removeIdRef(id);

                                else setIdRef(id, obj);

                            } catch(NoSuchMethodException e) {

                                continue;

                            }

                        } else if(fields[i].getType() == java.util.Vector.class) {

                            Vector vec = (Vector)fields[i].get(obj);

                            for(int j=0; j<vec.size(); j++)

                                objectList.addElement(vec.elementAt(j));

                        } else objectList.addElement(fields[i].get(obj));

                    }

                } else if(obj instanceof Vector)

                    objectList.addAll((Vector)obj);

                objectList.removeElementAt(0);

            }

        } catch(Exception e) {

            e.printStackTrace();

        }

    }





    private void add2reverseLookup(Object target, Object idrefContainer, String idrefName, String idrefType) {

        HashSet set = (HashSet)reverseLookup.get(target);

        if(set == null)

            set = new HashSet();

        Object[] triple = {idrefContainer, idrefName, idrefType};

        set.add(triple);

        reverseLookup.put(target, set);

    }



    public String generateUniqueName(String prefix) {

        int x = 0;

        while(true) {

            String temp = prefix + x++;

            if(getIdRefRaw(temp) == null)

                return temp;

        }

    }



    public static String fixURL(String url) {

        url = url.replace('\\', '/');

        while(url.startsWith("/"))

            url = url.substring(1, url.length());

        if(!url.startsWith("http://") && !url.startsWith("file:///"))

            url = "file:///" + url;

        return url;

    }

}
