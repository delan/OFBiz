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



class NRLRTGenerator extends BaseRTGenerator {
	public static String workflowName = null;
	public static String xtargetDir = null;

	public static String xmlDir = null;

	//public static final String xmlFile = xmlDir + "\\org.ofbiz.designer.task\\" + workflowName + ".xml";
	public static String xmlFile = null;
	public static String rootTaskName = null;

	private static ParentTaskTable parents = null;

	public static void genSpecs(String _workflowName,
								String _rootTaskName,
								String _xtargetDir
							   ) {

		workflowName = _workflowName;
		xtargetDir = _xtargetDir;
		rootTaskName = _rootTaskName;
		xmlDir = System.getProperty("WF_XMLDIR");
		xmlFile = xmlDir + "\\task\\" + workflowName + ".xml";


		NRLRTGenerator or = new NRLRTGenerator();
		XmlWrapper xml = XmlWrapper.openDocument(new File(xmlFile));
		INetworkDesignWrapper context = (INetworkDesignWrapper)xml.getRoot();
		//context.getArcAt(0).getMappingAt(0).

		parents = new ParentTaskTable(context);
		int count = context.getTaskCount();
		File wffile = null;
		for (int i=0; i<count; i++) {
			ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
			if (task.getRealization().getNetworkTaskRealization() != null) {
				wffile = new File(xtargetDir + "\\workflows\\" + task.getNameAttribute());
				if (wffile.exists() && wffile.isDirectory()) {
					WARNING.println(wffile + " already exists, quitting");
					return;
				}
				if (!wffile.mkdir()) {
					WARNING.println("could not create directory " + xtargetDir + "\\workflows\\" + task.getNameAttribute());
					return;
				}
			}
		}


		//batchOut.println("rmic -v1.2 -d classes wfruntime.Monitor_Serv wfruntime.Scheduler_Serv wfruntime.ServiceHost_Serv wfruntime.WFManager_Serv");

		//rmic -v1.2 -d \workflow\public_html\specfiles data.Plan

		for (int i=0; i<count; i++) {
			ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
			if ((task.getRealization().getNetworkTaskRealization() != null) && (task.getParentTask()==null)) {

				File batchFile = new File(xtargetDir + "\\workflows\\" + task.getNameAttribute() + "\\prepareJavaSpecs.bat");

				PrintWriter batchOut = null;

				try {
					batchOut = new PrintWriter(new FileOutputStream(batchFile));
				} catch (Throwable t) {
					t.printStackTrace();
				}


				batchOut.println("set oldcp=%classpath%");
				//  batchOut.println("set oldpath=%path%");

				batchOut.println("set classpath=%classpath%;"+xtargetDir);
				//  batchOut.println("set path=C:\\jdk1.3\\bin");

				or.generateRuntime(task, batchOut);

				batchOut.println("set classpath=%oldcp%");
				//  batchOut.println("set path=%oldpath%");
				batchOut.println("pause");
				batchOut.close();
			}
		}
	}


