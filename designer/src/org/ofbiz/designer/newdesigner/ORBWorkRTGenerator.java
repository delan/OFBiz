package org.ofbiz.designer.newdesigner;

import java.io.*;
import org.ofbiz.designer.util.*;
import java.util.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;
import java.net.*;
import org.ofbiz.designer.dataclass.IDataClass;
import org.ofbiz.designer.dataclass.IField;

class ORBWorkRTGenerator extends BaseRTGenerator {
    private static String xmlDir = System.getProperty("WF_XMLDIR");
    private static String dtdDir = System.getProperty("WF_DTDDIR");

    private String workflowName, targetDir, xmlFile, rootTaskName;

    ORBWorkRTGenerator(String _workflowName, String _rootTaskName, String _targetDir){
        workflowName = _workflowName;
        rootTaskName = _rootTaskName;
        targetDir = _targetDir + "\\" + workflowName;
        xmlFile = xmlDir + "\\task\\" + _workflowName + ".xml";
    }

    public static void generate(String workflowName, String rootTaskName, String targetDir){
        ORBWorkRTGenerator or = new ORBWorkRTGenerator(workflowName, rootTaskName, targetDir);
        or.generateRuntime();
    }

    private static FileOutputStream openDeepFile(String fileName){
        StringTokenizer stk = new StringTokenizer(fileName, "\\");
        String fullName = "";
        File file = null;
        while(stk.hasMoreTokens()) {
            if(fullName.trim().length() == 0) 
                fullName += stk.nextToken();
            else
                fullName += "\\" + stk.nextToken();
            LOG.println("fullName is " + fullName);
            file = new File(fullName);
            if(stk.hasMoreTokens()) {
                if(!file.exists()) {
                    if(!file.mkdir()) {
                        WARNING.println("could not create directory " + file);
                        return null;
                    }
                }
            }
        }
        try{
            LOG.println("returning " + file.getName());
            return new FileOutputStream(file);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception{
        String name = "hello\\test";
        DataOutputStream ds = new DataOutputStream(openDeepFile(name));
        ds.writeBytes("hello");
        ds.close();
        if (true) return;

        String workflowName = System.getProperty("WF_NAME");
        String targetDir = "C:\\workflow\\public_html\\wflows";
        String rootTaskName = System.getProperty("WF_ROOTTASK");
        
        ORBWorkRTGenerator or = new ORBWorkRTGenerator(workflowName, rootTaskName, targetDir);
        or.generateRuntime();
    }

    private void generateRuntime() {
        XmlWrapper xml = XmlWrapper.openDocument(new File(xmlFile));
        try {
            ITaskWrapper rootTask = (ITaskWrapper)xml.getIdRef(rootTaskName);
            INetworkDesignWrapper context = (INetworkDesignWrapper)xml.getRoot();
            File file = new File(targetDir);
            if(file.exists() &&  file.isDirectory()) {
                WARNING.println(file + " already exists, quitting");
                return;
            }
            if(!file.mkdir()) {
                WARNING.println("could not create directory " + targetDir);
                return;
            }
            createDSpecs(rootTask);
            createESpecs(rootTask);
            createTSpecs(context);
            createClasses(rootTask);
            createHosts(context);
            createStart(rootTask);
            createTasks(context);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createDSpecs(ITaskWrapper task) {
        String dataDir = targetDir + "\\dspecs\\";
        File file = new File(dataDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + dataDir);
            return;
        }

        HashSet allData = getAllData(task, DATA);
        Iterator it = allData.iterator();
        while(it.hasNext()) {
            IDataClass data = (IDataClass)it.next();
            createDSpecsForData(data);
        }
    }

    private void createDSpecsForData(IDataClass data) {
        String url = data.getName();
        String dataDir = targetDir + "\\dspecs\\" + url;
        File file = new File(dataDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + dataDir);
            return;
        }
        dataDir = targetDir + "\\dspecs\\" + url + "\\" + workflowName;
        file = new File(dataDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + dataDir);
            return;
        }

        int count = data.getFieldList().getFieldCount();
        for(int i=0;i<count;i++) {
            IField field = data.getFieldList().getFieldAt(i);
            if(field.getType().getDimensionCount() > 0)
                throw new RuntimeException("Cannot handle Array type in " + url);
            if(field.getType().getSimpleTypeOrUrl().getUrl() != null)
                throw new RuntimeException("Cannot handle Complex type in " + url);
        }

        createXIDL(url, data);
        createXLoader(url);
        createXFactoryImpl(url);
        createXImpl(url, data);
    }

    private void createXIDL(String url, IDataClass data) {
        File idlFile = new File(targetDir + "\\dspecs\\" + url + "\\" + workflowName + "\\" + url + ".idl");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(idlFile))), CodeGenerator.IDL);
            ds.println("//");
            ds.println("// File: " + url + ".xml");
            ds.println("//");
            ds.println("// Interface for class: " + url);
            ds.println("//");
            ds.println();

            ds.println("typedef sequence<octet> octets;");
            ds.begin("interface " + url);{
                int count = data.getFieldList().getFieldCount();
                for(int i=0;i<count;i++) {
                    IField field = data.getFieldList().getFieldAt(i);
                    String name = field.getName();
                    String type = field.getType().getSimpleTypeOrUrl().getSimpleType();
                    ds.println("" + type + " get_" + name + "();");
                    ds.println("void set_" + name + "(in " + type + " " + name + ");");
                }

                ds.println("octets Export();");
                ds.println("void Save();");
                ds.println("void Remove(in Object ob);");
            } ds.end();
            ds.println();

            ds.begin("interface " + url + "Factory");{
                ds.println(url + " New" + url + "(in string instance);");
                ds.println(url + " Import" + url + "(in string instance, in octets state);");
            } ds.end();

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createXLoader(String url) {
        File file = new File(targetDir + "\\dspecs\\" + url + "\\" + workflowName + "\\" + url + "Loader.java");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

            ds.println("//");
            ds.println("// File: " + url + "Loader.java");
            ds.println("//");
            ds.println("// Implementation for class: " + url);
            ds.println("//");
            ds.println();
            ds.println("package " + workflowName + ";");
            ds.println("");
            ds.println("import IE.Iona.OrbixWeb._OrbixWeb;");
            ds.println("import IE.Iona.OrbixWeb.Features.Config;");
            ds.println("import org.omg.CORBA.ORB;");
            ds.println("import java.io.*;");
            ds.println("import java.lang.ClassNotFoundException;");
            ds.println("import IE.Iona.OrbixWeb._CORBA;");
            ds.println("import org.omg.CORBA.SystemException;");
            ds.println("import org.omg.CORBA.Object;");
            ds.println("import IE.Iona.OrbixWeb.Features.LoaderClass;");
            ds.println("import Meteor.OrbWork.*;");
            ds.println();
            ds.begin("public class " + url + "Loader extends LoaderClass");{
                ds.begin("public " + url + "Loader()");{
                    ds.println("super (true);");
                } ds.end();
                ds.println();
                ds.begin("public Object load(String interf, String marker, boolean isBind) throws SystemException");{
                    ds.begin("if (marker != null && marker != \"\" && interf.equals(\"" + url + "\"))");{
                        ds.println("return " + url + "Impl.load(marker, this);");
                    } ds.end();
                    ds.println("return null;");
                } ds.end();
                ds.println();
                ds.begin("public void save(Object obj, int reason) throws SystemException");{
                    ds.println("MeteorMonitor mm = new MeteorMonitor();");
                    ds.println("mm.RecordMessage( \"Saving: reason: \" + reason + \" ob: \" + obj );");
                    ds.println("String marker = _OrbixWeb.Object(obj)._marker();");
                    ds.begin("if (reason == _CORBA.processTermination || reason == _CORBA.explicitCall)");{
                        ds.println("" + url + "Impl impl = (" + url + "Impl)(((" + url + ")obj)._deref());");
                        ds.println("impl.save(marker);");
                    } ds.end();
                } ds.end();
            } ds.end();
            ds.println();

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createXFactoryImpl(String url) {
        File file = new File(targetDir + "\\dspecs\\" + url + "\\" + workflowName + "\\" + url + "FactoryImpl.java");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

            ds.println("package " + workflowName + ";");
            ds.println();
            ds.println("import IE.Iona.OrbixWeb._OrbixWeb;");
            ds.println("import IE.Iona.OrbixWeb.Features.Config;");
            ds.println("import org.omg.CORBA.ORB;");
            ds.println("import java.io.*;");
            ds.println("import java.lang.ClassNotFoundException;");
            ds.println();
            ds.println("import IE.Iona.OrbixWeb._CORBA;");
            ds.println("import org.omg.CORBA.SystemException;");
            ds.println("import org.omg.CORBA.Object;");
            ds.println("import IE.Iona.OrbixWeb.Features.LoaderClass;");
            ds.println();
            ds.println();
            ds.begin("public class " + url + "FactoryImpl implements _" + url + "FactoryOperations");{
                ds.println("private LoaderClass p_loader;");
                ds.println("//  constructors");
                ds.println("//");
                ds.begin("public " + url + "FactoryImpl(LoaderClass loader)");{
                    ds.println("p_loader = loader;");
                } ds.end();
                ds.println("");
                ds.println("// methods");
                ds.println("//");
                ds.begin("public " + url + " New" + url + "(String instance) throws SystemException");{
                    ds.println(url + "Impl new_object = null;");
                    ds.println("new_object = new " + url + "Impl(instance);");
                    ds.println(url + " new_object_ref = new _tie_" + url + "(new_object, instance, p_loader); ");
                    ds.println("return new_object_ref;");
                } ds.end();
                ds.begin("public " + url + " Import" + url + "(String instance, byte[] state) throws SystemException");{
                    ds.println("ByteArrayInputStream baiStream = null;");
                    ds.println("ObjectInputStream oiStream = null;");
                    ds.println("" + url + "Impl new_object = null;");
                    ds.println("" + url + " new_object_ref = null;");
                    ds.begin("try ");{
                        ds.println("baiStream = new ByteArrayInputStream(state);");
                        ds.println("oiStream = new ObjectInputStream(baiStream);");
                        ds.println("new_object = (" + url + "Impl)oiStream.readObject();");
                        ds.println("new_object_ref = new _tie_" + url + "(new_object, instance, p_loader);");
                    } ds.end();
                    ds.begin("catch (ClassNotFoundException ce) ");{
                        ds.println("Meteor.OrbWork.MeteorMonitor mm = new Meteor.OrbWork.MeteorMonitor();");
                        ds.println("Meteor.OrbWork.Debug.Msg(mm, Meteor.OrbWork.Debug.Strict, \"" + url + "FactoryImpl.Import" + url + ": \" + ce);");
                    } ds.end();
                    ds.begin("catch(IOException ioe) ");{
                        ds.println("Meteor.OrbWork.MeteorMonitor mm = new Meteor.OrbWork.MeteorMonitor();");
                        ds.println("Meteor.OrbWork.Debug.Msg(mm, Meteor.OrbWork.Debug.Strict, \"" + url + "FactoryImpl.Import" + url + ": \" + ioe);");
                    } ds.end();
                    ds.println("return new_object_ref;");
                } ds.end();
            } ds.end();


            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createXImpl(String url, IDataClass data) {
        int count = data.getFieldList().getFieldCount();
        File file = new File(targetDir + "\\dspecs\\" + url + "\\" + workflowName + "\\" + url + "Impl.java");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

            ds.println("//");
            ds.println("// File: " + url + "Impl.java");
            ds.println("//");
            ds.println("// Implementation for class: " + url + "");
            ds.println("//");
            ds.println();
            ds.println("package " + workflowName + ";");
            ds.println();
            ds.println("import IE.Iona.OrbixWeb._OrbixWeb;");
            ds.println("import IE.Iona.OrbixWeb.Features.Config;");
            ds.println("import org.omg.CORBA.ORB;");
            ds.println("import java.io.*;");
            ds.println("import java.lang.ClassNotFoundException;");
            ds.println("import IE.Iona.OrbixWeb._CORBA;");
            ds.println("import org.omg.CORBA.SystemException;");
            ds.println("import org.omg.CORBA.Object;");
            ds.println("import IE.Iona.OrbixWeb.Features.LoaderClass;");
            ds.println();
            ds.begin("class " + url + "Impl implements _" + url + "Operations, Serializable ");{
                ds.println("public static long serialVersionUID = 100;");
                ds.println("// non-IDL attributes");
                ds.println("private static String p_data_dir = null;");
                ds.println("private String p_instance;");
                ds.println("private boolean p_save;");
                ds.println("static String p_separator = System.getProperty(\"file.separator\");");
                ds.println();
                ds.println("// IDL declared attributes");

                for(int i=0;i<count;i++) {
                    IField field = data.getFieldList().getFieldAt(i);
                    String name = field.getName();
                    String vartype = field.getType().getSimpleTypeOrUrl().getSimpleType();
                    vartype = convert(vartype);
                    ds.println("private " + vartype + " p_" + name + ";");
                }

                ds.println();
                ds.println("// IDL methods");
                ds.println();
                ds.println("// Constructor");
                ds.begin("public " + url + "Impl(String instance)");{
                    ds.println("p_instance = new String(instance);");
                    ds.println("p_save = true;");
                    ds.println("p_data_dir = Meteor.OrbWork.DataServerSrv.p_DataDir + p_separator + \"" + workflowName + "\" + p_separator + \"" + url + "\";");
                    ds.println();

                    for(int i=0;i<count;i++) {
                        IField field = data.getFieldList().getFieldAt(i);
                        String name = field.getName();
                        String vartype = field.getType().getSimpleTypeOrUrl().getSimpleType();
                        vartype = convert(vartype);
                        if(vartype.equals("String"))
                            ds.println("p_" + name + " = (String)\"\";");
                    }

                } ds.end();
                ds.println();

                for(int i=0;i<count;i++) {
                    IField field = data.getFieldList().getFieldAt(i);
                    String name = field.getName();
                    String vartype = field.getType().getSimpleTypeOrUrl().getSimpleType();
                    vartype = convert(vartype);
                    ds.begin("public " + vartype + " get_" + name+ "() throws SystemException ");{
                        ds.println("return p_" + name + ";");
                    } ds.end();
                    ds.println();
                    ds.begin("public void set_" + name + "(" + vartype + " " + name + ") throws SystemException ");{
                        ds.println("p_" + name + " = " + name + ";");
                    } ds.end();
                    ds.println();
                }

                ds.println();
                ds.begin("public byte[] Export()");{
                    ds.println("ByteArrayOutputStream baoStream = null;");
                    ds.println("ObjectOutputStream oStream = null;");
                    ds.begin("try ");{
                        ds.println("baoStream = new ByteArrayOutputStream();");
                        ds.println("oStream = new ObjectOutputStream(baoStream);");
                        ds.println("oStream.writeObject(this);");
                        ds.println("return baoStream.toByteArray();");
                    } ds.end();
                    ds.begin("catch (IOException ie) ");{
                        ds.println("Meteor.OrbWork.MeteorMonitor mm = new Meteor.OrbWork.MeteorMonitor();");
                        ds.println("Meteor.OrbWork.Debug.Msg(mm, Meteor.OrbWork.Debug.Strict, \"" + url + "Impl.Export: IOException: \" + ie);");
                        ds.println("return null;");
                    } ds.end();
                } ds.end();
                ds.println();
                ds.begin("public void save(String fname) ");{
                    ds.println("FileOutputStream f = null;");
                    ds.println("ObjectOutput s = null;");
                    ds.println("String data_dir = Meteor.OrbWork.DataServerSrv.p_DataDir + p_separator + \"" + workflowName + "\" + p_separator + \"" + url + "\";");
                    ds.begin("");{
                        ds.println("Meteor.OrbWork.MeteorMonitor mm = new Meteor.OrbWork.MeteorMonitor();");
                        ds.println("mm.RecordMessage( \"Save: fname: \" + fname );");
                    } ds.end();
                    ds.begin("try ");{
                        ds.println("f = new FileOutputStream(data_dir + p_separator + fname);");
                        ds.println("s = new ObjectOutputStream(f);");
                        ds.println();
                        ds.println("s.writeObject(this);");
                        ds.println("s.flush();");
                        ds.println("s.close();");
                    } ds.end();
                    ds.begin("catch (IOException ie) ");{
                        ds.println("Meteor.OrbWork.MeteorMonitor mm = new Meteor.OrbWork.MeteorMonitor();");
                        ds.println("Meteor.OrbWork.Debug.Msg(mm, Meteor.OrbWork.Debug.Strict, \"" + url + "Impl.save: IOException: \" + ie);");
                    } ds.end();
                } ds.end();
                ds.println();
                ds.begin("public static Object load(String fname, LoaderClass loader) ");{
                    ds.println("FileInputStream in = null;");
                    ds.println("ObjectInputStream s = null;");
                    ds.println(url + " ref = null;");
                    ds.println(url + "Impl impl = null;");
                    ds.println("String data_dir = Meteor.OrbWork.DataServerSrv.p_DataDir + p_separator + \"" + workflowName + "\" + p_separator + \"" + url + "\";");
                    ds.begin("try ");{
                        ds.println("in = new FileInputStream( data_dir + p_separator + fname );");
                        ds.println("s = new ObjectInputStream(in);");
                        ds.println();
                        ds.println("impl =(" + url + "Impl)s.readObject();");
                        ds.println("ref = new _tie_" + url + "(impl, fname, loader);");
                    } ds.end();
                    ds.begin("catch (ClassNotFoundException ce) ");{
                        ds.println("Meteor.OrbWork.MeteorMonitor mm = new Meteor.OrbWork.MeteorMonitor();");
                        ds.println("Meteor.OrbWork.Debug.Msg(mm, Meteor.OrbWork.Debug.Strict, \"" + url + "Impl.load: Exception: \" + ce);");
                    } ds.end();
                    ds.begin("catch (IOException ie) ");{
                        ds.println("Meteor.OrbWork.MeteorMonitor mm = new Meteor.OrbWork.MeteorMonitor();");
                        ds.println("Meteor.OrbWork.Debug.Msg(mm, Meteor.OrbWork.Debug.Strict, \"" + url + "Impl.load: IOException: \" + ie);");
                    } ds.end();
                    ds.println();
                    ds.println("return ref;");
                } ds.end();
                ds.println();
                ds.begin("public void Save()throws SystemException ");{
                    ds.println("save(p_instance);");
                } ds.end();
                ds.println();
                ds.begin("public void Remove(Object ob) ");{
                    ds.println("File f = null;");
                    ds.println("String data_dir = Meteor.OrbWork.DataServerSrv.p_DataDir + p_separator + \"" + workflowName + "\" + p_separator + \"" + url + "\";");
                    ds.println("p_save = false;		// mark for non-save");
                    ds.println("f = new File(data_dir + p_separator + p_instance);");
                    ds.println("if (f.exists()) f.delete();");
                    ds.println("Meteor.OrbWork.DataServerSrv.p_ORB.disconnect(ob);");
                } ds.end();
            } ds.end();
            ds.println();
            ds.println();

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private  void createTSpecs(INetworkDesignWrapper context) {
        String taskDir = targetDir + "\\tspecs\\";
        File file = new File(taskDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + taskDir);
            return;
        }
        int count = context.getTaskCount();
        for(int i=0;i<count;i++) {
            ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
            if(task.getTaskType().equals("SynchronizationTaskIn"))
                continue;
            createTSpecsForTask(task);
        }
    }

    private void createTSpecsForTask(ITaskWrapper task) {
        // ID2NAME
        //String taskDir = targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute();
        String taskDir = targetDir + "\\tspecs\\" + task.getNameAttribute();
        File file = new File(taskDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + taskDir);
            return;
        }
        // ID2NAME
        //taskDir = targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task";
        taskDir = targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task";
        file = new File(taskDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + taskDir);
            return;
        }
        createFoutputs(task);
        createInputs(task);
        createCreate(task);
        createSoutputs(task);
        createData(task);
        createIndexHtml(task);
        createAdminHtml(task);
        createAcl(task);
        createLocalHandlers(task);
        createType(task);
        createParams(task);
        createStationHtml(task);
        createDatain(task);
        createXHtml(task); 
    }

    private void createFoutputs(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\" + "foutputs");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\" + "foutputs");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            String[] eArcs = IDRefHelper.getReferenceArray(task.getOutarcsAttribute());
            int eCount = 0;
            for(int i=0; i<eArcs.length; i++) {
                IArcWrapper arc = (IArcWrapper)task.getXml().getIdRef(eArcs[i]);
                if(arc.getArctypeAttribute().equals("Fail")) eCount++;
            }
            ds.println(eCount);
            for(int i=0; i<eArcs.length; i++) {
                IArcWrapper arc = (IArcWrapper)task.getXml().getIdRef(eArcs[i]);
                if(arc.getArctypeAttribute().equals("Success")) continue;
                ITask newTask = (ITask)task.getXml().getIdRef(arc.getDestinationAttribute());
                String exception = arc.getExceptionAttribute();
                if(exception == null) throw new RuntimeException("No exception associated with arc in org.ofbiz.designer.task " + task.getNameAttribute());
                // ID2NAME
                //ds.println("fail initial@" + newTask.getIdAttribute() + " " + exception);
                ds.println("fail initial@" + newTask.getNameAttribute() + " " + exception);
                ds.println("true");

                int mcount = arc.getMappingCount();
                String objList = "";                    
                for(int j=0; j<mcount; j++) {
                    IMapping mapping = arc.getMappingAt(j);
                    String temp = mapping.getFirstElementAttribute();
                    IOutput output = (IOutput)task.getXml().getIdRef(temp);
                    objList += output.getVariablenameAttribute();
                }

                ds.println(objList);
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void createSoutputs(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\" + "soutputs");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\" + "soutputs");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            Vector andBlock = getOutputDNF(task);
            ds.println(andBlock.size() + " AND");
            int blockSize = andBlock.size();
            for(int j=0; j<blockSize; j++) {
                Object[] triple = (Object[])andBlock.elementAt(j);
                ITaskWrapper newTask = (ITaskWrapper)triple[0];
                String condition = (String)triple[1];
                String objects = (String)triple[2];
                // ID2NAME
                //ds.println("done initial@" + newTask.getIdAttribute() + " ");
                ds.println("done initial@" + newTask.getNameAttribute() + " ");
                ds.println(condition);
                ds.println(objects);
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }       

    private void createInputs(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\" + "inputs");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\" + "inputs");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            Vector inputList = getInputDNF(task);
            int size = inputList.size();
            ds.println(size);
            for(int i=0; i<size; i++) {
                Vector andBlock = (Vector)inputList.elementAt(i);
                ds.print(andBlock.size() + " ");
                int blockSize = andBlock.size();
                for(int j=0; j<blockSize; j++) {
                    ITaskWrapper newTask = (ITaskWrapper)andBlock.elementAt(j);
                    // ID2NAME
                    //ds.print("done@" + newTask.getIdAttribute() + " ");
                    ds.print("done@" + newTask.getNameAttribute() + " ");
                }
                ds.println();
                for(int j=0; j<blockSize; j++) {
                    ITaskWrapper sourceTask = (ITaskWrapper)andBlock.elementAt(j);
                    IArcWrapper arc = getConnectingArc(sourceTask, task);
                    int count = arc.getMappingCount();
                    for(int k=0; k<count; k++) {
                        IMapping mapping = arc.getMappingAt(k);
                        IOutput output = ((IOutput)arc.getXml().getIdRef(mapping.getFirstElementAttribute()));
                        String type = output.getDatatypeurlAttribute();
                        String name = output.getVariablenameAttribute();
                        //ID2NAME
                        //ds.print(" " + name + "@done@" + sourceTask.getIdAttribute() + " " + type);
                        ds.print(" " + name + "@done@" + sourceTask.getNameAttribute() + " " + type);
                    }
                }
                ds.println();
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createCreate(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\" + "create");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\" + "create");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            HashSet createList = getCreateList(task);
            ds.println(createList.size());
            Iterator it = createList.iterator();
            while(it.hasNext()) {
                String[] pair = (String[])it.next();
                ds.println(pair[0] + " " + pair[1]);
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createData(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\" + "data");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\" + "data");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            HashSet dataSet = getData(task);
            Iterator it = dataSet.iterator();
            while(it.hasNext()) {
                IDataClass data = (IDataClass)it.next();
                int count = data.getFieldList().getFieldCount();
                for(int i=0; i<count; i++) {
                    org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
                    ds.println(data.getName() + " " + field.getName() + " " + field.getType());
                }
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createIndexHtml(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\" + "index.html");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\" + "index.html");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.XML);

            ds.begin("html");{
                ds.begin("head");{
                    ds.println("<title>" + task.getNameAttribute() + " Task Page</title>");
                }ds.end();
                ds.begin("body");{
                    ds.println("<h1>" + task.getNameAttribute() + " Task Page</h1>");
                    ds.println("<P>Available links:");
                    ds.begin("OL");{
                        ds.println("<LI>Access the <A HREF=\"station.html\">" + task.getNameAttribute() + " org.ofbiz.designer.task page</A>. </LI>");
                        ds.println("<LI>Access the <A HREF=\"admin.html\"> org.ofbiz.designer.task administrator page</A>. </LI>");
                    }ds.end();
                }ds.end();
            }ds.end();

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createAdminHtml(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\" + "admin.html");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\" + "admin.html");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.XML);

            ds.begin("html");{
                ds.begin("head");{
                    ds.println("<title>" + task.getNameAttribute() + " Administrator Interface</title>");
                }ds.end();
                ds.begin("BODY bgcolor=\"#FFFFFF\"");{
                    ds.println("<h1>" + task.getNameAttribute() + " Task Administrator Page</h1>");
                    ds.println("<P>You may:");
                    ds.println("<P>Access org.ofbiz.designer.task <A HREF=\"worklist.html\"> worklist</A>");
                    ds.println("<P><A HREF=\"config.html\">View</A> org.ofbiz.designer.task configuration");
                    ds.println("<P><A HREF=\"editconfig.html\">Edit</A> org.ofbiz.designer.task configuration");
                    ds.println("<P><A HREF=\"resetconfig\">Reset</A> org.ofbiz.designer.task configuration");
                    ds.println("<P><A HREF=\"shutdown\">Shutdown</A> the org.ofbiz.designer.task");
                }ds.end();
            }ds.end();
            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createAcl(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task\\" + "acl");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task\\" + "acl");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            int count = task.getDataSecurityMaskCount();
            for(int i=0; i<count; i++) {
                IDataSecurityMask mask = task.getDataSecurityMaskAt(i);
                int fieldCount = mask.getFieldMaskCount();
                for(int j=0; j<fieldCount; j++) {
                    IFieldMask fieldMask = mask.getFieldMaskAt(j);
                    ds.println("* * " + mask.getDatanameAttribute() + " " + fieldMask.getFieldnameAttribute() + " " + fieldMask.getAccesstypeAttribute());
                }
            }
            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createLocalHandlers(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task\\" + "localhandlers");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task\\" + "localhandlers");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            int count = task.getTaskExceptionCount();
            for(int i=0; i<count; i++) {
                ITaskException exception = task.getTaskExceptionAt(i);
                ILocalHandler handler = exception.getLocalHandler();
                if(handler == null) {
                    WARNING.println("WARNING ! NO HANDLER DEFINED FOR EXCEPTION " + exception.getDatatypeurlAttribute() + " in org.ofbiz.designer.task " + task.getNameAttribute());
                    continue;
                }
                String printStr = "";
                if(exception.getDatatypeurlAttribute() != null) printStr += exception.getDatatypeurlAttribute() + " ";
                if(handler.getRetrytimesAttribute() != null) printStr += handler.getRetrytimesAttribute() + " ";
                if(handler.getRetrydelayAttribute() != null) printStr += handler.getRetrydelayAttribute() + " ";
                if(handler.getRethrowexceptionAttribute() != null) printStr += handler.getRethrowexceptionAttribute() + " ";
                if(handler.getEmailAttribute() != null) printStr += handler.getEmailAttribute() + " ";

                ds.println(printStr);
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createType(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task\\" + "type");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task\\" + "type");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);
            ds.println(convertTaskType(task.getTaskType()));
            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createParams(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task\\" + "params");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task\\" + "params");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            Hashtable table = new Hashtable();
            int count = task.getInvocationCount();
            for(int i=0; i<count; i++) {
                IInvocation invocation = task.getInvocationAt(i);
                int iCount = invocation.getParameterCount();
                for(int j=0; j<iCount; j++) {
                    IParameter parameter = invocation.getParameterAt(j);
                    String name = parameter.getVariablenameAttribute();
                    String type = parameter.getDatatypeurlAttribute();
                    table.put(name, type);
                }
            }

            count = task.getOutputCount();
            for(int i=0; i<count; i++) {
                IOutput output = task.getOutputAt(i);
                String name = output.getVariablenameAttribute();
                String type = output.getDatatypeurlAttribute();
                table.put(name, type);
            }

            Enumeration keys = table.keys();
            ds.println(table.size());
            while(keys.hasMoreElements()) {
                String name = (String)keys.nextElement();
                String type = (String)table.get(name);
                ds.println(type + " " + name);
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createStationHtml(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task\\" + "station.html");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task\\" + "station.html");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.XML);

            ds.begin("html");{
                ds.begin("head");{
                    ds.println("<title>" + task.getNameAttribute() + " Task Page</title>");
                }ds.end();
                ds.begin("FRAMESET rows=\"20%, 80%\"");{
                    ds.println("<frame scrolling=\"no\" src=\"title.html\">");
                    ds.begin("FRAMESET cols=\"30%,70%\"");{
                        ds.println("<frame src = \"worklist.html\">");
                        ds.println("<frame name = \"workspace\" src = \"empty.html\">");
                    }ds.end();
                }ds.end();
            }ds.end();

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createDatain(ITaskWrapper task) {
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task\\" + "datain");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task\\" + "datain");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);
            Hashtable dataSet = getDataHashtable(task);
            Enumeration it = dataSet.keys();
            Stack strings = new Stack();
            while(it.hasMoreElements()) {
                String dataName = (String)it.nextElement();
                IDataClass data = (IDataClass)dataSet.get(dataName);
                int count = data.getFieldList().getFieldCount();
                for(int i=0;i<count;i++) {
                    org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
                    String printStr = "%" + data.getName() + "-" + dataName + "-" + field.getName() + "% " + field.getType();
                    strings.push(printStr);
                }
            }

            ds.println(strings.size());
            while(!strings.isEmpty())
                ds.println((String)strings.pop());

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createXHtml(ITaskWrapper task) {
        Hashtable dataSet = getDataHashtable(task);
        // ID2NAME
        //File file = new File(targetDir + "\\tspecs\\" + org.ofbiz.designer.task.getIdAttribute() + "\\org.ofbiz.designer.task\\" + org.ofbiz.designer.task.getIdAttribute() + ".html");
        File file = new File(targetDir + "\\tspecs\\" + task.getNameAttribute() + "\\org.ofbiz.designer.task\\" + task.getNameAttribute() + ".html");
        CodeGenerator ds = null;
        try {
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            ds.begin("html");{
                ds.begin("head");{
                    ds.println("<title>" + task.getNameAttribute() + " Station</title>");
                }ds.end();
                ds.begin("BODY bgcolor=\"#FFFFFF\"");{
                    ds.begin("form action=\"%ORBWORK-SERVER%/finish\" method=\"POST\"");{
                        ds.println("<input type=\"hidden\" name=\"ORBWORK-ID\" value=\"%ORBWORK-ID%\">");
                        ds.println("<input type=\"hidden\" name=\"ORBWORK-KEY\" value=\"%ORBWORK-KEY%\">");
                        ds.println("<p align=\"left\"><strong>" + task.getNameAttribute() + " Task</ strong></p>");
                        ds.println("<p><font size=\"4\"><b>%ORBWORK-STATUS% !</b> Instance: <b>%ORBWORK-ID%</b> </font></p>");
                        ds.println("<p>");
                        ds.println("<input type=\"submit\" name=\"ORBWORK-ACTION\" value=\"Select\">");
                        ds.println("<input type=\"submit\" name=\"ORBWORK-ACTION\" value=\"Done\">");
                        ds.println("<input type=\"submit\" name=\"ORBWORK-ACTION\" value=\"Cancel\">");
                        ds.println("<input type=\"reset\" value=\"Reset\"> </p>");
                        ds.println("<p>");

                        Enumeration it = dataSet.keys();
                        while(it.hasMoreElements()) {
                            String dataName = (String)it.nextElement();
                            IDataClass data = (IDataClass)dataSet.get(dataName);
                            ds.println("<BR>" + dataName + "<BR><BR>");
                            int count = data.getFieldList().getFieldCount();
                            for(int i=0;i<count;i++) {
                                org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
                                String printStr = data.getName() + "-" + dataName + "-" + field.getName();
                                ds.println("&nbsp;&nbsp;&nbsp;&nbsp;" + field.getName() + ":<input type=\"text\" size=\"40\" name=\"" + printStr + "\"  value=\"%" + printStr + "%\">");
                                ds.println("<BR>");
                                ds.println();
                            }
                        }
                        ds.println("<p><p>");
                        ds.println("<input type=\"submit\" name=\"ORBWORK-ACTION\" value=\"Select\">");
                        ds.println("<input type=\"submit\" name=\"ORBWORK-ACTION\" value=\"Done\">");
                        ds.println("<input type=\"submit\" name=\"ORBWORK-ACTION\" value=\"Cancel\">");
                        ds.println("<input type=\"reset\" value=\"Reset\"> </p>");
                    }ds.end();
                    ds.println("<hr>");
                    ds.println(" HTML file has been generated by the NRL runtime generator.");
                }ds.end();
            }ds.end();
            ds.println();
            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private  void createClasses(ITaskWrapper task) {
        try {
            HashSet allData = getAllData(task, DATA);
            File file = new File(targetDir + "\\classes");
            CodeGenerator ds = null;
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            int count = allData.size();
            LOG.println("org.ofbiz.designer.task is " + task);
            String host = task.getHostAttribute();
            ds.println(count);
            Iterator it = allData.iterator();
            while(it.hasNext()) {
                String data = ((IDataClass)it.next()).getName();
                ds.println(data + " " + host);
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private  void createHosts(INetworkDesign context) {
        try {
            File file = new File(targetDir + "\\hosts");
            CodeGenerator ds = null;
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            int count = context.getTaskCount();
            HashSet hosts = new HashSet();
            for(int i=0;i<count;i++) {
                ITask task = (ITask)context.getTaskAt(i);
                hosts.add(task.getHostAttribute());
            }
            count = 0;
            Iterator it = hosts.iterator();
            while(it.hasNext()) {
                count++;
                it.next();
            }

            ds.println(count);
            it = hosts.iterator();
            while(it.hasNext())
                ds.println(it.next() + " 1");

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private  void createStart(ITask rootTask) {
        try {
            File file = new File(targetDir + "\\start");
            CodeGenerator ds = null;
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            ds.println(rootTaskName + " " + rootTask.getHostAttribute());

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private  void createTasks(INetworkDesignWrapper context) {
        try {
            File file = new File(targetDir + "\\tasks");
            CodeGenerator ds = null;
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.IDL);

            //Hashtable parents = getParents(context);
            ParentTaskTable parents = new ParentTaskTable(context);
            int count = context.getTaskCount();
            int realCount = 0; // excluding syncIn tasks
            for(int i=0;i<count;i++) {
                ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
                if(task.getTaskType().equals("SynchronizationTaskIn"))
                    continue;
                realCount++;
            }
            ds.println(realCount);

            for(int i=0;i<count;i++) {
                ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
                if(task.getTaskType().equals("SynchronizationTaskIn"))
                    continue;
                ITaskWrapper parent = (ITaskWrapper)parents.getParentTask(task.getIdAttribute());
                String parentID = parent==null?"OrbWork":parent.getNameAttribute();
                // ID2NAME
                //ds.println(org.ofbiz.designer.task.getIdAttribute() + " " + convertTaskType(org.ofbiz.designer.task.getTaskType()) + " " + org.ofbiz.designer.task.getHostAttribute() + " " + parentID);
                ds.println(task.getNameAttribute() + " " + convertTaskType(task.getTaskType()) + " " + task.getHostAttribute() + " " + parentID);
            }

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private  void createESpecs(ITaskWrapper task) {
        String dataDir = targetDir + "\\exceptions\\";
        File file = new File(dataDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + dataDir);
            return;
        }

        dataDir += workflowName + "\\";
        file = new File(dataDir);
        if(!file.mkdir()) {
            WARNING.println("could not create directory " + dataDir);
            return;
        }

        HashSet allExceptions = getAllData(task, EXCEPTION);
        Iterator it = allExceptions.iterator();
        while(it.hasNext()) {
            String data = (String)it.next();
            createESpecsForException(data);
        }
    }

    private void createESpecsForException(String data) {
        try {
            // trim data
            {
                int index = data.lastIndexOf(".");
                if(index != -1)
                    data = data.substring(index+1, data.length());
            }
            String file = targetDir + "\\exceptions\\" + workflowName + "\\" + data + ".java";
            CodeGenerator ds = null;
            ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(openDeepFile(file))), CodeGenerator.JAVA);

            ds.println("// Application specific exception");
            ds.println("//");
            ds.println();
            ds.println("package " + workflowName + ";");
            ds.println();
            ds.begin("public class " + data + " extends Meteor.OrbWork.OWSystemException");{
                ds.begin("  public " + data + "()");{
                    ds.println("    super();");
                }ds.end();
                ds.println();
                ds.begin("  public " + data +"(String originator)");{
                    ds.println("    super(originator);");
                }ds.end();
                ds.println();
                ds.begin("  public " + data + "(String originator, String msg)");{
                    ds.println("    super(originator, msg);");
                }ds.end();
            }ds.end();

            ds.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertTaskType(String xmlName) {
        if(xmlName.equals("NonTransactionalNetwork"))
            return "NETWORK";
        else if(xmlName.equals("HumanRealization"))
            return "HUMANCOMPUTER";
        else if(xmlName.equals("NonTransactionalTaskRealization"))
            return "NONTRANSACTIONAL";
        else if(xmlName.equals("TransactionalTaskRealization"))
            return "TRANSACTIONAL";
        else if(xmlName.equals("SynchronizationTaskIn"))
            return "DOMAINTRANSFER";
        else if(xmlName.equals("SynchronizationTaskOut"))
            return "DOMAINTRANSFER";
        else throw new RuntimeException("Cannot handle type " + xmlName);
    }
}

