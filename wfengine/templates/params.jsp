<%@page import="java.util.Date"%><%	MBase clazz = (MBase)request.getAttribute("root");	
	String classname = MetaModel.getName(clazz);
	String fqclassname = MetaModel.getFQName(clazz);
	String packagename = MetaModel.getNamespaceName(clazz);
	MBase superclazz = 	MetaModel.getSuperclass(clazz);
	String stereotype = MetaModel.getStereotype(clazz);
	String supername =null;
	if (superclazz==null) {
		supername ="java.lang.Object";
	} else {
		supername = MetaModel.getName(superclazz);
	}
%><%! 
public static String COLLECTION_TYPE="Collection";
public static String COLLECTION_IMPL_TYPE="ArrayList"; %>