	public void generateRuntime(ITaskWrapper rootTask, PrintWriter batchOut) {
		try {
			INetworkDesignWrapper context = (INetworkDesignWrapper)rootTask.getXml().getRoot();

			createDSpecs(rootTask, batchOut);

			createTSpecs(context, batchOut);

			batchOut.println();

			createConfig(rootTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createConfig(ITaskWrapper task) {
		File file = new File(xtargetDir + "\\workflows\\" + task.getNameAttribute() + "\\config");

		CodeGenerator ds = null;
		try {
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			ITaskWrapper stask = (ITaskWrapper)task.getXml().getIdRef(task.getRealization().getNetworkTaskRealization().getFirsttaskAttribute());

			ds.println("StartTask " + stask.getNameAttribute());
			//ds.println("MonitorHost pumptest2 5000");
			String host = task.getHostAttribute();
			if (host == null || host.trim().length() == 0)
				host = InetAddress.getLocalHost().getHostName();
			ds.println("MonitorHost " + host + " 5000");
			Vector children = parents.getChildren(task);
			ds.println("TaskAssignments " + children.size());
			int count = children.size();
			for (int i=0; i<count; i++) {
				String child = (String)children.elementAt(i);
				String childName = ((ITaskWrapper)task.getXml().getIdRef(child)).getNameAttribute();
				ds.println(childName + " " + host + ":5000");
			}

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashSet createDSpecs(ITaskWrapper task, PrintWriter batchOut) {
		HashSet allData = getAllDataForThisLevel(task, DATA);
		Iterator it = allData.iterator();

		batchOut.print("javac ");
		HashSet rmicSet = new HashSet();

		while (it.hasNext()) {
			//String[] data = (String[])it.next();
			IDataClass data = (IDataClass)it.next();
			createDSpecsForData(task, data, batchOut);
			String pack;
			if (data.getPackage()==null || data.getPackage().trim().length()==0) {
				rmicSet.add(data.getName());
			} else {
				rmicSet.add(data.getPackage().replace('\\','.')+"."+data.getName());
			}
		}

		allData = getAllDataForThisLevel(task, EXCEPTION);
		it = allData.iterator();
		while (it.hasNext()) {
			//String[] data = (String[])it.next();
			IDataClass data = (IDataClass)it.next();
			createExceptionSpecs(task, data, batchOut);
		}

		batchOut.println();

		if (!(rmicSet.isEmpty())) {
			Iterator rmicIter = rmicSet.iterator();
			batchOut.print("rmic -v1.2 -d " + xtargetDir + " ");
			while (rmicIter.hasNext()) {
				batchOut.print("data."+rmicIter.next()+" ");
			}
			batchOut.print("\n");
		}
		return rmicSet;
	}

	private void createExceptionSpecs(ITaskWrapper task, IDataClass data, PrintWriter batchOut) {
		int count = data.getFieldList().getFieldCount();

		for (int i=0;i<count;i++) {
			IField field = data.getFieldList().getFieldAt(i);
			if (field.getType().getDimensionCount() > 0)
				throw new RuntimeException("Cannot handle Array type in " + data.getName());
			if (field.getType().getSimpleTypeOrUrl().getUrl() != null)
				throw new RuntimeException("Cannot handle Complex type in " + data.getName());
		}
		createException(task,data, batchOut);
	}

	private void createException(ITaskWrapper task, IDataClass data, PrintWriter batchOut) {
		String pack = data.getPackage();
		String localPath;
		String dataPath;

		LOG.println("pack is >" + pack + "<");
		if (pack!=null && pack.trim().length()>0) {
			localPath = "data\\"+pack;
		} else {
			localPath = "data";
		}

		dataPath = createPath(xtargetDir,localPath);

		File file = new File(dataPath + "\\" + data.getName() + ".java");

		try {
			batchOut.print(file.getCanonicalPath()+" ");
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (file.exists()) {
			System.out.println("WARNING: File "+file.getAbsolutePath()+" already exists.  Will not be overwritten.");
			return;
		}
		CodeGenerator ds = null;
		try {
			//batchOut.println(file.getCanonicalPath()+" ");
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			ds.println("package " + localPath.replace('\\','.') + ";");
			ds.println();
			ds.println("import java.rmi.*;");
			ds.println("import wfruntime.*;");
			ds.println();
			ds.println();
			if ((data.getParent().getUrl().getHrefAttribute()!=null) && (data.getParent().getUrl().getHrefAttribute().trim().length()>0)) {
				ds.begin("public class " + data.getName() + " extends " + data.getParent().getUrl().getHrefAttribute());
			} else {
				ds.begin("public class " + data.getName());
			}
			{
				int count = data.getFieldList().getFieldCount();
				for (int i=0; i<count; i++) {
					org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
					ds.println("private "+convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " " + field.getName() + ";");
				}

				//ds.println("	String wordOne = null;");
				ds.println();
				ds.begin("public " + data.getName() + "()");
				{
				}ds.end();
				ds.println();

				for (int i=0; i<count; i++) {
					org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
					ds.begin("public " + convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " get" + field.getName() + "() throws RemoteException");
					{
						ds.println("return " + field.getName() + ";");
					}ds.end();
					ds.println();
					ds.begin("public void set" + field.getName() + "(" + convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " inp) throws RemoteException");
					{
						ds.println(field.getName() + " = inp;");
					}ds.end();
					ds.println();
				}
			}ds.end();

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void createDSpecsForData(ITaskWrapper task, IDataClass data, PrintWriter batchOut) {
		int count = data.getFieldList().getFieldCount();

		for (int i=0;i<count;i++) {
			IField field = data.getFieldList().getFieldAt(i);
			if (field.getType().getDimensionCount() > 0)
				throw new RuntimeException("Cannot handle Array type in " + data.getName());
			if (field.getType().getSimpleTypeOrUrl().getUrl() != null)
				throw new RuntimeException("Cannot handle Complex type in " + data.getName());
		}

		/*
		String basePath = xtargetDir + "\\data";
		File basePathFile = new File(basePath);
		if(!(basePathFile.exists())) {
		basePathFile.mkdir();
		}
		*/
		createX(task, data, batchOut);
		createIX(task, data, batchOut);
	}

	private String createPath(String currPath, String domain) {
		String restDomain = domain;
		int firstPeriod;
		String topDir;
		File currPathFile;
		while (restDomain!=null) {
			firstPeriod = restDomain.indexOf("\\");
			if (firstPeriod>=0) {
				topDir = restDomain.substring(0,firstPeriod);
				restDomain = restDomain.substring(firstPeriod+1);
			} else {
				topDir = restDomain;
				restDomain = null;
			}
			currPath = currPath+"\\"+topDir;
			currPathFile = new File(currPath);
			if (!(currPathFile.exists())) {
				currPathFile.mkdir();
			}
		}
		return currPath;
	}

	private void createX(ITaskWrapper task, IDataClass data, PrintWriter batchOut) {
		String pack = data.getPackage();
		String localPath;
		String dataPath;

		if (pack!=null && pack.trim().length()>0) {
			localPath = "data\\"+pack;
		} else {
			localPath = "data";
		}

		dataPath = createPath(xtargetDir,localPath);

		File file = new File(dataPath + "\\" + data.getName() + ".java");

		try {
			batchOut.print(file.getCanonicalPath()+" ");
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (file.exists()) {
			System.out.println("WARNING: File "+file.getAbsolutePath()+" already exists.  Will not be overwritten.");
			return;
		}
		CodeGenerator ds = null;
		try {
			//batchOut.print(file.getCanonicalPath()+" ");
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			ds.println("package " + localPath.replace('\\','.') + ";");
			ds.println();
			ds.println("import java.rmi.*;");
			ds.println("import wfruntime.*;");
			ds.println();
			ds.println();
			if (data.getParent() != null && (data.getParent().getUrl().getHrefAttribute()!=null) && (data.getParent().getUrl().getHrefAttribute().trim().length()>0)) {
				ds.begin("public class " + data.getName() + " extends " + data.getParent().getUrl().getHrefAttribute() + " implements I" + data.getName());
			} else {
				ds.begin("public class " + data.getName() + " implements I" + data.getName());
			}
			{
				//ds.begin("public class " + data.getName() + " extends " + data.getParent().getUrl()+ " implements I" + data.getName());{
				int count = data.getFieldList().getFieldCount();
				for (int i=0; i<count; i++) {
					org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
					ds.println("private "+convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " " + field.getName() + ";");
				}

				//ds.println("	String wordOne = null;");
				ds.println();
				ds.begin("public " + data.getName() + "() throws RemoteException");
				{
				}ds.end();
				ds.println();

				for (int i=0; i<count; i++) {
					org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
					ds.begin("public " + convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " get" + field.getName() + "() throws RemoteException");
					{
						ds.println("return " + field.getName() + ";");
					}ds.end();
					ds.println();
					ds.begin("public void set" + field.getName() + "(" + convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " inp) throws RemoteException");
					{
						ds.println(field.getName() + " = inp;");
					}ds.end();
					ds.println();
				}
			}ds.end();

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createIX(ITaskWrapper task, IDataClass data, PrintWriter batchOut) {
		String pack = data.getPackage();
		String localPath;
		String dataPath;

		if (pack!=null && pack.trim().length()>0) {
			localPath = "data\\"+pack;
		} else {
			localPath = "data";
		}

		dataPath = createPath(xtargetDir,localPath);
		File file = new File(dataPath + "\\I" + data.getName() + ".java");

		try {
			batchOut.print(file.getCanonicalPath()+" ");
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (file.exists()) {
			System.out.println("WARNING: File "+file.getAbsolutePath()+" already exists.  Will not be overwritten.");
			return;
		}
		CodeGenerator ds = null;
		try {
			//batchOut.print(file.getCanonicalPath()+" ");
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);
			ds.println("package " + localPath.replace('\\','.') + ";");
			ds.println();
			ds.println("import java.rmi.*;");
			ds.println("import wfruntime.*;");
			ds.println();
			ds.println();
			ds.begin("public interface I" + data.getName() + " extends Remote ");
			{
				int count = data.getFieldList().getFieldCount();
				for (int i=0; i<count; i++) {
					org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
					ds.println("public " + convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " get" + field.getName() + "() throws RemoteException;");
					ds.println("public void set" + field.getName() + "(" + convert(field.getType().getSimpleTypeOrUrl().getSimpleType()) + " inp) throws RemoteException;");
				}
			}ds.end();

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private  void createTSpecs(INetworkDesignWrapper context, PrintWriter batchOut) {
		int count = context.getTaskCount();

		batchOut.print("javac ");
		try {
			for (int i=0;i<count;i++) {
				ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
				createTSpecsForTask(task,batchOut);
				if (task.getTaskType().equals(TaskSupportClass.NONTRANSACTIONALNETWORK)) {
					createConfig(task);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void createTSpecsForTask(ITaskWrapper task, PrintWriter batchOut) throws Exception
	{
		if (task.getNameAttribute().equals(rootTaskName))
			return;
		String taskDir = xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute();
		File file = new File(taskDir);
		if (file.exists())
			return;
		else if (!file.mkdir()) {
			WARNING.println("Could not create directory " + file.getPath());
			return;
		}
		createCreates(task);
		createExceptionsFile(task);
		String dperms = createDataperm(task);
		createRouting(task);
		String dtypes = createDataTypes(task,batchOut);

		createFieldTypes(task);

		org.ofbiz.designer.util.LOG.println("XXXXXXXXXXXXXXXXXXXXXXXXX");

		createRealization(task, batchOut, dtypes, dperms);

/*
		if (org.ofbiz.designer.task.getTaskType().equals("NonTransactionalNetwork"))
		{
			createMapping(org.ofbiz.designer.task);
		}
		*/

		createRealizationFile(task);
	}

	private void createMapping(ITaskWrapper task, PrintWriter out) {
		IRealization real = task.getRealization();
		INetworkTaskRealization netreal = real.getNetworkTaskRealization();
		IInputMappingList inMap = netreal.getInputMappingList();
		IOutputMappingList outMap = netreal.getOutputMappingList();

		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "mapping");
		//CodeGenerator ds = null;
		try {
			//ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			int count = inMap.getMappingCount();
			int num = 0;
			Stack strings = new Stack();
			for (int i=0; i<count; i++) {
				IMapping map = inMap.getMappingAt(i);
				String firstId = map.getFirstElementAttribute();
				String secId = map.getSecondElementAttribute();
				IParameter firstParam = (IParameter)task.getXml().getIdRef(firstId);
				IParameter secParam = (IParameter)task.getXml().getIdRef(secId);
				String firstName = firstParam.getVariablenameAttribute();
				String secName = secParam.getVariablenameAttribute();
				strings.push(firstName + " " + secName);
				num++;
			}

			out.println("InputMappings " + num);
			while (!strings.isEmpty())
				out.println((String)strings.pop());

			count = outMap.getMappingCount();
			num = 0;
			strings = new Stack();
			for (int i=0; i<count; i++) {
				IMapping map = outMap.getMappingAt(i);
				String firstId = map.getFirstElementAttribute();
				String secId = map.getSecondElementAttribute();
				IOutput firstParam = (IOutput)task.getXml().getIdRef(firstId);
				IOutput secParam = (IOutput)task.getXml().getIdRef(secId);
				String firstName = firstParam.getVariablenameAttribute();
				String secName = secParam.getVariablenameAttribute();
				strings.push(firstName + " " + secName);
				num++;
			}

			out.println("OutputMappings " + num);
			while (!strings.isEmpty())
				out.println((String)strings.pop());

			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createCreates(ITaskWrapper task) {
		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "creates");
		CodeGenerator ds = null;
		String returnObj = "";
		try {
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			HashSet createList = getCreateList(task);
			ds.println("CreatesTypes " + createList.size());
			Iterator it = createList.iterator();
			while (it.hasNext()) {
				String[] pair = (String[])it.next();
				ds.println(pair[1] + " " + pair[0]);
				returnObj = pair[0] + " " + pair[1] + "\n";
			}

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createExceptionsFile(ITaskWrapper task) {
		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "exceptions");
		CodeGenerator ds = null;
		String returnObj = "";
		try {
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			int num = task.getTaskExceptionCount();
			int actualNum = 0;
			for (int i=0;i<num;i++) {
				ITaskException taskException = task.getTaskExceptionAt(i);
				ILocalHandler handler = taskException.getLocalHandler();
				if (handler != null)
					actualNum++;
			}
			ds.println("Exceptions " + actualNum);
			for (int i=0;i<num;i++) {
				ITaskException taskException = task.getTaskExceptionAt(i);
				ILocalHandler handler = taskException.getLocalHandler();
				if (handler == null)
					continue;

				String exName = taskException.getDatatypeurlAttribute().replace('\\','.');

/*
				IDataClass exClass = (IDataClass)org.ofbiz.designer.task.getXml().getHref(taskException.getDatatypeurlAttribute());
				if(exClass.getPackage() != null && (exClass.getPackage().trim().length()>0)) {
				exName = "data." + exClass.getPackage().replace('\\','.') + "." + exClass.getName();
				} else {
				exName = "data." + exClass.getName();
				}*/

				String retryTimes = handler.getRetrytimesAttribute();
				String retryDelay = handler.getRetrydelayAttribute();
				String rethrow = handler.getRethrowexceptionAttribute();
				String email = handler.getEmailAttribute();

				ds.println("Exception "+exName);
				ds.println("Retries " + retryTimes);
				ds.println("Delay " + retryDelay);
				ds.println("Rethrow " + rethrow);
				ds.println("Email " + email);
			}

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String createDataperm(ITaskWrapper task) {
		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "dataperm");
		CodeGenerator ds = null;
		String dperms = null;
		String returnObj = "";
		try {
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			int count = task.getDataSecurityMaskCount();
			int num = 0;
			Stack strings = new Stack();
			org.ofbiz.designer.util.LOG.println("@@@@@"+task.getNameAttribute()+" count: "+count);
			String paramName = null;

			for (int i=0; i<count; i++) {
				IDataSecurityMask mask = task.getDataSecurityMaskAt(i);
				IParameter param = (IParameter) task.getXml().getIdRef(mask.getDatanameAttribute());
				paramName = param.getVariablenameAttribute();
				int fieldCount = mask.getFieldMaskCount();
				for (int j=0; j<fieldCount; j++) {
					IFieldMask fieldMask = mask.getFieldMaskAt(j);
					strings.push(paramName + " " + fieldMask.getFieldnameAttribute() + " " + fieldMask.getAccesstypeAttribute());
					num++;
				}
			}

			HashSet clist = getCreateList(task);
			Iterator citer = clist.iterator();

			while (citer.hasNext()) {
				String[] currCreate = (String[]) citer.next();
				//String paramName = currCreate[0];
				org.ofbiz.designer.util.LOG.println("datatypeurl: "+currCreate[0]);
				org.ofbiz.designer.util.LOG.println("paramName: "+currCreate[1]);

				XmlWrapper dataxml = XmlWrapper.openDocument(new File(xmlDir+"\\data\\"+currCreate[0]+".xml"));


				IDataClass data = (IDataClass) dataxml.getRoot();
				for (int i=0;i<data.getFieldList().getFieldCount();i++) {
					org.ofbiz.designer.dataclass.IField fld = data.getFieldList().getFieldAt(i);
					strings.push(currCreate[1]+" "+fld.getName()+" FullControl");
					num++;
				}
			}

			ds.println("DataRights " + num);
			while (!strings.isEmpty()) {
				dperms = (String)strings.pop();
				ds.println(dperms);
				returnObj += dperms + "\n";
			}
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}

	private String createDataTypes(ITaskWrapper task, PrintWriter batchOut) {
		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "datatypes");
		CodeGenerator ds = null;
		Hashtable dataSet = getDataHashtable(task);
		Object[] dataNames = dataSet.keySet().toArray();
		String dtypes = null;
		String returnObj = "";
		try {
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);
			int count = dataSet.size();
			int num = 0;
			Stack strings = new Stack();
			IDataClass theClass;
			String thePackage;
			for (int i=0;i<dataNames.length;i++) {
				theClass = (IDataClass) dataSet.get(dataNames[i]);
				thePackage = theClass.getPackage();
				if (thePackage == null || thePackage.trim().length() == 0) {
					thePackage = "";
				} else {
					thePackage = thePackage+"\\";
				}
				strings.push((String)dataNames[i]+" "+thePackage+theClass.getName());
				//batchOut.println("rmic data."+thePackage+theClass.getName());
				num++;
			}
			ds.println("DataTypes " + num);
			while (!strings.isEmpty()) {
				dtypes = (String)strings.pop();
				ds.println(dtypes);
				returnObj += dtypes + "\n";
			}
			ds.close();




		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}

	private String createFieldTypes(ITaskWrapper task) {
		org.ofbiz.designer.util.LOG.println("CREATE FIELD TYPES WAS CALLED");
		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "fieldtypes");
		CodeGenerator ds = null;
		Hashtable dataSet = getDataHashtable(task);
		Object[] dataNames = dataSet.keySet().toArray();
		String ftypes = null;
		String returnObj = "";
		try {
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);
			int count = dataSet.size();
			int num = 0;
			Stack strings = new Stack();
			IDataClass theClass;
			String thePackage;
			for (int i=0;i<dataNames.length;i++) {
				theClass = (IDataClass) dataSet.get(dataNames[i]);


				org.ofbiz.designer.dataclass.IFieldList fl = theClass.getFieldList();

				int fcount = fl.getFieldCount();

				for (int j=0;j<fcount;j++) {
					IField theField = fl.getFieldAt(j);
					strings.push((String)dataNames[i]+" "+theField.getName()+" "+theField.getType());
					num++;
				}

			}
			ds.println("FieldTypes " + num);
			while (!strings.isEmpty()) {
				ftypes = (String)strings.pop();
				ds.println(ftypes);
				returnObj += ftypes + "\n";
			}
			ds.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}


	private void createRouting(ITaskWrapper task) {
		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "routing");
		CodeGenerator ds = null;
		try {
			ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

			// write inputs
			INetworkTaskRealization nr = task.getRealization().getNetworkTaskRealization();
			if (nr != null)	ds.println(task.getTaskType() + " " + task.getNameAttribute());
			else if (task.getTaskType().equals("SynchronizationTaskIn") || task.getTaskType().equals("SynchronizationTaskOut")) {
				ds.println(task.getTaskType() + " " + task.getRealization().getSyncRealization().getPartnerAttribute() );
			} else ds.println(task.getTaskType());

			ITask ptask = parents.getParentTask(task.getIdAttribute());
			String ftaskid = null;
			if (ptask!=null) {
				ftaskid = ptask.getRealization().getNetworkTaskRealization().getFirsttaskAttribute();
			}
			if ((ftaskid != null) && (ftaskid.equals(task.getIdAttribute()))) {
				ds.println("InputGroups 1");
				ds.println("Inputs 1");
				ds.println("START Success");

				if (task.getInvocationCount()>0) {
					IInvocation startInv = task.getInvocationAt(0);
					int count = startInv.getParameterCount();
					ds.println("InputParams " + count);
					String paramName;
					for (int i=0;i<count;i++) {
						paramName = startInv.getParameterAt(i).getVariablenameAttribute();
						ds.println(paramName+" "+paramName);
					}
				} else {
					ds.println("InputParams 0");
				}
			} else {
				Vector inputList = getInputDNF(task);
				int size = inputList.size();
				ds.println("InputGroups " + size);
				for (int i=0; i<size; i++) {
					Vector andBlock = (Vector)inputList.elementAt(i);
					ds.println("Inputs " + andBlock.size());
					int blockSize = andBlock.size();
					for (int j=0; j<blockSize; j++) {
						ITaskWrapper sourceTask = null;
						if (andBlock.elementAt(j) instanceof IArcWrapper)
							sourceTask = (ITaskWrapper)task.getXml().getIdRef(((IArcWrapper)andBlock.elementAt(j)).getSourceAttribute());
						else
							sourceTask = (ITaskWrapper)andBlock.elementAt(j);
						IArcWrapper arc = getConnectingArc(sourceTask, task);
						int count = arc.getMappingCount();
						String arcType = arc.getArctypeAttribute();
						if (arcType.equals("Alternative")) {
							arcType = ((IArc)task.getXml().getIdRef(arc.getSourceAttribute())).getArctypeAttribute();
						}
						ds.println(sourceTask.getNameAttribute() + " " + arcType);
						ds.println("InputParams " + count);
						for (int k=0; k<count; k++) {
							IMapping mapping = arc.getMappingAt(k);
							IOutput output = ((IOutput)arc.getXml().getIdRef(mapping.getFirstElementAttribute()));
							IParameter parameter = ((IParameter)arc.getXml().getIdRef(mapping.getSecondElementAttribute()));
							ds.println(output.getVariablenameAttribute() + " " + parameter.getVariablenameAttribute());
						}
					}
				}
			}

			// write outputs
			Vector andBlock = getOutputDNF(task);
			int blockSize = andBlock.size();


			org.ofbiz.designer.util.LOG.println(""+parents.getParentTask(task.getIdAttribute()).getRealization().getNetworkTaskRealization().getLasttaskAttribute());
			String etaskid = null;
			if (ptask!=null) {
				etaskid = ptask.getRealization().getNetworkTaskRealization().getLasttaskAttribute();
			}

			if ((etaskid!=null) && (etaskid.equals(task.getIdAttribute()))) {
				ds.println("SOutputs 1");
				ds.println("true");
				ds.println("END");
				//ds.print("\n");
				IOutput theOut;
				for (int i=0;i<task.getOutputCount();i++) {
					theOut = task.getOutputAt(i);
					ds.print(theOut.getVariablenameAttribute() + " ");
				}
				ds.print("\n");
			} else {
				ds.println("SOutputs " + andBlock.size());
			}

			for (int j=0; j<blockSize; j++) {
				Object[] triple = (Object[])andBlock.elementAt(j);
				ITaskWrapper newTask = (ITaskWrapper)triple[0];
				ITaskWrapper altTask = (ITaskWrapper)triple[3];
				String condition = (String)triple[1];
				String objects = (String)triple[2];
				ds.println(condition);
				LOG.println("*** org.ofbiz.designer.task is " + task.getNameAttribute() + " alt is " + altTask);
				if (altTask == null)
					ds.println(newTask.getNameAttribute());
				else ds.println(newTask.getNameAttribute() + " " + altTask.getNameAttribute());
				ds.println(objects);
			}

			// write exceptions
			String[] eArcs = IDRefHelper.getReferenceArray(task.getOutarcsAttribute());
			int eCount = 0;
			for (int i=0; i<eArcs.length; i++) {
				IArcWrapper arc = (IArcWrapper)task.getXml().getIdRef(eArcs[i]);
				if (arc.getArctypeAttribute().equals("Fail")) eCount++;
			}
			ds.println("FOutputs " + eCount);
			for (int i=0; i<eArcs.length; i++) {
				IArcWrapper arc = (IArcWrapper)task.getXml().getIdRef(eArcs[i]);
				if (arc.getArctypeAttribute().equals("Success")) continue;
				ITask newTask = (ITask)task.getXml().getIdRef(arc.getDestinationAttribute());
				ITask altTask = null;
				if (arc.getAlternativetransitionAttribute() != null)
					altTask = (ITask)task.getXml().getIdRef(arc.getAlternativetransitionAttribute());
				String exception = arc.getExceptionAttribute();
				if (exception == null) throw new RuntimeException("No exception associated with arc in org.ofbiz.designer.task " + task.getNameAttribute());
				ds.print("data."+exception.replace('\\','.')+" ");
				if (altTask == null)
					ds.println(newTask.getNameAttribute());
				else ds.println(newTask.getNameAttribute() + " " + altTask.getNameAttribute());

				int mcount = arc.getMappingCount();
				String objList = "";
				for (int j=0; j<mcount; j++) {
					IMapping mapping = arc.getMappingAt(j);
					String temp = mapping.getFirstElementAttribute();
					IOutput output = (IOutput)task.getXml().getIdRef(temp);
					objList += output.getVariablenameAttribute();
				}

				ds.println(objList);
			}

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String parentID(ITaskWrapper task) {
		if (task.getIdAttribute().equals(rootTaskName))
			return workflowName;
		else
			return parents.getParentTask(task.getIdAttribute()).getIdAttribute();
	}

	private void createRealization(ITaskWrapper task, PrintWriter batchOut, String dtypes, String dperms) {
		org.ofbiz.designer.util.LOG.println("1");
		File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "Realization.java");
		org.ofbiz.designer.util.LOG.println("2");
		CodeGenerator ds = null;
		try {
			org.ofbiz.designer.util.LOG.println("FILE: "+file.getCanonicalPath());
			if (task.getTaskType().equals("SynchronizationTaskIn")||
				task.getTaskType().equals("SynchronizationTaskOut") ||
				task.getTaskType().equals(TaskSupportClass.NONTRANSACTIONALTASKREALIZATION)) {
				org.ofbiz.designer.util.LOG.println("FILE AFTER IF: "+file.getCanonicalPath());
				batchOut.print(file.getCanonicalPath()+" ");

				ds = new CodeGenerator(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))), CodeGenerator.JAVA);

				ds.println("package workflows." + parentID(task) +"." + task.getNameAttribute() + ";");
				ds.println();
				ds.println("import wfruntime.*;");
				ds.println();

				ds.println("/*");
				ds.println("The following methods may be used to access data in this realization:");

				ds.println("public void setIntField(String paramName, String fieldName, int val);");
				ds.println("public void setDoubleField(String paramName, String fieldName, double val);");
				ds.println("public void setFloatField(String paramName, String fieldName, float val);");
				ds.println("public void setBooleanField(String paramName, String fieldName, boolean val);");
				ds.println("public void setLongField(String paramName, String fieldName, long val);");
				ds.println("public void setByteField(String paramName, String fieldName, byte val);");
				ds.println("public void setShortField(String paramName, String fieldName, short val);");
				ds.println("public void setCharField(String paramName, String fieldName, char val);");
				ds.println("public int getIntField(String paramName, String fieldName);");
				ds.println("public double getDoubleField(String paramName, String fieldName);");
				ds.println("public float getFloatField(String paramName, String fieldName);");
				ds.println("public boolean getBooleanField(String paramName, String fieldName);");
				ds.println("public long getLongField(String paramName, String fieldName);");
				ds.println("public byte getByteField(String paramName, String fieldName);");
				ds.println("public short getShortField(String paramName, String fieldName);");
				ds.println("public char getCharField(String paramName, String fieldName);");
				ds.println("public Object getObjField(String paramName, String fieldName);");
				ds.println("public void setObjField(String paramName, String fieldName, Object val);");
				ds.println();

				ds.println("The following data objects are available:");
				ds.println(dtypes);
				ds.println("The following permissions are granted:");
				ds.println(dperms);
				ds.println("*/");
				ds.println();

				ds.begin("public class Realization extends AbstractRealization");
				{
					ds.println();
					ds.begin("	public void run()");
					{
						ds.begin("try");
						{
							ds.println();
							ds.println("taskMgr().endTask(null);");
						}ds.end();
						ds.begin("catch(Exception e)");
						{
							ds.println();
							ds.println("taskMgr().endTask(e);");
						}ds.end();
					}ds.end();
					ds.println();
				}ds.end();
				ds.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*
	static final HashSet getAllDataForThisLevel(ITaskWrapper mainTask) {
	Vector tasks = new Vector();
	HashSet returnSet = new HashSet();
	tasks.addElement(mainTask.getRealization().getNetworkTaskRealization());
	while (tasks.size() > 0) {
	Object obj = tasks.elementAt(0);
	if (obj instanceof ITask) {
	ITaskWrapper org.ofbiz.designer.task = (ITaskWrapper)obj;
	returnSet.addAll(getCreatesTypesSet(org.ofbiz.designer.task));
	} else if (obj instanceof INetworkTaskRealization) {
	INetworkTaskRealization nr = (INetworkTaskRealization)obj;
	int count = nr.getDomainCount();
	for (int i=0;i<count;i++)File file = new File(xtargetDir + "\\workflows\\" + parentID(org.ofbiz.designer.task) + "\\" + org.ofbiz.designer.task.getNameAttribute() + "\\" + "routing");
	tasks.addElement(nr.getDomainAt(i));
	} else if (obj instanceof IDomain) {
	IDomain domain = (IDomain)obj;
	int count = domain.getCompartmentCount();
	for (int i=0;i<count;i++)
	tasks.addElement(domain.getCompartmentAt(i));
	String[] taskRefs = IDRefHelper.getReferenceArray(domain.getTasksAttribute());
	count = taskRefs.length;
	for (int i=0;i<count;i++)
	tasks.addElement(mainTask.getXml().getIdRef(taskRefs[i]));
	} else if (obj instanceof ICompartment) {
	ICompartment compartment = (ICompartment)obj;
	int count = compartment.getCompartmentCount();
	for (int i=0;i<count;i++)
	tasks.addElement(compartment.getCompartmentAt(i));
	String[] taskRefs = IDRefHelper.getReferenceArray(compartment.getTasksAttribute());
	count = taskRefs.length;
	for (int i=0;i<count;i++)
	tasks.addElement(mainTask.getXml().getIdRef(taskRefs[i]));
	}
	tasks.removeElementAt(0);
	}
	return returnSet;
	}
	*/

	static final HashSet getAllDataForThisLevel(ITaskWrapper mainTask, String mode) {
		if (!(DATA.equals(mode)) && !(EXCEPTION.equals(mode)))
			throw new RuntimeException("Invalid mode");
		Vector tasks = new Vector();
		HashSet returnSet = new HashSet();
		tasks.addElement(mainTask.getRealization().getNetworkTaskRealization());
		while (tasks.size() > 0) {
			Object obj = tasks.elementAt(0);
			if (obj instanceof ITask) {
				ITask task = (ITask)obj;
				if (mode.equals(DATA))
					returnSet.addAll(getData(task));
				else
					returnSet.addAll(myGetException(task));
			} else if (obj instanceof INetworkTaskRealization) {
				INetworkTaskRealization nr = (INetworkTaskRealization)obj;
				int count = nr.getDomainCount();
				for (int i=0;i<count;i++)
					tasks.addElement(nr.getDomainAt(i));
			} else if (obj instanceof IDomain) {
				IDomain domain = (IDomain)obj;
				int count = domain.getCompartmentCount();
				for (int i=0;i<count;i++)
					tasks.addElement(domain.getCompartmentAt(i));
				String[] taskRefs = IDRefHelper.getReferenceArray(domain.getTasksAttribute());
				count = taskRefs.length;
				for (int i=0;i<count;i++)
					tasks.addElement(mainTask.getXml().getIdRef(taskRefs[i]));
			} else if (obj instanceof ICompartment) {
				ICompartment compartment = (ICompartment)obj;
				int count = compartment.getCompartmentCount();
				for (int i=0;i<count;i++)
					tasks.addElement(compartment.getCompartmentAt(i));
				String[] taskRefs = IDRefHelper.getReferenceArray(compartment.getTasksAttribute());
				count = taskRefs.length;
				for (int i=0;i<count;i++)
					tasks.addElement(mainTask.getXml().getIdRef(taskRefs[i]));
			}
			tasks.removeElementAt(0);
		}
		return returnSet;
	}

	public void createRealizationFile(ITaskWrapper task) {
		org.ofbiz.designer.util.LOG.println("CREATE REALIZATION WAS CALLED");
		//if (!((org.ofbiz.designer.task.getTaskType().equals(TaskSupportClass.TRANSACTIONALTASKREALIZATION)) ||(org.ofbiz.designer.task.getTaskType().equals(TaskSupportClass.NONTRANSACTIONALNETWORK))))return;
		try {
			File file = new File(xtargetDir + "\\workflows\\" + parentID(task) + "\\" + task.getNameAttribute() + "\\" + "realization");
			PrintWriter out = new PrintWriter(new FileOutputStream(file));
			out.println(task.getTaskType());
			if (task.getTaskType().equals(TaskSupportClass.TRANSACTIONALTASKREALIZATION)) {
				ITransactionalTaskRealization treal = task.getRealization().getSimpleRealization().getTransactionalTaskRealization();
				out.println("URL "+treal.getUrlAttribute());
				out.println("Login "+treal.getUsernameAttribute());
				out.println("Password "+treal.getUserpasswordAttribute());
				out.println("Query "+treal.getQueryAttribute());
				int ocount = treal.getTransactionalOutputCount();
				out.println("Outputs "+ocount);
				for (int i=0;i<ocount;i++) {
					out.println(treal.getTransactionalOutputAt(i));
				}
			} else if (task.getTaskType().equals(TaskSupportClass.NONTRANSACTIONALNETWORK)) {
				INetworkTaskRealization ntreal = task.getRealization().getNetworkTaskRealization();
				out.println("InnerWFName " + task.getNameAttribute());
				out.println("StartTask " + ((ITaskWrapper) task.getXml().getIdRef(ntreal.getFirsttaskAttribute())).getNameAttribute());
				createMapping(task,out);
			} else if (task.getTaskType().equals(TaskSupportClass.NONTRANSACTIONALTASKREALIZATION)) {
				String rwfName = workflowName;
				if (rwfName.startsWith("/"))
					rwfName=rwfName.substring(1);
				out.println("WFName " + rwfName);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static final HashSet myGetException(ITask task) {
		HashSet returnSet = new HashSet();
		int count = task.getTaskExceptionCount();
		for (int i=0;i<count;i++) {
			String exceptionName = task.getTaskExceptionAt(i).getDatatypeurlAttribute();
			IDataClass ex = getDataFromUrl(exceptionName);
			returnSet.add(ex);
		}
		return returnSet;
	}


}

