<%@ page extends "JspMain" %>

<%!
private static final String[] ENUM_LIST = new String[]{"enum.jsp"};
private static final String[] EXCEPTION_LIST = new String[]{"exception.jsp"};
private static final String[] CLASS_LIST = new String[]{"class.jsp", "drop_and_create.jsp"};
private static final String[] INTERFACE_LIST = new String[]{"interface.jsp"};
private static final String[] LEIGHTWEIGHT_LIST = new String[]{"lightweight.jsp"};
private static final String[] EMPTY_LIST = new String[]{};

HashMap components =null;

public String[] getTemplates(MBase element)  {
	String s = getStereotype(element).trim();
	String name = getName(element);
	//System.out.println("<<"+s+">> "+name);
	if ("Exception".equalsIgnoreCase(s) || isException(element)) {		
		return EXCEPTION_LIST;
	} else if ("enumeration".equalsIgnoreCase(s)) {			
		return ENUM_LIST;
	} else if ("lightweight".equalsIgnoreCase(s)) {			
		return LEIGHTWEIGHT_LIST;
	} else if ("interface".equalsIgnoreCase(s) || isInterface(element)) {			
		return INTERFACE_LIST;
	} else {		
		return CLASS_LIST;
	}	
}

public String getName() {
	return "ofbiz workflow engine";
}

public String[] getPostprocessTemplates()  {
	return EMPTY_LIST;
}

public boolean canBeGenerated(MBase pElem)  {
	return super.canBeGenerated(pElem) || isException(pElem);
}
%><%@include file="metamodel.jsp" %>