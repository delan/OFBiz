<%@include file="params.jsp" %><%@include file="copyright.jsp" %>
package <%=getNamespaceName(element)%>;


<%@include file="imports.jsp" %>
/**
 * <%=getDocumentation(element).length()>0 ? getDocumentation(element) : "Class "+classname + " - no documentation available"%>
 * @author <%=System.getProperty("user.name")%>
 * @version 1.0
 */

public class <%=classname%><%if (superclazz!=null)  {%> extends <%=supername%> <%}

Collection c=getInterfaces(clazz);
if (c!=null && c.size()>0)  {
	
	Iterator it = c.iterator();
	out.print(" implements ");
	while (it.hasNext())  {
		MBase elem = (MBase)it.next();
		
		out.print(getName(elem));
		if (it.hasNext())  {
			out.print(", ");
		} 
	}
} else {%> implements Serializable <%}%> {

	<%@include file="attributes.jsp" %>
		
	/**
	 * Empty constructor
	 */
	<%=classname%>() {
		<%if (superclazz != null)%>super();	
	}
<%if (getAttributes(element).size() > 0) {%>
	/**
	 * Constructor with all attributes <%for(Iterator it = getAttributes(element).iterator(); it.hasNext();) {
	 	MBase m = (MBase) it.next();
		String attrType = getTypeName(m);
		String attrName = getName(m);
		String attrNameFU = getNameFU(m);
	 %>
	 * @param p<%=attrNameFU%> Initial value for attribute '<%=attrName%>'<%}%>
	 */
	<%=classname%>(<%for(Iterator it = getAttributes(element).iterator(); it.hasNext();) {
		MBase m = (MBase) it.next();
		String attrType = getTypeName(m);
		String attrName = getNameFD(m);
		String attrNameFU = getNameFU(m);
		%>
		<%=attrType%> p<%=attrNameFU%><%if (it.hasNext()) {%>,<%}}%>) {		
		<%for(Iterator it = getAttributes(element).iterator(); it.hasNext();) {
		MBase m = (MBase) it.next();
		String attrType = getTypeName(m);
		String attrName = getName(m);
		String attrNameFU = getNameFU(m);
		%>		
		<%=attrName%> = p<%=attrNameFU%>;<%}%>
	}
<%}%>
<%@include file="attributes_gs.jsp" %><%@include file="operations.jsp" %><%@include file="associations.jsp" %>
	/**
	 * String representation of <%=classname%>
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("<%=classname%>");	
		return lRet.toString();
	}
}
<%!	public void addToImports()  {
		super.addToImports();
		addToImport("java.util.Collection");
		addToImport("java.util.Iterator");
		addToImport("java.util.ArrayList");
		addToImport("java.io.Serializable");
		addToImport("org.ofbiz.service.workflow.WFException");
	}

%><%@include file="utilities.jsp" %><%@include file="metamodel.jsp" %>


