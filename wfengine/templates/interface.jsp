<%@include file="params.jsp" %><%@include file="copyright.jsp" %>
/*
 * (#)<%=classname%>.java 
 */
package <%=getPath()%>;

<%@include file="imports.jsp" %>
import java.util.Collection;
import java.io.Serializable;

/**
 * <%=getDocumentation(element).length()>0 ? getDocumentation(element) : "Schnittstelle "+classname%>
 * @author <%=System.getProperty("user.name")%>
 * @version 1.0
 */
 
public interface <%=classname%> <%if (superclazz!=null) {%> extends <%=supername%><%} else {%> extends Serializable <%}%> {
<%@include file="operations_decl.jsp" %><%@include file="associations_decl.jsp" %><%@include file="metamodel.jsp" %><%@include file="utilities.jsp" %>
}