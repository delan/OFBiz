<%@include file="params.jsp" %><%@include file="copyright.jsp" %>/*
 * (#)<%=classname%>.java 
 * Exception
 */
package <%=getPath()%>;

<%@include file="imports.jsp" %>

/**
 * <%=getDocumentation(element).length()>0 ? getDocumentation(element) : "Exception "+classname%>
 * @author <%=System.getProperty("user.name")%>
 * @version 1.0
 */

public class <%=classname%><%if (superclazz!=null)  {%> extends <%=supername%> <%} else {%> extends Exception <%}

Collection c=MetaModel.getInterfaces(clazz);
if (c!=null && c.size()>0)  {
	
	Iterator it = c.iterator();
	out.print(" implements ");
	while (it.hasNext())  {
		MBase elem = (MBase)it.next();
		out.print(MetaModel.getName(elem));
		if (it.hasNext())  {
			out.print(", ");
		} 
	}
}
%> {

	/**
	 * Constructor with error message
	 * @param pMessage Error message
	 */
	public <%=classname%>(String pMessage) {
		super(pMessage);
	}
	
	/**
	 * Constructor with error message and nested exception
	 * @param pMessage Error message
	 * @param pNestedException Root cause
	 */
	public <%=classname%>(String pMessage, Throwable pNestedException) {<%if (superclazz!=null) {%>
		super(pMessage, pNestedException);<%} else {%>
		super(pMessage);
		nestedException = pNestedException;<%}%>
	}	

	/**
	 * String representation of exception <%=classname%>
	 * @return Error message, if nested exception is present, the message of the nested execption 
	 * is appended
	 */
	public String toString() {<%if (superclazz!=null) {%>
		return super.toString();<%} else {%>
		String ret = "<%=classname%>: " + getMessage();
		if (nestedException != null) {
			ret += "\\nNested exception is " + nestedException.toString();
		}
		return ret;<%}%>
	}<%@include file="attributes.jsp" %><%@include file="operations.jsp" %><%@include file="associations.jsp" %>
}
<%! public String getFilename()  { return super.getFilename();} %>
<%@include file="utilities.jsp" %><%@include file="metamodel.jsp" %>
